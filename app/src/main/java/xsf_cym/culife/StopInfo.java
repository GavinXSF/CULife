package xsf_cym.culife;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class StopInfo extends AppCompatActivity {
    private ListView myListView;
    private ArrayAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_info);
        Intent intent = getIntent();
        ArrayList<String> stopInfo = (ArrayList<String>)intent.getStringArrayListExtra("stops_info");
        myListView = (ListView) findViewById(R.id.myListView);
        myAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,stopInfo);
        myListView.setAdapter(myAdapter);
    }
}
