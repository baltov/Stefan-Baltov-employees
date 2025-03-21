package org.sirma.sb.services;

import lombok.RequiredArgsConstructor;
import org.sirma.sb.exceptions.FileSaveException;
import org.sirma.sb.model.*;
import org.sirma.sb.model.entities.ErrorEntity;
import org.sirma.sb.model.entities.FileEntity;
import org.sirma.sb.model.entities.ReportEntity;
import org.sirma.sb.repositories.ErrorRepository;
import org.sirma.sb.repositories.FileRepository;
import org.sirma.sb.repositories.ReportRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final ErrorRepository errorRepository;
    private final ReportRepository reportRepository;
    private final FileProcessingService fileProcessingService;


    @Value("${application.path:/files}")
    private String path;

    @Transactional
    public FileUploadResponseDto saveFile(MultipartFile file) {
        try {
            File directory = new File(path);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Generate unique file path
            String originalFileName = file.getOriginalFilename();
            Path filePath = getUniqueFilePath(Paths.get(path, originalFileName));

            try (InputStream inputStream = file.getInputStream();
                 OutputStream outputStream = new FileOutputStream(filePath.toFile());
                 BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 8 * 1024);
                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream, 8 * 1024)) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                    bufferedOutputStream.write(buffer, 0, bytesRead);
                }
            }

            FileEntity fileEntity = new FileEntity();
            fileEntity.setOriginalFileName(file.getOriginalFilename());
            fileEntity.setPath(filePath.toString());
            fileEntity.setSize(file.getSize());
            fileEntity.setStatus(FileStatusEnum.UPLOADED);
            fileEntity.setCreated(LocalDateTime.now());
            fileEntity = fileRepository.save(fileEntity);

            //start file processing async
            fileProcessingService.parseFile(fileEntity.getId());

            return FileUploadResponseDto.builder()
                    .fileName(fileEntity.getOriginalFileName())
                    .id(fileEntity.getId())
                    .status(fileEntity.getStatus())
                    .created(fileEntity.getCreated())
                    .result("Waiting for processing to complete.")
                    .build();

        } catch (IOException e) {
            throw new FileSaveException("Failed to save the file to disk", e);
        } catch (Exception e) {
            throw new FileSaveException("Failed to save file data to the database", e);
        }
    }


    //TODO: add pagination
    @Transactional(readOnly = true)
    public List<ErrorDto> getErrorsForFile(Long fileId) {
        List<ErrorEntity> errorEntities = errorRepository.findAllByFileIdOrderByCreated(fileId);
        if (CollectionUtils.isEmpty(errorEntities)) {
            return List.of();
        }
        return errorEntities.stream()
                .map(errorEntity -> ErrorDto.builder()
                        .fileId(errorEntity.getFileId())
                        .lineNumber(errorEntity.getLineNumber())
                        .message(errorEntity.getMessage())
                        .created(errorEntity.getCreated())
                        .build())
                .toList();
    }


    @Transactional(readOnly = true)
    public List<FileUploadResponseDto> getFileList() {
        var result = fileRepository.findAllByOrderByCreatedDesc()
                .stream().map(fileEntity -> FileUploadResponseDto.builder()
                        .fileName(fileEntity.getOriginalFileName())
                        .id(fileEntity.getId())
                        .created(fileEntity.getCreated())
                        .status(fileEntity.getStatus())
                        .result(fileProcessingService.createResultForFile(fileEntity.getId()))
                        .build()).toList();
        return result;
    }

    @Transactional(readOnly = true)
    public List<ReportDto> getReportsForFile(Long fileId) {
        List<ReportEntity> entities = reportRepository.findAllByFileIdOrderByDaysDesc(fileId);
        if (CollectionUtils.isEmpty(entities)) {
            return List.of();
        }
        var result = entities.stream().map(entity -> ReportDto.builder()
                .projectId(entity.getProjectId())
                .oneUserId(entity.getOneUserId())
                .secondUserId(entity.getSecondUserId())
                .days(entity.getDays())
                .build()).toList();
        return result;
    }


    @Transactional
    public void deleteFile(Long fileId) {
        FileEntity fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileSaveException("File with ID " + fileId + " not found in the database."));

        File fileOnDisk = new File(fileEntity.getPath());

        if (fileOnDisk.exists()) {
            if (!fileOnDisk.delete()) {
                throw new FileSaveException("Failed to delete file from disk: " + fileEntity.getPath());
            }
        }

        reportRepository.deleteByFileId(fileId);
        errorRepository.deleteAllByFileId(fileId);
        fileRepository.delete(fileEntity);
    }


    /**
     * Ensures the file path is unique by checking the existence of the file and adding a numeric suffix if needed.
     *
     * @param filePath The initial file path
     * @return A unique file path
     */
    private Path getUniqueFilePath(Path filePath) {
        String fileName = filePath.getFileName().toString();
        String fileNameWithoutExtension = fileName.contains(".")
                ? fileName.substring(0, fileName.lastIndexOf('.'))
                : fileName;
        String extension = fileName.contains(".")
                ? fileName.substring(fileName.lastIndexOf('.'))
                : "";

        int counter = 0;
        Path uniqueFilePath = filePath;
        while (uniqueFilePath.toFile().exists()) {
            counter++;
            String newFileName = fileNameWithoutExtension + "_" + counter + extension;
            uniqueFilePath = Paths.get(filePath.getParent().toString(), newFileName);
        }
        return uniqueFilePath;
    }


}