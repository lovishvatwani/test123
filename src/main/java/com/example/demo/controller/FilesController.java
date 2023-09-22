package com.example.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.model.DBFile;
import com.example.demo.response.ResponseFile;
import com.example.demo.response.ResponseMessage;
import com.example.demo.service.FileHandlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@Controller
@CrossOrigin("http://localhost:8081")
public class FilesController {

    @Autowired
    FileHandlingService storageService;

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            storageService.store(file);

            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<ResponseFile>> getListFiles() {
        List<ResponseFile> fileInfos = storageService.getAllFiles().map(dbFile -> {
            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/files/")
                    .path(dbFile.getId())
                    .toUriString();

            return new ResponseFile(
                    dbFile.getName(),
                    fileDownloadUri,
                    dbFile.getType(),
                    dbFile.getData().length);
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<byte[]> getFile(@PathVariable String filename) {
        DBFile file = storageService.getFile(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"").body(file.getData());
    }

    @DeleteMapping("/files/{filename:.+}")
    public ResponseEntity<ResponseMessage> deleteFile(@PathVariable String filename) {
        String message = "";

        try {
            boolean existed = storageService.checkIfFileExists(filename);


            if (existed) {
                storageService.delete(filename);
                message = "Delete the file successfully: " + filename;
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            }

            message = "The file does not exist!";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Could not delete the file: " + filename + ". Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage(message));
        }
    }
}
