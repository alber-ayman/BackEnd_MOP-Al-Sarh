package com.example.demo.service;


import com.example.demo.models.ExitJobOrder;
import com.example.demo.models.JobOrderParent;
import com.example.demo.models.PandsToJobOrder;
import com.example.demo.payload.CheckLimitResponse;
import com.example.demo.repository.ExitJobOrderRepository;
import com.example.demo.repository.PandsToJobOrderRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFPrintSetup;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.text.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ExitJobOrderService {

    @Autowired
    ExitJobOrderRepository exitJobOrderRepository;

    @Autowired
    PandsToJobOrderService pandsToJobOrderService;

    @Autowired
    PandsToJobOrderRepository pandsToJobOrderRepository;

    private static final AtomicInteger counter = new AtomicInteger(1000);

    public ResponseEntity<List<ExitJobOrder>> getAll() {

        return new ResponseEntity<>(exitJobOrderRepository.findAll(), HttpStatus.OK);

    }

    public ResponseEntity<JobOrderParent> saveChildPand(JobOrderParent exitJobOrders, String unifiedSerial) throws ParseException {
        try {

            List<ExitJobOrder> jobOrders = exitJobOrderRepository.getByUnifiedSerial(unifiedSerial);

            if (jobOrders.size() > 0) {
                exitJobOrders.setFlag(1);
                exitJobOrders.setMessage("رقم الفاتوره مستخدم من قبل");
                return new ResponseEntity<>(exitJobOrders, HttpStatus.OK);
            }

            for (int i = 0; i < exitJobOrders.getPandsToJobOrderList().size(); i++) {
                if (exitJobOrders.getPandsToJobOrderList().get(i).getQuantityUsedRaws() == null) {
                    exitJobOrders.setFlag(1);
                    exitJobOrders.setMessage("برجاء ادخال كمية الخامات المستخدمة للبند " + exitJobOrders.getPandsToJobOrderList().get(i).getPandCode());
                    return new ResponseEntity<>(exitJobOrders, HttpStatus.OK);
                }
            }

            Double total = 0.0;
            DecimalFormat df = new DecimalFormat("#.###");
//        NumberFormat format = NumberFormat.getInstance(Locale.US);
            String serialNumber = "";
            List<PandsToJobOrder> pandsToJobOrder = pandsToJobOrderService.getByJobOrderId(exitJobOrders.getPandsToJobOrderList().get(0).getJobOrderId());

            int count = 0;
            for (int i = 0; i < exitJobOrders.getPandsToJobOrderList().size(); i++) {
                count++;
                ExitJobOrder exitJobOrder = new ExitJobOrder();
                exitJobOrder = mappingJobOrder(exitJobOrders.getPandsToJobOrderList().get(i));
                System.out.println("unit: " + exitJobOrders.getPandsToJobOrderList().get(i).getUnit());
                if (exitJobOrders.getPandsToJobOrderList().get(i).getUnit().equals("متر مربع")) {
                    total = (Double.parseDouble(exitJobOrders.getPandsToJobOrderList().get(i).getHeight()) * Double.parseDouble(exitJobOrders.getPandsToJobOrderList().get(i).getWidth()) * exitJobOrders.getPandsToJobOrderList().get(i).getQuantity()) / 10000.0;
                } else if (exitJobOrders.getPandsToJobOrderList().get(i).getUnit().equals("متر طولى")) {
                    total = (Double.parseDouble(exitJobOrders.getPandsToJobOrderList().get(i).getHeight()) * exitJobOrders.getPandsToJobOrderList().get(i).getQuantity()) / 100.0;
                } else {
                    total = exitJobOrders.getPandsToJobOrderList().get(i).getQuantity();
                    System.out.println("?????????????????");
                }

                total = Double.valueOf(df.format(total));
                System.out.println(":::::: " + total + " ::::::");
                System.out.println("quantyyyy:" + exitJobOrders.getPandsToJobOrderList().get(i).getQuantity());

                for (int k = 0; k < pandsToJobOrder.size(); k++) {
                    if ((exitJobOrder.getPandCode().equals(pandsToJobOrder.get(k).getPandCode()))
                            && (exitJobOrder.getProjectCode().equals(pandsToJobOrder.get(k).getProjectCode()))
                            && (exitJobOrder.getHeight().equals(pandsToJobOrder.get(k).getHeight()))
                            && (exitJobOrder.getWidth().equals(pandsToJobOrder.get(k).getWidth()))
                            && (exitJobOrder.getUniqueId().equals(pandsToJobOrder.get(k).getUniqueId()))
                    ) {
                        exitJobOrder.setManufacturingCode(pandsToJobOrder.get(k).getManufacturingCode());
                        if (pandsToJobOrder.get(k).getQuantity() < exitJobOrder.getQuantity()) {

                            exitJobOrders.setFlag(1);
                            exitJobOrders.setMessage(" الكمية: " + " ( " + exitJobOrder.getQuantity() + " ) " + " المطلوبة اعلى من الكمية فى بند رقم " + exitJobOrder.getPandCode());
                            return new ResponseEntity<>(exitJobOrders, HttpStatus.OK);
                        }
//                        String totalAfterExit = String.valueOf(df.format(Double.valueOf(pandsToJobOrder.get(k).getTotal()) - total));
//                        pandsToJobOrder.get(k).setTotal(totalAfterExit);
//                        double result2 = pandsToJobOrder.get(k).getQuantity() - exitJobOrder.getQuantity();
//                        pandsToJobOrder.get(k).setQuantity(result2);
//                        pandsToJobOrderRepository.save(pandsToJobOrder.get(k));
                    }
                }
                if (i == 0) {
                    serialNumber = generateSerialNumber();
                }
                Date dNow = new Date();
                SimpleDateFormat ft =
                        new SimpleDateFormat("hh:mm:ss a");

                DateFormat formatter1 = new SimpleDateFormat("dd.MM.yy");

                String formattedNumber = df.format(total);
                exitJobOrder.setTotal(String.valueOf(formattedNumber));
                exitJobOrder.setQuantity(exitJobOrders.getPandsToJobOrderList().get(i).getQuantity());
//                exitJobOrder.setUniqueId(exitJobOrders.getPandsToJobOrderList().get(i).getUniqueId());
                exitJobOrder.setSerialNumber(serialNumber);
                exitJobOrder.setUnifiedSerial(unifiedSerial);
                exitJobOrder.setExitDate(formatter1.format(dNow)+ " " + ft.format(dNow).toString());

                exitJobOrderRepository.save(exitJobOrder);
            }
            return new ResponseEntity<>(exitJobOrders, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ExitJobOrder mappingJobOrder(PandsToJobOrder updatedJobOrder) {

        ExitJobOrder jobOrder = new ExitJobOrder();

//        jobOrder.setExitJobOrderId(generateUniqueId());
        jobOrder.setUniqueId(updatedJobOrder.getUniqueId());
        jobOrder.setJobOrderId(updatedJobOrder.getJobOrderId());
        jobOrder.setProjectProfileId((updatedJobOrder.getProjectProfileId()));
        jobOrder.setProjectCode(updatedJobOrder.getProjectCode());
        jobOrder.setProjectName(updatedJobOrder.getProjectName());
        jobOrder.setEngineerName(updatedJobOrder.getEngineerName());
        jobOrder.setJobOrderType(updatedJobOrder.getJobOrderType());
        jobOrder.setInstallationArea(updatedJobOrder.getInstallationArea());
        jobOrder.setQuantityUsedRaws(updatedJobOrder.getQuantityUsedRaws());
        jobOrder.setPandCode(updatedJobOrder.getPandCode());
        jobOrder.setDescription(updatedJobOrder.getDescription());
        jobOrder.setManufacturing(updatedJobOrder.getManufacturing());
//        jobOrder.setManufacturingCode(updatedJobOrder.getManufacturingCode());
        jobOrder.setRawType(updatedJobOrder.getRawType());
        jobOrder.setRawUsed(updatedJobOrder.getRawUsed());
        jobOrder.setFinishType(updatedJobOrder.getFinishType());
        jobOrder.setThickness(updatedJobOrder.getThickness());
        jobOrder.setBlockNumber(updatedJobOrder.getBlockNumber());
        jobOrder.setFloor(updatedJobOrder.getFloor());
        jobOrder.setOfficerName(updatedJobOrder.getOfficerName());
        jobOrder.setHeight(updatedJobOrder.getHeight());
        jobOrder.setWidth(updatedJobOrder.getWidth());
        jobOrder.setRepetition(updatedJobOrder.getRepetition());
        jobOrder.setTotal(updatedJobOrder.getTotal());
        jobOrder.setUnit(updatedJobOrder.getUnit());
        jobOrder.setQuantity(updatedJobOrder.getQuantity());
        jobOrder.setAdditionalDescription(updatedJobOrder.getAdditionalDescription());

        return jobOrder;
    }

    public static String generateSerialNumber() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHss");
        String timestamp = sdf.format(new Date());
        int uniqueId = counter.getAndIncrement();
        return timestamp + uniqueId;
    }

    public static String generateUniqueId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHss");
        String timestamp = sdf.format(new Date());
        return timestamp;
    }


    public List<ExitJobOrder> getByJobOrderId(String id) {

        return exitJobOrderRepository.getByJobOrderId(id);
    }

    public ResponseEntity<List<ExitJobOrder>> getAllExitJobOrders(String jobOrderid) {
        return new ResponseEntity<>(exitJobOrderRepository.getByJobOrderId(jobOrderid), HttpStatus.OK);
    }

    public ResponseEntity<ExitJobOrder> getExitById(Long jobOrderid) {
        Optional<ExitJobOrder> jobOrder = exitJobOrderRepository.findById(jobOrderid);
        return new ResponseEntity<>(jobOrder.get(), HttpStatus.OK);
    }

    public void checkQuantity() {
        List<ExitJobOrder> jobOrder = exitJobOrderRepository.findAll();
        for (int i = 0; i < jobOrder.size(); i++) {
            if (jobOrder.get(i).getQuantity() == 0) {
                exitJobOrderRepository.delete(jobOrder.get(i));
            }
        }
    }

    public CheckLimitResponse returnJobOrder(JobOrderParent jobOrderParent) {

        CheckLimitResponse checkLimitResponse = new CheckLimitResponse();
        for (int i = 0; i < jobOrderParent.getPandsToJobOrderList().size(); i++) {
            List<ExitJobOrder> jobOrder = exitJobOrderRepository.getByUniqueId(jobOrderParent.getPandsToJobOrderList().get(i).getUniqueId());
            PandsToJobOrder pandsToJobOrder = pandsToJobOrderRepository.getByUniqueId(jobOrder.get(0).getUniqueId());
//            double value = Double.valueOf(pandsToJobOrder.getTotal()) + Double.valueOf(jobOrderParent.getPandsToJobOrderList().get(i).getTotal());

            double total;
            DecimalFormat df = new DecimalFormat("#.###");

            if (jobOrder.get(0).getUnit().equals("متر مربع")) {
                total = (Double.valueOf(jobOrder.get(0).getHeight()) * Double.valueOf(jobOrder.get(0).getWidth()) * Double.valueOf(jobOrder.get(0).getQuantity())) / 10000;
            } else if (jobOrder.get(0).getUnit().equals("متر طولى")) {
                total = (Double.valueOf(jobOrder.get(0).getHeight()) * Double.valueOf(jobOrder.get(0).getQuantity())) / 100;
            } else {
                total = Double.valueOf(jobOrder.get(0).getQuantity());
            }

            if (total > Double.valueOf(pandsToJobOrder.getMainTotal())) {
                checkLimitResponse.setFlag(1);
                checkLimitResponse.setMessage("الكمية المرجعه تتخطى الكمية الأساسية فى بند رقم " + pandsToJobOrder.getPandCode());
                return checkLimitResponse;
            } else {
                checkLimitResponse.setFlag(0);
                checkLimitResponse.setMessage("تم أرجاع أوامر الشغل المحدده بنجاح" + pandsToJobOrder.getPandCode());
            }
            pandsToJobOrder.setQuantity(pandsToJobOrder.getQuantity() + (jobOrderParent.getPandsToJobOrderList().get(i).getQuantity()));
//            pandsToJobOrder.setTotal(String.valueOf(Double.valueOf(pandsToJobOrder.getTotal()) + Double.valueOf(df.format(total))));
            pandsToJobOrderRepository.save(pandsToJobOrder);
            jobOrder.get(jobOrder.size() - 1).setQuantity(jobOrder.get(jobOrder.size() - 1).getQuantity() + jobOrderParent.getPandsToJobOrderList().get(i).getQuantity());
            jobOrder.get(jobOrder.size() - 1).setTotal(String.valueOf(Double.valueOf(jobOrder.get(jobOrder.size() - 1).getTotal()) + Double.valueOf(df.format(total))));
            exitJobOrderRepository.save(jobOrder.get(jobOrder.size() - 1));

        }
        return checkLimitResponse;
    }

    public void deleteJobOrder(Long id) {
        try {
            double total;
            DecimalFormat df = new DecimalFormat("#,##0.000");

            ExitJobOrder jobOrder = exitJobOrderRepository.getById(id);
//            PandsToJobOrder pandsToJobOrder = pandsToJobOrderRepository.findAllByJobOrderIdAndPandCode(jobOrder.getJobOrderId(), jobOrder.getPandCode(), jobOrder.getHeight(), jobOrder.getWidth());
            PandsToJobOrder pandsToJobOrder = pandsToJobOrderRepository.getByUniqueIdAndJobOrderId(jobOrder.getUniqueId(), jobOrder.getJobOrderId());

            pandsToJobOrder.setQuantity(pandsToJobOrder.getQuantity() + jobOrder.getQuantity());
            NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
            Number number = format.parse(pandsToJobOrder.getTotal());
            double value = number.doubleValue();
            double result = value + Double.valueOf(jobOrder.getTotal());
            pandsToJobOrder.setTotal(df.format(result));

            exitJobOrderRepository.delete(jobOrder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteJobOrderBySerialNumber(String serialNumber) {
        try {
            double total;
            DecimalFormat df = new DecimalFormat("#.###");

            List<ExitJobOrder> jobOrder = exitJobOrderRepository.getBySerial(serialNumber);
            for (int i = 0; i < jobOrder.size(); i++) {
//                PandsToJobOrder pandsToJobOrder = pandsToJobOrderRepository.findAllByJobOrderIdAndPandCode(jobOrder.getJobOrderId(), jobOrder.getPandCode(), jobOrder.getHeight(), jobOrder.getWidth());
                PandsToJobOrder pandsToJobOrder = pandsToJobOrderRepository.getByUniqueIdAndJobOrderId(jobOrder.get(i).getUniqueId(), jobOrder.get(i).getJobOrderId());

                pandsToJobOrder.setQuantity(pandsToJobOrder.getQuantity() + jobOrder.get(i).getQuantity());
                pandsToJobOrder.setTotal(df.format(Double.valueOf(pandsToJobOrder.getTotal()) + Double.valueOf(jobOrder.get(i).getTotal())));

                exitJobOrderRepository.delete(jobOrder.get(i));
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public ByteArrayOutputStream buildFile(String id) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("exit Orders");
            sheet.setRightToLeft(true);


            int widthInCharacters = 12;
            sheet.setColumnWidth(0, widthInCharacters * 256);
            sheet.setColumnWidth(1, widthInCharacters * 256);
            sheet.setColumnWidth(2, widthInCharacters * 256);
            sheet.setColumnWidth(3, widthInCharacters * 256);
            sheet.setColumnWidth(4, widthInCharacters * 256);
            sheet.setColumnWidth(5, widthInCharacters * 256);
            sheet.setColumnWidth(6, widthInCharacters * 256);
            sheet.setColumnWidth(7, widthInCharacters * 256);
            sheet.setColumnWidth(8, widthInCharacters * 256);
            sheet.setColumnWidth(9, widthInCharacters * 256);
            sheet.setColumnWidth(10, widthInCharacters * 256);
            sheet.setColumnWidth(11, widthInCharacters * 256);
            sheet.setColumnWidth(12, widthInCharacters * 256);
            sheet.setColumnWidth(13, widthInCharacters * 256);
            sheet.setColumnWidth(14, widthInCharacters * 256);
            sheet.setColumnWidth(15, widthInCharacters * 256);


            XSSFPrintSetup printSetup = (XSSFPrintSetup) sheet.getPrintSetup();
            printSetup.setLandscape(true);

            // Header
            Row firstRow = sheet.createRow(0);
            Cell firstRow01 = firstRow.createCell(0);
            Cell firstRow1 = firstRow.createCell(1);
            Cell firstRow2 = firstRow.createCell(2);
            Cell firstRow3 = firstRow.createCell(3);
            Cell firstRow4 = firstRow.createCell(4);
            Cell firstRow5 = firstRow.createCell(5);
            Cell firstRow6 = firstRow.createCell(6);
            Cell firstRow7 = firstRow.createCell(7);
            Cell firstRow8 = firstRow.createCell(8);
            Cell firstRow9 = firstRow.createCell(9);
            Cell firstRow10 = firstRow.createCell(10);
            Cell firstRow11 = firstRow.createCell(11);
            Cell firstRow12 = firstRow.createCell(12);
            Cell firstRow13 = firstRow.createCell(13);
            Cell firstRow14 = firstRow.createCell(14);
            Cell firstRow15 = firstRow.createCell(15);


            CellStyle cellStyle = workbook.createCellStyle();
//            cellStyle.setFont(font);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);


            firstRow.createCell(0).setCellValue("أسم المشروع");
            firstRow01.setCellStyle(cellStyle);
            firstRow.createCell(1).setCellValue("كود المشروع");
            firstRow1.setCellStyle(cellStyle);
            firstRow.createCell(2).setCellValue("المهندس المسؤول");
            firstRow2.setCellStyle(cellStyle);
            firstRow.createCell(3).setCellValue("كود البند");
            firstRow3.setCellStyle(cellStyle);
            firstRow.createCell(4).setCellValue("التوصيف");
            firstRow4.setCellStyle(cellStyle);

            firstRow.createCell(5).setCellValue("الوحدة");
            firstRow6.setCellStyle(cellStyle);
            firstRow.createCell(6).setCellValue("التصنيع");
            firstRow7.setCellStyle(cellStyle);
            firstRow.createCell(7).setCellValue("نوع الخامات فى البند");
            firstRow8.setCellStyle(cellStyle);
            firstRow.createCell(8).setCellValue("الخامة المستخدمة");
            firstRow9.setCellStyle(cellStyle);
            firstRow.createCell(9).setCellValue("نوع التشطيب");
            firstRow10.setCellStyle(cellStyle);
            firstRow.createCell(10).setCellValue("السمك");
            firstRow11.setCellStyle(cellStyle);
            firstRow.createCell(11).setCellValue("العدد");
            firstRow5.setCellStyle(cellStyle);
            firstRow.createCell(12).setCellValue("الطول");
            firstRow12.setCellStyle(cellStyle);
            firstRow.createCell(13).setCellValue("العرض");
            firstRow13.setCellStyle(cellStyle);
            firstRow.createCell(14).setCellValue("الاجمالى");
            firstRow14.setCellStyle(cellStyle);
            firstRow.createCell(15).setCellValue("كمية الخامات المستخدمة");
            firstRow15.setCellStyle(cellStyle);


            int rowIdx = 1;

            List<ExitJobOrder> pands = getByJobOrderId(id);


            for (int i = 0; i < pands.size(); i++) {
                Row row = sheet.createRow(rowIdx);
                Cell cellRawType = row.createCell(0);
                cellRawType.setCellValue(pands.get(i).getProjectName());
                cellRawType.setCellStyle(cellStyle);

                Cell projectCode = row.createCell(1);
                projectCode.setCellValue(pands.get(i).getProjectCode());
                projectCode.setCellStyle(cellStyle);

                Cell engName = row.createCell(2);
                engName.setCellValue(pands.get(i).getEngineerName());
                engName.setCellStyle(cellStyle);

                Cell pandCode = row.createCell(3);
                pandCode.setCellValue(pands.get(i).getPandCode());
                pandCode.setCellStyle(cellStyle);

                Cell description = row.createCell(4);
                description.setCellValue(pands.get(i).getDescription());
                description.setCellStyle(cellStyle);


                Cell unit = row.createCell(5);
                unit.setCellValue(pands.get(i).getUnit());
                unit.setCellStyle(cellStyle);

                Cell manufacturing = row.createCell(6);
                manufacturing.setCellValue(pands.get(i).getManufacturing());
                manufacturing.setCellStyle(cellStyle);

                Cell rawType = row.createCell(7);
                rawType.setCellValue(pands.get(i).getRawType());
                rawType.setCellStyle(cellStyle);

                Cell rawUsed = row.createCell(8);
                rawUsed.setCellValue(pands.get(i).getRawUsed());
                rawUsed.setCellStyle(cellStyle);

                Cell finishType = row.createCell(9);
                finishType.setCellValue(pands.get(i).getFinishType());
                finishType.setCellStyle(cellStyle);

                Cell thickness = row.createCell(10);
                thickness.setCellValue(pands.get(i).getThickness());
                thickness.setCellStyle(cellStyle);

                Cell quantity = row.createCell(11);
                quantity.setCellValue(pands.get(i).getQuantity());
                quantity.setCellStyle(cellStyle);

                Cell height = row.createCell(12);
                height.setCellValue(pands.get(i).getHeight());
                height.setCellStyle(cellStyle);

                Cell width = row.createCell(13);
                width.setCellValue(pands.get(i).getWidth());
                width.setCellStyle(cellStyle);

                Cell total = row.createCell(14);
                total.setCellValue(pands.get(i).getTotal());
                total.setCellStyle(cellStyle);

                Cell quantityUsedRaws = row.createCell(15);
                quantityUsedRaws.setCellValue(pands.get(i).getQuantityUsedRaws());
                quantityUsedRaws.setCellStyle(cellStyle);

                rowIdx++;
            }

            System.out.println("////////////////////////////");
//            sheet.autoSizeColumn(0);
//            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
//            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(8);
            sheet.autoSizeColumn(9);
            sheet.autoSizeColumn(15);

            System.err.println("rrrrrrrrrrrrrrrrrrrrrrrrrrr");
            ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
            workbook.write(fileOut);
            workbook.close();

            System.err.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzz");
            return fileOut;

        } catch (Exception e) {

            System.err.println("xxxxxxxxxxxxxxxxxxxxxxx");
            System.out.println("catch");
            e.printStackTrace();
        }
        return null;
    }

    public ResponseEntity<ExitJobOrder> updateChildPand(ExitJobOrder updatedJobOrder) {
        try {
            List<ExitJobOrder> jobOrder = exitJobOrderRepository.getByUniqueId(updatedJobOrder.getUniqueId());

            double total;
            DecimalFormat df = new DecimalFormat("#.###");

            if (updatedJobOrder.getUnit().equals("متر مربع")) {
                total = (Double.valueOf(updatedJobOrder.getHeight()) * Double.valueOf(updatedJobOrder.getWidth()) * Double.valueOf(updatedJobOrder.getQuantity())) / 10000;
            } else if (updatedJobOrder.getUnit().equals("متر طولى")) {
                total = (Double.valueOf(updatedJobOrder.getHeight()) * Double.valueOf(updatedJobOrder.getQuantity())) / 100;
            } else {
                total = Double.valueOf(updatedJobOrder.getQuantity());
            }

//        jobOrder.setJobOrderId(updatedJobOrder.getJobOrderId());
//        jobOrder.setProjectProfileId((updatedJobOrder.getProjectProfileId()));
//        jobOrder.setProjectCode(updatedJobOrder.getProjectCode());
//        jobOrder.setProjectName(updatedJobOrder.getProjectName());
//        jobOrder.setEngineerName(updatedJobOrder.getEngineerName());
//        jobOrder.setJobOrderType(updatedJobOrder.getJobOrderType());
//        jobOrder.setInstallationArea(updatedJobOrder.getInstallationArea());
//        jobOrder.setQuantityUsedRaws(updatedJobOrder.getQuantityUsedRaws());
//        jobOrder.setPandCode(updatedJobOrder.getPandCode());
//        jobOrder.setDescription(updatedJobOrder.getDescription());
//        jobOrder.setManufacturing(updatedJobOrder.getManufacturing());
//        jobOrder.setRawType(updatedJobOrder.getRawType());
//        jobOrder.setRawUsed(updatedJobOrder.getRawUsed());
//        jobOrder.setFinishType(updatedJobOrder.getFinishType());
            jobOrder.get(0).setThickness(updatedJobOrder.getThickness());
//        jobOrder.setBlockNumber(updatedJobOrder.getBlockNumber());
//        jobOrder.setFloor(updatedJobOrder.getFloor());
//        jobOrder.setOfficerName(updatedJobOrder.getOfficerName());
            jobOrder.get(0).setHeight(updatedJobOrder.getHeight());
            jobOrder.get(0).setWidth(updatedJobOrder.getWidth());
            jobOrder.get(0).setRepetition(updatedJobOrder.getRepetition());
            jobOrder.get(0).setQuantityUsedRaws(updatedJobOrder.getQuantityUsedRaws());
            jobOrder.get(0).setTotal(df.format(total));
            jobOrder.get(0).setUnit(updatedJobOrder.getUnit());
            jobOrder.get(0).setQuantity(updatedJobOrder.getQuantity());
            jobOrder.get(0).setQuantityDelivered(updatedJobOrder.getQuantityDelivered());


//        jobOrder.setAdditionalDescription(updatedJobOrder.getAdditionalDescription());

            exitJobOrderRepository.save(jobOrder.get(0));

            return new ResponseEntity<>(jobOrder.get(0), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        }

    }

    public ResponseEntity<List<ExitJobOrder>> getAllExitJobOrdersBySerial(String jobOrderid) {
        List<ExitJobOrder> exitJobOrders = exitJobOrderRepository.getByJobOrderIdAndSerial(jobOrderid);
        return new ResponseEntity<>(exitJobOrders, HttpStatus.OK);
    }

    public ResponseEntity<List<ExitJobOrder>> getAllExitJobOrdersBySpacificSerial(String serialNumber) {
        List<ExitJobOrder> exitJobOrders = exitJobOrderRepository.getBySerial(serialNumber);
        return new ResponseEntity<>(exitJobOrders, HttpStatus.OK);
    }
}
