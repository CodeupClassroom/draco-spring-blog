package com.codeup.dracospringblog.models;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class AdImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String url;

    @ManyToOne
    @JoinColumn(name = "ad_id")
    @JsonIgnore
    private Ad ad;

    public AdImage() {
    }

    public AdImage(String url) {
        this.url = url;
    }

    public AdImage(String url, Ad ad) {
        this.url = url;
        this.ad = ad;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Ad getAd() {
        return ad;
    }

    public void setAd(Ad ad) {
        this.ad = ad;
    }
}
