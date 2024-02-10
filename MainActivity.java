package com.example.digitalneering_image_capture_part_3;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private static int CAMERA_PERMISSION_CODE = 100;
    private static int STORAGE_PERMISSION_CODE = 101;

    String currentPhotoPath;

    ImageView imageView;
    Button imagecapturing,showimage;

    TextView textview;

    ActivityResultLauncher<Intent> activityResultLauncher;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        imagecapturing = findViewById(R.id.imagecapture);
        showimage = findViewById(R.id.showimage);
        textview = findViewById(R.id.textView);


        if (iscamerapresent()) {
            Log.i("Camera phone", "Camera detected");
            camerapermission();
        } else {
            Log.i("Camera phone", "Camera not detected");
        }

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Log.i("result","good result");



                        } else {

                        }

                    }
                });
        imagecapturing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Image capture clicked",Toast.LENGTH_SHORT).show();
                String fileName = "photo";
                //File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File storageDirectory = new File(Environment.getExternalStorageDirectory(),"/DCIM/digitalneering");
                try {
                    File imageFile = File.createTempFile(fileName,".jpg",storageDirectory);
                    currentPhotoPath = imageFile.getAbsolutePath();
                    textview.setText(currentPhotoPath);
                  Uri imageUri =  FileProvider.getUriForFile(MainActivity.this,
                            "com.example.digitalneering_image_capture_part_3",imageFile);

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        activityResultLauncher.launch(intent);


                    } else {
                        Toast.makeText(MainActivity.this, "there is no support this action",
                                Toast.LENGTH_SHORT).show();

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    showimage.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            imageView.setImageBitmap(bitmap); //show previev image
        }
    });
    }

    private boolean iscamerapresent() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private void camerapermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        }
    }

    private void storagepermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        }
        Log.i("permission","success");
    }

}