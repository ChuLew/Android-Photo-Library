package com.example.photoalbum65;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;


public class AlbumData implements Serializable{

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

}
