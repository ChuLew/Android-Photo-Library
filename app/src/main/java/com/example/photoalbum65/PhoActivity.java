package com.example.photoalbum65;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class PhoActivity extends AppCompatActivity {
    public ViewPager viewPager;
    private SlideAdapter slideAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pho);
        List<PhotoData> photoData = AlbumActivity.albumData.photos;
        viewPager =  findViewById(R.id.view_pager);
        slideAdapter = new SlideAdapter(this);
        viewPager.setAdapter(slideAdapter);
        final int index = getIntent().getIntExtra("index",0);
        viewPager.setCurrentItem(index);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        Intent intent = getIntent();
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.action_add:
                        Toast toast = Toast.makeText(PhoActivity.this,
                                "Add/Edit/Remove Tags", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                        Intent intent = new Intent(PhoActivity.this, AddTag.class);
                        intent.putExtra("index",viewPager.getCurrentItem());
                        startActivity(intent);
                        break;
                    case R.id.action_copy:
                        List<String> lister = new ArrayList<String>();
                        if(Tab1Fragment.data.albums.size() <= 1){
                            Toast.makeText(getBaseContext(), "Only one album available", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        lister = new ArrayList<String>();
                        for(AlbumData a: Tab1Fragment.data.albums.values()){
                            if(!a.name.equals(AlbumActivity.albumData.name)){
                                lister.add(a.name);
                            }
                        }
                        final ArrayAdapter<String> adp2 = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, lister);
                        final Spinner spin = new Spinner(getBaseContext());
                        spin.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                        spin.setAdapter(adp2);

                        new AlertDialog.Builder(PhoActivity.this).setTitle("Album You'd like to copy to").setView(spin).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String album = spin.getSelectedItem().toString();
                                        PhotoData p = AlbumActivity.albumData.photos.get(index);
                                        Tab1Fragment.data.albums.get(album).photos.add(p);
                                        Toast.makeText(getBaseContext(), "Copied photo to album: " + album, Toast.LENGTH_SHORT).show();

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
                    case R.id.action_move:
                        int selected = index;
                        if (Tab1Fragment.data.albums.size() <= 1) {
                            Toast.makeText(getBaseContext(), "Only 1 Album, Cant move Photo to itself!", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        lister = new ArrayList<String>();
                        for (AlbumData a : Tab1Fragment.data.albums.values()) {
                            if (!a.name.equals(AlbumActivity.albumData.name)) {
                                lister.add(a.name);
                            }
                        }
                        final ArrayAdapter<String> adp = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, lister);
                        final Spinner sp = new Spinner(getBaseContext());
                        sp.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        sp.setAdapter(adp);

                        new AlertDialog.Builder(PhoActivity.this).setTitle("Move to which album?").setView(sp).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String album = sp.getSelectedItem().toString();
                                PhotoData p = AlbumActivity.albumData.photos.get(index);
                                Tab1Fragment.data.albums.get(album).photos.add(p);
                                int selected = index;
                                AlbumActivity.albumData.photos.remove(selected);
                                onBackPressed();

                                Toast.makeText(PhoActivity.this, "Moved photo to album: " + album, Toast.LENGTH_SHORT).show();

                            }
                        })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                                .create()
                                .show();
                        break;
                }
                return true;
            }
        });
    }

}
class SlideAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;
    ArrayList<PhotoData> p;

    public SlideAdapter(Context context) {
        this.context = context;
        p = AlbumActivity.albumData.photos;
    }

    @Override
    public int getCount() {
        return AlbumActivity.albumData.photos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==(LinearLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.slide,container,false);
        LinearLayout layoutslide = (LinearLayout) view.findViewById(R.id.slide_linear_layout);
        ImageView imgslide = (ImageView)  view.findViewById(R.id.image_slider);
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = container.getResources().getDisplayMetrics().heightPixels;
        Glide.with(context).load(Uri.parse(p.get(position).location)).apply(new RequestOptions().override(height,width)).into(imgslide);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
    }
}