package com.example.demo.service;

import com.aspose.cells.*;
import com.example.demo.DTO.MarbleItemDto;
import com.example.demo.models.*;
import com.example.demo.payload.CheckLimitResponse;
import com.example.demo.repository.*;
import com.example.demo.security.jwt.JwtUtils;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PandsToJobOrderService {

    private static final Logger logger = LoggerFactory.getLogger(PandsToJobOrderService.class);

    private static final int FLAG_ERROR = 1;

    @Autowired
    private ChangeHistoryLog changeHistoryLog;

    @Autowired
    PandsToJobOrderRepository pandsToJobOrderRepository;

    @Autowired
    PreviewJobOrderRepository previewJobOrderRepository;

    @Autowired
    ExitJobOrderRepository exitJobOrderRepository;

    @Autowired
    PandsService pandsService;

    @Autowired
    PandsRepository pandsRepository;

    @Autowired
    JobOrderRepository jobOrderRepository;

    @Autowired
    ReturnRepository returnRepository;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    JobOrderService jobOrderService;

    @RequestScope
    public PandsToJobOrder saveChildPand(PandsToJobOrder pandsToJobOrder, int flag, HttpServletRequest request) throws SQLException {

        Pand pand = pandsService.getPandByPandCode(pandsToJobOrder.getPandCode(), pandsToJobOrder.getProjectProfileId());
        double total;
        double restTotal;
        DecimalFormat df = new DecimalFormat("#.###");
        try {
            double repetation = 0;
            if (pandsToJobOrder.getRepetition().equals("0") || pandsToJobOrder.getRepetition().isEmpty()) {
                repetation = 1;
                pandsToJobOrder.setRepetition("1");
            } else {
                repetation = Double.parseDouble(pandsToJobOrder.getRepetition());
            }

            if (pandsToJobOrder.getUnit().equals("متر مربع")) {
                total = (Double.parseDouble(pandsToJobOrder.getHeight()) * Double.parseDouble(pandsToJobOrder.getWidth()) * (pandsToJobOrder.getQuantity() * repetation)) / 10000;
            } else if (pandsToJobOrder.getUnit().equals("متر طولى")) {
                total = (Double.parseDouble(pandsToJobOrder.getHeight()) * (pandsToJobOrder.getQuantity() * repetation)) / 100;
            } else {
                total = pandsToJobOrder.getQuantity() * repetation;
            }

            if (Double.parseDouble(df.format(pand.getRestQuantity() - Double.parseDouble(df.format(total)))) < 0) {
                pandsToJobOrder.setFlag(1);
                pandsToJobOrder.setMessage(
                        "الكمية المطلوبة: " + df.format(total) + "<br>" +
                                "الكمية المتبقية في البند: " + pand.getRestQuantity()
                );
                return pandsToJobOrder;
            } else {
                pand.setRestQuantity(Double.parseDouble(df.format(pand.getRestQuantity() - Double.parseDouble(df.format(total)))));
            }

//            restTotal = Double.parseDouble(df.format(pand.getRestQuantity() - total));
//
//            pand.setRestQuantity(restTotal);
//            pandsRepository.save(pand);
        }catch (Exception e){
            e.printStackTrace();
            pandsToJobOrder.setFlag(1);
            pandsToJobOrder.setMessage(pand.getPandCode() +" الكمية المطلوبة اعلى من الكمية المتبقية فى البند " );
            return pandsToJobOrder;
        }

        pandsToJobOrder.setFlag(0);
        Integer number = jobOrderService.getTheMaxNumber(pandsToJobOrder.getProjectProfileId());
        GregorianCalendar gcalendar = new GregorianCalendar();

        JobOrder isJobOrderExist = null;

        String username = changeHistoryLog.getUser(request);
        JobOrder jobOrder = jobOrderRepository.findLastInserted(pand.getProjectCode());

        if (flag == 0) {
            if (jobOrder.isCommit()) {
//                isJobOrderExist = jobOrderService.getByJobOrder(number + 1 + "/" + gcalendar.get(Calendar.YEAR));
//                if (isJobOrderExist == null) {
                isJobOrderExist = new JobOrder();
                Date dNow = new Date();
                SimpleDateFormat ft =
                        new SimpleDateFormat("hh:mm:ss a");

                DateFormat formatter1 = new SimpleDateFormat("dd.MM.yy");
                isJobOrderExist.setProjectProfileId(pandsToJobOrder.getProjectProfileId());
                isJobOrderExist.setJobOrderDate(formatter1.format(dNow));
                isJobOrderExist.setJobOrderTime(ft.format(dNow));
                int nextNumber = number + 1;
                isJobOrderExist.setJobOrderNumber(pand.getProjectCode().concat("/") + nextNumber + "/" + gcalendar.get(Calendar.YEAR));
                isJobOrderExist.setNumber(number + 1);
                isJobOrderExist.setProjectName(pand.getProjectName());
                isJobOrderExist.setProjectCode(pand.getProjectCode());
                isJobOrderExist.setInstallementArea(pandsToJobOrder.getInstallationArea());
                isJobOrderExist.setCreatedBy(username);
                isJobOrderExist.setYear(gcalendar.get(Calendar.YEAR));

//                jobOrderRepository.save(isJobOrderExist);
                if (isJobOrderExist.getPandsToJobOrders() == null) {
                    isJobOrderExist.setPandsToJobOrders(new ArrayList<>());
                }
//                }
                pandsToJobOrder.setJobOrderId(pand.getProjectCode().concat("/") + nextNumber + "/" + gcalendar.get(Calendar.YEAR));
            }else{
                pandsToJobOrder.setJobOrderId(jobOrder.getJobOrderNumber());
                jobOrder.getPandsToJobOrders().add(pandsToJobOrder);
            }
        } else {
            List<PandsToJobOrder> pandsToJobOrderList = pandsToJobOrderRepository.getByJobOrderId(pandsToJobOrder.getJobOrderId());
            if (pandsToJobOrderList != null) {
                pandsToJobOrder.setBlockNumber(pandsToJobOrderList.getFirst().getBlockNumber());
                pandsToJobOrder.setFloor(pandsToJobOrderList.getFirst().getFloor());
                pandsToJobOrder.setJobOrderType(pandsToJobOrderList.getFirst().getJobOrderType());
                pandsToJobOrder.setEngineerName(pandsToJobOrderList.getFirst().getEngineerName());
                pandsToJobOrder.setOfficerName(pandsToJobOrderList.getFirst().getOfficerName());
                pandsToJobOrder.setInstallationArea(pandsToJobOrderList.getFirst().getInstallationArea());
            }
        }


        Date dNow = new Date();
        SimpleDateFormat ft =
                new SimpleDateFormat("hh:mm:ss a");
        pandsToJobOrder.setJobOrderTime(ft.format(dNow));

        UUID uuid = UuidCreator.getTimeBased();

        pandsToJobOrder.setUniqueId(uuid.toString());
        pandsToJobOrder.setTotal(df.format(total));
        pandsToJobOrder.setMainTotal(df.format(total));
        pandsToJobOrder.setMainQuantity(pandsToJobOrder.getQuantity());
        pandsToJobOrder.setQuantity(pandsToJobOrder.getQuantity() * Double.parseDouble(pandsToJobOrder.getRepetition()));
        pandsToJobOrder.setProjectCode(pand.getProjectCode());
        pandsToJobOrder.setProjectName(pand.getProjectName());
//        pandsToJobOrder.setQuantityInPand(restTotal);
        pandsToJobOrderRepository.save(pandsToJobOrder);
        changeHistoryLog.saveChange(pandsToJobOrder.getId().toString(), pandsToJobOrder.toString(), pandsToJobOrder.toString(), "save", request);

        if (flag == 0 && jobOrder.isCommit()) {
            assert isJobOrderExist != null;
            isJobOrderExist.getPandsToJobOrders().add(pandsToJobOrder);
            jobOrderRepository.save(isJobOrderExist);

//            GregorianCalendar gcalendar2 = new GregorianCalendar();
//            String jobOrderNumber = number + 1 + "/" + gcalendar2.get(Calendar.YEAR);
//            JobOrder jobOrder1 = jobOrderRepository.getByJobOrderNumber(jobOrderNumber);
//            jobOrder1.setNumber(number + 1);

        }
        return pandsToJobOrder;
    }

    @Transactional
    public ResponseEntity<PandsToJobOrder> updateJobOrder(Long id, PandsToJobOrder updatedJobOrder, HttpServletRequest request) {

        PandsToJobOrder pandsToJobOrder = pandsToJobOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkOrder Not Found for ID: " + id));

        JobOrder fatherJobOrder = jobOrderService.getByJobOrder(pandsToJobOrder.getJobOrderId());

//        if (fatherJobOrder.isApproved()) {
//            return errorResponse(updatedJobOrder, "cannot add/edit on approved work order", HttpStatus.CONFLICT);
//        }

        List<ExitJobOrder> exitJobOrders = exitJobOrderRepository.getByJobOrderId(pandsToJobOrder.getJobOrderId());
            if (!exitJobOrders.isEmpty()) {
                pandsToJobOrder.setFlag(1);
                pandsToJobOrder.setMessage(" غير مسموح بالتعديل .. تم صرف أذونات لأمر الشغل " + pandsToJobOrder.getJobOrderId());
                return new ResponseEntity<>(pandsToJobOrder, HttpStatus.BAD_REQUEST);
            }

        // --- Parse numeric string fields once, with a clear error if they're bad ---
        final double height;
        final double width;
        final double repetition;
        try {
            height = parseRequiredDouble(updatedJobOrder.getHeight(), "height");
            width = parseRequiredDouble(updatedJobOrder.getWidth(), "width");
            repetition = parseRequiredDouble(updatedJobOrder.getRepetition(), "repetition");
        } catch (NumberFormatException nfe) {
            logger.warn("Invalid numeric field on job order {}: {}", id, nfe.getMessage());
            return errorResponse(updatedJobOrder, "Invalid numeric value: " + nfe.getMessage(), HttpStatus.BAD_REQUEST);
        }

        double total = calculateTotal(updatedJobOrder.getUnit(), height, width, updatedJobOrder.getMainQuantity(), repetition);

        Pand pand = pandsService.getPandByPandCode(updatedJobOrder.getPandCode(), updatedJobOrder.getProjectProfileId());

        copyBaseFields(pandsToJobOrder, updatedJobOrder);

        boolean quantityFieldsChanged =
                !Objects.equals(pandsToJobOrder.getHeight(), updatedJobOrder.getHeight())
                        || !Objects.equals(pandsToJobOrder.getWidth(), updatedJobOrder.getWidth())
                        || !Objects.equals(pandsToJobOrder.getRepetition(), updatedJobOrder.getRepetition())
                        || pandsToJobOrder.getMainQuantity() != updatedJobOrder.getMainQuantity();

        if (quantityFieldsChanged) {
            DecimalFormat df = new DecimalFormat("#.###");
            double oldMainTotal = parseRequiredDouble(pandsToJobOrder.getMainTotal(), "mainTotal");

            double quantityInBand = pand.getRestQuantity() - total;
            if (quantityInBand < 0) {
                return errorResponse(pandsToJobOrder,
                        " الكمية المطلوبة اعلى من الكمية المتبقية فى البند " + pand.getPandCode(),
                        HttpStatus.BAD_REQUEST);
            }

            // Adjust the pand's remaining quantity by the delta between old and new totals
            double delta = oldMainTotal - total; // positive => quantity freed up, negative => quantity consumed
            pand.setRestQuantity(Double.parseDouble(df.format(pand.getRestQuantity() + delta)));

            pandsToJobOrder.setTotal(df.format(total));
            pandsToJobOrder.setMainTotal(df.format(total));
            pandsToJobOrder.setQuantityInPand(Double.parseDouble(df.format(pand.getRestQuantity() - total)));
            pandsToJobOrder.setQuantity(updatedJobOrder.getMainQuantity() * repetition);
            pandsToJobOrder.setMainQuantity(updatedJobOrder.getMainQuantity());
        }

        if (!Objects.equals(pandsToJobOrder.getInstallationArea(), updatedJobOrder.getInstallationArea())) {
            pandsToJobOrder.setInstallationArea(updatedJobOrder.getInstallationArea());
            fatherJobOrder.setInstallementArea(updatedJobOrder.getInstallationArea());
            jobOrderRepository.save(fatherJobOrder);
        }

        pandsToJobOrder.setHeight(updatedJobOrder.getHeight());
        pandsToJobOrder.setWidth(updatedJobOrder.getWidth());
        pandsToJobOrder.setRepetition(updatedJobOrder.getRepetition());

        // Single save now (removed the duplicate call that existed before this refactor)
        pandsRepository.save(pand);

        pandsToJobOrder.setMessage(" الكميةالمتبقية فى البند " + pand.getRestQuantity());
        pandsToJobOrderRepository.save(pandsToJobOrder);

        changeHistoryLog.saveChange(id.toString(), updatedJobOrder.toString(), pandsToJobOrder.toString(), "update", request);

        return new ResponseEntity<>(pandsToJobOrder, HttpStatus.OK);
    }

    // --- helpers -------------------------------------------------------------

    private double parseRequiredDouble(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new NumberFormatException(fieldName + " is missing");
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(fieldName + " is not a valid number: '" + value + "'");
        }
    }

    private double calculateTotal(String unit, double height, double width, double mainQuantity, double repetition) {
        return switch (unit) {
            case "متر مربع" -> (height * width * (mainQuantity * repetition)) / 10000;
            case "متر طولى" -> (height * (mainQuantity * repetition)) / 100;
            default -> mainQuantity * repetition;
        };
    }

    private void copyBaseFields(PandsToJobOrder target, PandsToJobOrder source) {
        target.setProjectCode(source.getProjectCode());
        target.setProjectName(source.getProjectName());
        target.setEngineerName(source.getEngineerName());
        target.setJobOrderType(source.getJobOrderType());
        target.setManufacturingCode(source.getManufacturingCode());
        target.setPandCode(source.getPandCode());
        target.setDescription(source.getDescription());
        target.setManufacturing(source.getManufacturing());
        target.setRawType(source.getRawType());
        target.setRawUsed(source.getRawUsed());
        target.setFinishType(source.getFinishType());
        target.setThickness(source.getThickness());
        target.setBlockNumber(source.getBlockNumber());
        target.setFloor(source.getFloor());
        target.setUnit(source.getUnit());
        target.setAdditionalDescription(source.getAdditionalDescription());
    }

    private ResponseEntity<PandsToJobOrder> errorResponse(PandsToJobOrder entity, String message, HttpStatus status) {
        entity.setFlag(FLAG_ERROR);
        entity.setMessage(message);
        return new ResponseEntity<>(entity, status);
    }


