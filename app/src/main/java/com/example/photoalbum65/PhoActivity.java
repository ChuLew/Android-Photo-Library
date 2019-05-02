package com.example.photoalbum65;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class PhoActivity extends AppCompatActivity {
    public ViewPager viewPager;
    private SlideAdapter slideAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pho);
        viewPager =  findViewById(R.id.view_pager);
        slideAdapter = new SlideAdapter(this);
        viewPager.setAdapter(slideAdapter);
        int index = getIntent().getIntExtra("index",0);
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
                        break;
                    case R.id.action_move:
                        break;
                }
                return true;
            }
        });
    }
    //i was thinking on copy Photo i can display album and onclick of selected album we can copy album to that album
    // i was thinking same for movePhoto

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