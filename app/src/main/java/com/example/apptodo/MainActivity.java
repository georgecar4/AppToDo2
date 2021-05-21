package com.example.apptodo;


import org.apache.commons.io.FileUtils;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
public static final String KEY_ITEM_TEXT = "item text";
public static final String KEY_ITEM_POSITION = "item position";
public static final int EDIT_TEXT_CODE = 20;

List<String> items;
Button addbttn;
EditText etItem;
RecyclerView rvItems;
ItemsAdapter itemsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setContentView(R.layout.activity_main);
        addbttn = findViewById(R.id.addbttn);
        etItem = findViewById(R.id.etItem);
        rvItems = findViewById(R.id.rvItems);

        loadItems();

        ItemsAdapter.OnLongClickListener onLongClickListener;

        onLongClickListener = position -> {
            items.remove(position);
        //  itemsAdapter.notifyItemRemoved(position);
            Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT).show();
            saveitems();
        };
        ItemsAdapter.OnClickListener onClickListener = position -> {
            Log.d("MainActivity", "single" + position);
            Intent i = new Intent(MainActivity.this, EditActivity.class);
            i.putExtra(KEY_ITEM_TEXT, items.get(position));
            i.putExtra(KEY_ITEM_POSITION, position);
            startActivityForResult(i, EDIT_TEXT_CODE);
        };

        ItemsAdapter itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        addbttn.setOnClickListener(v -> {
            String todoItem = etItem.getText().toString();
            items.add(todoItem);
            itemsAdapter.notifyItemInserted(items.size() - 1);
            etItem.setText("");
            Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT).show();
            saveitems();
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE){

            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
            items.set(position,itemText);
          //  itemsAdapter.notifyItemChanged(position);
            saveitems();
            Toast.makeText(getApplicationContext(), "Item was updated", Toast.LENGTH_SHORT).show();


        }
        else {
            Log.w( "MainActivity","Unknown call to on ActivityResult" );
        }
    }

    private File getDataFile(){
         return new File(getFilesDir(),"data.txt");
        }
        private void loadItems(){
        try{
        items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (Exception e) {
            Log.e("MainActivity","Error reading Items",e);
            items = new ArrayList<>();
        }
        }
        private void saveitems(){
            try {
                FileUtils.writeLines(getDataFile(),items);
            } catch (IOException e) {
                Log.e("MainActivity","Error writing",e);

            }
        }
    }
