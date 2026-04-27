package com.example.demo.service;

import com.aspose.cells.*;
import com.aspose.cells.Color;
import com.aspose.cells.Font;
import com.aspose.cells.Picture;
import com.example.demo.models.Pand;
import com.example.demo.models.PandsToJobOrder;
import com.example.demo.repository.ExitJobOrderRepository;
import com.example.demo.repository.PandsRepository;
import com.example.demo.repository.PandsToJobOrderRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFPrintSetup;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

@Service
public class RestQuantityForRaws {

    @Autowired
    PandsRepository pandsRepository;

    @Autowired
    PandsToJobOrderRepository pandsToJobOrderRepository;

    @Autowired
    ExitJobOrderRepository exitJobOrderRepository;

    public ByteArrayInputStream buildReport(Long id) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("rest quantity");
            sheet.setRightToLeft(true);

            XSSFPrintSetup printSetup = (XSSFPrintSetup) sheet.getPrintSetup();
            printSetup.setLandscape(true);

//            Font font = workbook.createFont();
//            font.setBold(true);
            // Header
            Row firstRow = sheet.createRow(0);

            CellStyle cellStyle = workbook.createCellStyle();
//            cellStyle.setFont(font);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);

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
            sheet.setColumnWidth(16, widthInCharacters * 256);

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
            Cell firstRow16 = firstRow.createCell(16);
            Cell firstRow17 = firstRow.createCell(17);

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

            firstRow.createCell(5).setCellValue("التصنيع");
            firstRow7.setCellStyle(cellStyle);
            firstRow.createCell(6).setCellValue("نوع الخامات فى البند");
            firstRow8.setCellStyle(cellStyle);
            firstRow.createCell(7).setCellValue("الخامة المستخدمة");
            firstRow9.setCellStyle(cellStyle);
            firstRow.createCell(8).setCellValue("نوع التشطيب");
            firstRow10.setCellStyle(cellStyle);
            firstRow.createCell(9).setCellValue("السمك");
            firstRow11.setCellStyle(cellStyle);
            firstRow.createCell(10).setCellValue("الطول");
            firstRow12.setCellStyle(cellStyle);
            firstRow.createCell(11).setCellValue("العرض");
            firstRow13.setCellStyle(cellStyle);

            firstRow.createCell(12).setCellValue("الوحدة");
            firstRow6.setCellStyle(cellStyle);

            firstRow.createCell(13).setCellValue("اجمالى البند");
            firstRow14.setCellStyle(cellStyle);

            firstRow.createCell(14).setCellValue("الكمية المتبقية");
            firstRow5.setCellStyle(cellStyle);

            firstRow.createCell(15).setCellValue("اجمالى أوامر الشغل");
            firstRow15.setCellStyle(cellStyle);

            firstRow.createCell(16).setCellValue("أجمالى الأنتاج التام");
            firstRow16.setCellStyle(cellStyle);

            firstRow.createCell(17).setCellValue("المتبقى فى أمر الشغل");
            firstRow17.setCellStyle(cellStyle);

            int rowIdx = 1;


            List<String> rawTypes = pandsRepository.getRawsPandByProjectId(id);


