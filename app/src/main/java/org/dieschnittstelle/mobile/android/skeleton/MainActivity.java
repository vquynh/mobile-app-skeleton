package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityMainListItemBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.DataItem;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private List<DataItem> items = Stream.of("Item 1", "Item 2", "Item 3",
            "Item 4", "Item 5", "Item 6", "Item 7", "Item 8", "Item 9",
            "Item 10", "Item 11", "Item 12", "Item 13")
            .map(name -> new DataItem(name, "Description"))
            .collect(Collectors.toList());
    private ArrayAdapter<DataItem> listViewAdapter;
    private FloatingActionButton addNewItemButton;
    private  int CALL_DETAILVIEW_FOR_CREATE = 0;
    private static String logTag = "MainView";
    private ProgressBar progressBar;

    private class DataItemsAdapter extends ArrayAdapter<DataItem>{
        private int layoutResource;
        public DataItemsAdapter(@NonNull @NotNull Context context, int resource, @NonNull @NotNull List<DataItem> objects) {
            super(context, resource, objects);
            this.layoutResource = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View recyclableItemView, @NonNull ViewGroup parent) {
            Log.i(logTag,"getView(): for position" + position + " and convertView" + recyclableItemView);
            View itemView = null;
            DataItem currentItem = getItem(position);
            if(recyclableItemView != null){

                TextView textView = (TextView) recyclableItemView.findViewById(R.id.itemName);
                if(textView != null) {

                    Log.i(logTag,"getView(): itemName in convertView: " + textView.getText());
                }
                itemView = recyclableItemView;
                ActivityMainListItemBinding recycleBinding = (ActivityMainListItemBinding) itemView.getTag();
                recycleBinding.setItem(currentItem);

            }else{
                ActivityMainListItemBinding currentBinding = DataBindingUtil
                        .inflate(getLayoutInflater(), this.layoutResource, null, false);
                currentBinding.setItem(currentItem);
                currentBinding.setController(MainActivity.this);
                itemView =  currentBinding.getRoot();
                itemView.setTag(currentBinding);
            }
            return itemView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. access view elements
        this.listView = findViewById(R.id.listView);
        this.listViewAdapter = new DataItemsAdapter(this,R.layout.activity_main_list_item, this.items);
        this.listView.setAdapter(this.listViewAdapter);
        this.progressBar = null;
        this.addNewItemButton = findViewById(R.id.addNewItemButton);

        this.listView.setOnItemClickListener((parent, view, position, id) -> {
            DataItem selectedItem = listViewAdapter.getItem(position);
            onItemSelected(selectedItem);
        });
        this.addNewItemButton.setOnClickListener(v -> this.onItemCreationRequested());

        // 3. load data
        //listViewAdapter.addAll(readAllDataItems());
        readAllDataItems(items -> listViewAdapter.addAll(items));
    }

    protected void onItemSelected(DataItem item) {
        item.setChecked(true);
        Intent detailViewIntent  = new Intent(this, DetailViewActivity.class);
        detailViewIntent.putExtra(DetailViewActivity.ARG_ITEM, item);
        this.startActivity(detailViewIntent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CALL_DETAILVIEW_FOR_CREATE){
            if(resultCode == Activity.RESULT_OK){
                this.onNewItemCreated((DataItem) data.getSerializableExtra(DetailViewActivity.ARG_ITEM));
            }else{
                showFeedbackMessage("Returning from detailView with:" + resultCode);
            }
        }
    }

    protected void onItemCreationRequested(){
        //this.showFeedbackMessage("New item creation requested");
        Intent detailViewForCreateIntent = new Intent(this, DetailViewActivity.class);
        this.startActivityForResult(detailViewForCreateIntent, CALL_DETAILVIEW_FOR_CREATE);
    }
    protected void showFeedbackMessage(String msg) {
        Snackbar.make(findViewById(R.id.rootView), msg, Snackbar.LENGTH_SHORT).show();

    }
    protected void onNewItemCreated(DataItem item){
        //showFeedbackMessage("Created item: " + itemName);
        //TextView newItemView = (TextView) getLayoutInflater().inflate(R.layout.activity_main_list_item, null);
        //newItemView.setText(itemName);
        this.listViewAdapter.add(item);
        this.listView.setSelection(this.listViewAdapter.getPosition(item));

    }

    public void onCheckedChangedInListView(DataItem item){
        item.setChecked(true);
    }

    protected void readAllDataItems(Consumer<List<DataItem>> onread) {
        this.progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<DataItem> dataItems = Stream.of("Item 1", "Item 2", "Item 3",
                    "Item 4", "Item 5", "Item 6", "Item 7", "Item 8", "Item 9",
                    "Item 10", "Item 11", "Item 12", "Item 13")
                    .map(name -> new DataItem(name, "Description of "+name))
                    .collect(Collectors.toList());
            runOnUiThread(() -> {
                this.progressBar.setVisibility(View.GONE);

                onread.accept(dataItems);
            });
        }).start();
    }
}
