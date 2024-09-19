package com.company.app.DTOs;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ProductWithDetailDTO implements Serializable {

    private String productNumber;
    private String title;
    private Integer salesrank;
    private String image;
    private String productType;

    // Constructor
    public ProductWithDetailDTO(String productNumber, String title, Integer salesrank, String image, String productType) {
        this.productNumber = productNumber;
        this.title = title;
        this.salesrank = salesrank;
        this.image = image;
        this.productType = productType;

    }

}