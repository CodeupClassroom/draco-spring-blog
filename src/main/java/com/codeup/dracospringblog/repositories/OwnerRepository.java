package com.codeup.dracospringblog.repositories;

import com.codeup.dracospringblog.models.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
}
