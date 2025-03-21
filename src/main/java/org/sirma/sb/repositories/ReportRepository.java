package org.sirma.sb.repositories;

import org.sirma.sb.model.UsersMaxDaysIntf;
import org.sirma.sb.model.entities.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {

    @Modifying
    @Query("delete from ReportEntity r where r.fileId = ?1")
    int deleteByFileId(Long fileId);

    List<ReportEntity> findAllByFileIdOrderByDaysDesc(Long fileId);

    @Query("select r from ReportEntity r where r.fileId = ?1 order by r.days desc limit 1")
    Optional<ReportEntity> findMaxForFileId(Long id);

    @Query(value = "select sum(days) as maxDays, one_user_id as user1Id, " +
            "second_user_id as user2Id " +
            "from reports where reports.file_id = ?1 group by one_user_id,second_user_id\n" +
            "order by sum(days) desc limit 1;", nativeQuery = true)
    Optional<UsersMaxDaysIntf> getMaxDaysForFileId(Long fileId);
}
