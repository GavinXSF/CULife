package xsf_cym.culife;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class StopInfo extends AppCompatActivity {
    private ListView myListView;
    private ArrayAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_info);

        ActionBar topBar = getSupportActionBar();
        topBar.hide();

        TextView updateTopbar = findViewById(R.id.topbar_textview);
        updateTopbar.setText("Stop Information");

        Intent intent = getIntent();
        ArrayList<String> stopInfo = (ArrayList<String>)intent.getStringArrayListExtra("stops_info");
        myListView = (ListView) findViewById(R.id.myListView);
        myAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,stopInfo);
        myListView.setAdapter(myAdapter);

    }
}
