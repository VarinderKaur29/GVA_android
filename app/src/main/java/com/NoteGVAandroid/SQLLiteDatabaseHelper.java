package com.NoteGVAandroid;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLLiteDatabaseHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "NOTES.DB";
    static final int DB_VERSION = 1;

    //CategoryModel Table Name
    public static final String CATEGORY_TABLE_NAME = "CATEGORIES";

    //CategoryModel Table Column
    public static final String CATEGORY_ID = "category_id";
    public static final String CATEGORY_NAME = "category_name";

    // Create CategoryModel Table Query
    private static final String CREATE_CATEGORY_TABLE = "create table " +
            CATEGORY_TABLE_NAME + "(" +
            CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CATEGORY_NAME + " TEXT NOT NULL );";


    //Notes Table Name
    public static final String NOTES_TABLE_NAME = "NOTES";

    public static final String NOTES_ID = "notes_id";
    public static final String TITLE = "title";
    public static final String DATE = "date";
    public static final String DESCRIPTION = "description";
    public static final String AUDIO = "audio";
    public static final String LOCATION = "location";
    public static final String NOTES_CATEGORY_ID = "category_id";


    //Create Notes Table Query
    private static final String CREATE_NOTES_TABLE = "create table " +
            NOTES_TABLE_NAME + " (" +
            NOTES_ID + " INTEGER , " +
            TITLE + " TEXT NOT NULL, " +
            DATE + " TEXT NOT NULL, " +
            DESCRIPTION + " TEXT, " +
            AUDIO + " TEXT, " +
            LOCATION + " TEXT, " +
            NOTES_CATEGORY_ID + " INTEGER NOT NULL); ";

    // Images Table Name
    public static final String NOTES_TABLE_IMAGES = "IMAGES";

    public static final String IMAGE = "image";
    public static final String NOTES_IMAGE_ID = "notes_id";
    public static final String IMAGE_CATEGORY_ID = "category_id";

    //Create ImageModel Table Query
    private static final String CREATE_IMAGE_TABLE = "create table " +
            NOTES_TABLE_IMAGES + " ( " +
            IMAGE + " text , " +
            NOTES_IMAGE_ID + " INTEGER NOT NULL ," +
            IMAGE_CATEGORY_ID + " INTEGER NOT NULL ) ; ";

    public SQLLiteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATE_CATEGORY_TABLE);
        sqLiteDatabase.execSQL(CREATE_NOTES_TABLE);
        sqLiteDatabase.execSQL(CREATE_IMAGE_TABLE);

    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE_IMAGES);
        onCreate(sqLiteDatabase);
    }
}
