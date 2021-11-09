# Services

## Coffee mailing list example

### Add the dependency

pom.xml
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
    <version>2.1.2.RELEASE</version>
</dependency>
```
### Add the email service class in the services directory
Add a services directory on the same level as controllers, models, and repositories
In the services directory, add the EmailService class

```
package com.codeup.dracospringblog.services;

import com.codeup.dracospringblog.models.Ad;
import com.codeup.dracospringblog.models.Ad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service("mailService")
public class EmailService {

    @Autowired
    public JavaMailSender emailSender;

    @Value("${spring.mail.from}")
    private String from;

    public void prepareAndSend(Ad ad, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
//        msg.setTo(ad.getOwner().getEmail());
        msg.setSubject(subject);
        msg.setText(body);

        try{
            this.emailSender.send(msg);
        }
        catch (MailException ex) {
            // simply log it and go on...
            System.err.println(ex.getMessage());
        }
    }
}
```

### Sign up for mail.io 
Go to mailtrap.io inbox and drop-down menu to Java Play-Mailer.

### Modify the properties files

Add to application.properties:

```
spring.mail.host=smtp.mailtrap.io
spring.mail.port=25
spring.mail.username=username
spring.mail.password=password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.from=admin@example.com 
```

Copy username, password, and port credentials from mailtrap.io into application.properties.

Adjust example.properties accordingly.

```
#spring.mail.host=smtp.mailtrap.io
#spring.mail.port=2525
#spring.mail.username=username
#spring.mail.password=password
#spring.mail.properties.mail.smtp.auth=true
#spring.mail.properties.mail.smtp.starttls.enable=true
#spring.mail.from=admin@example.com
```

### Inject the email service into a controller

CoffeeController.java
```
private final CoffeeRepository coffeeRepository;
private final EmailService emailService;

    public CoffeeController(CoffeeRepository coffeeRepository, EmailService emailService){
        this.coffeeRepository = coffeeRepository;
        this.emailService = emailService;
    }
```
## Customize the prepareAndSend method

```
public void prepareAndSend(String email, String message) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(email);
        msg.setSubject("Thanks for signing up to coffee emails!");
        msg.setText(message);

        try{
            this.emailSender.send(msg);
        }
        catch (MailException ex) {
            // simply log it and go on...
            System.err.println(ex.getMessage());
        }
    }
```

### Modify the controller to send the message

```
@PostMapping("/coffee")
    public String newsletterSignup(@RequestParam(name="email") String email, Model model){
        model.addAttribute("email", email);
        emailService.prepareAndSend(email, "You have signed up for coffee emails! Thank you!");
        return "coffee/coffee";
    }
```

## Build out a service that notifies users when they have created an Ad

### Build a create ad view

Create an ads subdirectory in the templates directory. Inside, create an index.html page

```
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="en">
<head>
    <meta charset="UTF-8">
    <title>Ads</title>
</head>
<body>
    <h1>Here are all the ads:</h1>
    <ul>
        <li th:each="ad : ${ads}">
            <h2 th:text="${ad.title}"></h2>
            <p th:text="${ad.description}"></p>
        </li>
    </ul>
</body>
</html>
```

Modify the AdController to show the new view
AdController.java:
```
@GetMapping("/ads")
public String showAds(Model model) {
model.addAttribute("ads", adRepository.findAll());
return "/ads/index";
}
```
Add a couple of ads to the db
```
INSERT INTO ads (description, title) VALUES ('ad 1', 'amazing stuff');
INSERT INTO ads (description, title) VALUES ('ad 2', 'truly amazing stuff');
```
Reload the Spring project. Test the view.

### Build create-ads functionality

Now build out a create ads view
ads/create.html:
```
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="en">
<head>
    <meta charset="UTF-8">
    <title>Create an Ad</title>
</head>
<body>
    <h1>Create an Ad</h1>
    <form th:action="@{/ads/create}" th:method="post" th:object="${ad}">
        <label for="title">Title:</label>
        <input th:field="*{title}" id="title"><br>
        <label for="description">Description:</label>
        <textarea th:field="*{description}" id="description" cols="30" rows="10"></textarea><br>
        <button type="submit">Submit Post</button>
    </form>
</body>
</html>
```
AdController.java:
```
@GetMapping("/ads/create")
    public String showCreateAdsForm(Model model){
        model.addAttribute("ad", new Ad());
        return "ads/create";
    }
    
    @PostMapping("/ads/create")
    public String createAdWithForm(@ModelAttribute Ad ad){
        adRepository.save(ad);
        return "redirect:/ads";
    }
```

### Modify the Ad model so it's a user who has ownership of the ad, like in Adlister

Looking at our current User class, I see three of our columns aren't annotated for JPA, and we don't have the four-argument constructor. Let's go ahead and do that.

User.java:
```
@Column(nullable = false, length = 25)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;
```
```
 public User(long id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }
