package xsf_cym.culife;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Set;

public class SelectStop extends AppCompatActivity {
    private Spinner mySpinner;
    private EditText inputText;
    private Button btn;
    private int inputNum;
    private HashMap<String, Double> waitingTimes;
    private ArrayList<String> stopInfo;
    private int selectedStop = -1;
    public SelectStop() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_stop);
        ActionBar topBar = getSupportActionBar();

            topBar.hide();

        TextView updateTopbar = findViewById(R.id.topbar_textview);
        updateTopbar.setText("Select Stop");



        Intent intent = getIntent();
        final BusStop[] stopsArray = (BusStop[]) intent.getSerializableExtra("stops");
        final ArrayList<String> stopNames = (ArrayList<String>) intent.getStringArrayListExtra("stop_names");
        final Bus[] buses = (Bus[]) intent.getSerializableExtra("buses");
        final String[] stopNamesArray = stopNames.toArray(new String[stopNames.size()]);
//        Log.d("Tsai",stopNamesArray[3]);


        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(SelectStop.this,R.layout.support_simple_spinner_dropdown_item,stopNamesArray);
        mySpinner = (Spinner) findViewById(R.id.stopsSpinner);
        mySpinner.setAdapter(spinnerAdapter);
        //choose the nearest one by default
        double min = 999;
        int nearest=-1;
        for (int i = 0; i < stopsArray.length;i++){   // 为了运行测试暂时注释掉, 之后传递userLatitude之后再还原
//            if((stopsArray[i].Latitude!=null)&&(stopsArray[i].Longitude!=null)){
//                double distanceSquare = (stopsArray[i].Latitude-userLatitude)*(stopsArray[i].Latitude-userLatitude)
//                        +(stopsArray[i].Longitude-userLongitude)*(stopsArray[i].Longitude-userLongitude);
//                if(distanceSquare<min){
//                    min = distanceSquare;
//                    nearest = i;
//                }
//            }
        }
        mySpinner.setSelection(nearest);
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStop = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        inputText = (EditText) findViewById(R.id.inputTime);
        Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minutes = calendar.get(Calendar.MINUTE);
        inputText.setText((hour*100+minutes)+"");
        btn = (Button) findViewById(R.id.btn1);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp;
                temp = inputText.getText().toString();
                inputNum = Integer.parseInt(temp);
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
                        waitingTimes = stopsArray[selectedStop].waitingTime(inputNum);
                        Set<String> keys=waitingTimes.keySet();
                        Iterator<String> iterator1=keys.iterator();
                        while (iterator1.hasNext()){
                            String busNum = iterator1.next();
                            double bestTime;
                            long timeFromDB = -1;
                            int stopIndexOfData=-1;
                            //todo: search for info in mysql; timeFromDB = currentTime - (found time + interval) (if valid time was found)
                            Connection connection = null;
                            try {

                                Class.forName("com.mysql.jdbc.Driver");
                                String jdbcUrl = String.format("jdbc:mysql://34.92.5.65:3306/culife");

                                connection = DriverManager.getConnection(jdbcUrl, "root", "carlos0923=-=");
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            } catch (ClassNotFoundException ex) {
                                ex.printStackTrace();
                            }
                            try{
                                Statement st = connection.createStatement();
                                String sql = "SELECT * FROM busInfo WHERE busNum='"+busNum+"' and stopIndex between "+
                                        (buses[busIndex.get(busNum)].passStops.indexOf(stopsArray[selectedStop].stopName)-3) +" and "+
                                        (buses[busIndex.get(busNum)].passStops.indexOf(stopsArray[selectedStop].stopName)-1)+
                                        "ORDER BY id DESC LIMIT 1";
                                ResultSet rs = st.executeQuery(sql);
                                rs.next();
                                timeFromDB = rs.getLong("time");
                                stopIndexOfData = rs.getInt("stopIndex");
                                connection.close();
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                            if(timeFromDB==-1)
                                bestTime = waitingTimes.get(busNum);
                            else{
                                int travelTime = (int)(60*buses[busIndex.get(busNum)].estimateTime(buses[busIndex.get(busNum)].passStops.get(stopIndexOfData),stopsArray[selectedStop].stopName));
                                long now = System.currentTimeMillis();
                                long bestTimeInSeconds = (timeFromDB-now)/1000 +travelTime;
                                double tempConverter = (double)bestTimeInSeconds;
                                bestTime = tempConverter/60.0;
                                if(bestTime<0.0)
                                    bestTime = waitingTimes.get(busNum);
                            }
                            NumberFormat nf = NumberFormat.getNumberInstance();
                            // 保留一位小数
                            nf.setMaximumFractionDigits(1);
                            nf.setRoundingMode(RoundingMode.UP);
                            stopInfo.add("Line "+ busNum + ": " + nf.format(bestTime) + " mins\n" );
                        }

                        stopInfo.add(0, stopNamesArray[selectedStop]);
                        if((inputNum%100)<10)
                            stopInfo.add(1, inputNum/100 + ":0" + inputNum%100);
                        else
                            stopInfo.add(1, inputNum/100 + ":" + inputNum%100);
//                        stopInfo.add(1,"Lat:"+stopsArray[selectedStop].Latitude);
                        Intent stop_info = new Intent(SelectStop.this, StopInfo.class);
                        stop_info.putExtra("stops_info", stopInfo);
                        startActivity(stop_info);
                    }
                }
            }
        });

    }
}
