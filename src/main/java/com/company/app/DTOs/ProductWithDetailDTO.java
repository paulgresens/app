package com.company.app.DTOs;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductWithDetailDTO implements Serializable {

    // general INfo
    private String productNumber;
    private String title;
    private Integer salesrank;
    private String image;
    private String productType;

    // Book Info
    private Integer numberPages;
    private Instant publishedAt;
    private String publisher;

    //DVD Info
    private String format;
    private String regionCode;
    private String runtime;

    //CD Info
    private String publishedDate;
    private String label;

    // Constructor
    public ProductWithDetailDTO(String productNumber, String title, Integer salesrank, String image, String productType, Integer numberPages, Instant publishedAt, String format, String regionCode, String runtime, String publishedDate, String publisher, String label) {
        this.productNumber = productNumber;
        this.title = title;
        this.salesrank = salesrank;
        this.image = image;
        this.productType = productType;
        this.numberPages = numberPages;
        this.publishedAt = publishedAt;
        this.format = format;
        this.regionCode = regionCode;
        this.runtime = runtime;
        this.publishedDate = publishedDate;
        this.publisher = publisher;
        this.label = label;

    }

}