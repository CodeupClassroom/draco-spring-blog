package com.codeup.dracospringblog.models;


import javax.persistence.*;
import java.util.List;

@Entity
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private int age;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @ManyToMany
    @JoinTable(
            name="pet_vet",
            joinColumns = {@JoinColumn(name = "pet_id")},
            inverseJoinColumns = {@JoinColumn(name = "vet_id")}
    )
    private List<Vet> vets;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pet_stat_id", referencedColumnName = "id")
    private PetStats petStats;

    public Pet() {
    }

    public Pet(String name, int age) {
        this.name = name;
        this.age = age;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public List<Vet> getVets() {
        return vets;
    }

    public void setVets(List<Vet> vets) {
        this.vets = vets;
    }

    public PetStats getPetStats() {
        return petStats;
    }

    public void setPetStats(PetStats petStats) {
        this.petStats = petStats;
    }

    @Override
    public String toString() {
        return "=============== Pet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", owner=" + owner +
                ", vets=" + vets +
                ", petStats=" + petStats +
                '}';
    }
}
