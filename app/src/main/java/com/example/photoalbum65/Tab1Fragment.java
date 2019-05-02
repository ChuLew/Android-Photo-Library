package com.example.photoalbum65;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


public class Tab1Fragment extends Fragment {
    public static final String storeFile = "save.dat";
    private static final String TAG = "Tab1Fragment";
    private FloatingActionButton createAlbum;
    private Button Open_Album;
    public static UserData data;
    public static String selected_album = "";
    public static Context context;
    public static RecyclerView rv;
    public static RVAlbumAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment,container,false);
        createAlbum = view.findViewById(R.id.fab);
        createAlbum.setImageResource(R.drawable.pluser);
        this.context = getActivity();
        ObjectInputStream objectIn = null;
        UserData user = null;
        try {
            FileInputStream fileIn = context.getApplicationContext().openFileInput(storeFile);
            objectIn = new ObjectInputStream(fileIn);
            user = (UserData)objectIn.readObject();
            objectIn.close();
            fileIn.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (objectIn != null) {
                try {
                    objectIn.close();
                } catch (IOException e) {
                }
            }
        }
        data = user;
        if(data == null){
            data = new UserData("Current User");
        }
        rv = (RecyclerView) view.findViewById(R.id.rv);
        rv.setBackgroundColor(Color.TRANSPARENT);
        rv.setLayoutManager(new LinearLayoutManager(context));
        adapter = new RVAlbumAdapter(context);
        rv.setAdapter(adapter);
        registerForContextMenu(rv);

        createAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                new AlertDialog.Builder(context).setTitle("Album Name?").setView(input).setMessage("Album Name:").setPositiveButton("Create", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(input.getText().toString().isEmpty()){
                                    Toast.makeText(getActivity().getBaseContext(),"Album name cannot be empty", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if(data.albums.containsKey(input.getText().toString())){
                                    Toast.makeText(getActivity().getBaseContext(),"Existing Album Name, try again!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                data.albums.put(input.getText().toString(), new AlbumData(input.getText().toString()));
                                adapter.albumData = new ArrayList<>(data.albums.values());
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .create()
                        .show();
                return;
            }
        });

        return view;
    }
    @Override
    public void onStop(){
        super.onStop();
        ObjectOutputStream objectOut = null;
        try {
            FileOutputStream fileOut = context.openFileOutput(storeFile, Activity.MODE_PRIVATE);
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(data);
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
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        if(data != null){
            Tab1Fragment.adapter.albumData = new ArrayList<>(data.albums.values());
            Tab1Fragment.adapter.notifyDataSetChanged();
        }
    }
}
class RVAlbumAdapter extends RecyclerView.Adapter<RVAlbumAdapter.AlbumViewHolder> {
    List<AlbumData> albumData;
    Context context;
    int selectedPosition = -1;
    public RVAlbumAdapter(Context context) {
        albumData = new ArrayList<>(MainActivity.data.albums.values());
        this.context = context;
    }
    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        CardView cardView ;
        TextView albumName;
        TextView numPhotos;
        ImageView firstPhoto;

        AlbumViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cv);
            albumName = (TextView) itemView.findViewById(R.id.album_name);
            numPhotos = (TextView) itemView.findViewById(R.id.num_photos);
            firstPhoto = (ImageView) itemView.findViewById(R.id.first_photo);
        }
    }
    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.album_card_layout, viewGroup, false);
        AlbumViewHolder albumViewHolder = new AlbumViewHolder(v);
        return albumViewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull final AlbumViewHolder albumViewHolder, final int i) {

        albumViewHolder.albumName.setText(albumData.get(i).name);
        albumViewHolder.numPhotos.setText(albumData.get(i).photos.size() + "");
        if (albumData.get(i).photos.size() <= 0) {
            albumViewHolder.firstPhoto.setImageResource(R.mipmap.emptyimage); // default android logo
        } else {
            Uri uri = Uri.parse((albumData.get(i).photos.get(0).location));
            int width = (int) (context.getResources().getDisplayMetrics().widthPixels / 4);
            Glide.with(context).load(uri).apply(new RequestOptions().centerCrop().override(width, width)).into(albumViewHolder.firstPhoto);
        }
        if (selectedPosition == i) {
            albumViewHolder.cardView.setBackgroundColor(Color.parseColor("#ADD8E6"));
        } else {
            albumViewHolder.cardView.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        albumViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //selectedPosition = i;
                //Tab1Fragment.selected_album = albumData.get(i).name;
                //notifyDataSetChanged();
                Intent intent = new Intent(context, AlbumActivity.class);
                intent.putExtra("album", albumData.get(i).name);
                context.startActivity(intent);
            }
        });
        albumViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectedPosition = i;
                Tab1Fragment.selected_album = albumData.get(i).name;
                notifyDataSetChanged();

                PopupMenu popup = new PopupMenu(context,v);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.poupup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.open:
                                Toast.makeText(context,"Opened Album " + albumData.get(i).name, Toast.LENGTH_SHORT).show();
                                selectedPosition = i;
                                Tab1Fragment.selected_album = albumData.get(i).name;
                                notifyDataSetChanged();
                                Intent intent = new Intent(context, AlbumActivity.class);
                                intent.putExtra("album", albumData.get(i).name);
                                context.startActivity(intent);
                                return true;
                            case R.id.rename:
                                final EditText input = new EditText(context);
                                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                                new AlertDialog.Builder(context)
                                        .setTitle("Album New Name?")
                                        .setView(input)
                                        .setMessage("Name:")
                                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if(input.getText().toString().isEmpty()){
                                                    Toast.makeText(context,"Empty Album Name not allowed", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                String name = input.getText().toString();
                                                if(Tab1Fragment.data.albums.containsKey(name)){
                                                    Toast.makeText(context,"Existing Album Name present, Try Again!", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                Tab1Fragment.data.renameAlbum(Tab1Fragment.selected_album, name);
                                                Tab1Fragment.selected_album = name;
                                                Tab1Fragment.adapter.albumData = new ArrayList<>(Tab1Fragment.data.albums.values());
                                                Tab1Fragment.adapter.notifyDataSetChanged();
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
                            case R.id.close:
                                Toast.makeText(context,"You have deleted " +  albumData.get(i).name, Toast.LENGTH_SHORT).show();
                                if(Tab1Fragment.selected_album.isEmpty()){
                                    Toast.makeText(context,"No Selected Album", Toast.LENGTH_SHORT).show();
                                    return false;
                                }
                                Tab1Fragment.data.albums.remove(Tab1Fragment.selected_album);
                                Tab1Fragment.adapter.albumData = new ArrayList<>(Tab1Fragment.data.albums.values());
                                Tab1Fragment.adapter.notifyDataSetChanged();
                                Tab1Fragment.adapter.selectedPosition = -1;
                                Tab1Fragment.selected_album = "";
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
        return albumData.size();
    }

}
