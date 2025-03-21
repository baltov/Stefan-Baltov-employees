package org.sirma.sb.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "reports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_id", nullable = false)
    private Long fileId;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "one_user_id", nullable = false)
    private Long oneUserId;

    @Column(name = "second_user_id", nullable = false)
    private Long secondUserId;

    @Column(name = "days", nullable = false)
    private long days;

    @Column
    private String users;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

}
