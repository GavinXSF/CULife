//package xsf_cym.culife;
//
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.text.NumberFormat;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Set;
//
//
///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link MyRouteFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link MyRouteFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class MyRouteFragment extends Fragment {
//    private ArrayList<String> startTime = new ArrayList<String>();
//    private ArrayList<String> startStop = new ArrayList<String>();
//    private ArrayList<String> endStop = new ArrayList<String>();
//    private ListView myListView;
//    private ArrayAdapter myAdapter;
//    private HashMap<String,Double> waitingTimes = new HashMap<String, Double>();
//    public static Object lock=new Object();
//    private Integer myI;
//
//
//    public MyRouteFragment() {
//        // Required empty public constructor
//    }
//
//
//    public static MyRouteFragment newInstance(String param1, String param2) {
//        MyRouteFragment fragment = new MyRouteFragment();
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        getActivity().setTitle("My routes");
//
//        Intent intent = getIntent();
//
//
//        BusStop[] stopsArray = (BusStop[]) intent.getSerializableExtra("stops");
//        Bus[] buses = (Bus[]) intent.getSerializableExtra("buses");
//        ArrayList<String> stopNames = (ArrayList<String>) intent.getStringArrayListExtra("stop_names");
//
//        File newFile = new File(getActivity().getFilesDir().getAbsolutePath(), "my_route.txt");
//        FileReader fileReader = null;
//        BufferedReader bufferedReader = null;
//        try{
//            fileReader = new FileReader(newFile);
//            bufferedReader = new BufferedReader(fileReader);
//            String line;
//            int tag = 0;
//            while((line = bufferedReader.readLine())!= null){
//                if(tag == 0)
//                    startTime.add(line);
//                if(tag == 1)
//                    startStop.add(line);
//                if(tag == 2)
//                    endStop.add(line);
//                tag = (tag + 1) % 3;
//            }
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//        finally {
//            try {
//                if (bufferedReader != null) {
//                    bufferedReader.close();
//                }
//                if (fileReader != null) {
//                    fileReader.close();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//
//        //Log.d("Tsai", startTime.size()+"");
//
//        ArrayList<String> displayInfo = new ArrayList<String>();
//        ArrayList<String> firstHalf = new ArrayList<String>();
//
//        if(startTime.size()!=startStop.size() || startStop.size()!= endStop.size()){
//            Log.e("Tsai", "File broken");
////            MyRoute.this.finish();
//            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
//
//        }
//        else {
//            Route[] routes = new Route[startStop.size()];
//
//            NumberFormat nf = NumberFormat.getNumberInstance();
//            // 保留一位小数
//            nf.setMaximumFractionDigits(1);
//            nf.setMinimumFractionDigits(1);
//
//            HashMap<String,Integer> busIndex = new HashMap<String, Integer>();
//
//            busIndex.put("1A",0);
//            busIndex.put("2",1);
//            busIndex.put("3",2);
//            busIndex.put("4",3);
//            busIndex.put("5",4);
//            busIndex.put("6A",5);
//            busIndex.put("7",6);
//            busIndex.put("8",7);
//            busIndex.put("N",8);
//            busIndex.put("H",9);
//            busIndex.put("6B",10);
//            busIndex.put("1B",11);
//
//            for(int i = 0; i < startStop.size(); i++){
//
//                routes[i] = new Route(Integer.parseInt((String)startTime.get(i)),
//                        (String)startStop.get(i),(String)endStop.get(i));
//                routes[i].computeLine(stopsArray,buses);
//                myI = i;
//
//                waitingTimes = stopsArray[stopNames.indexOf(routes[myI].startPosition)].waitingTime(routes[myI].startTime, routes[myI].validBus);
//                Set<String> keys = waitingTimes.keySet();
//                Iterator<String> iterator1 = keys.iterator();
//                while (iterator1.hasNext()) {
//                    String busNum = iterator1.next();
//                    double bestTime;
//                    bestTime = waitingTimes.get(busNum);
//                    if (bestTime > 0.0)
//                        firstHalf.add("Line " + busNum + ": " + nf.format(bestTime) + " mins\nEstimated travel time: ");
//                    else
//                        firstHalf.add("Line " + busNum + ": Due\nEstimated travel time: ");
//                }
//
//                //     Log.d("Tsai", stopsArray[stopNames.indexOf(routes[i].destination)].stopName +" "+routes[i].startPosition+" "+routes[i].destination+"  "+routes[i].validBus[0]+" "+routes[i].validBus[1]+" "+routes[i].validBus[2]);
//                displayInfo.add("Route"+(i+1)+ "\n" + routes[i].startPosition + " → \n" + routes[i].destination + "\n" + routes[i].startTime/100 + ":" + String.format("%0"+2+"d", routes[i].startTime%100));
//                int index = 0;
//
//                for(int j = 0; j < firstHalf.size(); j++){
//                    for(int k = index; k < 12; k++){
//                        if(routes[i].validBus[k]){
//                            index = k + 1;
//                            break;
//                        }
//                    }
//                    displayInfo.add(firstHalf.get(j)+ nf.format(buses[index - 1].estimateTime(routes[i].startPosition,routes[i].destination)) + " mins");
//                }
//            }
//        }
//        myListView = (ListView) getActivity().findViewById(R.id.myListView);
//        myAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,displayInfo);
//        myListView.setAdapter(myAdapter);
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_my_route, container, false);
//    }
//
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
//}
