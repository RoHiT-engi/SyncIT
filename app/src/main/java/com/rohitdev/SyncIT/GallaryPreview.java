package com.rohitdev.SyncIT;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.rohitdev.SyncIT.Adapters.FilesAdapter;
import com.rohitdev.SyncIT.Adapters.PagerAdapter;
import com.rohitdev.SyncIT.Transitions.ZoomOutPageTransformer;
import com.rohitdev.SyncIT.helper.CropnSave;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public class GallaryPreview extends FragmentActivity {
    private PagerAdapter mPagerAdapter;
    private File[] imagesFiles;
    private ViewPager viewPager;
    private ImageButton backBtn;
    private ImageButton ShareBtn;
    private TextView textView;
    private ImageButton DeleteButton;
    private ImageButton CheckButton,Setting_btn;
    final int PIC_CROP = 1;
    private ImageButton CropBtn;
    private List<StorageReference> images;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_gallery);
        String app_folder_path = getApplicationContext().getExternalMediaDirs()[0] + "/images";
        File rootDir = new File(app_folder_path);
        imagesFiles = rootDir.listFiles();
        viewPager = (ViewPager) findViewById(R.id.photo_view_pager);
        Setting_btn = (ImageButton) findViewById(R.id.setting_btn);
        textView = (TextView) findViewById(R.id.no_files);
        backBtn = (ImageButton) findViewById(R.id.back_button);
        ShareBtn = (ImageButton) findViewById(R.id.share_button);
        DeleteButton = (ImageButton) findViewById(R.id.delete_button);
        CheckButton = (ImageButton) findViewById(R.id.check_button);
        CropBtn = (ImageButton) findViewById(R.id.crop_button);


        if(imagesFiles!=null && imagesFiles.length!=0 ){
            textView.setVisibility(View.GONE);
            mPagerAdapter = new PagerAdapter(this,imagesFiles);
            viewPager.setAdapter(mPagerAdapter);
            viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(GallaryPreview.this, MainActivity.class);
                    startActivity(intent);
                }
            });
            ShareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imagesFiles[viewPager.getCurrentItem()].getAbsolutePath()));
                    sendIntent.setType("image/jpeg");
                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    startActivity(shareIntent);
                }
            });
            Setting_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopup(view);
                }
            });
            DeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    File fdelete = new File(imagesFiles[viewPager.getCurrentItem()].getAbsolutePath());
                    if (fdelete.exists()) {
                        boolean delete = fdelete.delete();
                        if(delete){
                            Toast.makeText(GallaryPreview.this,"Image Deleted Successfully",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(GallaryPreview.this,MainActivity.class);
                            startActivity(intent);
                        }
                    }
                }
            });
            CropBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    File fCrop = new File(imagesFiles[viewPager.getCurrentItem()].getAbsolutePath());
                    Intent intent = new Intent(GallaryPreview.this, CropnSave.class);
                    intent.putExtra("CropUri",fCrop.getAbsolutePath());
                    startActivity(intent);
                }
            });
            CheckButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user!=null){
                        for(File i:imagesFiles){
                            Intent intent = new Intent(GallaryPreview.this,UploadImages.class);
                            intent.putExtra("File_path",i.getAbsolutePath());
                            GallaryPreview.this.startService(intent);
                        }
                        Intent intent = new Intent(GallaryPreview.this,MainActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(GallaryPreview.this,"Login To Proceed Further",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(GallaryPreview.this,SyncActivity.class);
                        startActivity(intent);
                    }
                }
            });

        }else{
            Setting_btn.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
            DeleteButton.setVisibility(View.GONE);
            CheckButton.setVisibility(View.GONE);
            backBtn.setVisibility(View.GONE);
            CropBtn.setVisibility(View.GONE);
            ShareBtn.setVisibility(View.GONE);
        }
    }
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.settings_gallery, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (item.getItemId() == R.id.logout) {
                    if(user!=null) {
                        AuthUI.getInstance()
                                .signOut(GallaryPreview.this)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(GallaryPreview.this, "Signed Out SucceedFully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        return true;
                    } else {
                        item.setTitle("Sign In");
                        Intent intent = new Intent(GallaryPreview.this,SyncActivity.class);
                        startActivity(intent);
                    }
                } else if(item.getItemId()==R.id.File_Explorer){
                    Intent intent = new Intent(GallaryPreview.this,File_explorer.class);
                    GallaryPreview.this.startActivity(intent);
                }
                return false;
            }
        });
        popup.show();
    }

}
