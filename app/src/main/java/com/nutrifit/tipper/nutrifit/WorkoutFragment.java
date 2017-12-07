package com.nutrifit.tipper.nutrifit;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nutrifit.tipper.nutrifit.Database.DatabaseHandler;
import com.nutrifit.tipper.nutrifit.Model.User;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WorkoutFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WorkoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkoutFragment extends Fragment implements SensorEventListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View record_workout_view;
    private TextView duration;
    private TextView caloriesWkout;
    private TextView currPace;
    private WatchTime watchTime; // singleton
    private Bundle saveInstanceState;
    private Handler mHandler;
    private Button recordButton;
    private long timeInMilliseconds;
    private ArrayList<LatLng> points = null;
    private LocationListener locationListener;
    private MapView mapView;
    private GoogleMap googleMap;
    private boolean startTime;
    private boolean currentLocation;
    private TextView distance;
    private int steps;
    private SensorManager sManager;
    private Sensor stepSensor;
    private DatabaseHandler db;
    private User user;
    private int hour;
    private int minutes;
    private int seconds;
    private static WorkoutFragment wFragment = new WorkoutFragment();
    private boolean activityRunning;

    public static final String PREFS_NAME = "USER" ;


    private Context mContext;
    private double latitude;
    private double longitude;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private GoogleApiClient mGoogleApiClient;
    private OnFragmentInteractionListener mListener;
    private FusedLocationProviderClient mFusedLocationClient;

    private static int REQUEST_FINE_LOCATION = 0;
    private static int REQUEST_COARSE_LOCATION = 1;


    public WorkoutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecordWorkoutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WorkoutFragment newInstance(String param1, String param2) {
        WorkoutFragment fragment = new WorkoutFragment();
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
    }

    public static WorkoutFragment newInstance()
    {
        return wFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        record_workout_view = inflater.inflate(R.layout.fragment_workout, container, false);
        currentLocation = false;
        points = new ArrayList<LatLng>();
        startTime = false;
        distance = (TextView) record_workout_view.findViewById(R.id.distance);
        currPace = (TextView) record_workout_view.findViewById(R.id.pace);
        caloriesWkout = (TextView) record_workout_view.findViewById(R.id.caloriesWkout);
        mContext = record_workout_view.getContext();
        sManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        db = new DatabaseHandler(mContext);
        activityRunning = true;
        user = getUserData();

        // DURATION || STOPWATCH
        recordButton = (Button) record_workout_view.findViewById(R.id.recordButton);
            /* CODE CITATION FOR STOPCLOCK: Android Programming Concepts Pg 566-569 */
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!startTime) {
                    // user wants to start time
                    googleMap.clear(); // reset map
                    points.clear(); // reset list of LatLng points
                    resetTimer(); // reset timer
                    distance.setText("0.00");
                    currPace.setText("00:00");
                    caloriesWkout.setText("0");
                    startTime = true; // time has started
                    startTimerAndLocationTracking();
                } else {
                    // user wants to stop time
                    stopTimerAndLocationTracking();
                    steps = 0;
                    startTime = false; // time has stopped

                    User dbUser = db.getUser(user.getEmail());
                    float remaining = 0; // Goal - Food + Exercise = Remaining
                    float exerciseCal = dbUser.getExerciseCalories();
                    if(exerciseCal == 0) {
                        user.setExerciseCalories(Float.valueOf(caloriesWkout.getText().toString()));
                        remaining = dbUser.getCaloriesToBurnPerDay() - dbUser.getFoodCalories() + user.getExerciseCalories();
                        user.setCaloriesToBurnPerDay(remaining);
                        db.updateUser(user);
                        saveUserData(getActivity(), user);
                    } else {
                        exerciseCal += Float.valueOf(caloriesWkout.getText().toString());
                        user.setExerciseCalories(exerciseCal);
                        remaining = dbUser.getCaloriesToBurnPerDay() - dbUser.getFoodCalories() + user.getExerciseCalories();
                        user.setCaloriesToBurnPerDay(remaining);
                        db.updateUser(user);
                        saveUserData(getActivity(), user);
                    }
                }
            }
        });

          /* Displaying Google Maps on Fragment UI */
        mapView = (MapView) record_workout_view.findViewById(R.id.map);
        saveInstanceState = savedInstanceState;
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        if (checkPlayServices()) {
            Log.v("Success", "Building Google API Client...");
            buildGoogleApiClient();
        }

        if(checkLocationPermission()) {
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                Log.v("Permission", "Location Enabled");
                mapView.getMapAsync(this);
            }
        } else {
            Log.v("Permission", "Location was disabled");
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }


        duration = (TextView) record_workout_view.findViewById(R.id.duration);
        watchTime = WatchTime.getInstance();
        mHandler = new Handler();

        SharedPreferences settings;
        settings = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String userObj = settings.getString(PREFS_NAME, null);
        user = gson.fromJson(userObj, User.class);

        return record_workout_view;
    }

    public void startTimerAndLocationTracking()
    {
        // Change button text to STOP and background color to Red
        recordButton.setText("STOP");
        recordButton.setBackgroundColor(Color.RED);

        // Set the start time and call the custom handler
        watchTime.setStartTime(SystemClock.uptimeMillis());
        sManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mHandler.postDelayed(updateLocationRunnable, 20);
        mHandler.postDelayed(updateTimerRunnable, 20);
    }

    private Runnable updateTimerRunnable = new Runnable() {
        @Override
        public void run() {

            // Compute Time Difference
            timeInMilliseconds = SystemClock.uptimeMillis() - watchTime.getStartTime();
            watchTime.setTimeUpdate(watchTime.getStoredTime() + timeInMilliseconds);

            int time = (int) (watchTime.getTimeUpdate() / 1000);

            // Compute Minutes, Seconds, and Milliseconds
            hour = 0;
            minutes = time / 60;
            seconds = time % 60;
            int milliseconds = (int) (watchTime.getTimeUpdate() % 1000);

            if(minutes != 60) {
                // an hour hasn't passed yet
                duration.setText(String.format("%02d", minutes) + ":" +
                        String.format("%02d", seconds));
            } else {
                // increment hour
                hour++;
                duration.setText(String.format("%02d", hour) + ":" +
                        String.format("%02d", minutes) + ":" +
                        String.format("%02d", seconds));

            }
            mHandler.postDelayed(this, 0);
        }
    };

    public void stopTimerAndLocationTracking()
    {
        // Reset to START button text and green background
        recordButton.setText("START");
        recordButton.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.buttonColor, null));

        // Update the stored time value
        watchTime.addStoredTime(timeInMilliseconds);

        sManager.unregisterListener(this, stepSensor);

        // Handler clears the message queue
        mHandler.removeCallbacks(updateLocationRunnable);
        mHandler.removeCallbacks(updateTimerRunnable);
    }

    public void resetTimer()
    {
        watchTime.resetWatchTime();
        watchTime.resetWatchTime();
        timeInMilliseconds = 0L;

        int minutes = 0;
        int seconds = 0;
        int milliseconds = 0;

        // Display the duration in TextView
        duration.setText(String.format("%02d", minutes) + ":" +
                String.format("%02d", seconds) + ":" +
                String.format("%02d", milliseconds));

    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        setCurrentLocation();
    }

    private Runnable updateLocationRunnable = new Runnable() {
        @Override
        public void run() {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                requestFineLocationPermissions();
                requestCoarseLocationPermissions();
            }

            googleMap.setMyLocationEnabled(true);

            LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(bestProvider);

            if (location != null) {
                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        float dist = 0;
                        if (!currentLocation) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            LatLng latLng = new LatLng(latitude, longitude);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
                            currentLocation = true;
                        }

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        LatLng latLng = new LatLng(latitude, longitude);

                        // Moves and animate camera as the user moves
                        CameraUpdate center = CameraUpdateFactory.newLatLng(latLng);
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16.0f);
                        googleMap.moveCamera(center);
                        googleMap.animateCamera(zoom);

                        points.add(latLng);

                        // Draws the Polyline when the user moves location
                        if(startTime) {
                            drawPolyline();

                            dist = getDistance(steps);
                            Log.v("Distance", String.valueOf(dist));
                            distance.setText(String.format("%.2f", dist));
                            float calories = calculateCalories(steps);
                            caloriesWkout.setText(String.valueOf(calories));
                            // Current Pace Formula: https://www.livestrong.com/article/291604-treadmills-calculate-pace-times/
                            int seconds = 0;
                            double curSecondPerMile = 0;
                            double curPace = 0;
                            double curPaceMinute = 0;
                            double curPaceSecond = 0;
                            try {
                                seconds = convertHHMMToSeconds(duration.getText().toString());
                                curSecondPerMile = seconds / dist; // seconds per mile
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            curPace = curSecondPerMile / 60;
                            curPaceMinute = Math.floor(curPace);
                            curPaceSecond = (curPace % 1) * 60; // Example: .75 * 60 = 45 second
                            if(dist != 0.0 && dist > 0.01) { // avoid funky numbers on mm side of the duration
                                if (curPaceSecond >= 10 && curPaceSecond <= 99) { // Double Digit
                                    currPace.setText(String.valueOf((int) curPaceMinute) + ":" + String.valueOf((int) curPaceSecond));
                                } else { // Single Digit
                                    currPace.setText(String.valueOf((int) curPaceMinute) + ":" + "0" + String.valueOf((int) curPaceSecond));
                                }
                            }
                        }

                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                };
                locationManager.requestLocationUpdates(bestProvider, 3000, 0, locationListener);
            }
        }

    };

    //function to determine the distance run in kilometers using average step length for men and number of steps
    public float getDistance(int steps){
        Log.v("Steps", String.valueOf(steps));
        float distance = (float)(steps*78)/(float)100000;
        return kmToMi(distance);
    }

    // Code Citation: https://github.com/xd6/GoogleMapDistanceMeasure/blob/master/app/src/main/java/com/xd6/googlemapstoy/MapsActivity.java
    public static float kmToMi(float km) {
        float miles = (float) (km * 0.621371);
        return miles;
    }

    public float calculateCalories(int numOfSteps) { // time elapsed is min / 60
        float totalCalories = 0;
        totalCalories = (float) (numOfSteps * 0.063);
        Log.v("Calories Burned", String.valueOf(totalCalories));
        return totalCalories;
    }


    private void requestFineLocationPermissions()
    {
        Log.i(TAG, "FINE LOCATION permission has NOT been granted. Requesting permission.");

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG, "Displaying fine location permission rationale to provide additional context.");
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.title_location_permission)
                    .setMessage(R.string.text_location_permission)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_FINE_LOCATION);
                        }
                    })
                    .create()
                    .show();
        } else {

            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_FINE_LOCATION);
        }

    }

    private void requestCoarseLocationPermissions()
    {
        Log.i(TAG, "COARSE LOCATION permission has NOT been granted. Requesting permission.");

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG,
                    "Displaying coarse location permission rationale to provide additional context.");
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.title_location_permission)
                    .setMessage(R.string.text_location_permission)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_COARSE_LOCATION);
                        }
                    })
                    .create()
                    .show();
        } else {

            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_COARSE_LOCATION);
        }

    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }


    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(getActivity());
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(getActivity(), result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            return false;
        }

        return true;
    }

    private void setCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestFineLocationPermissions();
            requestCoarseLocationPermissions();
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    LatLng latLng = new LatLng(latitude, longitude);
                    enableLocation();
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
                } else {
                    Log.v("Error", "Unable to get latitude and longitude");
                }
            }
        });

    }

    private void enableLocation()
    {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestFineLocationPermissions();
            requestCoarseLocationPermissions();
        }

        googleMap.setMyLocationEnabled(true);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    private void drawPolyline()
    {
        googleMap.clear();  //clears all Markers and Polylines

        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int i = 0; i < points.size() - 1; i++) {
            LatLng point = points.get(i);
            options.add(point);
        }

        googleMap.addPolyline(options); //add Polyline
    }

    private int convertHHMMToSeconds(String time) throws ParseException {
        DateFormat dateFormat;
        if (time.length() == 5) {
            dateFormat = new SimpleDateFormat("mm:ss");
        } else {
            dateFormat = new SimpleDateFormat("HH:mm:ss");
        }
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = dateFormat.parse(time);
        long seconds = date.getTime() / 1000L;
        return (int) seconds;
    }


    @Override
    public void onResume(){
        super.onResume();
        activityRunning = true;
        Log.v("Fragment", "OnResume");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.v("Fragment", "OnDestroy");
        activityRunning = false;
        sManager.unregisterListener(this, stepSensor);
        // Handler clears the message queue
        mHandler.removeCallbacks(updateLocationRunnable);
        mHandler.removeCallbacks(updateTimerRunnable);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        Log.v("Fragment", "OnDestroyView");
        activityRunning = false;
        sManager.unregisterListener(this, stepSensor);
        // Handler clears the message queue
        stopTimerAndLocationTracking();
        mHandler.removeCallbacks(updateLocationRunnable);
        mHandler.removeCallbacks(updateTimerRunnable);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Deprecated according to --> https://stackoverflow.com/questions/29292942/how-do-i-implement-onfragmentinteractionlistener
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("Connect", "Connected ");
        Log.d("onConnected", Boolean.toString(mGoogleApiClient.isConnected()));
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(activityRunning) {
            Sensor sensor = event.sensor;
            float[] values = event.values;
            int value = -1;

            if (values.length > 0) {
                value = (int) values[0];
            }


            if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                steps++;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i)
    {

    }

    private void saveUserData(Context context, User user) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String userObj = gson.toJson(user);

        editor.putString(PREFS_NAME, userObj);
        editor.commit();
    }

    private User getUserData()
    {
        SharedPreferences settings;
        settings = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String userObj = settings.getString(PREFS_NAME, null);
        User retUser = gson.fromJson(userObj, User.class);
        return retUser;
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                       REQUEST_FINE_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_FINE_LOCATION);
            }
            return false;
        } else {
            return true;
        }
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