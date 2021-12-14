package com.rohitdev.SyncIT;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rohitdev.SyncIT.MainActivity;
import com.rohitdev.SyncIT.R;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class UploadImages extends Service {

    private static final String CHANNEL_ID = "Upload Images";
    //    private List<File> Images;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = Objects.requireNonNull(storage).getReference();;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("onStartedCommand", "Started a Service ");
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent NotificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,0,NotificationIntent,0);
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentText(input)
                .setContentTitle("Uploading Images...")
                .setSmallIcon(R.drawable.ic_sync)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1,notification);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Uri file = Uri.fromFile(new File(intent.getStringExtra("File_path")));
        StorageReference riversRef = storageRef.child(Objects.requireNonNull(user).getEmail() + "/images/" + file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                // Handle unsuccessful uploads
                Log.e("UpLoad Failure", exception.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Toast.makeText(UploadImages.this,"File Uploaded Successfully with ID "+taskSnapshot.getMetadata().getName(),Toast.LENGTH_SHORT).show();
                Log.i("File Uploaded", "File Uploaded SuccessFully with uri " + taskSnapshot.getUploadSessionUri());
                stopService(intent);
                File ActualFile = new File(intent.getStringExtra("File_path"));
                ActualFile.delete();
            }
        });


        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("OnCreate", "Creates a Service ");
        storageRef = storage.getReference();
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,"Upload Images", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("OnDestroy", "Destroys a Service");
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

