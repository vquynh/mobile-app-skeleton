package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DetailViewActivity extends AppCompatActivity {

    public static final String ARG_ITEM = "item";
    private String item;
    private EditText itemNameText;
    private FloatingActionButton saveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailview);

        // Read elements
        saveButton = findViewById(R.id.saveButton);
        itemNameText = findViewById(R.id.itemName);

        // Make elements interactive
        saveButton.setOnClickListener(v -> this.onSaveItem());

        // Fill view with data
        item = getIntent().getStringExtra(ARG_ITEM);

        if(item!=null){
            itemNameText.setText(item);
        }
    }

    private void onSaveItem() {
        String itemName = this.itemNameText.getText().toString();
        Intent returnIntent = new Intent();
        returnIntent.putExtra(ARG_ITEM, itemName);
        this.setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }


}
