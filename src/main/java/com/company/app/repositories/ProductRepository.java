package com.company.app.repositories;

import com.company.app.entities.Product;
import com.company.app.DTOs.ProductRatingDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    //Methode 1
    @Query(value = "SELECT p.*, " +
            "CASE " +
            "  WHEN EXISTS (SELECT 1 FROM book b WHERE b.product_number = :productNumber) THEN 'Book' " +
            "  WHEN EXISTS (SELECT 1 FROM dvd d WHERE d.product_number = :productNumber) THEN 'DVD' " +
            "  WHEN EXISTS (SELECT 1 FROM cd c WHERE c.product_number = :productNumber) THEN 'CD' " +
            "  ELSE 'Unknown' " +
            "END AS product_type " +
            "FROM product p " +
            "WHERE p.product_number = :productNumber", nativeQuery = true)
    List<Product> getProducts(@Param("productNumber") String productNumber);


    // Methode 2
    List<Product> findByTitleContaining(String title);

    @Query("SELECT p FROM Product p")
    List<Product> getAllProducts();

    // Methode 4
    @Query(value = "WITH RECURSIVE CategoryPath AS (" +
            "  SELECT c.id, c.super_category, c.name, CAST(ARRAY[c.name] AS varchar[]) as path" +
            "  FROM Category c" +
            "  WHERE c.name = :rootCategoryName" +
            "  UNION ALL" +
            "  SELECT c2.id, c2.super_category, c2.name, array_cat(cp.path, ARRAY[c2.name])" +
            "  FROM Category c2" +
            "  JOIN CategoryPath cp ON cp.id = c2.super_category" +
            ") " +
            "SELECT p.*" +
            "FROM Product p " +
            "JOIN product_category pc ON pc.product_number=p.product_number " +
            "JOIN CategoryPath cp ON pc.id = cp.id " +
            "WHERE cp.path = :categoryPath ",
            nativeQuery = true)
    List<Product> findProductsByCategoryPath(
            @Param("rootCategoryName") String leafCategoryName,
            @Param("categoryPath") String[] categoryPath);


    // Methode 5
    @Query("SELECT new com.company.app.DTOs.ProductRatingDTO(p.productNumber, p.title, p.salesrank, avg(r.ratingOption)) FROM Product p, ProdCondition pc, Rating r " +
            "Where p.productNumber=pc.productNumber.productNumber " +
            "and r.prodCondition.productNumber = pc.productNumber " +
            "and r.prodCondition.branchOffice.id = :branchoffice_id " +
            "GROUP BY p.productNumber "+
            "ORDER BY avg(r.ratingOption) DESC "+
            "Limit :limit")
    List<ProductRatingDTO> findTopProducts(@Param("limit") int limit, @Param("branchoffice_id") Integer branchoffice_id);


    @Query(value = "WITH min_price AS (SELECT MIN (price) as lowest_price, product_number FROM prod_condition GROUP BY product_number)" +
            "SELECT similar_products.product_number FROM min_price INNER JOIN ( SELECT product_number_2 AS product_number FROM similarity WHERE product_number_1 = :productNumber UNION SELECT product_number_1 AS product_number FROM similarity WHERE product_number_2 = :productNumber) AS similar_products ON min_price.product_number = similar_products.product_number " +
            "WHERE lowest_price IS NOT NULL AND " +
            "min_price.product_number <> :productNumber AND " +
            "(SELECT lowest_price FROM min_price " +
            "WHERE product_number = :productNumber) IS NOT NULL AND " +
            "lowest_price < ( " +
            "(SELECT lowest_price FROM min_price " +
            "WHERE product_number = :productNumber) " +
            ");",
            nativeQuery = true
    )
    List<String> getSimilarCheaperProduct(@Param("productNumber") String productNumber);


}