//    public ResponseEntity<PandsToJobOrder> updateJobOrder(Long id, PandsToJobOrder updatedJobOrder, int flag, HttpServletRequest request) {
//
//        try {
//            PandsToJobOrder jobOrder = pandsToJobOrderRepository.findById(id)
//                    .orElseThrow(() -> new ResourceNotFoundException("rawType Not Found for ID: " + id));
//
//            List<ExitJobOrder> exitJobOrders = exitJobOrderRepository.getByJobOrderId(jobOrder.getJobOrderId());
//            if (!exitJobOrders.isEmpty()) {
//                jobOrder.setFlag(1);
//                jobOrder.setMessage(" غير مسموح بالتعديل .. تم صرف أذونات لأمر الشغل " + jobOrder.getJobOrderId());
//                return new ResponseEntity<>(jobOrder, HttpStatus.BAD_REQUEST);
//            }
//
//            if (updatedJobOrder.getRepetition().equals("0") || updatedJobOrder.getRepetition().isEmpty()) {
//                updatedJobOrder.setRepetition("1");
//            }
//
//            double total;
//            DecimalFormat df = new DecimalFormat("#.###");
//
//            Pand pand = pandsService.getPandByPandCode(updatedJobOrder.getPandCode(), updatedJobOrder.getProjectProfileId());
//            if (updatedJobOrder.getUnit().equals("متر مربع")) {
//                total = (Double.parseDouble(updatedJobOrder.getHeight()) * Double.parseDouble(updatedJobOrder.getWidth()) * (updatedJobOrder.getMainQuantity() * Double.parseDouble(updatedJobOrder.getRepetition()))) / 10000;
//            } else if (updatedJobOrder.getUnit().equals("متر طولى")) {
//                total = (Double.parseDouble(updatedJobOrder.getHeight()) * (updatedJobOrder.getMainQuantity() * Double.parseDouble(updatedJobOrder.getRepetition()))) / 100;
//            } else {
//                total = updatedJobOrder.getMainQuantity() * Double.parseDouble(updatedJobOrder.getRepetition());
//            }
//
//            if (jobOrder.getMainQuantity() != updatedJobOrder.getMainQuantity()) {
//                double quantityInPand = pand.getRestQuantity() + updatedJobOrder.getQuantity();
//                if (total > quantityInPand) {
//                    jobOrder.setFlag(1);
//                    jobOrder.setMessage(" الكمية المطلوبة اعلى من الكمية المتبقية فى البند " + pand.getPandCode());
//                    return new ResponseEntity<>(jobOrder, HttpStatus.BAD_REQUEST);
//                }
//                pand.setRestQuantity(Double.valueOf(df.format(pand.getRestQuantity() + jobOrder.getQuantity())));
//                pandsRepository.save(pand);
//            }
//
//            jobOrder.setProjectCode(updatedJobOrder.getProjectCode());
//            jobOrder.setProjectName(updatedJobOrder.getProjectName());
//            jobOrder.setEngineerName(updatedJobOrder.getEngineerName());
//            jobOrder.setJobOrderType(updatedJobOrder.getJobOrderType());
//            jobOrder.setManufacturingCode(updatedJobOrder.getManufacturingCode());
//            jobOrder.setPandCode(updatedJobOrder.getPandCode());
//            jobOrder.setDescription(updatedJobOrder.getDescription());
//            jobOrder.setManufacturing(updatedJobOrder.getManufacturing());
//            jobOrder.setRawType(updatedJobOrder.getRawType());
//            jobOrder.setRawUsed(updatedJobOrder.getRawUsed());
//            jobOrder.setFinishType(updatedJobOrder.getFinishType());
//            jobOrder.setThickness(updatedJobOrder.getThickness());
//            jobOrder.setBlockNumber(updatedJobOrder.getBlockNumber());
//            jobOrder.setFloor(updatedJobOrder.getFloor());
//
//            jobOrder.setUnit(updatedJobOrder.getUnit());
//
//            jobOrder.setAdditionalDescription(updatedJobOrder.getAdditionalDescription());
//
//            JobOrder fatherJobOrder = jobOrderService.getByJobOrder(updatedJobOrder.getJobOrderId());
//            if (!jobOrder.getInstallationArea().equals(updatedJobOrder.getInstallationArea())) {
//                jobOrder.setInstallationArea(updatedJobOrder.getInstallationArea());
//
//                fatherJobOrder.setInstallementArea(updatedJobOrder.getInstallationArea());
//                jobOrderRepository.save(fatherJobOrder);
//            }
//            jobOrder.setHeight(updatedJobOrder.getHeight());
//            jobOrder.setWidth(updatedJobOrder.getWidth());
//            jobOrder.setRepetition(updatedJobOrder.getRepetition());
////        } else {
////            jobOrder.setQuantity(jobOrder.getQuantity());
////        }
//
//            try {
//                if (total < Double.parseDouble(jobOrder.getMainTotal())) {
//                    double newVal = Double.parseDouble(jobOrder.getMainTotal()) - total;
//                    pand.setRestQuantity(Double.parseDouble(df.format(pand.getRestQuantity() + newVal)));
//                } else {
//                    double newVal = total - Double.parseDouble(jobOrder.getMainTotal());
//                    pand.setRestQuantity(Double.parseDouble(df.format(pand.getRestQuantity() - newVal)));
//                }
//                pandsRepository.save(pand);
//
//            } catch (Exception e) {
//                jobOrder.setFlag(1);
//                jobOrder.setMessage(" الكمية المطلوبة " + Double.parseDouble(df.format(total)) + '\n' +
//                        " الكمية المتبقيه فى البند " + pand.getRestQuantity());
//                return new ResponseEntity<>(jobOrder, HttpStatus.BAD_REQUEST);
//            }
//
//            if (!jobOrder.getHeight().equals(updatedJobOrder.getHeight())
//                    || !jobOrder.getWidth().equals(updatedJobOrder.getWidth())
//                    || !jobOrder.getRepetition().equals(updatedJobOrder.getRepetition())
//                    || jobOrder.getMainQuantity() != updatedJobOrder.getMainQuantity()
//            ) {
//                jobOrder.setTotal(df.format(total));
//                jobOrder.setMainTotal(df.format(total));
//
//                jobOrder.setQuantity(updatedJobOrder.getMainQuantity() * Double.parseDouble(updatedJobOrder.getRepetition()));
//                jobOrder.setMainQuantity(updatedJobOrder.getMainQuantity());
//
//            }
//
//
//            jobOrder.setMessage(" الكميةالمتبقية فى البند " + pand.getRestQuantity());
//            pandsToJobOrderRepository.save(jobOrder);
//            changeHistoryLog.saveChange(id.toString(), updatedJobOrder.toString(), jobOrder.toString(), "update", request);
//
//            return new ResponseEntity<>(jobOrder, HttpStatus.OK);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity<>(null, HttpStatus.OK);
//        }
//    }

    public List<PandsToJobOrder> getByJobOrderId(String id) {

        return pandsToJobOrderRepository.getByJobOrderId(id);
    }

    public List<PandsToJobOrder> getByJobOrderIdWzNoZeros(UnifiedSerial unifiedSerial) {
        List<PandsToJobOrder> pandsToJobOrders = pandsToJobOrderRepository.getByJobOrderId(unifiedSerial.getJobOrderNumber());
        List<PandsToJobOrder> pandsToJobOrdersNoZeros = new ArrayList<>();
        for (PandsToJobOrder pandsToJobOrder : pandsToJobOrders) {
            if (pandsToJobOrder.getQuantity() > 0) {
                pandsToJobOrdersNoZeros.add(pandsToJobOrder);
            }
        }
        return pandsToJobOrdersNoZeros;
    }

    public PandsToJobOrder getByJobOrderId(Long id) {

        return pandsToJobOrderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("rawType Not Found for ID: " + id));
    }

    public PandsToJobOrder getByjobOrderAndPandId(String jobOrderid, String pandId) {

        return pandsToJobOrderRepository.findByJobOrderIdAndPandCode(jobOrderid, pandId);
    }

    public List<PandsToJobOrder> getByProjectId(Long id) {
        try {
            return pandsToJobOrderRepository.getByProjectProfileId(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    public ResponseEntity<PandsToJobOrder> deletePandToJobOrder(Long id, HttpServletRequest request) {
        PandsToJobOrder pandsToJobOrder = pandsToJobOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("pand Not Found for ID: " + id));

        List<ExitJobOrder> exitJobOrders = exitJobOrderRepository.getByJobOrderId(pandsToJobOrder.getJobOrderId());
        if (exitJobOrders.size() > 0) {
            pandsToJobOrder.setFlag(1);
            pandsToJobOrder.setMessage(" غير مسموح بمسح أمر الشغل .. تم صرف أذونات لأمر الشغل " + pandsToJobOrder.getJobOrderId());
            return new ResponseEntity<>(pandsToJobOrder, HttpStatus.BAD_REQUEST);
        }

        changeHistoryLog.saveChange(id.toString(), pandsToJobOrder.toString(), pandsToJobOrder.toString(), "delete", request);

        double total;
        DecimalFormat df = new DecimalFormat("#.###");
        Pand pand = pandsService.getPandByPandCode(pandsToJobOrder.getPandCode(), pandsToJobOrder.getProjectProfileId());
        pand.setRestQuantity(Double.parseDouble(df.format(pand.getRestQuantity() + Double.parseDouble(pandsToJobOrder.getTotal()))));

        pandsToJobOrderRepository.deleteFromMutuleTable(id);
        pandsRepository.save(pand);
        pandsToJobOrderRepository.deleteById(id);

        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

    }

    public CheckLimitResponse returnJobOrder(String id, PandsToJobOrder returnReason) {
        CheckLimitResponse checkLimitResponse = new CheckLimitResponse();

        try {

            if(returnReason.getReturnReason() == ""){
                checkLimitResponse.setFlag(1);
                checkLimitResponse.setMessage("برجاء إدخال سبب الارجاع");
                return checkLimitResponse;
            }
            List<ExitJobOrder> actualPandsToJobOrder = exitJobOrderRepository.getBySerial(id);


            for (int i = 0; i < actualPandsToJobOrder.size(); i++) {
                PandsToJobOrder pandsToJobOrder = pandsToJobOrderRepository.getByUniqueIdAndJobOrderId(actualPandsToJobOrder.get(i).getUniqueId()
                        , actualPandsToJobOrder.get(i).getJobOrderId());
                pandsToJobOrder.setQuantity(pandsToJobOrder.getQuantity() + actualPandsToJobOrder.get(i).getQuantity());
                pandsToJobOrder.setTotal(pandsToJobOrder.getTotal() + actualPandsToJobOrder.get(i).getTotal());
                actualPandsToJobOrder.get(i).setSerialNumber(actualPandsToJobOrder.get(i).getSerialNumber().concat("  (مرتجع)"));
                actualPandsToJobOrder.get(i).setReturnFlag(true);
                exitJobOrderRepository.save(actualPandsToJobOrder.get(i));
                ReturnJobOrders returnJobOrders = new ReturnJobOrders();
                returnJobOrders = mappingJobOrder(actualPandsToJobOrder.get(i),returnReason.getReturnReason());
                returnRepository.save(returnJobOrders);

            }
//        double total;
//        DecimalFormat df = new DecimalFormat("#.###");
//
//        if (jobOrderParent.getUnit().equals("متر مربع")) {
//            total = (Double.valueOf(jobOrderParent.getHeight()) * Double.valueOf(jobOrderParent.getWidth()) * Double.valueOf(jobOrderParent.getMainQuantity())) / 10000;
//        } else if (jobOrderParent.getUnit().equals("متر طولى")) {
//            total = (Double.valueOf(jobOrderParent.getHeight()) * Double.valueOf(jobOrderParent.getMainQuantity())) / 100;
//        } else {
//            total = Double.valueOf(jobOrderParent.getMainQuantity());
//        }

//        if (((Double.valueOf(actualPandsToJobOrder.get().getTotal()) + total) > Double.valueOf(actualPandsToJobOrder.get().getMainTotal())) &&
//                ((Double.valueOf(actualPandsToJobOrder.get().getQuantity()) + Double.valueOf(jobOrderParent.getMainQuantity())) > Double.valueOf(actualPandsToJobOrder.get().getMainQuantity()))) {
//            checkLimitResponse.setFlag(1);
//            checkLimitResponse.setMessage("الكمية المرجعه تتخطى الكمية الأساسية فى بند " + actualPandsToJobOrder.get().getPandCode());
//            return checkLimitResponse;
//        } else {
//
//        }
//        actualPandsToJobOrder.get().setQuantity(actualPandsToJobOrder.get().getQuantity() + jobOrderParent.getMainQuantity());
//        actualPandsToJobOrder.get().setTotal(String.valueOf(Double.valueOf(actualPandsToJobOrder.get().getTotal()) + Double.valueOf(df.format(total))));
//        pandsToJobOrderRepository.save(actualPandsToJobOrder.get());


//        returnJobOrders.setTotal(String.valueOf(Double.valueOf(df.format(total))));

            checkLimitResponse.setFlag(0);
            checkLimitResponse.setMessage("تم أرجاع أوامر الشغل المحدده بنجاح");

            return checkLimitResponse;
        } catch (Exception e) {
            checkLimitResponse.setFlag(1);
            checkLimitResponse.setMessage("حدث خطأ");
            return checkLimitResponse;
        }
    }

    public ReturnJobOrders mappingJobOrder(ExitJobOrder updatedJobOrder,String returnReason) {

        ReturnJobOrders jobOrder = new ReturnJobOrders();
        jobOrder.setJobOrderId(updatedJobOrder.getJobOrderId());
        jobOrder.setProjectProfileId((updatedJobOrder.getProjectProfileId()));
        jobOrder.setProjectCode(updatedJobOrder.getProjectCode());
        jobOrder.setProjectName(updatedJobOrder.getProjectName());
        jobOrder.setPandCode(updatedJobOrder.getPandCode());
        jobOrder.setThickness(updatedJobOrder.getThickness());
        jobOrder.setHeight(updatedJobOrder.getHeight());
        jobOrder.setWidth(updatedJobOrder.getWidth());
        jobOrder.setQuantity(updatedJobOrder.getQuantity());
        jobOrder.setUnit(updatedJobOrder.getUnit());
        jobOrder.setReturnReason(returnReason);
        jobOrder.setRawType(updatedJobOrder.getRawType());
        return jobOrder;
    }

    // PDF File
    public InputStreamResource getPdf(String id) throws Exception {

        JobOrder jobOrder = jobOrderService.getByJobOrder(id);

        List<PandsToJobOrder> pandsToJobOrdersRaws = pandsToJobOrderRepository.getByJobOrderIdGroupByRawType(jobOrder.getProjectProfileId(), id);


        Workbook workbook = new Workbook();

        WorksheetCollection worksheets = workbook.getWorksheets();
        Worksheet sheet = worksheets.get(0);
        sheet.setDisplayRightToLeft(true);
        sheet.getPageSetup().setPrintTitleRows("$9:$9");
        Cells cells = sheet.getCells();

//            sheet.getCells().setRowHeight(7, 20);

        PageSetup pageSetup = sheet.getPageSetup();
        pageSetup.setOrientation(PageOrientationType.LANDSCAPE);
        pageSetup.setPaperSize(PaperSizeType.PAPER_A_3);


        pageSetup.setTopMargin(1);
        pageSetup.setBottomMargin(1);
        pageSetup.setLeftMargin(1);
        pageSetup.setRightMargin(1);

        pageSetup.setFitToPagesWide(1);     // Fit to one page wide
        pageSetup.setFitToPagesTall(0);     // Don't force page height
        pageSetup.setZoom(100);             // Prevent zoom from shrinking
        pageSetup.setPercentScale(false);   // Don't scale by percent

        Date dNow = new Date();
        SimpleDateFormat ft =
                new SimpleDateFormat("hh:mm:ss a");

        DateFormat formatter1 = new SimpleDateFormat("dd.MM.yy");


        Style tableHeaderStyle = sheet.getCells().get("C1").getStyle();
        tableHeaderStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
        tableHeaderStyle.setVerticalAlignment(TextAlignmentType.CENTER);
        tableHeaderStyle.getFont().setItalic(true);
        tableHeaderStyle.getFont().setSize(15);
        tableHeaderStyle.getFont().setBold(false);
        tableHeaderStyle.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());
        tableHeaderStyle.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
        tableHeaderStyle.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, Color.getBlack());
        tableHeaderStyle.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, Color.getBlack());

        Style discriptionDataStyle = sheet.getCells().get("F3").getStyle();
        discriptionDataStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
        discriptionDataStyle.setVerticalAlignment(TextAlignmentType.CENTER);
        discriptionDataStyle.getFont().setSize(15);

        sheet.getCells().get("A1").putValue("أمر الشغل");
        sheet.getCells().get("A1").setStyle(discriptionDataStyle);


        sheet.getCells().get("B1").putValue(pandsToJobOrdersRaws.get(0).getJobOrderId());
        sheet.getCells().get("B1").setStyle(discriptionDataStyle);


        sheet.getCells().get("A3").putValue("نوع");
        sheet.getCells().get("A3").setStyle(discriptionDataStyle);

        sheet.getCells().get("B3").putValue(pandsToJobOrdersRaws.get(0).getJobOrderType());
        sheet.getCells().get("B3").setStyle(discriptionDataStyle);


        sheet.getCells().get("A5").putValue("الأنشاء");
        sheet.getCells().get("A5").setStyle(discriptionDataStyle);

