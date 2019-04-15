package xsf_cym.culife;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.content.res.AssetManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> stopInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //the following checks user permissions and asks to request those permissions

        //检测位置权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }





        Bus[] buses = new Bus[12];
        String[] lineNum = {"1A","2","3","4","5","6A","7","8","N","H","6B","1B"};
        for(int i = 0; i < 12; i++){
            buses[i] = new Bus(lineNum[i]);
        }
        final Bus[] buses_final = buses;
        System.out.println( buses[0].passStops.get(3) );
        ArrayList<BusStop> stops = new ArrayList<BusStop>();
        final ArrayList<String> initializedStops = new ArrayList<String>();
        for(int i = 0; i < 12; i++){
            for(int j = 0; j < buses[i].passStops.size(); j++){
                if(!initializedStops.contains(buses[i].passStops.get(j))){
                    initializedStops.add(buses[i].passStops.get(j));
                    stops.add(new BusStop(buses[i].passStops.get(j), buses));
                }
            }
        }


        StringBuilder stringBuilder = new StringBuilder();
        try {

            AssetManager assetManager = getAssets();

            BufferedReader bf = new BufferedReader(new InputStreamReader(
                        assetManager.open("stops.json")));
            String line;
            while ((line = bf.readLine()) != null) {
                    stringBuilder.append(line);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String jsonData = stringBuilder.toString();
        ArrayList<String> name = new ArrayList<String>();
        ArrayList<Double> Lng = new ArrayList<Double>();
        ArrayList<Double> Lat = new ArrayList<Double>();
        try
        {
            JSONObject jsonObj = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObj.getJSONArray("stops");
//            Log.d("Tsai",""+jsonArray.length());
            for (int i=0; i < jsonArray.length(); i++)    {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                name.add(jsonObject.getString("name"));
                Lng.add(jsonObject.getDouble("longitude"));
                Lat.add(jsonObject.getDouble("latitude"));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        final BusStop[] stopsArray = new BusStop[stops.size()];
        stops.toArray(stopsArray);


        for(int i = 0; i < stops.size(); i++){
            stopsArray[i].calculateTime(buses);
            if(name.contains(stopsArray[i].stopName)){

                int index = name.indexOf(stopsArray[i].stopName);
                stopsArray[i].setLocation(Lat.get(index),Lng.get(index));
            }
        }

        //stopsArray[3].waitingTime(1313);
        //启动服务
        Intent serviceIntent = new Intent(MainActivity.this, GpsService.class);
        serviceIntent.putExtra("stops",stopsArray);
        serviceIntent.putExtra("buses",buses_final);
        serviceIntent.putExtra("stop_names",initializedStops);
        startService(serviceIntent);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ActionBar topBar = getSupportActionBar();
//        topBar.setBackgroundDrawable(getDrawable(R.i));


        Button SelectStop = findViewById(R.id.select_stop);
        Button AddNewRoute = findViewById(R.id.add_new_route);
        Button MyRoute = findViewById(R.id.my_route);
        Button TempButton = findViewById(R.id.temp_button);

        TempButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent temp_intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(temp_intent);
            }

        });

        SelectStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent select_stop = new Intent(MainActivity.this, SelectStop.class);
                select_stop.putExtra("stops",stopsArray);
                select_stop.putExtra("stop_names",initializedStops);
                select_stop.putExtra("buses",buses_final);
                startActivity(select_stop);
            }
        });

        AddNewRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add_new_route = new Intent(MainActivity.this, AddNewRoute.class);
                add_new_route.putExtra("stop_names",initializedStops);
                add_new_route.putExtra("stops",stopsArray);
                add_new_route.putExtra("buses",buses_final);
                startActivity(add_new_route);
            }
        });

        MyRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent my_route = new Intent(MainActivity.this, MyRoute.class);
                my_route.putExtra("stops",stopsArray);
                my_route.putExtra("buses",buses_final);
                my_route.putExtra("stop_names",initializedStops);
                startActivity(my_route);
            }
        });
    }



    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.library_item:
                Intent browse_library_info = new Intent(Intent.ACTION_VIEW);
                browse_library_info.setData(Uri.parse("http://www.cuhk.edu.hk/chinese/campus/library-museum.html"));
                startActivity(browse_library_info);
                break;
            case R.id.canteen_item:
                Intent browse_canteen_info = new Intent(Intent.ACTION_VIEW);
                browse_canteen_info.setData(Uri.parse("http://www.cuhk.edu.hk/chinese/campus/accommodation.html#canteen_info"));
                startActivity(browse_canteen_info);
                break;
            default:
        }
        return true;
    }
}
