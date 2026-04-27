package com.example.demo.service;

import com.aspose.cells.*;
import com.example.demo.DTO.MarbleItemDto;
import com.example.demo.DTO.WorkOrderReport;
import com.example.demo.models.JobOrder;
import com.example.demo.models.Pand;
import com.example.demo.models.PandsToJobOrder;
import com.example.demo.models.RawTypes;
import com.example.demo.repository.*;
import com.example.demo.security.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Service
public class JobOrderService {

    @Autowired
    ChangeHistoryLog changeHistoryLog;

    @Autowired
    JobOrderRepository jobOrderRepository;

    @Autowired
    PandsToJobOrderRepository pandsToJobOrderRepository;

    @Autowired
    PandsRepository pandsRepository;

    @Autowired
    PandsService pandsService;

    @Autowired
    ExitJobOrderRepository exitJobOrderRepository;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private FileStorageService storageService;

//    private final AtomicLong counter = new AtomicLong();

//    @Cacheable(value = "JOBORDER_ALL")
    public ResponseEntity<List<JobOrder>> getAllJobOrders() {
        return new ResponseEntity<>(jobOrderRepository.findAll(), HttpStatus.OK);
    }
//    @Cacheable(value = "JOBORDER",key = "#id")
    public JobOrder getJobOrderById(Long id) {
        JobOrder jobOrder = jobOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("job order Not Found for ID: " + id));

        String fileurl = storageService.getFileByJobOrder(id);
        jobOrder.setFileDB(fileurl);

        return jobOrder;
    }
