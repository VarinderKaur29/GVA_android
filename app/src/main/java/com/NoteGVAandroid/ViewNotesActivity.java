package com.NoteGVAandroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ViewNotesActivity extends AppCompatActivity {

    //Variables
    private static final int SELECT_IMAGE_CODE = 101;
    private Uri cameraPhotoURI = null;
    private List<Uri> uriArrayList = new ArrayList<>();
    private Boolean audioPlaying = false;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    NoteModel noteModel;
    DatabaseManager db;
    EditText noteTitle_et, noteDetail_et, updateNoteTitle_et, UpdateNoteDetail_et;
    TextView noteDate, location, tv;
    int note_id;
    List<ImageModel> imageModels;
    LinearLayout imageLinearLayout;
    ImageView image, playImage;
    MediaPlayer mediaPlayer;
    String title, text;
    NoteModel oldNoteModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notes);
        loadNoteData();
        initView();
    }

    private void initView() {

        noteTitle_et = findViewById(R.id.title_id);
        noteDetail_et = findViewById(R.id.text_id);
        noteDate = findViewById(R.id.date);
        location = findViewById(R.id.location);
        playImage = findViewById(R.id.play_audio);
        imageLinearLayout = findViewById(R.id.viewImages);
        updateNoteTitle_et = findViewById(R.id.title_id);
        UpdateNoteDetail_et = findViewById(R.id.text_id);
    }


    public void finish(View view) {
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to update noteModel ? ")
                .setPositiveButton("Yes ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        update_note();
                        finish();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        alertDialog = builder.create();
        alertDialog.setTitle("Update");
        alertDialog.show();

    }

    public void loadNoteData() {

        EditText title_et = findViewById(R.id.title_id);

        EditText text_et = findViewById(R.id.text_id);

        note_id = getIntent().getIntExtra("note_id", 0);
        db = new DatabaseManager(ViewNotesActivity.this);
        db.open();
        noteModel = db.getSingleNote(note_id);
        TextView noteDate = findViewById(R.id.date);
        noteDate.setText("Created on : " + noteModel.DATE);
        final TextView location = findViewById(R.id.location);

        if (!noteModel.LOCATION.trim().equalsIgnoreCase("")) {

            updateLocation.run();

            location.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String url = "http://maps.google.com/maps?daddr=" + noteModel.LOCATION;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
            });
        }

        title_et.setText(noteModel.TITLE);
        text_et.setText(noteModel.DESCRIPTION);
        imageModels = db.getImages(noteModel.NOTE_ID, noteModel.CATEGORY_ID);

        LinearLayout imageLinearLayout =  findViewById(R.id.viewImages);
        for (int i = 0; i < imageModels.size(); i++) {

            if (!imageModels.get(i).IMAGE.equalsIgnoreCase("")) {
                Log.d("View ", "" + i);
                image = new ImageView(ViewNotesActivity.this);
                image.setImageURI(Uri.parse(imageModels.get(i).IMAGE));
                imageLinearLayout.addView(image);

                //for gap between two imageModels
                tv = new TextView(this);
                tv.setHeight(20);

                imageLinearLayout.addView(tv);
                uriArrayList.add(Uri.parse(imageModels.get(i).IMAGE));

            }
        }

        if (!noteModel.AUDIO.equalsIgnoreCase("")) {
            ImageView playImage = findViewById(R.id.play_audio);

            playImage.setVisibility(View.VISIBLE);
            mediaPlayer = MediaPlayer.create(this, Uri.parse(noteModel.AUDIO));

            playImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!audioPlaying) {
                        mediaPlayer.start();
                        audioPlaying = true;
                    } else {
                        audioPlaying = false;
                        mediaPlayer.stop();
                    }
                }
            });
        }
    }


    public void update_note() {

        int note_id = getIntent().getIntExtra("note_id", 0);

        db = new DatabaseManager(ViewNotesActivity.this);
        db.open();
        oldNoteModel = db.getSingleNote(note_id);
        title = updateNoteTitle_et.getText().toString().trim();
        text = UpdateNoteDetail_et.getText().toString().trim();

        String location = oldNoteModel.LOCATION;
        String date = oldNoteModel.DATE;
        int category_id = oldNoteModel.CATEGORY_ID;
        String audioPath = oldNoteModel.AUDIO;

        if (title.equalsIgnoreCase("") && text.equalsIgnoreCase("")) {
        } else {
            db.updateNote(note_id, title, date, text, audioPath, location, category_id);
            finish();
        }
        db.close();
    }

    Runnable updateLocation = new Runnable() {
        @Override
        public void run() {

            final TextView location = findViewById(R.id.location);
            String[] latLong = noteModel.LOCATION.split(",");
            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = null;

            try {
                addresses = gcd.getFromLocation(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1]), 1);

                if (addresses.size() > 0) {
                    location.setText(addresses.get(0).getAdminArea() );
                } else {
                    location.setText("Location on map");
                }
            } catch (IOException e) {

                location.setText("Location on map");

            } catch (Exception error) {

                Log.d("View Notes","Something else went wrong") ;

            }
        }
    };

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        String z = image.getAbsolutePath();
        return image;
    }


    public void add_image(View view) {
        Intent photoIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        photoIntent.setType("image/*");


        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            cameraPhotoURI = FileProvider.getUriForFile(this,
                    "com.NoteGVAandroid.fileprovider",
                    photoFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPhotoURI);
            uriArrayList.add(cameraPhotoURI);
        }

        Intent selectOption = Intent.createChooser(photoIntent, "Select ImageModel");
        selectOption.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
        startActivityForResult(selectOption, SELECT_IMAGE_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                if (cameraPhotoURI != null) {
                    image = new ImageView(this);
                    image.setImageURI(cameraPhotoURI);
                    imageLinearLayout.addView(image);
                    tv = new TextView(this);
                    tv.setHeight(20);
                    imageLinearLayout.addView(tv);
                }
            } else {

                for (int i = 0; i < uriArrayList.size(); i++) {
                    image = new ImageView(this);
                    image.setImageURI(uriArrayList.get(0));
                    imageLinearLayout.addView(image);
                    tv = new TextView(this);
                    tv.setHeight(20);
                    imageLinearLayout.addView(tv);
                }
            }

            db = new DatabaseManager(this);
            db.open();
            db.insertImage(cameraPhotoURI.toString(), noteModel.NOTE_ID, noteModel.CATEGORY_ID);
            db.close();
        }
    }
}
