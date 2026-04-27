package com.example.demo.service;

import com.example.demo.models.ExitJobOrder;
import com.example.demo.models.JobOrder;
import com.example.demo.models.PandsToJobOrder;
import com.example.demo.models.ProjectProfile;
import com.example.demo.repository.ExitJobOrderRepository;
import com.example.demo.repository.JobOrderRepository;
import com.example.demo.repository.PandsToJobOrderRepository;
import com.example.demo.repository.ProjectProfileRepository;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFPrintSetup;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ExcelFileService {

    @Autowired
    PandsToJobOrderRepository pandsToJobOrderRepository;
    @Autowired
    JobOrderRepository jobOrderRepository;

    @Autowired
    ExitJobOrderRepository exitJobOrderRepository;

    @Autowired
    ProjectProfileRepository projectProfileRepository;

    public ByteArrayInputStream buildPandsToJobOrderExcel(String id) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("quantity by unit");
            sheet.setRightToLeft(true);

            XSSFPrintSetup printSetup = (XSSFPrintSetup) sheet.getPrintSetup();
            printSetup.setLandscape(true);
            Row firstRow = sheet.createRow(0);
            Row secondRow = sheet.createRow(1);
            Row thirdRow = sheet.createRow(2);
            Row forthRow = sheet.createRow(3);

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle cellStyleWithColor = workbook.createCellStyle();
            cellStyleWithColor.setAlignment(HorizontalAlignment.CENTER);
            cellStyleWithColor.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            cellStyleWithColor.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            List<PandsToJobOrder> pandsToJobOrders = pandsToJobOrderRepository.getByJobOrderId(id);
            JobOrder jobOrder = jobOrderRepository.getByJobOrderNumber(id);

            Cell cell2 = firstRow.createCell(0);
            cell2.setCellValue("أمر شغل");
            cell2.setCellStyle(cellStyle);

            Cell cell3 = firstRow.createCell(1);
            cell3.setCellValue(id);
            cell3.setCellStyle(cellStyle);

            Cell cell4 = firstRow.createCell(3);
                cell4.setCellValue("منطقة التركيب");
            cell4.setCellStyle(cellStyle);

            Cell cell7 = firstRow.createCell(4);
            cell7.setCellValue(pandsToJobOrders.get(0).getInstallationArea());
            cell7.setCellStyle(cellStyle);

            Cell cell10 = secondRow.createCell(0);
            cell10.setCellValue("نوع");
            cell10.setCellStyle(cellStyle);


            Cell cell11 = secondRow.createCell(1);
            cell11.setCellValue(pandsToJobOrders.get(0).getJobOrderType());
            cell11.setCellStyle(cellStyle);


            Cell floor = secondRow.createCell(3);
            floor.setCellValue("الدور");
            floor.setCellStyle(cellStyle);

            Cell floorNum = secondRow.createCell(4);
            floorNum.setCellValue(pandsToJobOrders.get(0).getFloor());
            floorNum.setCellStyle(cellStyle);


            Cell projectCode = secondRow.createCell(6);
            projectCode.setCellValue("كود المشروع");
            projectCode.setCellStyle(cellStyle);

            Cell projectCodeNum = secondRow.createCell(7);
            projectCodeNum.setCellValue(pandsToJobOrders.get(0).getProjectCode());
            projectCodeNum.setCellStyle(cellStyle);

            Cell creation = thirdRow.createCell(0);
            creation.setCellValue("الأنشاء");
            creation.setCellStyle(cellStyle);

            Cell creationDate = thirdRow.createCell(1);
            creationDate.setCellValue(jobOrder.getJobOrderDate() + "  " + jobOrder.getJobOrderTime());
            creationDate.setCellStyle(cellStyle);

            Cell block = thirdRow.createCell(3);
            block.setCellValue("البلوك");
            block.setCellStyle(cellStyle);

            Cell blockNum = thirdRow.createCell(4);
            blockNum.setCellValue(pandsToJobOrders.get(0).getBlockNumber());
            blockNum.setCellStyle(cellStyle);

            Cell eng = thirdRow.createCell(6);
            eng.setCellValue("أسم المهندس");
            eng.setCellStyle(cellStyle);

            Cell engName = thirdRow.createCell(7);
            engName.setCellValue(pandsToJobOrders.get(0).getEngineerName());
            engName.setCellStyle(cellStyle);

            Cell print = forthRow.createCell(0);
            print.setCellValue("الطباعة");
            print.setCellStyle(cellStyle);

            Date dNow = new Date();
            SimpleDateFormat ft =
                    new SimpleDateFormat("hh:mm:ss a");

            DateFormat formatter1 = new SimpleDateFormat("dd.MM.yy");

            Cell printDate = forthRow.createCell(1);
            printDate.setCellValue(formatter1.format(dNow) + "  " + ft.format(dNow).toString());
            printDate.setCellStyle(cellStyle);

            Row sixthRow = sheet.createRow(6);

            Cell firstRow01 = sixthRow.createCell(0);
            Cell firstRow1 = firstRow.createCell(1);
            Cell firstRow2 = sixthRow.createCell(2);
            Cell firstRow3 = sixthRow.createCell(3);
            Cell firstRow4 = sixthRow.createCell(4);
            Cell firstRow5 = sixthRow.createCell(5);
            Cell firstRow6 = sixthRow.createCell(6);
            Cell firstRow7 = sixthRow.createCell(7);
            Cell firstRow8 = sixthRow.createCell(8);
            Cell firstRow9 = sixthRow.createCell(9);
            Cell firstRow10 = sixthRow.createCell(10);
            Cell firstRow11 = sixthRow.createCell(11);
            Cell firstRow12 = sixthRow.createCell(12);


            sixthRow.createCell(0).setCellValue("م");
            firstRow01.setCellStyle(cellStyle);

            sixthRow.createCell(1).setCellValue("كود البند");
            firstRow1.setCellStyle(cellStyle);

            sixthRow.createCell(2).setCellValue("ملاحظات فنية");
            firstRow3.setCellStyle(cellStyle);

            sixthRow.createCell(3).setCellValue("التوصيف");
            firstRow2.setCellStyle(cellStyle);

            sixthRow.createCell(4).setCellValue("التصنيع");
            firstRow2.setCellStyle(cellStyle);

            sixthRow.createCell(5).setCellValue("كود التصنيع");
            firstRow3.setCellStyle(cellStyle);

            sixthRow.createCell(6).setCellValue("الوحدة");
            firstRow4.setCellStyle(cellStyle);

            sixthRow.createCell(7).setCellValue("الخامة الفعلية");
            firstRow5.setCellStyle(cellStyle);

            sixthRow.createCell(8).setCellValue("نوع التشطيب");
            firstRow6.setCellStyle(cellStyle);

            sixthRow.createCell(9).setCellValue("العدد");
            firstRow7.setCellStyle(cellStyle);

            sixthRow.createCell(10).setCellValue("الطول");
            firstRow8.setCellStyle(cellStyle);

            sixthRow.createCell(11).setCellValue("العرض");
            firstRow9.setCellStyle(cellStyle);

            sixthRow.createCell(12).setCellValue("السمك");
            firstRow10.setCellStyle(cellStyle);

            sixthRow.createCell(13).setCellValue("التكرار");
            firstRow11.setCellStyle(cellStyle);

            sixthRow.createCell(14).setCellValue("الاجمالى");
            firstRow12.setCellStyle(cellStyle);

            sixthRow.createCell(15).setCellValue("الاجمالى المتبقى");
            firstRow12.setCellStyle(cellStyle);

            sixthRow.createCell(16).setCellValue("الكمية المتبقية فى البند");
            firstRow12.setCellStyle(cellStyle);

            int rowIdx = 7;

            List<String> units = pandsToJobOrderRepository.getAllUnits(id);

            int count = 1;
            for (int k = 0; k < units.size(); k++) {

                List<PandsToJobOrder> pands = pandsToJobOrderRepository.getPandByProjectIdGroupByUnit(units.get(k), id);
                Double result = 0.0;

                Double quantityTotal = 0.0;

                for (int i = 0; i < pands.size(); i++) {

                    Row row = sheet.createRow(rowIdx);
                    Cell cellRawType = row.createCell(0);
                    cellRawType.setCellValue(count);
                    cellRawType.setCellStyle(cellStyle);

                    Cell pandCode = row.createCell(1);
                    pandCode.setCellValue(pands.get(i).getPandCode());
                    pandCode.setCellStyle(cellStyle);

                    Cell additionalDescription = row.createCell(2);
                    additionalDescription.setCellValue(pands.get(i).getAdditionalDescription());
                    additionalDescription.setCellStyle(cellStyle);

                    Cell desc = row.createCell(3);
                    desc.setCellValue(pands.get(i).getDescription());
                    desc.setCellStyle(cellStyle);

                    Cell manufacturing = row.createCell(4);
                    manufacturing.setCellValue(pands.get(i).getManufacturing());
                    manufacturing.setCellStyle(cellStyle);


                    Cell manufacturingCode = row.createCell(5);
                    manufacturingCode.setCellValue(pands.get(i).getManufacturingCode());
                    manufacturingCode.setCellStyle(cellStyle);


                    Cell unit = row.createCell(6);
                    unit.setCellValue(pands.get(i).getUnit());
                    unit.setCellStyle(cellStyle);

                    Cell rawType = row.createCell(7);
                    rawType.setCellValue(pands.get(i).getRawType());
                    rawType.setCellStyle(cellStyle);

                    Cell finishType = row.createCell(8);
                    finishType.setCellValue(pands.get(i).getFinishType());
                    finishType.setCellStyle(cellStyle);

                    Cell quantity = row.createCell(9);
                    quantity.setCellValue(pands.get(i).getMainQuantity());
                    quantity.setCellStyle(cellStyle);


                    Cell height = row.createCell(10);
                    height.setCellValue(pands.get(i).getHeight());
                    height.setCellStyle(cellStyle);

                    Cell width = row.createCell(11);
                    width.setCellValue(pands.get(i).getWidth());
                    width.setCellStyle(cellStyle);

                    Cell thickness = row.createCell(12);
                    thickness.setCellValue(pands.get(i).getThickness());
                    thickness.setCellStyle(cellStyle);

                    Cell repetition = row.createCell(13);
                    repetition.setCellValue(pands.get(i).getRepetition());
                    repetition.setCellStyle(cellStyle);

                    Cell mainTotal = row.createCell(14);
                    mainTotal.setCellValue(pands.get(i).getMainTotal());
                    mainTotal.setCellStyle(cellStyle);

                    Cell total = row.createCell(15);
                    total.setCellValue(pands.get(i).getTotal());
                    total.setCellStyle(cellStyle);

                    Cell quantityInPand = row.createCell(16);
                    quantityInPand.setCellValue(pands.get(i).getQuantityInPand());
                    quantityInPand.setCellStyle(cellStyle);

                    rowIdx++;

                    result += pands.get(i).getMainQuantity();

                    quantityTotal += Double.valueOf(pands.get(i).getMainTotal());

                    count++;

                }

                Row rowResult = sheet.createRow(rowIdx);
//                Double result = pandsRepository.getSumByUnit(pands.get(i).getProjectCode(), pands.get(i).getUnit());

                Cell finalQuantity = rowResult.createCell(9);
                finalQuantity.setCellValue(result);
                finalQuantity.setCellStyle(cellStyleWithColor);

                Cell finalTotal = rowResult.createCell(14);
                finalTotal.setCellValue(quantityTotal);
                finalTotal.setCellStyle(cellStyleWithColor);

                rowIdx+=2;
            }


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

            System.err.println("rrrrrrrrrrrrrrrrrrrrrrrrrrr");
            ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
            workbook.write(fileOut);
            workbook.close();

            System.err.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzz");
            return new ByteArrayInputStream(fileOut.toByteArray());

        } catch (Exception e) {

            System.err.println("xxxxxxxxxxxxxxxxxxxxxxx");
            System.out.println("catch");
            e.printStackTrace();
        }
        return null;
    }

    public ByteArrayInputStream buildExcelExitJobOrderBySerial(String serial) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("quantity by unit");
            sheet.setRightToLeft(true);

            XSSFPrintSetup printSetup = (XSSFPrintSetup) sheet.getPrintSetup();
            printSetup.setLandscape(true);
            Row firstRow = sheet.createRow(0);
            Row secondRow = sheet.createRow(1);
            Row thirdRow = sheet.createRow(2);
            Row forthRow = sheet.createRow(3);

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle cellStyleWithColor = workbook.createCellStyle();
            cellStyleWithColor.setAlignment(HorizontalAlignment.CENTER);
            cellStyleWithColor.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            cellStyleWithColor.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            List<ExitJobOrder> allExitJobOrder = exitJobOrderRepository.getBySerial(serial);
            JobOrder jobOrder = jobOrderRepository.getByJobOrderNumber(allExitJobOrder.get(0).getJobOrderId());

            Cell cell2 = firstRow.createCell(0);
            cell2.setCellValue("عرض فنى");
            cell2.setCellStyle(cellStyle);

            Cell cell3 = firstRow.createCell(1);
            cell3.setCellValue(jobOrder.getJobOrderNumber());
            cell3.setCellStyle(cellStyle);

            Cell cell4 = firstRow.createCell(3);
            cell4.setCellValue("كود المشروع");
            cell4.setCellStyle(cellStyle);

            Cell cell7 = firstRow.createCell(4);
            cell7.setCellValue(allExitJobOrder.get(0).getProjectName() + "  " + allExitJobOrder.get(0).getProjectCode());
            cell7.setCellStyle(cellStyle);

            Cell serialCount = firstRow.createCell(6);
            serialCount.setCellValue("مسلسل");
            serialCount.setCellStyle(cellStyle);

            ProjectProfile projectProfile = projectProfileRepository.getById(allExitJobOrder.get(0).getProjectProfileId());
            Cell serialCountNum = firstRow.createCell(7);
            serialCountNum.setCellValue(projectProfile.getSerial()-1);
            serialCountNum.setCellStyle(cellStyle);

            Cell cell10 = secondRow.createCell(0);
            cell10.setCellValue("تاريخ الاصدار");
            cell10.setCellStyle(cellStyle);


            Cell cell11 = secondRow.createCell(1);
            cell11.setCellValue(jobOrder.getJobOrderDate() + "  " + jobOrder.getJobOrderTime());
            cell11.setCellStyle(cellStyle);


            Cell floor = secondRow.createCell(3);
            floor.setCellValue("أسم الفراغ");
            floor.setCellStyle(cellStyle);

            Cell floorNum = secondRow.createCell(4);
            floorNum.setCellValue(allExitJobOrder.get(0).getInstallationArea());
            floorNum.setCellStyle(cellStyle);


            Cell projectCodeNum = thirdRow.createCell(7);
            projectCodeNum.setCellValue(serial);
            projectCodeNum.setCellStyle(cellStyle);

            Cell creation = thirdRow.createCell(0);
            creation.setCellValue("مكتب فنى");
            creation.setCellStyle(cellStyle);

            Cell creationDate = thirdRow.createCell(1);
            creationDate.setCellValue(allExitJobOrder.get(0).getOfficerName());
            creationDate.setCellStyle(cellStyle);

            Cell block = thirdRow.createCell(3);
            block.setCellValue("أسم الدور");
            block.setCellStyle(cellStyle);

            Cell blockNum = thirdRow.createCell(4);
            blockNum.setCellValue(allExitJobOrder.get(0).getFloor());
            blockNum.setCellStyle(cellStyle);

            Cell unifiedSerial = forthRow.createCell(7);
            unifiedSerial.setCellValue(allExitJobOrder.get(0).getUnifiedSerial());
            unifiedSerial.setCellStyle(cellStyle);


            Row sixthRow = sheet.createRow(6);

            Cell firstRow01 = sixthRow.createCell(0);
            Cell firstRow1 = sixthRow.createCell(1);
            Cell firstRow2 = sixthRow.createCell(2);
            Cell firstRow3 = sixthRow.createCell(3);
            Cell firstRow4 = sixthRow.createCell(4);
            Cell firstRow5 = sixthRow.createCell(5);
            Cell firstRow6 = sixthRow.createCell(6);
            Cell firstRow7 = sixthRow.createCell(7);
            Cell firstRow8 = sixthRow.createCell(8);
            Cell firstRow9 = sixthRow.createCell(9);
            Cell firstRow10 = sixthRow.createCell(10);
            Cell firstRow11 = sixthRow.createCell(11);
            Cell firstRow12 = sixthRow.createCell(12);


            sixthRow.createCell(0).setCellValue("م");
            firstRow01.setCellStyle(cellStyle);

            sixthRow.createCell(1).setCellValue("البند");
            firstRow1.setCellStyle(cellStyle);

            sixthRow.createCell(2).setCellValue("أسم الخامة");
            firstRow2.setCellStyle(cellStyle);

            sixthRow.createCell(3).setCellValue("التوصيف");
            firstRow3.setCellStyle(cellStyle);

            sixthRow.createCell(4).setCellValue("المنطقة");
            firstRow4.setCellStyle(cellStyle);

            sixthRow.createCell(5).setCellValue("العدد");
            firstRow5.setCellStyle(cellStyle);

            sixthRow.createCell(6).setCellValue("الطول");
            firstRow6.setCellStyle(cellStyle);

            sixthRow.createCell(7).setCellValue("العرض");
            firstRow7.setCellStyle(cellStyle);

            sixthRow.createCell(8).setCellValue("السمك");
            firstRow8.setCellStyle(cellStyle);

            sixthRow.createCell(9).setCellValue("المعالجة");
            firstRow9.setCellStyle(cellStyle);

            sixthRow.createCell(10).setCellValue("البلوك");
            firstRow10.setCellStyle(cellStyle);

            sixthRow.createCell(11).setCellValue("الكمية");
            firstRow11.setCellStyle(cellStyle);

            sixthRow.createCell(12).setCellValue("الوحدة");
            firstRow12.setCellStyle(cellStyle);


            int rowIdx = 7;

            List<ExitJobOrder> jobOrdersByRawType = exitJobOrderRepository.jobOrdersByRawType
                    (allExitJobOrder.get(0).getProjectCode()
                            , allExitJobOrder.get(0).getJobOrderId()
                            , serial);

            List<ExitJobOrder> jobOrdersByThickness = new ArrayList<>();

            int count = 1;
            int i = 0;


            for (int k = 0; k < jobOrdersByRawType.size(); k++) {

                jobOrdersByThickness.addAll(exitJobOrderRepository.jobOrdersByUnit(allExitJobOrder.get(0).getProjectCode()
                        , allExitJobOrder.get(0).getJobOrderId()
                        , jobOrdersByRawType.get(k).getUnit()
                        , serial));

                double totalQuantity = 0;
                double total = 0.0;

                for (; i < jobOrdersByThickness.size(); i++) {

                    Row row = sheet.createRow(rowIdx);
                    Cell cellRawType = row.createCell(0);
                    cellRawType.setCellValue(count);
                    cellRawType.setCellStyle(cellStyle);

                    Cell pandCode = row.createCell(1);
                    pandCode.setCellValue(jobOrdersByThickness.get(i).getPandCode());
                    pandCode.setCellStyle(cellStyle);

                    Cell rawType = row.createCell(2);
                    rawType.setCellValue(jobOrdersByThickness.get(i).getRawType());
                    rawType.setCellStyle(cellStyle);


                    Cell description = row.createCell(3);
                    description.setCellValue(jobOrdersByThickness.get(i).getDescription());
                    description.setCellStyle(cellStyle);


                    Cell place = row.createCell(4);
                    place.setCellValue("         ");
                    place.setCellStyle(cellStyle);

                    Cell quantity = row.createCell(5);
                    quantity.setCellValue(jobOrdersByThickness.get(i).getQuantity());
                    quantity.setCellStyle(cellStyle);

                    Cell height = row.createCell(6);
                    height.setCellValue(jobOrdersByThickness.get(i).getHeight());
                    height.setCellStyle(cellStyle);

                    Cell width = row.createCell(7);
                    width.setCellValue(jobOrdersByThickness.get(i).getWidth());
                    width.setCellStyle(cellStyle);


                    Cell thickness = row.createCell(8);
                    thickness.setCellValue(jobOrdersByThickness.get(i).getThickness());
                    thickness.setCellStyle(cellStyle);

                    Cell finishType = row.createCell(9);
                    finishType.setCellValue(jobOrdersByThickness.get(i).getFinishType());
                    finishType.setCellStyle(cellStyle);

                    Cell blockNumber = row.createCell(10);
                    blockNumber.setCellValue(jobOrdersByThickness.get(i).getBlockNumber());
                    blockNumber.setCellStyle(cellStyle);


                    double result = 0.0;
                    String unit = "";
                    DecimalFormat df = new DecimalFormat("#,###.000");
                    String formattedNumber = "";
                    if (jobOrdersByThickness.get(i).getUnit().equals("متر طولى")) {
                        result = (Double.valueOf(jobOrdersByThickness.get(i).getHeight())) *
                                (Double.valueOf(jobOrdersByThickness.get(i).getQuantity()));
                        formattedNumber = df.format(result / 100);
                        unit = "متر طولى";
//                    sheet.getCells().get("L" + rowIdx).putValue(formattedNumber);
//                    sheet.getCells().get("M" + rowIdx).putValue(unit);
                    } else if (jobOrdersByThickness.get(i).getUnit().equals("متر مربع")) {
                        result = (Double.valueOf(jobOrdersByThickness.get(i).getHeight())) *
                                (Double.valueOf(jobOrdersByThickness.get(i).getWidth())) *
                                (Double.valueOf(jobOrdersByThickness.get(i).getQuantity()));
//                    row.createCell(13).setCellValue((result)/10000);
                        formattedNumber = df.format(result / 10000);
                        unit = "متر مربع";
//                    sheet.getCells().get("L" + rowIdx).putValue(formattedNumber + " " + unit);
//                    sheet.getCells().get("L" + rowIdx).putValue(formattedNumber);
//                    sheet.getCells().get("M" + rowIdx).putValue(unit);
                    } else {
                        unit = "وحدة";
                        formattedNumber = String.valueOf(jobOrdersByThickness.get(i).getQuantity());
                    }

                    total += Double.valueOf(formattedNumber);

                    Cell totalVal = row.createCell(11);
                    totalVal.setCellValue(formattedNumber);
                    totalVal.setCellStyle(cellStyle);


                    Cell unitVal = row.createCell(12);
                    unitVal.setCellValue(jobOrdersByThickness.get(i).getUnit());
                    unitVal.setCellStyle(cellStyle);


                    rowIdx++;

                    totalQuantity += jobOrdersByThickness.get(i).getQuantity();

                    count++;

                }

                Row rowResult = sheet.createRow(rowIdx);
//                Double result = pandsRepository.getSumByUnit(pands.get(i).getProjectCode(), pands.get(i).getUnit());

                Cell finalQuantity = rowResult.createCell(5);
                finalQuantity.setCellValue(totalQuantity);
                finalQuantity.setCellStyle(cellStyleWithColor);


                Cell finalTotal = rowResult.createCell(11);
                finalTotal.setCellValue(total);
                finalTotal.setCellStyle(cellStyleWithColor);

                rowIdx+=2;
            }


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

            System.err.println("rrrrrrrrrrrrrrrrrrrrrrrrrrr");
            ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
            workbook.write(fileOut);
            workbook.close();

            System.err.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzz");
            return new ByteArrayInputStream(fileOut.toByteArray());

        } catch (Exception e) {

            System.err.println("xxxxxxxxxxxxxxxxxxxxxxx");
            System.out.println("catch");
            e.printStackTrace();
        }
        return null;
    }


}
