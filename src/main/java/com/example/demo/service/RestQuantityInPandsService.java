package com.example.demo.service;

import com.aspose.cells.*;
import com.aspose.cells.Color;
import com.aspose.cells.Font;
import com.aspose.cells.Picture;
import com.example.demo.models.Pand;
import com.example.demo.models.PandsToJobOrder;
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
public class RestQuantityInPandsService {

    @Autowired
    ProjectProfileService projectProfileService;

    @Autowired
    PandsService pandsService;

    @Autowired
    PandsRepository pandsRepository;

    @Autowired
    PandsToJobOrderService pandsToJobOrderService;

    @Autowired
    PandsToJobOrderRepository pandsToJobOrderRepository;

    public ByteArrayInputStream buildReport(Long id) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("quantity by unit");
            sheet.setRightToLeft(true);

            XSSFPrintSetup printSetup = (XSSFPrintSetup) sheet.getPrintSetup();
            printSetup.setLandscape(true);

//            Font font = workbook.createFont();
//            font.setBold(true);
            // Header
            Row firstRow = sheet.createRow(0);
//            CellStyle headerCellStyle = workbook.createCellStyle();
//            headerCellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
//            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//            headerCellStyle.setFont(font);
//            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
//            headerCellStyle.setBorderTop(BorderStyle.THICK);
//            headerCellStyle.setBorderBottom(BorderStyle.THICK);
//            headerCellStyle.setBorderLeft(BorderStyle.THICK);
//            headerCellStyle.setBorderRight(BorderStyle.THICK);

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
//            sheet.setColumnWidth(15, widthInCharacters * 256);
//            sheet.setColumnWidth(16, widthInCharacters * 256);

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
//            Cell firstRow15 = firstRow.createCell(15);
//            Cell firstRow16 = firstRow.createCell(16);

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
            firstRow6.setCellStyle(cellStyle);
            firstRow.createCell(6).setCellValue("نوع الخامات فى البند");
            firstRow7.setCellStyle(cellStyle);
            firstRow.createCell(7).setCellValue("الخامة المستخدمة");
            firstRow8.setCellStyle(cellStyle);
            firstRow.createCell(8).setCellValue("نوع التشطيب");
            firstRow9.setCellStyle(cellStyle);
            firstRow.createCell(9).setCellValue("السمك");
            firstRow10.setCellStyle(cellStyle);
            firstRow.createCell(10).setCellValue("الطول");
            firstRow11.setCellStyle(cellStyle);
            firstRow.createCell(11).setCellValue("العرض");
            firstRow12.setCellStyle(cellStyle);
            firstRow.createCell(12).setCellValue("الوحدة");
            firstRow5.setCellStyle(cellStyle);

            firstRow.createCell(13).setCellValue("الكمية المتبقية");
            firstRow13.setCellStyle(cellStyle);

            firstRow.createCell(14).setCellValue("اجمالى الكميات");
            firstRow14.setCellStyle(cellStyle);
//
//            firstRow.createCell(15).setCellValue("أجمالى أوامر الشغل");
//            firstRow15.setCellStyle(cellStyle);
//
//            firstRow.createCell(16).setCellValue("المتبقى فى البند");
//            firstRow16.setCellStyle(cellStyle);

            int rowIdx = 1;

            List<String> units = pandsRepository.getAllUnits(id);


            for (int k = 0; k < units.size(); k++) {

                List<Pand> pands = pandsRepository.getPandByProjectIdGroupByUnit(id, units.get(k));
                Double result = 0.0;

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

                    Cell quantity = row.createCell(13);
                    quantity.setCellValue(pands.get(i).getRestQuantity());
                    quantity.setCellStyle(cellStyle);

                    rowIdx++;

                    result += pands.get(i).getRestQuantity();
                }

                Row rowResult = sheet.createRow(rowIdx);
//                Double result = pandsRepository.getSumByUnit(pands.get(i).getProjectCode(), pands.get(i).getUnit());

                Cell totalQuantity = rowResult.createCell(14);
                totalQuantity.setCellValue(result);
                totalQuantity.setCellStyle(cellStyle);

                rowIdx++;
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

        Style discriptionDataStyle = sheet.getCells().get("F3").getStyle();
        discriptionDataStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
        discriptionDataStyle.setVerticalAlignment(TextAlignmentType.CENTER);
        discriptionDataStyle.getFont().setSize(11);

        Style titleStyle = sheet.getCells().get("E3").getStyle();
        titleStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
        titleStyle.setVerticalAlignment(TextAlignmentType.CENTER);
        titleStyle.getFont().setSize(16);

        Style shadowStyle = workbook.createStyle();
        shadowStyle.setPattern(BackgroundType.SOLID);
        shadowStyle.setForegroundColor(Color.getDarkGray());
        shadowStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
        shadowStyle.getFont().setSize(11);

        sheet.getCells().get("A5").putValue("كود البند");
        sheet.getCells().get("A5").setStyle(tableHeaderStyle);

        sheet.getCells().get("B5").putValue("التوصيف");
        sheet.getCells().get("B5").setStyle(tableHeaderStyle);

        sheet.getCells().get("C5").putValue("التصنيع");
        sheet.getCells().get("C5").setStyle(tableHeaderStyle);

        sheet.getCells().get("D5").putValue("الخامة الفعلية");
        sheet.getCells().get("D5").setStyle(tableHeaderStyle);

        sheet.getCells().get("E5").putValue("الخامة المستخدمة");
        sheet.getCells().get("E5").setStyle(tableHeaderStyle);

        sheet.getCells().get("F5").putValue("نوع التشطيب");
        sheet.getCells().get("F5").setStyle(tableHeaderStyle);

