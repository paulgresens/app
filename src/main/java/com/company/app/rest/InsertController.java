package com.company.app.rest;

import com.company.app.DataInserter;
import com.company.app.Insert;
import com.company.app.entities.ProdConditionId;
import com.company.app.entities.Rating;
import com.company.app.entities.RatingId;
import com.company.app.repositories.RatingRepository;
import org.antlr.v4.runtime.misc.MultiMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Optional;

@RestController
public class InsertController {
    @Autowired
    DataInserter dataInserter;
    @Autowired
    private RatingRepository ratingRepository;

    @GetMapping("/insert")
    public void insert() {
        File file1 = new File("src/data/dresden.xml");
        File file2 = new File("src/data/leipzig_transformed.xml");
        File file3 = new File("src/data/categories.xml");


        Insert.processXMLFile(file1, "ISO-8859-1");
        Insert.processXMLFile(file2, StandardCharsets.UTF_8.name());
        Insert.processXMLFile(file3, "ISO-8859-1");

        dataInserter.insertReviewData();
    }

    public static class AddReviewRequestBody {
        private Integer customerId;
        private String productNumber;
        private Integer branchOfficeId;
        private String productCondition;
        private Integer rating;
        private String description;
        private String summary;
    }
    @PostMapping("/review")
    public ResponseEntity addNewReview(@RequestBody AddReviewRequestBody addReviewRequestBody ){
        Optional<Rating> rating = ratingRepository.findById(new RatingId(new ProdConditionId(addReviewRequestBody.productNumber, addReviewRequestBody.branchOfficeId, addReviewRequestBody.productCondition), addReviewRequestBody.customerId));

        if(rating.isPresent()){
            return new ResponseEntity(HttpStatus.CONFLICT);
        }

        try{
            ratingRepository.addNewRating(addReviewRequestBody, Instant.now().toString(), 0);
            return new ResponseEntity(HttpStatus.CREATED);
        } catch(Exception e){
            return  new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

