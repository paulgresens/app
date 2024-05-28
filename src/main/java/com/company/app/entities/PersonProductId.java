package com.company.app.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PersonProductId implements java.io.Serializable {
    private static final long serialVersionUID = 4211979383527518288L;
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "product_number", nullable = false)
    private String productNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PersonProductId entity = (PersonProductId) o;
        return Objects.equals(this.name, entity.name) &&
                Objects.equals(this.productNumber, entity.productNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, productNumber);
    }

}