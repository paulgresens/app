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
public class CdTitleId implements java.io.Serializable {
    private static final long serialVersionUID = -1270308045905816488L;
    @Column(name = "product_number", nullable = false)
    private String productNumber;

    @Column(name = "title", nullable = false)
    private String title;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CdTitleId entity = (CdTitleId) o;
        return Objects.equals(this.productNumber, entity.productNumber) &&
                Objects.equals(this.title, entity.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productNumber, title);
    }

}