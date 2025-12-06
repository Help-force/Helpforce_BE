package com.web.helpforce.domain.question.service;

import com.web.helpforce.domain.attachment.service.FileStorageService;
import com.web.helpforce.domain.question.dto.AcceptAnswerRequest;
import com.web.helpforce.domain.question.dto.AcceptAnswerResponse;
import com.web.helpforce.domain.question.dto.QuestionCreateRequest;
import com.web.helpforce.domain.question.dto.QuestionCreateResponse;
import com.web.helpforce.domain.question.dto.QuestionDeleteResponse;
import com.web.helpforce.domain.question.dto.QuestionDetailResponse;
import com.web.helpforce.domain.question.dto.QuestionListPageResponse;
import com.web.helpforce.domain.question.dto.QuestionListResponse;
import com.web.helpforce.domain.question.dto.QuestionUpdateRequest;
import com.web.helpforce.domain.question.dto.QuestionUpdateResponse;
import com.web.helpforce.domain.question.dto.QuestionViewResponseDto;
import com.web.helpforce.domain.attachment.entity.Attachment;
import com.web.helpforce.domain.answer.entity.Answer;
import com.web.helpforce.domain.question.entity.Question;
import com.web.helpforce.domain.question.entity.QuestionTag;
import com.web.helpforce.domain.tag.entity.Tag;
import com.web.helpforce.domain.attachment.repository.AttachmentRepository;
import com.web.helpforce.domain.question.repository.QuestionRepository;
import com.web.helpforce.domain.question.repository.QuestionTagRepository;
import com.web.helpforce.domain.tag.repository.TagRepository;
import com.web.helpforce.domain.user.entity.User;
import com.web.helpforce.domain.answer.repository.AnswerRepository;
import com.web.helpforce.domain.answer.repository.AnswerLikeRepository;
import com.web.helpforce.domain.question.repository.QuestionBookmarkRepository;
import com.web.helpforce.domain.user.repository.UserRepository;
import com.web.helpforce.global.exception.ConflictException;
import com.web.helpforce.global.exception.ForbiddenException;
import com.web.helpforce.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
            String sort,
            int page,
            int size,
            Long currentUserId) {

        // 페이징 및 정렬 설정
        Sort sortOption = getSortOption(sort);
        Pageable pageable = PageRequest.of(page - 1, size, sortOption);

        // 질문 조회
        Page<Question> questionPage;
        if (tagIds != null && !tagIds.isEmpty()) {
            // 태그 필터링이 있는 경우
            questionPage = questionRepository.findByTagIdsAndIsDeletedFalse(tagIds, pageable);
        } else {
            // 전체 조회
            questionPage = questionRepository.findByIsDeletedFalse(pageable);
        }

        // DTO 변환
        List<QuestionListResponse> questions = questionPage.getContent().stream()
                .map(question -> toQuestionListResponse(question, currentUserId))
                .collect(Collectors.toList());

        // 페이지네이션 정보
        QuestionListPageResponse.Pagination pagination = QuestionListPageResponse.Pagination.builder()
                .currentPage(page)
                .totalPages(questionPage.getTotalPages())
                .totalItems(questionPage.getTotalElements())
                .itemsPerPage(size)
                .hasPrevious(questionPage.hasPrevious())
                .hasNext(questionPage.hasNext())
                .build();

        // 필터 정보
        QuestionListPageResponse.Filters filters = QuestionListPageResponse.Filters.builder()
                .tagIds(tagIds)
                .sort(sort != null ? sort : "latest")
                .build();

        return QuestionListPageResponse.builder()
                .questions(questions)
                .pagination(pagination)
                .filters(filters)
                .build();
    }

    private QuestionListResponse toQuestionListResponse(Question question, Long currentUserId) {
        // 답변 개수
        long answerCount = answerRepository.countByQuestion_IdAndIsDeletedFalse(question.getId());

        // 북마크 여부
        boolean isBookmarked = false;
        if (currentUserId != null) {
            isBookmarked = questionBookmarkRepository.existsByQuestion_IdAndUser_Id(
                    question.getId(), currentUserId);
        }

        // 태그 ID 목록
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
        if (sort == null || sort.equals("latest")) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        } else if (sort.equals("views")) {
            return Sort.by(Sort.Direction.DESC, "views");
        }
        return Sort.by(Sort.Direction.DESC, "createdAt");
    }

    @Transactional
    public QuestionCreateResponse createQuestion(QuestionCreateRequest request, Long userId) {
        // 1. 제목 필수 검증
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("제목은 필수 입력 항목입니다.");
        }

        // 2. 내용 길이 검증
        if (request.getBody() == null || request.getBody().length() < 10) {
            throw new IllegalArgumentException("질문 내용은 10자 이상 작성해주세요.");
        }

        // 3. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 4. Question 엔티티 생성
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

        // 5. 태그 연결
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<Tag> tags = tagRepository.findByIdIn(request.getTagIds());

            for (Tag tag : tags) {
                QuestionTag questionTag = QuestionTag.builder()
                        .question(question)
                        .tag(tag)
                        .build();
                question.getQuestionTags().add(questionTag);
            }
        }

        // 6. 파일 첨부 처리
        List<QuestionCreateResponse.AttachmentDto> attachmentDtos = new ArrayList<>();
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            System.out.println("=== 파일 개수: " + request.getFiles().size());
            for (MultipartFile file : request.getFiles()) {
                System.out.println("=== 파일 처리: " + file.getOriginalFilename() + ", isEmpty: " + file.isEmpty());
                if (!file.isEmpty()) {
                    // 파일 저장
                    String fileUrl = fileStorageService.storeFile(file);
                    System.out.println("=== 파일 URL: " + fileUrl);

                    // Attachment 엔티티 생성
                    Attachment attachment = Attachment.builder()
                            .question(question)
                            .fileUrl(fileUrl)
                            .mimeType(file.getContentType())
                            .build();

                    question.getAttachments().add(attachment);
                    System.out.println("=== Attachment 추가 완료, 현재 개수: " + question.getAttachments().size());

                    // 응답 DTO 추가 (임시 ID, 실제로는 저장 후 ID 할당)
                    attachmentDtos.add(QuestionCreateResponse.AttachmentDto.of(null, fileUrl));
                }
            }
        }

        // 7. DB 저장 (Question 먼저 저장)
        System.out.println("=== 저장 전 Attachments 개수: " + question.getAttachments().size());
        System.out.println("=== 저장 전 QuestionTags 개수: " + question.getQuestionTags().size());
        Question savedQuestion = questionRepository.save(question);
        System.out.println("=== 저장 후 Attachments 개수: " + savedQuestion.getAttachments().size());
        System.out.println("=== 저장 후 QuestionTags 개수: " + savedQuestion.getQuestionTags().size());

        // 8. 첨부파일 DTO 생성
        attachmentDtos.clear();
        for (Attachment attachment : savedQuestion.getAttachments()) {
            attachmentDtos.add(QuestionCreateResponse.AttachmentDto.of(
                    attachment.getId(),
                    attachment.getFileUrl()
            ));
        }

        // 9. 응답 반환
        return QuestionCreateResponse.of(
                savedQuestion.getId(),
                savedQuestion.getCreatedAt(),
                attachmentDtos
        );
    }

    @Transactional
    public QuestionUpdateResponse updateQuestion(Long questionId, QuestionUpdateRequest request, Long userId) {
        // 1. 질문 조회
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("질문을 찾을 수 없습니다."));

        // 2. 삭제된 질문인지 확인
        if (question.getIsDeleted()) {
            throw new NotFoundException("삭제된 질문은 수정할 수 없습니다.");
        }

        // 3. 작성자 권한 확인
        if (!question.getUser().getId().equals(userId)) {
            throw new ForbiddenException("자신의 질문만 수정할 수 있습니다.");
        }

        // 4. 제목 검증
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("제목은 필수 입력 항목입니다.");
        }

        // 5. 내용 검증
        if (request.getBody() == null || request.getBody().trim().isEmpty()) {
            throw new IllegalArgumentException("내용은 필수 입력 항목입니다.");
        }

        // 6. 질문 정보 수정
        question.setTitle(request.getTitle());
        question.setBody(request.getBody());

        // 7. 태그 수정 (기존 태그 완전히 삭제 후 새로 추가)
        questionTagRepository.deleteByQuestionId(questionId);

        // 변경사항 즉시 반영
        questionRepository.flush();

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<Tag> tags = tagRepository.findByIdIn(request.getTagIds());

            for (Tag tag : tags) {
                QuestionTag questionTag = QuestionTag.builder()
                        .question(question)
                        .tag(tag)
                        .build();
                questionTagRepository.save(questionTag);
            }
        }

        // 8. 질문 다시 조회해서 최신 상태로 응답
        Question updatedQuestion = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("질문을 찾을 수 없습니다."));

        // 9. 응답 생성
        List<Long> tagIds = updatedQuestion.getQuestionTags().stream()
                .map(qt -> qt.getTag().getId())
                .collect(Collectors.toList());

        return QuestionUpdateResponse.of(
                updatedQuestion.getTitle(),
                updatedQuestion.getBody(),
                tagIds
        );
    }

    @Transactional
    public QuestionDeleteResponse deleteQuestion(Long questionId, Long userId) {
        // 1. 질문 조회
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("질문을 찾을 수 없습니다."));

        // 2. 이미 삭제된 질문인지 확인
        if (question.getIsDeleted()) {
            throw new NotFoundException("질문을 찾을 수 없습니다.");
        }

        // 3. 작성자 권한 확인
        if (!question.getUser().getId().equals(userId)) {
            throw new ForbiddenException("자신의 질문만 삭제할 수 있습니다.");
        }

        // 4. 채택된 답변이 있는지 확인
        if (question.getAcceptedAnswerId() != null) {
            throw new ConflictException("채택된 답변이 있는 질문은 삭제할 수 없습니다.");
        }

        // 5. Soft Delete 처리
        question.setIsDeleted(true);
        questionRepository.save(question);

        // 6. 응답 반환
        return QuestionDeleteResponse.of("질문이 삭제되었습니다.", true);
    }

    @Transactional
    public AcceptAnswerResponse acceptAnswer(Long questionId, AcceptAnswerRequest request, Long userId) {
        // 1. 질문 조회
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("질문을 찾을 수 없습니다."));

        // 2. 삭제된 질문인지 확인
        if (question.getIsDeleted()) {
            throw new NotFoundException("질문을 찾을 수 없습니다.");
        }

        // 3. 질문 작성자 권한 확인
        if (!question.getUser().getId().equals(userId)) {
            throw new ForbiddenException("자신의 질문에만 답변을 채택할 수 있습니다.");
        }

        // 4. 이미 채택된 답변이 있는지 확인
        if (question.getAcceptedAnswerId() != null) {
            throw new ConflictException("이미 채택된 답변이 있습니다.");
        }

        // 5. 답변 조회
        Answer answer = answerRepository.findById(request.getAnswerId())
                .orElseThrow(() -> new NotFoundException("답변을 찾을 수 없습니다."));

        // 6. 답변이 이 질문에 속한 답변인지 확인
        if (!answer.getQuestion().getId().equals(questionId)) {
            throw new IllegalArgumentException("해당 질문의 답변이 아닙니다.");
        }

        // 7. 삭제된 답변인지 확인
        if (answer.getIsDeleted()) {
            throw new NotFoundException("답변을 찾을 수 없습니다.");
        }

        // 8. 답변 채택 처리
        answer.setIsAccepted(true);
        answerRepository.save(answer);

        // 9. 질문에 채택된 답변 ID 저장 및 상태 변경
        question.setAcceptedAnswerId(answer.getId());
        question.setStatus("closed");
        questionRepository.save(question);

        // 10. 응답 반환
        return AcceptAnswerResponse.of(
                question.getId(),
                answer.getId(),
                "답변이 채택되었습니다."
        );
    }

    public QuestionDetailResponse getQuestionDetail(Long questionId, Long currentUserId) {
        // 1. 질문 조회
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("질문을 찾을 수 없습니다."));

        // 2. 삭제된 질문인지 확인
        if (question.getIsDeleted()) {
            throw new NotFoundException("질문을 찾을 수 없습니다.");
        }

        // 3. 질문 정보 생성
        QuestionDetailResponse.QuestionDto questionDto = buildQuestionDto(question, currentUserId);

        // 4. 답변 목록 조회 (댓글만, parent_answer_id가 null인 것)
        List<Answer> parentAnswers = answerRepository.findByQuestion_IdAndParentAnswerIdIsNullAndIsDeletedFalse(questionId);

        // 5. 답변 DTO 변환 (대댓글 포함)
        List<QuestionDetailResponse.AnswerDto> answerDtos = parentAnswers.stream()
                .map(answer -> buildAnswerDto(answer, currentUserId))
                .collect(Collectors.toList());

        // 6. 첨부파일 목록 조회
        List<Attachment> attachments = question.getAttachments();
        List<QuestionDetailResponse.AttachmentDto> attachmentDtos = attachments.stream()
                .map(this::buildAttachmentDto)
                .collect(Collectors.toList());

        // 7. 응답 생성
        return QuestionDetailResponse.builder()
                .question(questionDto)
                .answers(answerDtos)
                .attachments(attachmentDtos)
                .build();
    }

    private QuestionDetailResponse.QuestionDto buildQuestionDto(Question question, Long currentUserId) {
        // 답변 개수
        long answerCount = answerRepository.countByQuestion_IdAndIsDeletedFalse(question.getId());

        // 북마크 여부
        boolean isBookmarked = false;
        if (currentUserId != null) {
            isBookmarked = questionBookmarkRepository.existsByQuestion_IdAndUser_Id(
                    question.getId(), currentUserId);
        }

        // 태그 ID 목록
        List<Long> tagIds = question.getQuestionTags().stream()
                .map(qt -> qt.getTag().getId())
                .collect(Collectors.toList());

        return QuestionDetailResponse.QuestionDto.builder()
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
                        .email(question.getUser().getEmail())
                        .build())
                .tagIds(tagIds)
                .answerCount(answerCount)
                .build();
    }

    private QuestionDetailResponse.AnswerDto buildAnswerDto(Answer answer, Long currentUserId) {
        // 좋아요 개수
        long likeCount = answerLikeRepository.countByAnswer_Id(answer.getId());

        // 좋아요 여부
        boolean isLiked = false;
        if (currentUserId != null) {
            isLiked = answerLikeRepository.existsByAnswer_IdAndUser_Id(answer.getId(), currentUserId);
        }

        // 대댓글 조회
        List<Answer> replies = answerRepository.findByParentAnswerIdAndIsDeletedFalse(answer.getId());
        List<QuestionDetailResponse.AnswerDto> replyDtos = replies.stream()
                .map(reply -> buildAnswerDto(reply, currentUserId)) // 재귀 호출
                .collect(Collectors.toList());

        return QuestionDetailResponse.AnswerDto.builder()
                .id(answer.getId())
                .body(answer.getBody())
                .parentAnswerId(answer.getParentAnswerId())
                .isAccepted(answer.getIsAccepted())
                .createdAt(answer.getCreatedAt())
                .updatedAt(answer.getUpdatedAt())
                .user(QuestionDetailResponse.UserSummary.builder()
                        .id(answer.getUser().getId())
                        .nickname(answer.getUser().getNickname())
                        .email(answer.getUser().getEmail())
                        .build())
                .likeCount(likeCount)
                .isLiked(isLiked)
                .replies(replyDtos)
                .build();
    }

    private QuestionDetailResponse.AttachmentDto buildAttachmentDto(Attachment attachment) {
        return QuestionDetailResponse.AttachmentDto.builder()
                .id(attachment.getId())
                .questionId(attachment.getQuestion() != null ? attachment.getQuestion().getId() : null)
                .answerId(attachment.getAnswer() != null ? attachment.getAnswer().getId() : null)
                .fileUrl(attachment.getFileUrl())
                .mimeType(attachment.getMimeType())
                .build();
    }

    /**
     * 질문 조회수 증가
     */
    @Transactional
    public QuestionViewResponseDto incrementViews(Long questionId, Long userId) {
        System.out.println("=== Question View Increment Debug ===");
        System.out.println("questionId: " + questionId);
        System.out.println("userId: " + userId);

        // 1. 질문 존재 확인
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("질문을 찾을 수 없습니다."));

        // 2. 삭제된 질문인지 확인
        if (question.getIsDeleted()) {
            throw new NotFoundException("삭제된 질문입니다.");
        }

        // 3. 조회수 증가
        question.setViews(question.getViews() + 1);
        questionRepository.save(question);

        System.out.println("✅ 조회수 증가 완료: " + question.getViews());

        // 4. 응답 DTO 생성
        return QuestionViewResponseDto.builder()
                .questionId(questionId)
                .views(question.getViews())
                .build();
    }
}