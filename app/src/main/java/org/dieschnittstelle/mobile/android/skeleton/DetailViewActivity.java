package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityDetailviewBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.DataItem;

public class DetailViewActivity extends AppCompatActivity {

    public static final String ARG_ITEM = "item";
    private DataItem item;
    private ActivityDetailviewBinding dataBindingHandle;
    private EditText itemNameText;
    private EditText itemDescription;
    private FloatingActionButton saveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dataBindingHandle = DataBindingUtil.setContentView(this, R.layout.activity_detailview);

        item = (DataItem) getIntent().getSerializableExtra(ARG_ITEM);

        if(item==null){
            item = new DataItem();
        }
        this.dataBindingHandle.setController(this);
    }

    public void onSaveItem() {
        item.setItemName(this.itemNameText.getText().toString());
        item.setDescription(this.itemDescription.getText().toString());
        Intent returnIntent = new Intent();
        returnIntent.putExtra(ARG_ITEM, item);
        this.setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public DataItem getItem() {
        return item;
    }

    public void setItem(DataItem item) {
        this.item = item;
    }
}
