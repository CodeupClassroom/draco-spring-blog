package com.codeup.dracospringblog.repositories;

import com.codeup.dracospringblog.models.Coffee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoffeeRepository extends JpaRepository<Coffee, Long> {

    List<Coffee> findByRoast(String roast);

}
