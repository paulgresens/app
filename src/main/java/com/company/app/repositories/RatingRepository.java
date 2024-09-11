package com.company.app.repositories;

import com.company.app.entities.Rating;
import com.company.app.entities.RatingId;
import com.company.app.rest.InsertController;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, RatingId> {
    @Modifying
    @Query(value = "INSERT INTO rating (customer_id,product_number,branch_office_id,product_condition,rating,description,rated_at,summary,marked_helpful_count) VALUES (:addReviewRequestBody.customerId, :addReviewRequestBody.productNumber, :addReviewRequestBody.branchOfficeId, :addReviewRequestBody.productCondition, :addReviewRequestBody.rating, :addReviewRequestBody.description, :ratedAt, :addReviewRequestBody.summary, :ratedHelpfulCount )", nativeQuery = true)
    public void addNewRating(@Param("addReviewRequestBody") InsertController.AddReviewRequestBody addReviewRequestBody, @Param("ratedAt") String ratedAt, @Param("ratedHelpfulCount") Integer ratedHelpfulCount);
}
