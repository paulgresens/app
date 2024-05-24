package com.company.app.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@Embeddable
public class SimilarId implements java.io.Serializable {
    private static final long serialVersionUID = 547523337853505276L;
    @Column(name = "product_number_1", nullable = false)
    private String productNumber1;

    @Column(name = "product_number_2", nullable = false)
    private String productNumber2;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SimilarId entity = (SimilarId) o;
        return Objects.equals(this.productNumber1, entity.productNumber1) &&
                Objects.equals(this.productNumber2, entity.productNumber2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productNumber1, productNumber2);
    }

}