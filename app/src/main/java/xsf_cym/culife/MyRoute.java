package xsf_cym.culife;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MyRoute extends AppCompatActivity {
    private ArrayList<String> startTime = new ArrayList<String>();
    private ArrayList<String> startStop = new ArrayList<String>();
    private ArrayList<String> endStop = new ArrayList<String>();
    private ListView myListView;
    private ArrayAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_route);

        Intent intent = getIntent();
        BusStop[] stopsArray = (BusStop[]) intent.getSerializableExtra("stops");
        Bus[] buses = (Bus[]) intent.getSerializableExtra("buses");
        ArrayList<String> stopNames = (ArrayList<String>) intent.getStringArrayListExtra("stop_names");

        File newFile = new File(MyRoute.this.getFilesDir().getAbsolutePath(), "my_route.txt");
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try{
            fileReader = new FileReader(newFile);
            bufferedReader = new BufferedReader(fileReader);
            String line;
            int tag = 0;
            while((line = bufferedReader.readLine())!= null){
                if(tag == 0)
                    startTime.add(line);
                if(tag == 1)
                    startStop.add(line);
                if(tag == 2)
                    endStop.add(line);
                tag = (tag + 1) % 3;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        //Log.d("Tsai", startTime.size()+"");

        ArrayList<String> displayInfo = new ArrayList<String>();
        ArrayList<String> firstHalf = new ArrayList<String>();

        if(startTime.size()!=startStop.size() || startStop.size()!= endStop.size()){
            Log.e("Tsai", "File broken");
            MyRoute.this.finish();
        }
        else {
            Route[] routes = new Route[startStop.size()];
            for(int i = 0; i < startStop.size(); i++){
                routes[i] = new Route(Integer.parseInt((String)startTime.get(i)),
                        (String)startStop.get(i),(String)endStop.get(i));
                routes[i].computeLine(stopsArray,buses);
                firstHalf = stopsArray[stopNames.indexOf(routes[i].startPosition)].waitingTime(routes[i].startTime, routes[i].validBus);
           //     Log.d("Tsai", stopsArray[stopNames.indexOf(routes[i].destination)].stopName +" "+routes[i].startPosition+" "+routes[i].destination+"  "+routes[i].validBus[0]+" "+routes[i].validBus[1]+" "+routes[i].validBus[2]);
                displayInfo.add("Route"+(i+1)+ "\n" + routes[i].startPosition + " → " + routes[i].destination + "\n" + routes[i].startTime/100 + ":" + routes[i].startTime%100);
                int index = 0;

                for(int j = 0; j < firstHalf.size(); j++){
                    for(int k = index; k < 12; k++){
                        if(routes[i].validBus[k]){
                            index = k + 1;
                            break;
                        }
                    }
                    displayInfo.add(firstHalf.get(j)+ buses[index - 1].estimateTime(routes[i].startPosition,routes[i].destination) + " mins\n");
                }
            }
        }
        myListView = (ListView) findViewById(R.id.myListView);
        myAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,displayInfo);
        myListView.setAdapter(myAdapter);




        ActionBar topBar = getSupportActionBar();
        topBar.hide();

        TextView updateTopbar = findViewById(R.id.topbar_textview);
        updateTopbar.setText("My Route");
    }
}
