
package com.cupcakecorner.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.cupcakecorner.models.User;
import com.cupcakecorner.models.Category;
import com.cupcakecorner.models.Order;
import com.cupcakecorner.models.admin;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private boolean test = true;
    private static final String DATABASE_NAME = "cupcakecorner.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String COLUMN_PRICE = "price";
    private static final String TABLE_ORDERS = "orders";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DETAILS = "details";
    private static DatabaseHelper dbConnection = null;
    private SQLiteDatabase writableDatabase;
    private SQLiteDatabase readableDatabase;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (dbConnection == null) {
            dbConnection = new DatabaseHelper(context.getApplicationContext());
        }
        return dbConnection;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_EMAIL + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_PRICE + " INTEGER" + ")";
        db.execSQL(CREATE_CATEGORIES_TABLE);

        String CREATE_ORDERS_TABLE = "CREATE TABLE " + TABLE_ORDERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DETAILS + " TEXT" + ")";
        db.execSQL(CREATE_ORDERS_TABLE);

        addAdminUserIfNotExists(db);
        if (test) {
            addSampleCategories(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        onCreate(db);
    }

    public boolean createUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_EMAIL, user.getEmail());

        Cursor cursor = db.rawQuery("SELECT MAX(" + COLUMN_ID + ") FROM " + TABLE_USERS, null);
        int newId = 1;

        if (cursor != null && cursor.moveToFirst()) {
            int maxId = cursor.getInt(0);
            newId = maxId + 1;
        }

        if (cursor != null) {
            cursor.close();
        }

        values.put(COLUMN_ID, newId);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean checkUser(String userName, String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=?" + " OR " + COLUMN_EMAIL + "=?",
                new String[] { userName, email });
        return cursor.getCount() > 0;

    }

    public User getUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[] { COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_EMAIL },
                COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[] { username, password }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            User user = new User(cursor.getString(1), cursor.getString(2), cursor.getString(3));
            user.setId(cursor.getInt(0));
            return user;
        }

        return null;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        User user = null;

        try {
            cursor = db.query(TABLE_USERS,
                    new String[] { COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_EMAIL },
                    COLUMN_ID + "=?",
                    new String[] { String.valueOf(userId) },
                    null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int usernameIndex = cursor.getColumnIndex(COLUMN_USERNAME);
                int passwordIndex = cursor.getColumnIndex(COLUMN_PASSWORD);
                int emailIndex = cursor.getColumnIndex(COLUMN_EMAIL);
                int idIndex = cursor.getColumnIndex(COLUMN_ID);

                if (usernameIndex >= 0 && passwordIndex >= 0 && emailIndex >= 0 && idIndex >= 0) {
                    user = new User(cursor.getString(usernameIndex),
                            cursor.getString(passwordIndex),
                            cursor.getString(emailIndex));
                    user.setId(cursor.getInt(idIndex));
                } else {
                    Log.e("DatabaseHelper", "Column index not found.");
                }
            } else {
                Log.d("DatabaseHelper", "No user found for ID: " + userId);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error retrieving user by ID", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return user;
    }

    public User getUserByName(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        User user = null;

        try {
            cursor = db.query(TABLE_USERS,
                    new String[] { COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_EMAIL },
                    COLUMN_USERNAME + "=?",
                    new String[] { username },
                    null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int usernameIndex = cursor.getColumnIndex(COLUMN_USERNAME);
                int passwordIndex = cursor.getColumnIndex(COLUMN_PASSWORD);
                int emailIndex = cursor.getColumnIndex(COLUMN_EMAIL);
                int idIndex = cursor.getColumnIndex(COLUMN_ID);

                if (usernameIndex >= 0 && passwordIndex >= 0 && emailIndex >= 0 && idIndex >= 0) {
                    user = new User(cursor.getString(usernameIndex),
                            cursor.getString(passwordIndex),
                            cursor.getString(emailIndex));
                    user.setId(cursor.getInt(idIndex));
                } else {

                    Log.e("DatabaseHelper", "Column index not found.");
                }
            } else {
                Log.d("DatabaseHelper", "No user found for Username: " + username);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error retrieving user by ID", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return user;
    }

    public boolean verifyUserPassword(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean isPasswordCorrect = false;

        try {
            cursor = db.query(TABLE_USERS,
                    new String[] { COLUMN_PASSWORD },
                    COLUMN_USERNAME + "=?",
                    new String[] { username },
                    null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int passwordColumnIndex = cursor.getColumnIndex(COLUMN_PASSWORD);

                if (passwordColumnIndex != -1) {
                    String storedPassword = cursor.getString(passwordColumnIndex);
                    isPasswordCorrect = storedPassword.equals(password);
                } else {
                    throw new IllegalStateException("Column index for " + COLUMN_PASSWORD + " is invalid.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return isPasswordCorrect;
    }

    public boolean updateUserPassword(String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);

        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USERNAME + "=?", new String[] { username });

        return rowsAffected > 0;
    }

    public String updateUserDetails(String username, String newUsername, String newEmail) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=? OR " + COLUMN_EMAIL + "=?",
                new String[] { newUsername, newEmail });

        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return "Username or email already exists";
        }

        if (cursor != null) {
            cursor.close();
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, newUsername);
        values.put(COLUMN_EMAIL, newEmail);

        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USERNAME + "=?", new String[] { username });

        return rowsAffected > 0 ? "true" : "false";
    }

    public boolean createCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, category.getName());
        values.put(COLUMN_PRICE, category.getPrice());

        long result = db.insert(TABLE_CATEGORIES, null, values);
        return result != -1;
    }

    public boolean deleteCategory(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_CATEGORIES, COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
        return rowsAffected > 0;
    }

    public boolean updateCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, category.getName());
        values.put(COLUMN_PRICE, category.getPrice());

        int rowsAffected = db.update(TABLE_CATEGORIES, values, COLUMN_ID + " = ?",
                new String[] { String.valueOf(category.getId()) });
        return rowsAffected > 0;
    }

    public Category getCategoryByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES, new String[] { COLUMN_ID, COLUMN_NAME, COLUMN_PRICE },
                COLUMN_NAME + "=?", new String[] { name }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            Category category = new Category();
            category.setId(cursor.getInt(0));
            category.setName(cursor.getString(1));
            category.setPrice(cursor.getInt(2));
            return category;
        }

        return null;
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CATEGORIES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                category.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                category.setPrice(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRICE)));
                categories.add(category);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }

        return categories;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ORDERS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                order.setId(cursor.getInt(0));
                order.setDetails(cursor.getString(1));
                orders.add(order);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return orders;
    }

    public boolean createOrder(Order order) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DETAILS, order.getDetails());

        long result = db.insert(TABLE_ORDERS, null, values);

        return result != -1;
    }

    public boolean updateOrder(Order order) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DETAILS, order.getDetails());

        int rowsAffected = db.update(TABLE_ORDERS, values, COLUMN_ID + " = ?",
                new String[] { String.valueOf(order.getId()) });
        return rowsAffected > 0;
    }

    public boolean deleteOrder(int orderId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_ORDERS, COLUMN_ID + " = ?", new String[] { String.valueOf(orderId) });
        return rowsDeleted > 0;
    }

    private void addAdminUserIfNotExists(SQLiteDatabase db) {
        admin adminUser = new admin();
        String adminUsername = adminUser.getAdminUsername();
        String adminPassword = adminUser.getAdminPassword();
        String adminEmail = adminUser.getAdminEmail();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=?",
                new String[] { adminUsername });
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, adminUsername);
            values.put(COLUMN_PASSWORD, adminPassword);
            values.put(COLUMN_EMAIL, adminEmail);
            db.insert(TABLE_USERS, null, values);
        }
        cursor.close();
    }

    public void addSampleCategories(SQLiteDatabase db) {
        List<Category> sampleCategories = new ArrayList<>();
        sampleCategories.add(new Category("Chocolate", 200));
        sampleCategories.add(new Category("Vanilla", 250));
        sampleCategories.add(new Category("Strawberry", 400));
        sampleCategories.add(new Category("Red Velvet", 400));
        sampleCategories.add(new Category("Lemon", 300));

        for (Category category : sampleCategories) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, category.getName());
            values.put(COLUMN_PRICE, category.getPrice());
            db.insert(TABLE_CATEGORIES, null, values);
        }

    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        if (writableDatabase == null || !writableDatabase.isOpen()) {
            writableDatabase = super.getWritableDatabase();
        }
        return writableDatabase;
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase() {
        if (readableDatabase == null || !readableDatabase.isOpen()) {
            readableDatabase = super.getReadableDatabase();
        }
        return readableDatabase;
    }

}