package com.matsavx.vkrtest.fragments;

import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vkrtest.R;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.GeoObjectCollection;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.directions.driving.VehicleOptions;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.CompositeIcon;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.map.VisibleRegionUtils;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.Response;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.Session;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements UserLocationObjectListener, Session.SearchListener, CameraListener, DrivingSession.DrivingRouteListener {

    private MapView mapView;
    private MapKit mapKit;
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private UserLocationLayer userLocationLayer;

    private SearchManager searchManager;
    private Session searchSession;
    private EditText searchEdit;

    private DrivingRouter drivingRouter;
    private DrivingSession drivingSession;
    private MapObjectCollection mapObjects;

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private SensorEventListener gyroscopeSensorEventListener;
    private Sensor accelerometerSensor;
    private SensorEventListener accelerometerSensorEventListener;
    private float accelerometerCalibrateValueX = 0;

    private boolean btnSensorOnFlag = false;

    private ImageButton btnSetCameraLocation, btnCancelDriving, btnSensorOn;
    private TextView tvAccMap, tvGyrMap;


    public MapFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public Bitmap drawRedBump() {
        int picSize = 20;
        Bitmap bitmap = Bitmap.createBitmap(picSize, picSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        // отрисовка плейсмарка
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(picSize / 2, picSize / 2, picSize / 2, paint);
        return bitmap;
    }

    public Bitmap drawOrangeBump() {
        int picSize = 20;
        Bitmap bitmap = Bitmap.createBitmap(picSize, picSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        // отрисовка плейсмарка
        Paint paint = new Paint();
        paint.setColor(Color.rgb(219, 116, 20));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(picSize / 2, picSize / 2, picSize / 2, paint);
        return bitmap;
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                "android.permission.ACCESS_FINE_LOCATION")
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{"android.permission.ACCESS_FINE_LOCATION"},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requestLocationPermission();

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = view.findViewById(R.id.mapview);

        btnCancelDriving = view.findViewById(R.id.btnCancelDriving);
        btnCancelDriving.setVisibility(View.INVISIBLE);
        btnCancelDriving.setActivated(false);

        tvAccMap = view.findViewById(R.id.tvAccMap);
        tvGyrMap = view.findViewById(R.id.tvGyrMap);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mapView.getMap().setRotateGesturesEnabled(true);

        mapView.getMap().move(
                new CameraPosition(new Point(43.10562, 131.87353), 11.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH,0),
                null
        );

        mapKit = MapKitFactory.getInstance();
        mapKit.resetLocationManagerToDefault();
        userLocationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());
        userLocationLayer.setVisible(true);

        userLocationLayer.setHeadingEnabled(true);

        userLocationLayer.setObjectListener(this);

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);
        mapView.getMap().addCameraListener(this);
        searchEdit = view.findViewById(R.id.search_edit);
        searchEdit.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (searchEdit.getText().toString().matches("")) {
                searchEdit.setText("Нажмите, чтобы ввести адрес");
            }
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                submitQuery(searchEdit.getText().toString());
            }

            return false;
        });


        submitQuery(searchEdit.getText().toString());

        DirectionsFactory.initialize(getContext());
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter();

        btnSetCameraLocation = view.findViewById(R.id.btnSetCameraLocation);
        btnSetCameraLocation.setOnClickListener(v->{
//            userLocationLayer.cameraPosition();
            moveCameraToPosition(userLocationLayer.cameraPosition().getTarget());

        });

        btnSensorOn = view.findViewById(R.id.btnSensorOn);
        btnSensorOn.setOnClickListener(v->{
            if (btnSensorOnFlag) {
                btnSensorOnFlag = false;
            } else {
                btnSensorOnFlag = true;
            }
        });

        gyroscopeSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (btnSensorOnFlag) {
                    float gyroscopeValueX = (event.values[0]);
                    tvGyrMap.setText(String.valueOf((int) gyroscopeValueX));
                } else {
                    tvGyrMap.setText("");
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        accelerometerSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (btnSensorOnFlag) {
//                        btnCalibrateAccelerometer.setOnClickListener(v -> accelerometerCalibrateValueX = event.values[2]);
                    tvAccMap.setText(String.valueOf((int) (event.values[2] - accelerometerCalibrateValueX)));
                } else {
                    tvAccMap.setText("");
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        return view;
    }

    public void moveCameraToPosition(@NonNull Point target) {
        mapView.getMap().move(
                new CameraPosition(target, 15.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 2), null);
    }

    //search
    private void submitQuery(String query) {
        searchSession = searchManager.submit(
                query,
                VisibleRegionUtils.toPolygon(mapView.getMap().getVisibleRegion()),
                new SearchOptions(),
                this);
    }

    @Override
    public void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(gyroscopeSensorEventListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(accelerometerSensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(gyroscopeSensorEventListener);
        sensorManager.unregisterListener(accelerometerSensorEventListener);
    }

    @Override
    public void onObjectAdded(UserLocationView userLocationView) {
//        userLocationLayer.setAnchor(
//                new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.5)),
//                new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.83)));

        userLocationView.getArrow().setIcon(ImageProvider.fromResource(
                getContext(), R.drawable.user_location_arrow));

        drawBumps();

        userLocationView.getAccuracyCircle().setFillColor(Color.BLUE & 0x99ffffff);
    }

    @Override
    public void onObjectRemoved(UserLocationView view) {
    }

    @Override
    public void onObjectUpdated(UserLocationView view, ObjectEvent event) {

    }

    @Override
    public void onCameraPositionChanged(@NonNull Map map, @NonNull CameraPosition cameraPosition, @NonNull CameraUpdateReason cameraUpdateReason, boolean b) {

    }

    public void drawBumps() {
        mapView.getMap().getMapObjects().addPlacemark(new Point(43.10529, 131.87353), ImageProvider.fromBitmap(drawRedBump()));
        mapView.getMap().getMapObjects().addPlacemark(new Point(43.10790, 131.87920), ImageProvider.fromBitmap(drawOrangeBump()));
    }

    @Override
    public void onSearchResponse(@NonNull Response response) {
        MapObjectCollection mapObjects = mapView.getMap().getMapObjects();
        mapObjects.clear();
        drawBumps();

        for (GeoObjectCollection.Item searchResult : response.getCollection().getChildren()) {
            Point resultLocation = searchResult.getObj().getGeometry().get(0).getPoint();
            if (resultLocation != null) {
                mapObjects.addPlacemark(
                        resultLocation,
                        ImageProvider.fromResource(getContext(), R.drawable.search_result_mark))
                .addTapListener(new MapObjectTapListener() {
                    @Override
                    public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Построение маршрута")
                                .setMessage("Хотите построить маршрут до выбранного места?")
                                .setCancelable(true)
                                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                        mapObjects = mapView.getMap().getMapObjects().addCollection();
                                        submitRequest(
                                                userLocationLayer.cameraPosition().getTarget(),
                                                point
                                        );
                                        btnCancelDriving.setVisibility(View.VISIBLE);
                                        btnCancelDriving.setActivated(true);
                                        btnCancelDriving.setOnClickListener(v->{
                                            mapObjects.clear();
                                            drawBumps();
                                            btnCancelDriving.setVisibility(View.INVISIBLE);
                                            btnCancelDriving.setActivated(false);
                                        });
                                    }
                                })
                                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        final AlertDialog dlg = builder.create();
                        dlg.show();

                        return false;
                    }
                });
            }
        }
    }

    @Override
    public void onSearchError(@NonNull Error error) {
        String errorMessage = getString(R.string.unknown_error_message);
        if (error instanceof RemoteError) {
            errorMessage = getString(R.string.remote_error_message);
        } else if (error instanceof NetworkError) {
            errorMessage = getString(R.string.network_error_message);
        }

        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDrivingRoutes(@NonNull List<DrivingRoute> routes) {
        for (DrivingRoute route : routes) {
            mapObjects.addPolyline(route.getGeometry());
        }
    }

    @Override
    public void onDrivingRoutesError(@NonNull Error error) {
        String errorMessage = getString(R.string.unknown_error_message);
        if (error instanceof RemoteError) {
            errorMessage = getString(R.string.remote_error_message);
        } else if (error instanceof NetworkError) {
            errorMessage = getString(R.string.network_error_message);
        }

        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void submitRequest(Point startLocation, Point endLocation) {
        mapObjects = mapView.getMap().getMapObjects().addCollection();

        DrivingOptions drivingOptions = new DrivingOptions();
        VehicleOptions vehicleOptions = new VehicleOptions();
        ArrayList<RequestPoint> requestPoints = new ArrayList<>();
        requestPoints.add(new RequestPoint(
                startLocation,
                RequestPointType.WAYPOINT,
                null));
        requestPoints.add(new RequestPoint(
                endLocation,
                RequestPointType.WAYPOINT,
                null));
        drivingRouter.requestRoutes(requestPoints, drivingOptions, vehicleOptions, this);
    }
}