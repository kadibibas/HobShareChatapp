package com.example.kadib.hobsharechatapp;



import android.Manifest;
        import android.app.DownloadManager;
        import android.app.ProgressDialog;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.content.pm.PackageManager;
        import android.graphics.Color;
        import android.graphics.drawable.Drawable;
        import android.icu.text.DateFormat;
        import android.location.Criteria;
        import android.location.Location;
        import android.location.LocationManager;
        import android.os.Build;
        import android.os.Bundle;
        import android.os.Handler;
        import android.support.annotation.NonNull;
        import android.support.design.internal.ForegroundLinearLayout;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.app.AppCompatActivity;

        import java.lang.reflect.Array;
        import java.text.ParseException;
        import java.text.SimpleDateFormat;

        import android.util.Log;
        import android.view.MotionEvent;
        import android.view.View;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.widget.Switch;
        import android.widget.Toast;

        import com.android.volley.Request;
        import com.android.volley.RequestQueue;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.StringRequest;
        import com.android.volley.toolbox.Volley;
        import com.getbase.floatingactionbutton.FloatingActionsMenu;
        import com.google.android.gms.location.FusedLocationProviderClient;
        import com.google.android.gms.location.LocationRequest;
        import com.google.android.gms.location.LocationServices;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.ChildEventListener;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.Query;
        import com.google.firebase.database.ValueEventListener;
        import com.mapbox.mapboxsdk.Mapbox;
        import com.mapbox.mapboxsdk.annotations.Icon;
        import com.mapbox.mapboxsdk.annotations.IconFactory;
        import com.mapbox.mapboxsdk.annotations.Marker;
        import com.mapbox.mapboxsdk.annotations.MarkerOptions;
        import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
        import com.mapbox.mapboxsdk.annotations.Polygon;
        import com.mapbox.mapboxsdk.annotations.PolygonOptions;
        import com.mapbox.mapboxsdk.annotations.Polyline;
        import com.mapbox.mapboxsdk.annotations.PolylineOptions;
        import com.mapbox.mapboxsdk.camera.CameraPosition;
        import com.mapbox.mapboxsdk.camera.CameraUpdate;
        import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
        import com.mapbox.mapboxsdk.constants.Style;
        import com.mapbox.mapboxsdk.geometry.LatLng;
        import com.mapbox.mapboxsdk.maps.MapView;
        import com.mapbox.mapboxsdk.maps.MapboxMap;
        import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
        import com.mapbox.mapboxsdk.style.functions.Function;
        import com.mapbox.mapboxsdk.style.layers.CircleLayer;
        import com.mapbox.mapboxsdk.style.layers.Property;
        import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
        import com.mapbox.mapboxsdk.style.sources.VectorSource;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.AbstractSequentialList;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.HashMap;
        import java.util.HashSet;
        import java.util.LinkedList;
        import java.util.List;
        import java.util.Map;

        import okhttp3.OkHttpClient;

        import static com.mapbox.mapboxsdk.style.functions.stops.Stop.stop;
        import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
        import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleOpacity;
        import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
        import static java.lang.Math.cos;
        import static java.lang.Math.sin;


public class activity_main_map extends AppCompatActivity implements android.location.LocationListener {

    private static final String TAG = MainActivity.class.getName();

    final Handler h = new Handler();

    boolean move = false;

    LatLng prev_Latlng;
    LatLng cur_LatLng;

    //menu
    FloatingActionsMenu fabMenu;

    //mapbox
    MapView mapView;
    MapboxMap mapbox;

    //Buttons
    ImageButton mLocationBTN;
    ImageView mLocationDisableBTN;
    ImageButton myLocation;
    ImageButton myLocationDisable;

    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;
    private FirebaseUser mCurrent_user;



    //Http request
    private RequestQueue mRequestQueue;
    private StringRequest stringRequest;




    //location
    double Lat;
    double Lng;
    LocationManager locationManager;
    private FusedLocationProviderClient mFusedLocationClient;
    private FusedLocationProviderClient mFusedLocationClient1;
    private boolean visible = false;


    //markers hashmap by id,marker value
    HashMap<String,Marker> Markers = new HashMap<>();
    HashMap<String,Marker> Markers_Polygon = new HashMap<>();



    //user object
    private MapUser user;
    private HotSpot hotspot;
    HashMap<String,MapUser> users = new HashMap<>();
    HashMap<String,MapUser> usersPrev = new HashMap<>();
    HashMap<String,HotSpot> hotspots = new HashMap<>();

    //shared preferences
    private static final String MY_PREFS_NAME = "MyPrefs";
    SharedPreferences sharedpreferens;