        sheet.getCells().get("G5").putValue("السمك");
        sheet.getCells().get("G5").setStyle(tableHeaderStyle);

        sheet.getCells().get("H5").putValue("الطول");
        sheet.getCells().get("H5").setStyle(tableHeaderStyle);

        sheet.getCells().get("I5").putValue("العرض");
        sheet.getCells().get("I5").setStyle(tableHeaderStyle);

        sheet.getCells().get("J5").putValue("الوحدة");
        sheet.getCells().get("J5").setStyle(tableHeaderStyle);

        sheet.getCells().get("K5").putValue("الكمية المتبقية");
        sheet.getCells().get("K5").setStyle(tableHeaderStyle);

        sheet.getCells().get("L5").putValue("اجمالى الكميات");
        sheet.getCells().get("L5").setStyle(tableHeaderStyle);

        InputStream imageStream = new ClassPathResource("static/Hossam-Zeitoun-Logo-Black.png").getInputStream();

        // Add the image to the worksheet (X, Y coordinates in pixels)
        // Place the image inside the merged cells (A1:C5)
        int pictureIndex = sheet.getPictures().add(0, 10, imageStream);

        // Get the added picture
        Picture picture = sheet.getPictures().get(pictureIndex);

        // Optionally, set the picture to fit within the merged area
        picture.setPlacement(PlacementType.MOVE);
        picture.setWidthScale(20); // Scale the image to fit width
        picture.setHeightScale(10);

        Cells cells = sheet.getCells();
        cells.merge(1, 5, 2, 3);

        // Assign a value to the merged cell
//        cells.get(3, 4).setValue("اجمالى الكميات بالوحدات");
//        sheet.getCells().get("E3").setStyle(titleStyle);

        com.aspose.cells.Cell mergedCell = cells.get(1, 5);
        mergedCell.setValue("اجمالى الكميات بالوحدات");

        // Modify the style to set font size to 16
        Style style = mergedCell.getStyle();
        Font font = style.getFont();
        font.setSize(20);
        mergedCell.setStyle(style);

        int rowIdx = 7;

        List<String> units = pandsRepository.getAllUnits(id);

        for (int k = 0; k < units.size(); k++) {

            List<Pand> pands = pandsRepository.getPandByProjectIdGroupByUnit(id, units.get(k));
            Double result = 0.0;
            for (int i = 0; i < pands.size(); i++) {

                if (i == 0) {
                    sheet.getCells().get("A1").putValue("أسم المشروع: ");
                    sheet.getCells().get("A1").setStyle(discriptionDataStyle);

                    sheet.getCells().get("B1").putValue(pands.get(0).getProjectName());
                    sheet.getCells().get("B1").setStyle(discriptionDataStyle);

                    sheet.getCells().get("A3").putValue("كود المشروع: ");
                    sheet.getCells().get("A3").setStyle(discriptionDataStyle);

                    sheet.getCells().get("B3").putValue(pands.get(0).getProjectCode());
                    sheet.getCells().get("B3").setStyle(discriptionDataStyle);

                    sheet.getCells().get("D1").putValue("المهندس المسؤول: ");
                    sheet.getCells().get("D1").setStyle(discriptionDataStyle);

                    sheet.getCells().get("E1").putValue(pands.get(0).getEngineerName());
                    sheet.getCells().get("E1").setStyle(discriptionDataStyle);
                }

                sheet.getCells().get("A" + rowIdx).putValue(pands.get(i).getPandCode());
                sheet.getCells().get("A" + rowIdx).setStyle(discriptionDataStyle);

                sheet.getCells().get("B" + rowIdx).putValue(pands.get(i).getDescription());
                sheet.getCells().get("B" + rowIdx).setStyle(discriptionDataStyle);

                sheet.getCells().get("C" + rowIdx).putValue(pands.get(i).getManufacturing());
                sheet.getCells().get("C" + rowIdx).setStyle(discriptionDataStyle);

                sheet.getCells().get("D" + rowIdx).putValue(pands.get(i).getRawType());
                sheet.getCells().get("D" + rowIdx).setStyle(discriptionDataStyle);

                sheet.getCells().get("E" + rowIdx).putValue(pands.get(i).getRawUsed());
                sheet.getCells().get("E" + rowIdx).setStyle(discriptionDataStyle);

                sheet.getCells().get("F" + rowIdx).putValue(pands.get(i).getFinishType());
                sheet.getCells().get("F" + rowIdx).setStyle(discriptionDataStyle);

                sheet.getCells().get("G" + rowIdx).putValue(pands.get(i).getThickness());
                sheet.getCells().get("G" + rowIdx).setStyle(discriptionDataStyle);

                sheet.getCells().get("H" + rowIdx).putValue(pands.get(i).getHeight());
                sheet.getCells().get("H" + rowIdx).setStyle(discriptionDataStyle);

                sheet.getCells().get("I" + rowIdx).putValue(pands.get(i).getWidth());
                sheet.getCells().get("I" + rowIdx).setStyle(discriptionDataStyle);

                sheet.getCells().get("J" + rowIdx).putValue(pands.get(i).getUnit());
                sheet.getCells().get("J" + rowIdx).setStyle(discriptionDataStyle);

                sheet.getCells().get("K" + rowIdx).putValue(pands.get(i).getRestQuantity());
                sheet.getCells().get("K" + rowIdx).setStyle(discriptionDataStyle);


                result += pands.get(i).getRestQuantity();

                rowIdx++;
            }

            sheet.getCells().get("L" + rowIdx).putValue(result);
            sheet.getCells().get("L" + rowIdx).setStyle(shadowStyle);


            rowIdx++;
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
