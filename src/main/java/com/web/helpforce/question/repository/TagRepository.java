package com.web.helpforce.question.repository;

import com.web.helpforce.question.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findByIdIn(List<Long> ids);
}
