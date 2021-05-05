package org.dieschnittstelle.mobile.android.skeleton;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private ViewGroup listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.listView = findViewById(R.id.listView);
        for (int i = 0; i < listView.getChildCount(); i++) {
            TextView currentChild = (TextView) this.listView.getChildAt(i);
            currentChild.setOnClickListener(v -> {
                onItemSelected(currentChild.getText() + " selected.");
            });
        }
    }

    protected void onItemSelected(String itemName) {

        Intent detailViewIntent  = new Intent(this, DetailViewActivity.class);
        detailViewIntent.putExtra(DetailViewActivity.ARG_ITEM, "test");
        this.startActivity(detailViewIntent);


    }

    public void showFeedbackMessage(String msg) {
        Snackbar.make(findViewById(R.id.rootView), msg, Snackbar.LENGTH_INDEFINITE).show();

    }
}
