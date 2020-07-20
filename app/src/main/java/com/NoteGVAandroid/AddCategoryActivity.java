package com.NoteGVAandroid;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddCategoryActivity extends AppCompatActivity {

    //Variables
    EditText category_et;
    String category_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
    }

    public void finish(View view) {
        finish();
    }

    public void add_category(View view) {
        initData();

    }
    private void initData() {
        category_et = findViewById(R.id.category_name);
        category_name = category_et.getText().toString().trim();
        if (category_name.equalsIgnoreCase("")) {
            Toast.makeText(AddCategoryActivity.this, "Enter CategoryModel", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseManager db = new DatabaseManager(AddCategoryActivity.this);
            db.open();
            db.insertCategory(category_name);
            db.close();
            Toast.makeText(AddCategoryActivity.this, "CategoryModel Added Successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
