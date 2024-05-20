package com.company.app.databaseEntities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "title")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Title {
    @Id
    private String title;

    @ManyToMany
    @JoinTable(name = "cd_label", joinColumns = {@JoinColumn(name = "title")}, inverseJoinColumns = {
            @JoinColumn(name = "product_number")})
    private Set<Cd> cds = new HashSet<>();
}
