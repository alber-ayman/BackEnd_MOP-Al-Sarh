package com.example.demo.controllers;

import com.example.demo.models.FileDB;
import com.example.demo.models.JobOrder;
import com.example.demo.models.Pand;
import com.example.demo.models.ResponseFile;
import com.example.demo.payload.excel.message.ResponseMessage;
import com.example.demo.repository.JobOrderRepository;
import com.example.demo.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://192.168.1.249:4200")
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private FileStorageService storageService;

    @Autowired
    private JobOrderRepository jobOrderRepository;

    @PostMapping("/upload/{id}/{flag}")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable String id, @PathVariable int flag) {
        String message = "";
        try {
            Long idValue = Long.valueOf(id);
            FileDB fileDB = storageService.store(file, idValue, flag);

            if(fileDB == null){
                message = "يوجد ملف مرفق للأمر شغل رقم  " + id;
                ResponseMessage responseMessage = new ResponseMessage(message, "0");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMessage);
            }
            Optional<JobOrder> jobOrder = jobOrderRepository.findById(Long.valueOf(id));
            jobOrder.get().setFileId(fileDB.getId());
            jobOrderRepository.save(jobOrder.get());
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            ResponseMessage responseMessage = new ResponseMessage(message, fileDB.getId());
            return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message, null));
        }
    }

    @GetMapping("/allFiles")
    public ResponseEntity<List<ResponseFile>> getListFiles() {
        try {

            List<ResponseFile> files = storageService.getAllFiles().map(dbFile -> {
                String fileDownloadUri = ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path("/api/file/fileById/")
                        .path(dbFile.getId())
                        .toUriString();

                return new ResponseFile(
                        dbFile.getName(),
                        fileDownloadUri,
                        dbFile.getType(),
                        dbFile.getData().length);
            }).collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK).body(files);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
        }
    }

    @GetMapping("/fileById/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable String id) {
        String message = "";
        try {
            FileDB fileDB = storageService.getFile(id);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
                    .body(fileDB.getData());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
        }
    }

    @GetMapping("/fileByJobOrderId/{id}")
    public ResponseEntity<String> fileByJobOrderId(@PathVariable Long id) {
        try {
            String fileDownloadUri = storageService.getFileByJobOrder(id);
            return ResponseEntity.status(HttpStatus.OK).body(fileDownloadUri);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteFileByJobOrderId(@PathVariable(value = "id") Long id) throws ResourceNotFoundException, SQLException {
        try {
            storageService.deleteFile(id);
            Optional<JobOrder> jobOrder = jobOrderRepository.findById(id);
            jobOrder.get().setFileId(null);
            jobOrder.get().setFileDB(null);
            jobOrderRepository.save(jobOrder.get());
            return new ResponseEntity<>("Deleted Successfully", HttpStatus.OK);


        } catch (Exception e) {
            return new ResponseEntity<>("Exception", HttpStatus.BAD_REQUEST);
        }
    }
}

