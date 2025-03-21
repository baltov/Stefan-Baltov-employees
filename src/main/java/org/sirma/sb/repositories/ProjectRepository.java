package org.sirma.sb.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.sirma.sb.model.entities.ProjectEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
    @Query("SELECT p FROM ProjectEntity p WHERE p.projectId = ?1 AND p.file.id = ?2")
    Optional<ProjectEntity> findByProjectIdAndFileId(Long projectId, Long fileId);

    @Query("SELECT p FROM ProjectEntity p WHERE p.file.id = ?1")
    List<ProjectEntity> findAllByFileId(Long fileId);

    @Query("DELETE FROM ProjectEntity p WHERE p.file.id = ?1")
    void deleteByFileId(Long fileId);
}