            for (int k = 0; k < rawTypes.size(); k++) {

                List<Pand> pands = pandsRepository.getPandByRawType(id, rawTypes.get(k));

                Double result1 = 0.0;
                Double result2 = 0.0;
                Double result3 = 0.0;
                Double result4 = 0.0;
                Double result5 = 0.0;

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

                    Cell discription = row.createCell(4);
                    discription.setCellValue(pands.get(i).getDescription());
                    discription.setCellStyle(cellStyle);

                    Cell manufacturing = row.createCell(5);
                    manufacturing.setCellValue(pands.get(i).getManufacturing());
                    manufacturing.setCellStyle(cellStyle);

                    Cell rawType = row.createCell(6);
                    rawType.setCellValue(pands.get(i).getRawType());
                    rawType.setCellStyle(cellStyle);

                    Cell rawUsed = row.createCell(7);
                    rawUsed.setCellValue(pands.get(i).getRawUsed());
                    rawUsed.setCellStyle(cellStyle);

                    Cell finishType = row.createCell(8);
                    finishType.setCellValue(pands.get(i).getFinishType());
                    finishType.setCellStyle(cellStyle);

                    Cell thickness = row.createCell(9);
                    thickness.setCellValue(pands.get(i).getThickness());
                    thickness.setCellStyle(cellStyle);

                    Cell height = row.createCell(10);
                    height.setCellValue(pands.get(i).getHeight());
                    height.setCellStyle(cellStyle);

                    Cell width = row.createCell(11);
                    width.setCellValue(pands.get(i).getWidth());
                    width.setCellStyle(cellStyle);

                    Cell unit = row.createCell(12);
                    unit.setCellValue(pands.get(i).getUnit());
                    unit.setCellStyle(cellStyle);

                    Cell mainQuantity = row.createCell(13);
                    mainQuantity.setCellValue(pands.get(i).getMainQuantity());
                    mainQuantity.setCellStyle(cellStyle);

                    result1 += pands.get(i).getMainQuantity();

                    Cell restQuantity = row.createCell(14);
                    restQuantity.setCellValue(pands.get(i).getRestQuantity());
                    restQuantity.setCellStyle(cellStyle);

                    result2 += pands.get(i).getRestQuantity();

                    Double restTotalAmount = pandsToJobOrderRepository.getSumByRawTypeAndThinckness(pands.get(i).getProjectProfileId(), pands.get(i).getPandCode(), pands.get(i).getRawType(), pands.get(i).getThickness());

                    if (restTotalAmount == null) {
                        restTotalAmount = 0.0;
                    }

                    Cell totalQuantity = row.createCell(15);
                    totalQuantity.setCellValue(restTotalAmount);
                    totalQuantity.setCellStyle(cellStyle);

                    result3 += restTotalAmount;

                    Double totalAmount = exitJobOrderRepository.getSumByRawTypeAndThinckness(pands.get(i).getProjectCode(), pands.get(i).getPandCode(), pands.get(i).getRawType(), pands.get(i).getThickness());

                    if (totalAmount == null) {
                        totalAmount = 0.0;
                    }

                    Cell finalProducted = row.createCell(16);
                    finalProducted.setCellValue(totalAmount);
                    finalProducted.setCellStyle(cellStyle);

                    result4 += totalAmount;


                    Cell restInJobOrder = row.createCell(17);
                    restInJobOrder.setCellValue(restTotalAmount - totalAmount);
                    restInJobOrder.setCellStyle(cellStyle);

                    result5 += (restTotalAmount - totalAmount);

                    rowIdx++;

                }

                Row rowResult = sheet.createRow(rowIdx);

                Cell totalMainQuantity = rowResult.createCell(13);
                totalMainQuantity.setCellValue(result1);
                totalMainQuantity.setCellStyle(cellStyle);

                Cell totalRestQuantity = rowResult.createCell(14);
                totalRestQuantity.setCellValue(result2);
                totalRestQuantity.setCellStyle(cellStyle);

                Cell totalQuantity = rowResult.createCell(15);
                totalQuantity.setCellValue(result3);
                totalQuantity.setCellStyle(cellStyle);

                Cell totalFinalProducted = rowResult.createCell(16);
                totalFinalProducted.setCellValue(result4);
                totalFinalProducted.setCellStyle(cellStyle);

                Cell totalRestInJobOrder = rowResult.createCell(17);
                totalRestInJobOrder.setCellValue(result5);
                totalRestInJobOrder.setCellStyle(cellStyle);

