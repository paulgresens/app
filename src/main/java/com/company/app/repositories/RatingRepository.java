package com.company.app.repositories;

import com.company.app.entities.Rating;
import com.company.app.entities.RatingId;
import com.company.app.rest.InsertController;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Repository
public interface RatingRepository extends JpaRepository<Rating, RatingId> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO rating (customer_id,product_number,branch_office_id,product_condition,rating,description,summary,rated_at,marked_helpful_count) VALUES (:customerId, :productNumber, :branchOfficeId, :productCondition, :rating, :description, :summary, :ratedAt, :ratedHelpfulCount );", nativeQuery = true)
    public void addNewRating(@Param ("customerId") Integer customerId, @Param("productNumber") String productNumber, @Param("branchOfficeId") Integer branchOfficeId, @Param("productCondition") String productCondition, @Param("rating") Integer rating, @Param("description") String description, @Param("summary") String summary, @Param("ratedAt") Timestamp ratedAt, @Param("ratedHelpfulCount") Integer ratedHelpfulCount);
}