//        sheet.getCells().merge(5, 2, 1, 2);  // (startRow, startColumn, totalRows, totalColumns)

        sheet.getCells().get("C5").putValue(jobOrder.getJobOrderDate());
        sheet.getCells().get("C5").setStyle(discriptionDataStyle);

        sheet.getCells().get("B5").putValue(jobOrder.getJobOrderTime());
        sheet.getCells().get("B5").setStyle(discriptionDataStyle);

        sheet.getCells().get("A7").putValue("الطباعة");
        sheet.getCells().get("A7").setStyle(discriptionDataStyle);

        sheet.getCells().get("C7").putValue(formatter1.format(dNow));
        sheet.getCells().get("C7").setStyle(discriptionDataStyle);

        sheet.getCells().get("B7").putValue(ft.format(dNow).toString());
        sheet.getCells().get("B7").setStyle(discriptionDataStyle);

        sheet.getCells().get("D1").putValue("منطقة التركيب");
        sheet.getCells().get("D1").setStyle(discriptionDataStyle);

//        sheet.getCells().merge(0, 4, 1, 5);  // (startRow, startColumn, totalRows, totalColumns)

        sheet.getCells().get("E1").putValue(pandsToJobOrdersRaws.get(0).getInstallationArea());
        sheet.getCells().get("E1").setStyle(discriptionDataStyle);

        sheet.getCells().get("D3").putValue("الدور");
        sheet.getCells().get("D3").setStyle(discriptionDataStyle);

        sheet.getCells().get("E3").putValue(pandsToJobOrdersRaws.get(0).getFloor());
        sheet.getCells().get("E3").setStyle(discriptionDataStyle);

        sheet.getCells().get("D5").putValue("البلوك");
        sheet.getCells().get("D5").setStyle(discriptionDataStyle);

        sheet.getCells().get("E5").putValue(pandsToJobOrdersRaws.get(0).getBlockNumber());
        sheet.getCells().get("E5").setStyle(discriptionDataStyle);

        sheet.getCells().get("F1").putValue("أسم المشروع");
        sheet.getCells().get("F1").setStyle(discriptionDataStyle);

        sheet.getCells().get("G1").putValue(pandsToJobOrdersRaws.get(0).getProjectName());
        sheet.getCells().get("G1").setStyle(discriptionDataStyle);

        sheet.getCells().get("F3").putValue("كود المشروع");
        sheet.getCells().get("F3").setStyle(discriptionDataStyle);

