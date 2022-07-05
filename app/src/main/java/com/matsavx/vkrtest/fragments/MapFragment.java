package com.matsavx.vkrtest.fragments;

import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

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

    FileOutputStream outputStream;
    FileWriter writer;

    private int interval = 500;
    private boolean flagg = false;
    private boolean flaga = false;
    private Handler handler;

    private boolean btnSensorOnFlag = false;
    private boolean loopFlagA = false;

    private ImageButton btnSetCameraLocation, btnCancelDriving, btnSensorOn, btnCalibrateSensors;
    private TextView tvAccMap, tvGyrMap;

    private FileWriter fileWriter;
    private FileReader fileReader;


    private final Runnable processSensors = new Runnable() {
        @Override
        public void run() {
            flagg = true;
            flaga = true;
            handler.postDelayed(this, interval);
        }
    };


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

    public Bitmap drawYellowBump() {
        int picSize = 20;
        Bitmap bitmap = Bitmap.createBitmap(picSize, picSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        // отрисовка плейсмарка
        Paint paint = new Paint();
        paint.setColor(Color.rgb(247, 255, 0));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(picSize / 2, picSize / 2, picSize / 2, paint);
        return bitmap;
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                "android.permission.ACCESS_FINE_LOCATION")
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{"android.permission.ACCESS_FINE_LOCATION"},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(getContext(),
                "android.permission.WRITE_EXTERNAL_STORAGE")
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(getContext(),
                "android.permission.READ_EXTERNAL_STORAGE")
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{"android.permission.READ_EXTERNAL_STORAGE"},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requestPermission();

//        writeFile();
//        readFile();

        handler = new Handler();

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
        btnCalibrateSensors = view.findViewById(R.id.btnCalibrateSensors);
        btnCalibrateSensors.setVisibility(View.INVISIBLE);
        btnCalibrateSensors.setActivated(false);
        btnSetCameraLocation.setOnClickListener(v->{
//            userLocationLayer.cameraPosition();
            moveCameraToPosition(userLocationLayer.cameraPosition().getTarget());

        });

        btnSensorOn = view.findViewById(R.id.btnSensorOn);
        btnSensorOn.setOnClickListener(v-> {
            btnSensorOnFlag = !btnSensorOnFlag;

            if (btnSensorOnFlag) {
                btnCalibrateSensors.setVisibility(View.VISIBLE);
                btnCalibrateSensors.setActivated(true);
            } else {
                btnCalibrateSensors.setVisibility(View.INVISIBLE);
                btnCalibrateSensors.setActivated(false);
                accelerometerCalibrateValueX = 0;
            }
        });

        gyroscopeSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (btnSensorOnFlag) {
                    float gyroscopeValueX = (event.values[0]);
                    tvGyrMap.setText(String.valueOf(gyroscopeValueX));
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
                btnCalibrateSensors.setOnClickListener(v->accelerometerCalibrateValueX = event.values[2]);
                if (btnSensorOnFlag) {
                    if (flaga) {
                        tvAccMap.setText(String.valueOf((event.values[2] - accelerometerCalibrateValueX)));
                        if ((event.values[2] < 2 && event.values[2] >= 0) || (event.values[2] > -2 && event.values[2] <= 0)) {
                            loopFlagA = false;
                        }

                        if ((event.values[2] > 2 || event.values[2] < -2) && !loopFlagA) {
                            loopFlagA = true;
                            sensorBump(event.values[2]);
                        }
                        flaga = false;
                    }
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

    private void writeFile(double latitude, double longitude, int type) {
        try {
            /*
             * Создается объект файла, при этом путь к файлу находиться методом класcа Environment
             * Обращение идёт, как и было сказано выше к внешнему накопителю
             */
//            File myFile = new File(Environment.getExternalStorageDirectory().toString() + "/" + "bumps.txt");
//            myFile.createNewFile();
//            FileOutputStream outputStream = new FileOutputStream(myFile);

//            outputStream = getActivity().openFileOutput("bumps.txt", Context.MODE_APPEND);
//
//            try {
//                writer = new FileWriter(Environment.getExternalStorageDirectory().toString() + "/" + "bumps.txt", true);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            String latitudeStr = Double.toString(latitude);
            String longitudeStr = Double.toString(longitude);
            String typeStr = Integer.toString(type);
            String text = "\n" + latitudeStr + "," + longitudeStr + "," + typeStr;// После чего создаем поток для записи
//            writer.append(text);
////            outputStream.write(text.getBytes());                            // и производим непосредственно запись
//            outputStream.close();

            try {
                FileWriter writer = new FileWriter(Environment.getExternalStorageDirectory().toString() + "/" + "bumps.txt", true);
                BufferedWriter bufferWriter = new BufferedWriter(writer);
                bufferWriter.write(text);
                bufferWriter.close();
            }
            catch (IOException e) {
                System.out.println(e);
            }
            /*
             * Вызов сообщения Toast не относится к теме.
             * Просто для удобства визуального контроля исполнения метода в приложении
             */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readFile() {
        /*
         * Аналогично создается объект файла
         */
        File myFile = new File(Environment.getExternalStorageDirectory().toString() + "/" + "bumps.txt");
        try {
            FileInputStream inputStream = new FileInputStream(myFile);
            /*
             * Буфферезируем данные из выходного потока файла
             */
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            /*
             * Класс для создания строк из последовательностей символов
             */
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            try {
                /*
                 * Производим построчное считывание данных из файла в конструктор строки,
                 * Псоле того, как данные закончились, производим вывод текста в TextView
                 */
                while ((line = bufferedReader.readLine()) != null){
                    drawBumps(line);
//                    System.out.println(line);
                }
//                System.out.println(stringBuilder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void drawBumps(String str) {
//        mapView.getMap().getMapObjects().addPlacemark(new Point(43.10529, 131.87353), ImageProvider.fromBitmap(drawRedBump()));
//        mapView.getMap().getMapObjects().addPlacemark(new Point(43.10790, 131.87920), ImageProvider.fromBitmap(drawOrangeBump()));
        if (!str.matches("")) {
            String latitude, longitude, type = "";
            int index = 0;
            index = str.indexOf(',');
            latitude = str.substring(0, index);
            str = str.substring(index + 1);
            index = str.indexOf(',');
            longitude = str.substring(0, index);
            type = str.substring(index + 1);

            float latitudeFloat = Float.parseFloat(latitude);
            float longitudeFloat = Float.parseFloat(longitude);

            if (Integer.parseInt(type) == 1) {
                mapView.getMap().getMapObjects().addPlacemark(new Point(latitudeFloat, longitudeFloat), ImageProvider.fromBitmap(drawYellowBump()));
            } else if (Integer.parseInt(type) == 2) {
                mapView.getMap().getMapObjects().addPlacemark(new Point(latitudeFloat, longitudeFloat), ImageProvider.fromBitmap(drawOrangeBump()));
            } else if (Integer.parseInt(type) == 3) {
                mapView.getMap().getMapObjects().addPlacemark(new Point(latitudeFloat, longitudeFloat), ImageProvider.fromBitmap(drawRedBump()));
            } else System.out.println("Ошибка в drawBumps");
        }
    }

    private void sensorBump(float value) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Дорожный дефект")
                .setMessage("Вы проехали неровность?")
                .setCancelable(true)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      if ((value > 7 || value < -7)) {
//                          System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
//                        submitRequest(new Point(43.10529, 131.87353), new Point(43.10790, 131.87920));
                        mapView.getMap().getMapObjects().addPlacemark(userLocationLayer.cameraPosition().getTarget(), ImageProvider.fromBitmap(drawRedBump()));
                        writeFile(userLocationLayer.cameraPosition().getTarget().getLatitude(), userLocationLayer.cameraPosition().getTarget().getLongitude(), 3);
                        } else if ((value > 5 || value < -5)) {
//                          System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
//                          submitRequest(new Point(43.10529, 131.87353), new Point(43.10790, 131.87920));

                            mapView.getMap().getMapObjects().addPlacemark(userLocationLayer.cameraPosition().getTarget(), ImageProvider.fromBitmap(drawOrangeBump()));
                          writeFile(userLocationLayer.cameraPosition().getTarget().getLatitude(), userLocationLayer.cameraPosition().getTarget().getLongitude(), 2);
                      } else if ((value > 2 || value < -2)) {
//                          System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
//                          submitRequest(new Point(43.10529, 131.87353), new Point(43.10790, 131.87920));
//                          new Point(43.10862, 131.87653)
                            mapView.getMap().getMapObjects().addPlacemark(userLocationLayer.cameraPosition().getTarget(), ImageProvider.fromBitmap(drawYellowBump()));
                          writeFile(userLocationLayer.cameraPosition().getTarget().getLatitude(), userLocationLayer.cameraPosition().getTarget().getLongitude(), 1);
                      }
                        dialog.cancel();
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

        handler.post(processSensors);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(gyroscopeSensorEventListener);
        sensorManager.unregisterListener(accelerometerSensorEventListener);

        handler.removeCallbacks(processSensors);
    }

    @Override
    public void onObjectAdded(UserLocationView userLocationView) {
//        userLocationLayer.setAnchor(
//                new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.5)),
//                new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.83)));

        userLocationView.getArrow().setIcon(ImageProvider.fromResource(
                getContext(), R.drawable.user_location_arrow));

        readFile();

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

    @Override
    public void onSearchResponse(@NonNull Response response) {
        MapObjectCollection mapObjects = mapView.getMap().getMapObjects();
        mapObjects.clear();
        readFile();

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
                                            readFile();
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