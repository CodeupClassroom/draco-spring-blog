package com.codeup.dracospringblog.controllers;

import com.codeup.dracospringblog.repositories.PostRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PostController {

    // injecting a dependency...

    private PostRepository postsDao;

    public PostController(PostRepository postsDao) {
        this.postsDao = postsDao;
    }

    @GetMapping("/posts")
    @ResponseBody
    public String index() {
        // seed posts in the DB
        // fetch all posts with postsDao
        // create posts index view
        // send list of post objects to index view
        return "Here are all the posts...";
    }

    @GetMapping("/posts/{id}")
    @ResponseBody
    public String show(@PathVariable long id) {
        return "Here is the post " + id;
    }

    @GetMapping("/posts/create")
    @ResponseBody
    public String create() {
        return "Here is the post create form...";
    }

    @PostMapping("/posts/create")
    @ResponseBody
    public String insert() {
        return "New post saved...";
    }

    // ================ EDIT
    // add an endpoint (GET "/posts/{id}/edit) to send the user an edit post form / view
    // create an edit post form
    // create another endpoint (POST "/posts/{id}/edit") to handle the post request of editing a post
    // add controller logic to edit the fields of the post and save the changes and redirect to the index view


    // ================ DELETE
    // add another endpoint (POST "/posts/{id}/delete") to delete a post
    // add needed controller logic to delete the correct post


}
