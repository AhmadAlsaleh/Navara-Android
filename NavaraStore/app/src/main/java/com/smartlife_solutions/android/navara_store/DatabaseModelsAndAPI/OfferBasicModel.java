package com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "offers")
public class OfferBasicModel {

    @DatabaseField(columnName = "id")
    private String id;

    @DatabaseField(columnName = "title")
    private String title;

    @DatabaseField(columnName = "short_description")
    private String shortDescription;

    @DatabaseField(columnName = "offer_type")
    private String offerType;

    @DatabaseField(columnName = "thumbnail_image_path")
    private String thumbnailImagePath;

    @DatabaseField(columnName = "discount")
    private Integer discount;

    @DatabaseField(columnName = "unit_net_price")
    private Integer unitNetPrice = 0;

    private String currencyCode = "S.P";

    public OfferBasicModel() {

    }

    public OfferBasicModel(String id, String title, String shortDescription, String offerType, String thumbnailImagePath, Integer discount, Integer unitNetPrice) {
        this.id = id;
        this.title = title;
        this.shortDescription = shortDescription;
        this.offerType = offerType;
        this.thumbnailImagePath = thumbnailImagePath;
        this.discount = discount;
        this.currencyCode = "S.P";
        this.unitNetPrice = unitNetPrice;
    }

    public Integer getUnitNetPrice() {
        if (unitNetPrice.toString().equals("null")) {
            return 0;
        }
        return unitNetPrice;
    }

    public void setUnitNetPrice(Integer unitNetPrice) {
        this.unitNetPrice = unitNetPrice;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getOfferType() {
        return offerType;
    }

    public void setOfferType(String offerType) {
        this.offerType = offerType;
    }

    public String getThumbnailImagePath() {
        return thumbnailImagePath;
    }

    public void setThumbnailImagePath(String thumbnailImagePath) {
        this.thumbnailImagePath = thumbnailImagePath;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }
}