```

Now we add a user to our Ad object.

Ad.java:
```
 @OneToOne
    private User owner;
```
```
 public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
```
Run the project to make sure our db is up to date. We should get an owner_id column in our ads table.

Create a sample user. Verify they are in the database.
```
INSERT INTO users (email, password, username) VALUES ('jojo@jojo.info', 'password', 'jojo');
```
Update the ads table to the user owns the ads.
```
UPDATE ads SET owner_id = 1;
```

### Make the service send an email when a user creates a new ad

Inject the email service in the AdController
AdController.java
```
private final AdRepository adRepository;
    private final EmailService emailService;

    public AdController(AdRepository adRepository, EmailService emailService) {
        this.emailService = emailService;
        this.adRepository = adRepository;
    }
```
Modify the post-mapped createAd method to send an email
```
@PostMapping("/ads/create")
    public String createAdWithForm(@ModelAttribute Ad ad){
        User user = new User(1, "jojo", "jojo@jojo.info", "password");
        ad.setOwner(user);
        adRepository.save(ad);
        emailService.prepareAndSend(ad, "You created" + ad.getTitle(), ad.getDescription());
        return "redirect:/ads";
    }
```

Create an ad and verify that the email was received at mailtrap.io


## Syllabus example

pom.xml
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
    <version>2.1.2.RELEASE</version>
</dependency>
```
Add a services directory on the same level as controllers, models, and repositories
In the services directory, add the EmailService class

```
@Service("mailService")
public class EmailService {

    @Autowired
    public JavaMailSender emailSender;

    @Value("${spring.mail.from}")
    private String from;

    public void prepareAndSend(Ad ad, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(ad.getOwner().getEmail());
        msg.setSubject(subject);
        msg.setText(body);

        try{
            this.emailSender.send(msg);
        }
        catch (MailException ex) {
            // simply log it and go on...
            System.err.println(ex.getMessage());
        }
    }
}
```
Sign up for mailtrap.io

Add to application.properties:

```
spring.mail.host=smtp.mailtrap.io
spring.mail.port=25
spring.mail.username=username
spring.mail.password=password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.from=admin@example.com 
```

Go to mailtrap.io inbox and drop-down menu to Java Play-Mailer. Copy username, password, and port credentials into application.properties.

Adjust example.properties accordingly.

```
#spring.mail.host=smtp.mailtrap.io
#spring.mail.port=2525
#spring.mail.username=username
#spring.mail.password=password
#spring.mail.properties.mail.smtp.auth=true
#spring.mail.properties.mail.smtp.starttls.enable=true
#spring.mail.from=admin@example.com
```

Inject the email service into the controller, e.g.
AdController.java
```
    private final AdRepository adRepository;
    private final EmailService emailService;

    public AdController(AdRepository adRepository, EmailService emailService) {
        this.emailService = emailService;
        this.adRepository = adRepository;
    }
```
Modify the example user so that it is an email that can get emails and you can verify this works. First insert the user into the database so it's in the users table. Then modify the example user that gets used when ads are created.

AdController.java:
```
    @PostMapping("/ads/create")
    public String createAd(@ModelAttribute Ad ad){
        User user = new User(2, "javier", "javier@codeup.com", "codeup");
        ad.setOwner(user);
        adRepository.save(ad);
        return "redirect:/ads";
    }
```

Now we have to go ahead and use the email service class prepareAndSend method

Note: refactor to inject the UserRepository into the AdController, and use the userRepository.getById(1L) to get the user.

AdController.java:
```
@PostMapping("/ads/create")
    public String createAd(@ModelAttribute Ad ad){
        User user = new User(2, "javier", "javier@codeup.com", "codeup");
        ad.setOwner(user);
        adRepository.save(ad);
        emailService.prepareAndSend(ad, "You created" + ad.getTitle(), ad.getDescription());
        return "redirect:/ads";
    }
```

It worked ... you check the  mailtrap.io site ... which means you could use any email, including the original fake email for jojo ... 

## Exercise

Inject the email service dependency into the PostController
PostController.java:
```
private final EmailService emailService;

    public PostController(PostRepository postsDao, UserRepository usersDao, EmailService emailService) {
        this.postsDao = postsDao;
        this.usersDao = usersDao;
        this.emailService = emailService;
    }
```

Overload the prepareAndSend method
EmailService.java:
```
public void prepareAndSend(Post post, String title, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(post.getUser().getEmail());
        msg.setSubject(title);
        msg.setText(body);

        try{
            this.emailSender.send(msg);
        }
        catch (MailException ex) {
            // simply log it and go on...
            System.err.println(ex.getMessage());
        }
    }
```

Modify the post creation method:
PostController.java:
```
emailService.prepareAndSend(post, "You submitted: " + post.getTitle(), post.getBody());
```

