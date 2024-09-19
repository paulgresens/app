package com.company.app.rest;

import com.company.app.DTOs.ProdConditionDTO;
import com.company.app.entities.Customer;
import com.company.app.DTOs.ProductRatingDTO;
import com.company.app.entities.Product;
import com.company.app.repositories.ConditionRepository;
import com.company.app.repositories.CustomerRepository;
import com.company.app.repositories.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.Optional;

@RestController
public class QueryController {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    private ConditionRepository conditionRepository;

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

    @GetMapping("/products/{pattern}")
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
    @GetMapping("/{productNumber}/offers")
    public ResponseEntity<Map<String, List<ProdConditionDTO>>> getProductOffers(@PathVariable String productNumber){
        Optional<Product> product = productRepository.findById(productNumber);
        if (product.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product does not exist");
        }
        List<ProdConditionDTO> offers = conditionRepository.getOffers(productNumber);
        Map<String, List<ProdConditionDTO>> response = Map.of("offers", offers);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{product_number}")
    public List<Product> getProduct( @PathVariable String product_number){
        return productRepository.getProducts(product_number);

    }

    @GetMapping("/products/byPath/**")
    public List<Product> getProductsByCategoryPath(HttpServletRequest request){
        List<String> path_list =extractAndSplitPath(request);
        String rootCategory = path_list.get(0);
        String[] path = path_list.toArray(new String[0]);

        System.out.println(Arrays.toString(path));
        return productRepository.findProductsByCategoryPath(rootCategory, path);

    }

    @GetMapping("/topProducts/{branch_office}/{limit}")
    public List<ProductRatingDTO> getTopProducts(@PathVariable String branch_office, @PathVariable int limit){
        if (branch_office.equalsIgnoreCase("Dresden")) {
            return productRepository.findTopProducts(limit, 1);
        }
        else if (branch_office.equalsIgnoreCase("Leipzig")) {
            return productRepository.findTopProducts(limit, 2);
        }
        else {
            return productRepository.findTopProducts(limit, 3);
        }
    }

    // Utility method to extract path and split into segments
    private List<String> extractAndSplitPath(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String basePath = "/product/byPath/";
        String path = requestURI.substring(requestURI.indexOf(basePath) + basePath.length()+1);

        // URL decode the path to handle encoded characters
        String decodedPath;
        try {
            decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // Handle the exception (e.g., log it and use the original path)
            System.out.println(e.getMessage());
            decodedPath = path;
        }

//        String processedPath = path.replace("%20", " ");

        // Split the path into segments and filter out any empty segments
        return Arrays.stream(decodedPath.split("/"))
                .filter(segment -> !segment.isEmpty())
                .collect(Collectors.toList());
    }
}


