package com.company.app.rest;

import com.company.app.DTOs.ProdConditionDTO;
import com.company.app.DTOs.ProductWithDetailDTO;
import com.company.app.entities.Category;
import com.company.app.entities.Customer;
import com.company.app.DTOs.ProductRatingDTO;
import com.company.app.entities.Product;
import com.company.app.repositories.CategoryRepository;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class QueryController {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    private CategoryRepository categoryRepository;
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

    @GetMapping("/product")
    public ResponseEntity<Map<String, List<Product>>> getProducts( @RequestParam(required = false) String pattern){
        List<Product> products=Collections.emptyList();
        if (pattern == null ){
           products= productRepository.getAllProducts();
        } else {
            products= productRepository.findByTitleContaining(pattern);
        }
        Map<String, List<Product>> response = Map.of("products", products);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/belowRating/{minimumAverageRating}")
    public List<Customer> getTrolls(@PathVariable Float minimumAverageRating){
        if (minimumAverageRating < 0 || minimumAverageRating > 5){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return customerRepository.getCustomersWithBelowCertainAverageRating(minimumAverageRating);
    }

    @GetMapping("/product/{product_number}")
    public ResponseEntity<Map<String, ProductWithDetailDTO>> getProduct(@PathVariable String product_number){
        ProductWithDetailDTO product= productRepository.getProducts(product_number);
        if (product==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product does not exist");
        }

        Map<String, ProductWithDetailDTO> response = Map.of("product", product);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/path/**")
    public ResponseEntity<Map<String, List<Product>>> getProductsByCategoryPath(HttpServletRequest request){
        List<String> path_list =extractAndSplitPath(request);
        String rootCategory = path_list.get(0);
        String[] path = path_list.toArray(new String[0]);

        if (productRepository.checkIfCategoryPathExists(rootCategory, path)==0){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such Path exists");
        }
        List<Product> products= productRepository.findProductsByCategoryPath(rootCategory, path);

        Map<String, List<Product>> response = Map.of("products", products);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/top")
    public ResponseEntity<Map<String, List<ProductRatingDTO>>> getTopProducts(@RequestParam(required = false, defaultValue="10") int limit){
        if (limit < 1 ){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        List<ProductRatingDTO> products= productRepository.findTopProducts(limit);
        Map<String, List<ProductRatingDTO>> response = Map.of("products", products);

        return ResponseEntity.ok(response);

    }

    @GetMapping("/categorytree")
    public ResponseEntity<Map<String, Map<String,Object>>> findCategoryTreeByName(){

        List<Category> categories = categoryRepository.getAllCategories();

        if (categories.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Root category does not exist");
        }

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> categoryList = new ArrayList<>();

        Category rootCategory= new Category(null, "Root", null);
//        for (Category category : categories) {
//            if (category.getSuperCategory()==null){
//                 rootCategory=category;
//                 break;
//            }
//        }

        Map<String, Object> rootMap = buildCategoryMap(rootCategory, categories);

        categoryList.add(rootMap);
        result.put("Root category", categoryList);
        Map<String, Map<String,Object>> response = Map.of("Category Tree", result);

        return ResponseEntity.ok(response);

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
            if (parentCategory.getId()==null){
                if (category.getSuperCategory()==null) {
                    // Recursively build the map for child categories
                    childCategoryList.add(buildCategoryMap(category, allCategories));
                }
            }
            else{
                if(category.getSuperCategory()!=null){
                    if (parentCategory.getId() == category.getSuperCategory().getId()) {
                        // Recursively build the map for child categories
                        childCategoryList.add(buildCategoryMap(category, allCategories));
                    }

                }

            }
        }

        // Add the children categories to the current category
        categoryMap.put("subcategories", childCategoryList);

        return categoryMap;
    }


    // Utility method to extract path and split into segments
    private List<String> extractAndSplitPath(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String basePath = "/product/path/";
        String path = requestURI.substring(requestURI.indexOf(basePath) + basePath.length());

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

}


