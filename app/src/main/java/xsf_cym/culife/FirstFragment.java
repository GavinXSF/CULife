package xsf_cym.culife;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static android.content.Context.LOCATION_SERVICE;


public class FirstFragment extends Fragment {
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
    private int inputNum;
    private HashMap<String, Double> waitingTimes;
    private ArrayList<String> stopInfo = new ArrayList<String>();
    private ArrayList<String> timeInfo = new ArrayList<String>();
    private int selectedStop = -1;
    private ArrayList<String> busNumList = new ArrayList<String>();
    private ArrayList<Integer> stopIndexList = new ArrayList<Integer>();
    private ArrayList<Boolean> isDueList = new ArrayList<Boolean>();

    public static FirstFragment newInstance() {
        return new FirstFragment();
    }


    private Location getBestLocation(LocationManager locationManager) {
        Location result = null;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED ) {
            // 待解决, 在服务中若检查到没有权限怎么办
            Toast.makeText(getActivity(),"没有权限（多次更新）",Toast.LENGTH_SHORT).show();
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






    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FirstFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FirstFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstFragment newInstance(String param1, String param2) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



        getActivity().setTitle("Select stop");

        mListView1 = (ListView) getActivity().findViewById(R.id.myListView);
        myAdapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,stopInfo);

        mListView1.setAdapter(myAdapter);
        mListView2 = (ListView) getActivity().findViewById(R.id.myListView2);
        MyAdapter myAdapter2 = new MyAdapter(timeInfo,busNumList,stopIndexList,isDueList);

        mListView2.setAdapter(myAdapter2);


        Intent intent = getActivity().getIntent();
        final BusStop[] stopsArray = (BusStop[]) intent.getSerializableExtra("stops");
        final ArrayList<String> stopNames = (ArrayList<String>) intent.getStringArrayListExtra("stop_names");
        final Bus[] buses = (Bus[]) intent.getSerializableExtra("buses");
        final String[] stopNamesArray = stopNames.toArray(new String[stopNames.size()]);
//        Log.d("Tsai",stopNamesArray[3]);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else {


            locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            List<String> providers = locationManager.getProviders(true);
            if (providers.contains(LocationManager.GPS_PROVIDER)) {
                //如果是GPS
                locationProvider = LocationManager.GPS_PROVIDER;
            } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                //如果是Network
                locationProvider = LocationManager.NETWORK_PROVIDER;
            } else {
                Toast.makeText(getActivity(), "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
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
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, stopNamesArray);
            mySpinner = (Spinner) getActivity().findViewById(R.id.stopsSpinner);
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



        btn = (Button) getActivity().findViewById(R.id.btn1);
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
                    Toast.makeText(getActivity(), "Wrong input\nInput format:HHmm", Toast.LENGTH_SHORT).show();
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
                                        nf.setRoundingMode(RoundingMode.UP);
                                        if(bestTime>0.0){
                                            timeInfo.add("Line " + busNum + ": " + nf.format(bestTime) + " mins\n");
                                            busNumList.add(busNum);
                                            stopIndexList.add(buses[busIndex.get(busNum)].passStops.indexOf(stopsArray[selectedStop].stopName));
                                            isDueList.add(false);
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




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
