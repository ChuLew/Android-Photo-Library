package com.example.photoalbum65;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.photoalbum65.ui.main.PhotoActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends AppCompatActivity {
    final int CAMERA_IMAGE = 1231;
    public static AlbumData albumData;
    public String album_name;
    private RVPhotoAdapter recyclerViewAdapter;
    private RecyclerView recView;
    private FloatingActionButton addPhoto;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        addPhoto = (FloatingActionButton)findViewById(R.id.fab);
        recView = findViewById(R.id.recyclerView);
        album_name = getIntent().getStringExtra("album");
        albumData = MainActivity.data.albums.get(album_name);
        layoutManager = new GridLayoutManager(getApplicationContext(),3);
        recView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new RVPhotoAdapter(this);
        recView.setAdapter(recyclerViewAdapter);

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add open camera to take photos as well
                //****************************************************************************************************************************************
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                startActivityForResult(intent, CAMERA_IMAGE);
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        if(intent == null) return;
        switch (requestCode) {
            case CAMERA_IMAGE:
                if(intent.getData() == null){
                    return;
                }
                Uri u = intent.getData().normalizeScheme();
                this.getApplicationContext().getContentResolver().takePersistableUriPermission(u, intent.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        + Intent.FLAG_GRANT_WRITE_URI_PERMISSION));
                Tab1Fragment.data.albums.get(album_name).photos.add(new PhotoData(u.toString()));
                recyclerViewAdapter.photoData = Tab1Fragment.data.albums.get(album_name).photos;
                recyclerViewAdapter.notifyDataSetChanged();
                break;
            default:
                return;
        }
    }
    @Override
    protected void onStop(){
        super.onStop();
        ObjectOutputStream objectOut = null;
        try {
            FileOutputStream fileOut = Tab1Fragment.context.openFileOutput(Tab1Fragment.storeFile, Activity.MODE_PRIVATE);
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(Tab1Fragment.data);
            fileOut.getFD().sync();
            fileOut.close();
            objectOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (objectOut != null) {
                try {
                    objectOut.close();
                } catch (IOException e) {

                }
            }
        } // write to persistent storage
    }
}
class RVPhotoAdapter extends RecyclerView.Adapter<RVPhotoAdapter.PhotoViewHolder> {
    List<PhotoData> photoData;
    Context context;
    int selectedPosition = -1;

    public RVPhotoAdapter(Context context) {
        photoData = AlbumActivity.albumData.photos;
        this.context = context;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.photo_card_layout, viewGroup, false);
        PhotoViewHolder pvh = new PhotoViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder albumViewHolder, final int i) {
        Uri photopath = Uri.parse(photoData.get(i).location);
        int width = (int)(context.getResources().getDisplayMetrics().widthPixels/3.05);
        Glide.with(context).load(photopath).apply(new RequestOptions().centerCrop().override(width,width)).into(albumViewHolder.photoContainer);
        if(selectedPosition == i){
            albumViewHolder.cv.setBackgroundColor(Color.parseColor("#ADD8E6"));
        }else{
            albumViewHolder.cv.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        albumViewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                selectedPosition = i;
                notifyDataSetChanged();
                Intent intent = new Intent(context, PhotoActivity.class);
                intent.putExtra("index",i);
                context.startActivity(intent);

            }
        });
        albumViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                selectedPosition = i;
                notifyDataSetChanged();
                PopupMenu popup = new PopupMenu(context, v);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.photopop, popup.getMenu());
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.displayPhoto:
                                Toast.makeText(context,"display photo", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.deletePhoto:
                                Toast.makeText(context,"deleted photo", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.movephoto:
                                Toast.makeText(context,"moved photo", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.copyphoto:
                                Toast.makeText(context,"copied photo", Toast.LENGTH_SHORT).show();
                                return true;
                            default:
                                return false;

                        }
                    }
                });
                        popup.show();
                        return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return photoData.size();
    }


    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        ImageView photoContainer;
        PhotoViewHolder(View itemView) {
            super(itemView);
            cv =  itemView.findViewById(R.id.cv);
            photoContainer =  itemView.findViewById(R.id.thumb_photo);

        }
    }
}
