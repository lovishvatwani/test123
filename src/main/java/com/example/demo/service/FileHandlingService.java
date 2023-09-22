package com.example.demo.service;

import com.example.demo.model.DBFile;
import com.example.demo.repository.DBFileRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.stream.Stream;

@Service
@Component
public class FileHandlingService {
    @Autowired
    private DBFileRepo DBFileRepository;

    public DBFile store(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        DBFile DBFile = new DBFile(fileName, file.getContentType(), file.getBytes());

        return DBFileRepository.save(DBFile);
    }

    public DBFile getFile(String id) {
        return DBFileRepository.findById(id).get();
    }

    public Stream<DBFile> getAllFiles() {
        return DBFileRepository.findAll().stream();
    }

    public boolean checkIfFileExists(String id){
        return DBFileRepository.existsById(id);

    }

    public void delete(String filename){
        DBFileRepository.deleteById(filename);
    }
}
