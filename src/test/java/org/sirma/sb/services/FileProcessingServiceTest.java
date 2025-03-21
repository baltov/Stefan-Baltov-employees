package org.sirma.sb.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sirma.sb.model.entities.ProjectEntity;
import org.sirma.sb.model.entities.ReportEntity;
import org.sirma.sb.model.entities.UserEntity;
import org.sirma.sb.repositories.ProjectRepository;
import org.sirma.sb.repositories.ReportRepository;
import org.sirma.sb.repositories.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FileProcessingServiceTest {

    private final ProjectRepository projectRepository = Mockito.mock(ProjectRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final ReportRepository reportRepository = Mockito.mock(ReportRepository.class);
    private final FileProcessingService fileProcessingService = new FileProcessingService(projectRepository, userRepository, null, reportRepository, null, null);

    @Test
    void testCreateProjectResult_WithValidData_ShouldSaveReports() {
        Long fileId = 1L;
        Long projectId = 1L;

        List<UserEntity> users = new ArrayList<>();

        ProjectEntity project = new ProjectEntity();
        project.setId(projectId);
        project.setProjectId(projectId);
        UserEntity user1 = new UserEntity();
        user1.setUserId(1L);
        user1.setProject(project);
        user1.setStartDate(LocalDate.of(2023, 1, 1));
        user1.setEndDate(LocalDate.of(2023, 3, 1));

        UserEntity user2 = new UserEntity();
        user2.setUserId(2L);
        user2.setProject(project);
        user2.setStartDate(LocalDate.of(2023, 2, 1));
        user2.setEndDate(LocalDate.of(2023, 4, 1));

        users.add(user1);
        users.add(user2);

        when(userRepository.findAllByProjectIdOrderByStartDate(projectId)).thenReturn(users);

        fileProcessingService.createProjectResult(fileId, projectId);

        verify(reportRepository, times(1)).save(any(ReportEntity.class));
    }

    @Test
    void testCreateProjectResult_WithNoUsers_ShouldNotSaveReports() {
        Long fileId = 1L;
        Long projectId = 1L;

        when(userRepository.findAllByProjectIdOrderByStartDate(projectId)).thenReturn(new ArrayList<>());

        fileProcessingService.createProjectResult(fileId, projectId);

        verify(reportRepository, never()).save(any(ReportEntity.class));
    }

    @Test
    void testCreateProjectResult_WithNoOverlappingDates_ShouldNotSaveReports() {
        Long fileId = 1L;
        Long projectId = 1L;

        List<UserEntity> users = new ArrayList<>();

        UserEntity user1 = new UserEntity();
        user1.setUserId(1L);
        user1.setStartDate(LocalDate.of(2023, 1, 1));
        user1.setEndDate(LocalDate.of(2023, 2, 1));

        UserEntity user2 = new UserEntity();
        user2.setUserId(2L);
        user2.setStartDate(LocalDate.of(2023, 3, 1));
        user2.setEndDate(LocalDate.of(2023, 4, 1));

        users.add(user1);
        users.add(user2);

        when(userRepository.findAllByProjectIdOrderByStartDate(projectId)).thenReturn(users);

        fileProcessingService.createProjectResult(fileId, projectId);

        verify(reportRepository, never()).save(any(ReportEntity.class));
    }

    @Test
    void testCreateProjectResult_WithSingleUser_ShouldNotSaveReports() {
        Long fileId = 1L;
        Long projectId = 1L;

        List<UserEntity> users = new ArrayList<>();

        UserEntity user1 = new UserEntity();
        user1.setUserId(1L);
        user1.setStartDate(LocalDate.of(2023, 1, 1));
        user1.setEndDate(LocalDate.of(2023, 2, 1));

        users.add(user1);

        when(userRepository.findAllByProjectIdOrderByStartDate(projectId)).thenReturn(users);

        fileProcessingService.createProjectResult(fileId, projectId);

        verify(reportRepository, never()).save(any(ReportEntity.class));
    }
}