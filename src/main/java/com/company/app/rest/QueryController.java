package com.company.app.rest;

import com.company.app.entities.Customer;
import com.company.app.entities.Product;
import com.company.app.repositories.CustomerRepository;
import com.company.app.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class QueryController {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CustomerRepository customerRepository;

    @GetMapping("/{productNumber}/similarCheaper")
    public ResponseEntity<Map<String, List<String>>> getSimilarCheaperProduct(@PathVariable String productNumber){
        Optional<Product> product = productRepository.findById(productNumber);
        if (product.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product does not exist");
        }
        List<String> productNumbers =  productRepository.getSimilarCheaperProduct(productNumber);
        Map<String, List<String>> response = Map.of("productNumbers", productNumbers);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{pattern}")
    public List<Product> getProducts( @PathVariable String pattern){
        if (pattern.equalsIgnoreCase("null")){
           return productRepository.getAllProducts();
        } else {
            return productRepository.findByTitleContaining(pattern);
        }
    }

    @GetMapping("/belowRating/{minimumAverageRating}")
    public List<Customer> getTrolls(@PathVariable Float minimumAverageRating){
        if (minimumAverageRating < 0 || minimumAverageRating > 5){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return customerRepository.getCustomersWithBelowCertainAverageRating(minimumAverageRating);
    }
}


