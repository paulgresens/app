package com.company.app;

import java.sql.Connection;
import java.io.File;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Insert {

    public static void processXMLFile(File file) {
        try {
            // Parse XML file
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            Node rootElement = doc.getDocumentElement();

            // Determine the appropriate approach based on XML content
            if (rootElement.getNodeName().equals("shop")) {
                Element element = (Element) rootElement;
                String name = element.getAttribute("name");
                if (name.equals("Leipzig")) {
                    processLeipzig(doc);
                } else if (name.equals("Dresden")) {
                    processDresden(doc);
                } else {
                    System.out.println("Unrecognized XML structure in file: " + file.getName());
                }
            } else if (rootElement.getNodeName().equals("categories")) {

                processCategories(doc);
            } else {
                // Handle unrecognized XML structure
                System.out.println("Unrecognized XML structure in file: " + file.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Define methods for processing each type of XML file
    public static void processLeipzig(Document doc) {

        try {
            Utils.connect();
            Connection connection = Utils.getConnection();
            connection.setAutoCommit(true);

            System.err.println(connection);
            System.err.println("-----------------------------------------------------");
            Node rootElement = doc.getDocumentElement();

            Utils.insertShop(rootElement);
            //connection.commit();

            if (rootElement.hasChildNodes()) {
                NodeList children = rootElement.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    Node child = children.item(i);
                    if (child.getNodeType() == Node.ELEMENT_NODE) {
                        Utils.insertProduct(child, "leipzig");
                    }
                }
            }

            //connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                Connection connection = Utils.getConnection();
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        } finally {
            Utils.closeConnection();
        }
    }

    public static void processDresden(Document doc) {
        try {
            Utils.connect();
            Connection connection = Utils.getConnection();
            connection.setAutoCommit(true);
            Node rootElement = doc.getDocumentElement();

            Utils.insertShop(rootElement);
            //connection.commit();

            if (rootElement.hasChildNodes()) {
                NodeList children = rootElement.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    Node child = children.item(i);
                    if (child.getNodeType() == Node.ELEMENT_NODE) {
                        Utils.insertProduct(child, "dresden");
                        //connection.commit();
                    }
                }
            }

            //connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                Connection connection = Utils.getConnection();
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        } finally {
            Utils.closeConnection();
        }
    }

    public static void processCategories(Document doc) {
        try {
            Utils.connect();
            Connection connection = Utils.getConnection();
            connection.setAutoCommit(true);
            Node rootElement = doc.getDocumentElement();

            if (rootElement.hasChildNodes()) {
                NodeList children = rootElement.getChildNodes();

                for (int i = 0; i < children.getLength(); i++) {
                    Node child = children.item(i);
                    if (child.getNodeType() == Node.ELEMENT_NODE) {
                        Utils.insertCategory(child, -1);
                    }
                }
            }

            //connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                Connection connection = Utils.getConnection();
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        } finally {
            Utils.closeConnection();
        }
    }
}
  