//        sheet.getCells().merge(2, 6, 1, 2);  // (startRow, startColumn, totalRows, totalColumns)

        sheet.getCells().get("G3").putValue(pandsToJobOrdersRaws.get(0).getProjectCode());
        sheet.getCells().get("G3").setStyle(discriptionDataStyle);

        sheet.getCells().get("F5").putValue("أسم المهندس");
        sheet.getCells().get("F5").setStyle(discriptionDataStyle);

//        sheet.getCells().merge(4, 6, 1, 2);  // (startRow, startColumn, totalRows, totalColumns)

        sheet.getCells().get("G5").putValue(pandsToJobOrdersRaws.get(0).getEngineerName());
        sheet.getCells().get("G5").setStyle(discriptionDataStyle);

//        sheet.getCells().get("F7").putValue("أسم المستخدم");
//        sheet.getCells().get("F7").setStyle(discriptionDataStyle);
//
//        sheet.getCells().get("G7").putValue(jwtUtils.userName);
//        sheet.getCells().get("G7").setStyle(discriptionDataStyle);


        InputStream imageStream = new ClassPathResource("static/Hossam-Zeitoun-Logo-Black.png").getInputStream();

        // Add the image to the worksheet (X, Y coordinates in pixels)
        // Place the image inside the merged cells (A1:C5)
        int pictureIndex = sheet.getPictures().add(0, 10, imageStream);

        // Get the added picture
        Picture picture = sheet.getPictures().get(pictureIndex);

        // Optionally, set the picture to fit within the merged area
        picture.setPlacement(PlacementType.MOVE);
        picture.setWidthScale(20); // Scale the image to fit width
        picture.setHeightScale(20);


        Style shadowStyle = workbook.createStyle();
        shadowStyle.setPattern(BackgroundType.SOLID);
        shadowStyle.setForegroundColor(Color.getDarkGray());
        shadowStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
        shadowStyle.getFont().setSize(15);

        sheet.getCells().get("A9").putValue("م");
        sheet.getCells().get("A9").setStyle(tableHeaderStyle);

        sheet.getCells().get("B9").putValue("كود البند");
        sheet.getCells().get("B9").setStyle(tableHeaderStyle);

        sheet.getCells().merge(8, 2, 1, 1);

        sheet.getCells().get("C9").putValue("التوصيف");
        sheet.getCells().get("C9").setStyle(tableHeaderStyle);


        sheet.getCells().get("D9").putValue("كود التصنيع");
        sheet.getCells().get("D9").setStyle(tableHeaderStyle);

