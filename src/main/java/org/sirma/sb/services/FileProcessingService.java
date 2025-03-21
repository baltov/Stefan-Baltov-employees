package org.sirma.sb.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sirma.sb.exceptions.FileSaveException;
import org.sirma.sb.model.*;
import org.sirma.sb.model.entities.*;
import org.sirma.sb.repositories.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileProcessingService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    private final ReportRepository reportRepository;
    private final ErrorRepository errorRepository;
    private final SseService sseService;

    @Async
    @Transactional
    public void parseFile(Long fileId) {
        FileEntity fileEntity = fileRepository.findById(fileId).orElse(null);

        if (fileEntity == null) {
            handleException(fileId, 0L, new FileSaveException("File with ID " + fileId + " not found in the database."));
            return;
        }

        fileEntity.setStatus(FileStatusEnum.PROCESSING);
        fileRepository.save(fileEntity);

        CsvParser.parseCsv(
                fileEntity.getPath(),
                (lineNumber, row) -> handleRow(fileEntity, lineNumber, row),
                (lineNumber, exception) -> handleException(fileId, lineNumber, exception),
                lineNumber -> handleProgress(fileId, lineNumber)
        );

        processProjects(fileId);

        fileEntity.setStatus(FileStatusEnum.PROCESSED);
        fileRepository.save(fileEntity);
        var dto = createResponseDto(fileEntity);
        sseService.broadcastEvent("updateFile", dto);

    }

    private void handleException(Long fileId, Long lineNumber, Exception exception) {
        var errorEntity = ErrorEntity.builder()
                .message(exception.getMessage())
                .fileId(fileId)
                .lineNumber(lineNumber)
                .created(LocalDateTime.now())
                .build();
        errorRepository.save(errorEntity);
    }

    private void handleRow(FileEntity fileEntity, Long lineNumber, CsvRow row) {
        try {
            Long projectId;
            Long userId;
            try {
                userId = Long.valueOf(row.getUserId().trim());
                if (userId.equals(11)) {
                    log.info("Skipping line: {}", lineNumber);
                }
                projectId = Long.valueOf(row.getProjectId().trim());

            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Project ID and User ID must be numbers", e);
            }
            LocalDate startDate = DateParser.parseToLocalDate(row.getStartDate().trim());
            LocalDate endDate = StringUtils.hasText(row.getEndDate()) && !"NULL".equalsIgnoreCase(row.getEndDate().trim())
                    ? DateParser.parseToLocalDate(row.getEndDate().trim()) : LocalDate.now();
            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("Start date cannot be after end date");
            }

            ProjectEntity projectEntity = projectRepository.findByProjectIdAndFileId(projectId, fileEntity.getId()).orElse(null);
            if (projectEntity == null) {
                projectEntity = new ProjectEntity();
                projectEntity.setProjectId(projectId);
                projectEntity.setFile(fileEntity);
                projectRepository.save(projectEntity);
            }
            UserEntity userEntity = new UserEntity();
            userEntity.setUserId(userId);
            userEntity.setStartDate(startDate);
            userEntity.setEndDate(endDate);
            userEntity.setProject(projectEntity);
            userRepository.save(userEntity);

        } catch (Exception e) {
            handleException(fileEntity.getId(), lineNumber, e);
        }
    }

    private void handleProgress(Long fileId, Long lineNumber) {

        log.info("Processing line: {}", lineNumber);
        var dto = new FileUploadResponseDto();
        dto.setId(fileId);
        dto.setResult("Processed lines: " + lineNumber);
        sseService.broadcastEvent("updateFile", dto);
    }


    void processProjects(Long fileId) {
        List<ProjectEntity> projects = projectRepository.findAllByFileId(fileId);
        if (CollectionUtils.isEmpty(projects)) {
            return;
        }
        projects.stream().forEach(projectEntity -> createProjectResult(fileId, projectEntity.getId()));
    }

    protected void createProjectResult(Long fileId, Long projectId) {
        List<UserEntity> users = userRepository.findAllByProjectIdOrderByStartDate(projectId);
        if (CollectionUtils.isEmpty(users) || users.size() < 2) {
            return;
        }
        // Users are ordered by start date
        for (int i = 0; i < users.size() - 1; i++) {
            UserEntity firstUser = users.get(i);
            for (int j = i + 1; j < users.size(); j++) {
                // This user has start date equal or after the previous
                UserEntity secondUser = users.get(j);

                if (firstUser.getEndDate().isBefore(secondUser.getStartDate())) {
                    continue;
                }

                //we know that first user has earlier start date or equal to the start date of second user
                LocalDate maxStartDate = secondUser.getStartDate();

                LocalDate minEndDate = firstUser.getEndDate().isAfter(secondUser.getEndDate())
                        ? secondUser.getEndDate() : firstUser.getEndDate();

                long days = ChronoUnit.DAYS.between(maxStartDate, minEndDate);
                 var user1 = firstUser.getUserId() > secondUser.getUserId() ? firstUser.getUserId() : secondUser.getUserId();
                 var user2 = firstUser.getUserId() < secondUser.getUserId() ? firstUser.getUserId() : secondUser.getUserId();

                var reportEntity = ReportEntity.builder()
                        .fileId(fileId)
                        .projectId(firstUser.getProject().getProjectId())
                        //order ids so we can query them :)
                        .oneUserId(user1)
                        .secondUserId(user2)
                        .days(days)
                        .startDate(maxStartDate)
                        .endDate(minEndDate)
                        .users(user1.toString() + "_" + user2.toString())
                        .build();
                reportRepository.save(reportEntity);
            }
        }
    }

    private FileUploadResponseDto createResponseDto(FileEntity fileEntity) {
        var dto =  FileUploadResponseDto.builder()
                .fileName(fileEntity.getOriginalFileName())
                .id(fileEntity.getId())
                .created(fileEntity.getCreated())
                .status(fileEntity.getStatus())
                .result(createResultForFile(fileEntity.getId()))
                .build();
        return dto;
    }

    public String createResultForFile(Long id) {
        var report = reportRepository.getMaxDaysForFileId(id);
        if (report.isPresent()) {
            return String.format("Emp1 ID: %d, Emp2 ID: %d, Days: %d", report.get().getUser1Id(),
                    report.get().getUser2Id(), report.get().getMaxDays());
        }
        return "No data found";
    }

}