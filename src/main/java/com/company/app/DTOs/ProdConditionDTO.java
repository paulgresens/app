package com.company.app.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class ProdConditionDTO implements Serializable{
    private String product_number;
    private Integer branchOfficeId;
    private String productCondition;
    private Integer price;
    private Integer count;
    private String currency;
}