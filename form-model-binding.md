# Form Model Binding

We have our Coffee class.
Coffee.java:
```
package com.codeup.models;

public class Coffee {
    private String roast;
    private String origin;
    private String brand;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Coffee() {
    }

    public Coffee(String roast, String brand) {
        this.roast = roast;
        this.brand = brand;
    }

    public String getRoast() {
        return roast;
    }

    public void setRoast(String roast) {
        this.roast = roast;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}
```

Now, let's add the capability to add coffees to the database by a form.

Create a directory under templates called ```cofeee```. Inside it, create a file called ```create.html```.

```
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add a coffee to the database</title>
</head>
<body>
    <h1>Create-a-coffee</h1>
    <form action="/coffee/create" method="post">
        <label for="brand">Brand:</label>
        <input type="text" name="brand" id="brand"><br>
        <label for="origin">Origin:</label>
        <input type="text" name="origin" id="origin"><br>
        <label for="roast">Roast:</label>
        <input type="text" name="roast" id="roast"><br>
        <button type="submit">Submit</button>
    </form>
</body>
</html>
```

We never added JPA-Hibernate auto-update for our coffee class. We are actually just making them on the spot in the coffee controller, which we are now going to move beyond. Let's set our coffee object to auto-update in the database.

Coffee.java:
```
import javax.persistence.*;

@Entity
@Table(name = "coffees")
public class Coffee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    @Column(nullable = false, length = 6)
    private String roast;
    
    @Column(nullable = false, length = 50)
    private String origin;
    
    @Column(nullable = false, length = 50)
    private String brand;
    
    ...
```

We should now get the table in the database upon restarting the server.

Now let's add a create-coffee functionality in our Coffee controller. For this to work properly, I'm first going to have to add a new constructor in our Coffee object.

Coffee.java
```
 public Coffee(String brand, String origin, String roast){
        this.brand = brand;
        this.origin = origin;
        this.roast = roast;
    }
```
And of course we need a Coffee Repository

```
package com.codeup.repositories;

import com.codeup.models.Coffee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
    
}
```

And now that we have a Coffee Repository we have to inject it into our Coffee Controller
CoffeeController.java
```
   private final CoffeeRepository coffeeRepository;
    
    public CoffeeController(CoffeeRepository coffeeRepository){
        this.coffeeRepository = coffeeRepository;
    }
```

Now we can proceed with our createCoffee() method
CoffeeController.java
```
  @PostMapping("/coffee/create")
    public String createCoffee(@RequestParam(name = "brand") String brand, @RequestParam(name = "origin") String origin, @RequestParam(name = "roast") String roast){
        Coffee coffee = new Coffee(brand, origin, roast);
        coffeeRepository.save(coffee);
        return "redirect:/views-lec/coffee";
    }
```

Notice how boring it is to create the list of RequestParams. Notice also that the parameters are identical to the properties of the Coffee objects and to the corresponding columns in the database. Wouldn't it be nice if there was an easier way to do this? First, let's get this working though. We need a way to get to the create-a-coffee form first.

CoffeeController.java:
```
 @GetMapping("/coffee/create")
    public String showCreateCoffee(){
        return "/coffee/create";
    }
```
Move coffee.html from /views-lec to /coffee and delete views-lec. Change all references to views-lec/coffee in the CoffeeController to coffee/coffee, including the PostMapping we just created, which should now go to ``` return "redirect:/coffee/coffee";```. This redirect is the preferred syntax for when the result of a request is to move users from one view to another.

Now let's refactor our CoffeeController to show from the database instead of artificially created objects.

CoffeeRepository.java:
```
public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
    List<Coffee> findByRoast(String roast);
}
```
CoffeeController.java:
```
  @GetMapping("/coffee/{roast}")
    public String coffeeInfo(@PathVariable String roast, Model model){
        model.addAttribute("selections", coffeeRepository.findByRoast(roast));
        return "/coffee/coffee";
    }
```

Now let's solve that problem with the long list of params. We'll use form model binding to do so.

First we change the opening form tag in create.html as follows:
```
<form th:action="@{/coffee/create}" th:method="post" th:object="${coffee}">
```
But notice IntelliJ redlines our object. It has no idea what we are talking about. Let's fix that.

```
    @GetMapping("/coffee/create")
    public String showCreateCoffee(Model model){
        model.addAttribute("coffee", new Coffee());
        return "/coffee/create";
    }
```
In the GetMapping method that leads us to the coffee-create view, we add a model as a parameter and then add a new coffee object to the model. This empty coffee object is now passed to the view every time it's rendered. The thymeleaf template engine will then bind the coffee object to the form. The logic will allow the parameters to be mapped to coffee object properties and coffee table db columns automatically. We do this as follows. For each name attribute, replace it with a ```th:field="*{name}" attribute.
create.html:
```
 <form th:action="@{/coffee/create}" th:method="post" th:object="${coffee}">
        <label for="brand">Brand:</label>
        <input type="text" th:field="*{brand}" id="brand"><br>
        <label for="origin">Origin:</label>
        <input type="text" th:field="*{origin}" id="origin"><br>
        <label for="roast">Roast:</label>
        <input type="text" th:field="*{roast}" id="roast"><br>
        <button type="submit">Submit</button>
    </form>
```
Now we just need to do one more thing in our controller to make it all work.
CoffeeController.java
```
    @PostMapping("/coffee/create")
    public String createCoffee(@ModelAttribute Coffee coffee){
        coffeeRepository.save(coffee);
        return "redirect:/coffee/coffee";
    }
```
Just get rid of the param list and add a ModelAttribute parameter. Now that you know form model binding you don't have to write endless parameter lists for every Post controller. :)