//        sheet.getCells().get("E9").putValue("الملاحظات الفنية");
//        sheet.getCells().get("E9").setStyle(tableHeaderStyle);


        sheet.getCells().get("E9").putValue("الوحدة");
        sheet.getCells().get("E9").setStyle(tableHeaderStyle);

        sheet.getCells().get("F9").putValue("الخامة الفعلية");
        sheet.getCells().get("F9").setStyle(tableHeaderStyle);

        sheet.getCells().get("G9").putValue("نوع التشطيب");
        sheet.getCells().get("G9").setStyle(tableHeaderStyle);

        sheet.getCells().get("H9").putValue("العدد");
        sheet.getCells().get("H9").setStyle(tableHeaderStyle);

        sheet.getCells().get("I9").putValue("الطول");
        sheet.getCells().get("I9").setStyle(tableHeaderStyle);

        sheet.getCells().get("J9").putValue("العرض");
        sheet.getCells().get("J9").setStyle(tableHeaderStyle);

        sheet.getCells().get("K9").putValue("السمك");
        sheet.getCells().get("K9").setStyle(tableHeaderStyle);

        sheet.getCells().get("L9").putValue("التكرار");
        sheet.getCells().get("L9").setStyle(tableHeaderStyle);

        sheet.getCells().get("M9").putValue("الاجمالى");
        sheet.getCells().get("M9").setStyle(tableHeaderStyle);


        int rowIdx = 11;

        int totalQuantity = 0;
        double finalTotal = 0.0;
        DecimalFormat df = new DecimalFormat("#.###");

        List<PandsToJobOrder> pandsToJobOrders = new ArrayList<>();

        for (int i = 0; i < pandsToJobOrdersRaws.size(); i++) {
            pandsToJobOrders.addAll(pandsToJobOrderRepository.getByJobOrderIdAndRawType(pandsToJobOrdersRaws.get(i).getProjectProfileId()
                    , pandsToJobOrdersRaws.get(i).getJobOrderId(),
                    pandsToJobOrdersRaws.get(i).getRawType()));

//            pandsToJobOrders.add(null);

        }

        int flag = 0;

        for (int i = 0; i < pandsToJobOrders.size(); i++) {

            sheet.getCells().get("A" + rowIdx).putValue(i + 1);
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("A" + rowIdx).setStyle(shadowStyle);

            } else {
                sheet.getCells().get("A" + rowIdx).setStyle(discriptionDataStyle);
            }

            sheet.getCells().get("B" + rowIdx).putValue(pandsToJobOrders.get(i).getPandCode());
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("B" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("B" + rowIdx).setStyle(discriptionDataStyle);
            }


            int count = 0;
            if (pandsToJobOrders.get(i).getDescription().length() > 30) {
                int rowIndex = rowIdx;
                String input = pandsToJobOrders.get(i).getDescription();
                int partLength = 30;
                for (int d = 0; d < input.length(); d += partLength) {
                    int end = Math.min(d + partLength, input.length());
                    String part = input.substring(d, end);
                    count++;

                    Row row = cells.getRows().get(rowIndex);
                    row.setHeight(100);

                    sheet.getCells().get("C" + rowIndex).putValue(part + "\n");
//                    if (rowIndex % 2 != 0) {
                    sheet.getCells().get("C" + rowIndex).setStyle(shadowStyle);
//                    } else {
//                        sheet.getCells().get("C" + rowIndex).setStyle(discriptionDataStyle);
//                    }
                    rowIndex++;
                }
            } else {
                sheet.getCells().get("C" + rowIdx).putValue(pandsToJobOrders.get(i).getDescription());
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("C" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("C" + rowIdx).setStyle(discriptionDataStyle);
                }
            }
//            if (count > 0) {
//                sheet.getCells().merge(rowIdx, 2, count - 1, 2);
//            }


            sheet.getCells().get("D" + rowIdx).putValue(pandsToJobOrders.get(i).getManufacturingCode());
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("D" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("D" + rowIdx).setStyle(discriptionDataStyle);
            }

//            String value = pandsToJobOrders.get(i).getAdditionalDescription();
//
//            if (value.length() > 30) {
//
//                int splitIndex = value.indexOf(" ", 30);
//
//                if (splitIndex == -1) {
//                    // Fallback to character 50 if no space found
//                    splitIndex = 30;
//                }
//
//                String firstPart = value.substring(0, splitIndex).trim();
//                String secondPart = value.substring(splitIndex).trim();
//
//                // Set the split values
//                sheet.getCells().get("E" + rowIdx).setValue(firstPart);
//                sheet.getCells().get("E" + rowIdx + 1).setValue(secondPart);
//                flag = 1;
//
//                // Double the height of the original row
//                double originalHeight = sheet.getCells().getRowHeight(rowIdx);
//                sheet.getCells().setRowHeight(rowIdx, originalHeight * 2);
//            } else {
//                sheet.getCells().get("E" + rowIdx).putValue(pandsToJobOrders.get(i).getAdditionalDescription());
//            }
//
//            if (rowIdx % 2 != 0) {
//                sheet.getCells().get("E" + rowIdx).setStyle(shadowStyle);
//                if(flag == 1){
//                    sheet.getCells().get("E" + rowIdx + 1 ).setStyle(shadowStyle);
//                }
//            } else {
//                sheet.getCells().get("E" + rowIdx).setStyle(discriptionDataStyle);
//                if(flag == 1){
//                    sheet.getCells().get("E" + rowIdx + 1 ).setStyle(discriptionDataStyle);
//                }
//            }

            sheet.getCells().get("E" + rowIdx).putValue(pandsToJobOrders.get(i).getUnit());
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("E" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("E" + rowIdx).setStyle(discriptionDataStyle);
            }

            sheet.getCells().get("F" + rowIdx).putValue(pandsToJobOrders.get(i).getRawType());
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("F" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("F" + rowIdx).setStyle(discriptionDataStyle);
            }

            sheet.getCells().get("G" + rowIdx).putValue(pandsToJobOrders.get(i).getFinishType());
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("G" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("G" + rowIdx).setStyle(discriptionDataStyle);
            }

//            totalQuantity += pandsToJobOrders.get(i).getMainQuantity();
            sheet.getCells().get("H" + rowIdx).putValue(pandsToJobOrders.get(i).getMainQuantity());
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("H" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("H" + rowIdx).setStyle(discriptionDataStyle);
            }

            sheet.getCells().get("I" + rowIdx).putValue(pandsToJobOrders.get(i).getHeight());
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("I" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("I" + rowIdx).setStyle(discriptionDataStyle);
            }

            sheet.getCells().get("J" + rowIdx).putValue(pandsToJobOrders.get(i).getWidth());
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("J" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("J" + rowIdx).setStyle(discriptionDataStyle);
            }

            sheet.getCells().get("K" + rowIdx).putValue(pandsToJobOrders.get(i).getThickness());
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("K" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("K" + rowIdx).setStyle(discriptionDataStyle);
            }

            sheet.getCells().get("L" + rowIdx).putValue(pandsToJobOrders.get(i).getRepetition());
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("L" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("L" + rowIdx).setStyle(discriptionDataStyle);
            }