                rowIdx += 2;
            }

            System.out.println("////////////////////////////");
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);
            sheet.autoSizeColumn(6);
            sheet.autoSizeColumn(7);
            sheet.autoSizeColumn(8);
            sheet.autoSizeColumn(9);
            sheet.autoSizeColumn(10);
            sheet.autoSizeColumn(11);
            sheet.autoSizeColumn(12);
            sheet.autoSizeColumn(13);
            sheet.autoSizeColumn(14);
            sheet.autoSizeColumn(15);
            sheet.autoSizeColumn(16);
            sheet.autoSizeColumn(17);

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

    public InputStreamResource getPdf(Long id) throws Exception {

        com.aspose.cells.Workbook workbook = new com.aspose.cells.Workbook();
        WorksheetCollection worksheets = workbook.getWorksheets();
        Worksheet sheet = worksheets.get(0);
        sheet.setDisplayRightToLeft(true);

        PageSetup pageSetup = sheet.getPageSetup();
        pageSetup.setOrientation(PageOrientationType.LANDSCAPE);
        pageSetup.setFooter(1, "Page &P of &N");
        pageSetup.setFitToPagesWide(1); // Fit to 1 page width
        pageSetup.setFitToPagesTall(0); // Set to 0 for automatic height

        pageSetup.setTopMargin(1);
        pageSetup.setBottomMargin(1);
        pageSetup.setLeftMargin(1);
        pageSetup.setRightMargin(1);

        Style tableHeaderStyle = sheet.getCells().get("C1").getStyle();
        tableHeaderStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
        tableHeaderStyle.setVerticalAlignment(TextAlignmentType.CENTER);
//        tableHeaderStyle.getFont().setItalic(true);
        tableHeaderStyle.getFont().setSize(12);
        tableHeaderStyle.getFont().setBold(false);
        tableHeaderStyle.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());
        tableHeaderStyle.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());
        tableHeaderStyle.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());
        tableHeaderStyle.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());

//        Style tableHeaderStyle2 = sheet.getCells().get("C1").getStyle();
//        tableHeaderStyle2.setHorizontalAlignment(TextAlignmentType.CENTER);
//        tableHeaderStyle2.setVerticalAlignment(TextAlignmentType.CENTER);
//        tableHeaderStyle2.getFont().setItalic(true);
//        tableHeaderStyle2.getFont().setSize(9);
//        tableHeaderStyle2.getFont().setBold(false);
//        tableHeaderStyle2.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());
//        tableHeaderStyle2.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());
//        tableHeaderStyle2.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());
//        tableHeaderStyle2.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());

        Style discriptionDataStyle = sheet.getCells().get("F3").getStyle();
        discriptionDataStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
        discriptionDataStyle.setVerticalAlignment(TextAlignmentType.CENTER);
        discriptionDataStyle.getFont().setSize(11);


        Style shadowStyle = workbook.createStyle();
        shadowStyle.setPattern(BackgroundType.SOLID);
        shadowStyle.setForegroundColor(Color.getDarkGray());
        shadowStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
        shadowStyle.getFont().setSize(11);

        sheet.getCells().get("A5").putValue("كود البند");
        sheet.getCells().get("A5").setStyle(tableHeaderStyle);

        sheet.getCells().get("B5").putValue("التوصيف");
        sheet.getCells().get("B5").setStyle(tableHeaderStyle);

        sheet.getCells().get("C5").putValue("الخامة الفعلية");
        sheet.getCells().get("C5").setStyle(tableHeaderStyle);

