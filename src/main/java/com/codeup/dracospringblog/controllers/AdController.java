package com.codeup.dracospringblog.controllers;

import com.codeup.dracospringblog.models.Ad;
import com.codeup.dracospringblog.models.AdImage;
import com.codeup.dracospringblog.repositories.AdRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdController {

    private final AdRepository adsDao;

    public AdController(AdRepository adsDao) {
        this.adsDao = adsDao;
    }

    @GetMapping("/ads")
    @ResponseBody
    public List<Ad> showAds() {
        return adsDao.findAll();
    }

    @GetMapping("/ads/{id}")
    @ResponseBody
    public Ad showSingleAd(@PathVariable long id) {
        return adsDao.findById(id).get();
    }

    @GetMapping("/ads/byTitle")
    @ResponseBody
    public List<Ad> getByTitle(@RequestParam(name="title") String title){
        return adsDao.findByTitle(title);
    }

    @GetMapping("/ads/byTitlePart")
    @ResponseBody
    public List<Ad> getByTitlePart(@RequestParam(name="titlePart") String titlePart){
        return adsDao.findByTitleLike(titlePart);
    }

    @GetMapping("/ads/byDescription")
    @ResponseBody
    public Ad getByDescription(@RequestParam(name="description") String description){
        return adsDao.findByDescription(description);
    }

    @GetMapping("/ads/create")
    public String getAdsCreate() {
        return "/ad-create";
    }

    @PostMapping("/ads")
    public String createAd(@RequestParam String title, @RequestParam String description, @RequestParam(required = false) List<String> urls) {
        List<AdImage> images = null;
        Ad ad = new Ad(title, description);
        if (urls != null) {
            images = urls.stream().map(url -> {
                AdImage adImage = new AdImage(url);
                adImage.setAd(ad);
                return adImage;
            }).collect(Collectors.toList());
        }
        ad.setImages(images);
        adsDao.save(ad);
        return "redirect:/ads";
    }

// To test the creation of an ad without the concern of the view, try using a fetch!
// After that works, binding your data to a view will require much less troubleshooting
// because you KNOW the repo, entity, and controller methods are fine!

/*
    fetch("/ads",
    {
        method: "POST",
        headers: {"content-type": "application/json"},
        body: JSON.stringify({
            title: "intellisense is awful",
            description: "for sale: one awful predictive text ai. price negotiable"
            })
    })
    .then(res => res.json())
    .then(data => console.log(data))
* */


}
