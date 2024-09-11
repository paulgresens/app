package com.company.app.repositories;

import com.company.app.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    /*//Methode 1
    @Query(value = "SELECT * FROM product WHERE product_number = :productNumber", nativeQuery = true)
    Product getProducts(@Param("productNumber") String productNumber);
    */

    // Methode 2
    /*@Query(value = "SELECT p FROM Product p WHERE p.title LIKE :pattern", nativeQuery = true)
    List<Product> getProductsByPattern(@Param("pattern") String pattern);
    */

    List<Product> findByTitleContaining(String title);

    @Query("SELECT p FROM Product p")
    List<Product> getAllProducts();

    /*// Methode 4
    @Query(value = "WITH RECURSIVE CategoryPath AS (" +
            "  SELECT c.id, c.super_category_id, c.name, ARRAY[c.name] as path" +
            "  FROM Category c" +
            "  WHERE c.name = :leafCategoryName" +
            "  UNION ALL" +
            "  SELECT c2.id, c2.super_category_id, c2.name, c2.name || cp.path" +
            "  FROM Category c2" +
            "  JOIN CategoryPath cp ON cp.super_category_id = c2.id" +
            ")" +
            "SELECT p.*" +
            "FROM Product p" +
            "JOIN ProductCategory pc ON pc.productNumber=p.productNumber" +
            "JOIN Category c ON pc.id = c.id" +
            "JOIN CategoryPath cp ON c.id = cp.id" +
            "WHERE cp.path = :categoryPath",
            nativeQuery = true)
    List<Product> findProductsByCategoryPath(
            @Param("leafCategoryName") String leafCategoryName,
            @Param("categoryPath") String[] categoryPath);
    // Methode 5
    @Query("SELECT p FROM Product p, ProdCondition pc, Rating r " +
            "Where p.productNumber=pc.productNumber and pc.id=r.prodCondition  " +
            "and r.prodCondition = (p.productNumber, :branchoffice, :condition) order by r.ratingOption DESC")
    List<Product> findTopProducts(@Param("limit") int limit, @Param("branchoffice_id") int branchoffice_id, @Param("condition") String condition);*/


    @Query(value = "SELECT * FROM product WHERE product_number IN" +
            "(SELECT product_number_2 FROM similarity WHERE product_number_1 = :productNumber UNION " +
             "SELECT product_number_1 FROM similarity WHERE product_number_2 = :productNumber);",
            nativeQuery = true
    )
    List<Product> getSimilarCheaperProduct(@Param("productNumber") String productNumber);


}
