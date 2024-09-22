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
    private String[] publishers;
    private String[] authors;

    //DVD Info
    private String format;
    private String regionCode;
    private String runtime;
    private String[] actors;
    private String[] creators;
    private String[] directors;


    //CD Info
    private String publishedDate;
    private String[] labels;
    private String[] artists;

    // Constructor
    public ProductWithDetailDTO(String productNumber, String title, Integer salesrank, String image,
                                String productType, Integer numberPages, Instant publishedAt, String format,
                                String regionCode, String runtime, String publishedDate, String publishers,
                                String labels, String authors, String artists, String actors, String creators, String directors) {
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

        // Split the aggregated string by comma into arrays
        this.publishers = publishers != null ? publishers.split(",") : null;
        this.labels = labels != null ? labels.split(",") : null;
        this.authors = authors != null ? authors.split(",") : null;
        this.artists = artists != null ? artists.split(",") : null;
        this.actors = actors != null ? actors.split(",") : null;
        this.creators = creators != null ? creators.split(",") : null;
        this.directors = directors != null ? directors.split(",") : null;

    }

}