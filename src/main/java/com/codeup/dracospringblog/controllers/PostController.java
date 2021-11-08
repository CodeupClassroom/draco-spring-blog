package com.codeup.dracospringblog.controllers;

import com.codeup.dracospringblog.models.Post;
import com.codeup.dracospringblog.models.PostImage;
import com.codeup.dracospringblog.models.User;
import com.codeup.dracospringblog.repositories.PostRepository;
import com.codeup.dracospringblog.repositories.UserRepository;
import com.codeup.dracospringblog.services.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PostController {

    // injecting a dependency...

    private PostRepository postsDao;
    private UserRepository usersDao;
    private EmailService emailService;

    public PostController(PostRepository postsDao, UserRepository usersDao, EmailService emailService) {
        this.postsDao = postsDao;
        this.usersDao = usersDao;
        this.emailService = emailService;
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
    public String create(Model model) {
        model.addAttribute("post", new Post());
        return "posts/create";
    }

    @PostMapping("/posts/create")
    public String insert(@ModelAttribute Post post, @RequestParam List<String> urls) {
        List<PostImage> images = new ArrayList<>();
        User author = usersDao.getById(1L);
//        Post post = new Post(title, body);

        // create list of post image objects to pass to the new post constructor
        for (String url : urls) {
            PostImage postImage = new PostImage(url);
            postImage.setPost(post);
            images.add(postImage);
        }

        post.setImages(images);

        post.setUser(author);

        // save a post object with images

        postsDao.save(post);

        emailService.prepareAndSend(post, "You submitted: " + post.getTitle(), post.getBody());
        // modify the post index view to display post images
        return "redirect:/posts";
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
    public String updatePost(@ModelAttribute Post post) {
        // use the new form inputs to update the existing post in the DB
        // pull the existing post object from the database
        Post editedPost = postsDao.getById(post.getId());
//         set the title and body to the request param values
        editedPost.setTitle(post.getTitle());
        editedPost.setBody(post.getBody());

//        User user = usersDao.getById(1L);
//        post.setUser(user);
//        // persist the change in the db with the postsDao
//        postsDao.save(post); // works to both update existing posts and insert new posts
        postsDao.save(editedPost);
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
