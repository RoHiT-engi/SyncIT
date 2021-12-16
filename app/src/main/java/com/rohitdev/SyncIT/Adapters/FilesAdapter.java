package com.rohitdev.SyncIT.Adapters;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.rohitdev.SyncIT.GallaryPreview;
import com.rohitdev.SyncIT.MainActivity;
import com.rohitdev.SyncIT.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FilesAdapterViewHolder> {
    List<StorageReference> mImages;
    Context context;
    @NonNull
    @Override
    public FilesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context =parent.getContext();
        int layoutResID = R.layout.grid_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachToParent = false;
        View view =inflater.inflate(layoutResID,parent,attachToParent);
        return new FilesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilesAdapterViewHolder holder, int position) {
        int widthPixels = Resources.getSystem().getDisplayMetrics().widthPixels;
        int heightPixels = Resources.getSystem().getDisplayMetrics().heightPixels;

        mImages.get(position).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().setLoggingEnabled(true);
                Picasso.get().load(uri).placeholder(R.drawable.ic_image_black).centerCrop().resize(widthPixels/3,heightPixels/4).into(holder.imageView);
            }
        });
        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setMessage("Do you want to Delete this Image From Cloud Storage ?");

                alert.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mImages.get(holder.getAdapterPosition()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context,"Image Delete From Cloud Storage",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, MainActivity.class);
                                context.startActivity(intent);
                            }
                        });
                    }
                });

                alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        alert.setCancelable(true);
                    }
                });

                alert.show();
                return true;
            }
        });
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"Image Delete From Cloud Storage",Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public static class FilesAdapterViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public FilesAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.list_item_rv);

        }
    }

    public FilesAdapter(List<StorageReference> images,Context context){
        mImages= images;
        this.context = context;
    }
}
