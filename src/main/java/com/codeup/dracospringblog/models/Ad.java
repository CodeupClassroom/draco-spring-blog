package com.codeup.dracospringblog.models;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ads")
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = true, length = 300)
    private String title;

    @Column(columnDefinition = "TEXT NOT NULL")
    private String description;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ad")
    private List<AdImage> images;

    public Ad() {
    }

    public Ad(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Ad(long id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }


    public Ad(String title, String description, List<AdImage> images) {
        this.title = title;
        this.description = description;
        this.images = images;
    }

    public Ad(long id, String title, String description, List<AdImage> images) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.images = images;
    }

    public List<AdImage> getImages() {
        return images;
    }

    public void setImages(List<AdImage> images) {
        this.images = images;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
