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
public class ProductCategoryId implements java.io.Serializable {
    private static final long serialVersionUID = 6299112384699408195L;
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "product_number", nullable = false)
    private String productNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProductCategoryId entity = (ProductCategoryId) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.productNumber, entity.productNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productNumber);
    }

}