## Exercises

### 1. Create a posts/create.html view inside the templates folder. This HTML page should contain a form for creating a new post

```
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="en">
<head>
    <meta charset="UTF-8">
    <title>Create a Post</title>
</head>
<body>
    <h2>Create a Post:</h2>
    <form action="/posts/create" method="post">
        <label for="title">Title:</label>
        <input type="text" name="title" id="title">
        <label for="body">Body:</label>
        <textarea name="body" id="body" cols="30" rows="10"></textarea>
        <button type="submit">Submit Post</button>
    </form>
</body>
</html>
```

### 2. Change your controller method for showing the post creation form to actually show the form created in the step above.

```
@GetMapping("/posts/create")
    public String showCreatePostsForm(){
        return "posts/create";
    }
```

### This method should pass a new (i.e. empty) Post object to the view

```
 @GetMapping("/posts/create")
    public String showCreatePostsForm(Model model){
        model.addAttribute("post", new Post());
        return "posts/create";
    }
```

### 3. Use what you have learned in this lesson to have the post creation form submit a post object and store that post using the posts repository. After the post is created you should redirect the user to the posts index page (i.e. /posts). You can redirect by returning a string from a controller method that starts with "redirect:"

```
  @PostMapping("/posts/create")
    public String showCreatePostsProcess(@RequestParam String title, @RequestParam String body){
        User user = new User(1, "jojo", "email@email.info", "codeup");
        Post post = new Post(title, body, user);
        postRepository.save(post);
        return "redirect:/posts";
    }
```

Now we'll actually do form-model binding

```
    <form th:action="@{/posts/create}" th:method="post" th:object="${post}">
        <label for="title">Title:</label>
        <input type="text" th:field="*{title}" id="title">
        <label for="body">Body:</label>
        <textarea th:field="*{body}" id="body" cols="30" rows="10"></textarea>
        <button type="submit">Submit Post</button>
    </form>
```

And refactor the posts controller

```
   @PostMapping("/posts/create")
    public String showCreatePostsProcess(@ModelAttribute Post post){
        User user = new User(1, "jojo", "email@email.info", "codeup");
        post.setUser(user);
        postRepository.save(post);
        return "redirect:/posts";
    }
```

### 4. Create a controller method and HTML template for viewing a form to edit a specific post. This method should map to /posts/{id}/edit. When you view this page, the form should be pre-populated with the values from an existing post

```
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Post</title>
</head>
<body>
<form th:action="@{/posts/edit/{id}(id=${id})}" method="post">
    <label for="title">Title:</label>
    <input type="text" name="title" id="title" th:value="${post.title}">
    <label for="body">Body:</label>
    <textarea id="body" name="body" th:text="${post.body}" ></textarea>
    <button type="submit">Submit</button>
</form>
</body>
</html>
```
PostController.java:
```
    @GetMapping("/posts/edit/{id}")
    public String editForm(@PathVariable long id, Model model){
        model.addAttribute("post", postRepository.getById(id));
        return "posts/edit";
    }

    @PostMapping("/posts/edit/{id}")
    public String editPost(@PathVariable long id, @RequestParam String title, @RequestParam String body){
        Post post = postRepository.getById(id);
        post.setTitle(title);
        post.setBody(body);
        postRepository.save(post);
        return "redirect:/posts/" + id;
    }
}
```

I suppose we're supposed to use form-model binding on this, too.


edit.html:
```
<form th:action="@{/posts/edit/{id}(id=${id})}" th:method="post" th:object="${post}">
    <label for="title">Title:</label>
    <input type="text" th:field="*{title}" id="title" th:value="${post.title}">
    <label for="body">Body:</label>
    <textarea id="body" th:field="*{body}" th:text="${post.body}" ></textarea>
    <button type="submit">Submit</button>
</form>
```
Notice, I don't have to add the new empty Post object to the GetMapping method, because a post object is already being passed in the model.

PostController.java:
```
    @PostMapping("/posts/edit/{id}")
    public String editPost(@PathVariable long id, @ModelAttribute Post post){
        User user = new User(1, "jojo", "email@email.info", "codeup");
        post.setUser(user);
        postRepository.save(post);
        return "redirect:/posts/" + id;
    }
```

Bonus
PostController.java:
```
    @PostMapping("/posts/edit/{id}")
    public String editPost(@PathVariable long id, @ModelAttribute Post post){
        return createPosts(post);
    }
```