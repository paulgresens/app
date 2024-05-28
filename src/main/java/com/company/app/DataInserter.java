package com.company.app;


import com.company.app.entities.*;
import com.company.app.repositories.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
public class DataInserter implements CommandLineRunner {

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

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        //insert data here
        //Product product = new Product("thisifdfdfdfdshit13245678910", "titleundso", 3, null);
        //entityManager.persist(product);
        //Dvd dvd = new Dvd(null, product, "format", "runtime", "DE");
        //entityManager.persist(dvd);
        // insertReviewData();

    }

    public void insertReviewData() {
        try {

            //linked branchOffice
            List<List<String>> csvData = new CSVParser("/app/setupFiles/reviews.csv", ",", true).parseCsv();
            BranchOffice defaultBranchOffice = new BranchOffice(0, "unknown", "unknown", "unknown");
            Optional<BranchOffice> existingBranchOffice = branchOfficeRepository.findOne(Example.of(defaultBranchOffice));
            if (existingBranchOffice.isEmpty()) {
                entityManager.persist(defaultBranchOffice);
            }
            BranchOffice linkedBranchOffice = existingBranchOffice.isEmpty() ? defaultBranchOffice : existingBranchOffice.get();
            for (List<String> data : csvData) {
                //linked Customer
                Customer customerProbe = new Customer();
                customerProbe.setUsername(data.get(4));
                Optional<Customer> existingCustomer = customerRepository.findOne(Example.of(customerProbe));
                Integer maxId = customerRepository.findEntityWithHighestId();
                int nextCustomerId = maxId != null ? maxId + 1 : -0;
                Customer linkedCustomer = existingCustomer.isEmpty() ? new Customer(nextCustomerId + 1, "", "", data.get(4), "") : existingCustomer.get();
                if (existingCustomer.isEmpty()) {
                    entityManager.persist(linkedCustomer);
                }
                //linked Date
                Instant timestamp;
                try {
                    LocalDate localDate = LocalDate.parse("2004-08-27"/* for some fucking reasons this bullshit does not work, iT IS THE FUCKING RIGHT FORMATdata.get(3)*/);
                    timestamp = localDate.atStartOfDay().toInstant(ZoneOffset.UTC);
                } catch (Exception e) {
                    writeLog("Failed to parse Date");
                    continue;
                }
                //linked Product
                Optional<Product> existingProduct = productRepository.findById(data.get(0));
                if (existingProduct.isEmpty()) {
                    //    writeLog("Product does not exist");
                    continue;
                }
                System.err.println("This Rating has an existing Product");
                //linked Condition
                ProdConditionId prodConditionId = new ProdConditionId(existingProduct.get().getProductNumber(), defaultBranchOffice.getId(), "unknown");
                Optional<ProdCondition> existingCondition = conditionRepository.findById(prodConditionId);
                System.err.println("already in database? : " + !existingCondition.isEmpty());
                ProdCondition linkedProdCondition = existingCondition.isEmpty() ? new ProdCondition(new ProdConditionId(existingProduct.get().getProductNumber(), linkedBranchOffice.getId(), "unknown"), existingProduct.get(), linkedBranchOffice, null, 0, null) : existingCondition.get();

                if (existingCondition.isEmpty()) {
                    entityManager.persist(linkedProdCondition);
                }
                System.err.println(linkedProdCondition);
                try {
                    RatingId ratingProbeId = new RatingId(linkedCustomer.getId(), linkedProdCondition.getId().getProductNumber(), defaultBranchOffice.getId(), "unknown");
                    Optional<Rating> existingRating = ratingRepository.findById(ratingProbeId);
                    if (existingRating.isEmpty()) {
                        //map this somehow
                        Rating rating = new Rating(null, linkedCustomer, linkedProdCondition, data.get(6), timestamp, Integer.parseInt(data.get(2)), Rating.RatingOption.ONE);

                        //entityManager.persist(rating);
                    } else {
                        writeLog("Customer already rated this item");
                    }
                } catch (Exception e) {
                    writeLog("OOPS");

                }
            }

        } catch (IOException e) {
            System.err.println(e);
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