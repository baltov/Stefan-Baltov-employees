package org.sirma.sb.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.sirma.sb.model.entities.UserEntity;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT u FROM UserEntity u WHERE u.project.id = ?1 order by u.startDate")
    List<UserEntity> findAllByProjectIdOrderByStartDate(Long id);
}
