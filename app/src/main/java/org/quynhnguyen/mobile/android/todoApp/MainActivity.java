package org.quynhnguyen.mobile.android.todoApp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import org.quynhnguyen.mobile.android.todoApp.databinding.ActivityMainListItemBinding;
import org.quynhnguyen.mobile.android.todoApp.model.DataItem;
import org.quynhnguyen.mobile.android.todoApp.model.IDataItemCRUDOperations;
import org.quynhnguyen.mobile.android.todoApp.model.impl.DataItemCRUDOperationsAsyncImpl;
import org.jetbrains.annotations.NotNull;
import org.quynhnguyen.mobile.android.todoApp.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {

    private static final int CALL_DETAILVIEW_FOR_CREATE = 0;
    private static final int CALL_DETAILVIEW_FOR_EDIT = 1;
    private static final int CALL_LOGIN_VIEW = 2;

    private ListView listView;
    private List<DataItem> items = new ArrayList<>();
    private ArrayAdapter<DataItem> listViewAdapter;
    private ProgressBar progressBar;
    private DataItemCRUDOperationsAsyncImpl crudOperations;

    private class DataItemsAdapter extends ArrayAdapter<DataItem>{
        private final int layoutResource;
        public DataItemsAdapter(@NonNull @NotNull Context context, int resource, @NonNull @NotNull List<DataItem> objects) {
            super(context, resource, objects);
            this.layoutResource = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View recyclableItemView, @NonNull ViewGroup parent) {
            String logTag = "MainView";
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

        if(((DataItemApplication) getApplication()).isServerAvailable()) {
            startActivity(new Intent(this, LoginActivity.class));
        }

        setContentView(R.layout.activity_main);

        // 1. access view elements
        this.listView = findViewById(R.id.listView);
        this.listViewAdapter = new DataItemsAdapter(this,R.layout.activity_main_list_item, this.items);
        this.listView.setAdapter(this.listViewAdapter);
        this.progressBar = new ProgressBar(this);
        FloatingActionButton addNewItemButton = findViewById(R.id.addNewItemButton);

        this.listView.setOnItemClickListener((parent, view, position, id) -> {
            DataItem selectedItem = listViewAdapter.getItem(position);
            onItemSelected(selectedItem);
        });
        addNewItemButton.setOnClickListener(v -> this.onItemCreationRequested());

        // 3. load data
        //listViewAdapter.addAll(readAllDataItems());
        IDataItemCRUDOperations crudExecutor = ((DataItemApplication) this.getApplication()).getCRUDOperations();
        this.crudOperations = new DataItemCRUDOperationsAsyncImpl(crudExecutor, this,progressBar);
        //this.crudOperations = new DataItemCRUDOperationsAsyncImpl(new RetrofitRemoteDataItemCRUDOperationsImpl(), this, progressBar);
        this.crudOperations.readAllDataItems(items ->
        {
            listViewAdapter.addAll(items);
            this.sortListAndScrollToItem(null);

        });
    }

    protected void onItemSelected(DataItem item) {
        item.setDone(true);
        Intent detailViewIntent  = new Intent(this, DetailViewActivity.class);
        detailViewIntent.putExtra(DetailViewActivity.ARG_ITEM, item);
        this.startActivityForResult(detailViewIntent, CALL_DETAILVIEW_FOR_EDIT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CALL_DETAILVIEW_FOR_CREATE:
                if(resultCode == Activity.RESULT_OK){
                    this.onNewItemCreated((DataItem) data.getSerializableExtra(DetailViewActivity.ARG_ITEM));
                }else{
                    showFeedbackMessage("Returning from detailView with:" + resultCode);

                }
            case CALL_DETAILVIEW_FOR_EDIT:
                if(resultCode == Activity.RESULT_OK){
                    DataItem editedItem = (DataItem) data.getSerializableExtra(DetailViewActivity.ARG_ITEM);
                    showFeedbackMessage("Updated item: " + editedItem.getItemName());
                    this.onItemEdited(editedItem);
                }else {
                    showFeedbackMessage(" Returning with requestCode: " + requestCode + " and resultCode: " + resultCode);
                }
            default:
                showFeedbackMessage("Returning with requestCode: " + requestCode + " and resultCode: " + resultCode);
        }

    }

    protected void onItemEdited(DataItem editedItem) {
        int pos = this.items.indexOf(editedItem);
        this.items.remove(pos);
        this.crudOperations.updateDataItem(editedItem, item -> {
            this.items.add(pos,item);
            this.listViewAdapter.notifyDataSetChanged();
        });
        sortListAndScrollToItem(editedItem);
    }

    protected void onItemCreationRequested(){
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
        this.crudOperations.createDataItem(item, i -> this.listViewAdapter.add(i));
        sortListAndScrollToItem(item);

    }

    public void onCheckedItemDoneInListView(DataItem item){
        item.setDone(item.isDone());
        this.crudOperations.updateDataItem(item, this::sortListAndScrollToItem);
    }

    public void onCheckedItemFavouriteInListView(DataItem item){
        item.setFavourite(item.isFavourite());
        this.crudOperations.updateDataItem(item, this::sortListAndScrollToItem);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // TODO: add option menu for simulation action of deleting local/remote todos and sync data
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sortItems){
            this.sortListAndScrollToItem(null);
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    private void sortListAndScrollToItem(DataItem item) {
        sortItems(this.items);
        this.listViewAdapter.notifyDataSetChanged();
        if(item != null) {
            int thisPosition = this.listViewAdapter.getPosition(item);
            this.listView.setSelection(thisPosition);
        }
    }

    private void sortItems(List<DataItem> items) {
        items.sort(Comparator.comparing(DataItem::isDone).thenComparing(DataItem::getItemName));
    }

}