//        sheet.getCells().get("D5").putValue("الخامة المستخدمة");
//        sheet.getCells().get("D5").setStyle(tableHeaderStyle);

        sheet.getCells().get("D5").putValue("نوع التشطيب");
        sheet.getCells().get("D5").setStyle(tableHeaderStyle);

        sheet.getCells().get("E5").putValue("السمك");
        sheet.getCells().get("E5").setStyle(tableHeaderStyle);

        sheet.getCells().get("F5").putValue("الطول");
        sheet.getCells().get("F5").setStyle(tableHeaderStyle);

        sheet.getCells().get("G5").putValue("العرض");
        sheet.getCells().get("G5").setStyle(tableHeaderStyle);

        sheet.getCells().get("H5").putValue("الوحدة");
        sheet.getCells().get("H5").setStyle(tableHeaderStyle);

        sheet.getCells().get("I5").putValue("اجمالى البند");
        sheet.getCells().get("I5").setStyle(tableHeaderStyle);

        sheet.getCells().get("J5").putValue("الكمية المتبقية");
        sheet.getCells().get("J5").setStyle(tableHeaderStyle);

        sheet.getCells().get("K5").putValue("ك أوامر الشغل");
        sheet.getCells().get("K5").setStyle(tableHeaderStyle);

        sheet.getCells().get("L5").putValue("ك الأنتاج التام");
        sheet.getCells().get("L5").setStyle(tableHeaderStyle);

        sheet.getCells().get("M5").putValue("رصيد أمر الشغل");
        sheet.getCells().get("M5").setStyle(tableHeaderStyle);

        InputStream imageStream = new ClassPathResource("static/Hossam-Zeitoun-Logo-Black.png").getInputStream();

        // Add the image to the worksheet (X, Y coordinates in pixels)
        // Place the image inside the merged cells (A1:C5)
        int pictureIndex = sheet.getPictures().add(0, 11, imageStream);

        // Get the added picture
        Picture picture = sheet.getPictures().get(pictureIndex);

        // Optionally, set the picture to fit within the merged area
        picture.setPlacement(PlacementType.MOVE);
        picture.setWidthScale(20); // Scale the image to fit width
        picture.setHeightScale(10);

        Cells cells = sheet.getCells();
        cells.merge(1, 7, 2, 4);

        // Assign a value to the merged cell
