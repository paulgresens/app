package com.company.app.databaseEntities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "similar")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Similar {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SimilarPrimaryKey {
        @ManyToOne
        @JoinColumn(name = "productNumber")
        private Product productNumber1;

        @ManyToOne
        @JoinColumn(name = "productNumber")
        private Product productNumber2;
    }

    @EmbeddedId
    private SimilarPrimaryKey key;


}
