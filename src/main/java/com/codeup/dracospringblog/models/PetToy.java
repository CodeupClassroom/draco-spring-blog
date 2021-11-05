package com.codeup.dracospringblog.models;

import javax.persistence.*;

@Entity
@Table(name="pet_toys")
public class PetToy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;


    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    public PetToy() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }
}
