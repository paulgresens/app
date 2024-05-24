package com.company.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cd_title")
public class CdTitle {
    @EmbeddedId
    private CdTitleId id;

    @MapsId("productNumber")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_number", nullable = false)
    private Cd productNumber;

    @MapsId("title")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "title", nullable = false)
    private Title title;

}