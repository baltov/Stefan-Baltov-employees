package org.sirma.sb.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sirma.sb.model.ErrorDto;
import org.sirma.sb.model.FileUploadResponseDto;
import org.sirma.sb.model.ReportDto;
import org.sirma.sb.services.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "File Management", description = "APIs to manage file uploads and their analysis")
public class FileController {

    private final FileService fileService;

    @ResponseBody
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @Operation(summary = "Upload a file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File uploaded successfully",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file provided")
    })
    
    public FileUploadResponseDto upload(@RequestParam("file") MultipartFile file) {
        return fileService.saveFile(file);
    }

    // TODO: add pagination
    @GetMapping("/files")
    @Operation(summary = "List all files", description = "Fetches a list of all uploaded files.")
    public List<FileUploadResponseDto> listFiles() {
        return fileService.getFileList();
    }

    @DeleteMapping("/files/{id}")
    @Operation(summary = "Delete a file", description = "Deletes a file by its ID.")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        fileService.deleteFile(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/files/{id}/reports")
    @Operation(summary = "Get reports for a file", description = "Fetches reports associated with the given file ID.")
    public List<ReportDto> getReportsForFile(@PathVariable Long id) {
        return fileService.getReportsForFile(id);
    }

    @GetMapping("/files/{id}/errors")
    @Operation(summary = "Get errors for a file", description = "Fetches errors associated with the given file ID.")
    public List<ErrorDto> getErrorsForFile(@PathVariable Long id) {
        return fileService.getErrorsForFile(id);
    }
}
