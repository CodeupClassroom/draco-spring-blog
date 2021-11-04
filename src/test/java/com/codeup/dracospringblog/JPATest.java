package com.codeup.dracospringblog;

import com.codeup.dracospringblog.models.Owner;
import com.codeup.dracospringblog.models.Pet;
import com.codeup.dracospringblog.models.PetStats;
import com.codeup.dracospringblog.repositories.OwnerRepository;
import com.codeup.dracospringblog.repositories.PetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class JPATest {

    @Autowired
    private PetRepository petDao;

    @Autowired
    private OwnerRepository ownerDao;

    @Test
    public void myTest() {
        petDao.save(new Pet("Fred", 3));
    }

    @Test
    public void saveAnOwnerWithPets() {
        Owner o = new Owner("Bob");
        Pet p = new Pet("Bubbles", 2);
        p.setOwner(o);
        o.addPet(p);
        ownerDao.save(o);
    }

    @Test
    @Transactional
    public void addPetStat() {
        PetStats pts = new PetStats(22.0);
        Pet pet  = new Pet("Snickers", 3);
        // Pet pet = petDao.getById(1L);
        // pts.setPet(pet);
        pet.setPetStats(pts);
        petDao.save(pet);
    }

}
