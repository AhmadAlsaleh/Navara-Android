package com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI;

import java.io.Serializable;
import java.util.ArrayList;

public class ProjectBasicModel implements Serializable {

    private String name, description;
    private ArrayList<String> images;
    private ArrayList<ItemBasicModel> items;

    public ProjectBasicModel(String name, String description, ArrayList<String> images, ArrayList<ItemBasicModel> items) {
        this.name = name;
        this.description = description;
        this.images = images;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public ArrayList<ItemBasicModel> getItems() {
        return items;
    }

    public void setItems(ArrayList<ItemBasicModel> items) {
        this.items = items;
    }
}
