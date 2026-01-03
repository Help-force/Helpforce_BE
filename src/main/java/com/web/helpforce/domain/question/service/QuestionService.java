package com.web.helpforce.domain.question.service;

import com.web.helpforce.domain.attachment.entity.Attachment;
import com.web.helpforce.domain.attachment.repository.AttachmentRepository;
import com.web.helpforce.domain.attachment.service.FileStorageService;
import com.web.helpforce.domain.answer.entity.Answer;
import com.web.helpforce.domain.answer.repository.AnswerLikeRepository;
import com.web.helpforce.domain.answer.repository.AnswerRepository;
import com.web.helpforce.domain.question.dto.*;
import com.web.helpforce.domain.question.entity.Question;
import com.web.helpforce.domain.question.entity.QuestionTag;
import com.web.helpforce.domain.question.repository.QuestionBookmarkRepository;
import com.web.helpforce.domain.question.repository.QuestionRepository;
import com.web.helpforce.domain.question.repository.QuestionTagRepository;
import com.web.helpforce.domain.tag.entity.Tag;
import com.web.helpforce.domain.tag.repository.TagRepository;
import com.web.helpforce.domain.user.entity.User;
import com.web.helpforce.domain.user.repository.UserRepository;
import com.web.helpforce.global.exception.ConflictException;
import com.web.helpforce.global.exception.ForbiddenException;
import com.web.helpforce.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final AnswerLikeRepository answerLikeRepository;
    private final QuestionBookmarkRepository questionBookmarkRepository;
    private final QuestionTagRepository questionTagRepository;
    private final TagRepository tagRepository;
    private final AttachmentRepository attachmentRepository;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;

    public QuestionListPageResponse getQuestions(
            List<Long> tagIds,
            String searchType,
            String keyword,
            String sort,
            int page,
            int size,
            Long currentUserId
    ) {
        // 0) page 방어: 명세서는 1부터, PageRequest는 0부터
        int pageIndex = Math.max(page - 1, 0);
        int pageSize = Math.max(size, 1);

        Sort sortOption = getSortOption(sort);
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sortOption);

        String normalizedKeyword = (keyword == null) ? null : keyword.trim();
        boolean hasKeyword = normalizedKeyword != null && !normalizedKeyword.isEmpty();

        String normalizedSearchType = (searchType == null || searchType.trim().isEmpty())
                ? "all"
                : searchType.trim().toLowerCase();

        // 허용값 방어
        if (!normalizedSearchType.equals("all")
                && !normalizedSearchType.equals("title")
                && !normalizedSearchType.equals("body")) {
            normalizedSearchType = "all";
        }

        Page<Question> questionPage;

        if (hasKeyword) {
            if (tagIds != null && !tagIds.isEmpty()) {
                questionPage = questionRepository.searchWithTagsAndKeyword(tagIds, normalizedSearchType, normalizedKeyword, pageable);
            } else {
                questionPage = questionRepository.searchByKeyword(normalizedSearchType, normalizedKeyword, pageable);
            }
        } else {
            if (tagIds != null && !tagIds.isEmpty()) {
                questionPage = questionRepository.findByTagIdsAndIsDeletedFalse(tagIds, pageable);
            } else {
                questionPage = questionRepository.findByIsDeletedFalse(pageable);
            }
        }

        List<QuestionListResponse> questions = questionPage.getContent().stream()
                .map(q -> toQuestionListResponse(q, currentUserId))
                .collect(Collectors.toList());

        QuestionListPageResponse.Pagination pagination = QuestionListPageResponse.Pagination.builder()
                .currentPage(page) // 명세서 기준 1-based로 그대로 내려줌
                .totalPages(questionPage.getTotalPages())
                .totalItems(questionPage.getTotalElements())
                .itemsPerPage(pageSize)
                .hasPrevious(questionPage.hasPrevious())
                .hasNext(questionPage.hasNext())
                .build();

        QuestionListPageResponse.Filters filters = QuestionListPageResponse.Filters.builder()
                .tagIds(tagIds)
                .sort((sort == null || sort.isBlank()) ? "latest" : sort)
                .searchType(hasKeyword ? normalizedSearchType : null)
                .keyword(hasKeyword ? normalizedKeyword : null)
                .build();

        return QuestionListPageResponse.builder()
                .questions(questions)
                .pagination(pagination)
                .filters(filters)
                .build();
    }

    private QuestionListResponse toQuestionListResponse(Question question, Long currentUserId) {
        long answerCount = answerRepository.countByQuestion_IdAndIsDeletedFalse(question.getId());

        boolean isBookmarked = false;
        if (currentUserId != null) {
            isBookmarked = questionBookmarkRepository.existsByQuestion_IdAndUser_Id(question.getId(), currentUserId);
        }

        List<Long> tagIds = question.getQuestionTags().stream()
                .map(qt -> qt.getTag().getId())
                .collect(Collectors.toList());

        return QuestionListResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .body(question.getBody())
                .status(question.getStatus())
                .views(question.getViews())
                .isBookmarked(isBookmarked)
                .acceptedAnswerId(question.getAcceptedAnswerId())
                .user(QuestionListResponse.UserSummary.builder()
                        .id(question.getUser().getId())
                        .nickname(question.getUser().getNickname())
                        .build())
                .tagIds(tagIds)
                .answerCount(answerCount)
                .build();
    }

    private Sort getSortOption(String sort) {
        if (sort == null || sort.equalsIgnoreCase("latest")) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        if (sort.equalsIgnoreCase("views")) {
            return Sort.by(Sort.Direction.DESC, "views");
        }
        return Sort.by(Sort.Direction.DESC, "createdAt");
    }

    @Transactional
    public QuestionCreateResponse createQuestion(QuestionCreateRequest request, Long userId) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("제목은 필수 입력 항목입니다.");
        }
        if (request.getBody() == null || request.getBody().length() < 10) {
            throw new IllegalArgumentException("질문 내용은 10자 이상 작성해주세요.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Question question = Question.builder()
                .user(user)
                .title(request.getTitle())
                .body(request.getBody())
                .status("open")
                .views(0)
                .isDeleted(false)
                .questionTags(new ArrayList<>())
                .attachments(new ArrayList<>())
                .build();

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<Tag> tags = tagRepository.findByIdIn(request.getTagIds());
            for (Tag tag : tags) {
                question.getQuestionTags().add(QuestionTag.builder()
                        .question(question)
                        .tag(tag)
                        .build());
            }
        }

        // 파일 첨부
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            for (MultipartFile file : request.getFiles()) {
                if (file == null || file.isEmpty()) continue;

                String fileUrl = fileStorageService.storeFile(file);

                Attachment attachment = Attachment.builder()
                        .question(question)
                        .fileUrl(fileUrl)
                        .mimeType(file.getContentType())
                        .build();

                question.getAttachments().add(attachment);
            }
        }

        Question savedQuestion = questionRepository.save(question);

        List<QuestionCreateResponse.AttachmentDto> attachmentDtos = savedQuestion.getAttachments().stream()
                .map(a -> QuestionCreateResponse.AttachmentDto.of(a.getId(), a.getFileUrl()))
                .collect(Collectors.toList());

        return QuestionCreateResponse.of(savedQuestion.getId(), savedQuestion.getCreatedAt(), attachmentDtos);
    }

    @Transactional
    public QuestionUpdateResponse updateQuestion(
            Long questionId,
            String title,
            String body,
            List<Long> tagIds,
            List<MultipartFile> files,
            Long userId
    ) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("질문을 찾을 수 없습니다."));

        if (question.getIsDeleted()) {
            throw new NotFoundException("질문을 찾을 수 없습니다.");
        }
        if (!question.getUser().getId().equals(userId)) {
            throw new ForbiddenException("자신의 질문만 수정할 수 있습니다.");
        }

        // 1) 기본 필드 수정
        question.setTitle(title);
        question.setBody(body);

        // 2) 태그 갱신 (Replace)
        if (tagIds != null) {
            questionTagRepository.deleteByQuestion_Id(questionId);

            if (!tagIds.isEmpty()) {
                List<Tag> tags = tagRepository.findByIdIn(tagIds);
                for (Tag tag : tags) {
                    questionTagRepository.save(QuestionTag.builder()
                            .question(question)
                            .tag(tag)
                            .build());
                }
            }
        }

        // 3) 파일 갱신 (Replace)
        boolean hasNewFiles = files != null && files.stream().anyMatch(f -> f != null && !f.isEmpty());

        if (hasNewFiles) {
            // 기존 첨부파일 DB row 삭제
            attachmentRepository.deleteByQuestion_Id(questionId);
            questionRepository.flush(); // 선택(안정용)

            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) continue;

                String fileUrl = fileStorageService.storeFile(file);

                Attachment attachment = Attachment.builder()
                        .question(question)
                        .fileUrl(fileUrl)
                        .mimeType(file.getContentType())
                        .build();

                attachmentRepository.save(attachment);
            }
        }

        // 4) 최신 상태 재조회해서 응답 구성
        Question updated = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("질문을 찾을 수 없습니다."));

        List<Long> updatedTagIds = updated.getQuestionTags().stream()
                .map(qt -> qt.getTag().getId())
                .toList();

        List<QuestionUpdateResponse.FileDto> fileDtos = attachmentRepository.findByQuestion_Id(questionId).stream()
                .map(a -> QuestionUpdateResponse.FileDto.builder()
                        .id(a.getId())
                        .fileUrl(a.getFileUrl())
                        .mimeType(a.getMimeType())
                        .build())
                .toList();

        return QuestionUpdateResponse.of(updated.getTitle(), updated.getBody(), updatedTagIds, fileDtos);
    }

    @Transactional
    public QuestionDeleteResponse deleteQuestion(Long questionId, Long userId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("질문을 찾을 수 없습니다."));

        if (question.getIsDeleted()) {
            throw new NotFoundException("질문을 찾을 수 없습니다.");
        }
        if (!question.getUser().getId().equals(userId)) {
            throw new ForbiddenException("자신의 질문만 삭제할 수 있습니다.");
        }
        if (question.getAcceptedAnswerId() != null) {
            throw new ConflictException("채택된 답변이 있는 질문은 삭제할 수 없습니다.");
        }

        question.setIsDeleted(true);
        questionRepository.save(question);

        return QuestionDeleteResponse.of("질문이 삭제되었습니다.", true);
    }

    @Transactional
    public AcceptAnswerResponse acceptAnswer(Long questionId, AcceptAnswerRequest request, Long userId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("질문을 찾을 수 없습니다."));

        if (question.getIsDeleted()) {
            throw new NotFoundException("질문을 찾을 수 없습니다.");
        }
        if (!question.getUser().getId().equals(userId)) {
            throw new ForbiddenException("자신의 질문에만 답변을 채택할 수 있습니다.");
        }
        if (question.getAcceptedAnswerId() != null) {
            throw new ConflictException("이미 채택된 답변이 있습니다.");
        }

        Answer answer = answerRepository.findById(request.getAnswerId())
                .orElseThrow(() -> new NotFoundException("답변을 찾을 수 없습니다."));

        if (!answer.getQuestion().getId().equals(questionId)) {
            throw new IllegalArgumentException("해당 질문의 답변이 아닙니다.");
        }
        if (answer.getIsDeleted()) {
            throw new NotFoundException("답변을 찾을 수 없습니다.");
        }

        answer.setIsAccepted(true);
        answerRepository.save(answer);

        question.setAcceptedAnswerId(answer.getId());
        question.setStatus("closed");
        questionRepository.save(question);

        return AcceptAnswerResponse.of(question.getId(), answer.getId(), "답변이 채택되었습니다.");
    }

    public QuestionDetailResponse getQuestionDetail(Long questionId, Long currentUserId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("질문을 찾을 수 없습니다."));

        if (question.getIsDeleted()) {
            throw new NotFoundException("질문을 찾을 수 없습니다.");
        }

        // 부모 답변: 채택 우선 + 나머지 오래된 순(ASC)
        List<Answer> parentAnswers = answerRepository.findByQuestionIdOrderByAcceptedAndCreatedAt(questionId);

        List<QuestionDetailResponse.AnswerDto> answers = parentAnswers.stream()
                .map(a -> buildAnswerDtoFlat(a, currentUserId))
                .collect(Collectors.toList());

        List<QuestionDetailResponse.AttachmentDto> attachments = question.getAttachments().stream()
                .map(this::buildAttachmentDtoFlat)
                .collect(Collectors.toList());

        List<Long> tagIds = question.getQuestionTags().stream()
                .map(qt -> qt.getTag().getId())
                .collect(Collectors.toList());

        boolean isBookmarked = false;
        if (currentUserId != null) {
            isBookmarked = questionBookmarkRepository.existsByQuestion_IdAndUser_Id(questionId, currentUserId);
        }

        return QuestionDetailResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .body(question.getBody())
                .status(question.getStatus())
                .views(question.getViews())
                .isBookmarked(isBookmarked)
                .acceptedAnswerId(question.getAcceptedAnswerId())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .user(QuestionDetailResponse.UserSummary.builder()
                        .id(question.getUser().getId())
                        .nickname(question.getUser().getNickname())
                        .build())
                .tagIds(tagIds)
                .attachments(attachments)
                .answers(answers)
                .build();
    }

    private QuestionDetailResponse.AnswerDto buildAnswerDtoFlat(Answer answer, Long currentUserId) {
        long likeCount = answerLikeRepository.countByAnswer_Id(answer.getId());

        boolean isLiked = false;
        if (currentUserId != null) {
            isLiked = answerLikeRepository.existsByAnswer_IdAndUser_Id(answer.getId(), currentUserId);
        }

        // ✅ 대댓글 오래된 순(ASC) 정렬된 Repository 메서드 필요
        List<Answer> children = answerRepository
                .findByParentAnswerIdAndIsDeletedFalseOrderByCreatedAtAsc(answer.getId());

        List<QuestionDetailResponse.AnswerDto> childDtos = children.stream()
                .map(child -> buildAnswerDtoFlat(child, currentUserId))
                .collect(Collectors.toList());

        return QuestionDetailResponse.AnswerDto.builder()
                .id(answer.getId())
                .body(answer.getBody())
                .parentAnswerId(answer.getParentAnswerId())
                .isAccepted(answer.getIsAccepted())
                .likeCount(likeCount)
                .isLiked(isLiked)
                .createdAt(answer.getCreatedAt())
                .updatedAt(answer.getUpdatedAt())
                .user(QuestionDetailResponse.UserSummary.builder()
                        .id(answer.getUser().getId())
                        .nickname(answer.getUser().getNickname())
                        .build())
                .childAnswers(childDtos)
                .build();
    }

    private QuestionDetailResponse.AttachmentDto buildAttachmentDtoFlat(Attachment attachment) {
        return QuestionDetailResponse.AttachmentDto.builder()
                .id(attachment.getId())
                .fileUrl(attachment.getFileUrl())
                .mimeType(attachment.getMimeType())
                .build();
    }

    @Transactional
    public QuestionViewResponseDto incrementViews(Long questionId, Long userId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("질문을 찾을 수 없습니다."));

        if (question.getIsDeleted()) {
            throw new NotFoundException("삭제된 질문입니다.");
        }

        question.setViews(question.getViews() + 1);
        questionRepository.save(question);

        return QuestionViewResponseDto.builder()
                .questionId(questionId)
                .views(question.getViews())
                .build();
    }
}
