package com.example.photoalbum65;

import android.app.Activity;
import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class UserData implements Serializable {
    private static final long serialVersionUID = 1L;
    public String userName;
    public HashMap<String, AlbumData> albums;
    public UserData(String u) {
        userName = u;
        albums = new HashMap<String, AlbumData>();
    }
    public void movePhoto(String a, String b, PhotoData p) {
        albums.get(a).photos.remove(p);
        albums.get(b).photos.add(p);
    }
    public void renameAlbum(String a, String newName) {
        if(!albums.containsKey(a)) {
            return;
        }
        AlbumData tmp = albums.remove(a);
        tmp.name = newName;
        albums.put(newName, tmp);
    }

}
