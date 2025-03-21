package org.sirma.sb.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sirma.sb.exceptions.FileSaveException;
import org.sirma.sb.model.entities.FileEntity;
import org.sirma.sb.model.FileStatusEnum;
import org.sirma.sb.model.FileUploadResponseDto;
import org.sirma.sb.repositories.ErrorRepository;
import org.sirma.sb.repositories.FileRepository;
import org.sirma.sb.repositories.ReportRepository;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileServiceTest {

    private FileRepository fileRepository = Mockito.mock(FileRepository.class);

    private ErrorRepository errorRepository = Mockito.mock(ErrorRepository.class);

    private ReportRepository reportRepository = Mockito.mock(ReportRepository.class);

    private FileProcessingService fileProcessingService = Mockito.mock(FileProcessingService.class);

    private FileService fileService;
    @BeforeEach
    void setUp() {

        fileService = new FileService(fileRepository, errorRepository, reportRepository, fileProcessingService);

        ReflectionTestUtils.setField(fileService, "path", "./files");

    }
    @Test
    void saveFile_Success() throws IOException {

        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test-file.txt");
        when(mockFile.getInputStream()).thenReturn(InputStream.nullInputStream());
        when(mockFile.getSize()).thenReturn(1024L);

        FileEntity savedFileEntity = new FileEntity();
        savedFileEntity.setId(1L);
        savedFileEntity.setOriginalFileName("test-file.txt");
        savedFileEntity.setPath("./files/test-file.txt");
        savedFileEntity.setSize(1024L);
        savedFileEntity.setStatus(FileStatusEnum.UPLOADED);
        savedFileEntity.setCreated(LocalDateTime.now());

        when(fileRepository.save(any(FileEntity.class))).thenReturn(savedFileEntity);

        FileUploadResponseDto responseDto = fileService.saveFile(mockFile);

        assertNotNull(responseDto);
        assertEquals("test-file.txt", responseDto.getFileName());
        assertEquals(1L, responseDto.getId());
        assertEquals(FileStatusEnum.UPLOADED, responseDto.getStatus());
        assertEquals("Waiting for processing to complete.", responseDto.getResult());

        verify(fileProcessingService, times(1)).parseFile(1L);
    }

    @Test
    void saveFile_FileSaveToDiskFailure() throws IOException {

        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test-file.txt");
        when(mockFile.getInputStream()).thenThrow(new IOException("Disk error"));

        FileSaveException exception = assertThrows(FileSaveException.class, () -> fileService.saveFile(mockFile));
        assertEquals("Failed to save the file to disk", exception.getMessage());
    }

    @Test
    void saveFile_FileSaveToDatabaseFailure() throws IOException {

        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test-file.txt");
        when(mockFile.getInputStream()).thenReturn(InputStream.nullInputStream());
        when(mockFile.getSize()).thenReturn(1024L);

        when(fileRepository.save(any(FileEntity.class))).thenThrow(new RuntimeException("Database error"));

        FileSaveException exception = assertThrows(FileSaveException.class, () -> fileService.saveFile(mockFile));
        assertEquals("Failed to save file data to the database", exception.getMessage());
    }

}