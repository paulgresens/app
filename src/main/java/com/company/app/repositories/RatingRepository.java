package com.company.app.repositories;

import com.company.app.entities.Rating;
import com.company.app.entities.RatingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, RatingId> {
    
}