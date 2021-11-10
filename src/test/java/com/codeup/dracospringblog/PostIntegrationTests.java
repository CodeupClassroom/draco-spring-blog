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
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = DracoSpringBlogApplication.class)
@AutoConfigureMockMvc
public class PostIntegrationTests {

    // define test user variable
    private User testUser;

    // define HttpSession variable
    private HttpSession httpSession;

    // autowire MockMvc object
    @Autowired
    private MockMvc mvc;

    // autowire UserRepository
    @Autowired
    private UserRepository userDao;

    // autowire PostRepository
    @Autowired
    private PostRepository postDao;

    // autowire PasswordEncoder
    @Autowired
    private PasswordEncoder passwordEncoder;


    // ======= before running tests, find test user or create one, then 'login'

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

        httpSession = this.mvc.perform(post("/login").with(csrf())
                .param("username", "testUser")
                .param("password", "pass"))
                .andExpect(status().is(HttpStatus.FOUND.value()))
                .andExpect(redirectedUrl("/posts"))
                .andReturn()
                .getRequest()
                .getSession();
    }

    // sanity test to assert that the session is not null
    @Test
    public void testSessionNotNull() {
        assertNotNull(httpSession);
    }

    // test post index view
    @Test
    public void testPostIndex() throws Exception {
        Post firstPost = postDao.findAll().get(0);
        mvc.perform(get("/posts"))
            .andExpect(status().isOk()) // expect that status is ok
            .andExpect(content().string(containsString("Posts"))) // expect content of response to include header title
            .andExpect(content().string(containsString(firstPost.getTitle()))); // expect content to contain string of a post
    }

    @Test
    public void testPostShow() throws Exception {
        Post firstPost = postDao.findAll().get(0);
        mvc.perform(get("/posts/" + firstPost.getId()))
                .andExpect(status().isOk()) // expect that status is ok
                .andExpect(content().string(containsString("Show Post"))) // expect content of response to include header title
                .andExpect(content().string(containsString(firstPost.getTitle()))); // expect content to contain string of a post
    }

    @Test
    public void testPostCreate() throws Exception {
        mvc.perform(post("/posts/create").with(csrf())
                .session((MockHttpSession) httpSession)
                .param("title", "New Created Post")
                .param("body", "New Created post body"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testEditPost() throws Exception {
        List<Post> posts = postDao.findAll();
        Post post = posts.get(posts.size() - 1);
        mvc.perform(post("/posts/" + post.getId() + "/edit").with(csrf())
                .session((MockHttpSession) httpSession)
                .param("title", "Blah Blah")
                .param("body", "Blah Blah"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testDeletePost() throws Exception {
        List<Post> posts = postDao.findAll();
        Post post = posts.get(posts.size() - 1);
        mvc.perform(post("/posts/" + post.getId() + "/delete").with(csrf())
                .session((MockHttpSession) httpSession))
                .andExpect(status().is3xxRedirection());
    }

}
