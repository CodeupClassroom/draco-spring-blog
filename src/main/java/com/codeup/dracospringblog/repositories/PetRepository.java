package com.codeup.dracospringblog.repositories;

import com.codeup.dracospringblog.models.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
}
