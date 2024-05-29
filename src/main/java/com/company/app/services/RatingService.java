package com.company.app.services;

import com.company.app.repositories.*;
import jakarta.persistence.EntityManager;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import com.company.app.entities.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class RatingService {
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BranchOffice insertDefaultBranchOffice() {
        BranchOffice existingBranchOffice = branchOfficeRepository.findDefaultBranchOffice();
        BranchOffice linkedBranchOffice = existingBranchOffice;
        if (existingBranchOffice == null) {
            Integer maxId = branchOfficeRepository.findEntityWithHighestId();
            int nextBranchOfficeId = maxId != null ? maxId + 1 : 0;
            BranchOffice defaultBranchOffice = new BranchOffice(nextBranchOfficeId, "unknown", "unknown", "unknown");
            entityManager.persist(defaultBranchOffice);
            linkedBranchOffice = defaultBranchOffice;
        }
        return linkedBranchOffice;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insertReview(List<String> data, BranchOffice officeWhereBought) throws Exception {
        String ratingByForProduct = "By: " + data.get(4) + " For: " + data.get(0) + ":   ";

        //linked Customer
        Customer existingCustomer = customerRepository.findByUsername(data.get(4));
        Customer linkedCustomer;
        if (existingCustomer == null) {
            Integer maxId = customerRepository.findEntityWithHighestId();
            int nextCustomerId = maxId != null ? maxId + 1 : 0;
            linkedCustomer = new Customer(nextCustomerId + 1, "", "", data.get(4), "");
            entityManager.persist(linkedCustomer);
        } else {
            linkedCustomer = existingCustomer;
        }

        //linked Date
        Instant timestamp;
        try {
            LocalDate localDate = LocalDate.parse(data.get(3));
            timestamp = localDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        } catch (Exception e) {
            throw new Exception(ratingByForProduct + "Failed to parse date");
        }

        //linked Product
        Optional<Product> existingProduct = productRepository.findById(data.get(0));
        if (existingProduct.isEmpty()) {
            //    writeLog("Product does not exist");
            throw new Exception(ratingByForProduct + "Product does not exists in Database");
        }

        //linked Condition
        ProdConditionId prodConditionId = new ProdConditionId(existingProduct.get().getProductNumber(), officeWhereBought.getId(), "unknown");
        Optional<ProdCondition> existingCondition = conditionRepository.findById(prodConditionId);
        ProdCondition linkedProdCondition = existingCondition.isEmpty() ? new ProdCondition(new ProdConditionId(existingProduct.get().getProductNumber(), officeWhereBought.getId(), "unknown"), existingProduct.get(), officeWhereBought, null, 0, null) : existingCondition.get();

        if (existingCondition.isEmpty()) {
            entityManager.persist(linkedProdCondition);
        }
        //rating
        Optional<Rating> existingRating = ratingRepository.findById(new RatingId(new ProdConditionId(linkedProdCondition.getId().getProductNumber(), officeWhereBought.getId(), linkedProdCondition.getId().getCondition()), linkedCustomer.getCustomerId()));
        if (existingRating.isEmpty()) {
            RatingId ratingId = new RatingId(new ProdConditionId(linkedProdCondition.getId().getProductNumber(), officeWhereBought.getId(), linkedProdCondition.getId().getCondition()), linkedCustomer.getCustomerId());
            Integer helpfulCount;
            try {
                helpfulCount = Integer.parseInt(data.get(2));
            } catch (Exception e) {
                throw new Exception(ratingByForProduct + "Invalid Value for helpful count");
            }
            Rating.RatingOption chosenRating;
            try {
                chosenRating = Rating.RatingOption.getByValue(Integer.parseInt(data.get(1)));

            } catch (Exception e) {
                throw new Exception(ratingByForProduct + "Invalid Value for rating");
            }
            String summary;
            if (data.get(5) == null) {
                throw new Exception(ratingByForProduct + "No value for summary provided");
            } else {
                summary = data.get(5);
            }

            Rating rating = new Rating(ratingId, linkedCustomer, linkedProdCondition, summary, data.get(6), timestamp, helpfulCount, chosenRating);
            entityManager.persist(rating);
        } else {
            throw new Exception(ratingByForProduct + "Customer already rated this item");
        }
    }
}
