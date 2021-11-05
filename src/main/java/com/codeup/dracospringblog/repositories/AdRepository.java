package com.codeup.dracospringblog.repositories;

import com.codeup.dracospringblog.models.Ad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// By extending JpaRepository, our AdRepository is able to have access to a TON of baked-in methods
public interface AdRepository extends JpaRepository<Ad, Long> { // Ad - which entity is the repo related to, Long - what is Ad's Primary Key type?

    // MAGIC! Spring Data will create a query by interpreting the method signature
    List<Ad> findByTitle(String title);

    // find == select
    // List == * (all)
    // by == where clause
    // Title == column to filter on within *where clause*
    // (String title) actual value to filter against

    //Result ---> // select * from ads where title = :title

    //Try this: Turn on logging.level.root=debug in your application.properties
    //then, run a controller method or test which calls one of these methods
    //open the intellij console and see what query Hibernate creates and executes based on your custom method!

    Ad findByDescription(String title);


    @Query(nativeQuery = true, value="select * from ads where title like :t%")
    List<Ad> findByTitleLike(@Param("t") String term);
}
