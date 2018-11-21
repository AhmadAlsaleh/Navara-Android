package com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.smartlife_solutions.android.navara_store.Statics;

@DatabaseTable(tableName = "category")
public class CategoryDatabaseModel {

    @DatabaseField(columnName = "id")
    private String id;

    @DatabaseField(columnName = "name")
    private String name;

    @DatabaseField(columnName = "name2")
    private String name2;

    @DatabaseField(columnName = "description")
    private String description;

    @DatabaseField(columnName = "image_path")
    private String imagePath;

    public CategoryDatabaseModel() {

    }

    public CategoryDatabaseModel(String id, String name, String description, String imagePath) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        try {
            if (Statics.getCurrentLanguageName(null).equals(Statics.arabic) && getName2().length() > 0) {
                return getName2();
            }
            return name;
        } catch (Exception e) {
            return name;
        }
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