//    @Cacheable(value = "PROJECT_PROFILE",key = "#id")
    public List<JobOrder> getByProjectId(Long id) {
        try {
            return jobOrderRepository.findByProjectProfileIdOrderByIdDesc(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
//    @CachePut(value = "JOBORDER",key = "#result.id")
//    @CacheEvict(value = "PROJECT_PROFILE", allEntries = true)
    public ResponseEntity<JobOrder> addNewJobORder(JobOrder jobOrder, HttpServletRequest request) throws SQLException {

        GregorianCalendar gcalendar = new GregorianCalendar();
        Integer number = getTheMaxNumber(jobOrder.getProjectProfileId());
        String jobOrderNumber = number + 1 + "/" + gcalendar.get(Calendar.YEAR);
        JobOrder jobOrder1 = jobOrderRepository.getByJobOrderNumber(jobOrderNumber);
        jobOrder1.setNumber(number + 1);

        String username = jwtUtils.userName;

        jobOrder1.setCreatedBy(username);
        jobOrder1.setYear(gcalendar.get(Calendar.YEAR));


//        List<PandsToJobOrder> pandsToJobOrders = pandsToJobOrderRepository.getByJobOrderId(jobOrderNumber);
//        if (pandsToJobOrders.size() == 0) {
//            return new ResponseEntity<>(jobOrder, HttpStatus.BAD_REQUEST);
//        }

//        jobOrder.getPandsToJobOrders().addAll(pandsToJobOrders);

//        Date dNow = new Date();
//        SimpleDateFormat ft =
//                new SimpleDateFormat("hh:mm:ss a");

//        DateFormat formatter1 = new SimpleDateFormat("dd.MM.yy");
//        jobOrder.setProjectProfileId(pandsToJobOrders.get(0).getProjectProfileId());
//        jobOrder.setJobOrderDate(formatter1.format(dNow));
//        jobOrder.setJobOrderTime(c);
//        jobOrder.setJobOrderNumber(number + 1 + "/" + gcalendar.get(Calendar.YEAR));
//        jobOrder.setNumber(number + 1);
//        jobOrder.setProjectCode(pandsToJobOrders.get(0).getProjectCode());
//        jobOrder.setInstallementArea(pandsToJobOrders.get(0).getInstallationArea());

        jobOrderRepository.save(jobOrder1);
        changeHistoryLog.saveChange(jobOrder1.getJobOrderNumber(),jobOrder1.toString(),jobOrder1.toString(),"save",request);

//        DecimalFormat df = new DecimalFormat("#.###");

//        for (int i = 0; i < jobOrder.getPandsToJobOrders().size(); i++) {
//            Pand pand = pandsService.getPandByPandCode(jobOrder.getPandsToJobOrders().get(i).getPandCode(),jobOrder.getPandsToJobOrders().get(i).getProjectProfileId());
//            pand.setRestQuantity(Double.valueOf(df.format(pand.getRestQuantity() - Double.valueOf(pandsToJobOrders.get(i).getTotal()))));
//            pandsRepository.save(pand);
//            pandsToJobOrders.get(i).setQuantityInPand(pand.getRestQuantity());
//            pandsToJobOrders.get(i).setMainQuantity(pandsToJobOrders.get(i).getQuantity() * Double.valueOf(pandsToJobOrders.get(i).getRepetition()));
//            pandsToJobOrderRepository.save(pandsToJobOrders.get(i));
//        }
        return new ResponseEntity<>(jobOrder1, HttpStatus.OK);
    }

//    @CachePut(value = "JOBORDER",key = "#id")
//    @CacheEvict(value = "PROJECT_PROFILE", allEntries = true)
    public ResponseEntity<JobOrder> updateJobOrder(Long id, JobOrder updatedJobOrder, int flag, HttpServletRequest request) {

        JobOrder jobOrder = jobOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("rawType Not Found for ID: " + id));

        jobOrderRepository.save(jobOrder);

        changeHistoryLog.saveChange(updatedJobOrder.getJobOrderNumber(),updatedJobOrder.toString(),jobOrder.toString(),"update",request);

        return new ResponseEntity<>(jobOrder, HttpStatus.OK);
    }

//    @CacheEvict(value = "PROJECT_PROFILE", allEntries = true)
    public void deleteJobOrder(Long id, HttpServletRequest request) {
        try {
            JobOrder jobOrder = jobOrderRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("rawType Not Found for ID: " + id));

            returnInDelete(jobOrder,request);
            changeHistoryLog.saveChange(jobOrder.getJobOrderNumber(),jobOrder.toString(),jobOrder.toString(),"delete",request);

            jobOrderRepository.deleteById(id);
        } catch (Exception e) {
            System.out.printf("deleteJobOrder");
            e.printStackTrace();
        }
    }

    public void returnInDelete(JobOrder jobOrder,HttpServletRequest request) {
        try {
            DecimalFormat df = new DecimalFormat("#.###");
            List<PandsToJobOrder> pandsToJobOrders = pandsToJobOrderRepository.jobOrdersByJobOrderId(jobOrder.getProjectProfileId(), jobOrder.getJobOrderNumber());
            for (PandsToJobOrder item : pandsToJobOrders) {
                // First delete child entries
                pandsToJobOrderRepository.deleteFromMutuleTable(item.getId());

                // Then update the related Pand
                Pand pand = pandsService.getPandByPandCode(item.getPandCode(), item.getProjectProfileId());
                pand.setRestQuantity(Double.valueOf(df.format(pand.getRestQuantity() + Double.valueOf(item.getTotal()))));
                pandsRepository.save(pand);

                changeHistoryLog.saveChange(jobOrder.getJobOrderNumber(),item.toString(),item.toString(),"delete",request);


                // Now safely delete the parent
                pandsToJobOrderRepository.deleteById(item.getId());
            }
        } catch (Exception e) {
            System.out.println("returnInDelete");
            e.printStackTrace();
        }
    }


    public Integer getTheMaxNumber(Long projectId) {

        Integer number = jobOrderRepository.findMaxNumber(projectId);

        if (number == null) {
            number = 0;
        }

        return number;
    }

    public JobOrder getByJobOrder(String jobOrderNumber) {
        return jobOrderRepository.getByJobOrderNumber(jobOrderNumber);
    }

    public JobOrder jobOrderSearch(String jobOrderNumber, Long projectId) {
        return jobOrderRepository.getJobOrderByProjectProfileIdAndJobOrderNumber(jobOrderNumber,projectId);
    }

//    public long getNextCounter() {
//        return counter.incrementAndGet();
//    }

    public JobOrder copyJobORder(String jobOrder) throws SQLException, BadRequestException {


        GregorianCalendar gcalendar = new GregorianCalendar();
        JobOrder jobOrder1 = jobOrderRepository.getByJobOrderNumber(jobOrder);
        if(!jobOrder1.isApproved()){
            throw new BadRequestException();
        }
        Integer number = getTheMaxNumber(jobOrder1.getProjectProfileId());
        int nextNumber = number + 1;
        String jobOrderNumber =jobOrder1.getProjectCode().concat("/") + nextNumber + "/" + gcalendar.get(Calendar.YEAR);
        JobOrder newJobOrder = new JobOrder();
        newJobOrder.setJobOrderNumber(jobOrderNumber);
        newJobOrder.setNumber(number + 1);


        newJobOrder.setInstallementArea(jobOrder1.getInstallementArea());
        newJobOrder.setProjectCode(jobOrder1.getProjectCode());
        newJobOrder.setProjectName(jobOrder1.getProjectName());
        newJobOrder.setProjectProfileId(jobOrder1.getProjectProfileId());
        newJobOrder.setYear(gcalendar.get(Calendar.YEAR));
        newJobOrder.setCommit(true);

        DateFormat formatter1 = new SimpleDateFormat("dd.MM.yy");

        Date dNow = new Date();
        SimpleDateFormat ft =
                new SimpleDateFormat("hh:mm:ss a");

        newJobOrder.setJobOrderDate(formatter1.format(dNow));
        newJobOrder.setJobOrderTime(ft.format(dNow));

        List<PandsToJobOrder> pandsToJobOrders = pandsToJobOrderRepository.getByJobOrderId(jobOrder);


        jobOrderRepository.save(newJobOrder);

        DecimalFormat df = new DecimalFormat("#.###");

        List<PandsToJobOrder> pandsToJobOrderList = new ArrayList<PandsToJobOrder>();

        for (int i = 0; i < pandsToJobOrders.size(); i++) {
            PandsToJobOrder pandsToJobOrder = new PandsToJobOrder();
            Pand pand = pandsService.getPandByPandCode(pandsToJobOrders.get(i).getPandCode(), pandsToJobOrders.get(i).getProjectProfileId());
            pand.setRestQuantity(Double.parseDouble(df.format(pand.getRestQuantity() - Double.parseDouble(pandsToJobOrders.get(i).getTotal()))));
            pandsRepository.save(pand);
            long leastSigBits = System.currentTimeMillis();
            long mostSigBits = Instant.now().getEpochSecond();
            UUID uuid = new UUID(mostSigBits, leastSigBits);
//            pandsToJobOrderRepository.getByUniqueId(uuid.toString());
            pandsToJobOrder.setUniqueId(uuid.toString());

//            double total;
//
//            if (pandsToJobOrders.get(i).getUnit().equals("متر مربع")) {
//                total = (Double.parseDouble(pandsToJobOrders.get(i).getHeight()) * Double.parseDouble(pandsToJobOrders.get(i).getWidth()) * (pandsToJobOrders.get(i).getQuantity() * Double.parseDouble(pandsToJobOrders.get(i).getRepetition()))) / 10000;
//            } else if (pandsToJobOrders.get(i).getUnit().equals("متر طولى")) {
//                total = (Double.parseDouble(pandsToJobOrders.get(i).getHeight()) * (pandsToJobOrders.get(i).getQuantity() * Double.parseDouble(pandsToJobOrders.get(i).getRepetition()))) / 100;
//            } else {
//                total = pandsToJobOrders.get(i).getQuantity() * Double.parseDouble(pandsToJobOrders.get(i).getRepetition());
//            }

            pandsToJobOrder.setTotal(pandsToJobOrders.get(i).getMainTotal());
            pandsToJobOrder.setMainTotal(pandsToJobOrders.get(i).getMainTotal());
            pandsToJobOrder.setMainQuantity(pandsToJobOrders.get(i).getMainQuantity());
            pandsToJobOrder.setQuantity(pandsToJobOrders.get(i).getMainQuantity());

            pandsToJobOrder.setJobOrderId(jobOrderNumber);
            pandsToJobOrder.setRawType(pandsToJobOrders.get(i).getRawType());
//            pandsToJobOrder.setUnifiedSerial(pandsToJobOrders.get(i).getUnifiedSerial());
            pandsToJobOrder.setInstallationArea(pandsToJobOrders.get(i).getInstallationArea());
            pandsToJobOrder.setFinishType(pandsToJobOrders.get(i).getFinishType());
            pandsToJobOrder.setRawUsed(pandsToJobOrders.get(i).getRawUsed());
            pandsToJobOrder.setManufacturing(pandsToJobOrders.get(i).getManufacturing());
            pandsToJobOrder.setManufacturingCode(pandsToJobOrders.get(i).getManufacturingCode());
            pandsToJobOrder.setThickness(pandsToJobOrders.get(i).getThickness());
            pandsToJobOrder.setDescription(pandsToJobOrders.get(i).getDescription());
            pandsToJobOrder.setAdditionalDescription(pandsToJobOrders.get(i).getAdditionalDescription());
            pandsToJobOrder.setFloor(pandsToJobOrders.get(i).getFloor());
            pandsToJobOrder.setHeight(pandsToJobOrders.get(i).getHeight());
            pandsToJobOrder.setWidth(pandsToJobOrders.get(i).getWidth());
            pandsToJobOrder.setUnit(pandsToJobOrders.get(i).getUnit());
            pandsToJobOrder.setRepetition(pandsToJobOrders.get(i).getRepetition());
            pandsToJobOrder.setPandCode(pandsToJobOrders.get(i).getPandCode());
            pandsToJobOrder.setProjectName(pandsToJobOrders.get(i).getProjectName());
            pandsToJobOrder.setProjectCode(pandsToJobOrders.get(i).getProjectCode());
            pandsToJobOrder.setOfficerName(pandsToJobOrders.get(i).getOfficerName());
            pandsToJobOrder.setJobOrderType(pandsToJobOrders.get(i).getJobOrderType());
            pandsToJobOrder.setBlockNumber(pandsToJobOrders.get(i).getBlockNumber());
            pandsToJobOrder.setProjectProfileId(pandsToJobOrders.get(i).getProjectProfileId());
            pandsToJobOrder.setEngineerName(pandsToJobOrders.get(i).getEngineerName());
            pandsToJobOrder.setQuantityInPand(pandsToJobOrders.get(i).getQuantityInPand());
            pandsToJobOrderRepository.save(pandsToJobOrder);
            pandsToJobOrderList.add(pandsToJobOrder);
        }
        newJobOrder.setPandsToJobOrders(pandsToJobOrderList);
        return newJobOrder;
    }

    public InputStreamResource getJobOrderDetails(Long id) throws Exception {
        try {
            com.aspose.cells.Workbook workbook = new com.aspose.cells.Workbook();
            WorksheetCollection worksheets = workbook.getWorksheets();
            Worksheet sheet = worksheets.get(0);
            sheet.setDisplayRightToLeft(true);
//            sheet.getCells().setRowHeight(7, 20);

            PageSetup pageSetup = sheet.getPageSetup();
            pageSetup.setFooter(1, "Page &P of &N");
            pageSetup.setOrientation(PageOrientationType.LANDSCAPE);
            pageSetup.setFitToPagesWide(1); // Fit to 1 page width
            pageSetup.setFitToPagesTall(0); // Set to 0 for automatic height

            pageSetup.setTopMargin(1);
            pageSetup.setBottomMargin(1);
            pageSetup.setLeftMargin(1);
            pageSetup.setRightMargin(1);

            Style tableHeaderStyle = sheet.getCells().get("C1").getStyle();
            tableHeaderStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
            tableHeaderStyle.setVerticalAlignment(TextAlignmentType.CENTER);
            tableHeaderStyle.getFont().setItalic(true);
            tableHeaderStyle.getFont().setSize(11);
            tableHeaderStyle.getFont().setBold(false);
            tableHeaderStyle.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());
            tableHeaderStyle.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());
            tableHeaderStyle.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());
            tableHeaderStyle.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());

