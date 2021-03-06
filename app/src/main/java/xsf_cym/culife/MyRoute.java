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
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

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
    private HashMap<String,Double> waitingTimes = new HashMap<String, Double>();
    public static Object lock=new Object();
    private Integer myI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_route);

        this.setTitle("My routes");

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

            NumberFormat nf = NumberFormat.getNumberInstance();
            // 保留一位小数
            nf.setMaximumFractionDigits(1);
            nf.setMinimumFractionDigits(1);

            HashMap<String,Integer> busIndex = new HashMap<String, Integer>();

            busIndex.put("1A",0);
            busIndex.put("2",1);
            busIndex.put("3",2);
            busIndex.put("4",3);
            busIndex.put("5",4);
            busIndex.put("6A",5);
            busIndex.put("7",6);
            busIndex.put("8",7);
            busIndex.put("N",8);
            busIndex.put("H",9);
            busIndex.put("6B",10);
            busIndex.put("1B",11);

            for(int i = 0; i < startStop.size(); i++){

                routes[i] = new Route(Integer.parseInt((String)startTime.get(i)),
                        (String)startStop.get(i),(String)endStop.get(i));
                routes[i].computeLine(stopsArray,buses);
                myI = i;

                waitingTimes = stopsArray[stopNames.indexOf(routes[myI].startPosition)].waitingTime(routes[myI].startTime, routes[myI].validBus);
                Set<String> keys = waitingTimes.keySet();
                Iterator<String> iterator1 = keys.iterator();
                while (iterator1.hasNext()) {
                    String busNum = iterator1.next();
                    double bestTime;
                    bestTime = waitingTimes.get(busNum);
                    if (bestTime > 0.0)
                        firstHalf.add("Line " + busNum + ": " + nf.format(bestTime) + " mins\nEstimated travel time: ");
                    else
                        firstHalf.add("Line " + busNum + ": Due\nEstimated travel time: ");
                }

           //     Log.d("Tsai", stopsArray[stopNames.indexOf(routes[i].destination)].stopName +" "+routes[i].startPosition+" "+routes[i].destination+"  "+routes[i].validBus[0]+" "+routes[i].validBus[1]+" "+routes[i].validBus[2]);
                displayInfo.add("Route"+(i+1)+ "\n" + routes[i].startPosition + " → " + routes[i].destination + "\n" + routes[i].startTime/100 + ":" + String.format("%0"+2+"d", routes[i].startTime%100));
                int index = 0;

                for(int j = 0; j < firstHalf.size(); j++){
                    for(int k = index; k < 12; k++){
                        if(routes[i].validBus[k]){
                            index = k + 1;
                            break;
                        }
                    }
                    displayInfo.add(firstHalf.get(j)+ nf.format(buses[index - 1].estimateTime(routes[i].startPosition,routes[i].destination)) + " mins");
                }
            }
        }
        myListView = (ListView) findViewById(R.id.myListView);
        myAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,displayInfo);
        myListView.setAdapter(myAdapter);

    }
}
