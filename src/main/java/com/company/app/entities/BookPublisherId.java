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
public class BookPublisherId implements java.io.Serializable {
    private static final long serialVersionUID = -2758191526724054019L;
    @Column(name = "product_number", nullable = false)
    private String productNumber;

    @Column(name = "name", nullable = false)
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BookPublisherId entity = (BookPublisherId) o;
        return Objects.equals(this.name, entity.name) &&
                Objects.equals(this.productNumber, entity.productNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, productNumber);
    }

}