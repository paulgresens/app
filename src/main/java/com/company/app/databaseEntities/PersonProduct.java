package com.company.app.databaseEntities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "person_product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonProduct {
    enum role {
        AUTHOR,
        ACTOR,
        CREATOR,
        DIRECTOR,
        ARTIST
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PersonProductPrimaryKey {
        @ManyToOne
        @JoinColumn(name = "name")
        private Person name;

        @ManyToOne
        @JoinColumn(name = "product_number")
        private Product productNumber;
    }

    @EmbeddedId
    @PrimaryKeyJoinColumn
    private PersonProductPrimaryKey key;

    @Column
    private String role;


}
