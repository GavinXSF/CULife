package xsf_cym.culife;

import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddNewRoute extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_route);

        final String[] stopData = this.getResources().getStringArray(R.array.stopName);
        final String[] timeHours = getResources().getStringArray(R.array.timePoint_hour);
        final String[] timeMins = getResources().getStringArray(R.array.timePoint_min);

        Spinner start_stop = (Spinner) findViewById(R.id.startpos_spinner);
        ArrayAdapter<String> start_stop_adpter = new ArrayAdapter<String>(AddNewRoute.this,android.R.layout.simple_spinner_item,stopData);
        start_stop.setAdapter(start_stop_adpter);
        start_stop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String clickStop = stopData[position];
                Toast.makeText(AddNewRoute.this, "You clicked:"+clickStop, Toast.LENGTH_SHORT).show();
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
                String clickStop = stopData[position];
                Toast.makeText(AddNewRoute.this, "You clicked:"+clickStop, Toast.LENGTH_SHORT).show();
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
                String clickStop = timeHours[position];
                Toast.makeText(AddNewRoute.this, "You clicked:"+clickStop, Toast.LENGTH_SHORT).show();
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
                String clickStop = timeMins[position];
                Toast.makeText(AddNewRoute.this, "You clicked:"+clickStop, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        Button testButton = (Button) findViewById(R.id.test_button);
//        testButton.setOnClickListener(new View.OnClickListener() {
//            int hour = 8, minute = 0;
//            @Override
//            public void onClick(View v) {
//                final String[] pickData = new String[2];
//
//                TimePickerDialog timePick = new TimePickerDialog(AddNewRoute.this, new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                        pickData[0] = String.valueOf(hourOfDay);
//                        pickData[1] = String.valueOf(minute);
//                    }
//                },hour,minute,true);
//                timePick.show();
//                Log.d("pickHours: ",pickData[0]);
//                Log.d("pickMin: ",pickData[1]);
//
//            }
//        });
    }

}
