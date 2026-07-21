package com.example.demo.service;

import com.aspose.cells.*;
import com.aspose.cells.Color;
import com.aspose.cells.Picture;
import com.example.demo.models.*;
import com.example.demo.payload.CheckLimitResponse;
import com.example.demo.repository.ExitJobOrderRepository;
import com.example.demo.repository.PandsRepository;
import com.example.demo.repository.PandsToJobOrderRepository;
import com.example.demo.repository.ProjectProfileRepository;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PandsService {

    @Autowired
    PandsRepository pandsRepository;
    @Autowired
    PandsToJobOrderRepository pandsToJobOrderRepository;

    @Autowired
    ProjectProfileRepository projectProfileRepository;

    @Autowired
    ExitJobOrderRepository exitJobOrderRepository;

    @Autowired
    private ChangeHistoryLog changeHistoryLog;

    @Autowired
    private FileStorageService storageService;

    public ResponseEntity<List<Pand>> getAllPands(Long id) {
        try {
            List<Pand> pands = pandsRepository.findByProjectProfileId(id);

            if (pands.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(pands, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Pand getPandByPandCode(String pandCode, Long projectId) {
        Pand pands = pandsRepository.findByPandCodeAndProjectProfileId(pandCode, projectId);

        return pands;
    }

//    @Cacheable(value = "PAND",key = "#id")
    public Optional<Pand> getPandById(Long id) {
        Optional<Pand> pands = pandsRepository.findById(id);

//        String fileurl = storageService.getFileByPandId(id);
//        pands.get().setFileDB(fileurl);

        return pands;
    }

//    @Cacheable(value = "PROJECT_PROFILE",key = "#id")
    public List<Pand> getPandByProjectId(Long id) {
        List<Pand> pands = pandsRepository.findByProjectProfileId(id);

        return pands;
    }

//    @CachePut(value = "PAND",key = "#id")
    public Pand addNewPand(
            Pand pand,
            HttpServletRequest request) throws SQLException {
        try {
            double total;
            DecimalFormat df = new DecimalFormat("#.###");
            ProjectProfile projectProfile = projectProfileRepository.getById(pand.getProjectProfileId());

            String height;
            String width;
            if (pand.getHeight() == null) {
                height = "1";
            } else {
                height = pand.getHeight();
            }

            if (pand.getWidth() == null) {
                width = "1";
            } else {
                width = pand.getWidth();
            }


//            NumberFormat format = NumberFormat.getInstance(Locale.US);  // Use the appropriate Locale


            if (pand.getUnit().equals("متر مربع")) {
                total = (Double.parseDouble(height) * Double.parseDouble(width) * pand.getMainQuantity()) / 10000;
            } else if (pand.getUnit().equals("متر طولى")) {
                total = (Double.parseDouble(height) * pand.getMainQuantity()) / 100;
            } else {
                total = pand.getMainQuantity();
            }
//            Number number = format.parse(String.valueOf(total));
//            double value = number.doubleValue();
            String formattedNumber = df.format(total);
            pand.setTotal(Double.parseDouble(formattedNumber));
            pand.setProjectProfileId(projectProfile.getId());
            pand.setRestQuantity(pand.getMainQuantity()); // restQuantity
            pand.setTotalQuantity(pand.getMainQuantity());
            pand.setProjectCode(projectProfile.getProjectCode());
            pand.setProjectName(projectProfile.getProjectName());
            String pandCode = pand.getPandCode();

            if (pandCode.contains("/")) {
                pandCode = pandCode.replace("/", "-");
                pand.setPandCode(pandCode);
            }

            pand.setPandCode(pandCode.trim());

            pand.setAdditionalQuantity(0);

//            projectProfile.getPands().add(pand);
//            projectProfileRepository.save(projectProfile);

            changeHistoryLog.saveChange(pandCode,pand.toString(),pand.toString(),"save",request);

            pandsRepository.save(pand);

            return pand;
        } catch (Exception e) {
            e.printStackTrace();
            return pand;
        }
    }

//    @CachePut(value = "PAND",key = "#result.id")
    public Pand updatePand(Long id, Pand updatedPand, HttpServletRequest request) throws ResourceNotFoundException, SQLException {
        Optional<Pand> pand = getPandById(id);
        try {

            String requestMessage = pand.toString();
            DecimalFormat df = new DecimalFormat("#.###");


            LocalDateTime myDateObj = LocalDateTime.now();
            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = myDateObj.format(myFormatObj);

            String height;
            String width;
            if (updatedPand.getHeight() == null) {
                height = "1";
            } else {
                height = updatedPand.getHeight();
            }

            if (updatedPand.getWidth() == null) {
                width = "1";
            } else {
                width = updatedPand.getWidth();
            }

            pand.get().setUpdatedDate(formattedDate);
//            pand.get().setPandCode(updatedPand.getPandCode());
            pand.get().setUnit(updatedPand.getUnit());

            double mainQuantity = 0.0;

            double total;

            if (updatedPand.getUnit().equals("متر مربع")) {
                total = (Double.parseDouble(height) * Double.parseDouble(width) * updatedPand.getMainQuantity()) / 10000;
            } else if (updatedPand.getUnit().equals("متر طولى")) {
                total = (Double.parseDouble(height) * updatedPand.getMainQuantity()) / 100;
            } else {
                total = updatedPand.getMainQuantity();
            }

//            String formattedNumber = df.format(total);

            if (updatedPand.getAdditionalQuantity() != pand.get().getAdditionalQuantity()) {

                Date dNow = new Date();
                SimpleDateFormat ft =
                        new SimpleDateFormat("hh:mm:ss a");
                mainQuantity = Double.parseDouble(df.format(pand.get().getTotalQuantity() + updatedPand.getAdditionalQuantity()));
                pand.get().setTotalQuantity(mainQuantity);
                pand.get().setAdditionalQuantityDate(formattedDate);
                pand.get().setAdditionalQuantity(pand.get().getAdditionalQuantity() + updatedPand.getAdditionalQuantity());
                pand.get().setAdditionalReason(updatedPand.getAdditionalReason());
                pand.get().setAdditionalBy(updatedPand.getAdditionalBy());

                pand.get().setRestQuantity(Double.parseDouble(df.format(pand.get().getRestQuantity() + updatedPand.getAdditionalQuantity())));
            }

            pand.get().setDescription(updatedPand.getDescription());
            pand.get().setAdditionalDescription(updatedPand.getAdditionalDescription());
            pand.get().setProjectProfileId(updatedPand.getProjectProfileId());
            pand.get().setManufacturing(updatedPand.getManufacturing());
            pand.get().setRawType(updatedPand.getRawType());
            pand.get().setRawUsed(updatedPand.getRawUsed());
            pand.get().setFinishType(updatedPand.getFinishType());
            pand.get().setThickness(updatedPand.getThickness());
            pand.get().setHeight(height);
            pand.get().setWidth(width);
            pand.get().setRepetition(updatedPand.getRepetition());

            String pandCode = updatedPand.getPandCode();

            if (pandCode.contains("/")) {
                pandCode = pandCode.replace("/", "-");
            }

            List<ExitJobOrder> exitJobOrders = exitJobOrderRepository.getByProjectAndPandCode(pand.get().getProjectCode(),pand.get().getPandCode());
            for (ExitJobOrder exitJobOrder : exitJobOrders) {

                exitJobOrder.setPandCode(pandCode);

                exitJobOrderRepository.save(exitJobOrder);
            }

            List<PandsToJobOrder> pandsToJobOrders = pandsToJobOrderRepository.jobOrdersByPandCode(pand.get().getProjectProfileId(), pand.get().getPandCode());
            for (PandsToJobOrder pandsToJobOrder : pandsToJobOrders) {
                pandsToJobOrder.setPandCode(pandCode);
                pandsToJobOrder.setManufacturing(pand.get().getManufacturing());
                pandsToJobOrder.setRawType(pand.get().getRawType());
                pandsToJobOrder.setRawUsed(pand.get().getRawUsed());
                pandsToJobOrder.setFinishType(pand.get().getFinishType());
                pandsToJobOrder.setThickness(pand.get().getThickness());
                pandsToJobOrderRepository.save(pandsToJobOrder);
            }


            pand.get().setPandCode(pandCode);

            pandsRepository.save(pand.get());

            changeHistoryLog.saveChange(pandCode,requestMessage,pand.toString(),"update",request);

            return pand.get();
        } catch (Exception e) {
            e.printStackTrace();
            return pand.get();
        }
    }
//    @CacheEvict(value = "PAND", allEntries = true)
    public void deletePand(Long id) {
        Pand pand = pandsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("pand Not Found for ID: " + id));

        pandsRepository.deleteById(id);
    }

    public CheckLimitResponse checkLimit() {
        List<Pand> pands = pandsRepository.findAll();
        CheckLimitResponse checkLimitResponse = new CheckLimitResponse();
        for (int i = 0; i < pands.size(); i++) {
            if (pands.get(i).getRestQuantity() < 0) {
                checkLimitResponse.setMessage("تم تخطى الكمية المحدوده للبند رقم " + pands.get(i).getPandCode() + " للمشروع  " + pands.get(i).getProjectName());
                checkLimitResponse.setFlag(1);
                return checkLimitResponse;
            }
        }
        return new CheckLimitResponse("", 0);
    }


    public InputStreamResource getPandDetailsForEachJobOrder(String id, Long projectId) throws Exception {
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

            Date dNow = new Date();
            SimpleDateFormat ft =
                    new SimpleDateFormat("hh:mm:ss a");

            DateFormat formatter1 = new SimpleDateFormat("dd.MM.yy");

            Style discriptionDataStyle = sheet.getCells().get("F3").getStyle();
            discriptionDataStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
            discriptionDataStyle.setVerticalAlignment(TextAlignmentType.CENTER);
            discriptionDataStyle.getFont().setSize(11);

            Pand pand = pandsRepository.findByPandCodeAndProjectProfileId(id,projectId);

            sheet.getCells().get("A1").putValue("أسم المشروع");
            sheet.getCells().get("A1").setStyle(discriptionDataStyle);

            sheet.getCells().get("B1").putValue(pand.getProjectName());
            sheet.getCells().get("B1").setStyle(discriptionDataStyle);

            sheet.getCells().get("A3").putValue("كود المشروع");
            sheet.getCells().get("A3").setStyle(discriptionDataStyle);

            sheet.getCells().get("B3").putValue(pand.getProjectCode());
            sheet.getCells().get("B3").setStyle(discriptionDataStyle);

            sheet.getCells().get("A5").putValue("تاريخ الطباعه");
            sheet.getCells().get("A5").setStyle(discriptionDataStyle);

            sheet.getCells().get("B5").putValue(formatter1.format(dNow)+ " " + ft.format(dNow).toString());
            sheet.getCells().get("B5").setStyle(discriptionDataStyle);

            sheet.getCells().get("C1").putValue("كود البند");
            sheet.getCells().get("C1").setStyle(discriptionDataStyle);

            sheet.getCells().get("D1").putValue(pand.getPandCode());
            sheet.getCells().get("D1").setStyle(discriptionDataStyle);

            sheet.getCells().get("C3").putValue("الوحدة");
            sheet.getCells().get("C3").setStyle(discriptionDataStyle);

            sheet.getCells().get("D3").putValue(pand.getUnit());
            sheet.getCells().get("D3").setStyle(discriptionDataStyle);

            sheet.getCells().get("E1").putValue("كمية البند");
            sheet.getCells().get("E1").setStyle(discriptionDataStyle);

            sheet.getCells().get("F1").putValue(pand.getMainQuantity() + pand.getAdditionalQuantity());
            sheet.getCells().get("F1").setStyle(discriptionDataStyle);

            sheet.getCells().get("E3").putValue("المتبقى فى البند");
            sheet.getCells().get("E3").setStyle(discriptionDataStyle);

            sheet.getCells().get("F3").putValue(pand.getRestQuantity());
            sheet.getCells().get("F3").setStyle(discriptionDataStyle);

            Style shadowStyle = workbook.createStyle();
            shadowStyle.setPattern(BackgroundType.SOLID);
            shadowStyle.setForegroundColor(Color.getDarkGray());
            shadowStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
            shadowStyle.getFont().setSize(11);

//            sheet.getCells().get("A5").putValue("كود البند");
//            sheet.getCells().get("A5").setStyle(tableHeaderStyle);

//            sheet.getCells().get("B5").putValue("الخامات الفعلية");
//            sheet.getCells().get("B5").setStyle(tableHeaderStyle);
//
//            sheet.getCells().get("C5").putValue("العدد");
//            sheet.getCells().get("C5").setStyle(tableHeaderStyle);
//
//            sheet.getCells().get("D5").putValue("الكمية");
//            sheet.getCells().get("D5").setStyle(tableHeaderStyle);
//
//            sheet.getCells().get("E5").putValue("المتبقى فى البند");
//            sheet.getCells().get("E5").setStyle(tableHeaderStyle);
//
//            sheet.getCells().get("E5").putValue("الوحدة");
//            sheet.getCells().get("E5").setStyle(tableHeaderStyle);

            sheet.getCells().get("A7").putValue("أمر الشغل");
            sheet.getCells().get("A7").setStyle(tableHeaderStyle);

            sheet.getCells().get("B7").putValue("كمية أوامر الشغل");
            sheet.getCells().get("B7").setStyle(tableHeaderStyle);

            sheet.getCells().get("C7").putValue("الكمية المصروفة");
            sheet.getCells().get("C7").setStyle(tableHeaderStyle);

            sheet.getCells().get("D7").putValue("الرصيد المتبقى");
            sheet.getCells().get("D7").setStyle(tableHeaderStyle);
//
//            sheet.getCells().get("G5").putValue("نوع التشطيب");
//            sheet.getCells().get("G5").setStyle(tableHeaderStyle);
//
//            sheet.getCells().get("H5").putValue("السمك");
//            sheet.getCells().get("H5").setStyle(tableHeaderStyle);
//
//            sheet.getCells().get("I5").putValue("الطول");
//            sheet.getCells().get("I5").setStyle(tableHeaderStyle);
//
//            sheet.getCells().get("J5").putValue("العرض");
//            sheet.getCells().get("J5").setStyle(tableHeaderStyle);
//
//            sheet.getCells().get("K5").putValue("الوحدة");
//            sheet.getCells().get("K5").setStyle(tableHeaderStyle);

//            sheet.getCells().get("K5").putValue("الاجمالى");
//            sheet.getCells().get("K5").setStyle(tableHeaderStyle2);

//            sheet.getCells().get("K5").putValue("الأجمالى المتبقى");
//            sheet.getCells().get("K5").setStyle(tableHeaderStyle);

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

            int rowIdx = 9;


//                sheet.getCells().get("A" + rowIdx).putValue(pands.get(i).getPandCode());
//                if (rowIdx % 2 != 0) {
//                    sheet.getCells().get("A" + rowIdx).setStyle(shadowStyle);
//                } else {
//                    sheet.getCells().get("A" + rowIdx).setStyle(discriptionDataStyle);
//                }
//
//                sheet.getCells().get("B" + rowIdx).putValue(pands.get(i).getRawType());
//                if (rowIdx % 2 != 0) {
//                    sheet.getCells().get("B" + rowIdx).setStyle(shadowStyle);
//                } else {
//                    sheet.getCells().get("B" + rowIdx).setStyle(discriptionDataStyle);
//                }
//
//                sheet.getCells().get("C" + rowIdx).putValue(pands.get(i).getMainQuantity() + pands.get(i).getAdditionalQuantity());
//                if (rowIdx % 2 != 0) {
//                    sheet.getCells().get("C" + rowIdx).setStyle(shadowStyle);
//                } else {
//                    sheet.getCells().get("C" + rowIdx).setStyle(discriptionDataStyle);
//                }
//
//                sheet.getCells().get("D" + rowIdx).putValue(pands.get(i).getTotal());
//                if (rowIdx % 2 != 0) {
//                    sheet.getCells().get("D" + rowIdx).setStyle(shadowStyle);
//                } else {
//                    sheet.getCells().get("D" + rowIdx).setStyle(discriptionDataStyle);
//                }
//
//                sheet.getCells().get("K" + rowIdx).putValue(pands.get(i).getRestQuantity());
//                if (rowIdx % 2 != 0) {
//                    sheet.getCells().get("K" + rowIdx).setStyle(shadowStyle);
//                } else {
//                    sheet.getCells().get("K" + rowIdx).setStyle(discriptionDataStyle);
//                }
//
//                sheet.getCells().get("E" + rowIdx).putValue(pands.get(i).getUnit());
//                if (rowIdx % 2 != 0) {
//                    sheet.getCells().get("E" + rowIdx).setStyle(shadowStyle);
//                } else {
//                    sheet.getCells().get("E" + rowIdx).setStyle(discriptionDataStyle);
//                }

            List<PandsToJobOrder> pandsToJobOrders = pandsToJobOrderRepository.getPandDetails(projectId, id);
            Double totalQuantityInJobOrders = 0.0;
            Double totalQuantityInExitJobOrders = 0.0;
            for (PandsToJobOrder entry : pandsToJobOrders) {

                sheet.getCells().get("A" + rowIdx).putValue(entry.getJobOrderId());  // رقم امر شغل
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("A" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("A" + rowIdx).setStyle(discriptionDataStyle);
                }

                Double totalSumInJobOrders = pandsToJobOrderRepository.getSumByPandCodeAndJobOrder(projectId,id,entry.getJobOrderId());

                if (totalSumInJobOrders == null) {
                    totalSumInJobOrders = 0.0;
                }else{
                    totalSumInJobOrders = totalSumInJobOrders * Integer.parseInt(entry.getRepetition());
                }

                totalQuantityInJobOrders += totalSumInJobOrders;

                sheet.getCells().get("B" + rowIdx).putValue(totalSumInJobOrders); // الكميه لكل امر شغل
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("B" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("B" + rowIdx).setStyle(discriptionDataStyle);
                }

                Double totalExit = exitJobOrderRepository.getSumByJobOrderAndPand(pand.getProjectCode(), id, entry.getJobOrderId());

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

                DecimalFormat df = new DecimalFormat("#.###");
                String rTA = df.format(totalSumInJobOrders);

                String tA = df.format(totalExit);

                sheet.getCells().get("D" + rowIdx).putValue(Double.parseDouble(rTA) - Double.parseDouble(tA));
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("D" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("D" + rowIdx).setStyle(discriptionDataStyle);
                }
//
//                sheet.getCells().get("J" + rowIdx).putValue(pands.get(i).getUnit());
//                if (rowIdx % 2 != 0) {
//                    sheet.getCells().get("J" + rowIdx).setStyle(shadowStyle);
//                } else {
//                    sheet.getCells().get("J" + rowIdx).setStyle(discriptionDataStyle);
//                }

//                sheet.getCells().get("K" + rowIdx).putValue(pands.get(i).getTotal());
//                if (rowIdx % 2 != 0) {
//                    sheet.getCells().get("K" + rowIdx).setStyle(shadowStyle);
//                } else {
//                    sheet.getCells().get("K" + rowIdx).setStyle(discriptionDataStyle);
//                }


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

    public InputStreamResource getPdf(Long id) throws Exception {
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

            Style tableHeaderStyle2 = sheet.getCells().get("C1").getStyle();
            tableHeaderStyle2.setHorizontalAlignment(TextAlignmentType.CENTER);
            tableHeaderStyle2.setVerticalAlignment(TextAlignmentType.CENTER);
            tableHeaderStyle2.getFont().setItalic(true);
            tableHeaderStyle2.getFont().setSize(9);
            tableHeaderStyle2.getFont().setBold(false);
            tableHeaderStyle2.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());
            tableHeaderStyle2.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());
            tableHeaderStyle2.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());
            tableHeaderStyle2.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, com.aspose.cells.Color.getBlack());


            Style discriptionDataStyle = sheet.getCells().get("F3").getStyle();
            discriptionDataStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
            discriptionDataStyle.setVerticalAlignment(TextAlignmentType.CENTER);
            discriptionDataStyle.getFont().setSize(11);

            Date dNow = new Date();
            SimpleDateFormat ft =
                    new SimpleDateFormat("hh:mm:ss a");

            DateFormat formatter1 = new SimpleDateFormat("dd.MM.yy");

            List<Pand> pands = pandsRepository.findByProjectProfileId(id);

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

            sheet.getCells().get("E1").putValue(pands.get(0).getEngineerName());
            sheet.getCells().get("E1").setStyle(discriptionDataStyle);

            sheet.getCells().get("D3").putValue("تاريخ الطباعة");
            sheet.getCells().get("D3").setStyle(discriptionDataStyle);

            sheet.getCells().get("E3").putValue(formatter1.format(dNow)+ " " + ft.format(dNow).toString());
            sheet.getCells().get("E3").setStyle(discriptionDataStyle);

            Style shadowStyle = workbook.createStyle();
            shadowStyle.setPattern(BackgroundType.SOLID);
            shadowStyle.setForegroundColor(Color.getDarkGray());
            shadowStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
            shadowStyle.getFont().setSize(11);

            sheet.getCells().get("A5").putValue("كود البند");
            sheet.getCells().get("A5").setStyle(tableHeaderStyle);

            sheet.getCells().get("B5").putValue("التوصيف");
            sheet.getCells().get("B5").setStyle(tableHeaderStyle);

            sheet.getCells().get("C5").putValue("كمية التعاقد");
            sheet.getCells().get("C5").setStyle(tableHeaderStyle);

            sheet.getCells().get("D5").putValue("الخامات الفعلية");
            sheet.getCells().get("D5").setStyle(tableHeaderStyle);

            sheet.getCells().get("E5").putValue("الخامات المستخدمة");
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

//            sheet.getCells().get("K5").putValue("الاجمالى");
//            sheet.getCells().get("K5").setStyle(tableHeaderStyle2);

            sheet.getCells().get("K5").putValue("الأجمالى المتبقى");
            sheet.getCells().get("K5").setStyle(tableHeaderStyle);

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

            for (int i = 0; i < pands.size(); i++) {

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

                sheet.getCells().get("C" + rowIdx).putValue(pands.get(i).getMainQuantity());
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("C" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("C" + rowIdx).setStyle(discriptionDataStyle);
                }

                sheet.getCells().get("D" + rowIdx).putValue(pands.get(i).getRawType());
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("D" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("D" + rowIdx).setStyle(discriptionDataStyle);
                }

                sheet.getCells().get("E" + rowIdx).putValue(pands.get(i).getRawUsed());
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("E" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("E" + rowIdx).setStyle(discriptionDataStyle);
                }

                sheet.getCells().get("F" + rowIdx).putValue(pands.get(i).getFinishType());
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("F" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("F" + rowIdx).setStyle(discriptionDataStyle);
                }

                sheet.getCells().get("G" + rowIdx).putValue(pands.get(i).getThickness());
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("G" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("G" + rowIdx).setStyle(discriptionDataStyle);
                }

                sheet.getCells().get("H" + rowIdx).putValue(pands.get(i).getHeight());
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("H" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("H" + rowIdx).setStyle(discriptionDataStyle);
                }

                sheet.getCells().get("I" + rowIdx).putValue(pands.get(i).getWidth());
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("I" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("I" + rowIdx).setStyle(discriptionDataStyle);
                }

                sheet.getCells().get("J" + rowIdx).putValue(pands.get(i).getUnit());
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("J" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("J" + rowIdx).setStyle(discriptionDataStyle);
                }

//                sheet.getCells().get("K" + rowIdx).putValue(pands.get(i).getTotal());
//                if (rowIdx % 2 != 0) {
//                    sheet.getCells().get("K" + rowIdx).setStyle(shadowStyle);
//                } else {
//                    sheet.getCells().get("K" + rowIdx).setStyle(discriptionDataStyle);
//                }

                sheet.getCells().get("K" + rowIdx).putValue(pands.get(i).getRestQuantity());
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("K" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("K" + rowIdx).setStyle(discriptionDataStyle);
                }
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
            System.out.println("1111111111111111111111111");
            return resource;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public InputStreamResource getPandImage(Long id, Long projectId) throws Exception {
        try {
            com.aspose.cells.Workbook workbook = new com.aspose.cells.Workbook();
            WorksheetCollection worksheets = workbook.getWorksheets();
            Worksheet sheet = worksheets.get(0);
            sheet.setDisplayRightToLeft(true);

            Cells cells = sheet.getCells();

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


            Date dNow = new Date();
            SimpleDateFormat ft =
                    new SimpleDateFormat("hh:mm:ss a");

            DateFormat formatter1 = new SimpleDateFormat("dd.MM.yy");

            Style discriptionDataStyle = sheet.getCells().get("F3").getStyle();
            discriptionDataStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
            discriptionDataStyle.setVerticalAlignment(TextAlignmentType.CENTER);
            discriptionDataStyle.getFont().setSize(11);

            Pand pand = pandsRepository.findByIdAndProjectProfileId(id,projectId);

            sheet.getCells().get("A1").putValue("أسم المشروع");
            sheet.getCells().get("A1").setStyle(discriptionDataStyle);

            sheet.getCells().get("B1").putValue(pand.getProjectName());
            sheet.getCells().get("B1").setStyle(discriptionDataStyle);

            sheet.getCells().get("A3").putValue("كود المشروع");
            sheet.getCells().get("A3").setStyle(discriptionDataStyle);

            sheet.getCells().get("B3").putValue(pand.getProjectCode());
            sheet.getCells().get("B3").setStyle(discriptionDataStyle);

            sheet.getCells().get("A5").putValue("تاريخ الطباعه");
            sheet.getCells().get("A5").setStyle(discriptionDataStyle);

            sheet.getCells().get("B5").putValue(formatter1.format(dNow)+ " " + ft.format(dNow).toString());
            sheet.getCells().get("B5").setStyle(discriptionDataStyle);

            sheet.getCells().get("C1").putValue("كود البند");
            sheet.getCells().get("C1").setStyle(discriptionDataStyle);

            sheet.getCells().get("D1").putValue(pand.getPandCode());
            sheet.getCells().get("D1").setStyle(discriptionDataStyle);

            sheet.getCells().get("C3").putValue("الوحدة");
            sheet.getCells().get("C3").setStyle(discriptionDataStyle);

            sheet.getCells().get("D3").putValue(pand.getUnit());
            sheet.getCells().get("D3").setStyle(discriptionDataStyle);

            sheet.getCells().get("E1").putValue("كمية البند");
            sheet.getCells().get("E1").setStyle(discriptionDataStyle);

            sheet.getCells().get("F1").putValue(pand.getMainQuantity() + pand.getAdditionalQuantity());
            sheet.getCells().get("F1").setStyle(discriptionDataStyle);

            sheet.getCells().get("E3").putValue("المتبقى فى البند");
            sheet.getCells().get("E3").setStyle(discriptionDataStyle);

            sheet.getCells().get("F3").putValue(pand.getRestQuantity());
            sheet.getCells().get("F3").setStyle(discriptionDataStyle);

            Style shadowStyle = workbook.createStyle();
            shadowStyle.setPattern(BackgroundType.SOLID);
            shadowStyle.setForegroundColor(Color.getDarkGray());
            shadowStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
            shadowStyle.getFont().setSize(11);


            sheet.getCells().get("A7").putValue("أسم الخامة");
            sheet.getCells().get("A7").setStyle(tableHeaderStyle);

            sheet.getCells().get("B7").putValue("السمك");
            sheet.getCells().get("B7").setStyle(tableHeaderStyle);

            sheet.getCells().get("C7").putValue("الملاحظات");
            sheet.getCells().get("C7").setStyle(tableHeaderStyle);



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


            byte[] imageBytes = pand.getImage();

            int pictureIndex2 = sheet.getPictures().add(7, 5, new ByteArrayInputStream(imageBytes));
            Picture picture2 = sheet.getPictures().get(pictureIndex2);

            // Optionally, set the picture to fit within the merged area
            picture2.setPlacement(PlacementType.MOVE);
            picture2.setWidthScale(30); // Scale the image to fit width
            picture2.setHeightScale(30);

            int rowIdx = 9;


                sheet.getCells().get("A" + rowIdx).putValue(pand.getRawType());  // رقم امر شغل
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("A" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("A" + rowIdx).setStyle(discriptionDataStyle);
                }


                sheet.getCells().get("B" + rowIdx).putValue(pand.getThickness()); // الكميه لكل امر شغل
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("B" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("B" + rowIdx).setStyle(discriptionDataStyle);
                }


            if (pand.getImageDescription().length() > 30) {
                int rowIndex = rowIdx;
                String input = pand.getImageDescription();
                int partLength = 30;
                for (int d = 0; d < input.length(); d += partLength) {
                    int end = Math.min(d + partLength, input.length());
                    String part = input.substring(d, end);

                    Row row = cells.getRows().get(rowIndex);
//                    row.setHeight(50);

                    sheet.getCells().get("C" + rowIndex).putValue(part + "\n");
                    sheet.getCells().get("C" + rowIndex).setStyle(shadowStyle);

                    rowIndex++;
                }
            } else {
                sheet.getCells().get("C" + rowIdx).putValue(pand.getImageDescription());
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("C" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("C" + rowIdx).setStyle(discriptionDataStyle);
                }
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

    public String deletePandImage(Long id){
        Optional<Pand> pand = pandsRepository.findById(id);
        try{
            pand.get().setImage(null);
            pandsRepository.save(pand.get());
            System.out.println("deleted");
            return "Image Deleted Successfully";
        }catch (Exception e){
            System.out.println("not deleted");
            return "Failed To Delete The Image";
        }
    }
}
