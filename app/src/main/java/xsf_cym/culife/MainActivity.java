package xsf_cym.culife;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> stopInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bus[] buses = new Bus[12];
        String[] lineNum = {"1A","2","3","4","5","6A","7","8","N","H","6B","1B"};
        for(int i = 0; i < 12; i++){
            buses[i] = new Bus(lineNum[i]);
        }
        System.out.println( buses[0].passStops.get(3) );
        ArrayList<BusStop> stops = new ArrayList<BusStop>();
        ArrayList<String> initializedStops = new ArrayList<String>();
        for(int i = 0; i < 12; i++){
            for(int j = 0; j < buses[i].passStops.size(); j++){
                if(!initializedStops.contains(buses[i].passStops.get(j))){
                    initializedStops.add(buses[i].passStops.get(j));
                    stops.add(new BusStop(buses[i].passStops.get(j), buses));
                }
            }
        }

        Route myRoute1 = new Route("1800","Sir Run Run Shaw Hall","University MTR Station");
        final BusStop[] stopsArray = new BusStop[stops.size()];
        stops.toArray(stopsArray);
        myRoute1.computeLine(stopsArray, buses);

        stopsArray[3].calculateTime(buses);
        //stopsArray[3].waitingTime(1313);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button SelectStop = findViewById(R.id.select_stop);
        Button AddNewRoute = findViewById(R.id.add_new_route);
        Button MyRoute = findViewById(R.id.my_route);

        SelectStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent select_stop = new Intent(MainActivity.this, SelectStop.class);
                select_stop.putExtra("stops",stopsArray);
                startActivity(select_stop);
            }
        });

        AddNewRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add_new_route = new Intent(MainActivity.this, SelectStop.class);
                startActivity(add_new_route);
            }
        });

        MyRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent my_route = new Intent(MainActivity.this, MyRoute.class);
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
