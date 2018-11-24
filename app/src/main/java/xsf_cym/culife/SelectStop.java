package xsf_cym.culife;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class SelectStop extends AppCompatActivity {
    private EditText inputText;
    private Button btn;
    private int inputNum;
    private ArrayList<String> stopInfo;

    public SelectStop() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_stop);
        Intent intent = getIntent();
        final BusStop[] stopsArray = (BusStop[]) intent.getSerializableExtra("stops");
        final ArrayList<String> stopNames = (ArrayList<String>) intent.getStringArrayListExtra("stop_names");
        inputText = (EditText) findViewById(R.id.inputTime);
        btn = (Button) findViewById(R.id.btn1);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp;
                temp = inputText.getText().toString();
                inputNum = Integer.parseInt(temp);
                stopInfo = stopsArray[3].waitingTime(inputNum);
                Intent stop_info = new Intent(SelectStop.this, StopInfo.class);
                stop_info.putExtra("stops_info",stopInfo);
                startActivity(stop_info);
            }
        });

    }
}
