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

    @MapsId("customer_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id", nullable = false)
    private Customer customer;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "condition"),
            @JoinColumn(name = "branch_office_id"),
            @JoinColumn(name = "product_number")
    })
    private Condition condition;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;
    @Column(name = "rated_at", nullable = false)
    private Instant ratedAt;

/*
 TODO [Reverse Engineering] create field to map the 'rating' column
 Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "rating", columnDefinition = "ratingoption not null")
    private Object rating;
*/
}