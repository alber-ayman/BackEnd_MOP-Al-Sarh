package com.example.demo.service;

import com.example.demo.models.*;
import com.example.demo.repository.ExitJobOrderRepository;
import com.example.demo.repository.ExitProcessJobOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ExitProcessJobOrderService {

    @Autowired
    ExitProcessJobOrderRepository exitProcessJobOrderRepository;

    @Autowired
    ExitJobOrderRepository exitJobOrderRepository;

    public ResponseEntity<JobOrderParent> saveExitProcessJobOrder(JobOrderParent exitJobOrders, String unifiedSerial) {
        try {
            double total;

            for (int i = 0; i < exitJobOrders.getPandsToJobOrderList().size(); i++) {
                ExitProcessJobOrder exitJobOrder = new ExitProcessJobOrder();
                exitJobOrder = mappingJobOrder(exitJobOrders.getPandsToJobOrderList().get(i));

                if (exitJobOrders.getPandsToJobOrderList().get(i).getUnit().equals("متر مربع")) {
                    total = (Double.valueOf(exitJobOrders.getPandsToJobOrderList().get(i).getHeight()) * Double.valueOf(exitJobOrders.getPandsToJobOrderList().get(i).getWidth()) * Double.valueOf(exitJobOrders.getPandsToJobOrderList().get(i).getQuantity())) / 10000;
                } else if (exitJobOrders.getPandsToJobOrderList().get(i).getUnit().equals("متر طولى")) {
                    total = (Double.valueOf(exitJobOrders.getPandsToJobOrderList().get(i).getHeight()) * Double.valueOf(exitJobOrders.getPandsToJobOrderList().get(i).getQuantity())) / 100;
                } else {
                    total = Double.valueOf(exitJobOrders.getPandsToJobOrderList().get(i).getQuantity());
                }

                exitJobOrder.setSerialNumber(exitJobOrderRepository.getLastSerialNumber());
                exitJobOrder.setTotal(String.valueOf(total));
                exitJobOrder.setUnifiedSerial(unifiedSerial);
                System.out.println("unifiedSerial: " + unifiedSerial);
                exitProcessJobOrderRepository.save(exitJobOrder);
            }
            return new ResponseEntity<>(exitJobOrders, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    public ExitProcessJobOrder mappingJobOrder(PandsToJobOrder updatedJobOrder) {

        ExitProcessJobOrder jobOrder = new ExitProcessJobOrder();

        jobOrder.setJobOrderId(updatedJobOrder.getJobOrderId());
        jobOrder.setUnifiedSerial(updatedJobOrder.getUnifiedSerial());
        jobOrder.setProjectProfileId((updatedJobOrder.getProjectProfileId()));
        jobOrder.setProjectCode(updatedJobOrder.getProjectCode());
        jobOrder.setProjectName(updatedJobOrder.getProjectName());
        jobOrder.setEngineerName(updatedJobOrder.getEngineerName());
        jobOrder.setJobOrderType(updatedJobOrder.getJobOrderType());
        jobOrder.setInstallationArea(updatedJobOrder.getInstallationArea());
        jobOrder.setUniqueId(updatedJobOrder.getUniqueId());
        jobOrder.setPandCode(updatedJobOrder.getPandCode());
        jobOrder.setDescription(updatedJobOrder.getDescription());
        jobOrder.setManufacturing(updatedJobOrder.getManufacturing());
        jobOrder.setRawType(updatedJobOrder.getRawType());
        jobOrder.setRawUsed(updatedJobOrder.getRawUsed());
        jobOrder.setFinishType(updatedJobOrder.getFinishType());
        jobOrder.setThickness(updatedJobOrder.getThickness());
        jobOrder.setBlockNumber(updatedJobOrder.getBlockNumber());
        jobOrder.setFloor(updatedJobOrder.getFloor());
        jobOrder.setOfficerName(updatedJobOrder.getOfficerName());
        jobOrder.setHeight(updatedJobOrder.getHeight());
        jobOrder.setWidth(updatedJobOrder.getWidth());
        jobOrder.setQuantity(updatedJobOrder.getQuantity());
        jobOrder.setRepetition(updatedJobOrder.getRepetition());
        jobOrder.setTotal(updatedJobOrder.getTotal());
        jobOrder.setUnit(updatedJobOrder.getUnit());

        jobOrder.setAdditionalDescription(updatedJobOrder.getAdditionalDescription());

        return jobOrder;
    }

    public void deleteAll() {
        exitProcessJobOrderRepository.deleteAll();
    }
}