//            Style tableHeaderStyle2 = sheet.getCells().get("C1").getStyle();
//            tableHeaderStyle2.setHorizontalAlignment(TextAlignmentType.CENTER);
//            tableHeaderStyle2.setVerticalAlignment(TextAlignmentType.CENTER);
//            tableHeaderStyle2.getFont().setItalic(true);
//            tableHeaderStyle2.getFont().setSize(9);
//            tableHeaderStyle2.getFont().setBold(false);
//            tableHeaderStyle2.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());
//            tableHeaderStyle2.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());
//            tableHeaderStyle2.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());
//            tableHeaderStyle2.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());


            Style discriptionDataStyle = sheet.getCells().get("F3").getStyle();
            discriptionDataStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
            discriptionDataStyle.setVerticalAlignment(TextAlignmentType.CENTER);
            discriptionDataStyle.getFont().setSize(11);

            Date dNow = new Date();
            SimpleDateFormat ft =
                    new SimpleDateFormat("hh:mm:ss a");

            DateFormat formatter1 = new SimpleDateFormat("dd.MM.yy");

            Optional<JobOrder> jobOrderNumber = jobOrderRepository.findById(id);


            sheet.getCells().get("A1").putValue("أسم المشروع");
            sheet.getCells().get("A1").setStyle(discriptionDataStyle);

            sheet.getCells().get("B1").putValue(jobOrderNumber.get().getProjectName());
            sheet.getCells().get("B1").setStyle(discriptionDataStyle);

            sheet.getCells().get("A3").putValue("كود المشروع");
            sheet.getCells().get("A3").setStyle(discriptionDataStyle);

            sheet.getCells().get("B3").putValue(jobOrderNumber.get().getProjectCode());
            sheet.getCells().get("B3").setStyle(discriptionDataStyle);

            sheet.getCells().get("E1").putValue("تاريخ الطباعة");
            sheet.getCells().get("E1").setStyle(discriptionDataStyle);

            sheet.getCells().get("F1").putValue(formatter1.format(dNow)+ " " + ft.format(dNow));
            sheet.getCells().get("F1").setStyle(discriptionDataStyle);

            sheet.getCells().get("E3").putValue("رقم أمر الشغل");
            sheet.getCells().get("E3").setStyle(discriptionDataStyle);

            sheet.getCells().get("F3").putValue(jobOrderNumber.get().getJobOrderNumber());
            sheet.getCells().get("F3").setStyle(discriptionDataStyle);



            Style shadowStyle = workbook.createStyle();
            shadowStyle.setPattern(BackgroundType.SOLID);
            shadowStyle.setForegroundColor(Color.getDarkGray());
            shadowStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
            shadowStyle.getFont().setSize(11);


            sheet.getCells().get("A5").putValue("رقم البند");
            sheet.getCells().get("A5").setStyle(tableHeaderStyle);

            sheet.getCells().get("B5").putValue("الكمية");
            sheet.getCells().get("B5").setStyle(tableHeaderStyle);

            sheet.getCells().get("C5").putValue("الكمية المصروفة");
            sheet.getCells().get("C5").setStyle(tableHeaderStyle);

            sheet.getCells().get("D5").putValue("الرصيد المتبقى");
            sheet.getCells().get("D5").setStyle(tableHeaderStyle);


            InputStream imageStream = new ClassPathResource("static/Hossam-Zeitoun-Logo-Black.png").getInputStream();

            // Add the image to the worksheet (X, Y coordinates in pixels)
            // Place the image inside the merged cells (A1:C5)
            int pictureIndex = sheet.getPictures().add(0, 7, imageStream);

            // Get the added picture
            Picture picture = sheet.getPictures().get(pictureIndex);

            // Optionally, set the picture to fit within the merged area
            picture.setPlacement(PlacementType.MOVE);
            picture.setWidthScale(20); // Scale the image to fit width
            picture.setHeightScale(10);

            int rowIdx = 7;

            List<PandsToJobOrder> pandsToJobOrders = pandsToJobOrderRepository.getJobOrderDetails(jobOrderNumber.get().getProjectProfileId(),jobOrderNumber.get().getJobOrderNumber());
            Double totalQuantityInJobOrders = 0.0;
            Double totalQuantityInExitJobOrders = 0.0;
            for (PandsToJobOrder entry : pandsToJobOrders) {

                sheet.getCells().get("A" + rowIdx).putValue(entry.getPandCode());  // رقم امر شغل
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("A" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("A" + rowIdx).setStyle(discriptionDataStyle);
                }

                Double totalSumInJobOrders = pandsToJobOrderRepository.getSumByPandCodeAndJobOrder(entry.getProjectProfileId(),entry.getPandCode(),jobOrderNumber.get().getJobOrderNumber());

                if (totalSumInJobOrders == null) {
                    totalSumInJobOrders = 0.0;
                }

                totalQuantityInJobOrders += Double.valueOf(totalSumInJobOrders);

                sheet.getCells().get("B" + rowIdx).putValue(totalSumInJobOrders); // الكميه لكل امر شغل
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("B" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("B" + rowIdx).setStyle(discriptionDataStyle);
                }

                Double totalExit = exitJobOrderRepository.getSumByJobOrderAndPand(jobOrderNumber.get().getProjectCode(), entry.getPandCode(), entry.getJobOrderId());

                if (totalExit == null) {
                    totalExit = 0.0;
                }
                totalQuantityInExitJobOrders += totalExit;
//                DecimalFormat df = new DecimalFormat("#.###");
//                String formattedNumber = df.format(totalExit);
                sheet.getCells().get("C" + rowIdx).putValue(totalExit); // الكميه المصروفه
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("C" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("C" + rowIdx).setStyle(discriptionDataStyle);
                }

                Double value = totalSumInJobOrders - totalExit ;

                sheet.getCells().get("D" + rowIdx).putValue(value);
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("D" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("D" + rowIdx).setStyle(discriptionDataStyle);
                }


                rowIdx++;
            }

            rowIdx++;

            sheet.getCells().get("B" + rowIdx).putValue(totalQuantityInJobOrders); // الكميه لكل امر شغل
            sheet.getCells().get("B" + rowIdx).setStyle(shadowStyle);


            sheet.getCells().get("C" + rowIdx).putValue(totalQuantityInExitJobOrders); // الكميه لكل امر شغل
            sheet.getCells().get("C" + rowIdx).setStyle(shadowStyle);


            for (int i = 4; i < rowIdx; i++) {
                sheet.getCells().setRowHeight(i, 18);
            }

            //////////////////////////////////////////////////////////////////////
            // Adjust column widths to fit content
            sheet.autoFitColumns();

            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            workbook.save(pdfOutputStream, SaveFormat.PDF); // Save as PDF

            // 4. Return the PDF as a response
            ByteArrayInputStream pdfInputStream = new ByteArrayInputStream(pdfOutputStream.toByteArray());
            InputStreamResource resource = new InputStreamResource(pdfInputStream);
            System.out.println("1111111111111111111111111");
            return resource;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void commitWorkOrderById(long id) {
        Optional<JobOrder> jobOrder = jobOrderRepository.findById(id);
        jobOrder.get().setCommit(true);
        jobOrderRepository.save(jobOrder.get());
    }

    public void commitWorkOrderByProject(String id) {
        JobOrder jobOrder = jobOrderRepository.getJobOrderByProjectProfileId(id);
        jobOrder.setCommit(true);
        jobOrderRepository.save(jobOrder);
    }

    public JobOrder approveWorkOrder(String jobOrderId) {
        String pandCode = "";
        try {
            JobOrder jobOrder = jobOrderRepository.getByJobOrderNumber(jobOrderId);


            jobOrder.setApproved(true);
            jobOrderRepository.save(jobOrder);


            return jobOrder;
        } catch (Exception e) {
            JobOrder jobOrder1 = new JobOrder();
            jobOrder1.setFlag(1);
            jobOrder1.setMessage(pandCode + " الكمية المطلوبة اعلى من الكمية المتبقية فى البند ");
            return jobOrder1;
        }

    }

    public InputStreamResource getWorkOrderReport(WorkOrderReport workOrderReport) {

        try {
            com.aspose.cells.Workbook workbook = new com.aspose.cells.Workbook();
            WorksheetCollection worksheets = workbook.getWorksheets();
            Worksheet sheet = worksheets.get(0);
            sheet.setDisplayRightToLeft(true);
//            sheet.getCells().setRowHeight(7, 20);

            PageSetup pageSetup = sheet.getPageSetup();
            pageSetup.setFooter(1, "Page &P of &N");
            pageSetup.setOrientation(PageOrientationType.LANDSCAPE);
            pageSetup.setFitToPagesWide(1); // Fit to 1 page width
            pageSetup.setFitToPagesTall(0); // Set to 0 for automatic height

            pageSetup.setTopMargin(1);
            pageSetup.setBottomMargin(1);
            pageSetup.setLeftMargin(1);
            pageSetup.setRightMargin(1);

            Style tableHeaderStyle = sheet.getCells().get("C1").getStyle();
            tableHeaderStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
            tableHeaderStyle.setVerticalAlignment(TextAlignmentType.CENTER);
            tableHeaderStyle.getFont().setItalic(true);
            tableHeaderStyle.getFont().setSize(11);
            tableHeaderStyle.getFont().setBold(false);
            tableHeaderStyle.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());
            tableHeaderStyle.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());
            tableHeaderStyle.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());
            tableHeaderStyle.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());

