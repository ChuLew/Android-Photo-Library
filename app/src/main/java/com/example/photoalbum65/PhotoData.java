package com.example.photoalbum65;


import java.io.Serializable;
import java.util.HashMap;


public class PhotoData implements Serializable{
    private static final long serialVersionUID = 1L;
    public String location;
    public String caption;
    public HashMap<String, String> tags;
    public PhotoData(String loc) {
        location = loc;
        caption = "";
        tags = new HashMap<String, String>();
    }
    public PhotoData(PhotoData orig) {
        this(orig.location);
        this.caption = orig.caption;
        for(String key : orig.tags.keySet())
        {
            this.tags.put(key, orig.tags.get(key));
        }
    }
}


