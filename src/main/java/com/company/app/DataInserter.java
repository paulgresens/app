package com.company.app;


import com.company.app.entities.BranchOffice;
import com.company.app.repositories.*;
import com.company.app.services.RatingService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service

public class DataInserter {

    @Autowired
    BookPublisherRepository bookPublisherRepository;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    BranchOfficeRepository branchOfficeRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    CdLabelRepository cdLabelRepository;
    @Autowired
    CdRepository cdRepository;
    @Autowired
    CdTitleRepository cdTitleRepository;
    @Autowired
    ConditionRepository conditionRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    DvdRepository dvdRepository;
    @Autowired
    ItemPurchaseRepository itemPurchaseRepository;
    @Autowired
    LabelRepository labelRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    PersonProductRepository personProductRepository;
    @Autowired
    PersonRepository personRepository;
    @Autowired
    ProductCategoryRepository productCategoryRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    PublisherRepository publisherRepository;
    @Autowired
    RatingRepository ratingRepository;
    @Autowired
    SimilarRepository similarRepository;
    @Autowired
    TitleRepository titleRepository;
    @Autowired
    EntityManager entityManager;
    private static final String STANDARD_LOGFILE = "/app/logs/log.txt";
    @Autowired
    RatingService ratingService;

    public void insertReviewData() {
        try {
            //linked branchOffice
            List<List<String>> csvData;
            try {
                csvData = new CSVParser("/app/src/data/reviews.csv", ",", true).parseCsv();
            } catch (Throwable thr) {
                writeLog("Error while getting CSV file");
                return;
            }

            BranchOffice linkedBranchOffice;
            try {
                linkedBranchOffice = ratingService.insertDefaultBranchOffice();
                System.err.println("default branch office treated");
            } catch (Exception e) {
                writeLog("Error while getting default branch office");
                return;
            }

            for (List<String> data : csvData) {
                try {
                    ratingService.insertReview(data, linkedBranchOffice);

                } catch (Exception e) {
                    writeLog(e.getMessage());
                    System.err.println(e.getMessage());
                }
            }
        } catch (Throwable thr) {
            writeLog("Something went wrong!");
        }
    }

    public void writeLog(String logText, String logFilePath) {
        try {
            FileWriter writer = new FileWriter(logFilePath, true);
            writer.write(logText + "\n");
            writer.close();
        } catch (Exception e) {
            System.err.println("Failed to write to log file.");
        }
    }

    public void writeLog(String logText) {
        writeLog(logText, STANDARD_LOGFILE);
    }
}