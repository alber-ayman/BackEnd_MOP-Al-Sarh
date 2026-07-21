package com.example.demo.controllers;

import com.example.demo.DTO.MarbleItemDto;
import com.example.demo.models.*;
import com.example.demo.payload.CheckLimitResponse;
import com.example.demo.service.ExcelFileService;
import com.example.demo.service.FileService;
import com.example.demo.service.PandsToJobOrderService;
import com.example.demo.service.PreviewJobOrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@CrossOrigin(origins = "http://192.168.1.249:4200")
@RestController
@RequestMapping("/api/pandsToJobOrder")
public class pandsToJobOrderController {

    @Autowired
    PandsToJobOrderService pandsToJobOrderService;


    @Autowired
    ExcelFileService excelFileService;

    @Autowired
    PreviewJobOrderService previewJobOrderService;


    @GetMapping("/all/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('Viewer') or hasRole('ADMIN')")
    public ResponseEntity<PandsToJobOrder> getByJobOrder(@PathVariable("id") Long id) throws ResourceNotFoundException, SQLException {
        try {
            PandsToJobOrder pandsToJobOrders = pandsToJobOrderService.getByJobOrderId(id);

            return new ResponseEntity<>(pandsToJobOrders, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/jobOrder")
    @PreAuthorize("hasRole('USER') or hasRole('Viewer') or hasRole('ADMIN')")
    public ResponseEntity<List<PandsToJobOrder>> getByJobOrder(@RequestParam(name = "jobOrderNumber") String id) throws ResourceNotFoundException, SQLException {
        try {
            List<PandsToJobOrder> pandsToJobOrders = pandsToJobOrderService.getByJobOrderId(id);
//            JobOrderParent jobOrderParent = new JobOrderParent();
//            jobOrderParent.getPandsToJobOrderList().addAll(pandsToJobOrders);

            return new ResponseEntity<>(pandsToJobOrders, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/jobOrder/noZeros")
    @PreAuthorize("hasRole('USER') or hasRole('Viewer') or hasRole('ADMIN')")
    public ResponseEntity<List<PandsToJobOrder>> getByJobOrderWzNoZeros(@RequestBody UnifiedSerial unifiedSerial) throws ResourceNotFoundException, SQLException {
        try {
            List<PandsToJobOrder> pandsToJobOrders = pandsToJobOrderService.getByJobOrderIdWzNoZeros(unifiedSerial);
//            JobOrderParent jobOrderParent = new JobOrderParent();
//            jobOrderParent.getPandsToJobOrderList().addAll(pandsToJobOrders);

            return new ResponseEntity<>(pandsToJobOrders, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/projectId/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('Viewer') or hasRole('ADMIN')")
    public ResponseEntity<List<PandsToJobOrder>> getByProjectId(@PathVariable("id") Long id) throws ResourceNotFoundException, SQLException {
        try {
            System.out.println("innnnnnnnnnnn");
            List <PandsToJobOrder> pandsToJobOrders = pandsToJobOrderService.getByProjectId(id);

            return new ResponseEntity<>(pandsToJobOrders, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/jobOrderAndPandId/{id}/{pandId}")
    @PreAuthorize("hasRole('USER') or hasRole('Viewer') or hasRole('ADMIN')")
    public ResponseEntity<PandsToJobOrder> getByjobOrderAndPandId(@PathVariable("id") String id,@PathVariable("pandId") String pandId) throws ResourceNotFoundException, SQLException {
        try {
            System.out.println("innnnnnnnnnnn");
            PandsToJobOrder pandsToJobOrders = pandsToJobOrderService.getByjobOrderAndPandId(id,pandId);

            return new ResponseEntity<>(pandsToJobOrders, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/save/{flag}")  // Creating Project profile
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PandsToJobOrder> saveChildPand(
            @RequestBody PandsToJobOrder pandsToJobOrder, @PathVariable(value = "flag") int flag, HttpServletRequest request) throws SQLException {
        try {
            PandsToJobOrder jobOrder= pandsToJobOrderService.saveChildPand(pandsToJobOrder,flag,request);
            if(jobOrder.getFlag() == 1){
                return new ResponseEntity<>(jobOrder, HttpStatus.OK);
            }
            return new ResponseEntity<>(jobOrder, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(pandsToJobOrder, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/saveToPreview")  // Creating Project profile
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PreviewJobOrder> saveToPreview(
            @RequestBody PreviewJobOrder pandsToJobOrder) throws SQLException {
        try {
            PreviewJobOrder jobOrder= previewJobOrderService.savePreviewPandToJobOrder(pandsToJobOrder);
            if(jobOrder.getFlag() == 1){
                return new ResponseEntity<>(jobOrder, HttpStatus.OK);
            }
            return new ResponseEntity<>(jobOrder, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(pandsToJobOrder, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/preview/{flag}")
    public ResponseEntity<InputStreamResource> previewJobOrder(
            @RequestParam(value = "id") String projectId,
            @PathVariable(value = "flag") int flag
    ) throws Exception {
        // 1. Create an Excel workbook in memory
        InputStreamResource pdfBytes = pandsToJobOrderService.previewJobOrder(projectId, flag);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=converted.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @PutMapping("/update/{id}/{flag}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PandsToJobOrder> updateJobOrders(@PathVariable(value = "id") Long id, @RequestBody PandsToJobOrder jobOrder,@PathVariable(value = "flag") int flag,HttpServletRequest request) throws ResourceNotFoundException, SQLException {
        try {
            return pandsToJobOrderService.updateJobOrder(id,jobOrder,request);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(jobOrder, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PandsToJobOrder> updateDelete(@PathVariable(value = "id") Long id, HttpServletRequest request) throws ResourceNotFoundException, SQLException {
        try {

            return pandsToJobOrderService.deletePandToJobOrder(id,request);


        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>( null , HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/return/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CheckLimitResponse> returnJobOrders(@PathVariable(value = "id") String id,@RequestBody PandsToJobOrder returnReason) throws ResourceNotFoundException, SQLException {
        try {
            System.out.println("returnJobORder: " + returnReason.getMessage());
            CheckLimitResponse checkLimitResponse = pandsToJobOrderService.returnJobOrder(id,returnReason);
//            exitJobOrderService.checkQuantity();

            return new ResponseEntity<>(checkLimitResponse, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            CheckLimitResponse checkLimitResponse = new CheckLimitResponse();
            checkLimitResponse.setFlag(1);
            checkLimitResponse.setMessage("returned failed");
            return new ResponseEntity<>(checkLimitResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/generate/jobOrdersFile")
    public ResponseEntity<InputStreamResource> generateExcelToPdf(@RequestParam(name = "jobOrderNumber") String id) throws Exception {
        // 1. Create an Excel workbook in memory
        InputStreamResource pdfBytes = pandsToJobOrderService.getPdf(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=converted.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }


//    @GetMapping("/generate/excelFile")
//    public ResponseEntity<InputStreamResource> downLoadExcel(HttpServletResponse response,
//                              @RequestParam(name = "jobOrderNumber") String id) throws SQLException, IOException {
//        try {
//            String filename = "";
//            ByteArrayInputStream inputStream = null;
//                filename = "/rest quantity" + ".xls";
//                inputStream = excelFileService.buildPandsToJobOrderExcel(id);
//
//            response.setContentType("application/octet-stream");
//            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
//            IOUtils.copy(inputStream, response.getOutputStream());
//        } catch (Exception e) {
//        }
//    }

    @PostMapping("/generate/excelFile")
    @PreAuthorize("hasRole('USER') or hasRole('Viewer') or hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource> exitJobOrder(HttpServletResponse response,
                                                            @RequestParam(name = "jobOrderNumber") String id) throws ResourceNotFoundException, SQLException {
        try {
            System.out.println("innnnnnn");
            ByteArrayInputStream excelFile = excelFileService.buildPandsToJobOrderExcel(id);
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

    @PostMapping("/savePandToJobOrdersList")
    public ResponseEntity<?> save(@RequestBody List<MarbleItemDto> items, HttpServletRequest request) throws SQLException {
        List<PandsToJobOrder> pandsToJobOrders = pandsToJobOrderService.saveListJobOrderPands(items, request);
        return ResponseEntity.ok(pandsToJobOrders);
    }


}
