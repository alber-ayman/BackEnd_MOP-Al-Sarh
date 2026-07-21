package com.example.demo.service;

import com.example.demo.models.NewReturnJobOrders;
import com.example.demo.models.Pand;
import com.example.demo.models.ProjectProfile;
import com.example.demo.repository.NewReturnRepository;
import com.example.demo.repository.PandsRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

@Service
public class NewReturnService {

    private final ChangeHistoryLog changeHistoryLog;
    private final NewReturnRepository newReturnRepository;
    private final PandsRepository pandsRepository;

    public NewReturnService(ChangeHistoryLog changeHistoryLog, NewReturnRepository newReturnRepository, PandsRepository pandsRepository) {
        this.changeHistoryLog = changeHistoryLog;
        this.newReturnRepository = newReturnRepository;
        this.pandsRepository = pandsRepository;
    }

    public List<NewReturnJobOrders> getReturnsByProjectId(Long id) {
        return newReturnRepository.findByProjectProfileId(id);
    }

    public NewReturnJobOrders addNew(
            NewReturnJobOrders newReturnJobOrdersRequest,
            HttpServletRequest request) {
        try {

            Pand pand = pandsRepository.findByPandCodeAndProjectCode(newReturnJobOrdersRequest.getPandCode(),
                    newReturnJobOrdersRequest.getProjectCode());

            NewReturnJobOrders currentReturnJobOrders = newReturnRepository.findByPandCodeAndProjectCode(newReturnJobOrdersRequest.getPandCode(),
                    newReturnJobOrdersRequest.getProjectCode());
            DecimalFormat df = new DecimalFormat("#.###");

            double total;

            String height;
            String width;
            if (newReturnJobOrdersRequest.getHeight() == null) {
                height = "1";
            } else {
                height = newReturnJobOrdersRequest.getHeight();
            }

            if (newReturnJobOrdersRequest.getWidth() == null) {
                width = "1";
            } else {
                width = newReturnJobOrdersRequest.getWidth();
            }

            if (pand.getUnit().equals("متر مربع")) {
                total = (Double.parseDouble(height) * Double.parseDouble(width) * newReturnJobOrdersRequest.getQuantity()) / 10000;
            } else if (pand.getUnit().equals("متر طولى")) {
                total = (Double.parseDouble(height) * newReturnJobOrdersRequest.getQuantity()) / 100;
            } else {
                total = newReturnJobOrdersRequest.getQuantity();
            }



            String formattedNumber = df.format(total);
            if (currentReturnJobOrders == null) {
                newReturnJobOrdersRequest.setTotal(Double.parseDouble(formattedNumber));
                newReturnJobOrdersRequest.setProjectProfileId(pand.getProjectProfileId());
                newReturnJobOrdersRequest.setUnit(pand.getUnit());
                newReturnRepository.save(newReturnJobOrdersRequest);
            } else {
                currentReturnJobOrders.setTotal(Double.parseDouble(df.format(currentReturnJobOrders.getTotal() + total)));
                currentReturnJobOrders.setQuantity(Double.parseDouble(df.format(currentReturnJobOrders.getQuantity() +
                        newReturnJobOrdersRequest.getQuantity())));
                currentReturnJobOrders.setUnit(pand.getUnit());
                newReturnRepository.save(currentReturnJobOrders);
            }


            changeHistoryLog.saveChange(newReturnJobOrdersRequest.getPandCode(), newReturnJobOrdersRequest.toString(), newReturnJobOrdersRequest.toString(), "save", request);

            return currentReturnJobOrders;
        } catch (Exception e) {
            e.printStackTrace();
            return newReturnJobOrdersRequest;
        }
    }
}
