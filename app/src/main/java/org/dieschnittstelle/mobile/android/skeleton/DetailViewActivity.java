package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.dieschnittstelle.mobile.android.skeleton.model.DataItem;

public class DetailViewActivity extends AppCompatActivity {

    public static final String ARG_ITEM = "item";
    private DataItem item;
    private EditText itemNameText;
    private EditText itemDescription;
    private FloatingActionButton saveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailview);

        // Read elements
        saveButton = findViewById(R.id.saveButton);
        itemNameText = findViewById(R.id.itemName);
        itemDescription = findViewById(R.id.itemDescription);

        // Make elements interactive
        saveButton.setOnClickListener(v -> this.onSaveItem());

        // Fill view with data
        item = (DataItem) getIntent().getSerializableExtra(ARG_ITEM);

        if(item!=null){
            itemNameText.setText(item.getItemName());
            itemDescription.setText(item.getDescription());
        }else{
            item = new DataItem();
        }
    }

    private void onSaveItem() {
        item.setItemName(this.itemNameText.getText().toString());
        item.setDescription(this.itemDescription.getText().toString());
        Intent returnIntent = new Intent();
        returnIntent.putExtra(ARG_ITEM, item);
        this.setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }


}
