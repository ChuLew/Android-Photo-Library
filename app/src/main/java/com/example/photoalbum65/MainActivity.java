package com.example.photoalbum65;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    public static UserData data;
    public static String selected_album = "";
    public static Context context;
    RVAlbumAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting.");
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        this.context = this;
        data = UserData.readData(this);
        if(data == null){
            data = new UserData("Current User");
        }
    }
//    @Override
//    protected void onStop(){
//        super.onStop();
//        Log.d("Stopped","" + MainActivity.data.albums.toString());
//        UserData.writeData(data, this); // write to persistent storage
//    }
//    @Override
//    protected void onResume(){
//        super.onResume();
//        if(data != null){
//            Tab1Fragment.adapter.albumData = new ArrayList<>(data.albums.values());
//            Tab1Fragment.adapter.notifyDataSetChanged();
//        }
//    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapters = new SectionsPageAdapter(getSupportFragmentManager());
        adapters.addFragment(new Tab1Fragment(), "Albums");
        adapters.addFragment(new Tab2Fragment(), "Search");
        adapters.addFragment(new Tab3Fragment(), "How to");
        viewPager.setAdapter(adapters);
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
            albumViewHolder.firstPhoto.setImageResource(R.drawable.ic_launcher_background); // default android logo
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
                Log.d("tag", i + "");
                selectedPosition = i;
                MainActivity.selected_album = albumData.get(i).name;
                notifyDataSetChanged();
            }
        });
        albumViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectedPosition = i;
                MainActivity.selected_album = albumData.get(i).name;
                notifyDataSetChanged();
                Intent intent = new Intent(context, AlbumActivity.class);
                intent.putExtra("album", albumData.get(i).name);
                context.startActivity(intent);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumData.size();
    }

}