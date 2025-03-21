package org.sirma.sb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CsvRow {
    @JsonProperty("EmpID")
    private String userId;

    @JsonProperty("ProjectID")
    private String projectId;

    @JsonProperty("DateFrom")
    private String startDate;

    @JsonProperty("DateTo")
    private String endDate;
}