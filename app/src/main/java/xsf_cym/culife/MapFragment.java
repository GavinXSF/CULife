package xsf_cym.culife;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import static android.content.Context.LOCATION_SERVICE;


// This is Fragment of Google Map
public class MapFragment extends Fragment {
    private GoogleMap mMap;
    private LatLng mLatLng;
    private LocationManager locationManager;
    private String locationProvider;
    private Location location;
    private LocationListener locationListener;
    private double latitude;
    private double longitude;
    private MapView mapView;



    public static MapFragment newInstance() {
        return new MapFragment();
    }



    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState ) {

        super.onCreate(savedInstanceState);
        getActivity().setTitle("My location");


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
                Toast.makeText(getActivity(),"没有可用的位置提供器",Toast.LENGTH_SHORT).show();
            }
            // 检查权限

            // 得到location object
            location = locationManager.getLastKnownLocation(locationProvider);
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            if (location != null) {
                //不为空,显示地理位置经纬度
                showLocation(location);
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
                    //如果位置发生变化,重新显示
                    showLocation(location);
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }};

            //监视地理位置变化
            locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);

        }


        View view =  inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this::onMapReady);


        return view;
    }


    private void showLocation(Location location) {
//        Toast.makeText(getActivity(),"Latitude is:"+location.getLatitude()+"/n Longitude is: "+location.getLongitude(),Toast.LENGTH_LONG).show();
    }


    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(20);
        // Add a marker in Sydney and move the camera
        mLatLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(mLatLng).title("Marker at My location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            // 关闭程序时移除监听器
            locationManager.removeUpdates(locationListener);
        }
    }
}
