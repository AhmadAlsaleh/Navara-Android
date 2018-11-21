package com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String databaseName = "navara_store.db";
    private static final int databaseVersion = 6;
    private RuntimeExceptionDao<ItemBasicModel, Integer> itemBasicModelIntegerRuntimeException = null;
    private RuntimeExceptionDao<CategoryDatabaseModel, Integer> categoryModelIntegerRuntimeException = null;
    private RuntimeExceptionDao<OfferBasicModel, Integer> offerBasicModelIntegerRuntimeException = null;
    private RuntimeExceptionDao<UserModel, Integer> userModelIntegerRuntimeException = null;

    public DatabaseHelper(Context context) {
        super(context, databaseName, null, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, ItemBasicModel.class);
            TableUtils.createTableIfNotExists(connectionSource, CategoryDatabaseModel.class);
            TableUtils.createTableIfNotExists(connectionSource, OfferBasicModel.class);
            TableUtils.createTableIfNotExists(connectionSource, UserModel.class);
            Log.e("create db", "CREATED :)");
        } catch (SQLException e) {
            Log.e("create db", e.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try {
            TableUtils.dropTable(connectionSource, ItemBasicModel.class, true);
            TableUtils.dropTable(connectionSource, CategoryDatabaseModel.class, true);
            TableUtils.dropTable(connectionSource, OfferBasicModel.class, true);
            TableUtils.dropTable(connectionSource, UserModel.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearTable(Class myClass) {
        try {
            TableUtils.clearTable(getConnectionSource(), myClass);
        } catch (SQLException e) {
            Log.e("clear table", e.getMessage());
        }
    }

    public RuntimeExceptionDao<ItemBasicModel, Integer> getItemBasicModelIntegerRuntimeException() {
        if (itemBasicModelIntegerRuntimeException == null) {
            itemBasicModelIntegerRuntimeException = getRuntimeExceptionDao(ItemBasicModel.class);
        }
        return itemBasicModelIntegerRuntimeException;
    }
    public RuntimeExceptionDao<CategoryDatabaseModel, Integer> getCategoryModelIntegerRuntimeException() {
        if (categoryModelIntegerRuntimeException == null) {
            categoryModelIntegerRuntimeException = getRuntimeExceptionDao(CategoryDatabaseModel.class);
        }
        return categoryModelIntegerRuntimeException;
    }
    public RuntimeExceptionDao<OfferBasicModel, Integer> getOfferBasicModelIntegerRuntimeException() {
        if (offerBasicModelIntegerRuntimeException == null) {
            offerBasicModelIntegerRuntimeException = getRuntimeExceptionDao(OfferBasicModel.class);
        }
        return offerBasicModelIntegerRuntimeException;
    }
    public RuntimeExceptionDao<UserModel, Integer> getUserModelIntegerRuntimeException() {
        if (userModelIntegerRuntimeException == null) {
            userModelIntegerRuntimeException = getRuntimeExceptionDao(UserModel.class);
        }
        return userModelIntegerRuntimeException;
    }


}
