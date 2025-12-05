package com.web.helpforce.domain.question.entity;

import com.web.helpforce.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "question_bookmarks")
@IdClass(QuestionBookmark.QuestionBookmarkId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionBookmark {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 복합 키 클래스
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionBookmarkId implements Serializable {
        private Long question;
        private Long user;
    }
}