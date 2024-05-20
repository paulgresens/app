package com.company.app.databaseEntities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "rating")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Rating {
    enum RatingOptions {
        ONE,
        TWO,
        THREE,
        FOUR,
        FIVE
    }

    @Getter
    @Setter
    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RatingPrimaryKey {
        @ManyToOne
        @JoinColumns({
                @JoinColumn(name = "product_number", referencedColumnName = "product_number")
                ,
                @JoinColumn(name = "branch_office_id", referencedColumnName = "branch_office_id")
                ,
                @JoinColumn(name = "condition", referencedColumnName = "condition")})

        private Condition condition;

        @ManyToOne
        @JoinColumn(name = "id")
        private Customer customer;
    }

    @EmbeddedId
    @PrimaryKeyJoinColumn
    private RatingPrimaryKey key;

    @Column
    private RatingOptions rating;

    @Column
    private String description;

    @Column(name = "rated_at")
    private Timestamp ratedAt;
}
