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

    @DatabaseField(columnName = "cash_back")
    private String cashBack;

    @DatabaseField(columnName = "account_id")
    private String accountID;

    @DatabaseField(columnName = "days_to_be_available")
    private Integer daysToBeAvilable = 0;

    private String currencyCode = "S.P";

    private boolean isFree = false;

    private int discount = 0;

    private boolean isChecked = false;

    private String offerID = "";

    private boolean isEnable = false;

    public ItemBasicModel() {

    }

    public Integer getDaysToBeAvilable() {
        if (daysToBeAvilable == null) {
            return 0;
        }
        return daysToBeAvilable;
    }

    public void setDaysToBeAvilable(Integer daysToBeAvilable) {
        if (daysToBeAvilable == null) {
            this.daysToBeAvilable = 0;
        } else {
            this.daysToBeAvilable = daysToBeAvilable;
        }
    }

    public ItemBasicModel(String id, String name,
                          String itemCategory,
                          String itemCategoryID,
                          int quantity, float price,
                          String thumbnailImagePath,
                          String cashBack,
                          String accountID,
                          Integer daysToBeAvilable) {
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
        this.cashBack = cashBack;
        this.accountID = accountID;
        this.daysToBeAvilable = daysToBeAvilable;
    }

    public String getAccountID() {
        try {
            if (accountID.equals("null")) {
                return "";
            }
            return accountID;
        } catch (Exception e) {
            return "";
        }
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public String getCashBack() {
        try {
            if (cashBack.equals("null")) {
                return "0.0";
            }
            return cashBack;
        } catch (Exception e) {
            return "0.0";
        }
    }

    public void setCashBack(String cashBack) {
        this.cashBack = cashBack;
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