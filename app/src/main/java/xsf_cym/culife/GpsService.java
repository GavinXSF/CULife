package xsf_cym.culife;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class GpsService extends Service {
    private LatLng mLatLng;
    private LocationManager locationManager;
    private String locationProvider;
    private Location location;
    private LocationListener locationListener;
    private double latitude;
    private double longitude;


    public GpsService() {
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
    public int onStartCommand(Intent intent, int flags, int startID){

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
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            if (location != null) {
                //不为空
            }
            // add LocationListener
            locationListener = new LocationListener() {

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
                public void onLocationChanged(Location location) {
                    //如果位置发生变化
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }};
            //监视地理位置变化
            locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);

        }

        return super.onStartCommand(intent, flags, startID);
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
