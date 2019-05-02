package com.example.photoalbum65;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;


public class AlbumData implements Serializable{
 //class
    private static final long serialVersionUID = 1L;
    public ArrayList<PhotoData> photos;
    public String name;
    public AlbumData(String n) {
        name = n;
        photos = new ArrayList<PhotoData>();
    }
    public AlbumData(String n, Collection<PhotoData> col) {
        this(n);
        for(PhotoData p : col)
        {
            photos.add(new PhotoData(p));
        }
    }

    public static class PhotoActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_pho);
        }
    }
}
