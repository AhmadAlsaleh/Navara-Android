package com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "items")
public class ItemBasicModel {

    @DatabaseField(columnName = "id")
    private String id;

    @DatabaseField(columnName = "name")
    private String name;

    @DatabaseField(columnName = "item_category")
    private String itemCategory;

    @DatabaseField(columnName = "item_category_id")
    private String itemCategoryID;

    @DatabaseField(columnName = "quantity")
    private int quantity;

    @DatabaseField(columnName = "price")
    private float price;

    @DatabaseField(columnName = "thumbnail_image_path")
    private String thumbnailImagePath;

    private String currencyCode = "S.P";

    private boolean isFree = false;

    private int discount = 0;

    private boolean isChecked = false;

    private String offerID = "";

    public ItemBasicModel() {

    }

    public ItemBasicModel(String id, String name, String itemCategory,
                          String itemCategoryID,
                          int quantity, float price,
                          String thumbnailImagePath) {
        this.id = id;
        this.name = name;
        this.itemCategory = itemCategory;
        this.itemCategoryID = itemCategoryID;
        this.quantity = quantity;
        this.price = price;
        this.currencyCode = "S.P";
        this.thumbnailImagePath = thumbnailImagePath;
        this.isFree = false;
        this.discount = 0;
    }

    public String getOfferID() {
        if (offerID.equals("null")) {
            return "";
        }
        return offerID;
    }

    public void setOfferID(String offerID) {
        this.offerID = offerID;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public String getThumbnailImagePath() {
        return thumbnailImagePath;
    }

    public void setThumbnailImagePath(String thumbnailImagePath) {
        this.thumbnailImagePath = thumbnailImagePath;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getItemCategoryID() {
        return itemCategoryID;
    }

    public void setItemCategoryID(String itemCategoryID) {
        this.itemCategoryID = itemCategoryID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return (int) price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}