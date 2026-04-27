package com.example.demo.controllers;

import com.example.demo.models.FileDB;
import com.example.demo.models.Pand;
import com.example.demo.payload.CheckLimitResponse;
import com.example.demo.repository.FileDBRepository;
import com.example.demo.repository.PandsRepository;
import com.example.demo.service.FileStorageService;
import com.example.demo.service.PandsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://192.168.1.249:4200")
@RequestMapping("/api/pands")
public class PandController {

    @Autowired
    PandsService pandsService;

    @Autowired
    PandsRepository pandsRepository;

    @Autowired
    private FileDBRepository fileDBRepository;

    @Autowired
    private FileStorageService storageService;

    @GetMapping("/all/{projectId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<Pand>> getAllPands(@PathVariable(value = "projectId") Long id) throws SQLException {
        try {
            return pandsService.getAllPands(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/byId/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('Viewer') or hasRole('ADMIN')")
    public ResponseEntity<Pand> getPandById(@PathVariable("id") Long id) throws ResourceNotFoundException, SQLException {
        try {
            Optional<Pand> pand = pandsService.getPandById(id);

            return new ResponseEntity<>(pand.get(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/checkLimit")
    @PreAuthorize("hasRole('USER') or hasRole('Viewer') or hasRole('ADMIN')")
    public ResponseEntity<CheckLimitResponse> checkLimit() throws ResourceNotFoundException, SQLException {
        try {
            CheckLimitResponse pandLimit = pandsService.checkLimit();

            return new ResponseEntity<>(pandLimit, HttpStatus.OK);
        } catch (Exception e) {

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/projectId/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('Viewer') or hasRole('ADMIN')")
    public ResponseEntity<List<Pand>> getPandsByProjectId(@PathVariable("id") Long id) throws ResourceNotFoundException, SQLException {
        try {
            List<Pand> pand = pandsService.getPandByProjectId(id);

            return new ResponseEntity<>(pand, HttpStatus.OK);
        } catch (Exception e) {

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/byCode/{pandCode}/{projectId}")
    @PreAuthorize("hasRole('USER') or hasRole('Viewer') or hasRole('ADMIN')")
    public ResponseEntity<Pand> getPandByPandCode(@PathVariable("pandCode") String pandCode, @PathVariable("projectId") Long projectId) throws ResourceNotFoundException, SQLException {
        try {
            Pand pands = pandsService.getPandByPandCode(pandCode, projectId);

            return new ResponseEntity<>(pands, HttpStatus.OK);
        } catch (Exception e) {

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")  // Creating New Pand
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Pand> savePand(
            @RequestBody Pand pand,
            HttpServletRequest request) throws SQLException {
        try {
            return ResponseEntity.ok(pandsService.addNewPand(pand,request));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(pand, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Pand> updatePand(@PathVariable(value = "id") Long id, @RequestBody Pand pand, HttpServletRequest request) throws ResourceNotFoundException, SQLException {
        try {
            return ResponseEntity.ok(pandsService.updatePand(id, pand,request));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(pand, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateDelete(@PathVariable(value = "id") Long id) throws ResourceNotFoundException, SQLException {
        try {
            pandsService.deletePand(id);
            return new ResponseEntity<>("Deleted Successfully", HttpStatus.OK);


        } catch (Exception e) {
            return new ResponseEntity<>("Exception", HttpStatus.BAD_REQUEST);
        }
    }

//    @GetMapping("/download/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public void downLoadExcel(HttpServletResponse response,
//                              @PathVariable(value = "id") Long id) throws SQLException, IOException {
//        try {
//            String filename = "";
//            ByteArrayInputStream inputStream = null;
//            filename = "/الكميات المتبقية فى البنود"  + ".xls";
//            inputStream = pandsService.buildFile(id);
//            response.setContentType("application/octet-stream");
//            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
//            IOUtils.copy(inputStream, response.getOutputStream());
//        } catch (Exception e) {
//        }
//    }

    @PostMapping("/upload/{id}")
    public ResponseEntity<CheckLimitResponse> handleFileUpload(@RequestParam("file") MultipartFile file, @PathVariable(value = "id") Long id) {
        try {
            FileDB fileEntity = new FileDB();
            fileEntity.setName(file.getOriginalFilename());
            fileEntity.setData(file.getBytes());
            fileEntity.setPandId(id);

            fileDBRepository.save(fileEntity);
            String message = "File uploaded successfully with ID: " + fileEntity.getId();
            Optional<Pand> pand = pandsService.getPandById(id);
            pand.get().setFileId(fileEntity.getId());
            pandsRepository.save(pand.get());
            int flag = 1;
            CheckLimitResponse messageResponse = new CheckLimitResponse(message, flag);

            return new ResponseEntity<>(messageResponse, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            String message = "File upload failed";
            int flag = 0;
            CheckLimitResponse messageResponse = new CheckLimitResponse(message, flag);
            return new ResponseEntity<>(messageResponse, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/downloadFolder/{pandId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable(value = "pandId") Long fileId) {
        try {
            FileDB fileEntity = fileDBRepository.findByPandId(fileId);
            if (fileEntity == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileEntity.getName() + "\"")
                    .body(fileEntity.getData());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @DeleteMapping("/deleteFile/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteFileByJobOrderId(@PathVariable(value = "id") Long id) throws ResourceNotFoundException, SQLException {
        try {
            storageService.deleteFileByPand(id);
            Optional<Pand> pand = pandsService.getPandById(id);
            pand.get().setFileId(null);
            pand.get().setFileDB(null);
            pandsRepository.save(pand.get());
            return new ResponseEntity<>("Deleted Successfully", HttpStatus.OK);


        } catch (Exception e) {
            return new ResponseEntity<>("Exception", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/generate/PDFFile/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource> generateExcelToPdf(HttpServletResponse response, @PathVariable(value = "id") Long id) throws Exception {
        try {
            InputStreamResource pdfBytes = pandsService.getPdf(id);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=converted.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/pandDetails/{pandCode}/{projectId}")
    public ResponseEntity<InputStreamResource> getPandDetailsForEachJobOrder(HttpServletResponse response, @PathVariable(value = "pandCode") String id
            , @PathVariable(value = "projectId") Long projectId) {
        try {
            InputStreamResource pdfBytes = pandsService.getPandDetailsForEachJobOrder(id, projectId);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=converted.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/uploadImage/{id}")
    public ResponseEntity<String> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("description") String description,
            @PathVariable("id") Long id) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file selected");
        }

        try {
            Optional<Pand> pand = pandsRepository.findById(id);
            pand.get().setImageDescription(description);
            pand.get().setImage(file.getBytes());

            pandsRepository.save(pand.get());
            return ResponseEntity.ok("Image saved to DB successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving image: " + e.getMessage());
        }
    }

    @GetMapping("/pandImage/{pandCode}/{projectId}")
    public ResponseEntity<InputStreamResource> getPandDetailsWithImage(HttpServletResponse response, @PathVariable(value = "pandCode") Long id
            , @PathVariable(value = "projectId") Long projectId) {
        try {
            InputStreamResource pdfBytes = pandsService.getPandImage(id, projectId);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=converted.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/pandImage/delete/{pandCode}")
    public ResponseEntity<String> deletePandImage(HttpServletResponse response, @PathVariable(value = "pandCode") Long id
            ) {
        try {
            String message = pandsService.deletePandImage(id);

            return ResponseEntity.ok(message);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
