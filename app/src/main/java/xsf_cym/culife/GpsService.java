package xsf_cym.culife;

import android.Manifest;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

public class GpsService extends IntentService {
    private LatLng mLatLng;
    private LocationManager locationManager;
    private String locationProvider;
    private Location location;
    private LocationListener locationListener;
    private double latitude;
    private double longitude;


    public GpsService() {
        super("GpsService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    public void onCreate(){
        super.onCreate();


    }

    @Override
    public void onHandleIntent(Intent intent){
        BusStop[] stopsArray = (BusStop[]) intent.getSerializableExtra("stops");
        Bus[] buses = (Bus[]) intent.getSerializableExtra("buses");
        ArrayList<String> possibleBuses = new ArrayList<String>();
        final Object[] objs = new Object[1];
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED ) {
            // 待解决, 在服务中若检查到没有权限怎么办
//            ActivityCompat.requestPermissions(GpsService.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            List<String> providers = locationManager.getProviders(true);
            if (providers.contains(LocationManager.GPS_PROVIDER)) {
                //如果是GPS
                locationProvider = LocationManager.GPS_PROVIDER;
            } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                //如果是Network
                locationProvider = LocationManager.NETWORK_PROVIDER;
            } else {
                Toast.makeText(this,"没有可用的位置提供器",Toast.LENGTH_SHORT).show();
            }
            // 检查权限

            // 得到location object
            location = locationManager.getLastKnownLocation(locationProvider);
            //监视地理位置变化
            locationManager.requestLocationUpdates(locationProvider, 2000, 15, locationListener);
            latitude = location.getLatitude();
            longitude = location.getLongitude();

//            if (location != null) {
//                //不为空
//            }
            // add LocationListener
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
                        if(lastLocation!=null){
                            calculatedSpeed = 1000 * lastLocation.distanceTo(location)/(location.getTime()-lastLocation.getTime());
                        }
                        this.lastLocation=location;
                        objs[0]=calculatedSpeed;
                }};
            int count = 0;
            int stopIndex = -1;
            boolean atBusstop = false;
            while(true){  //firstly, check if the user is at one of the bus stop
                for(int i = 0; i <stopsArray.length;i++ ){
                    if(stopsArray[i].Longitude!=null){
//                        double deltaLng = location.getLongitude()-stopsArray[i].Longitude;
//                        double deltaLat = location.getLatitude()-stopsArray[i].Latitude;
//                        double distance = Math.sqrt(deltaLat*deltaLat + deltaLng*deltaLng);
                        Location stopLocation = new Location("");
                        stopLocation.setLatitude(stopsArray[i].Latitude);
                        stopLocation.setLongitude(stopsArray[i].Longitude);
                        double distance = location.distanceTo(stopLocation);
                        if(distance<10.0){
                            stopIndex = i;
                            atBusstop = true;
                            break;
                        }
                    }
                }
                if (atBusstop == true)
                    break;
                else
                    count++;
                try{
                Thread.sleep(10000);}              //do the checking once every 10 seconds
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                //if user doesn't go to the bus stop in 5 minutes, stop tracking their gps
                if(count>30)
                    stopSelf();
            }
            //second whilt loop: find out when the user get on the bus
            count = 0;
            while (true){
                long getOnTime = 0;
                double userSpeed = (double)objs[0];
                if(userSpeed>4){

                    //possible problem: if a user pass by a bus stop and take other forms of transportation
                    // check location again
                    if(stopIndex!=-1) {
                        Location stopLocation = new Location("");

                        stopLocation.setLatitude(stopsArray[stopIndex].Latitude);
                        stopLocation.setLongitude(stopsArray[stopIndex].Longitude);
                        double distance = location.distanceTo(stopLocation);
                        if(distance<30) {       //if the user get on the bus at that stop
                            getOnTime = location.getTime();//milliseconds since epoch
                            Date date = new Date(getOnTime);
                            DateFormat formatter = new SimpleDateFormat("HHmm");
                            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
                            String dateFormatted = formatter.format(date);
                            int time;
                            try {
                                time = Integer.parseInt(dateFormatted);
                            }
                            catch (NumberFormatException e)
                            {
                                time = -1;
                                Log.d("Tsai", "wrong date format");
                            }
                            HashMap<String, Double> waitingTimes = stopsArray[stopIndex].waitingTime(time);
                            //todo: Check the time stores in stops[stopIndex] and the database, find out the possible buses
                            Set<String> keys=waitingTimes.keySet();
                            Iterator<String> iterator1=keys.iterator();
                            while (iterator1.hasNext()){
                                boolean flag = false;
                                String busNum = iterator1.next();
                                double bestTime;
                                double timeFromDB = -1.0;
                                //todo: search for info in mysql; check if either timeFromDB+intervals<current time<that+errors or -yy<waitingTime<xx

                                if((timeFromDB==-1.0)||flag==false)
//                                    check by waiting time

                                if(flag)
                                    possibleBuses.add(busNum);

                            }
                            break;
                        }
                        else
                            stopSelf();  //if the user start moving in a high speed at somewhere else, stop tracking their gps
                    }
                    else
                        stopSelf();
                }
                count++;
                if(count>120)  // if the user doesn't get on the bus in 10 mins, stop tracking their gps
                    stopSelf();
                try{
                    Thread.sleep(5000);}              //do the checking once every 5 seconds
                catch (InterruptedException e){
                    e.printStackTrace();
                }

            }
            //third while loop: find out which bus the user take, and use their gps info to update bus info in our database
            count = 0;
            String lastStop = stopsArray[stopIndex].stopName;
            while(true){
                double userSpeed = (double)objs[0];
                int the_index = -1;
                if(userSpeed<1){
                    int currentStop = -1;
                    if(possibleBuses.size()>1){
                        //compare current location with all stops, if not at a stop, sleep and continue
                        for(int i = 0; i <stopsArray.length;i++ ){
                            if(stopsArray[i].Longitude!=null){
//                                double deltaLng = location.getLongitude()-stopsArray[i].Longitude;
//                                double deltaLat = location.getLatitude()-stopsArray[i].Latitude;
//                                double distance = Math.sqrt(deltaLat*deltaLat + deltaLng*deltaLng);
                                Location stopLocation = new Location("");
                                stopLocation.setLatitude(stopsArray[i].Latitude);
                                stopLocation.setLongitude(stopsArray[i].Longitude);
                                double distance = location.distanceTo(stopLocation);
                                if(distance<10.0){
                                    currentStop = i;
                                    break;
                                }
                            }
                        }
                        if(currentStop == -1){   //if the user stop at somewhere other than a bus stop
                            try{
                                Thread.sleep(5000);}              //do the checking once every 5 seconds
                            catch (InterruptedException e){
                                e.printStackTrace();
                            }
                            count++;
                            continue;
                        }
                        for(int i = 0;i<possibleBuses.size();i++){
                            //compare stops[i].stopName with next stop of the possible buses
                            //if they don't match, delete this bus from the list(and i--?)
                        }
                        if(possibleBuses.size() == 1){
                            the_index = buses[index_of_this_bus].passStops.indexOf(stopsArray[currentStop].stopName);
                            //write to mysql

                            continue;
                        }
                        //if there are still more than 1 possible buslines
                        else if (possibleBuses.size() > 1){
                            lastStop = stopsArray[currentStop].stopName;
                        }
                    }
                    if(possibleBuses.size() == 1){


//                        double deltaLng = location.getLongitude()-stopsArray[the_index].Longitude;
//                        double deltaLat = location.getLatitude()-stopsArray[the_index].Latitude;
//                        double distance = Math.sqrt(deltaLat*deltaLat + deltaLng*deltaLng);

                        // check if the bus still stop at the same stop
                        Location stopLocation = new Location("");
                        stopLocation.setLatitude(stopsArray[the_index].Latitude);
                        stopLocation.setLongitude(stopsArray[the_index].Longitude);
                        double distance = location.distanceTo(stopLocation);
                        if(distance<20.0) {
                            //write to mysql
                            count = 0; //if the user stop at a bus stop, clear the count value
                            continue;
                        }
                        //if not, check if it arrives at another stpo
                        stopLocation.setLatitude(stopsArray[the_index+1].Latitude);
                        stopLocation.setLongitude(stopsArray[the_index+1].Longitude);
                        distance = location.distanceTo(stopLocation);
                        if(distance<20.0){
                            //write to mysql
                            the_index++;
                            count = 0;      //if the user stop at a bus stop, clear the count value
                            continue;
                        }
                        else{
                            count++;
                        }
                        //if the user stop at somewhere other than a bus stop for 30 times in a row, stop tracking their location
                        if(count>30){
                            stopSelf();
                        }

                    }
                    if(possibleBuses.size()==0){
                        stopSelf();
                    }
                }

                else if(userSpeed>4) {
                    count = 0;  // if user move at the speed of car, clear
                }
                else {
                    count++;
                }
                if(count>30){
                    stopSelf();
                }

                try{
                    Thread.sleep(5000);}              //do the checking once every 5 seconds
                catch (InterruptedException e){
                    e.printStackTrace();
                }
            }

        }


    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (locationManager != null) {
            // 关闭程序时移除监听器
            locationManager.removeUpdates(locationListener);
        }
    }


}