//            Style tableHeaderStyle2 = sheet.getCells().get("C1").getStyle();
//            tableHeaderStyle2.setHorizontalAlignment(TextAlignmentType.CENTER);
//            tableHeaderStyle2.setVerticalAlignment(TextAlignmentType.CENTER);
//            tableHeaderStyle2.getFont().setItalic(true);
//            tableHeaderStyle2.getFont().setSize(9);
//            tableHeaderStyle2.getFont().setBold(false);
//            tableHeaderStyle2.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());
//            tableHeaderStyle2.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());
//            tableHeaderStyle2.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());
//            tableHeaderStyle2.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());


            Style discriptionDataStyle = sheet.getCells().get("F3").getStyle();
            discriptionDataStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
            discriptionDataStyle.setVerticalAlignment(TextAlignmentType.CENTER);
            discriptionDataStyle.getFont().setSize(11);

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yy");


            Date date1 = inputFormat.parse(workOrderReport.getFromDate());
            String fromDate = outputFormat.format(date1);

            Date date2 = inputFormat.parse(workOrderReport.getToDate());
            String toDate = outputFormat.format(date2);

            List<JobOrder> jobOrderNumber = jobOrderRepository.findFiltered(fromDate,toDate,null);

            List<String> projectNames = jobOrderNumber.stream()
                    .map(JobOrder::getProjectName)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());


            sheet.getCells().get("A1").putValue("From Date");
            sheet.getCells().get("A1").setStyle(discriptionDataStyle);

            sheet.getCells().get("B1").putValue(workOrderReport.getFromDate());
            sheet.getCells().get("B1").setStyle(discriptionDataStyle);

            sheet.getCells().get("A3").putValue("To Date");
            sheet.getCells().get("A3").setStyle(discriptionDataStyle);

            sheet.getCells().get("B3").putValue(workOrderReport.getToDate());
            sheet.getCells().get("B3").setStyle(discriptionDataStyle);


            Style shadowStyle = workbook.createStyle();
            shadowStyle.setPattern(BackgroundType.SOLID);
            shadowStyle.setForegroundColor(Color.getDarkGray());
            shadowStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
            shadowStyle.getFont().setSize(11);


            sheet.getCells().get("A5").putValue("أسم المشروع");
            sheet.getCells().get("A5").setStyle(tableHeaderStyle);

            sheet.getCells().get("B5").putValue("كود المشروع");
            sheet.getCells().get("B5").setStyle(tableHeaderStyle);

            sheet.getCells().get("C5").putValue("رقم أمر الشغل");
            sheet.getCells().get("C5").setStyle(tableHeaderStyle);


            InputStream imageStream = new ClassPathResource("static/Hossam-Zeitoun-Logo-Black.png").getInputStream();

            // Add the image to the worksheet (X, Y coordinates in pixels)
            // Place the image inside the merged cells (A1:C5)
            int pictureIndex = sheet.getPictures().add(0, 7, imageStream);

            // Get the added picture
            Picture picture = sheet.getPictures().get(pictureIndex);

            // Optionally, set the picture to fit within the merged area
            picture.setPlacement(PlacementType.MOVE);
            picture.setWidthScale(20); // Scale the image to fit width
            picture.setHeightScale(10);

            int rowIdx = 7;



            for (String entry : projectNames) {

                List<JobOrder> jobOrderList = jobOrderRepository.findFiltered(fromDate,toDate,entry);

                sheet.getCells().get("A" + rowIdx).putValue(entry);  // رقم امر شغل
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("A" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("A" + rowIdx).setStyle(discriptionDataStyle);
                }

                for (int i = 0; i < jobOrderList.size(); i++) {

                    sheet.getCells().get("B" + rowIdx).putValue(jobOrderList.get(i).getProjectCode());
                    if (rowIdx % 2 != 0) {
                        sheet.getCells().get("B" + rowIdx).setStyle(shadowStyle);
                    } else {
                        sheet.getCells().get("B" + rowIdx).setStyle(discriptionDataStyle);
                    }

                    sheet.getCells().get("C" + rowIdx).putValue(jobOrderList.get(i).getJobOrderNumber());
                    if (rowIdx % 2 != 0) {
                        sheet.getCells().get("C" + rowIdx).setStyle(shadowStyle);
                    } else {
                        sheet.getCells().get("C" + rowIdx).setStyle(discriptionDataStyle);
                    }

                    rowIdx++;
                }

                rowIdx+=2;
            }


            for (int i = 4; i < rowIdx; i++) {
                sheet.getCells().setRowHeight(i, 18);
            }

            //////////////////////////////////////////////////////////////////////
            // Adjust column widths to fit content
            sheet.autoFitColumns();

            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            workbook.save(pdfOutputStream, SaveFormat.PDF); // Save as PDF

            // 4. Return the PDF as a response
            ByteArrayInputStream pdfInputStream = new ByteArrayInputStream(pdfOutputStream.toByteArray());
            InputStreamResource resource = new InputStreamResource(pdfInputStream);
            System.out.println("1111111111111111111111111");
            return resource;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