//            finalTotal += Double.valueOf(pandsToJobOrders.get(i).getMainTotal());
            sheet.getCells().get("M" + rowIdx).putValue(pandsToJobOrders.get(i).getMainTotal());
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("M" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("M" + rowIdx).setStyle(discriptionDataStyle);
            }

            if (count > 0) {
                rowIdx += count + 1;
            } else {
                rowIdx += 2;
            }


        }


        ////////////////adding the cube summition//////////////////////////

        ImageOrPrintOptions printOptions = new ImageOrPrintOptions();
        printOptions.setPrintingPage(PrintingPageType.DEFAULT);
        WorkbookRender render = new WorkbookRender(workbook, printOptions);
        int totalPages = render.getPageCount();

        // Calculate where the last page starts
        SheetRender sheetRender = new SheetRender(sheet, printOptions);
        int lastUsedRow = sheet.getCells().getMaxDataRow();


        rowIdx = lastUsedRow + 3;

        sheet.getCells().get("G" + rowIdx).putValue("الخامة");
        sheet.getCells().get("G" + rowIdx).setStyle(tableHeaderStyle);

        sheet.getCells().get("H" + rowIdx).setStyle(tableHeaderStyle);
        sheet.getCells().get("H" + rowIdx).putValue("السمك");

        sheet.getCells().get("I" + rowIdx).putValue("الوحدة");
        sheet.getCells().get("I" + rowIdx).setStyle(tableHeaderStyle);

        sheet.getCells().get("J" + rowIdx).putValue("اجمالى الوحدة");
        sheet.getCells().get("J" + rowIdx).setStyle(tableHeaderStyle);

        sheet.getCells().get("K" + rowIdx).putValue("المسطح");
        sheet.getCells().get("K" + rowIdx).setStyle(tableHeaderStyle);


        List<PandsToJobOrder> pandsToJobOrdersByRawType = new ArrayList<>();

        for (PandsToJobOrder pandsToJobOrdersRaw : pandsToJobOrdersRaws) {
            pandsToJobOrdersByRawType.addAll(pandsToJobOrderRepository.getByThicknessAndRawType(pandsToJobOrdersRaw.getProjectProfileId()
                    , pandsToJobOrdersRaw.getJobOrderId(),
                    pandsToJobOrdersRaw.getRawType()));
        }

        rowIdx += 1;
        for (PandsToJobOrder toJobOrder : pandsToJobOrdersByRawType) {

            sheet.getCells().get("G" + rowIdx).putValue(toJobOrder.getRawType());
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("G" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("G" + rowIdx).setStyle(discriptionDataStyle);
            }

            sheet.getCells().get("H" + rowIdx).putValue(toJobOrder.getThickness());
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("H" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("H" + rowIdx).setStyle(discriptionDataStyle);
            }

            sheet.getCells().get("I" + rowIdx).putValue(toJobOrder.getUnit());
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("I" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("I" + rowIdx).setStyle(discriptionDataStyle);
            }

            List<PandsToJobOrder> getByThicknessAndRawTypeAndUnit = pandsToJobOrderRepository.getByThicknessAndRawTypeAndUnit(pandsToJobOrdersRaws.getFirst().getProjectProfileId()
                    , pandsToJobOrdersRaws.getFirst().getJobOrderId(),
                    toJobOrder.getRawType(),
                    toJobOrder.getThickness(),
                    toJobOrder.getUnit());

            Double totalQuantityInCube = 0.0;
            Double totalSum = 0.0;

            if (!getByThicknessAndRawTypeAndUnit.isEmpty()) {
                for (PandsToJobOrder pandsToJobOrder : getByThicknessAndRawTypeAndUnit) {
                    totalSum += Double.parseDouble(pandsToJobOrder.getMainTotal());
                    totalQuantityInCube += (pandsToJobOrder.getMainQuantity() *
                            Double.parseDouble(pandsToJobOrder.getRepetition()) *
                            Double.parseDouble(pandsToJobOrder.getHeight()) *
                            Double.parseDouble(pandsToJobOrder.getWidth())) / 10000;
                }
            }


            sheet.getCells().get("J" + rowIdx).putValue(df.format(totalSum));
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("J" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("J" + rowIdx).setStyle(discriptionDataStyle);
            }

//            finalTotal += Double.valueOf(pandsToJobOrders.get(i).getMainTotal());

            sheet.getCells().get("K" + rowIdx).putValue(df.format(totalQuantityInCube));
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("K" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("K" + rowIdx).setStyle(discriptionDataStyle);
            }

            rowIdx++;
        }

        sheet.getCells().setRowHeight(8, 18);

//        for (int i = 10; i < rowIdx; i++) {
//            sheet.getCells().setColumnWidth(0, 5);
//        }


        //////////////////////////////////////////////////////////////////////
        // Adjust column widths to fit content

        sheet.autoFitColumns();

//        sheet.getHorizontalPageBreaks().clear();
//
//        int lastRow = cells.getMaxDataRow();
//        for (int row = 22; row <= lastRow; row += 22) {
//            sheet.getHorizontalPageBreaks().add(row);
//        }

        for (int i = 10; i < rowIdx; i++) {
            sheet.getCells().setRowHeight(i, 35);
        }

        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        workbook.save(pdfOutputStream, SaveFormat.PDF); // Save as PDF

        // 4. Return the PDF as a response
        ByteArrayInputStream pdfInputStream = new ByteArrayInputStream(pdfOutputStream.toByteArray());
        InputStreamResource resource = new InputStreamResource(pdfInputStream);

        return resource;
    }

    public InputStreamResource previewJobOrder(String id, int flag) throws Exception {

        List<PreviewJobOrder> pandsToJobOrdersPreview = new ArrayList<>();
        if (flag == 0) {
            pandsToJobOrdersPreview = previewJobOrderRepository.getByProjectCode(id);
        } else {
            pandsToJobOrdersPreview = previewJobOrderRepository.getByJobOrderId(id);
        }


        Workbook workbook = new Workbook();

        WorksheetCollection worksheets = workbook.getWorksheets();
        Worksheet sheet = worksheets.get(0);
        sheet.setDisplayRightToLeft(true);
        sheet.getPageSetup().setPrintTitleRows("$9:$9");
        Cells cells = sheet.getCells();

//            sheet.getCells().setRowHeight(7, 20);

        PageSetup pageSetup = sheet.getPageSetup();
        pageSetup.setOrientation(PageOrientationType.LANDSCAPE);
        pageSetup.setPaperSize(PaperSizeType.PAPER_A_3);


        pageSetup.setTopMargin(1);
        pageSetup.setBottomMargin(1);
        pageSetup.setLeftMargin(1);
        pageSetup.setRightMargin(1);

        pageSetup.setFitToPagesWide(1);     // Fit to one page wide
        pageSetup.setFitToPagesTall(0);     // Don't force page height
        pageSetup.setZoom(100);             // Prevent zoom from shrinking
        pageSetup.setPercentScale(false);   // Don't scale by percent

        Date dNow = new Date();
        SimpleDateFormat ft =
                new SimpleDateFormat("hh:mm:ss a");

        DateFormat formatter1 = new SimpleDateFormat("dd.MM.yy");


        Style tableHeaderStyle = sheet.getCells().get("C1").getStyle();
        tableHeaderStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
        tableHeaderStyle.setVerticalAlignment(TextAlignmentType.CENTER);
        tableHeaderStyle.getFont().setItalic(true);
        tableHeaderStyle.getFont().setSize(15);
        tableHeaderStyle.getFont().setBold(false);
        tableHeaderStyle.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());
        tableHeaderStyle.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
        tableHeaderStyle.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, Color.getBlack());
        tableHeaderStyle.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, Color.getBlack());

        Style discriptionDataStyle = sheet.getCells().get("F3").getStyle();
        discriptionDataStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
        discriptionDataStyle.setVerticalAlignment(TextAlignmentType.CENTER);
        discriptionDataStyle.getFont().setSize(15);


        sheet.getCells().get("A3").putValue("نوع");
        sheet.getCells().get("A3").setStyle(discriptionDataStyle);

        sheet.getCells().get("B3").putValue(pandsToJobOrdersPreview.getFirst().getJobOrderType());
        sheet.getCells().get("B3").setStyle(discriptionDataStyle);


        sheet.getCells().get("D1").putValue("منطقة التركيب");
        sheet.getCells().get("D1").setStyle(discriptionDataStyle);

//        sheet.getCells().merge(0, 4, 1, 5);  // (startRow, startColumn, totalRows, totalColumns)

        sheet.getCells().get("E1").putValue(pandsToJobOrdersPreview.getFirst().getInstallationArea());
        sheet.getCells().get("E1").setStyle(discriptionDataStyle);

        sheet.getCells().get("D3").putValue("الدور");
        sheet.getCells().get("D3").setStyle(discriptionDataStyle);

        sheet.getCells().get("E3").putValue(pandsToJobOrdersPreview.getFirst().getFloor());
        sheet.getCells().get("E3").setStyle(discriptionDataStyle);

        sheet.getCells().get("D5").putValue("البلوك");
        sheet.getCells().get("D5").setStyle(discriptionDataStyle);

        sheet.getCells().get("E5").putValue(pandsToJobOrdersPreview.getFirst().getBlockNumber());
        sheet.getCells().get("E5").setStyle(discriptionDataStyle);

        sheet.getCells().get("F1").putValue("أسم المشروع");
        sheet.getCells().get("F1").setStyle(discriptionDataStyle);

        sheet.getCells().get("G1").putValue(pandsToJobOrdersPreview.getFirst().getProjectName());
        sheet.getCells().get("G1").setStyle(discriptionDataStyle);

        sheet.getCells().get("F3").putValue("كود المشروع");
        sheet.getCells().get("F3").setStyle(discriptionDataStyle);

//        sheet.getCells().merge(2, 6, 1, 2);  // (startRow, startColumn, totalRows, totalColumns)

        sheet.getCells().get("G3").putValue(pandsToJobOrdersPreview.getFirst().getProjectCode());
        sheet.getCells().get("G3").setStyle(discriptionDataStyle);

        sheet.getCells().get("F5").putValue("أسم المهندس");
        sheet.getCells().get("F5").setStyle(discriptionDataStyle);

//        sheet.getCells().merge(4, 6, 1, 2);  // (startRow, startColumn, totalRows, totalColumns)

        sheet.getCells().get("G5").putValue(pandsToJobOrdersPreview.getFirst().getOfficerName());
        sheet.getCells().get("G5").setStyle(discriptionDataStyle);

