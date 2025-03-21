package org.sirma.sb.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.sirma.sb.model.entities.ErrorEntity;

import java.util.List;

@Repository
public interface ErrorRepository extends JpaRepository<ErrorEntity, Long> {
    List<ErrorEntity> findAllByFileIdOrderByCreated(Long fileId);

    void deleteAllByFileId(Long fileId);
}
