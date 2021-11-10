package com.codeup.dracospringblog;


import com.codeup.dracospringblog.models.Post;
import com.codeup.dracospringblog.models.User;
import com.codeup.dracospringblog.repositories.PostRepository;
import com.codeup.dracospringblog.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DracoSpringBlogApplication.class)
@AutoConfigureMockMvc
public class PostsIntegrationTests {

    private User testUser;
    private HttpSession httpSession;

    @Autowired
    private MockMvc mvc;

    @Autowired
    UserRepository userDao;

    @Autowired
    PostRepository postsDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Before
    public void setup() throws Exception {
        testUser = userDao.findByUsername("testUser");
        // Creates the test user if not exists
        if(testUser == null){
            User newUser = new User();
            newUser.setUsername("testUser");
            newUser.setPassword(passwordEncoder.encode("pass"));
            newUser.setEmail("testUser@codeup.com");
            testUser = userDao.save(newUser);
        }

        Post postToSave = new Post("Test Post", "Test Body");
        testUser.setPosts(new ArrayList<>(Arrays.asList(postToSave)));
        postToSave.setUser(testUser);
        postsDao.save(postToSave);

        // Throws a Post request to /login and expect a redirection to the Ads index page after being logged in
        httpSession = this.mvc.perform(post("/login").with(csrf())
                .param("username", "testUser")
                .param("password", "pass"))
                .andExpect(status().is(HttpStatus.FOUND.value()))
                .andExpect(redirectedUrl("/posts"))
                .andReturn()
                .getRequest()
                .getSession();
    }

    @Test
    public void sessionExists() {
        assertNotNull(httpSession);
    }

    @Test
    public void testPostsIndex() throws Exception {
        Post firstPost = postsDao.findAll().get(0);
        mvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Posts")))
                .andExpect(content().string(containsString(firstPost.getTitle())));
    }

    @Test
    public void testPostShow() throws Exception {
        Post firstPost = postsDao.findAll().get(0);
        mvc.perform(get("/posts/" + firstPost.getId()))
                .andExpect((status().isOk()))
                .andExpect(content().string(containsString(firstPost.getTitle())));
    }

    @Test
    public void testCreatePost() throws Exception {
        Post newPost = new Post("Test Post", "This is the test post body.");
        mvc.perform(post("/posts/create").with(csrf())
                .session((MockHttpSession) httpSession)
                .param("title", newPost.getTitle())
                .param("body", newPost.getBody()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testEditPost() throws Exception {
        List<Post> posts = postsDao.findAll();
        Post post = posts.get(posts.size() - 1);
        mvc.perform(post("/posts/" + post.getId() + "/edit").with(csrf())
                .session((MockHttpSession) httpSession)
                .param("title", "Blah Blah")
                .param("body", "Blah Blah"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testDeletePost() throws Exception {
        List<Post> posts = postsDao.findAll();
        Post post = posts.get(posts.size() - 1);
        mvc.perform(post("/posts/" + post.getId() + "/delete").with(csrf())
                .session((MockHttpSession) httpSession))
                .andExpect(status().is3xxRedirection());
    }


}
