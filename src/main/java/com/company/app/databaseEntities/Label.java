package com.company.app.databaseEntities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "label")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Label {
    @Id
    private String name;

    @ManyToMany
    @JoinTable(name = "cd_label", joinColumns = {@JoinColumn(name = "name")}, inverseJoinColumns = {
            @JoinColumn(name = "product_number")})
    Set<Cd> cds = new HashSet<>();
}
