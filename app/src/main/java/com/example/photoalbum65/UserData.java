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
    public static final String storeFile = "photoAlbumData.dat";
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
    public static void writeData(UserData u, Context context) {
        ObjectOutputStream objectOut = null;
        try {
            FileOutputStream fileOut = context.openFileOutput(storeFile, Activity.MODE_PRIVATE);
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(u);
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
                    // do nowt
                }
            }
        }

    }
    public static UserData readData(Context context) {
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
        return user;
    }
}
