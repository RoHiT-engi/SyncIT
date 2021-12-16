package com.rohitdev.SyncIT;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.rohitdev.SyncIT.Adapters.FilesAdapter;
import com.rohitdev.SyncIT.helper.GridSpacingItemDecoration;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class File_explorer extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView textView;
    private List<StorageReference> images;
    private FilesAdapter filesAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_explorer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = (RecyclerView) findViewById(R.id.rv_file_explorer);
//        textView = (TextView) findViewById(R.id.txtv_nofiles);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(File_explorer.this, 3);
//        FilesAdapter filesAdapter ;
        if(user!=null) {
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setHasFixedSize(true);
            StorageReference listRef = FirebaseStorage.getInstance().getReference().child(user.getEmail()+"/images/");
            listRef.listAll()
                    .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                        @Override
                        public void onSuccess(ListResult listResult) {
                            images=listResult.getItems();
                            if(images.isEmpty()){
                                recyclerView.setVisibility(View.GONE);
                            }else{
//                                textView.setVisibility(View.GONE);
                                int spanCount = 3; // 3 columns
                                int spacing = 50; // 50px
                                boolean includeEdge = true;
                                recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
                                filesAdapter = new FilesAdapter(images,File_explorer.this);
                                recyclerView.setAdapter(filesAdapter);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure( Exception e) {
                            // Uh-oh, an error occurred!
                            Log.e(String.valueOf(e.getCause()),e.getMessage());
                        }
                    });


        }else{
            Toast.makeText(this,"Login To Proceed",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,SyncActivity.class);
            startActivity(intent);
        }
    }




}
