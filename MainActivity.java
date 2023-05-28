package com.example.digitalneering_image_capture_part_2;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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

    ImageView imageView;
    Button imagecapturing,saveimage;
    private Uri videopath;
    TextView textview;
    //OutputStream outputstream;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        imagecapturing = findViewById(R.id.imagecapture);
        textview = findViewById(R.id.textView);
        saveimage = findViewById(R.id.button);
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

                            Bundle bundle = result.getData().getExtras();
                            Bitmap bitmap = (Bitmap) bundle.get("data");


                            imageView.setImageBitmap(bitmap);



                        }

                    }
                });
        imagecapturing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Image capture clicked",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    activityResultLauncher.launch(intent);
                } else {
                    Toast.makeText(MainActivity.this, "there is no support this action",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });

        saveimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storagepermission();

                saveimage();
                Log.i("cek","ok");
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
    private void saveimage() {
        File dir = new File(Environment.getExternalStorageDirectory(),"/DCIM/digitalneering");
        Log.i("lokasi",dir.getAbsolutePath());
        if (!dir.exists()) {
            dir.mkdir();
        }

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();



        FileOutputStream outputstream = null;

        File file = new File(dir,"IMG" + System.currentTimeMillis() + ".png");
        try {

            outputstream = new FileOutputStream(file);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        bitmap.compress(Bitmap.CompressFormat.PNG,100,outputstream);
        Toast.makeText(this, "Image Successfuly saved", Toast.LENGTH_SHORT).show();

        try {
            outputstream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputstream.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
        textview.setText(dir.getAbsolutePath());
    }

}