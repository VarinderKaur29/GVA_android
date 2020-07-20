package com.NoteGVAandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private SQLLiteDatabaseHelper sqlHelper;
    private Context context;
    private SQLiteDatabase database;
    public DatabaseManager(Context context) {
        this.context = context;
    }

    public DatabaseManager open() {
        sqlHelper = new SQLLiteDatabaseHelper(context);
        database = sqlHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        sqlHelper.close();
    }

    public void insertCategory(String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(sqlHelper.CATEGORY_NAME, name);
        database.insert(sqlHelper.CATEGORY_TABLE_NAME, null, contentValues);
    }

    public List<CategoryModel> getCategories() {
        List<CategoryModel> categories = new ArrayList<>();
        Cursor c = database.rawQuery("SELECT * FROM " + sqlHelper.CATEGORY_TABLE_NAME, null);
        if (c.moveToFirst()) {
            do {
                CategoryModel categoryModel = new CategoryModel();
                categoryModel.CATEGORY_ID = c.getInt(0);
                categoryModel.CATEGORY_NAME = c.getString(1);
                categories.add(categoryModel);

            } while (c.moveToNext());
        }
        return categories;
    }

    public void insertNote(int note_id, String title, String date, String text, String audio, String location, int category_id) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(sqlHelper.NOTES_ID, note_id);
        contentValues.put(sqlHelper.TITLE, title);
        contentValues.put(sqlHelper.DATE, date);
        contentValues.put(sqlHelper.DESCRIPTION, text);
        contentValues.put(sqlHelper.AUDIO, audio);
        contentValues.put(sqlHelper.LOCATION, location);
        contentValues.put(sqlHelper.NOTES_CATEGORY_ID, category_id);

        database.insert(sqlHelper.NOTES_TABLE_NAME, null, contentValues);

    }

    public List<NoteModel> getNotes(int category_id) {
        List<NoteModel> noteModels = new ArrayList<>();
        Cursor c = database.rawQuery("SELECT * FROM " + sqlHelper.NOTES_TABLE_NAME + " WHERE " + sqlHelper.NOTES_CATEGORY_ID + " = " + category_id, null);
        if (c.moveToFirst()) {

            do {

                NoteModel noteModel = new NoteModel();
                noteModel.NOTE_ID = c.getInt(0);
                noteModel.TITLE = c.getString(1);
                noteModel.DATE = c.getString(2);
                noteModel.DESCRIPTION = c.getString(3);
                noteModel.AUDIO = c.getString(4);
                noteModel.LOCATION = c.getString(5);
                noteModel.CATEGORY_ID = c.getInt(6);

                noteModels.add(noteModel);
            } while (c.moveToNext());
        }
        return noteModels;
    }

    public List<NoteModel> searchNotes(int subject_id, String search) {
        List<NoteModel> noteModels = new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM " + sqlHelper.NOTES_TABLE_NAME + " WHERE " + sqlHelper.NOTES_CATEGORY_ID + " = " + subject_id + " and ( " + sqlHelper.TITLE + " LIKE '%" + search + "%' or " + sqlHelper.DESCRIPTION + " LIKE '%" + search + "%' )", null);
        if (c.moveToFirst()) {

            do {
                NoteModel noteModel = new NoteModel();
                noteModel.NOTE_ID = c.getInt(0);
                noteModel.TITLE = c.getString(1);
                noteModel.DATE = c.getString(2);
                noteModel.DESCRIPTION = c.getString(3);
                noteModel.AUDIO = c.getString(4);
                noteModel.LOCATION = c.getString(5);
                noteModel.CATEGORY_ID = c.getInt(6);
                noteModels.add(noteModel);

            } while (c.moveToNext());
        }
        return noteModels;
    }


    public NoteModel getSingleNote(int note_id) {
        Cursor c = database.rawQuery("SELECT * FROM " + sqlHelper.NOTES_TABLE_NAME + " WHERE " + sqlHelper.NOTES_ID + " = '" + note_id + "'", null);

        if (c.moveToFirst()) {
            NoteModel noteModel = new NoteModel();

            noteModel.NOTE_ID = c.getInt(0);
            noteModel.TITLE = c.getString(1);
            noteModel.DATE = c.getString(2);
            noteModel.DESCRIPTION = c.getString(3);
            noteModel.AUDIO = c.getString(4);
            noteModel.LOCATION = c.getString(5);
            noteModel.CATEGORY_ID = c.getInt(6);
            return noteModel;
        }

        return null;

    }

    public void deleteNote(int note_id, int category_id) {
        database.delete(sqlHelper.NOTES_TABLE_IMAGES, "NOTES_ID = ?" + " and CATEGORY_ID = ?",
                new String[]{
                        String.valueOf(note_id),
                        String.valueOf(category_id)
                });
        database.delete(sqlHelper.NOTES_TABLE_NAME, "NOTES_ID = ?" + " and CATEGORY_ID = ?",
                new String[]{
                        String.valueOf(note_id),
                        String.valueOf(category_id)
                });

    }

    public void deleteCategory(int category_id) {
        database.delete(sqlHelper.NOTES_TABLE_IMAGES, " CATEGORY_ID = ?",
                new String[]{
                        String.valueOf(category_id)
                });
        database.delete(sqlHelper.NOTES_TABLE_NAME, " CATEGORY_ID = ?",
                new String[]{
                        String.valueOf(category_id)
                });
        database.delete(sqlHelper.CATEGORY_TABLE_NAME, " CATEGORY_ID = ?",
                new String[]{
                        String.valueOf(category_id)
                });

    }


    public void updateNote(int note_id, String title, String date, String text, String audio, String location, int category_id) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(sqlHelper.NOTES_ID, note_id);
        contentValues.put(sqlHelper.TITLE, title);
        contentValues.put(sqlHelper.DATE, date);
        contentValues.put(sqlHelper.DESCRIPTION, text);

        contentValues.put(sqlHelper.AUDIO, audio);
        contentValues.put(sqlHelper.LOCATION, location);
        contentValues.put(sqlHelper.NOTES_CATEGORY_ID, category_id);

        database.update(sqlHelper.NOTES_TABLE_NAME, contentValues, "NOTES_ID = ?", new String[]{
                String.valueOf(note_id)
        });

    }

    public void insertImage(String image, int notes_id, int category_id) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(sqlHelper.IMAGE, image);
        contentValues.put(sqlHelper.NOTES_ID, notes_id);
        contentValues.put(sqlHelper.NOTES_CATEGORY_ID, category_id);

        database.insert(sqlHelper.NOTES_TABLE_IMAGES, null, contentValues);

    }

    public List<ImageModel> getImages(int note_id, int category_id) {
        List<ImageModel> imageModels = new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM " + sqlHelper.NOTES_TABLE_IMAGES + " WHERE " + sqlHelper.NOTES_CATEGORY_ID + " = " + category_id + " and " + sqlHelper.NOTES_IMAGE_ID + "=" + note_id, null);

        if (c.moveToFirst()) {

            do {

                ImageModel imageModel = new ImageModel();
                imageModel.IMAGE = c.getString(0);

                imageModels.add(imageModel);

            } while (c.moveToNext());

        }

        return imageModels;

    }

    public List<NoteModel> getSortedNotes(int category_id, String sortOrder) {
        List<NoteModel> noteModels = new ArrayList<>();
        Cursor c = database.rawQuery("SELECT * FROM " + sqlHelper.NOTES_TABLE_NAME + " WHERE " + sqlHelper.NOTES_CATEGORY_ID + " = " + category_id + " order by " + sortOrder, null);
        if (c.moveToFirst()) {

            do {

                NoteModel noteModel = new NoteModel();
                noteModel.NOTE_ID = c.getInt(0);
                noteModel.TITLE = c.getString(1);
                noteModel.DATE = c.getString(2);
                noteModel.DESCRIPTION = c.getString(3);
                noteModel.AUDIO = c.getString(4);
                noteModel.LOCATION = c.getString(5);
                noteModel.CATEGORY_ID = c.getInt(6);

                noteModels.add(noteModel);
            } while (c.moveToNext());
        }
        return noteModels;
    }
}
