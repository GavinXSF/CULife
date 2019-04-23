package xsf_cym.culife;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
        String[] text  = new String[]{
                "07:40 — 18:40\n"+"Every 20, 40 minute of the hour",
                "08:00 — 18:00\n"+" Every 00 minute of the hour",
                "07:45 — 18:45\n"+"Every *00, 15, 30, *45 minute of the hour",
                "09:00 — 18:40\n"+"Every 00, 20, 40 minute of the hour",
                "07:30 — 18:50\n"+"Every 10, 30, 50 minute of the hour",
                "08:50 — 17:50\n"+"Every 18, *25, 50 minute of the hour",
                "09:10 — 17:10\n"+"Every 10 minute of the hour",
                "12:20 — 17:20\n"+"Every 20 minute of the hour",
                "08:18 — 17:50\n"+"Every 18, 50 minute of the hour",
                "07:40 — 18:40\n"+"Every 00, 20, 40 minute of the hour",
                "19:00 — 23:30\n"+" Every *00, 15, 30, 45 minute of the hour",
                "08:20 — 23:20\n"+"Every *00, 20, 40 minute of the hour"
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


        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setIcon(R.drawable.bus);

        String[] contents = new String[]{
                "University MTR Station\n" +
                "University Sports Centre\n" +
                "Sir Run Run Shaw Hall\n" +
                "University Administration Building\n" +
                "S.H. Ho College\n" +
                "University MTR Station",

                "University MTR Station\n" +
                        "University Sports Centre\n" +
                        "*Sir Run Run Shaw Hall\n" +
                        "Fung King-hey Building\n" +
                        "United College\n" +
                        "New Asia College\n" +
                        "United College\n" +
                        "University Administration Building\n" +
                        "S.H. Ho College\n" +
                        "University MTR Station",

                "University MTR Station\n" +
                        "Jockey Club Postgraduate Hall\n" +
                        "University Sports Centre\n" +
                        "Sir Run Run Shaw Hall\n" +
                        "University Administration Building\n" +
                        "S.H. Ho College\n" +
                        "Jockey Club Postgraduate Hall\n" +
                        "University MTR Station",

                "Yasumoto International Academic Park\n" +
                        "University Sports Centre\n" +
                        "Science Centre\n" +
                        "Fung King-hey Building\n" +
                        "Residences 3 & 4\n" +
                        "Shaw College\n" +
                        "C.W. Chu College\n" +
                        "Residence 15\n" +
                        "United College Staff Residence\n" +
                        "Chan Chun Ha Hostel\n" +
                        "Shaw College\n" +
                        "Residences 3 & 4\n" +
                        "University Administration Building\n" +
                        "S.H. Ho College\n" +
                        "University MTR Station Piazza",

                "Yasumoto International Academic Park\n" +
                        "Campus Circuit East\n" +
                        "C.W. Chu College\n" +
                        "Area 39\n" +
                        "C.W. Chu College\n" +
                        "Residence 15\n" +
                        "United College Staff Residence\n" +
                        "Chan Chun Ha Hostel\n" +
                        "Shaw College\n" +
                        "Residences 3 & 4\n" +
                        "New Asia College\n" +
                        "United College\n" +
                        "University Administration Building\n" +
                        "S.H. Ho College\n" +
                        "University MTR Station",

                "Chung Chi Teaching Blocks\n" +
                        "University Sports Centre\n" +
                        "Sir Run Run Shaw Hall\n" +
                        "Fung King-hey Building\n" +
                        "United College\n" +
                        "New Asia College\n" +
                        "Residences 3 & 4\n" +
                        "Shaw College\n" +
                        "*C.W. Chu College",

                "C.W. Chu College\n" +
                        "United College Staff Residence\n" +
                        "Chan Chun Ha Hostel\n" +
                        "Residences 3 & 4\n" +
                        "New Asia College\n" +
                        "United College\n" +
                        "University Administration Building\n" +
                        "S.H. Ho College\n" +
                        "University MTR Station Piazza\n" +
                        "Chung Chi Teaching Blocks",

                "New Asia College\n" +
                        "United College\n" +
                        "University Administration Building\n" +
                        "S.H. Ho College\n" +
                        "University MTR Station Piazza\n" +
                        "Chung Chi Teaching Blocks",

                "Shaw College\n" +
                        "Residences 3 & 4\n" +
                        "New Asia College\n" +
                        "United College\n" +
                        "University Administration Building\n" +
                        "S.H. Ho College\n" +
                        "University MTR Station Piazza\n" +
                        "Chung Chi Teaching Blocks",

                "Area 39\n" +
                        "C.W. Chu College\n" +
                        "United College Staff Residence\n" +
                        "Chan Chun Ha Hostel\n" +
                        "Shaw College\n" +
                        "Residences 3 & 4\n" +
                        "University Administration Building\n" +
                        "Science Centre\n" +
                        "New Asia College\n" +
                        "United College\n" +
                        "Residences 3 & 4\n" +
                        "Shaw College\n" +
                        "Area 39\n" +
                        "Campus Circuit North\n" +
                        "Campus Circuit East\n" +
                        "University MTR Station",

                "University MTR Station\n" +
                        "*Jockey Club Postgraduate Hall\n" +
                        "University Sports Centre\n" +
                        "Sir Run Run Shaw Hall\n" +
                        "New Asia College\n" +
                        "United College\n" +
                        "Residences 3 & 4\n" +
                        "Shaw College\n" +
                        "Area 39\n" +
                        "C.W. Chu College\n" +
                        "Residence 15\n" +
                        "United College Staff Residence\n" +
                        "Chan Chun Ha Hostel\n" +
                        "Shaw College\n" +
                        "Residences 3 & 4\n" +
                        "New Asia College\n" +
                        "United College\n" +
                        "University Administration Building\n" +
                        "S.H. Ho College",

                "University MTR Station\n" +
                        "University Sports Centre\n" +
                        "Sir Run Run Shaw Hall\n" +
                        "New Asia College\n" +
                        "United College\n" +
                        "Residences 3 & 4\n" +
                        "Shaw College\n" +
                        "*Area 39\n" +
                        "C.W. Chu College\n" +
                        "Residence 10\n" +
                        "Residence 15\n" +
                        "United College Staff Residence\n" +
                        "Chan Chun Ha Hostel\n" +
                        "Shaw College\n" +
                        "Residences 3 & 4\n" +
                        "New Asia College\n" +
                        "United College\n" +
                        "University Administration Building\n" +
                        "S.H. Ho College\n" +
                        "University MTR Station"};

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                alertDialogBuilder.setTitle(title[position]);
                alertDialogBuilder.setMessage(contents[position]);
                alertDialogBuilder.setCancelable(true);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            }
        });
    }

}