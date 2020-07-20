package com.NoteGVAandroid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class AddNoteActivity extends AppCompatActivity {

    //Variables

    String userLocation = "";
    Boolean isRecording = false;
    private static final int SELECT_IMAGE_CODE = 101;
    MediaRecorder mediaRecorder;
    private Uri cameraPhotoURI = null;
    private ArrayList<Uri> uriArrayList = new ArrayList<>();
    private Uri audioDataUri = null;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 10 * 1000;
    private long FASTEST_INTERVAL = 2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        initViewData();
    }

    private void initViewData() {
        mediaRecorder = new MediaRecorder();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PackageManager.PERMISSION_GRANTED);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        startLocationUpdates();
        getCurrentLocation();
    }


    public void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }


    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {

                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    //Get Coordinates
    public void onLocationChanged(Location location) {
        userLocation = location.getLatitude() + "," + location.getLongitude();
    }

    public void finish(View view) {
        finish();
    }

    // Select ImageModel

    public void add_image(View view) {
        Intent photoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoIntent.setType("image/*");
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {

        }
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

            Log.d("Add noteModel", "123");

            if (data != null) {
                Log.d("Add noteModel", "126" + data.toString());

                //cameraPhotoURI = data.getData();
                //uriArrayList.add(cameraPhotoURI);

                if (cameraPhotoURI != null) {

                    Log.d("Add noteModel", "131");

                    LinearLayout linearLayout1 = findViewById(R.id.add_view_images);

                    ImageView image = new ImageView(AddNoteActivity.this);

                    image.setImageURI(cameraPhotoURI);
                    linearLayout1.addView(image);

                    TextView tv = new TextView(this);
                    tv.setHeight(20);
                    linearLayout1.addView(tv);

                }
            } else {


                System.out.println("camera photo uri : " + cameraPhotoURI);

                LinearLayout linearLayout1 = findViewById(R.id.add_view_images);

                for (int i = 0; i < uriArrayList.size(); i++) {
                    ImageView image = new ImageView(AddNoteActivity.this);
                    image.setImageURI(uriArrayList.get(0));
                    linearLayout1.addView(image);

                    TextView tv = new TextView(this);
                    tv.setHeight(20);
                    linearLayout1.addView(tv);

                }


            }

        }
    }

    public void add_note(View view) {

        Random random = new Random();
        int id = random.nextInt(9999999);

        EditText title_et = findViewById(R.id.title_id);
        EditText text_et = findViewById(R.id.text_id);
        String title = title_et.getText().toString().trim();
        String text = text_et.getText().toString().trim();
        String date = new SimpleDateFormat("dd-MM-yy HH:mm:ss").format(new Date());
        int category_id = getIntent().getIntExtra("category_id", 0);
        String audioPath = "";

        if (audioDataUri != null) {
            audioPath = audioDataUri.toString();
        }

        Log.d("Add NoteModel", audioPath);
        DatabaseManager db = new DatabaseManager(AddNoteActivity.this);
        db.open();


        if (title.equalsIgnoreCase("") && text.equalsIgnoreCase("")) {
            Toast.makeText(AddNoteActivity.this, "Text is Necessary !", Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < uriArrayList.size(); i++) {

                if (uriArrayList.get(i) != null) {
                    db.insertImage(uriArrayList.get(i).toString(), id, category_id);
                }
            }

            if (!userLocation.equals("")) {

                getCurrentLocation();

            }

            db.insertNote(id, title, date, text, audioPath, userLocation, category_id);
            Toast.makeText(AddNoteActivity.this, "NoteModel added Successfully", Toast.LENGTH_SHORT).show();
        }

        db.close();
        finish();
    }

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
        return image;
    }


    public void record_audio_Data(View view) throws IOException {
        if (!isRecording) {
            view.setBackgroundColor(Color.GREEN);
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            File audioFile = createAudioFile();

            if (audioFile != null) {
                audioDataUri = FileProvider.getUriForFile(this,
                        "com.NoteGVAandroid.fileprovider",
                        audioFile);

                if (Build.VERSION.SDK_INT >= 26) {
                    mediaRecorder.setOutputFile(audioFile);
                } else {
                    mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
                }

                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                mediaRecorder.prepare();
                mediaRecorder.start();
                isRecording = true;

            }
        } else {

            view.setBackgroundColor(Color.RED);

            mediaRecorder.stop();

            runOnUiThread(() ->{
                TextView tv = findViewById(R.id.audio_text);
                tv.setCursorVisible(true);
                tv.setText("Recording is Stopped and you can hear after the note is saved ! ");

            });

            isRecording = false;
        }
    }

    private File createAudioFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "3gp_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".3gp",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }
}
