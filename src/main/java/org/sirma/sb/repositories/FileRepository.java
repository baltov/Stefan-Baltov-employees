package org.sirma.sb.repositories;

import org.sirma.sb.model.entities.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findAllByOrderByCreatedDesc();
}
