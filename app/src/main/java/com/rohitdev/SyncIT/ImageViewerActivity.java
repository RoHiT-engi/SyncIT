package com.rohitdev.SyncIT;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ImageViewerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar()!=null)
        {this.getSupportActionBar().hide();}
        setContentView(R.layout.image_view_layout);
        Intent intent = getIntent();
        ImageButton DeleteBtn = (ImageButton) findViewById(R.id.download);
        ImageButton ShareBtn = (ImageButton) findViewById(R.id.share);
        ImageButton BackBtn = (ImageButton) findViewById(R.id.back_button_viewer);
        BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(ImageViewerActivity.this,File_explorer.class);
                startActivity(intent1);
            }
        });
        String app_folder_path = getApplicationContext().getExternalMediaDirs()[0] + "/downloads" ;
        String File_name = intent.getStringExtra("Filename");


        ShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse(app_folder_path+"/"+File_name));
                Intent share =Intent.createChooser(shareIntent,null);
                startActivity(share);
            }
        });

        DeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String imageuri = intent.getStringExtra("downloadPath");
                String File_name = intent.getStringExtra("Filename");
                StorageReference Imageref = FirebaseStorage.getInstance().getReference().child(imageuri);
                String app_folder_path = getApplicationContext().getExternalMediaDirs()[0] + "/downloads" ;
                File dir = new File(app_folder_path);
                if (!dir.exists() && !dir.mkdirs()) {
                    Toast.makeText(ImageViewerActivity.this, "Trip", Toast.LENGTH_SHORT).show();
                }
                File imageFile = new File(app_folder_path,File_name);


                Imageref.getFile(imageFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(ImageViewerActivity.this,"File DownLoaded SuccessFully",Toast.LENGTH_SHORT).show();
//                        TODO:add intent to open the image in Gallery
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception exception) {
                        Toast.makeText(ImageViewerActivity.this,exception.getMessage(),Toast.LENGTH_SHORT).show();
                        // Handle any errors
                    }
                });
            }
        });
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        String uri = intent.getStringExtra("ViewUri");
        Picasso.get().setLoggingEnabled(true);
        Picasso.get().load(uri).placeholder(R.drawable.ic_image_black).into(imageView);
    }


}
