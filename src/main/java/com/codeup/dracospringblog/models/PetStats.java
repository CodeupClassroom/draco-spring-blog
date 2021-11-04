package com.codeup.dracospringblog.models;

import javax.persistence.*;


@Entity
public class PetStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double weightInPounds;

    @OneToOne(mappedBy = "petStats")
    private Pet pet;

    public PetStats() {
    }

    public PetStats(double weightInPounds) {
        this.weightInPounds = weightInPounds;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getWeightInPounds() {
        return weightInPounds;
    }

    public void setWeightInPounds(double weightInPounds) {
        this.weightInPounds = weightInPounds;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }
}
