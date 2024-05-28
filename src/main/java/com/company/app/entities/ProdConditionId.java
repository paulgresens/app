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
public class ProdConditionId implements java.io.Serializable {
    private static final long serialVersionUID = 5473672223623211996L;
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
        ProdConditionId entity = (ProdConditionId) o;
        return Objects.equals(this.condition, entity.condition) &&
                Objects.equals(this.productNumber, entity.productNumber) &&
                Objects.equals(this.branchOfficeId, entity.branchOfficeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, productNumber, branchOfficeId);
    }

}