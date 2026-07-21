package com.example.demo.controllers;

import com.example.demo.models.ExitJobOrder;
import com.example.demo.models.JobOrderParent;
import com.example.demo.payload.CheckLimitResponse;
import com.example.demo.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.print.*;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

@CrossOrigin(origins = "http://192.168.1.249:4200")
@RestController
@RequestMapping("/api/exitJobOrder")
@Slf4j
public class ExitJobOrderController {

    @Autowired
    ExitJobOrderService exitJobOrderService;

    @Autowired
    ExcelFileService excelFileService;

    @Autowired
    PdfFileService pdfFileService;


    @GetMapping("/getAll")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<ExitJobOrder>> getAllExitJobOrders() throws SQLException {
        try {
            return exitJobOrderService.getAll();
        } catch (Exception e) {
            log.error("Business error getAllExitJobOrders: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/all")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<ExitJobOrder>> getAllExitJobOrdersByJobOrder(@RequestParam(value = "jobOrderId") String jobOrderId) throws SQLException {
        try {
            return exitJobOrderService.getAllExitJobOrders(jobOrderId);
        } catch (Exception e) {
            log.error("Business error getAllExitJobOrdersByJobORder: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @GetMapping("/all/byId/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<ExitJobOrder> getExitJobOrderById(@PathVariable(value = "id") Long id) throws SQLException {
        try {
            return exitJobOrderService.getExitById(id);
        } catch (Exception e) {
            log.error("Business error getExitJobOrderById: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save/{serial}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<JobOrderParent> saveChildBand(
            @PathVariable("serial") String serial,
            @RequestBody JobOrderParent jobOrderParent) {
        try {
            return exitJobOrderService.saveChildPand(jobOrderParent,serial);
        } catch (Exception e) {
            log.error("Business error saveExit: {}", e.getMessage());
            return new ResponseEntity<>(jobOrderParent, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")  // Creating Project profile
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExitJobOrder> updateChildBand(
            @PathVariable(value = "id")  Long exitJobOrderId,
            @RequestBody ExitJobOrder exitJobOrder) {
        try {
            return exitJobOrderService.updateChildPand(exitJobOrderId,exitJobOrder);
        } catch (Exception e) {
            log.error("Business error updateExit: {}", e.getMessage());
            return new ResponseEntity<>(exitJobOrder, HttpStatus.BAD_REQUEST);
        }
    }

    //اذن خروج الانتاج التام
    @PostMapping("/generate/excel-to-pdf")
    public ResponseEntity<InputStreamResource> generateExcelToPdf(@RequestBody JobOrderParent jobOrderParent ,HttpServletRequest request) {
        // 1. Create an Excel workbook in memory
        try {
            InputStreamResource pdfBytes = pdfFileService.processJobs(jobOrderParent,request);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=converted.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("Business error generateExcelToPdf: {}", e.getMessage());
            return null;
        }
    }

    @PostMapping("/generate/pdfBySerial")
    public ResponseEntity<InputStreamResource> generatePdfBySerial(@RequestParam(value = "serial") String serialNumber)  {
        // 1. Create an Excel workbook in memory
        try {
            InputStreamResource pdfBytes = pdfFileService.getPdfBySerial(serialNumber);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=converted.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("Business error generatePdfBySerial: {}", e.getMessage());
            return null;
        }

    }

    @PostMapping("/return")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CheckLimitResponse> returnJobOrders(@RequestBody JobOrderParent jobOrderParent) throws ResourceNotFoundException {
        try {

            CheckLimitResponse checkLimitResponse = exitJobOrderService.returnJobOrder(jobOrderParent);
            exitJobOrderService.checkQuantity();

            return new ResponseEntity<>(checkLimitResponse, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Business error returnJobOrders: {}", e.getMessage());
            CheckLimitResponse checkLimitResponse = new CheckLimitResponse();
            checkLimitResponse.setFlag(1);
            checkLimitResponse.setMessage("returned failed");
            return new ResponseEntity<>(checkLimitResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteJobOrders(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        try {
            exitJobOrderService.deleteJobOrder(id);
            return new ResponseEntity<>("Deleted Successfully", HttpStatus.OK);


        } catch (Exception e) {
            log.error("Business error deleteJobOrders: {}", e.getMessage());
            return new ResponseEntity<>("Exception", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/download")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> downLoadExcel(
                                                @RequestParam(value = "jobOrderNumber") String id) {
        try {
            ByteArrayOutputStream inputStream = exitJobOrderService.buildFile(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sample-data.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(inputStream.toByteArray());
        } catch (Exception e) {
            log.error("Business error downLoadExcel: {}", e.getMessage());
            return null;
        }
    }

    @PostMapping("/generate/PDFFile")
    public ResponseEntity<InputStreamResource> generateExcelToPdf(@RequestParam(name = "jobOrderNumber") String id) throws Exception {
        // 1. Create an Excel workbook in memory
        InputStreamResource pdfBytes = pdfFileService.getPdf(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=converted.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }



    @PostMapping("/generate/rawsDetails")
    public ResponseEntity<InputStreamResource> generateRawsDetails(@RequestParam(name = "jobOrderNumber") String id) throws Exception {
        InputStreamResource pdfBytes = pdfFileService.getPdfV2(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=converted.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }



    @GetMapping("/all/serials")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<ExitJobOrder>> getAllExitJobOrdersBySerials(@RequestParam(value = "jobOrderId") String jobOrderId) throws SQLException {
        try {
            return exitJobOrderService.getAllExitJobOrdersBySerial(jobOrderId);
        } catch (Exception e) {
            log.error("Business error getAllExitJobOrdersBySerials: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all/serialsByProject/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<ExitJobOrder>> serialsByProject(@PathVariable(value = "id") Long projectId) throws SQLException {
        try {
            return exitJobOrderService.getAllserialsByProject(projectId);
        } catch (Exception e) {
            log.error("Business error serialsByProject: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all/bySerial")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<ExitJobOrder>> getAllExitJobOrdersBySerial(@RequestParam(value = "serial") String serialNumber) throws SQLException {
        try {
            return exitJobOrderService.getAllExitJobOrdersBySpacificSerial(serialNumber);
        } catch (Exception e) {
            log.error("Business error getAllExitJobOrdersBySerial: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/generate/excelFile")
    @PreAuthorize("hasRole('USER') or hasRole('Viewer') or hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource> exitJobOrder(
                                                            @RequestParam(name = "serial") String id) throws ResourceNotFoundException {
        try {

            ByteArrayInputStream excelFile = excelFileService.buildExcelExitJobOrderBySerial(id);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=data.xlsx");
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(excelFile));
        } catch (Exception e) {
            log.error("Business error exitJobOrder: {}", e.getMessage());
            return null;
        }
    }

    @DeleteMapping("/delete/serialNumber")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAllBySerialNumber(@RequestParam(value = "serial") String serialNumber) throws ResourceNotFoundException {
        try {
            exitJobOrderService.deleteJobOrderBySerialNumber(serialNumber);
            return new ResponseEntity<>("Deleted Successfully", HttpStatus.OK);


        } catch (Exception e) {
            log.error("Business error deleteAllBySerialNumber: {}", e.getMessage());
            return new ResponseEntity<>("Exception", HttpStatus.BAD_REQUEST);
        }
    }

}
