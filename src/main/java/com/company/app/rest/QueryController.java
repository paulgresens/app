package com.company.app.rest;

import com.company.app.entities.Product;
import com.company.app.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class QueryController {
    @Autowired
    ProductRepository productRepository;

    @GetMapping("/{productNumber}/similarCheaper")
    public List<Product> getSimilarCheaperProduct(@PathVariable String productNumber){
        return productRepository.getSimilarCheaperProduct(productNumber);
    }

    @GetMapping("/product/{pattern}")
    public List<Product> getProducts( @PathVariable String pattern){
        if (pattern.equalsIgnoreCase("null")){
           return productRepository.getAllProducts();
        } else {
            return productRepository.findByTitleContaining(pattern);
        }

    }
}


