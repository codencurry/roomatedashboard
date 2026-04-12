package com.tammam.roomatedash.model;

import jakarta.persistence.*;

@Entity
public class Roommate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    private Household household;

    public Roommate() {}

    public Roommate(String name) {
        this.name = name;
    }

    public Roommate(String name, Household household) {
        this.name = name;
        this.household = household;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Household getHousehold() { return household; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setHousehold(Household household) { this.household = household; }
}
