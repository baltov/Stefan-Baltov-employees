package org.sirma.sb.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponseDto {
    private Long id;
    private String fileName;
    private LocalDateTime created;
    private FileStatusEnum status;
    private String result;
}
