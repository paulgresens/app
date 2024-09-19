package com.company.app.DTOs;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Setter
public class ProductRatingDTO implements Serializable {

    private String productNumber;
    private String title;
    private Integer salesrank;
    private Double averageRating;

    // Constructor
    public ProductRatingDTO(String productNumber, String title, Integer salesrank, Double averageRating) {
        this.productNumber = productNumber;
        this.title = title;
        this.salesrank = salesrank;
        this.averageRating = averageRating;
    }

}
