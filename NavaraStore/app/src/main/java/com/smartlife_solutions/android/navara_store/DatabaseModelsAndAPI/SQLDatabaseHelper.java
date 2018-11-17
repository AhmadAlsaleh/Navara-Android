package com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.Pair;
import android.util.Log;

import java.util.ArrayList;

public class SQLDatabaseHelper extends SQLiteOpenHelper {

    private static final String databaseName = "navarastore.db";
    private static final int databaseVersion = 1;

    private static final String CREATE_OFFERS_TABLE = "CREATE TABLE IF NOT EXISTS offers (" +
            "id TEXT, title TEXT, short_description TEXT, offer_type TEXT," +
            "thumbnail_image_path TEXT, discount INTEGER, unit_net_price INTEGER)";

    private static final String DROP_OFFERS_TABLE = "DROP TABLE IF EXISTS offers";

    private static final String GET_ALL_OFFERS = "SELECT id, title, short_description," +
            "offer_type, thumbnail_image_path, discount, unit_net_price FROM offers";

    public SQLDatabaseHelper(Context context) {
        super(context, databaseName, null, databaseVersion);
    }

    @SuppressLint("SQLiteString")
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_OFFERS_TABLE);
        Log.e("create", ":)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_OFFERS_TABLE);
        onCreate(db);
    }

    public void clearOffers() {
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            sqLiteDatabase.delete("offers", null, null);
            sqLiteDatabase.close();
        } catch (Exception ignored) {}
    }

    public boolean insertOffer(OfferBasicModel offer) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", offer.getId());
        contentValues.put("title", offer.getTitle());
        contentValues.put("short_description", offer.getShortDescription());
        contentValues.put("offer_type", offer.getOfferType());
        contentValues.put("thumbnail_image_path", offer.getThumbnailImagePath());
        contentValues.put("discount", offer.getDiscount());
        contentValues.put("unit_net_price", offer.getUnitNetPrice());
        long result = sqLiteDatabase.insert("offers", null, contentValues);
        sqLiteDatabase.close();
        return result != -1;
    }

    @SuppressLint("Recycle")
    public ArrayList<OfferBasicModel> getAllOffers() {
        ArrayList<OfferBasicModel> offerBasicModels = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(GET_ALL_OFFERS, null);
        try {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    offerBasicModels.add(new OfferBasicModel(cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getInt(5),
                            cursor.getInt(6)));
                    cursor.moveToNext();
                }
            }
        } catch (Exception ignored) {}
        cursor.close();
        sqLiteDatabase.close();
        return offerBasicModels;
    }
}
