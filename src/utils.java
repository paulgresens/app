import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class utils {

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

  public static Connection getConnection(){
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
    String sql = "INSERT INTO branch_office (name, street, zip) VALUES (?, ?, ?)";

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

  public static void insertProduct(Node node, String source) {
      Element element = (Element) node;

      // retireve relevant data
      String productId = element.getAttribute("asin");
      String picture = element.getAttribute("picture");
      Integer salesrank = parseIntegerToNullable(element.getAttribute("salesrank"));
      String title = getTagValue(element, "title");

      // build sql statement with placeholders
      String sql = "INSERT INTO product (product_number, title, salesrank, image) VALUES (?, ?, ?, ?)";

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

          if (picture!=null){
            preparedStatement.setString(4, picture);
          } else {
            preparedStatement.setNull(4, java.sql.Types.VARCHAR);
          }
         
          preparedStatement.executeUpdate();
          
      } catch (SQLException e) {
          logException(e);
      }
      
      // inserting in all related tables 
      insertSimilars(element, productId,source);
      insertSubProduct(element, productId, source);
      insertConditions(element, productId);
  }

  private static void insertConditions(Element element, String productId){

    // get data for id retrieval
    String branchOfficeName=element.getParentNode().getAttributes().getNamedItem("name").getTextContent();
    String branchOfficeStreet=element.getParentNode().getAttributes().getNamedItem("street").getTextContent();
    String branchOfficeZip=element.getParentNode().getAttributes().getNamedItem("zip").getTextContent();

    // combine into Map
    Map<String, String> params = Map.of("name", branchOfficeName, "street", branchOfficeStreet, "zip", branchOfficeZip);
    
    //retrieve id for specific branch office
    int branchOfficeId=getId("branch_office", params);

    // get relevant data for condition insert
    Integer price= parseIntegerToNullable(getTagValue(element, "price"));
    String state = parseStringToNullable(getAttributeValue(element, "price", "state"));
    String currency = parseStringToNullable(getAttributeValue(element, "price", "currency"));
    
    // build sql Statement with placeholders for product_condition statement 
    String sql = "INSERT INTO prod_condition (product_number, branch_office_id, price, product_condition, currency) VALUES (?, ?, ?, ?,?)";
    
    // build sql for count update 
    String updateSQL = "UPDATE prod_condition SET count = count + 1 WHERE product_number = ? and branch_office_id=? and product_condition=?";

    // prepare statements set values for placeholders and excecute 
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, productId);
      preparedStatement.setInt(2, branchOfficeId);
      if (price!=null){
        preparedStatement.setInt(3, price);
      } else{
        preparedStatement.setNull(3, java.sql.Types.INTEGER);
      }

      preparedStatement.setString(4, state);

      if (currency!= null){
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
              
          }  catch (SQLException f){
              f.printStackTrace();
            }
      } else {
            logException(e);
      }
    }
  }

  private static void insertSimilars(Element element, String productId, String source) {
    Element similarsElement = (Element) element.getElementsByTagName("similars").item(0);
    // check if there is similars 
    if (similarsElement != null) {
      // check data source to adjust data exctraction
      // case leipzig 
      if (source.toLowerCase().equals("leipzig")){
        NodeList simList = similarsElement.getElementsByTagName("sim_product");
        String sql = "INSERT INTO similar (product_number_1, product_number_2) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < simList.getLength(); i++) {
              Node simProductNode = simList.item(i);
              if (simProductNode.getNodeType() == Node.ELEMENT_NODE) {
                Element simProductElement = (Element) simProductNode;
                String sim = getTagValue(simProductElement, "asin");
                // avoid duplicates
                if (productId.compareTo(sim)>0){
                    preparedStatement.setString(1, productId);
                    preparedStatement.setString(2, sim);
                }
                else{
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
      else if (source.toLowerCase().equals("dresden")){
        NodeList simList = similarsElement.getElementsByTagName("item");
        String sql = "INSERT INTO similar (product_number_1, product_number_2) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < simList.getLength(); i++) {
              String sim = getAttributeValue(similarsElement, "item", "asin", i);
              // avoid duplicates
              if (productId.compareTo(sim)>0){
                  preparedStatement.setString(1, productId);
                  preparedStatement.setString(2, sim);
              }
              else{
                  preparedStatement.setString(1, sim);
                  preparedStatement.setString(2, productId);
              }
              preparedStatement.addBatch();
            }                
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            logException(e);
        }
      }
      // throw exception if source doesnt exist
      else{
        throw new IllegalArgumentException("Invalid source: " + source);
      }  
    }
  }

  private static void insertSubProduct(Element element, String productId, String source) {
    // extract type of subProduct
    String type = element.getAttribute("pgroup");
    //adjust insert according to type
    switch (type) {
        case "Music":
            insertCD(element, productId, source);
            break;
        case "Book":
            insertBook(element, productId, source);
            break;
        case "DVD":
            insertDVD(element, productId, source);
            break;
    }
  }

  private static void insertCD(Element element, String productId, String source) {

    // get relevant data and prepare sql statement with placeholders  
    Element musicSpec = (Element) element.getElementsByTagName("musicspec").item(0);
    String releaseDate = getTagValue(musicSpec, "releasedate");
    String sql = "INSERT INTO cd (product_number, published_at) VALUES (?, ?)";
    Element person_Element= (Element) element.getElementsByTagName("artists").item(0);
    
    //if there is no artists then no insert 
    if (!(element.getElementsByTagName("artists").item(0).hasChildNodes() && getAttributeValue(person_Element, "artist", "name")=="")){
      try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
          preparedStatement.setString(1, productId);
          preparedStatement.setString(2, releaseDate);
          preparedStatement.executeUpdate();
      } catch (SQLException e) {
          logException(e);
      }

      // Insert into related tables
      insertTitles(element, productId);
      insertLabels(element, productId, source);
      insertPeople(element, productId, "artist", source);
    }              
  }

  private static void insertBook(Element element, String productId, String source) {

    //get relevant data
    Element bookSpec = (Element) element.getElementsByTagName("bookspec").item(0);
    String isbn = getAttributeValue(bookSpec, "isbn", "val");
    Integer pages = parseIntegerToNullable(getTagValue(bookSpec, "pages"));
    String publishedAt = getAttributeValue(bookSpec, "publication", "date");

    // prepare sql statement with placeholders
    String sql = "INSERT INTO book (product_number, number_pages, isbn, published_at) VALUES (?, ?, ?, ?)";

    // prepare statement
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
        preparedStatement.setString(1, productId);
        if (pages!=null){
          preparedStatement.setInt(2, pages);
        }        
        preparedStatement.setString(3, isbn);
        preparedStatement.setString(4, publishedAt);
        preparedStatement.executeUpdate();
    } catch (SQLException e) {
        logException(e);
    }

    // Insert into related tables
    insertPublisher(element, productId, source);
    insertPeople(element, productId, "author", source);
  }

  private static void insertDVD(Element element, String productId, String source) {
    
    // get relevant data 
    Element dvdSpec = (Element) element.getElementsByTagName("dvdspec").item(0);
    String format = getTagValue(dvdSpec, "format");
    String runningTime = getTagValue(dvdSpec, "runningtime");
    String regionCode = getTagValue(dvdSpec, "regioncode");

    //prepare sql statement with placeholders
    String sql = "INSERT INTO dvd (product_number, format, runtime, region_code) VALUES (?, ?, ?, ?)";

    //prepare statement, set placeholders, excecute
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
        preparedStatement.setString(1, productId);
        preparedStatement.setString(2, format);
        preparedStatement.setString(3, runningTime);
        preparedStatement.setString(4, regionCode);

        preparedStatement.executeUpdate();
    } catch (SQLException e) {
        logException(e);
    }

    // insert into related tables
    insertPeople(element, productId, "creator", source);
    insertPeople(element, productId, "director", source);
    insertPeople(element, productId, "actor", source);
  }

  public static void insertTitles(Element element, String productId) {
    
    // get track element which has child elements with individual tracks
    Element tracksElement = (Element) element.getElementsByTagName("tracks").item(0);
    if (tracksElement != null) {

      // set list of nodes with individual tracks and prepare sql for table inserts
      NodeList trackList = tracksElement.getElementsByTagName("title");
      String titleSql = "INSERT INTO title (title) VALUES (?)";
      String cdTitleSql = "INSERT INTO cd_title (title, product_number) VALUES (?, ?)";
      
      try (PreparedStatement cdTitleStatement = connection.prepareStatement(cdTitleSql)) {
          
        // loop through tracks
        for (int i = 0; i < trackList.getLength(); i++) {
          String title = trackList.item(i).getTextContent();

          // insert title
          try (PreparedStatement titleStatement = connection.prepareStatement(titleSql)) {
              titleStatement.setString(1, title);
              titleStatement.executeUpdate();
          } catch (SQLException e) {
            logException(e);
          }

          //insert in cd_title
          cdTitleStatement.setString(1, title);
          cdTitleStatement.setString(2, productId);
          cdTitleStatement.addBatch();
        }

        cdTitleStatement.executeBatch();
      } catch (SQLException e) {
          logException(e);
      }
    }
  }
  public static void insertLabels(Element element, String productId, String source) {

    // get lables element 
    Element labelsElement = (Element) element.getElementsByTagName("labels").item(0);
    
    if (labelsElement != null) {
      NodeList labelList = labelsElement.getElementsByTagName("label");
      String labelsSql = "INSERT INTO label (name) VALUES (?)";
      String cdLabelSql = "INSERT INTO cd_label (name, product_number) VALUES (?, ?)";
      
      // loop through labellist
      for (int i = 0; i < labelList.getLength(); i++) {
        // adjust data ectraction according to source
        String label = null;
        if (source.toLowerCase().equals("leipzig")){
            label = getAttributeValue(labelsElement, "label", "name", i);
        }
        else if (source.toLowerCase().equals("dresden")){
            label = labelList.item(i).getTextContent();
        }

        if (label!=null){

          // insert label
          try (PreparedStatement labelStatement = connection.prepareStatement(labelsSql)) {
            labelStatement.setString(1, label);
            labelStatement.executeUpdate();
          } catch (SQLException e) {
            logException(e);
          }

          //insert cd_label
          try (PreparedStatement cdLabelStatement = connection.prepareStatement(cdLabelSql)) {
            cdLabelStatement.setString(1, label);
            cdLabelStatement.setString(2, productId);
            cdLabelStatement.executeUpdate();
          } catch (SQLException e) {
              logException(e);
          }
        }
        else {
          throw new IllegalArgumentException("Invalid source: " + source);
        }
      
      }
    }
  }

  public static void insertPublisher(Element element, String productId, String source) {
    // get publisher Element 
    Element publisherElement = (Element) element.getElementsByTagName("publishers").item(0);
    if (publisherElement != null) {

      // prepare sql statements
      NodeList publisherList = publisherElement.getElementsByTagName("publisher");
      String publisherSql = "INSERT INTO publisher (name) VALUES (?)";
      String bookPublisherSql = "INSERT INTO book_publisher (name, product_number) VALUES (?, ?)";
      
      // loop through publlisher list
      for (int i = 0; i < publisherList.getLength(); i++) {

        // adjust ectraction according to data source
        String publisher = null;
        if (source.toLowerCase().equals("leipzig")){
            publisher = getAttributeValue(publisherElement, "publisher", "name", i);
        }
        else if (source.toLowerCase().equals("dresden")){
            publisher = publisherList.item(i).getTextContent();
        }

        if (publisher!=null){

          // insert publisher
          try (PreparedStatement publisherStatement = connection.prepareStatement(publisherSql)) {
            publisherStatement.setString(1, publisher);
            publisherStatement.executeUpdate();
          } catch (SQLException e) {
            logException(e);
          }

          // insert book_publisher
          try (PreparedStatement bookPubisherStatement = connection.prepareStatement(bookPublisherSql)) {
            bookPubisherStatement.setString(1, publisher);
            bookPubisherStatement.setString(2, productId);
            bookPubisherStatement.executeUpdate();
          } catch (SQLException e) {
              logException(e);
          }
        }
        else {
          throw new IllegalArgumentException("Invalid source: " + source);
        } 
      }
    }   
  }

  public static void insertPeople(Element element, String productId, String role, String source) {
    
    /*
    node structure for people in in both xmlss look like this:
      <actors>
        <actor>
        <actor>
        ...
      <actors/>
    */

    String parent = role + "s";
    String child = role;

    Element personsElement = (Element) element.getElementsByTagName(parent).item(0);
    if (personsElement != null) {
      NodeList personList = personsElement.getElementsByTagName(child);
      String personSql = "INSERT INTO person (name) VALUES (?)";
      String productPersonSql = "INSERT INTO person_product (name, product_number, role) VALUES (?,?,?)";

      for (int i = 0; i < personList.getLength(); i++) {

        // adjust extraction according to source
        String person = null;
        if (source.toLowerCase().equals("leipzig")){
            person = getAttributeValue(personsElement, role, "name", i);
        }
        else if (source.toLowerCase().equals("dresden")){
            person = personList.item(i).getTextContent();
        }

        if(person!=null) {

          // insert into people
          try (PreparedStatement personStatement = connection.prepareStatement(personSql)){
            personStatement.setString(1, person);
            personStatement.executeUpdate();

          } catch(SQLException e){
            logException(e);
          }

          // insert into product_people
          try(PreparedStatement productPersonStatement= connection.prepareStatement(productPersonSql)) {
            productPersonStatement.setString(1, person);
            productPersonStatement.setString(2, productId);
            productPersonStatement.setString(3, role);
            productPersonStatement.executeUpdate();
          } catch(SQLException e){
            logException(e);
          }
        }
        else{
            throw new IllegalArgumentException("Invalid source: " + source);
        }
      }
    }
  }

  public static void insertCategory(Node cat, int supId){
  
    //initialize with -1
    int catId=-1;  
    String insertSql="Insert into category (name, super_category) Values (?,?)";

    try (PreparedStatement statement = connection.prepareStatement(insertSql, new String[]{"id"})) {
      String category =cat.getFirstChild().getNodeValue().replace("\n", "").trim();
      statement.setString(1,category );
      if (supId==-1) {
          statement.setNull(2, java.sql.Types.VARCHAR);
      } else {
          statement.setInt(2, supId);
      }
      statement.executeUpdate();

      // update catId to what has been generated in Category Insert
      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
            catId = generatedKeys.getInt(1);
        }
        else{
            throw new SQLException("Creating category failed, no ID obtained.");
        }
      }
        statement.close();
    } catch(SQLException e){
        logException(e);
    }

    // insert into Product Category using the retrieved category
    NodeList children=cat.getChildNodes();
    for (int i= 0; i<children.getLength();i++){
      Node child=children.item(i);
      if (child.getNodeName().equals("item")){
          insertProductCategory(catId, child.getTextContent());
      }
    }

    // recursively insert in sub categories
    for (int j= 0; j<children.getLength();j++){
      Node child=children.item(j);
      if (child.getNodeName().equals("category")){
          insertCategory(child,  catId);
      }
    }

  }  

  private static void insertProductCategory(int catId, String productNumber){
    String insertSql= "Insert into product_category (id, product_number) values(?,?)";
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
