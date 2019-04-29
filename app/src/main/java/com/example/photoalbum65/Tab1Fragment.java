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
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


public class Tab1Fragment extends Fragment {
    private static final String TAG = "Tab1Fragment";
    private Button createAlbum;
    public static UserData data;
    public static String selected_album = "";
    public static Context context;
    public static RecyclerView rv;
    public static RVAlbumAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment,container,false);
        createAlbum = (Button) view.findViewById(R.id.createAlb);
        context = getContext();
        data = UserData.readData(context);
        if(data == null){
            data = new UserData("Current User");
        }
        rv = (RecyclerView) view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(context));
        adapter = new RVAlbumAdapter(context);
        rv.setAdapter(adapter);

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
        UserData.writeData(data, context); // write to persistent storage
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
