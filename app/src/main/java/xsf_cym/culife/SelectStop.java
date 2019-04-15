package xsf_cym.culife;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SelectStop extends AppCompatActivity {
    public static Object lock=new Object();
    private LocationManager locationManager;
    private String locationProvider;
    private Location location;
    private LocationListener locationListener;
    private Spinner mySpinner;
    private EditText inputText;
    private ListView mListView1;
    private ListView mListView2;
    private ArrayAdapter myAdapter;
    private Button btn;
    private Button btn2;
    private int inputNum;
    private HashMap<String, Double> waitingTimes;
    private ArrayList<String> stopInfo = new ArrayList<String>();
    private ArrayList<String> timeInfo = new ArrayList<String>();
    private int selectedStop = -1;
    private ArrayList<String> busNumList = new ArrayList<String>();
    private ArrayList<Integer> stopIndexList = new ArrayList<Integer>();
    private ArrayList<Boolean> isDueList = new ArrayList<Boolean>();
    public SelectStop() {
    }

    private Location getBestLocation(LocationManager locationManager) {
        Location result = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED ) {
            // 待解决, 在服务中若检查到没有权限怎么办
            Toast.makeText(this,"没有权限（多次更新）",Toast.LENGTH_SHORT).show();
        }else {
            if (locationManager != null) {
                result = locationManager
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (result != null) {
                    return result;
                } else {
                    result = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    return result;
                }
            }
        }
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_stop);

        this.setTitle("Select stop");
//        ActionBar topBar = getSupportActionBar();
//
//            topBar.hide();
//
//        TextView updateTopbar = findViewById(R.id.topbar_textview);
//        updateTopbar.setText("Select Stop");
        mListView1 = (ListView) findViewById(R.id.myListView);
        myAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,stopInfo);

        mListView1.setAdapter(myAdapter);
        mListView2 = (ListView) findViewById(R.id.myListView2);
        MyAdapter myAdapter2 = new MyAdapter(timeInfo,busNumList,stopIndexList,isDueList);

        mListView2.setAdapter(myAdapter2);


        Intent intent = getIntent();
        final BusStop[] stopsArray = (BusStop[]) intent.getSerializableExtra("stops");
        final ArrayList<String> stopNames = (ArrayList<String>) intent.getStringArrayListExtra("stop_names");
        final Bus[] buses = (Bus[]) intent.getSerializableExtra("buses");
        final String[] stopNamesArray = stopNames.toArray(new String[stopNames.size()]);
//        Log.d("Tsai",stopNamesArray[3]);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(SelectStop.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else {


            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            List<String> providers = locationManager.getProviders(true);
            if (providers.contains(LocationManager.GPS_PROVIDER)) {
                //如果是GPS
                locationProvider = LocationManager.GPS_PROVIDER;
            } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                //如果是Network
                locationProvider = LocationManager.NETWORK_PROVIDER;
            } else {
                Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            }
            // 检查权限

            // 得到location object
            location = locationManager.getLastKnownLocation(locationProvider);
            locationListener = new LocationListener() {
                private Location lastLocation = null;
                private double calculatedSpeed = 0.0;

                @Override
                public void onStatusChanged(String provider, int status, Bundle arg2) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }

                @Override
                public synchronized void onLocationChanged(Location location) {

                    location = getBestLocation(locationManager);



                }
            };
            //监视地理位置变化
            locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(SelectStop.this, R.layout.support_simple_spinner_dropdown_item, stopNamesArray);
            mySpinner = (Spinner) findViewById(R.id.stopsSpinner);
            mySpinner.setAdapter(spinnerAdapter);
            //choose the nearest one by default