//        sheet.getCells().get("F7").putValue("أسم المستخدم");
//        sheet.getCells().get("F7").setStyle(discriptionDataStyle);
//
//        sheet.getCells().get("G7").putValue(jwtUtils.userName);
//        sheet.getCells().get("G7").setStyle(discriptionDataStyle);


        InputStream imageStream = new ClassPathResource("static/Hossam-Zeitoun-Logo-Black.png").getInputStream();

        // Add the image to the worksheet (X, Y coordinates in pixels)
        // Place the image inside the merged cells (A1:C5)
        int pictureIndex = sheet.getPictures().add(0, 10, imageStream);

        // Get the added picture
        Picture picture = sheet.getPictures().get(pictureIndex);

        // Optionally, set the picture to fit within the merged area
        picture.setPlacement(PlacementType.MOVE);
        picture.setWidthScale(20); // Scale the image to fit width
        picture.setHeightScale(20);


        Style shadowStyle = workbook.createStyle();
        shadowStyle.setPattern(BackgroundType.SOLID);
        shadowStyle.setForegroundColor(Color.getDarkGray());
        shadowStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
        shadowStyle.getFont().setSize(15);

        sheet.getCells().get("A9").putValue("م");
        sheet.getCells().get("A9").setStyle(tableHeaderStyle);

        sheet.getCells().get("B9").putValue("كود البند");
        sheet.getCells().get("B9").setStyle(tableHeaderStyle);

        sheet.getCells().merge(8, 2, 1, 1);

        sheet.getCells().get("C9").putValue("التوصيف");
        sheet.getCells().get("C9").setStyle(tableHeaderStyle);


        sheet.getCells().get("D9").putValue("كود التصنيع");
        sheet.getCells().get("D9").setStyle(tableHeaderStyle);

//        sheet.getCells().get("E9").putValue("الملاحظات الفنية");
//        sheet.getCells().get("E9").setStyle(tableHeaderStyle);


        sheet.getCells().get("E9").putValue("الوحدة");
        sheet.getCells().get("E9").setStyle(tableHeaderStyle);

        sheet.getCells().get("F9").putValue("الخامة الفعلية");
        sheet.getCells().get("F9").setStyle(tableHeaderStyle);

        sheet.getCells().get("G9").putValue("نوع التشطيب");
        sheet.getCells().get("G9").setStyle(tableHeaderStyle);

        sheet.getCells().get("H9").putValue("العدد");
        sheet.getCells().get("H9").setStyle(tableHeaderStyle);

        sheet.getCells().get("I9").putValue("الطول");
        sheet.getCells().get("I9").setStyle(tableHeaderStyle);

        sheet.getCells().get("J9").putValue("العرض");
        sheet.getCells().get("J9").setStyle(tableHeaderStyle);

        sheet.getCells().get("K9").putValue("السمك");
        sheet.getCells().get("K9").setStyle(tableHeaderStyle);

        sheet.getCells().get("L9").putValue("التكرار");
        sheet.getCells().get("L9").setStyle(tableHeaderStyle);

        sheet.getCells().get("M9").putValue("الاجمالى");
        sheet.getCells().get("M9").setStyle(tableHeaderStyle);


        int rowIdx = 11;

        int totalQuantity = 0;
        double finalTotal = 0.0;
        DecimalFormat df = new DecimalFormat("#.###");

        for (int i = 0; i < pandsToJobOrdersPreview.size(); i++) {

            sheet.getCells().get("A" + rowIdx).putValue(i + 1);
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("A" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("A" + rowIdx).setStyle(discriptionDataStyle);
            }

            sheet.getCells().get("B" + rowIdx).putValue(pandsToJobOrdersPreview.get(i).getPandCode());
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("B" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("B" + rowIdx).setStyle(discriptionDataStyle);
            }


            int count = 0;
            if (pandsToJobOrdersPreview.get(i).getDescription().length() > 30) {
                int rowIndex = rowIdx;
                String input = pandsToJobOrdersPreview.get(i).getDescription();
                int partLength = 30;
                for (int d = 0; d < input.length(); d += partLength) {
                    int end = Math.min(d + partLength, input.length());
                    String part = input.substring(d, end);
                    count++;

                    Row row = cells.getRows().get(rowIndex);
                    row.setHeight(100);

                    sheet.getCells().get("C" + rowIndex).putValue(part + "\n");
//                    if (rowIndex % 2 != 0) {
                    sheet.getCells().get("C" + rowIndex).setStyle(shadowStyle);
//                    } else {
//                        sheet.getCells().get("C" + rowIndex).setStyle(discriptionDataStyle);
//                    }
                    rowIndex++;
                }
            } else {
                sheet.getCells().get("C" + rowIdx).putValue(pandsToJobOrdersPreview.get(i).getDescription());
                if (rowIdx % 2 != 0) {
                    sheet.getCells().get("C" + rowIdx).setStyle(shadowStyle);
                } else {
                    sheet.getCells().get("C" + rowIdx).setStyle(discriptionDataStyle);
                }
            }
//            if (count > 0) {
//                sheet.getCells().merge(rowIdx, 2, count - 1, 2);
//            }


            sheet.getCells().get("D" + rowIdx).putValue(pandsToJobOrdersPreview.get(i).getManufacturingCode());
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("D" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("D" + rowIdx).setStyle(discriptionDataStyle);
            }

//            String value = pandsToJobOrders.get(i).getAdditionalDescription();
//
//            if (value.length() > 30) {
//
//                int splitIndex = value.indexOf(" ", 30);
//
//                if (splitIndex == -1) {
//                    // Fallback to character 50 if no space found
//                    splitIndex = 30;
//                }
//
//                String firstPart = value.substring(0, splitIndex).trim();
//                String secondPart = value.substring(splitIndex).trim();
//
//                // Set the split values
//                sheet.getCells().get("E" + rowIdx).setValue(firstPart);
//                sheet.getCells().get("E" + rowIdx + 1).setValue(secondPart);
//                flag = 1;
//
//                // Double the height of the original row
//                double originalHeight = sheet.getCells().getRowHeight(rowIdx);
//                sheet.getCells().setRowHeight(rowIdx, originalHeight * 2);
//            } else {
//                sheet.getCells().get("E" + rowIdx).putValue(pandsToJobOrders.get(i).getAdditionalDescription());
//            }
//
//            if (rowIdx % 2 != 0) {
//                sheet.getCells().get("E" + rowIdx).setStyle(shadowStyle);
//                if(flag == 1){
//                    sheet.getCells().get("E" + rowIdx + 1 ).setStyle(shadowStyle);
//                }
//            } else {
//                sheet.getCells().get("E" + rowIdx).setStyle(discriptionDataStyle);
//                if(flag == 1){
//                    sheet.getCells().get("E" + rowIdx + 1 ).setStyle(discriptionDataStyle);
//                }
//            }

            sheet.getCells().get("E" + rowIdx).putValue(pandsToJobOrdersPreview.get(i).getUnit());
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("E" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("E" + rowIdx).setStyle(discriptionDataStyle);
            }

            sheet.getCells().get("F" + rowIdx).putValue(pandsToJobOrdersPreview.get(i).getRawType());
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("F" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("F" + rowIdx).setStyle(discriptionDataStyle);
            }

            sheet.getCells().get("G" + rowIdx).putValue(pandsToJobOrdersPreview.get(i).getFinishType());
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("G" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("G" + rowIdx).setStyle(discriptionDataStyle);
            }

//            totalQuantity += pandsToJobOrders.get(i).getMainQuantity();
            sheet.getCells().get("H" + rowIdx).putValue(pandsToJobOrdersPreview.get(i).getMainQuantity());
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("H" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("H" + rowIdx).setStyle(discriptionDataStyle);
            }

            sheet.getCells().get("I" + rowIdx).putValue(pandsToJobOrdersPreview.get(i).getHeight());
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("I" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("I" + rowIdx).setStyle(discriptionDataStyle);
            }

            sheet.getCells().get("J" + rowIdx).putValue(pandsToJobOrdersPreview.get(i).getWidth());
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("J" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("J" + rowIdx).setStyle(discriptionDataStyle);
            }

            sheet.getCells().get("K" + rowIdx).putValue(pandsToJobOrdersPreview.get(i).getThickness());
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("K" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("K" + rowIdx).setStyle(discriptionDataStyle);
            }

            sheet.getCells().get("L" + rowIdx).putValue(pandsToJobOrdersPreview.get(i).getRepetition());
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("L" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("L" + rowIdx).setStyle(discriptionDataStyle);
            }

//            finalTotal += Double.valueOf(pandsToJobOrders.get(i).getMainTotal());
            sheet.getCells().get("M" + rowIdx).putValue(pandsToJobOrdersPreview.get(i).getMainTotal());
            if (rowIdx % 2 != 0) {
                sheet.getCells().get("M" + rowIdx).setStyle(shadowStyle);
            } else {
                sheet.getCells().get("M" + rowIdx).setStyle(discriptionDataStyle);
            }

            if (count > 0) {
                rowIdx += count + 1;
            } else {
                rowIdx += 2;
            }


        }

        sheet.getCells().setRowHeight(8, 18);

//        for (int i = 10; i < rowIdx; i++) {
//            sheet.getCells().setColumnWidth(0, 5);
//        }


        //////////////////////////////////////////////////////////////////////
        // Adjust column widths to fit content

        sheet.autoFitColumns();

