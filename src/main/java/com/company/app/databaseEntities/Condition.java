package com.company.app.databaseEntities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "condition")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Condition {

    @Getter
    @Setter
    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConditionPrimaryKey {
        @OneToOne
        @JoinColumn(name = "product_number")
        private Product product;

        @OneToOne
        @JoinColumn(name = "branch_office_id")
        private BranchOffice branchOffice;

        @Column
        private String condition;
    }

    @EmbeddedId
    @PrimaryKeyJoinColumn
    private ConditionPrimaryKey key;

    @Column
    private Integer price;

    @Column
    private Integer count;

    @Column
    private String currency;

}