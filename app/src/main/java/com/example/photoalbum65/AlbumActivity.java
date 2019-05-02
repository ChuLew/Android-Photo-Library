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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

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
        addPhoto.setImageResource(R.drawable.pluser);
        recView = findViewById(R.id.recyclerView);
        album_name = getIntent().getStringExtra("album");
        albumData = Tab1Fragment.data.albums.get(album_name);
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
    @Override
    protected void onStart(){
        super.onStart();
        recyclerViewAdapter.notifyDataSetChanged();
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
                Intent intent = new Intent(context, PhoActivity.class);
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
                popup.getMenuInflater().inflate(R.menu.photopop, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.displayPhoto:
                                Toast.makeText(context, "display photo", Toast.LENGTH_SHORT).show();
                                int selected = selectedPosition;
                                Intent intent = new Intent(context, PhoActivity.class);
                                intent.putExtra("index", selected);
                                context.startActivity(intent);
                                return true;
                            case R.id.deletePhoto:
                                Toast.makeText(context, "deleted photo", Toast.LENGTH_SHORT).show();
                                selected = selectedPosition;
                                AlbumActivity.albumData.photos.remove(selected);
                                photoData = AlbumActivity.albumData.photos;
                                notifyDataSetChanged();
                                selectedPosition = -1;
                                return true;
                            case R.id.movephoto:
                                selected = selectedPosition;
                                if (Tab1Fragment.data.albums.size() <= 1) {
                                    Toast.makeText(context, "Only 1 Album, Cant move Photo to itself!", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                                List<String> lister = new ArrayList<String>();
                                for (AlbumData a : Tab1Fragment.data.albums.values()) {
                                    if (!a.name.equals(AlbumActivity.albumData.name)) {
                                        lister.add(a.name);
                                    }
                                }
                                final ArrayAdapter<String> adp = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, lister);
                                final Spinner sp = new Spinner(context);
                                sp.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                sp.setAdapter(adp);

                                new AlertDialog.Builder(context).setTitle("Move to which album?").setView(sp).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                String album = sp.getSelectedItem().toString();
                                                PhotoData p = AlbumActivity.albumData.photos.get(selectedPosition);
                                                Tab1Fragment.data.albums.get(album).photos.add(p);
                                                int selected = selectedPosition;
                                                AlbumActivity.albumData.photos.remove(selected);
                                                photoData = AlbumActivity.albumData.photos;
                                                notifyDataSetChanged();
                                                selectedPosition = -1;
                                                Toast.makeText(context, "Moved photo to album: " + album, Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                            }
                                        })
                                        .create()
                                        .show();
                                return true;
                            case R.id.copyphoto:
                                if(Tab1Fragment.data.albums.size() <= 1){
                                    Toast.makeText(context, "Only one album available", Toast.LENGTH_SHORT).show();
                                    return true;
                                }
                                lister = new ArrayList<String>();
                                for(AlbumData a: Tab1Fragment.data.albums.values()){
                                    if(!a.name.equals(AlbumActivity.albumData.name)){
                                        lister.add(a.name);
                                    }
                                }
                                final ArrayAdapter<String> adp2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, lister);
                                final Spinner spin = new Spinner(context);
                                spin.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                                spin.setAdapter(adp2);

                                new AlertDialog.Builder(context)
                                        .setTitle("Album Name?")
                                        .setMessage("Name:")
                                        .setView(spin)
                                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                String album = spin.getSelectedItem().toString();
                                                PhotoData p = AlbumActivity.albumData.photos.get(selectedPosition);
                                                Tab1Fragment.data.albums.get(album).photos.add(p);
                                                Toast.makeText(context, "Copied photo to album: " + album, Toast.LENGTH_SHORT).show();

                                            }
                                        })
                                        .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                            }
                                        })
                                        .create()
                                        .show();
                                return true;
                            default:
                                return false;

                        }
                        return true;
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
