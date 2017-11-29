package com.nutrifit.tipper.nutrifit.Database;

/**
 * Created by tipper on 11/24/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.nutrifit.tipper.nutrifit.Model.User;

//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;

/**
 * Created by tipper on 9/8/17.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "NutrifitDB";

    // User table name
    private static final String TABLE_USER = "User";

    // User Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_FITNESS_GOALS = "fitness_goals";
    private static final String KEY_CALORIES_BURNED_PER_DAY = "caloriesToBurnPerDay";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_FIRST_NAME + " TEXT,"
                + KEY_LAST_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT,"
                + KEY_PASSWORD + " TEXT,"
                + KEY_GENDER + " TEXT,"
                + KEY_FITNESS_GOALS + " TEXT,"
                + KEY_CALORIES_BURNED_PER_DAY + " INTEGER, "
                + "UNIQUE (" + KEY_EMAIL + ") ON CONFLICT ROLLBACK)"; //helps avoid duplicates

        db.execSQL(CREATE_USER_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v("Upgrading Table", "True");
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /********** USER QUERIES **********/

    public boolean addUser(User user, Context context)
    {
        boolean userExist = false;

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FIRST_NAME, user.getFirstName());
        values.put(KEY_LAST_NAME, user.getLastName());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_GENDER, user.getGender());
        values.put(KEY_FITNESS_GOALS, user.getFitnessGoals());
        values.put(KEY_CALORIES_BURNED_PER_DAY, user.getCaloriesToBurnPerDay());

        try {
            db.insertOrThrow(TABLE_USER, null, values);
        } catch (SQLiteConstraintException e) {
            userExist = true;
            Toast.makeText(context, "User already exist, please try again", Toast.LENGTH_SHORT);
            e.printStackTrace();
        }

        db.close();
        return userExist;
    }

    public void updateUser(User user)
    {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(KEY_FITNESS_GOALS, user.getFitnessGoals());
        values.put(KEY_CALORIES_BURNED_PER_DAY, user.getCaloriesToBurnPerDay());

        // updating row
        db.update(TABLE_USER, values, KEY_EMAIL + " = ?",
                new String[] { user.getEmail() });
    }

    public User getUser(String email)
    {
        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.query(TABLE_USER, new String[] { KEY_ID, KEY_FIRST_NAME, KEY_LAST_NAME, KEY_EMAIL, KEY_PASSWORD, KEY_GENDER, KEY_FITNESS_GOALS, KEY_CALORIES_BURNED_PER_DAY}, KEY_EMAIL + "=?",
                new String[] {email}, null, null, null, null);

        if(cursor.getCount() != 0) {
            cursor.moveToFirst();

            User retUser = new User(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getInt(7));
            retUser.setId(cursor.getInt(0));

            return retUser;

        } else {
            return null;
        }

    }

}
