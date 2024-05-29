package com.company.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rating")
public class Rating {
    @EmbeddedId
    private RatingId id;

    @MapsId("customerId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id", nullable = false)
    private Customer customer;

    @MapsId("id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "product_condition", referencedColumnName = "product_condition"),
            @JoinColumn(name = "branch_office_id", referencedColumnName = "branch_office_id"),
            @JoinColumn(name = "product_number", referencedColumnName = "product_number")
    })
    private ProdCondition prodCondition;


    @Column(name = "summary", length = Integer.MAX_VALUE)
    private String summary;
    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;
    @Column(name = "rated_at", nullable = false)
    private Instant ratedAt;
    @Column(name = "marked_helpful_count", nullable = false)
    private Integer markedHelpfulCount;


    public enum RatingOption {
        ONE(1),
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5);

        private final int value;

        RatingOption(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static RatingOption getByValue(int value) {
            for (RatingOption option : RatingOption.values()) {
                if (option.getValue() == value) {
                    return option;
                }
            }
            throw new IllegalArgumentException("Rating option " + value + " is not possible");
        }
    }

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "rating", columnDefinition = "RatingOption")
    private RatingOption ratingOption;
}