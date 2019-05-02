package com.example.photoalbum65;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;


public class Tab2Fragment extends Fragment {
    private static final String TAG = "Tab2Fragment";
    AutoCompleteTextView locationSearch, personSearch;
    Button buttonSearch;
    RadioGroup rgl;
    RecyclerView rv;
    RecyclerView.LayoutManager glm;
    RVSPhotoAdapter rspa;
    Set<String> locationsSetList, peopleSetList;
    public static ArrayList<PhotoData> result;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_fragment, container, false);
        locationsSetList = new HashSet<>();
        buttonSearch =  view.findViewById(R.id.searchBtn);
        for (AlbumData albums : Tab1Fragment.data.albums.values()) {
            for (PhotoData p : albums.photos) {
                if (p.tags.containsKey("location")) {
                    locationsSetList.add(p.tags.get("location"));
                }
            }
        }
        peopleSetList = new HashSet<>();
        for (AlbumData albums : Tab1Fragment.data.albums.values()) {
            for (PhotoData phots : albums.photos) {
                if (phots.tags.containsKey("person")) {
                    peopleSetList.add(phots.tags.get("person"));
                }
            }
        }
        locationSearch = view.findViewById(R.id.locationText);
        personSearch = view.findViewById(R.id.personText);
        buttonSearch = view.findViewById(R.id.searchBtn);

        locationSearch.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_1, locationsSetList.toArray(new String[locationsSetList.size()])));
        personSearch.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_1, peopleSetList.toArray(new String[peopleSetList.size()])));

        rgl = view.findViewById(R.id.radioLogic);
        result = new ArrayList<>(); // place holder

        rv = view.findViewById(R.id.recycleView);
        glm = new GridLayoutManager(getContext(), 3);
        rv.setLayoutManager(glm);
        rspa = new RVSPhotoAdapter(getContext());
        rv.setAdapter(rspa);


        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result.clear();
                final String loc = locationSearch.getText().toString();
                //Predicate
                Predicate<PhotoData> loclogic = l -> {
                    if (loc.isEmpty() || !l.tags.containsKey("location"))
                        return false;
                    return l.tags.get("location").equals(loc);
                };
                final String per = personSearch.getText().toString();
                Predicate<PhotoData> perlogic = l -> {
                    if (per.isEmpty() || !l.tags.containsKey("person"))
                        return false;
                    return l.tags.get("person").equals(per);
                };
                if (loc.isEmpty() && per.isEmpty()) {
                    Toast.makeText(getContext(), "Both search bars cannot be blank", Toast.LENGTH_SHORT).show();
                    return;
                }
                Predicate<PhotoData> query;
                if (!loc.isEmpty() && !per.isEmpty()) { // set query if both on
                    if (rgl.getCheckedRadioButtonId() == R.id.radioOr) {
                        query = loclogic.or(perlogic);
                    } else if (rgl.getCheckedRadioButtonId() == R.id.radioAnd) {
                        query = loclogic.and(perlogic);
                    } else {
                        Toast.makeText(getContext(), "Please choose logical operation", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    if (!loc.isEmpty()) { // set query for one if one is empty
                        query = loclogic;
                    } else {
                        query = perlogic;
                    }
                }
                for(AlbumData a: Tab1Fragment.data.albums.values()) {
                    for(PhotoData p: a.photos) {
                        if(query.test(p)) {
                            result.add(p);
                        }
                    }
                }
                rspa.photoData = result;
                rspa.notifyDataSetChanged();


            }
        });
        return view;
    }
    public void searchPhotos(View v) {
        result.clear();
        final String loc = locationSearch.getText().toString();
        //Predicate
        Predicate<PhotoData> loclogic = l -> {
            if (loc.isEmpty() || !l.tags.containsKey("location"))
                return false;
            return l.tags.get("location").equals(loc);
        };
        final String per = personSearch.getText().toString();
        Predicate<PhotoData> perlogic = l -> {
            if (per.isEmpty() || !l.tags.containsKey("person"))
                return false;
            return l.tags.get("person").equals(per);
        };
        if (loc.isEmpty() && per.isEmpty()) {
            Toast.makeText(getContext(), "Both search bars cannot be blank", Toast.LENGTH_SHORT).show();
            return;
        }
        Predicate<PhotoData> query;
        if (!loc.isEmpty() && !per.isEmpty()) { // set query if both on
            if (rgl.getCheckedRadioButtonId() == R.id.radioOr) {
                query = loclogic.or(perlogic);
            } else if (rgl.getCheckedRadioButtonId() == R.id.radioAnd) {
                query = loclogic.and(perlogic);
            } else {
                Toast.makeText(getContext(), "Please choose logical operation", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            if (!loc.isEmpty()) { // set query for one if one is empty
                query = loclogic;
            } else {
                query = perlogic;
            }
        }
        for(AlbumData a: Tab1Fragment.data.albums.values()) {
            for(PhotoData p: a.photos) {
                if(query.test(p)) {
                    result.add(p);
                }
            }
        }
        rspa.photoData = result;
        rspa.notifyDataSetChanged();

    }
}
class RVSPhotoAdapter extends RecyclerView.Adapter<RVSPhotoAdapter.PhotoViewHolder> {
    List<PhotoData> photoData;
    Context context;
    int selectedPosition = -1;

    public RVSPhotoAdapter(Context context) {
        photoData = Tab2Fragment.result;
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
            cv = (CardView) itemView.findViewById(R.id.cv);
            photoContainer = (ImageView) itemView.findViewById(R.id.thumb_photo);
        }
    }
}