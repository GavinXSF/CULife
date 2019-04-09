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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
    private Location getBestLocation(LocationManager locationManager) {
        Location result = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED ) {
            // 待解决, 在服务中若检查到没有权限怎么办
            Toast.makeText(this,"没有权限（多次更新）",Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this,"没有权限（首次请求）",Toast.LENGTH_SHORT).show();
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

                    if(lastLocation!=null){
                        calculatedSpeed = 1000 * lastLocation.distanceTo(location)/(location.getTime()-lastLocation.getTime());
                        this.lastLocation=location;
                        objs[0]=calculatedSpeed;
                    }


                }};
            //监视地理位置变化
            locationManager.requestLocationUpdates(locationProvider, 2000, 15, locationListener);


            if (location != null) {
                //不为空
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
            else
                try{
                    Thread.sleep(10000);}
                catch (InterruptedException e){
                    e.printStackTrace();
                }
            // add LocationListener

            int count = 0;
            int stopIndex = -1;
            boolean atBusstop = false;
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
            //second while loop: find out when the user get on the bus
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
                            formatter.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
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
                                long timeFromDB = -1;
                                int stopIndexOfData = -1;
                                //todo: search for info in mysql; check if either timeFromDB+intervals<current time<that+errors or -yy<waitingTime<xx
                                Connection connection = null;
                                try {
                                    Class.forName("com.mysql.jdbc.Driver");
                                    String jdbcUrl = String.format("jdbc:mysql://34.92.5.65:3306/culife");

                                    connection = DriverManager.getConnection(jdbcUrl, "root", "carlos0923=-=");
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                } catch (ClassNotFoundException ex) {
                                    ex.printStackTrace();
                                }
                                try{
                                    Statement st = connection.createStatement();
                                    String sql = "SELECT * FROM busInfo WHERE busNum='"+busNum+"' and stopIndex between "+
                                            (buses[busIndex.get(busNum)].passStops.indexOf(stopsArray[stopIndex].stopName)-3) +" and "+
                                            (buses[busIndex.get(busNum)].passStops.indexOf(stopsArray[stopIndex].stopName)-1)+
                                            "ORDER BY id DESC LIMIT 1";
                                    ResultSet rs = st.executeQuery(sql);
                                    rs.next();
                                    timeFromDB = rs.getLong("time");
                                    stopIndexOfData = rs.getInt("stopIndex");
                                    connection.close();
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }
                                if((timeFromDB == -1) || (stopIndexOfData == -1)){
                                    Log.d("TSAI", "onHandleIntent: can't fetch data from db");
                                }
                                else{
                                    int errorForEachStop = 60; //seconds
                                    int travelTime = (int)(buses[busIndex.get(busNum)].estimateTime(buses[busIndex.get(busNum)].passStops.get(stopIndexOfData),stopsArray[stopIndex].stopName)*60);
                                    long errorTotal = (getOnTime-timeFromDB)/1000 - travelTime;
                                    int numOfStops = buses[busIndex.get(busNum)].passStops.indexOf(stopsArray[stopIndex].stopName) - stopIndexOfData;
                                    if((errorTotal<errorForEachStop*numOfStops)&&(errorTotal>-60))
                                        flag = true;
                                }
                                if(flag==false) {
//                                    check by waiting time
                                    if(((20-waitingTimes.get(busNum))<buses[busIndex.get(busNum)].passStops.indexOf(stopsArray[stopIndex].stopName)*60)&&((20-waitingTimes.get(busNum))>-60))
                                        flag = true;
                                }
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
            int writing_count = 0;
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

                            int indexOfLastStop = buses[busIndex.get(possibleBuses.get(i))].passStops.indexOf(lastStop);
                            int indexOfCurrentStop = buses[busIndex.get(possibleBuses.get(i))].passStops.indexOf(stopsArray[currentStop]);

                            if(indexOfCurrentStop!=(indexOfLastStop+1)){
                                possibleBuses.remove(i);
                                i--;
                            }

                        }
                        if(possibleBuses.size() == 1){
                            the_index = buses[busIndex.get(possibleBuses.get(0))].passStops.indexOf(stopsArray[currentStop].stopName);
                            //write to mysql
                            Connection connection = null;
                            try {
                                Class.forName("com.mysql.jdbc.Driver");
                                String jdbcUrl = String.format("jdbc:mysql://34.92.5.65:3306/culife");

                                connection = DriverManager.getConnection(jdbcUrl, "root", "carlos0923=-=");

                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            } catch (ClassNotFoundException ex) {
                                ex.printStackTrace();
                            }
                            try{
                                Statement st = connection.createStatement();
                                String sql = "INSERT INTO busInfo(busNum,stopIndex,time) VALUES('"+possibleBuses.get(0)+"','"+the_index+"','"+location.getTime()+"')";
                                int result = st.executeUpdate(sql);
                                connection.close();
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                            continue;
                        }
                        //if there are still more than 1 possible buslines
                        else if (possibleBuses.size() > 1){
                            lastStop = stopsArray[currentStop].stopName;
                        }
                    }
                    if(possibleBuses.size() == 1){
                        if(the_index==-1)//if the_index is not initialized,i.e, only one possible bus at the beginnning
                            the_index = buses[busIndex.get(possibleBuses.get(0))].passStops.indexOf(lastStop);

//                        double deltaLng = location.getLongitude()-stopsArray[the_index].Longitude;
//                        double deltaLat = location.getLatitude()-stopsArray[the_index].Latitude;
//                        double distance = Math.sqrt(deltaLat*deltaLat + deltaLng*deltaLng);

                        // check if the bus still stop at the same stop
                        Location stopLocation = new Location("");
                        stopLocation.setLatitude(stopsArray[the_index].Latitude);
                        stopLocation.setLongitude(stopsArray[the_index].Longitude);
                        double distance = location.distanceTo(stopLocation);
                        if(distance<20.0) {
                            if(writing_count>=24)//if this user already stay at the same stop for 2mins, stop tracking their gps
                                stopSelf();
                            //write to mysql
                            Connection connection = null;
                            try {
                                Class.forName("com.mysql.jdbc.Driver");
                                String jdbcUrl = String.format("jdbc:mysql://34.92.5.65:3306/culife");

                                connection = DriverManager.getConnection(jdbcUrl, "root", "carlos0923=-=");
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            } catch (ClassNotFoundException ex) {
                                ex.printStackTrace();
                            }
                            try{
                                Statement st = connection.createStatement();
                                String sql = "INSERT INTO busInfo(busNum,stopIndex,time) VALUES('"+possibleBuses.get(0)+"','"+the_index+"','"+location.getTime()+"')";
                                int result = st.executeUpdate(sql);
                                connection.close();
                                writing_count++;  //count how many times in a row the user wrote to db at this stop
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                            count = 0; //if the user stop at a bus stop, clear the count value
                            continue;
                        }
                        //if not, check if it arrives at another stpo
                        stopLocation.setLatitude(stopsArray[the_index+1].Latitude);
                        stopLocation.setLongitude(stopsArray[the_index+1].Longitude);
                        distance = location.distanceTo(stopLocation);
                        if(distance<20.0){
                            //write to mysql
                            Connection connection = null;
                            try {
                                Class.forName("com.mysql.jdbc.Driver");
                                String jdbcUrl = String.format("jdbc:mysql://34.92.5.65:3306/culife");

                                connection = DriverManager.getConnection(jdbcUrl, "root", "carlos0923=-=");
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            } catch (ClassNotFoundException ex) {
                                ex.printStackTrace();
                            }
                            try{
                                Statement st = connection.createStatement();
                                String sql = "INSERT INTO busInfo(busNum,stopIndex,time) VALUES('"+possibleBuses.get(0)+"','"+(the_index+1)+"','"+location.getTime()+"')";
                                int result = st.executeUpdate(sql);
                                connection.close();
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                            the_index++;
                            count = 0;      //if the user stop at a bus stop, clear the count value
                            writing_count = 0; // if the user arrive at next stop, clear previous writing_count values
                            continue;
                        }
                        else{
                            count++;
                        }
                        //if the user stop at somewhere other than a bus stop for 10 times in a row, stop tracking their location
                        if(count>10){
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
                else {     //if the user moves like walking for more than 2.5mins, stop tracking
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
