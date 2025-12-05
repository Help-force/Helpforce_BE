package com.web.helpforce.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = true, length = 255)
    private String passwordHash;

    @Column(nullable = true, length = 255)
    private String nickname;

    @Column(name = "crm_generation", nullable = true, length = 255)
    private String crmGeneration;

    @Column(nullable = true, length = 255)
    private String department;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 비밀번호 업데이트 메서드
    public void updatePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }

    // 프로필 업데이트 메서드
    public void updateProfile(String nickname, String department) {
        this.nickname = nickname;
        this.department = department;
    }
}