package com.rohitdev.SyncIT.helper;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.rohitdev.SyncIT.MainActivity;
import com.rohitdev.SyncIT.R;
import com.takusemba.cropme.CropLayout;
import com.takusemba.cropme.OnCropListener;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CropnSave extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop_image);
        if(getSupportActionBar()!=null)
        {this.getSupportActionBar().hide();}
        CropLayout cropLayout = (CropLayout) findViewById(R.id.Crop_preview);
        ImageButton btn = (ImageButton) findViewById(R.id.make_crop);
        Intent intent = getIntent();
        String uri = intent.getStringExtra("CropUri");
        cropLayout.setUri(Uri.parse(uri));
        cropLayout.addOnCropListener(new OnCropListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(@NonNull Bitmap bitmap) {
                File file = new File(uri);
                if (file.exists())
                    file.delete();
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                    Toast.makeText(CropnSave.this,"Image Cropped Successfully",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CropnSave.this, MainActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(CropnSave.this,"Error Cropping Image ",Toast.LENGTH_SHORT).show();
                    Log.e("CropnSave.class","Error : - "+e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CropnSave.this,"Error Cropping Image",Toast.LENGTH_SHORT).show();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropLayout.isOffFrame();
                cropLayout.crop();
            }
        });

    }
}
