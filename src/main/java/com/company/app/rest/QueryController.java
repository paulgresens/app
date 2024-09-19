package com.company.app.rest;

import com.company.app.DTOs.ProductWithDetailDTO;
import com.company.app.entities.Category;
import com.company.app.entities.Customer;
import com.company.app.DTOs.ProductRatingDTO;
import com.company.app.entities.Product;
import com.company.app.repositories.CategoryRepository;
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
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class QueryController {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    private CategoryRepository categoryRepository;

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
        List<Product> products=Collections.emptyList();
        if (pattern.equalsIgnoreCase("null")){
           products= productRepository.getAllProducts();
        } else {
            products= productRepository.findByTitleContaining(pattern);
        }
        if (products.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such products exist");
        }
        return products;
    }

    @GetMapping("/belowRating/{minimumAverageRating}")
    public List<Customer> getTrolls(@PathVariable Float minimumAverageRating){
        if (minimumAverageRating < 0 || minimumAverageRating > 5){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return customerRepository.getCustomersWithBelowCertainAverageRating(minimumAverageRating);
    }

    @GetMapping("/product/{product_number}")
    public ProductWithDetailDTO getProduct(@PathVariable String product_number){
        ProductWithDetailDTO product= productRepository.getProducts(product_number);
        if (product==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product does not exist");
        }
        return product;

    }

    @GetMapping("/products/byPath/**")
    public List<Product> getProductsByCategoryPath(HttpServletRequest request){
        List<String> path_list =extractAndSplitPath(request);
        String rootCategory = path_list.get(0);
        String[] path = path_list.toArray(new String[0]);

        List<Product> products= productRepository.findProductsByCategoryPath(rootCategory, path);
        if (products.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such products exist");
        }
        return products;
    }

    @GetMapping("/topProducts/{limit}")
    public List<ProductRatingDTO> getTopProducts(@PathVariable int limit){
        if (limit < 1 ){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return productRepository.findTopProducts(limit);

    }

    @GetMapping("/CategoryTree/{root}")
    public Map<String, Object> findCategoryTreeByName(@PathVariable String root){

        List<Category> categories = categoryRepository.getCategoryTreeByName(root);

        if (categories.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Root category does not exist");
        }

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> categoryList = new ArrayList<>();

        Category rootCategory=null;
        for (Category category : categories) {
            if (category.getSuperCategory()==null){
                 rootCategory=category;
                 break;
            }
        }

        Map<String, Object> rootMap = buildCategoryMap(rootCategory, categories);

        categoryList.add(rootMap);
        result.put("categories", categoryList);

        return result;
    }

    /**
     * Recursive function to build the nested category map.
     */
    private Map<String, Object> buildCategoryMap(Category parentCategory, List<Category> allCategories) {

        // Create a map for the current category
        Map<String, Object> categoryMap = new HashMap<>();
        categoryMap.put("id", parentCategory.getId());
        categoryMap.put("name", parentCategory.getName());

        // Find the children of the current category
        List<Map<String, Object>> childCategoryList = new ArrayList<>();

        for (Category category : allCategories) {
            if (parentCategory.equals(category.getSuperCategory())) {
                // Recursively build the map for child categories
                childCategoryList.add(buildCategoryMap(category, allCategories));
            }
        }

        // Add the children categories to the current category
        categoryMap.put("subcategories", childCategoryList);

        return categoryMap;
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


        // Split the path into segments and filter out any empty segments
        return Arrays.stream(decodedPath.split("/"))
                .filter(segment -> !segment.isEmpty())
                .collect(Collectors.toList());
    }
}


