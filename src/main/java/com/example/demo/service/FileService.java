package com.example.demo.service;

//import com.itextpdf.io.image.ImageDataFactory;
//import com.itextpdf.layout.element.Image;
//import com.itextpdf.layout.element.Table;

import com.example.demo.models.*;
import com.example.demo.repository.ExitJobOrderRepository;
import com.example.demo.repository.ExitProcessJobOrderRepository;
import com.example.demo.repository.PandsToJobOrderRepository;
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.layout.Document;
//import com.itextpdf.layout.element.Paragraph;
//import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
//import org.apache.poi.wp.usermodel.Paragraph;
import org.apache.poi.xssf.usermodel.XSSFPrintSetup;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class FileService {

    @Autowired
    PandsToJobOrderService pandsToJobOrderService;
    @Autowired
    ExitProcessJobOrderRepository exitProcessJobOrderRepository;

    @Autowired
    JobOrderService jobOrderService;

    private List<PandsToJobOrder> getByJobOrder(String jobOrderId) {
        List<PandsToJobOrder> jobOrders = pandsToJobOrderService.getByJobOrderId(jobOrderId);

        return jobOrders;
    }



}
