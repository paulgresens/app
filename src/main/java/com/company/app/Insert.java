package com.company.app;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

public class Insert {

    public static void processXMLFile(File file, String encoding) {
        try (InputStream inputStream = new FileInputStream(file);
             InputStreamReader reader = new InputStreamReader(inputStream, encoding)) {

            // Parse XML file
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(reader));
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

            Node rootElement = doc.getDocumentElement();

            Utils.insertShop(rootElement);
            Utils.insertProduct(rootElement);

            Utils.insertCD(rootElement);
            Utils.insertBook(rootElement);
            Utils.insertDvd(rootElement);

            Utils.insertTitle(rootElement);
            Utils.insertCDTitle(rootElement);
            Utils.insertLabels(rootElement, "leipzig");
            Utils.insertCdLabels(rootElement, "leipzig");
            Utils.insertPublisher(rootElement, "leipzig");
            Utils.insertBookPublisher(rootElement, "leipzig");

            // insert all artists
            Utils.insertPerson(rootElement, "cd", "artist", "leipzig");
            Utils.insertProductPerson(rootElement, "cd", "artist", "leipzig");

            // insert all authors
            Utils.insertPerson(rootElement, "book", "author", "leipzig");
            Utils.insertProductPerson(rootElement, "book", "author", "leipzig");

            // insert all creators
            Utils.insertPerson(rootElement, "dvd", "creator", "leipzig");
            Utils.insertProductPerson(rootElement, "dvd", "creator", "leipzig");

            // insert all directors
            Utils.insertPerson(rootElement, "dvd", "director", "leipzig");
            Utils.insertProductPerson(rootElement, "dvd", "director", "leipzig");

            //insert all actors
            Utils.insertPerson(rootElement, "dvd", "actor", "leipzig");
            Utils.insertProductPerson(rootElement, "dvd", "actor", "leipzig");

            Utils.insertSimilars(rootElement, "leipzig");
            Utils.insertConditions(rootElement);



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
            Utils.insertProduct(rootElement);

            Utils.insertCD(rootElement);
            Utils.insertBook(rootElement);
            Utils.insertDvd(rootElement);

            Utils.insertTitle(rootElement);
            Utils.insertCDTitle(rootElement);
            Utils.insertLabels(rootElement, "dresden");
            Utils.insertCdLabels(rootElement, "dresden");
            Utils.insertPublisher(rootElement, "dresden");
            Utils.insertBookPublisher(rootElement, "dresden");

            Utils.insertPerson(rootElement, "cd", "artist", "dresden");
            Utils.insertPerson(rootElement, "book", "author", "dresden");
            Utils.insertPerson(rootElement, "dvd", "creator", "dresden");
            Utils.insertPerson(rootElement, "dvd", "director", "dresden");
            Utils.insertPerson(rootElement, "dvd", "actor", "dresden");

            Utils.insertSimilars(rootElement, "dresden");
            Utils.insertConditions(rootElement);

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



