package org.sirma.sb.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {
    private Long projectId;
    private Long oneUserId;
    private Long secondUserId;
    private long days;
}
