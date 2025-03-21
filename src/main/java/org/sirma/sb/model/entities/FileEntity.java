package org.sirma.sb.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.sirma.sb.model.FileStatusEnum;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "files")
@Data
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String originalFileName;

    @Lob
    @Column(name = "path", nullable = false)
    @JsonIgnore
    private String path;

    @Column(name = "size")
    private Long size;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FileStatusEnum status;

    @Column(name = "created", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime created;

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectEntity> projects;


}