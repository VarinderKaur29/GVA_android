package com.NoteGVAandroid;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class MainActivity extends AppCompatActivity {

    private RecyclerView category_List;
    private List<CategoryModel> category_Model_Data;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViewData();

    }

    private void initViewData() {
        category_Model_Data = new ArrayList<>();
        category_List = findViewById(R.id.category_list);
        category_List.setLayoutManager(new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false));

    }


    // On Resume Action of Application
    @Override
    protected void onResume() {
        super.onResume();
        DatabaseManager db = new DatabaseManager(MainActivity.this);
        db.open();
        category_Model_Data = db.getCategories();

        adapter = new Adapter();
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(category_List);
        category_List.setAdapter(adapter);

    }

    public void add_category(View view) {
        Intent i = new Intent(MainActivity.this, AddCategoryActivity.class);
        startActivity(i);
    }




    // Adapter View Work
    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView category_Name;
        LinearLayout category_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            category_Name = itemView.findViewById(R.id.category_name);
            category_layout = itemView.findViewById(R.id.category_layout);

        }
    }

    private class Adapter extends RecyclerView.Adapter<MainActivity.ViewHolder> {
        @NonNull
        @Override
        public MainActivity.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MainActivity.ViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.category_cell, parent, false));
        }


        @Override
        public void onBindViewHolder(@NonNull MainActivity.ViewHolder holder, int position) {

            final CategoryModel categoryModel = category_Model_Data.get(position);

            holder.category_Name.setText(categoryModel.CATEGORY_NAME);

            holder.category_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(MainActivity.this, NotesActivity.class);
                    i.putExtra("category_id", categoryModel.CATEGORY_ID);
                    startActivity(i);
                }
            });

        }
        @Override
        public int getItemCount(){
            return category_Model_Data.size();
        }
    }


    public void deleteCategory(final CategoryModel categoryModel) {
        final Boolean[] value = {false};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Want to Delete or Not ?")
                .setPositiveButton("Yes ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        int cat_Id = categoryModel.CATEGORY_ID;
                        DatabaseManager db = new DatabaseManager(MainActivity.this);
                        db.open();
                        db.deleteCategory(cat_Id);
                        db.close();

                        category_Model_Data.remove(categoryModel);
                        adapter.notifyDataSetChanged();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        loadData();

                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Are you sure about the Choice ?");
        alertDialog.show();
    }


    public void loadData() {

        DatabaseManager db = new DatabaseManager(MainActivity.this);
        db.open();

        // Getting ID
        category_Model_Data = db.getCategories();
        db.close();

        adapter = new Adapter();
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(category_List);
        category_List.setAdapter(adapter);

    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getLayoutPosition();
            CategoryModel categoryModel = category_Model_Data.get(position);
            deleteCategory(categoryModel);
        }
    };
}