//        sheet.getHorizontalPageBreaks().clear();
//
//        int lastRow = cells.getMaxDataRow();
//        for (int row = 22; row <= lastRow; row += 22) {
//            sheet.getHorizontalPageBreaks().add(row);
//        }

        for (int i = 10; i < rowIdx; i++) {
            sheet.getCells().setRowHeight(i, 35);
        }

        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        workbook.save(pdfOutputStream, SaveFormat.PDF); // Save as PDF

        // 4. Return the PDF as a response
        ByteArrayInputStream pdfInputStream = new ByteArrayInputStream(pdfOutputStream.toByteArray());
        InputStreamResource resource = new InputStreamResource(pdfInputStream);
        previewJobOrderRepository.deleteByProjectId(pandsToJobOrdersPreview.get(0).getProjectProfileId());
        return resource;
    }


    @RequestScope
    @Transactional
    public List<PandsToJobOrder> saveListJobOrderPands(List<MarbleItemDto> pandsToJobOrder, HttpServletRequest request) throws SQLException {
        try {


            List<PandsToJobOrder> pandsToJobOrders = mapTopandsToJobOrder(pandsToJobOrder);

            if(pandsToJobOrders.getFirst().getFlag() == 1){
                return pandsToJobOrders;
            }

            pandsToJobOrders.getFirst().setFlag(0);
            Integer number = jobOrderService.getTheMaxNumber(pandsToJobOrders.getFirst().getProjectProfileId());
            GregorianCalendar gcalendar = new GregorianCalendar();

            JobOrder isJobOrderExist = new JobOrder();

            String username = changeHistoryLog.getUser(request);

            Date dNow = new Date();
            SimpleDateFormat ft =
                    new SimpleDateFormat("hh:mm:ss a");

            DateFormat formatter1 = new SimpleDateFormat("dd.MM.yy");
            isJobOrderExist.setProjectProfileId(pandsToJobOrders.getFirst().getProjectProfileId());
            isJobOrderExist.setJobOrderDate(formatter1.format(dNow));
            isJobOrderExist.setJobOrderTime(ft.format(dNow));
            int nextNumber = number + 1;
            isJobOrderExist.setJobOrderNumber(pandsToJobOrder.getFirst().getProjectCode().concat("/") + nextNumber + "/" + gcalendar.get(Calendar.YEAR));
            isJobOrderExist.setNumber(number + 1);
            isJobOrderExist.setProjectName(pandsToJobOrders.getFirst().getProjectName());
            isJobOrderExist.setProjectCode(pandsToJobOrder.getFirst().getProjectCode());
            isJobOrderExist.setInstallementArea(pandsToJobOrder.getFirst().getInstallationArea());
            isJobOrderExist.setCreatedBy(username);
            isJobOrderExist.setYear(gcalendar.get(Calendar.YEAR));
            isJobOrderExist.setApproved(false);
            isJobOrderExist.setPandsToJobOrders(new ArrayList<>());

            changeHistoryLog.saveChange(pandsToJobOrder.getFirst().getProjectCode().concat("/") + nextNumber + "/" + gcalendar.get(Calendar.YEAR)
                    , pandsToJobOrder.toString(), pandsToJobOrder.toString(), "save", request);

            isJobOrderExist.getPandsToJobOrders().addAll(pandsToJobOrders);

            pandsToJobOrderRepository.saveAll(pandsToJobOrders);
            jobOrderRepository.save(isJobOrderExist);

            return pandsToJobOrders;
        }catch (Exception e){
            e.printStackTrace();
            List<PandsToJobOrder> pandsToJobOrders = new ArrayList<>();
            PandsToJobOrder pandsToJobOrder1 = new PandsToJobOrder();
            pandsToJobOrder1.setFlag(1);
            pandsToJobOrder1.setMessage("حدث خطأ");
            pandsToJobOrders.add(pandsToJobOrder1);
            return pandsToJobOrders;
        }
    }

    private List<PandsToJobOrder> mapTopandsToJobOrder(List<MarbleItemDto> marbleItemDtos) {

            List<String> distinctPands = marbleItemDtos.stream()
                    .map(MarbleItemDto::getPandCode)   // extract the unit field
                    .filter(Objects::nonNull)      // optional: skip null units
                    .distinct()                    // keep only unique values
                    .toList();

        for (String distinctPand : distinctPands) {
            double totalQuantity = 0;
            double restQuantity = pandsRepository.findRestQuantityByPandCodeAndProjectProfileId(distinctPand, marbleItemDtos.getFirst().getProjectProfileId());
            totalQuantity = 0;
            for (MarbleItemDto marbleItemDto : marbleItemDtos) {
                if (marbleItemDto.getPandCode().equals(distinctPand)) {
                    totalQuantity += marbleItemDto.getTotal();
                }
            }

            if (totalQuantity > restQuantity) {
                List<PandsToJobOrder> pandsToJobOrders = new ArrayList<>();
                PandsToJobOrder pandsToJobOrder1 = new PandsToJobOrder();
                pandsToJobOrder1.setFlag(1);
                pandsToJobOrder1.setMessage(distinctPand + " الكمية المطلوبة اعلى من الكمية المتبقية فى البند ");
                pandsToJobOrders.add(pandsToJobOrder1);
                return pandsToJobOrders;
            }
        }
        DecimalFormat df = new DecimalFormat("#.###");

        List<PandsToJobOrder> pandsToJobOrderList = new ArrayList<>();

        Date dNow = new Date();
        SimpleDateFormat ft =
                new SimpleDateFormat("hh:mm:ss a");
        Integer number = jobOrderService.getTheMaxNumber(marbleItemDtos.getFirst().getProjectProfileId());
        GregorianCalendar gcalendar = new GregorianCalendar();
        int nextNumber = (number != null ? number : 0) + 1;


        double restTotal;

        for (int i = 0; i < marbleItemDtos.size(); i++) {
            Pand pand = pandsService.getPandByPandCode(marbleItemDtos.get(i).getPandCode(), marbleItemDtos.get(i).getProjectProfileId());



            PandsToJobOrder pandsToJobOrder = new PandsToJobOrder();

            pandsToJobOrder.setJobOrderTime(ft.format(dNow));

            UUID uuid = UuidCreator.getTimeBased();

            pandsToJobOrder.setRepetition(String.valueOf(marbleItemDtos.get(i).getRepetition()));
            pandsToJobOrder.setMainQuantity(marbleItemDtos.get(i).getQuantity());
            pandsToJobOrder.setUnit(marbleItemDtos.get(i).getUnit());
            pandsToJobOrder.setHeight(marbleItemDtos.get(i).getHeight());
            pandsToJobOrder.setWidth(marbleItemDtos.get(i).getWidth());

            double repetation = 0;
            if (pandsToJobOrder.getRepetition().equals("0") || pandsToJobOrder.getRepetition().isEmpty()) {
                repetation = 1;
                pandsToJobOrder.setRepetition(String.valueOf(Integer.parseInt("1")));
            } else {
                repetation = Double.parseDouble(pandsToJobOrder.getRepetition());
            }
            pandsToJobOrder.setQuantity(pandsToJobOrder.getMainQuantity() * repetation);



//            restTotal = Double.parseDouble(df.format(pand.getRestQuantity() - total));
            pandsToJobOrder.setJobOrderId(pand.getProjectCode().concat("/") + nextNumber + "/" + gcalendar.get(Calendar.YEAR));
            pandsToJobOrder.setUniqueId(uuid.toString());
            pandsToJobOrder.setTotal(df.format(marbleItemDtos.get(i).getTotal()));

            pandsToJobOrder.setMainTotal(df.format(marbleItemDtos.get(i).getTotal()));
            pandsToJobOrder.setThickness(String.valueOf(marbleItemDtos.get(i).getThickness()));
            pandsToJobOrder.setPandCode(pand.getPandCode());
            pandsToJobOrder.setManufacturingCode(marbleItemDtos.get(i).getManufacturingCode());
            pandsToJobOrder.setManufacturing(pand.getManufacturing());
            pandsToJobOrder.setJobOrderType(marbleItemDtos.getFirst().getJobOrderTybe());
            pandsToJobOrder.setProjectCode(pand.getProjectCode());
            pandsToJobOrder.setProjectName(pand.getProjectName());
//            pandsToJobOrder.setQuantityInPand(restTotal);
            pandsToJobOrder.setProjectProfileId(pand.getProjectProfileId());
            pandsToJobOrder.setFinishType(pand.getFinishType());
            pandsToJobOrder.setRawType(pand.getRawType());
            pandsToJobOrder.setRawUsed(pand.getRawUsed());
            pandsToJobOrder.setBlockNumber(marbleItemDtos.getFirst().getBlock());
            pandsToJobOrder.setFloor(marbleItemDtos.getFirst().getFloor());
            pandsToJobOrder.setJobOrderType(marbleItemDtos.getFirst().getJobOrderTybe());
            pandsToJobOrder.setEngineerName(marbleItemDtos.getFirst().getEngineerName());
            pandsToJobOrder.setInstallationArea(marbleItemDtos.getFirst().getInstallationArea());
            pandsToJobOrder.setDescription(marbleItemDtos.get(i).getDescription());
            pandsToJobOrderList.add(pandsToJobOrder);

//            pandsToJobOrderRepository.save(pandsToJobOrder);

        }

        for (String distinctPand : distinctPands) {
            double totalQuantity = 0;
            double restQuantity = pandsRepository.findRestQuantityByPandCodeAndProjectProfileId(distinctPand, marbleItemDtos.getFirst().getProjectProfileId());
            for (MarbleItemDto marbleItemDto : marbleItemDtos) {
                if (marbleItemDto.getPandCode().equals(distinctPand)) {
                    totalQuantity += Double.parseDouble(String.valueOf(marbleItemDto.getTotal()));
                }
            }
            Pand pand = pandsService.getPandByPandCode(distinctPand, marbleItemDtos.getFirst().getProjectProfileId());

            pand.setRestQuantity(Double.parseDouble(df.format(restQuantity - totalQuantity)));
            pandsRepository.save(pand);
        }

        return pandsToJobOrderList;
    }

}
