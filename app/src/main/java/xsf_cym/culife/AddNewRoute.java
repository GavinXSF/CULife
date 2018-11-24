package xsf_cym.culife;


import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class AddNewRoute extends AppCompatActivity {
    private String startStop;
    private String endStop;
    private String startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_route);

        ActionBar topBar = getSupportActionBar();
        topBar.hide();

        TextView updateTopbar = findViewById(R.id.topbar_textview);
        updateTopbar.setText("Add New Route");

        Intent intent = getIntent();
        ArrayList<String> stopNames = (ArrayList<String>) intent.getStringArrayListExtra("stop_names");
        final String[] stopData = stopNames.toArray(new String[stopNames.size()]);
        final String[] timeHours = getResources().getStringArray(R.array.timePoint_hour);
        final String[] timeMins = getResources().getStringArray(R.array.timePoint_min);

        final Spinner start_stop = (Spinner) findViewById(R.id.startpos_spinner);
        ArrayAdapter<String> start_stop_adpter = new ArrayAdapter<String>(AddNewRoute.this,android.R.layout.simple_spinner_item,stopData);
        start_stop.setAdapter(start_stop_adpter);
        start_stop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                startStop = stopData[position];
//                Toast.makeText(AddNewRoute.this, "You clicked:"+clickStop, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner end_stop = (Spinner) findViewById(R.id.endstops_spinner);
        ArrayAdapter<String> end_stop_adapter = new ArrayAdapter<String>(AddNewRoute.this,android.R.layout.simple_spinner_item,stopData);
        end_stop.setAdapter(end_stop_adapter);
        end_stop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                endStop = stopData[position];
//                Toast.makeText(AddNewRoute.this, "You clicked:"+clickStop, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Spinner timeHour = (Spinner) findViewById(R.id.start_time_hours);
        ArrayAdapter<String> hour_adapter = new ArrayAdapter<String>(AddNewRoute.this,android.R.layout.simple_spinner_item,timeHours);
        timeHour.setAdapter(hour_adapter);
        timeHour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                startTime = timeHours[position];
//                Toast.makeText(AddNewRoute.this, "You clicked:"+clickStop, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner timeMin = (Spinner) findViewById(R.id.start_time_minutes);
        ArrayAdapter<String> min_adapter = new ArrayAdapter<String>(AddNewRoute.this,android.R.layout.simple_spinner_item,timeMins);
        timeMin.setAdapter(min_adapter);
        timeMin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                startTime += timeMins[position];
//                Toast.makeText(AddNewRoute.this, "You clicked:"+clickStop, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button btn1 = (Button) findViewById(R.id.button1);
        btn1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v)  {
               File newFile = new File(AddNewRoute.this.getExternalFilesDir(null).getAbsolutePath());
               try {
                   FileWriter writer = new FileWriter(newFile, true);
                   BufferedWriter bfWriter = new BufferedWriter(writer);
                   bfWriter.write(startTime);
                   bfWriter.newLine();
                   bfWriter.write(startStop);
                   bfWriter.newLine();
                   bfWriter.write(endStop);
                   bfWriter.newLine();
               }
               catch(Exception e){

               }
               Intent myRoute = new Intent(AddNewRoute.this,MyRoute.class);
               startActivity(myRoute);
           }
         });

    }

}
