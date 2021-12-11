package com.rohitdev.SyncIT;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.itextpdf.text.DocumentException;
import com.rohitdev.SyncIT.Adapters.PagerAdapter;
import com.rohitdev.SyncIT.Transitions.ZoomOutPageTransformer;

import java.io.File;
import java.io.IOException;

public class GallaryPreview extends FragmentActivity {
    private PagerAdapter mPagerAdapter;
    private File[] imagesFiles;
    private ViewPager viewPager;
    private ImageButton backBtn;
    private ImageButton ShareBtn;
    private TextView textView;
    private ImageButton DeleteButton;
    private ImageButton CheckButton;
    final int PIC_CROP = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_gallery);
        String app_folder_path = getApplicationContext().getExternalMediaDirs()[0] + "/images";
        File rootDir = new File(app_folder_path);
        imagesFiles = rootDir.listFiles();
        viewPager = (ViewPager) findViewById(R.id.photo_view_pager);
        textView = (TextView) findViewById(R.id.no_files);
        backBtn = (ImageButton) findViewById(R.id.back_button);
        ShareBtn = (ImageButton) findViewById(R.id.share_button);
        DeleteButton = (ImageButton) findViewById(R.id.delete_button);
        CheckButton = (ImageButton) findViewById(R.id.check_button);

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
            CheckButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }else{
            viewPager.setVisibility(View.GONE);
            DeleteButton.setVisibility(View.GONE);
            CheckButton.setVisibility(View.GONE);
            backBtn.setVisibility(View.GONE);
            ShareBtn.setVisibility(View.GONE);
        }
    }

    private void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties here
            cropIntent.putExtra("crop", true);
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
