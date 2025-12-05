package com.web.helpforce.domain.user.repository;

import com.web.helpforce.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일으로 사용자 찾기 (로그인 시 사용)
    Optional<User> findByEmail(String email);

    // 이메일 중복 체크
    boolean existsByEmail(String email);

    // 닉네임 중복 체크
    boolean existsByNickname(String nickname);
}