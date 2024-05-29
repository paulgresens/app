package com.company.app;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;


public class Utils {

    //connection details
    private static String dbHost = System.getenv("DB_HOST");
    private static String dbPort = System.getenv("DB_PORT");
    private static String dbName = System.getenv("DB_NAME");
    private static String dbUser = System.getenv("DB_USER");
    private static String dbPassword = System.getenv("DB_PASSWORD");
    private static String url = String.format("jdbc:postgresql://%s:%s/%s", dbHost, dbPort, dbName);
    private static Connection connection;

    //path to log file
    private static final String logFilePath = "inconsistent_data_log.txt";

    public static Connection getConnection() {
        return connection;
    }

    //method to connect to db
    public static Connection connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(url, dbUser, dbPassword);
            } catch (ClassNotFoundException e) {
                logException(e);
            }
        }
        return connection;
    }

    //method to close conn to db
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            logException(e);
        }
    }

    private static java.sql.Timestamp convertToTimestamp(String timestampString) {
        if (timestampString == null || timestampString.isEmpty()) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate localDate = LocalDate.parse(timestampString, formatter);
            LocalDateTime localDateTime = localDate.atStartOfDay();
            return java.sql.Timestamp.valueOf(localDateTime);
        } catch (DateTimeParseException e) {
            System.err.println("Failed to parse timestamp: " + e.getMessage());
            return null;
        }
    }

    // helper method to retrieve text Value by tag name
    private static String getTagValue(Element element, String tag) {
        if (element != null) {
            NodeList nodeList = element.getElementsByTagName(tag);
            if (nodeList.getLength() > 0) {
                return nodeList.item(0).getTextContent();
            }
        }
        return null;
    }

    // helper method to retrieve text value of attribute from given tag name (first occurence of attr in given el)
    private static String getAttributeValue(Element element, String tag, String attribute) {
        if (element != null) {
            NodeList nodeList = element.getElementsByTagName(tag);
            if (nodeList.getLength() > 0) {
                Node node = nodeList.item(0);
                if (node != null && node.hasAttributes()) {
                    Node attr = node.getAttributes().getNamedItem(attribute);
                    if (attr != null) {
                        return attr.getTextContent();
                    }
                }
            }
        }
        return null;
    }

    // helper method to retrieve text value of attribute from given tag name (ith occurence of attr in given el)
    private static String getAttributeValue(Element element, String tag, String attribute, int index) {
        if (element != null) {
            NodeList nodeList = element.getElementsByTagName(tag);
            if (nodeList.getLength() > 0) {
                Node node = nodeList.item(index);
                if (node != null && node.hasAttributes()) {
                    Node attr = node.getAttributes().getNamedItem(attribute);
                    if (attr != null) {
                        return attr.getTextContent();
                    }
                }
            }
        }
        return null;
    }

    // helper method to retrieve text value of attribute from given tag name (ith occurence of attr in given el)
    private static String getAttributeValueByList(NodeList list, String attribute, int index) {

        if (list.getLength() > 0) {
            Node node = list.item(index);
            if (node != null && node.hasAttributes()) {
                Node attr = node.getAttributes().getNamedItem(attribute);
                if (attr != null) {
                    return attr.getTextContent();
                }
            }
        }

        return null;
    }

    // helper method to paarse Ineger as Null
    private static Integer parseIntegerToNullable(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // helper method to paarse String as Null
    private static String parseStringToNullable(String value) {
        return (value == null || value.trim().isEmpty()) ? null : value;
    }

    // helper function that retrieves id from given table and give values that should uniquely identify row
    public static Integer getId(String tableName, Map<String, String> parameters) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Integer id = null;

        try {

            StringBuilder sql = new StringBuilder("SELECT id FROM ").append(tableName).append(" WHERE ");
            boolean first = true;
            for (String column : parameters.keySet()) {
                if (first) {
                    first = false;
                } else {
                    sql.append(" AND ");
                }
                sql.append(column).append(" = ?");
            }

            // Create the prepared statement
            pstmt = connection.prepareStatement(sql.toString());

            // Set the parameters
            int index = 1;
            for (String value : parameters.values()) {
                pstmt.setString(index++, value);
            }

            // Execute the query
            rs = pstmt.executeQuery();

            // Get the id from the result set
            if (rs.next()) {
                id = rs.getInt("id");
            }

        } catch (SQLException e) {
            logException(e);
        }
        return id;
    }

    // helper method to check if there is any entries in given table
    public static boolean checkEntries(String tableName, Map<String, String> params) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            // Build the SQL query dynamically
            StringBuilder query = new StringBuilder("SELECT COUNT(*) FROM " + tableName + " WHERE ");
            boolean first = true;
            for (String key : params.keySet()) {
                if (!first) {
                    query.append(" AND ");
                }
                query.append(key).append(" = ?");
                first = false;
            }

            // Prepare the statement
            pstmt = connection.prepareStatement(query.toString());

            // Set the parameter values
            int index = 1;
            for (String value : params.values()) {
                pstmt.setString(index++, value);
            }

            // Execute the query
            rs = pstmt.executeQuery();

            // Check if there are any matching records
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            logException(e);
        }

        return false;
    }

    public static void insertShop(Node node) {

        Element element = (Element) node;

        // retrieve rlevant data
        String name = element.getAttribute("name");
        String street = element.getAttribute("street");
        String zip = element.getAttribute("zip");

        // basic sql statement for inserts with placeholders
        String sql = "INSERT INTO branch_office (name, street, zip) VALUES (?, ?, ?);";

        // prepare statements set values for placeholders and excecute
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, street);
            preparedStatement.setString(3, zip);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logException(e);
        }
    }

    public static void insertProduct(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) child;
                // retireve relevant data
                String productId = element.getAttribute("asin");
                String picture = parseStringToNullable(element.getAttribute("picture"));
                Integer salesrank = parseIntegerToNullable(element.getAttribute("salesrank"));
                String title = getTagValue(element, "title");

                // build sql statement with placeholders
                String sql = "INSERT INTO product (product_number, title, salesrank, image) VALUES (?, ?, ?, ?);";

                // prepare statements set values for placeholders and excecute
                // both salesrank and pictur can be null
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, productId);
                    preparedStatement.setString(2, title);
                    if (salesrank != null) {
                        preparedStatement.setInt(3, salesrank);
                    } else {
                        preparedStatement.setNull(3, java.sql.Types.INTEGER);
                    }

                    if (picture != null) {
                        preparedStatement.setString(4, picture);
                    } else {
                        preparedStatement.setNull(4, java.sql.Types.VARCHAR);
                    }

                    preparedStatement.executeUpdate();

                } catch (SQLException e) {
                    logException(e);
                }
            }
        }

    }


    public static void insertCD(Node node) {
        NodeList children = node.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) child;
                String type = element.getAttribute("pgroup");

                if (type.equals("Music")) {
                    String productId = element.getAttribute("asin");
                    Element musicSpec = (Element) element.getElementsByTagName("musicspec").item(0);
                    String releaseDateString = getTagValue(musicSpec, "releasedate");
                    java.sql.Timestamp releaseDate = convertToTimestamp(releaseDateString);
                    String sql = "INSERT INTO cd (product_number, published_at) VALUES (?, ?);";
                    Element person_Element = (Element) element.getElementsByTagName("artists").item(0);

                    if (!(element.getElementsByTagName("artists").item(0).hasChildNodes() && getAttributeValue(person_Element, "artist", "name") == "")) {
                        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                            preparedStatement.setString(1, productId);
                            preparedStatement.setTimestamp(2, releaseDate);
                            preparedStatement.executeUpdate();
                        } catch (SQLException e) {
                            logException(e);
                        }

                    }
                }
            }

        }
    }


    public static void insertCDTitle(Node node) {
        NodeList products = node.getChildNodes();
        String cdTitleSql = "INSERT INTO cd_title (title, product_number) VALUES (?, ?);";
        for (int i = 0; i < products.getLength(); i++) {
            Node child = products.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element product = (Element) products.item(i);
                String productId = product.getAttribute("asin");
                Map<String, String> params = Map.of("product_number", productId);

                if (checkEntries("cd", params)) {
                    NodeList titleList = product.getElementsByTagName("title");

                    for (int j = 0; j < titleList.getLength(); j++) {
                        if (titleList.item(j).getParentNode().getNodeName().equals("tracks")) {
                            String title = titleList.item(j).getTextContent();
                            try (PreparedStatement cdTitleStatement = connection.prepareStatement(cdTitleSql)) {
                                //insert in cd_title
                                cdTitleStatement.setString(1, title);
                                cdTitleStatement.setString(2, productId);
                                cdTitleStatement.addBatch();
                            } catch (SQLException e) {
                                logException(e);
                            }

                        }

                    }
                }
            }
        }
    }

    public static void insertTitle(Node node) {

        Element element = (Element) node;
        NodeList title_list = element.getElementsByTagName("title");
        for (int i = 0; i < title_list.getLength(); i++) {
            Node parent = title_list.item(i).getParentNode();
            if (parent.getNodeName().equals("tracks")) {
                Element connectedProd = (Element) title_list.item(i).getParentNode().getParentNode();
                String connectedProdId = connectedProd.getAttribute("asin");
                Map<String, String> params = Map.of("product_number", connectedProdId);
                String titleSql = "INSERT INTO title (title) VALUES (?)";

                if (checkEntries("cd", params)) {
                    String title = title_list.item(i).getTextContent();

                    try (PreparedStatement titleStatement = connection.prepareStatement(titleSql)) {
                        titleStatement.setString(1, title);
                        titleStatement.executeUpdate();
                    } catch (SQLException e) {
                        logException(e);
                    }

                }
            }


        }

    }

    public static void insertLabels(Node node, String source) {
        Element element = (Element) node;
        NodeList labelList = element.getElementsByTagName("label");
        for (int i = 0; i < labelList.getLength(); i++) {

            Element connectedProd = (Element) labelList.item(i).getParentNode().getParentNode();
            String connectedProdId = connectedProd.getAttribute("asin");
            Map<String, String> params = Map.of("product_number", connectedProdId);
            String labelSql = "INSERT INTO label (name) VALUES (?)";

            if (checkEntries("cd", params)) {

                String label = null;

                if (source.toLowerCase().equals("leipzig")) {
                    label = getAttributeValueByList(labelList, "name", i);
                } else if (source.toLowerCase().equals("dresden")) {
                    label = labelList.item(i).getTextContent();
                }

                if (label != null) {

                    try (PreparedStatement labelStatement = connection.prepareStatement(labelSql)) {
                        labelStatement.setString(1, label);
                        labelStatement.executeUpdate();
                    } catch (SQLException e) {
                        logException(e);
                    }
                }

            }

        }

    }

    public static void insertCdLabels(Node node, String source) {
        NodeList products = node.getChildNodes();
        String cdLabelSql = "INSERT INTO cd_label (name, product_number) VALUES (?, ?);";
        for (int i = 0; i < products.getLength(); i++) {
            Node child = products.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element product = (Element) child;
                String productId = product.getAttribute("asin");
                Map<String, String> params = Map.of("product_number", productId);

                if (checkEntries("cd", params)) {

                    NodeList labelList = product.getElementsByTagName("label");

                    for (int j = 0; j < labelList.getLength(); j++) {

                        String label = null;

                        if (source.toLowerCase().equals("leipzig")) {
                            label = getAttributeValueByList(labelList, "name", j);
                        } else if (source.toLowerCase().equals("dresden")) {
                            label = labelList.item(j).getTextContent();
                        }

                        if (label != null) {
                            try (PreparedStatement cdLabelStatement = connection.prepareStatement(cdLabelSql)) {
                                cdLabelStatement.setString(1, label);
                                cdLabelStatement.setString(2, productId);
                                cdLabelStatement.executeUpdate();
                            } catch (SQLException e) {
                                logException(e);
                            }
                        }


                    }
                }
            }
        }
    }


    public static void insertBook(Node node) {
        NodeList products = node.getChildNodes();

        for (int i = 0; i < products.getLength(); i++) {
            Node child = products.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) child;
                String type = element.getAttribute("pgroup");

                if (type.equals("Book")) {
                    String productId = element.getAttribute("asin");
                    Element bookSpec = (Element) element.getElementsByTagName("bookspec").item(0);
                    String isbn = getAttributeValue(bookSpec, "isbn", "val");
                    Integer pages = parseIntegerToNullable(getTagValue(bookSpec, "pages"));
                    String publishedAtString = getAttributeValue(bookSpec, "publication", "date");
                    java.sql.Timestamp publishedAt = convertToTimestamp(publishedAtString);

                    String sql = "INSERT INTO book (product_number, number_pages, isbn, published_at) VALUES (?, ?, ?, ?);";
                    Element person_Element = (Element) element.getElementsByTagName("author").item(0);

                    if (!(element.getElementsByTagName("authors").item(0).hasChildNodes() && getAttributeValue(person_Element, "author", "name") == "")) {
                        try (PreparedStatement bookStatement = connection.prepareStatement(sql)) {
                            bookStatement.setString(1, productId);
                            if (pages != null) {
                                bookStatement.setInt(2, pages);
                            } else {
                                bookStatement.setNull(2, java.sql.Types.INTEGER);
                            }
                            bookStatement.setString(3, isbn);
                            bookStatement.setTimestamp(4, publishedAt);
                            bookStatement.executeUpdate();
                        } catch (SQLException e) {
                            logException(e);
                        }

                    }
                }
            }
        }
    }

    public static void insertBookPublisher(Node node, String source) {
        NodeList products = node.getChildNodes();
        String bookPublisherSql = "INSERT INTO book_publisher (name, product_number) VALUES (?, ?);";
        for (int i = 0; i < products.getLength(); i++) {
            Node child = products.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element product = (Element) child;
                String productId = product.getAttribute("asin");
                Map<String, String> params = Map.of("product_number", productId);

                if (checkEntries("book", params)) {
                    NodeList publisherList = product.getElementsByTagName("publisher");


                    for (int j = 0; j < publisherList.getLength(); j++) {
                        String publisher = null;

                        if (source.toLowerCase().equals("leipzig")) {
                            publisher = getAttributeValueByList(publisherList, "name", j);
                        } else if (source.toLowerCase().equals("dresden")) {
                            publisher = publisherList.item(j).getTextContent();
                        }

                        if (publisher != null) {

                            try (PreparedStatement bookPubisherStatement = connection.prepareStatement(bookPublisherSql)) {
                                bookPubisherStatement.setString(1, publisher);
                                bookPubisherStatement.setString(2, productId);
                                bookPubisherStatement.executeUpdate();
                            } catch (SQLException e) {
                                logException(e);
                            }
                        }

                    }
                }
            }
        }
    }

    public static void insertPublisher(Node node, String source) {
        Element element = (Element) node;
        NodeList publisherList = element.getElementsByTagName("publisher");
        for (int i = 0; i < publisherList.getLength(); i++) {

            Element connectedProd = (Element) publisherList.item(i).getParentNode().getParentNode();
            String connectedProdId = connectedProd.getAttribute("asin");
            Map<String, String> params = Map.of("product_number", connectedProdId);
            String publisherSql = "INSERT INTO publisher (name) VALUES (?)";

            if (checkEntries("book", params)) {

                String publisher = null;

                if (source.toLowerCase().equals("leipzig")) {
                    publisher = getAttributeValueByList(publisherList, "name", i);
                } else if (source.toLowerCase().equals("dresden")) {
                    publisher = publisherList.item(i).getTextContent();
                }

                if (publisher != null) {

                    try (PreparedStatement labelStatement = connection.prepareStatement(publisherSql)) {
                        labelStatement.setString(1, publisher);
                        labelStatement.executeUpdate();
                    } catch (SQLException e) {
                        logException(e);
                    }
                }

            }

        }

    }

    public static void insertDvd(Node node) {
        NodeList products = node.getChildNodes();

        for (int i = 0; i < products.getLength(); i++) {
            Node product = products.item(i);
            if (product.getNodeType() == Node.ELEMENT_NODE) {
                Element productElement = (Element) product;
                String type = productElement.getAttribute("pgroup");

                if (type.equals("DVD")) {
                    String productId = productElement.getAttribute("asin");
                    Element dvdSpec = (Element) productElement.getElementsByTagName("dvdspec").item(0);
                    String format = getTagValue(dvdSpec, "format");
                    String runningTime = getTagValue(dvdSpec, "runningtime");
                    String regionCode = getTagValue(dvdSpec, "regioncode");

                    String sql = "INSERT INTO dvd (product_number, format, runtime, region_code) VALUES (?, ?, ?, ?);";

                    try (PreparedStatement dvdStatement = connection.prepareStatement(sql)) {

                        dvdStatement.setString(1, productId);
                        dvdStatement.setString(2, format);
                        dvdStatement.setString(3, runningTime);
                        dvdStatement.setString(4, regionCode);
                        dvdStatement.executeUpdate();
                    } catch (SQLException e) {
                        logException(e);
                    }


                }
            }

        }
    }

    public static void insertPerson(Node node, String productType, String role, String source) {
        Element element = (Element) node;
        String parent = role + "s";

        NodeList personList = element.getElementsByTagName(role);
        for (int i = 0; i < personList.getLength(); i++) {

            Element connectedProd = (Element) personList.item(i).getParentNode().getParentNode();
            String connectedProdId = connectedProd.getAttribute("asin");
            Map<String, String> params = Map.of("product_number", connectedProdId);
            String personSql = "INSERT INTO person (name) VALUES (?)";

            if (checkEntries(productType, params)) {
                Element personElement = (Element) element.getElementsByTagName(parent).item(0);
                // adjust extraction according to source
                String person = null;

                if (source.toLowerCase().equals("leipzig")) {
                    person = getAttributeValue(personElement, role, "name", i);
                } else if (source.toLowerCase().equals("dresden")) {
                    person = personList.item(i).getTextContent();
                }

                if (person != null) {

                    // insert into people
                    try (PreparedStatement personStatement = connection.prepareStatement(personSql)) {
                        personStatement.setString(1, person);
                        personStatement.executeUpdate();

                    } catch (SQLException e) {
                        logException(e);
                    }
                }
            }

        }
    }

    public static void insertProductPerson(Node node, String productType, String role, String source) {
        NodeList products = node.getChildNodes();
        String parent = role + "s";
        String productPersonSql = "INSERT INTO person_product (name, product_number, role) VALUES (?,?,?);";
        for (int i = 0; i < products.getLength(); i++) {
            Node product = products.item(i);
            if (product.getNodeType() == Node.ELEMENT_NODE) {
                Element productElement = (Element) product;
                String productId = productElement.getAttribute("asin");
                Map<String, String> params = Map.of("product_number", productId);

                if (checkEntries(productType, params)) {
                    Element personsElement = (Element) productElement.getElementsByTagName(parent).item(0);
                    NodeList personList = productElement.getElementsByTagName(role);

                    for (int j = 0; j < personList.getLength(); j++) {
                        // adjust extraction according to source
                        String person = null;
                        if (source.toLowerCase().equals("leipzig")) {
                            person = getAttributeValue(personsElement, role, "name", i);
                        } else if (source.toLowerCase().equals("dresden")) {
                            person = personList.item(i).getTextContent();
                        }

                        if (person != null) {
                            try (PreparedStatement productPersonStatement = connection.prepareStatement(productPersonSql)) {
                                productPersonStatement.setString(1, person);
                                productPersonStatement.setString(2, productId);
                                productPersonStatement.setString(3, role);
                                productPersonStatement.executeUpdate();
                            } catch (SQLException e) {
                                logException(e);
                            }
                        } else {
                            throw new IllegalArgumentException("Invalid source: " + source);
                        }

                    }
                }
            }
        }
    }


    public static void insertConditions(Node node) {

        Element element = (Element) node;
        // get data for id retrieval
        String branchOfficeName = element.getAttributes().getNamedItem("name").getTextContent();
        String branchOfficeStreet = element.getAttributes().getNamedItem("street").getTextContent();
        String branchOfficeZip = element.getAttributes().getNamedItem("zip").getTextContent();

        // combine into Map
        Map<String, String> params = Map.of("name", branchOfficeName, "street", branchOfficeStreet, "zip", branchOfficeZip);

        //retrieve id for specific branch office
        int branchOfficeId = getId("branch_office", params);

        NodeList products = node.getChildNodes();
        String sql = "INSERT INTO prod_condition (product_number, branch_office_id, price, product_condition, currency) VALUES (?, ?, ?, ?,?);";

        for (int i = 0; i < products.getLength(); i++) {
            Node child = products.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {

                Element product = (Element) child;
                String productId = product.getAttribute("asin");
                Map<String, String> params_prod = Map.of("product_number", productId);

                if (checkEntries("product", params_prod)) {

                    // get relevant data for condition insert
                    Integer price = parseIntegerToNullable(getTagValue(product, "price"));
                    String state = parseStringToNullable(getAttributeValue(product, "price", "state"));
                    String currency = parseStringToNullable(getAttributeValue(product, "price", "currency"));

                    // build sql for count update
                    String updateSQL = "UPDATE prod_condition SET count = count + 1 WHERE product_number = ? and branch_office_id=? and product_condition=?;";

                    // prepare statements set values for placeholders and excecute
                    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                        preparedStatement.setString(1, productId);
                        preparedStatement.setInt(2, branchOfficeId);
                        if (price != null) {
                            preparedStatement.setInt(3, price);
                        } else {
                            preparedStatement.setNull(3, java.sql.Types.INTEGER);
                        }

                        preparedStatement.setString(4, state);

                        if (currency != null) {
                            preparedStatement.setString(5, currency);
                        }
                        preparedStatement.setNull(5, java.sql.Types.VARCHAR);

                        preparedStatement.executeUpdate();
                    } catch (SQLException e) {

                        // if duplicate then up the count
                        if (e.getErrorCode() == 23505) {
                            try (PreparedStatement updateStmt = connection.prepareStatement(updateSQL)) {
                                updateStmt.setString(1, productId);
                                updateStmt.setInt(2, branchOfficeId);
                                updateStmt.setString(3, state);
                                updateStmt.executeUpdate();

                            } catch (SQLException f) {
                                f.printStackTrace();
                            }
                        } else {
                            logException(e);
                        }
                    }
                }
            }
        }
    }

    public static void insertSimilars(Node node, String source) {

        NodeList products = node.getChildNodes();

        for (int i = 0; i < products.getLength(); i++) {
            Node child = products.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element product = (Element) child;
                String productId = product.getAttribute("asin");
                Map<String, String> params_prod = Map.of("product_number", productId);

                if (checkEntries("product", params_prod)) {
                    Element similarsElement = (Element) product.getElementsByTagName("similars").item(0);
                    if (similarsElement != null) {
                        // check data source to adjust data exctraction
                        // case leipzig
                        if (source.toLowerCase().equals("leipzig")) {
                            NodeList simList = similarsElement.getElementsByTagName("sim_product");
                            String sql = "INSERT INTO similarity (product_number_1, product_number_2) VALUES (?, ?);";

                            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                                for (int k = 0; k < simList.getLength(); k++) {
                                    Node simProductNode = simList.item(k);
                                    if (simProductNode.getNodeType() == Node.ELEMENT_NODE) {
                                        Element simProductElement = (Element) simProductNode;
                                        String sim = getTagValue(simProductElement, "asin");
                                        // avoid duplicates
                                        if (productId.compareTo(sim) > 0) {
                                            preparedStatement.setString(1, productId);
                                            preparedStatement.setString(2, sim);
                                        } else {
                                            preparedStatement.setString(1, sim);
                                            preparedStatement.setString(2, productId);
                                        }

                                        preparedStatement.addBatch();
                                    }

                                }
                                preparedStatement.executeBatch();
                            } catch (SQLException e) {
                                logException(e);
                            }
                        }

                        // case Dresden
                        else if (source.toLowerCase().equals("dresden")) {
                            NodeList simList = similarsElement.getElementsByTagName("item");
                            String sql = "INSERT INTO similarity (product_number_1, product_number_2) VALUES (?, ?);";

                            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                                for (int j = 0; i < simList.getLength(); j++) {
                                    String sim = getAttributeValue(similarsElement, "item", "asin", j);
                                    // avoid duplicates
                                    if (productId.compareTo(sim) > 0) {
                                        preparedStatement.setString(1, productId);
                                        preparedStatement.setString(2, sim);
                                    } else {
                                        preparedStatement.setString(1, sim);
                                        preparedStatement.setString(2, productId);
                                    }
                                    preparedStatement.executeUpdate();
                                    connection.commit();
                                }
                                //preparedStatement.executeBatch();
                            } catch (SQLException e) {
                                logException(e);
                            }
                        }
                        // throw exception if source doesnt exist
                        else {
                            throw new IllegalArgumentException("Invalid source: " + source);
                        }
                    }
                }
            }
        }
    }


    public static void insertCategory(Node cat, int supId) {

        //initialize with -1
        int catId = -1;
        String insertSql = "Insert into category (name, super_category) Values (?,?)";

        try (PreparedStatement statement = connection.prepareStatement(insertSql, new String[]{"id"})) {
            String category = cat.getFirstChild().getNodeValue().replace("\n", "").trim();
            statement.setString(1, category);
            if (supId == -1) {
                statement.setNull(2, java.sql.Types.INTEGER);
            } else {
                statement.setInt(2, supId);
            }
            statement.executeUpdate();

            // update catId to what has been generated in Category Insert
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    catId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating category failed, no ID obtained.");
                }
            }
            statement.close();
        } catch (SQLException e) {
            logException(e);
        }

        // insert into Product Category using the retrieved category
        NodeList children = cat.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeName().equals("item")) {
                insertProductCategory(catId, child.getTextContent());
            }
        }

        // recursively insert in sub categories
        for (int j = 0; j < children.getLength(); j++) {
            Node child = children.item(j);
            if (child.getNodeName().equals("category")) {
                insertCategory(child, catId);
            }
        }

    }

    public static void insertProductCategory(int catId, String productNumber) {
        String insertSql = "Insert into product_category (id, product_number) values(?,?)";
        try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
            statement.setInt(1, catId);
            statement.setString(2, productNumber);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            logException(e);
        }
    }

    private static void logException(Exception e) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
            writer.write("Exception: " + e.toString());
            writer.newLine();
            for (StackTraceElement element : e.getStackTrace()) {
                writer.write("\tat " + element.toString());
                writer.newLine();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


}