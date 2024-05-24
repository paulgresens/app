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
public class OrderId implements java.io.Serializable {
    private static final long serialVersionUID = 6991612359241955836L;
    @Column(name = "\"itemPurchaseId\"", nullable = false)
    private Integer itemPurchaseId;

    @Column(name = "customer_id", nullable = false)
    private Integer customerId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        OrderId entity = (OrderId) o;
        return Objects.equals(this.itemPurchaseId, entity.itemPurchaseId) &&
                Objects.equals(this.customerId, entity.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemPurchaseId, customerId);
    }

}