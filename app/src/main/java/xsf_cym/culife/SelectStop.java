package xsf_cym.culife;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

public class SelectStop extends AppCompatActivity {
    private Spinner mySpinner;
    private EditText inputText;
    private Button btn;
    private int inputNum;
    private ArrayList<String> stopInfo;
    private int selectedStop = -1;
    public SelectStop() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_stop);
        Intent intent = getIntent();
        final BusStop[] stopsArray = (BusStop[]) intent.getSerializableExtra("stops");
        final ArrayList<String> stopNames = (ArrayList<String>) intent.getStringArrayListExtra("stop_names");
        String[] stopNamesArray = stopNames.toArray(new String[stopNames.size()]);
//        Log.d("Tsai",stopNamesArray[3]);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(SelectStop.this,R.layout.support_simple_spinner_dropdown_item,stopNamesArray);
        mySpinner = (Spinner) findViewById(R.id.stopsSpinner);
        mySpinner.setAdapter(spinnerAdapter);
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
        btn = (Button) findViewById(R.id.btn1);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp;
                temp = inputText.getText().toString();
                inputNum = Integer.parseInt(temp);
                if(selectedStop > -1) {
                    stopInfo = stopsArray[selectedStop].waitingTime(inputNum);
                    Intent stop_info = new Intent(SelectStop.this, StopInfo.class);
                    stop_info.putExtra("stops_info", stopInfo);
                    startActivity(stop_info);
                }
            }
        });

    }
}
