package xsf_cym.culife;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainFragment extends Fragment {
    public static MainFragment newInstance() {
        return new MainFragment();
    }

    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState ) {


        View view = inflater.inflate(R.layout.fragment_main, container, false);

//        listView = getActivity().findViewById(R.id.raw_bus_list);



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

            AssetManager assetManager = getActivity().getAssets();

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


        Button SelectStop = view.findViewById(R.id.select_stop);
        Button AddNewRoute = view.findViewById(R.id.add_new_route);
        Button MyRoute = view.findViewById(R.id.my_route);



        SelectStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent select_stop = new Intent(getActivity(), SelectStop.class);
                select_stop.putExtra("stops",stopsArray);
                select_stop.putExtra("stop_names",initializedStops);
                select_stop.putExtra("buses",buses_final);
                startActivity(select_stop);
            }
        });

        AddNewRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add_new_route = new Intent(getActivity(), AddNewRoute.class);
                add_new_route.putExtra("stop_names",initializedStops);
                add_new_route.putExtra("stops",stopsArray);
                add_new_route.putExtra("buses",buses_final);
                startActivity(add_new_route);
            }
        });

        MyRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent my_route = new Intent(getActivity(), MyRoute.class);
                my_route.putExtra("stops",stopsArray);
                my_route.putExtra("buses",buses_final);
                my_route.putExtra("stop_names",initializedStops);
                startActivity(my_route);
            }
        });

        return view;

    }


    @Override
    public void onStart(){
        super.onStart();



        List<HashMap<String , String>> list = new ArrayList<>();
        //使用List存入HashMap，用來顯示ListView上面的文字。

        String[] title = new String[]{"1A","1B", "2","3","4","5","6A","6B", "7","8","N","H"};
        String[] text  = new String[]{"University MTR Station->" +
                "University Sports Centre->" +
                "Sir Run Run Shaw Hall->" +
                "University Administration Building->" +
                "S.H. Ho College->" +
                "University MTR Station",

                "University MTR Station->" +
                        "University Sports Centre->" +
                        "*Sir Run Run Shaw Hall->" +
                        "Fung King-hey Building->" +
                        "United College->" +
                        "New Asia College->" +
                        "United College->" +
                        "University Administration Building->" +
                        "S.H. Ho College->" +
                        "University MTR Station",

                "University MTR Station->" +
                        "Jockey Club Postgraduate Hall->" +
                        "University Sports Centre->" +
                        "Sir Run Run Shaw Hall->" +
                        "University Administration Building->" +
                        "S.H. Ho College->" +
                        "Jockey Club Postgraduate Hall->" +
                        "University MTR Station",

                "Yasumoto International Academic Park->" +
                        "University Sports Centre->" +
                        "Science Centre->" +
                        "Fung King-hey Building->" +
                        "Residences 3 & 4->" +
                        "Shaw College->->" +
                        "C.W. Chu College->" +
                        "Residence 15->" +
                        "United College Staff Residence->" +
                        "Chan Chun Ha Hostel->" +
                        "Shaw College->" +
                        "Residences 3 & 4->" +
                        "University Administration Building->" +
                        "S.H. Ho College->" +
                        "University MTR Station Piazza",

                "Yasumoto International Academic Park->" +
                        "Campus Circuit East->" +
                        "C.W. Chu College->" +
                        "Area 39->" +
                        "C.W. Chu College->" +
                        "Residence 15->" +
                        "United College Staff Residence->" +
                        "Chan Chun Ha Hostel->" +
                        "Shaw College->" +
                        "Residences 3 & 4->" +
                        "New Asia College->" +
                        "United College->" +
                        "University Administration Building->" +
                        "S.H. Ho College->" +
                        "University MTR Station",

                "Chung Chi Teaching Blocks->" +
                        "University Sports Centre->" +
                        "Sir Run Run Shaw Hall->" +
                        "Fung King-hey Building->" +
                        "United College->" +
                        "New Asia College->" +
                        "Residences 3 & 4->" +
                        "Shaw College->" +
                        "*C.W. Chu College",

                "C.W. Chu College->" +
                        "United College Staff Residence->" +
                        "Chan Chun Ha Hostel->" +
                        "Residences 3 & 4->" +
                        "New Asia College->" +
                        "United College->" +
                        "University Administration Building->" +
                        "S.H. Ho College->" +
                        "University MTR Station Piazza->" +
                        "Chung Chi Teaching Blocks",

                "New Asia College->->" +
                        "United College->" +
                        "University Administration Building->" +
                        "S.H. Ho College->" +
                        "University MTR Station Piazza->" +
                        "Chung Chi Teaching Blocks",

                "Shaw College->" +
                        "Residences 3 & 4->" +
                        "New Asia College->" +
                        "United College->" +
                        "University Administration Building->" +
                        "S.H. Ho College->" +
                        "University MTR Station Piazza->" +
                        "Chung Chi Teaching Blocks",

                "Area 39->" +
                        "C.W. Chu College->" +
                        "United College Staff Residence->" +
                        "Chan Chun Ha Hostel->" +
                        "Shaw College->" +
                        "Residences 3 & 4->" +
                        "University Administration Building->" +
                        "Science Centre->" +
                        "New Asia College->" +
                        "United College->" +
                        "Residences 3 & 4->" +
                        "Shaw College->" +
                        "Area 39->" +
                        "Campus Circuit North->" +
                        "Campus Circuit East->" +
                        "University MTR Station",

                "University MTR Station->" +
                        "*Jockey Club Postgraduate Hall->" +
                        "University Sports Centre->" +
                        "Sir Run Run Shaw Hall->" +
                        "New Asia College->" +
                        "United College->" +
                        "Residences 3 & 4->" +
                        "Shaw College->" +
                        "Area 39->" +
                        "C.W. Chu College->" +
                        "Residence 15->" +
                        "United College Staff Residence->" +
                        "Chan Chun Ha Hostel->" +
                        "Shaw College->" +
                        "Residences 3 & 4->" +
                        "New Asia College->" +
                        "United College->" +
                        "University Administration Building->" +
                        "S.H. Ho College",

                "University MTR Station->" +
                        "University Sports Centre->" +
                        "Sir Run Run Shaw Hall->" +
                        "New Asia College->" +
                        "United College->" +
                        "Residences 3 & 4->" +
                        "Shaw College->" +
                        "*Area 39->" +
                        "C.W. Chu College->" +
                        "Residence 10->" +
                        "Residence 15->" +
                        "United College Staff Residence->" +
                        "Chan Chun Ha Hostel->" +
                        "Shaw College->" +
                        "Residences 3 & 4->" +
                        "New Asia College->" +
                        "United College->" +
                        "University Administration Building->" +
                        "S.H. Ho College->" +
                        "University MTR Station"

        };
        for(int i = 0 ; i < title.length ; i++){
            HashMap<String , String> hashMap = new HashMap<>();
            hashMap.put("title" , title[i]);
            hashMap.put("text" , text[i]);
            //把title , text存入HashMap之中
            list.add(hashMap);
            //把HashMap存入list之中
        }

        listView = getActivity().findViewById(R.id.raw_bus_list);
        listView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        ListAdapter listAdapter = new SimpleAdapter(
                getActivity(),
                list,
                android.R.layout.simple_list_item_2 ,
                new String[]{"title" , "text"} ,
                new int[]{android.R.id.text1 , android.R.id.text2});
        // 5個參數 : context , List , layout , key1 & key2 , text1 & text2

        listView.setAdapter(listAdapter);
    }

}