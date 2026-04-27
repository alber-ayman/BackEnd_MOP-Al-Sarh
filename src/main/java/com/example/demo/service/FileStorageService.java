package com.example.demo.service;

import java.io.IOException;
import java.util.stream.Stream;

import com.example.demo.models.FileDB;
import com.example.demo.repository.FileDBRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@Service
public class FileStorageService {
    @Autowired
    private FileDBRepository fileDBRepository;

    public FileDB store(MultipartFile file,Long id,int flag) throws IOException, java.io.IOException {
        if (flag == 1) {
            FileDB dbFile = fileDBRepository.findByJobOrderId(id);
            if (dbFile == null) {
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                FileDB fileDB = new FileDB(fileName, file.getContentType(), file.getBytes(), id,0L);
                fileDB.setJobOrderId(id);
                return fileDBRepository.save(fileDB);
            } else {
                return null;
            }
        }
            else{
            FileDB dbFile = fileDBRepository.findByPandId(id);
            if (dbFile == null) {
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                FileDB fileDB = new FileDB(fileName, file.getContentType(), file.getBytes(),0L, id);
                fileDB.setPandId(id);
                return fileDBRepository.save(fileDB);
            } else {
                return null;
            }
        }


    }

    public FileDB getFile(String id) {
        return fileDBRepository.findById(id).get();
    }

    public String getFileByJobOrder(Long id) {

        FileDB dbFile = fileDBRepository.findByJobOrderId(id);
        if(dbFile == null){
            return "0";
        }

        String fileDownloadUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/file/fileById/")
                .path(dbFile.getId())
                .toUriString();

        return fileDownloadUri;
    }

    public Stream<FileDB> getAllFiles() {
        return fileDBRepository.findAll().stream();
    }

    public void deleteFile(Long id) {
        FileDB dbFile = fileDBRepository.findByJobOrderId(id);
        fileDBRepository.deleteById(dbFile.getId());

    }

    public String getFileByPandId(Long id) {
        FileDB dbFile = fileDBRepository.findByPandId(id);
        if(dbFile == null){
            return "0";
        }

        String fileDownloadUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/file/fileById/")
                .path(dbFile.getId())
                .toUriString();

        return fileDownloadUri;
    }

    public void deleteFileByPand(Long id) {
        FileDB dbFile = fileDBRepository.findByPandId(id);
        fileDBRepository.deleteById(dbFile.getId());
    }
}
