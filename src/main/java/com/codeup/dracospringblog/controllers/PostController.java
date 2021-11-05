package com.codeup.dracospringblog.controllers;

import com.codeup.dracospringblog.models.Post;
import com.codeup.dracospringblog.repositories.PostRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PostController {

    // injecting a dependency...

    private PostRepository postsDao;

    public PostController(PostRepository postsDao) {
        this.postsDao = postsDao;
    }

    @GetMapping("/posts")
    public String index(Model viewModel) {
        // fetch all posts with postsDao
        List<Post> posts = postsDao.findAll();
        // send list of post objects to index view
        // add list of posts to
        viewModel.addAttribute("posts", posts);
        return "posts/index";
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

    @GetMapping("/posts/{id}/edit")
    public String returnEditView(@PathVariable long id, Model viewModel) {
        // send a edit form
        viewModel.addAttribute("post", postsDao.getById(id));
        return "posts/edit";
    }

    @PostMapping("/posts/{id}/edit")
    public String updatePost(@PathVariable long id, @RequestParam(name="title") String title, @RequestParam String body) {
        // use the new form inputs to update the existing post in the DB
        // pull the existing post object from the database
        Post post = postsDao.getById(id);
        // set the title and body to the request param values
        post.setTitle(title);
        post.setBody(body);
        // persist the change in the db with the postsDao
        postsDao.save(post); // works to both update existing posts and insert new posts
        return "redirect:/posts";
    }



    // ================ DELETE
    // add another endpoint (POST "/posts/{id}/delete") to delete a post
    // add needed controller logic to delete the correct post

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable long id) {
        postsDao.deleteById(id);
        return "redirect:/posts";
    }


}
