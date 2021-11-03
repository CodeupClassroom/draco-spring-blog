# Views

## The thymeleaf dependency

We are now going to add a dependency to our pom.xml: thymeleaf. Thymeleaf is something called a template engine. In essence, it does for us what all those JSP syntaxes did in servlets, to whit, facilitates the communication of information from Java classes to html views and the insertion of variable information into html templates.

We start by making sure our thymeleaf dependency is inside the ```<depenencies></dependencies>``` tags in our pom.xml file.

```
<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

## Basic controller-view relation

With this taken care of, Maven will make sure that thymeleaf is available to us throughout our project.

First let's set up a relationship between a controller and a view. We're going to put our views inside our src/main/resources/templates directory. To keep things from cluttering up, I'll go ahead and create a little subdirectory here, ```views-lec```.

Inside views-lec, I'll make an html file. Today I'll select the theme of coffee.

coffee.html:
```
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Coffee</title>
</head>
<body>
    <h1>Welcome to the coffee page!</h1>
</body>
</html>
```

if src/main/java/com/codeup/ doesn't yet have controllers and models packages, make them here. In the controllers package:

CoffeeController.java:

First add the @Controller annotation, which tells Spring Boot that this is a controller

```
package com.codeup.controllers;

import org.springframework.stereotype.Controller;

@Controller
public class CoffeeController {
}
```

Next, we create a GetMapping for /coffee

```
    @GetMapping("/coffee")
    public String coffeeInfo(){
        return "views-lec/coffee";
    }
```

Now pointing to localhost:8080/coffee will yield the coffee page.

## Passing GET data between view and controller

Now let's ask the user what kind of coffee they like. Note that I am using absolute paths here. In most circumstances, it doesn't matter if you use relative or absolute paths, but you do have to be aware of when you choose each one.

```
<body>
    <h1>Welcome to the coffee page!</h1>
    <h2>Select your favored roast:</h2>
    <ul>
        <li><a href="/coffee/dark">Dark Roast</a></li>
        <li><a href="/coffee/medium">Medium Roast</a></li>
        <li><a href="/coffee/light">Light Roast</a></li>
    </ul>
</body>
```

In CoffeeController:

```
 @GetMapping("/coffee/{roast}")
    public String coffeeInfo(@PathVariable String roast, Model model){
        model.addAttribute("roast", roast);
        return "views-lec/coffee";
    }
```

Back in coffee.html:

```
<html xmlns:th="http://www.thymeleaf.org" lang="en">
```

```
 <h1>Welcome to the coffee page!</h1>
    <h2 th:if="${roast}">
        <p>You selected <span th:text="${roast}"></span> roast!</p>
    </h2>
```

## Customized output with th:if

You can also use the if attribute to customize messages to some extent, although this can also be done in controllers and even models.

```
<h2 th:if="${roast}">
        <p>You selected <span th:text="${roast}"></span> roast!</p>
        <p th:if="${roast} == 'dark'">You chose correctly!</p>
        <p th:if="${roast} != 'dark'">That is incorrect!</p>
</h2>
```

Or you can generate a boolean in the controller and pass it to the view.

CoffeeController.java:
```
@GetMapping("/coffee/{roast}")
    public String coffeeInfo(@PathVariable String roast, Model model){
        model.addAttribute("roast", roast);
        boolean choseDark = false;
        if (roast.equals("dark")){
            choseDark = true;
        }
        model.addAttribute("choseDark", choseDark);
        return "views-lec/coffee";
    }
```

coffee.html:

```
<h2 th:if="${roast}">
        <p>You selected <span th:text="${roast}"></span> roast!</p>
        <p th:if="${choseDark}">You chose correctly!</p>
        <p th:if="!${choseDark}">That is incorrect!</p>
</h2>
```

## Passing POST data between view and controller

You can also use forms to pass information from a view to a controller. In this case you would use a PostMapping.

coffee.html:
```
<div>
        <p>Want to sign up for our newsletter? Enter your email!</p>
        <form action="/coffee" method="post">
            <legend for="email">Email:</legend>
            <input type="email" name="email" id="email">
            <button type="submit">Sign up</button>
        </form>
    </div>
```
CoffeeController.java:
```
@PostMapping("/coffee")
    public String newsletterSignup(@RequestParam(name="email") String email, Model model){
        model.addAttribute("email", email);
        return "views-lec/coffee";
    }
```
coffee.html:
```
<p th:if="${email}">Thanks for signing up! Newsletter will be sent to <span th:text="${email}"></span>!</p>
```

## Passing objects from controller to view

We can also pass full-scale objects using this thymeleaf model scope, and access the properties using dot notation with thymeleaf.

models/Coffee.java

```
package com.codeup.models;

public class Coffee {
    private String roast;
    private String origin;

    public Coffee() {
    }

    public Coffee(String roast, String origin) {
        this.roast = roast;
        this.origin = origin;
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

Now we can change the Controller to generate and pass objects to the view:

```
@GetMapping("/coffee/{roast}")
    public String coffeeInfo(@PathVariable String roast, Model model){
        Coffee selection = new Coffee();
        selection.setRoast(roast);
        if (roast.equals("dark")){
            selection.setOrigin("Colombia");
        } else if (roast.equals("medium")){
            selection.setOrigin("New Guinea");
        } else {
            selection.setOrigin("Kenya");
        }
        model.addAttribute("selection", selection);
        return "views-lec/coffee";
    }
```

And we modify the view to accommodate the new data structure:

```
<h2 th:if="${selection}">
        <p>You selected <span th:text="${selection.roast}"></span> roast!</p>
        <p>We have an amazing <span th:text="${selection.roast}"></span>roast from <span th:text="${selection.origin}"></span>!</p>
    </h2>
```

## Iterating over ArrayLists in the view

Modify Coffee.java. Note the modified constructor:
```
private String brand;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public Coffee(String roast, String brand) {
        this.roast = roast;
        this.brand = brand;
    }
```

Modify CoffeeController.java:

```
@GetMapping("/coffee/{roast}")
    public String coffeeInfo(@PathVariable String roast, Model model){
        Coffee selection = new Coffee(roast, "Cool Beans");
        Coffee selection2 = new Coffee(roast, "Jolting Joe");
        if (roast.equals("dark")){
            selection.setOrigin("Colombia");
            selection2.setOrigin("Brazil");
        } else if (roast.equals("medium")){
            selection.setOrigin("New Guinea");
            selection2.setOrigin("Sumatra");
        } else {
            selection.setOrigin("Kenya");
            selection2.setOrigin("Ethiopia");
        }
        List<Coffee> selections = new ArrayList<>();
        selections.add(selection);
        selections.add(selection2);
        model.addAttribute("roast", roast);
        model.addAttribute("selections", selections);
        return "views-lec/coffee";
    }
```

Modify coffee.html:

```
    <h2 th:if="${selections}">
        <p>You selected <span th:text="${roast}"></span> roast!</p>
        <p>Here are some options for you:</p>
        <ul>
            <li th:each="selection : ${selections}"><p><span th:text="${selection.brand}"></span> <span th:text="${selection.origin}"></span> <span th:text="${selection.roast}"></span> roast</p></li>
        </ul>
    </h2>
```