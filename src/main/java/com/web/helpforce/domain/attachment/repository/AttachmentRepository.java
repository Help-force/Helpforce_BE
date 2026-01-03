package com.web.helpforce.domain.attachment.repository;

import com.web.helpforce.domain.attachment.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    void deleteByQuestion_Id(Long questionId);
    List<Attachment> findByQuestion_Id(Long questionId);
}