//            final Object[] objs = new Object[1];
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    double min = 999;
                    int nearest = -1;
                    for (int i = 0; i < stopsArray.length; i++) {
                        if ((stopsArray[i].Latitude != null) && (stopsArray[i].Longitude != null)) {
                            Location stopLocation = new Location("");
                            stopLocation.setLatitude(stopsArray[i].Latitude);
                            stopLocation.setLongitude(stopsArray[i].Longitude);
                            double distance = location.distanceTo(stopLocation);

                            if (distance < min) {
                                min = distance;
                                nearest = i;
//                                objs[0] = nearest;
                            }
                        }
                    }
                    mySpinner.setSelection(nearest);
                }
            }, 500);


        }
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStop = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        btn = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                final int hour = calendar.get(Calendar.HOUR_OF_DAY);
                final int minutes = calendar.get(Calendar.MINUTE);
                inputNum = hour*100+minutes;
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
                if((inputNum%100)>59 || (inputNum/100 > 24)){
                    Toast.makeText(SelectStop.this, "Wrong input\nInput format:HHmm", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (selectedStop > -1) {
                        stopInfo.clear();
                        timeInfo.clear();
                        busNumList.clear();
                        stopIndexList.clear();
                        isDueList.clear();

                            //search for info in mysql; timeFromDB = currentTime - (found time + interval) (if valid time was found)
                        Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    synchronized(lock) {

                                        waitingTimes = stopsArray[selectedStop].waitingTime(inputNum);
                                        Set<String> keys=waitingTimes.keySet();
                                        Iterator<String> iterator1=keys.iterator();
                                        while (iterator1.hasNext()) {
                                            String busNum = iterator1.next();
                                            double bestTime;
                                            long timeFromDB = -1;
                                            int stopIndexOfData = -1;
                                            Object[] objs = new Object[2];
                                            Connection connection = null;
                                            try {

                                                Class.forName("com.mysql.jdbc.Driver");
                                                String jdbcUrl = String.format("jdbc:mysql://34.92.5.65:3306/culife");
                                                Log.d("Tsai", "getting connection");
                                                connection = DriverManager.getConnection(jdbcUrl, "root", "carlos0923=-=");
                                                Log.d("Tsai", "connection set");
                                            } catch (SQLException ex) {
                                                ex.printStackTrace();
                                            } catch (ClassNotFoundException ex) {
                                                ex.printStackTrace();
                                            }
                                            try {
                                                Statement st = connection.createStatement();
                                                String sql = "SELECT * FROM busInfo WHERE busNum='" + busNum + "' and stopIndex between " +
                                                        (buses[busIndex.get(busNum)].passStops.indexOf(stopsArray[selectedStop].stopName) - 3) + " and " +
                                                        (buses[busIndex.get(busNum)].passStops.indexOf(stopsArray[selectedStop].stopName) - 1) +
                                                        " ORDER BY id DESC LIMIT 1";
                                                ResultSet rs = st.executeQuery(sql);
                                                if (rs.next()) {

                                                    objs[0] = rs.getLong("time");
                                                    Log.d("Tsai", "onClick: " + (long) objs[0]);
                                                    objs[1] = rs.getInt("stopIndex");
                                                    connection.close();
                                                } else {
                                                    Log.d("Tsai", "No such result");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            if (objs[0] != null) {
                                                timeFromDB = (long) objs[0];
                                                stopIndexOfData = (int) objs[1];
                                                Log.d("Tsai", "Got it");
                                                int travelTime = (int) (60 * buses[busIndex.get(busNum)].estimateTime(buses[busIndex.get(busNum)].passStops.get(stopIndexOfData), stopsArray[selectedStop].stopName));
                                                long now = System.currentTimeMillis();
                                                long bestTimeInSeconds = (timeFromDB - now) / 1000 + travelTime;
                                                double errorAllowance = (buses[busIndex.get(busNum)].passStops.indexOf(stopsArray[selectedStop].stopName)-stopIndexOfData)*2.0;
                                                double tempConverter = (double) bestTimeInSeconds;
                                                bestTime = tempConverter / 60.0;
                                                if (bestTime < -errorAllowance)
                                                    bestTime = waitingTimes.get(busNum);
                                            } else {
                                                bestTime = waitingTimes.get(busNum);
                                                Log.d("Tsai", "Miss it");
                                            }
                                            NumberFormat nf = NumberFormat.getNumberInstance();
                                            // 保留一位小数
                                            nf.setMaximumFractionDigits(1);
                                            nf.setMinimumFractionDigits(1);

                                            if(bestTime>0.0){
                                                timeInfo.add("Line " + busNum + ": " + nf.format(bestTime) + " mins\n");
                                                busNumList.add(busNum);
                                                stopIndexList.add(buses[busIndex.get(busNum)].passStops.indexOf(stopsArray[selectedStop].stopName));
                                                if(busNum.compareTo("3")==0)
                                                    isDueList.add(false);
                                                else
                                                    isDueList.add(true);
                                            }
                                            else{
                                                timeInfo.add("Line " + busNum + ": Due\n");
                                                busNumList.add(busNum);
                                                stopIndexList.add(buses[busIndex.get(busNum)].passStops.indexOf(stopsArray[selectedStop].stopName));
                                                isDueList.add(true);
                                            }
                                        }
                                        Log.d("Tsai", "notify" +lock);
                                        lock.notify();

                                    }

                                }});


                            synchronized (lock) {
                                    try {
                                        thread.start();
                                        Log.d("Tsai", "wait"+lock );
                                        lock.wait();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }}

                        Log.d("Tsai", "stop waiting" );
                        stopInfo.add(0, stopNamesArray[selectedStop]);
                        if((inputNum%100)<10)
                            stopInfo.add(1, inputNum/100 + ":0" + inputNum%100);
                        else
                            stopInfo.add(1, inputNum/100 + ":" + inputNum%100);

//                        mListView1.postInvalidate();
//                        mListView2.postInvalidate();
                        myAdapter.notifyDataSetChanged();
                        myAdapter2.notifyDataSetChanged();

//                        stopInfo.add(1,"Lat:"+stopsArray[selectedStop].Latitude);

                    }
                }

            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                            double[] data = new double[4];
                            int indexOfCurrentStop = buses[2].passStops.indexOf(stopsArray[selectedStop].stopName);
                            double bestTime;
                            long timeFromDB = -1;
                            int stopIndexOfData = -1;
                            Object[] objs = new Object[2];
                            for(int i = indexOfCurrentStop+1;i<indexOfCurrentStop+5;i++) {
                                if(i>buses[2].passStops.size()-1)
                                    break;
                                waitingTimes = stopsArray[stopNames.indexOf(buses[2].passStops.get(i))].waitingTime(inputNum);
                                Connection connection = null;
                                try {

                                    Class.forName("com.mysql.jdbc.Driver");
                                    String jdbcUrl = String.format("jdbc:mysql://34.92.5.65:3306/culife");
                                    Log.d("Tsai", "getting connection btn2");
                                    connection = DriverManager.getConnection(jdbcUrl, "root", "carlos0923=-=");
                                    Log.d("Tsai", "connection set btn2");
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                } catch (ClassNotFoundException ex) {
                                    ex.printStackTrace();
                                }
                                try {
                                    Statement st = connection.createStatement();
                                    String sql = "SELECT * FROM busInfo WHERE busNum='3' and stopIndex between " +
                                            (i - 4) + " and " +
                                            (i - 1) +
                                            " ORDER BY id DESC LIMIT 1";
                                    ResultSet rs = st.executeQuery(sql);
                                    if (rs.next()) {

                                        objs[0] = rs.getLong("time");
                                        Log.d("Tsai", "onClick: " + (long) objs[0]);
                                        objs[1] = rs.getInt("stopIndex");
                                        connection.close();
                                    } else {
                                        Log.d("Tsai", "No such result");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (objs[0] != null) {
                                    timeFromDB = (long) objs[0];
                                    stopIndexOfData = (int) objs[1];
                                    Log.d("Tsai", "Got it btn2");
                                    int travelTime = (int) (60 * buses[2].estimateTime(buses[2].passStops.get(stopIndexOfData), buses[2].passStops.get(i)));
                                    long now = System.currentTimeMillis();
                                    long bestTimeInSeconds = (timeFromDB - now) / 1000 + travelTime;
                                    double errorAllowance = (i - stopIndexOfData) * 2.0;
                                    double tempConverter = (double) bestTimeInSeconds;
                                    bestTime = tempConverter / 60.0;
                                    if (bestTime < -errorAllowance)
                                        bestTime = waitingTimes.get("3");
                                } else {
                                    bestTime = waitingTimes.get("3");
                                    Log.d("Tsai", "Miss it btn2");
                                }
                                NumberFormat nf = NumberFormat.getNumberInstance();
                                // 保留一位小数
                                nf.setMaximumFractionDigits(1);
                                nf.setRoundingMode(RoundingMode.UP);
                                data[i-indexOfCurrentStop-1]=bestTime;
                            }
                            if(data[3]!=0.0){
                                Connection connection = null;
                                try {

                                    Class.forName("com.mysql.jdbc.Driver");
                                    String jdbcUrl = String.format("jdbc:mysql://34.92.5.65:3306/culife");
                                    Log.d("Tsai", "getting connection btn2 w");
                                    connection = DriverManager.getConnection(jdbcUrl, "root", "carlos0923=-=");
                                    Log.d("Tsai", "connection set btn2 w"+data[0]+" "+data[1]+" "+data[2]);
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                } catch (ClassNotFoundException ex) {
                                    ex.printStackTrace();
                                }
                                try {
                                    Statement st = connection.createStatement();

                                    String sql = "INSERT INTO test(now,one,two,three,four) VALUES('"+System.currentTimeMillis()+ "','"+data[0]+"','"+data[1]+"','"+data[2]+"','"+data[3]+"')";
                                    int result = st.executeUpdate(sql);
                                    connection.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }


                    }});
                thread.start();
            }
            });

    }
}