    //current user id
    private String currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();


        mRequestQueue = Volley.newRequestQueue(this);


        //locationrequest = new LocationRequest();
        //locationrequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient1 = LocationServices.getFusedLocationProviderClient(this);
        //mFusedLocationClient2 = LocationServices.getFusedLocationProviderClient(this);

        //shared preferences
        sharedpreferens = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        currentUser = sharedpreferens.getString("Id",null);



        //map box
        Mapbox.getInstance(this, "pk.eyJ1Ijoia2FkaWJpYmFzIiwiYSI6ImNqNnJ2bXN0aTBkZDYyeG56bnA5OGoxN3EifQ.gtbNnIgSv-jG9ssLf9I7SA");
        setContentView(R.layout.activity_main_map);
        //map view
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setStyleUrl(Style.MAPBOX_STREETS);
        mapView.onCreate(savedInstanceState);

        //buttons
        mLocationBTN = (ImageButton) findViewById(R.id.myLocationButton);
        mLocationDisableBTN = (ImageView)findViewById(R.id.myLocationDisableButton);
        myLocation = (ImageButton) findViewById(R.id.myEyeButton);
        myLocationDisable = (ImageButton) findViewById(R.id.myEyeDisableButton);

        onStartUserLocation();

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                sendGetRequest();

            }
        });








        //menu
        fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);


        mLocationBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                move = true;
                centerUser();
                mLocationBTN.setVisibility(View.GONE);
                mLocationDisableBTN.setVisibility(View.VISIBLE);

                mapView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        move = false;
                        mapbox.getUiSettings().setRotateGesturesEnabled(true);
                        mapbox.getUiSettings().setZoomGesturesEnabled(true);
                        mapbox.getUiSettings().setScrollGesturesEnabled(true);

                        mLocationDisableBTN.setVisibility(View.GONE);
                        mLocationBTN.setVisibility(View.VISIBLE);
                        return false;
                    }
                });






            }
        });


        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visible = true;
                move=true;
                mLocationBTN.setVisibility(View.GONE);
                mLocationDisableBTN.setVisibility(View.VISIBLE);
                mapView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        move = false;
                        mapbox.getUiSettings().setRotateGesturesEnabled(true);
                        mapbox.getUiSettings().setZoomGesturesEnabled(true);
                        mapbox.getUiSettings().setScrollGesturesEnabled(true);

                        mLocationDisableBTN.setVisibility(View.GONE);
                        mLocationBTN.setVisibility(View.VISIBLE);
                        return false;
                    }
                });
                mapbox.setMyLocationEnabled(false);
                visible();
                h.postDelayed(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        visible();
                        h.postDelayed(this, 2000);
                    }
                }, 2000); // 1 second delay (takes millis)

                myLocation.setVisibility(View.GONE);
                myLocationDisable.setVisibility(View.VISIBLE);



            }
        });

        //
        myLocationDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visible = false;
                move=false;
                mapbox.setMyLocationEnabled(true);
                h.removeCallbacksAndMessages(null);
                sendPutRequest();
                myLocationDisable.setVisibility(View.GONE);
                myLocation.setVisibility(View.VISIBLE);
                for(Map.Entry<String,MapUser>entry : users.entrySet())
                {

                    Marker marker = Markers.get(entry.getKey());
                    marker.remove();
                    Markers.remove(entry.getKey());

                }
                Markers.clear();
                users.clear();
                usersPrev.clear();

            }
        });

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);


    }


    // fAB Menu change map styles
    public void selectStyle(View view) {
        fabMenu.collapse();
        mapView.setStyleUrl(Style.DARK);
    }

    public void selectStyle2(View view) {
        fabMenu.collapse();
        mapView.setStyleUrl(Style.LIGHT);
    }

    public void selectStyle3(View view) {
        fabMenu.collapse();
        mapView.setStyleUrl("mapbox://styles/kadibibas/cj6ou588l1zsn2rszxeic9w37");
    }

    public void selectStyle4(View view) {
        fabMenu.collapse();
        mapView.setStyleUrl(Style.SATELLITE_STREETS);
    }

    public void selectStyle5(View view) {
        fabMenu.collapse();
        mapView.setStyleUrl(Style.MAPBOX_STREETS);
    }



    //lifecycle methods
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();


    }

    @Override
    protected void onResume() {
        /*if(visible==true)
        {
            visible();
            myLocation.setVisibility(View.GONE);
            myLocationDisable.setVisibility(View.VISIBLE);
        }*/
        //sendGetRequest();

        mapView.onResume();
        super.onResume();

    }

    @Override
    protected void onPause() {
        /*visible = false;

        sendPutRequest();*/
        mapView.onPause();
        super.onPause();

    }


    @Override
    protected void onStop() {

        mapView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        h.removeCallbacksAndMessages(null);
        mRequestQueue.cancelAll(null);
        sendPutRequest();
        mapView.onDestroy();
        super.onDestroy();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    //check location permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                centerUser();
                break;
            default:
                break;
        }
    }


    //method - centered the user location
    public void centerUser() {
        final double[] lat = new double[1];
        final double[] lon = new double[1];
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}
                        , 10);
            }

        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            lat[0] = location.getLatitude();
                            lon[0] = location.getLongitude();
                            LatLng latLng = new LatLng(lat[0], lon[0]);


                            mapbox.getUiSettings().setRotateGesturesEnabled(false);
                            mapbox.getUiSettings().setZoomGesturesEnabled(false);
                            mapbox.getUiSettings().setScrollGesturesEnabled(false);


                            mapbox.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                            .target(latLng)
                                            .zoom(17)
                                            .tilt(45.0)
                                            .build()),
                                    200);



                        }
                    }
                });
    }


    //method - put the current user location on the map, and animate the camera
    public void onStartUserLocation() {
        final double[] lat = new double[1];
        final double[] lon = new double[1];
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}
                        , 10);
            }

        }
        mFusedLocationClient1.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            lat[0] = location.getLatitude();
                            lon[0] = location.getLongitude();
                            final LatLng latLng = new LatLng(lat[0], lon[0]);


                            mapView.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(MapboxMap map) {
                                    mapbox = map;
                                    map.setMyLocationEnabled(true);
                                    map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                                    .target(latLng)
                                                    .zoom(17)
                                                    .tilt(45.0)
                                                    .build()),
                                            0);

                                }
                            });


                        }
                    }
                });
    }

    //send request to sever to update location and get all users back as response
    public void visible() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);

        final String lat1 = String.valueOf(Lat);
        final String lon1 = String.valueOf(Lng);

        String url = "http://mapapp.cyberserve.co.il/API/User/updateUser?fid=" + mCurrent_user.getUid() + "&vis=" + "true" +"&lat="+lat1+"&lng="+lon1;
        stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                Log.i(TAG,"Response: "+response.toString());

                JSONArray array = null;
                usersPrev.clear();
                for(Map.Entry<String,MapUser>entry : users.entrySet())
                {
                    usersPrev.put(entry.getKey(),entry.getValue());
                }
                try {
                    array = new JSONArray(response);
                    users.clear();
                    for (int i = 0; i<array.length();i++)
                    {
                        JSONObject jsonobject = array.getJSONObject(i);
                        user = new MapUser();
                        user.Device_token = jsonobject.getString("Device_token");
                        user.Visibility = jsonobject.getString("Visibility");
                        user.Lat = jsonobject.getString("Lat");
                        user.Lng = jsonobject.getString("Lng");
                        users.put(jsonobject.getString("Id"),user);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    HashMapToMap();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i(TAG,"Error: "+error.toString());

            }
        });
        mRequestQueue.add(stringRequest);
    }


    //insert the users location to map
    public void HashMapToMap()
    {

        for(Map.Entry<String,MapUser>entry : usersPrev.entrySet())
        {
            if(!users.containsKey(entry.getKey()))
            {
                Marker marker = Markers.get(entry.getKey());
                marker.remove();
                Markers.remove(entry.getKey());
            }

        }


        for(Map.Entry<String,MapUser>entry : users.entrySet())
        {
            if(usersPrev.containsKey(entry.getKey()))
            {

                MapUser user = entry.getValue();
                String Slat = user.Lat;
                String Slng = user.Lng;

                double lat = Double.parseDouble(Slat);
                double lng = Double.parseDouble(Slng);
                LatLng latlng = new LatLng(lat,lng);


                Marker marker = Markers.get(entry.getKey());
                marker.setPosition(latlng);

                Markers.put(entry.getKey(),marker);

                if(move == true)
                {

                    mapbox.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                    .target(cur_LatLng)
                                    .zoom(18)
                                    .tilt(45.0)
                                    .build()),
                            3000);
                }

            }
            else
            {
                MapUser user = entry.getValue();
                String Slat = user.Lat;
                String Slng = user.Lng;

                double lat = Double.parseDouble(Slat);
                double lng = Double.parseDouble(Slng);
                LatLng latlng = new LatLng(lat,lng);

                Marker marker = mapbox.addMarker(new MarkerOptions()
                        .position(latlng)
                );
                Markers.put(entry.getKey(), marker);
                mapbox.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(latlng)
                                .zoom(18)
                                .tilt(45.0)
                                .build()),
                        3000);
            }
        }



    }

    public void sendPutRequest()
    {
        String URL = "http://mapapp.cyberserve.co.il/API/User/updateUser?fid=" + mCurrent_user.getUid() + "&vis=" + "false" +"&lat="+"0.0"+"&lng="+"0.0";

        stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i(TAG,"Response: "+response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i(TAG,"Error: "+error.toString());


            }
        });
        mRequestQueue.add(stringRequest);


    }

    public void sendGetRequest()
    {
        String URL = "http://194.90.203.74/looking/api/hotspot";

        stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i(TAG,"Response: "+response.toString());
                JSONArray array = null;
                try {
                    array = new JSONArray(response);
                    for (int i = 0; i<array.length();i++)
                    {
                        JSONObject jsonobject = array.getJSONObject(i);
                        hotspot = new HotSpot();
                        hotspot.Lat = jsonobject.getString("Lat");
                        hotspot.Lng = jsonobject.getString("Lng");
                        hotspot.Radius = jsonobject.getString("Radius");
                        hotspot.Color = jsonobject.getString("Color");
                        if(jsonobject.getString("Category_Vis").equals("true"))
                        {
                            hotspots.put(jsonobject.getString("Publication_Category_ID"),hotspot);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    HotSpotToMap();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i(TAG,"Error: "+error.toString());


            }
        });
        mRequestQueue.add(stringRequest);


    }

    public void HotSpotToMap()
    {
        for(Map.Entry<String,HotSpot>entry : hotspots.entrySet())
        {
            HotSpot hotSpot_tmp = entry.getValue();
            String Slat = hotSpot_tmp.Lat;
            String Slng = hotSpot_tmp.Lng;
            String Sradius = hotSpot_tmp.Radius;
            String Color1 = hotSpot_tmp.Color;

            char hex = Color1.charAt(0);
            String start = Color1.substring(1);

            String tmp = hex+"80"+start;

            int radius = Integer.parseInt(Sradius);
            double lat = Double.parseDouble(Slat);
            double lng = Double.parseDouble(Slng);
            LatLng latlng = new LatLng(lat,lng);



            drawCircle(mapbox,latlng, Color.parseColor(tmp),radius);
        }
    }





    //location services methods
    @Override
    public void onLocationChanged(Location location) {

        prev_Latlng = cur_LatLng;

        double lat =  location.getLatitude();
        double lon =  location.getLongitude();
        LatLng latlng = new LatLng(lat,lon);

        Lat =  lat;
        Lng = lon;

        cur_LatLng = latlng;



    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    public void drawCircle(MapboxMap map, LatLng position, int color, double radiusMeters) {
        PolygonOptions polygonOptions = new PolygonOptions();
        polygonOptions.fillColor(color);
        polygonOptions.strokeColor(color);
        polygonOptions.addAll(getCirclePoints(position,radiusMeters));
        Polygon polygon = map.addPolygon(polygonOptions);
    }


    private ArrayList<LatLng> getCirclePoints(LatLng position, double radius) {
        int degreesBetweenPoints = 1; // change here for shape
        int numberOfPoints = (int) Math.floor(360 / degreesBetweenPoints);
        double distRadians = radius / 6371000.0; // earth radius in meters
        double centerLatRadians = position.getLatitude() * Math.PI / 180;
        double centerLonRadians = position.getLongitude() * Math.PI / 180;
        ArrayList<LatLng> polygons = new ArrayList<>(); // array to hold all the points
        for (int index = 0; index < numberOfPoints; index++) {
            double degrees = index * degreesBetweenPoints;
            double degreeRadians = degrees * Math.PI / 180;
            double pointLatRadians = Math.asin(sin(centerLatRadians) * cos(distRadians)
                    + cos(centerLatRadians) * sin(distRadians) * cos(degreeRadians));
            double pointLonRadians = centerLonRadians + Math.atan2(sin(degreeRadians)
                            * sin(distRadians) * cos(centerLatRadians),
                    cos(distRadians) - sin(centerLatRadians) * sin(pointLatRadians));
            double pointLat = pointLatRadians * 180 / Math.PI;
            double pointLon = pointLonRadians * 180 / Math.PI;
            LatLng point = new LatLng(pointLat, pointLon);
            polygons.add(point);
        }
        // add first point at end to close circle
        polygons.add(polygons.get(0));
        return polygons;
    }




}

