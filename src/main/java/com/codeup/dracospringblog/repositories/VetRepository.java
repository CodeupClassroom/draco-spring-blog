package com.codeup.dracospringblog.repositories;

import com.codeup.dracospringblog.models.Vet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VetRepository extends JpaRepository<Vet, Long> {
}
