package com.example.demo.controllers;

import com.example.demo.models.ExitJobOrder;
import com.example.demo.models.JobOrder;
import com.example.demo.models.JobOrderParent;
import com.example.demo.models.PandsToJobOrder;
import com.example.demo.payload.CheckLimitResponse;
import com.example.demo.service.*;
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.layout.Document;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.IOUtils;
import org.apache.poi.wp.usermodel.Paragraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.*;
import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://192.168.1.249:4200")
@RestController
@RequestMapping("/api/exitJobOrder")
public class ExitJobOrderController {

    @Autowired
    ExitJobOrderService exitJobOrderService;

    @Autowired
    FileService fileService;

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
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/all")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<ExitJobOrder>> getAllExitJobOrdersByJobORder(@RequestParam(value = "jobOrderId") String jobORderId) throws SQLException {
        try {
            return exitJobOrderService.getAllExitJobOrders(jobORderId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @GetMapping("/all/byId/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<ExitJobOrder> getExitJobOrderById(@PathVariable(value = "id") Long id) throws SQLException {
        try {
            return exitJobOrderService.getExitById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save/{serial}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<JobOrderParent> saveChildPand(
            @PathVariable("serial") String serial,
            @RequestBody JobOrderParent jobOrderParent) throws SQLException {
        try {
            return exitJobOrderService.saveChildPand(jobOrderParent,serial);
        } catch (Exception e) {
            return new ResponseEntity<>(jobOrderParent, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")  // Creating Project profile
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExitJobOrder> updateChildPand(
            @RequestBody ExitJobOrder exitJobOrder) throws SQLException {
        try {
            return exitJobOrderService.updateChildPand(exitJobOrder);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(exitJobOrder, HttpStatus.BAD_REQUEST);
        }
    }


//    @PostMapping("/exitFileJobOrder")
//    @PreAuthorize("hasRole('USER') or hasRole('Viewer') or hasRole('ADMIN')")
//    public ResponseEntity<InputStreamResource> exitJobOrder(HttpServletResponse response, @RequestBody JobOrderParent jobOrderParent) throws ResourceNotFoundException, SQLException {
//        try {
//            InputStreamResource pdfBytes = fileService.getLastJobOrder(jobOrderParent);
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("Content-Disposition", "attachment; filename=data.xlsx");
//            headers.setContentDispositionFormData("inline", "sample.pdf");
//
//
////            byte[] pdfBytes = excelFile.toByteArray();
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=generated.pdf")
//                    .contentType(MediaType.APPLICATION_PDF)
//                    .body(pdfBytes);
//        } catch (Exception e) {
//            return null;
//        }
//    }

    //اذن خروج الانتاج التام
    @PostMapping("/generate/excel-to-pdf")
    public ResponseEntity<InputStreamResource> generateExcelToPdf(@RequestBody JobOrderParent jobOrderParent ,HttpServletRequest request) throws IOException {
        // 1. Create an Excel workbook in memory
        try {
            InputStreamResource pdfBytes = pdfFileService.processJobs(jobOrderParent,request);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=converted.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/generate/pdfBySerial")
    public ResponseEntity<InputStreamResource> generatePdfBySerial(@RequestParam(value = "serial") String serialNumber) throws IOException {
        // 1. Create an Excel workbook in memory
        try {
            InputStreamResource pdfBytes = pdfFileService.getPdfBySerial(serialNumber);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=converted.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @PostMapping("/return")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CheckLimitResponse> returnJobOrders(@RequestBody JobOrderParent jobOrderParent) throws ResourceNotFoundException, SQLException {
        try {

            CheckLimitResponse checkLimitResponse = exitJobOrderService.returnJobOrder(jobOrderParent);
            exitJobOrderService.checkQuantity();

            return new ResponseEntity<>(checkLimitResponse, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            CheckLimitResponse checkLimitResponse = new CheckLimitResponse();
            checkLimitResponse.setFlag(1);
            checkLimitResponse.setMessage("returned failed");
            return new ResponseEntity<>(checkLimitResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteJobOrders(@PathVariable(value = "id") Long id) throws ResourceNotFoundException, SQLException {
        try {
            exitJobOrderService.deleteJobOrder(id);
            return new ResponseEntity<>("Deleted Successfully", HttpStatus.OK);


        } catch (Exception e) {
            return new ResponseEntity<>("Exception", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/download")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> downLoadExcel(HttpServletResponse response,
                                                @RequestParam(value = "jobOrderNumber") String id) throws SQLException, IOException {
        try {
            String filename = "";
            ByteArrayOutputStream inputStream = null;
            filename = "/exitJobOrders" + ".xls";
            inputStream = exitJobOrderService.buildFile(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sample-data.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(inputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
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
    public ResponseEntity<List<ExitJobOrder>> getAllExitJobOrdersBySerials(@RequestParam(value = "jobOrderId") String jobORderId) throws SQLException {
        try {
            return exitJobOrderService.getAllExitJobOrdersBySerial(jobORderId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all/bySerial")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<ExitJobOrder>> getAllExitJobOrdersBySerial(@RequestParam(value = "serial") String serialNumber) throws SQLException {
        try {
            return exitJobOrderService.getAllExitJobOrdersBySpacificSerial(serialNumber);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/generate/excelFile")
    @PreAuthorize("hasRole('USER') or hasRole('Viewer') or hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource> exitJobOrder(HttpServletResponse response,
                                                            @RequestParam(name = "serial") String id) throws ResourceNotFoundException, SQLException {
        try {
            System.out.println("innnnnnn");
            ByteArrayInputStream excelFile = excelFileService.buildExcelExitJobOrderBySerial(id);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=data.xlsx");
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(excelFile));
        } catch (Exception e) {
            return null;
        }
    }

    @DeleteMapping("/delete/serialNumber")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAllBySerialNumber(@RequestParam(value = "serial") String serialNumber) throws ResourceNotFoundException, SQLException {
        try {
            exitJobOrderService.deleteJobOrderBySerialNumber(serialNumber);
            return new ResponseEntity<>("Deleted Successfully", HttpStatus.OK);


        } catch (Exception e) {
            return new ResponseEntity<>("Exception", HttpStatus.BAD_REQUEST);
        }
    }

}