//        cells.get(3, 4).setValue("اجمالى الكميات بالوحدات");
//        sheet.getCells().get("E3").setStyle(titleStyle);

        com.aspose.cells.Cell mergedCell = cells.get(1, 7);
        mergedCell.setValue("تحليل الكميات المتبقية لكل خامة");

        // Modify the style to set font size to 16
        Style style = mergedCell.getStyle();
        Font font = style.getFont();
        font.setSize(20);
        mergedCell.setStyle(style);

        int rowIdx = 7;


        List<String> rawTypes = pandsRepository.getRawsPandByProjectId(id);

        for (int k = 0; k < rawTypes.size(); k++) {

            List<Pand> pands = pandsRepository.getPandByRawType(id, rawTypes.get(k));

            Double result1 = 0.0;
            Double result2 = 0.0;
            Double result3 = 0.0;
            Double result4 = 0.0;
            Double result5 = 0.0;

            for (int i = 0; i < pands.size(); i++) {

                if (i == 0) {
                    sheet.getCells().get("A1").putValue("أسم المشروع");
                    sheet.getCells().get("A1").setStyle(discriptionDataStyle);

                    sheet.getCells().get("B1").putValue(pands.get(0).getProjectName());
                    sheet.getCells().get("B1").setStyle(discriptionDataStyle);

                    sheet.getCells().get("A3").putValue("كود المشروع");
                    sheet.getCells().get("A3").setStyle(discriptionDataStyle);

                    sheet.getCells().get("B3").putValue(pands.get(0).getProjectCode());
                    sheet.getCells().get("B3").setStyle(discriptionDataStyle);

                    sheet.getCells().get("D1").putValue("المهندس المسؤول");
                    sheet.getCells().get("D1").setStyle(discriptionDataStyle);

                    sheet.getCells().get("F1").putValue(pands.get(0).getEngineerName());
                    sheet.getCells().get("F1").setStyle(discriptionDataStyle);
                }

                sheet.getCells().get("A" + rowIdx).putValue(pands.get(i).getPandCode());
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("A" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("A" + rowIdx).setStyle(discriptionDataStyle);
                }

                sheet.getCells().get("B" + rowIdx).putValue(pands.get(i).getDescription());
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("B" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("B" + rowIdx).setStyle(discriptionDataStyle);
                }


                sheet.getCells().get("C" + rowIdx).putValue(pands.get(i).getRawType());
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("C" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("C" + rowIdx).setStyle(discriptionDataStyle);
                }

//                sheet.getCells().get("D" + rowIdx).putValue(pands.get(i).getRawUsed());
//                if (rowIdx % 2 != 0) {
//                    sheet.getCells().get("D" + rowIdx).setStyle(shadowStyle);
//                } else {
//                    sheet.getCells().get("D" + rowIdx).setStyle(discriptionDataStyle);
//                }

                sheet.getCells().get("D" + rowIdx).putValue(pands.get(i).getFinishType());
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("D" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("D" + rowIdx).setStyle(discriptionDataStyle);
                }

                sheet.getCells().get("E" + rowIdx).putValue(pands.get(i).getThickness());
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("E" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("E" + rowIdx).setStyle(discriptionDataStyle);
                }


                sheet.getCells().get("F" + rowIdx).putValue(pands.get(i).getHeight());
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("F" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("F" + rowIdx).setStyle(discriptionDataStyle);
                }

                sheet.getCells().get("G" + rowIdx).putValue(pands.get(i).getWidth());
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("G" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("G" + rowIdx).setStyle(discriptionDataStyle);
                }

                sheet.getCells().get("H" + rowIdx).putValue(pands.get(i).getUnit());
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("H" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("H" + rowIdx).setStyle(discriptionDataStyle);
                }

                sheet.getCells().get("I" + rowIdx).putValue(pands.get(i).getMainQuantity());
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("I" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("I" + rowIdx).setStyle(discriptionDataStyle);
                }

                result1 += pands.get(i).getMainQuantity();

                sheet.getCells().get("J" + rowIdx).putValue(pands.get(i).getRestQuantity());
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("J" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("J" + rowIdx).setStyle(discriptionDataStyle);
                }

                result2 += pands.get(i).getRestQuantity();

                Double restTotalAmount = pandsToJobOrderRepository.getSumByRawTypeAndThinckness(pands.get(i).getProjectProfileId(), pands.get(i).getPandCode(), pands.get(i).getRawType(), pands.get(i).getThickness());

                if (restTotalAmount == null) {
                    restTotalAmount = 0.0;
                }

                sheet.getCells().get("K" + rowIdx).putValue(restTotalAmount);
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("K" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("K" + rowIdx).setStyle(discriptionDataStyle);
                }

                result3 += restTotalAmount;

                Double totalAmount = exitJobOrderRepository.getSumByRawTypeAndThinckness(pands.get(i).getProjectCode(), pands.get(i).getPandCode(), pands.get(i).getRawType(), pands.get(i).getThickness());

                if (totalAmount == null) {
                    totalAmount = 0.0;
                }

                sheet.getCells().get("L" + rowIdx).putValue(totalAmount);
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("L" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("L" + rowIdx).setStyle(discriptionDataStyle);
                }

                result4 += totalAmount;

                sheet.getCells().get("M" + rowIdx).putValue(restTotalAmount - totalAmount);
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("M" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("M" + rowIdx).setStyle(discriptionDataStyle);
                }

                result5 += (restTotalAmount - totalAmount);

                rowIdx++;

            }

            sheet.getCells().get("I" + rowIdx).putValue(result1);
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("I" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("I" + rowIdx).setStyle(discriptionDataStyle);
            }
            sheet.getCells().get("J" + rowIdx).putValue(result2);
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("J" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("J" + rowIdx).setStyle(discriptionDataStyle);
            }
            sheet.getCells().get("K" + rowIdx).putValue(result3);
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("K" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("K" + rowIdx).setStyle(discriptionDataStyle);
            }
            sheet.getCells().get("L" + rowIdx).putValue(result4);
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("L" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("L" + rowIdx).setStyle(discriptionDataStyle);
            }
            sheet.getCells().get("M" + rowIdx).putValue(result5);
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("M" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("M" + rowIdx).setStyle(discriptionDataStyle);
            }

            rowIdx += 2;
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

        return resource;
    }
}
