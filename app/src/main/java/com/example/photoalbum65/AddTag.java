package com.example.photoalbum65;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.adapter.ListViewAdapter;
import java.util.ArrayList;
import java.util.HashMap;

import static android.widget.Toast.LENGTH_SHORT;

public class AddTag extends AppCompatActivity {

    private ListView lv;
    private CustomAdapter customAdapter;
    private ArrayList<Model> modelArrayList;
   // private String[] myImageNameList = new String[]{"Add a Tag"};
    private ArrayList<String> myImageNameList;
    private FloatingActionButton addTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);
        final int indexGOT = getIntent().getIntExtra("index",0); //might be a problem when swiping page
        addTag = findViewById(R.id.fab);
        addTag.setImageResource(R.drawable.pluser);
        final PhotoData pd = AlbumActivity.albumData.photos.get(indexGOT);
        myImageNameList = new ArrayList<String>();// add all tags to list if null do something
        if(pd.tags.containsKey("person")){
            myImageNameList.add("Person: " + pd.tags.get("person"));
        }
        if(pd.tags.containsKey("location")){
            myImageNameList.add("Location: " + pd.tags.get("location"));
        }
     //   int nameListsize = myImageNameList.length;
        lv = findViewById(R.id.listview);
        if(myImageNameList.isEmpty()){
            //do nothing
        }else {
            modelArrayList = populateList(myImageNameList);
            customAdapter = new CustomAdapter(this, modelArrayList);
            lv.setAdapter(customAdapter);
        }
        addTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(AddTag.this);
                dialog.setTitle("Tag Input");
                dialog.setView(R.layout.tag_alert);
                dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //User says yes to create
                        int index = indexGOT;
                        PhotoData pd = AlbumActivity.albumData.photos.get(index);
                        RadioGroup rg = (RadioGroup)((AlertDialog)dialogInterface).findViewById(R.id.radioGroup);
                        EditText input = (EditText) ((AlertDialog)dialogInterface).findViewById(R.id.tagInput);
                        if(input.getText().toString().isEmpty()){
                            Toast.makeText(AddTag.this, "Tag cannot be empty", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(rg.getCheckedRadioButtonId() == R.id.personBtn){
                            pd.tags.put("person", input.getText().toString());
                            myImageNameList = new ArrayList<String>();// add all tags to list if null do something
                            if(pd.tags.containsKey("person")){
                                myImageNameList.add(" Person: " + pd.tags.get("person"));
                                //remove
                            }
                            if(pd.tags.containsKey("location")){
                                myImageNameList.add(" Location: " + pd.tags.get("location"));
                            }
                            modelArrayList = populateList(myImageNameList);
                            customAdapter = new CustomAdapter(AddTag.this, modelArrayList);
                            lv.setAdapter(customAdapter);
                            Toast.makeText(AddTag.this, "Tag: person Value: " + input.getText() + " added", Toast.LENGTH_SHORT).show();
                        }else if(rg.getCheckedRadioButtonId() == R.id.locationBtn){
                            pd.tags.put("location", input.getText().toString());
                            myImageNameList = new ArrayList<String>();// add all tags to list if null do something
                            if(pd.tags.containsKey("person")){
                                myImageNameList.add(" Person: " + pd.tags.get("person"));
                            }
                            if(pd.tags.containsKey("location")){
                                myImageNameList.add(" Location: " + pd.tags.get("location"));
                            }
                            modelArrayList = populateList(myImageNameList);
                            customAdapter = new CustomAdapter(AddTag.this, modelArrayList);
                            lv.setAdapter(customAdapter);
                            Toast.makeText(AddTag.this, "Tag: location Value: " + input.getText() + " added", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(AddTag.this, "Please select a tag option", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
                dialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //User says no to this is not the address
                    }
                });
                dialog.create();
                dialog.show();
            }
        });

        final SwipeToDismissTouchListener<ListViewAdapter> touchListener =
                new SwipeToDismissTouchListener<>(
                        new ListViewAdapter(lv),
                        new SwipeToDismissTouchListener.DismissCallbacks<ListViewAdapter>() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListViewAdapter view, int position) {
                                Model model = (Model)customAdapter.getItem(position);
                                String print = model.getName();
                                Toast.makeText(AddTag.this, print, LENGTH_SHORT).show();
                                System.out.println(print);
                                String tester = print.substring(0,print.indexOf(':'));
                                if(tester.equals("Person")){
                                    pd.tags.remove("person");
                                }else{
                                    pd.tags.remove("location");
                                }
                                customAdapter.remove(position);
                                if(customAdapter.isEmpty()){
                                    pd.tags = new HashMap<String, String>();
                                }
                            }
                        });

        lv.setOnTouchListener(touchListener);
        lv.setOnScrollListener((AbsListView.OnScrollListener) touchListener.makeScrollListener());
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (touchListener.existPendingDismisses()) {
                    touchListener.undoPendingDismiss();
                } else {
                    Toast.makeText(AddTag.this, "Swipe Tag Left to Delete", LENGTH_SHORT).show();
                }
            }
        });

    }

    private ArrayList<Model> populateList(ArrayList<String> booth){

        ArrayList<Model> list = new ArrayList<>();
        for(String value : booth)
        {
            Model model = new Model();
            model.setName(value);
            list.add(model);
        }
//        for(int i = 0; i < length; i++){
//            Model model = new Model();
//            model.setName(myImageNameList[i]);
//            list.add(model);
//        }

        return list;
    }
}
