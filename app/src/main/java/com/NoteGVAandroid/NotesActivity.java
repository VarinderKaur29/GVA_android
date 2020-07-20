package com.NoteGVAandroid;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class NotesActivity extends AppCompatActivity {

    //Variables
    private RecyclerView note_List;
    private List<NoteModel> note_Model_Data;
    private Adapter adapter;
    EditText search_et;
    ArrayList<NoteModel> localList;
    String search;
    DatabaseManager db;
    int noteID;
    int categoryID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        initDataView();

    }

    // Instance of DatabaseManager
    public void dataBase() {
        db = new DatabaseManager(NotesActivity.this);
        db.open();
    }

    private void initDataView() {

        search_et = findViewById(R.id.search_et);
        search_et = findViewById(R.id.search_et);
        note_Model_Data = new ArrayList<>();
        note_List = findViewById(R.id.notes_list);
        note_List.setLayoutManager(new LinearLayoutManager(NotesActivity.this, RecyclerView.VERTICAL, false));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                }
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        202);
                return;
            }
        }


        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    search();
                } else {
                    filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void filter(String text) {

        localList = new ArrayList<>();
        localList.clear();
        for (NoteModel item : note_Model_Data) {
            if (item.TITLE.toLowerCase().contains(text.toLowerCase())) {
                localList.add(item);
            }
        }
        adapter.filterList(localList);
    }

    public void add_note(View view) {

        Intent i = new Intent(NotesActivity.this, AddNoteActivity.class);
        i.putExtra("category_id", getIntent().getIntExtra("category_id", 0));
        startActivity(i);

    }

    public void search() {


        search = search_et.getText().toString().trim();

        if (!search.equalsIgnoreCase("")) {

            dataBase();
            int category_id = getIntent().getIntExtra("category_id", 0);
            note_Model_Data = db.searchNotes(category_id, search);
            db.close();
            adapter = new Adapter();
            note_List.setAdapter(adapter);
        } else {
            dataBase();
            int category_id = getIntent().getIntExtra("category_id", 0);
            note_Model_Data = db.getNotes(category_id);
            db.close();
            adapter = new Adapter();
            note_List.setAdapter(adapter);
        }
    }

    // Adapter Data of NoteModel Class

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, text, date;
        LinearLayout note_Layout;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            title = itemView.findViewById(R.id.title);
            text = itemView.findViewById(R.id.text);
            date = itemView.findViewById(R.id.date);
            note_Layout = itemView.findViewById(R.id.note_layout);
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        public void filterList(ArrayList<NoteModel> filterList) {
            note_Model_Data = filterList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(NotesActivity.this).inflate(R.layout.note_cell, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

            final NoteModel noteModel = note_Model_Data.get(position);
            holder.title.setText(noteModel.TITLE);
            holder.text.setText(noteModel.DESCRIPTION);
            holder.date.setText(noteModel.DATE);
            holder.note_Layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(NotesActivity.this, ViewNotesActivity.class);
                    i.putExtra("note_id", noteModel.NOTE_ID);
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return note_Model_Data.size();
        }
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getLayoutPosition();
            NoteModel noteModel = note_Model_Data.get(position);
            deleteNote(noteModel);
        }
    };


    public void deleteNote(final NoteModel noteModel) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete noteModel ? ")
                .setPositiveButton("Yes ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        noteID = noteModel.NOTE_ID;
                        categoryID = noteModel.CATEGORY_ID;

                        dataBase();
                        db.deleteNote(noteID, categoryID);
                        db.close();
                        note_Model_Data.remove(noteModel);
                        adapter.notifyDataSetChanged();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        loadNoteData();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Are you sure?");
        alertDialog.show();
    }

    public void finish(View view) {
        finish();
    }

    public void loadData(){
        // Get ID
        int category_id = getIntent().getIntExtra("category_id", 0);
        note_Model_Data = db.getNotes(category_id);
        db.close();
        adapter = new Adapter();
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(note_List);
        note_List.setAdapter(adapter);
    }

    // On Resume Action of Application
    @Override
    protected void onResume() {
        super.onResume();
        dataBase();
        loadData();
    }

    public void loadNoteData() {
        dataBase();
        loadData();
    }

    //Filter on based on NoteModel Title
    public void sortByTitle(View view) {
        dataBase();
        int category_id = getIntent().getIntExtra("category_id", 0);
        note_Model_Data = db.getSortedNotes(category_id, SQLLiteDatabaseHelper.TITLE);
        db.close();
        adapter.notifyDataSetChanged();
    }

    //Filter on based on NoteModel Time
    public void filterByTime(View view) {

        dataBase();
        int category_id = getIntent().getIntExtra("category_id", 0);
        note_Model_Data = db.getSortedNotes(category_id, SQLLiteDatabaseHelper.DATE);
        db.close();
        adapter.notifyDataSetChanged();
    }
}
