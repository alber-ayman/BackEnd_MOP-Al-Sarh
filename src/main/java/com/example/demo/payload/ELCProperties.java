///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package eg.com.khales.paymentgateway.payload;
//
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.Properties;
//
///**
// *
// * @author Alber_Ayman
// */
//
//public class ELCProperties {
//    static Properties properties;
//
//    static {
//        try {
//            properties = new Properties();
//            String propertiesFile = System.getProperty("ftproperties");
//            if (propertiesFile == null) {
//                properties.load(ELCProperties.class.getResourceAsStream("/configuration.properties"));
//            } else {
//                properties.load(new FileReader(propertiesFile));
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static String getProperty(String property) {
//        return (String) properties.get(property);
//    }
//}
