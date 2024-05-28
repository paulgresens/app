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
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class RatingId implements java.io.Serializable {
    private static final long serialVersionUID = -6223682851273135539L;
    @Column(name = "customer_id", nullable = false)
    private Integer customerId;

    @Column(name = "product_number", nullable = false)
    private String productNumber;

    @Column(name = "branch_office_id", nullable = false)
    private Integer branchOfficeId;

    @Column(name = "product_condition", nullable = false)
    private String condition;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RatingId entity = (RatingId) o;
        return Objects.equals(this.condition, entity.condition) &&
                Objects.equals(this.customerId, entity.customerId) &&
                Objects.equals(this.productNumber, entity.productNumber) &&
                Objects.equals(this.branchOfficeId, entity.branchOfficeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, customerId, productNumber, branchOfficeId);
    }

}