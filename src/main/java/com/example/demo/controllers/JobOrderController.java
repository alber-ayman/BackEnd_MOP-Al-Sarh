package com.example.demo.controllers;

import com.example.demo.DTO.WorkOrderReport;
import com.example.demo.models.FileDB;
import com.example.demo.models.JobOrder;
import com.example.demo.payload.CheckLimitResponse;
import com.example.demo.repository.FileDBRepository;
import com.example.demo.repository.JobOrderRepository;
import com.example.demo.service.JobOrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
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

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://192.168.1.249:4200")
@RestController
@RequestMapping("/api/jobOrder")
public class JobOrderController {

    @Autowired
    JobOrderService jobOrderService;

    @Autowired
    JobOrderRepository jobOrderRepository;

    @Autowired
    private FileDBRepository fileDBRepository;

    @GetMapping("/all")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<JobOrder>> getAllJobOrders() throws SQLException {
        try {
            return jobOrderService.getAllJobOrders();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/byId/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('Viewer') or hasRole('ADMIN')")
    public ResponseEntity<JobOrder> getJobOrdersById(@PathVariable("id") Long id) throws ResourceNotFoundException, SQLException {
        try {
            JobOrder jobOrder = jobOrderService.getJobOrderById(id);

            return new ResponseEntity<>(jobOrder, HttpStatus.OK);
        } catch (Exception e) {

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/byProjectId/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('Viewer') or hasRole('ADMIN')")
    public ResponseEntity<List<JobOrder>> getJobOrdersByProjectId(@PathVariable("id") Long id) throws ResourceNotFoundException, SQLException {
        try {
            List<JobOrder> jobOrder = jobOrderService.getByProjectId(id);

            return new ResponseEntity<>(jobOrder, HttpStatus.OK);
        } catch (Exception e) {

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @GetMapping("/byCode/{pandCode}")
//    @PreAuthorize("hasRole('USER') or hasRole('Viewer') or hasRole('ADMIN')")
//    public ResponseEntity<List<Pand>> getPandByPandCode(@PathVariable("pandCode") String pandCode) throws ResourceNotFoundException, SQLException {
//        try {
//            List<Pand> pands = jobOrderService.getPandByPandCode(pandCode);
//
//            return new ResponseEntity<>(pands, HttpStatus.OK);
//        } catch (Exception e) {
//
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @Transactional
    @PostMapping("/save")  // Creating New JobOrders
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<JobOrder> saveJobOrders(
            @RequestBody JobOrder jobOrder
            , HttpServletRequest request) throws SQLException {
        try {
            return jobOrderService.addNewJobORder(jobOrder,request);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(jobOrder, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<JobOrder> updateJobOrders(@PathVariable(value = "id") Long id, @RequestBody JobOrder jobOrder, HttpServletRequest request) throws ResourceNotFoundException, SQLException {
        try {
            return jobOrderService.updateJobOrder(id, jobOrder, 0,request);
        } catch (Exception e) {
            return new ResponseEntity<>(jobOrder, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/updateQuantity/{id}/{flag}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<JobOrder> updateQuantity(@PathVariable(value = "id") Long id, @RequestBody JobOrder jobOrder, @PathVariable(value = "flag") int flag, HttpServletRequest request) throws ResourceNotFoundException, SQLException {
        try {
            return jobOrderService.updateJobOrder(id, jobOrder, flag,request);
        } catch (Exception e) {
            return new ResponseEntity<>(jobOrder, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteJobOrders(@PathVariable(value = "id") Long id, HttpServletRequest request) throws ResourceNotFoundException, SQLException {
        try {
            jobOrderService.deleteJobOrder(id,request);
            return new ResponseEntity<>("Deleted Successfully", HttpStatus.OK);


        } catch (Exception e) {
            return new ResponseEntity<>("Exception", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/upload/{id}")
    public ResponseEntity<CheckLimitResponse> handleFileUpload(@RequestParam("file") MultipartFile file, @PathVariable(value = "id") Long id) {
        try {
            FileDB fileEntity = new FileDB();
            fileEntity.setName(file.getOriginalFilename());
            fileEntity.setData(file.getBytes());
            fileEntity.setJobOrderId(id);

            fileDBRepository.save(fileEntity);
            String message = "File uploaded successfully with ID: " + fileEntity.getId();
            Optional<JobOrder> jobOrder = jobOrderRepository.findById(id);
            jobOrder.get().setFileId(fileEntity.getId());
            jobOrderRepository.save(jobOrder.get());
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

    @Transactional
    @PostMapping("/copy")  // Creating New JobOrders
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<JobOrder> copyJobOrders(
            @RequestParam(name = "jobOrderNumber") String jobOrder) throws SQLException {
        JobOrder copiedJobOrder = new JobOrder();
        try {
            copiedJobOrder = jobOrderService.copyJobORder(jobOrder);
            return new ResponseEntity<>(copiedJobOrder,HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(copiedJobOrder, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/jobOrderDetails/{id}")
    public  ResponseEntity<InputStreamResource> getJobOrderDetails(HttpServletResponse response, @PathVariable(value = "id") Long id
            ){
        try {
            InputStreamResource pdfBytes = jobOrderService.getJobOrderDetails(id);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=converted.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/jobOrderSearch/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<JobOrder> searchJobOrders(
            @RequestParam(name = "jobOrderNumber") String jobOrder, @PathVariable (name = "id") Long id) throws SQLException {
        JobOrder copiedJobOrder = new JobOrder();
        try {
            copiedJobOrder = jobOrderService.jobOrderSearch(jobOrder, id);
            return new ResponseEntity<>(copiedJobOrder,HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(copiedJobOrder, HttpStatus.BAD_REQUEST);
        }
    }

//    @GetMapping("/commitWorkOrder/{id}")
//    public String commitWorkOrderById(@PathVariable(value = "id") Long id){
//        try {
//            jobOrderService.commitWorkOrderById(id);
//            return "commited";
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    @GetMapping("/commitWorkOrderByProject/{id}")
//    public String commitWorkOrderById(@PathVariable(value = "id") String id){
//        try {
//            jobOrderService.commitWorkOrderByProject(id);
//            return "commited";
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    @GetMapping("/jobOrder")
    @PreAuthorize("hasRole('USER') or hasRole('Viewer') or hasRole('ADMIN')")
    public ResponseEntity<JobOrder> getByJobOrderNumber(@RequestParam(name = "jobOrderNumber") String id) throws ResourceNotFoundException, SQLException {
        try {
            JobOrder jobOrder = jobOrderRepository.getByJobOrderNumber(id);

            return new ResponseEntity<>(jobOrder, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/approveWorkOrder")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<JobOrder> approveWorkOrder(
            @RequestParam(value = "jobOrderNumber") String id) throws SQLException {
        try {
            return new ResponseEntity<>(jobOrderService.approveWorkOrder(id),HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/generateWorkOrderReport")
    public  ResponseEntity<InputStreamResource> generateWorkOrderReport(HttpServletResponse response,@RequestBody WorkOrderReport workOrderReport
    ){
        try {
            InputStreamResource pdfBytes = jobOrderService.getWorkOrderReport(workOrderReport);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=converted.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
