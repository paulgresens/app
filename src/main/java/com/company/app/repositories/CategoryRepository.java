package com.company.app.repositories;

import com.company.app.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Query(
            value="WITH RECURSIVE CategoryTree AS ( "+
                    "SELECT c.id, c.name, c.super_category "+
                    "FROM category c "+
                    "WHERE c.name = :categoryName AND c.super_category IS NULL "+
                    "UNION ALL " +
                    "SELECT c.id, c.name, c.super_category "+
                    "FROM category c "+
                    "INNER JOIN CategoryTree ct ON c.super_category = ct.id " +
                    ") " +
                    "SELECT * FROM CategoryTree ", nativeQuery = true
    )
    List<Category> getCategoryTreeByName(@Param("categoryName") String categoryName);
}

