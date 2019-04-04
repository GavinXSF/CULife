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
                            double timeFromDB = -1.0;
                            //todo: search for info in mysql; timeFromDB = currentTime - (found time + interval) (if valid time was found)

                            if(timeFromDB==-1.0)
                                bestTime = waitingTimes.get(busNum);
                            else
                                bestTime = timeFromDB;

                            stopInfo.add("Line "+ busNum + ": " + bestTime + " mins\n" );
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
