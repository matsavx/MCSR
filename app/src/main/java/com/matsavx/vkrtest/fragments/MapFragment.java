package com.matsavx.vkrtest.fragments;

import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
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
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
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

import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements UserLocationObjectListener, Session.SearchListener, CameraListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MapView mapView;
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private UserLocationLayer userLocationLayer;

    private SearchManager searchManager;
    private Session searchSession;
    private EditText searchEdit;

    private ImageButton btnSetCameraLocation;


    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
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

    public Bitmap drawRedBump() {
        int picSize = 20;
        Bitmap bitmap = Bitmap.createBitmap(picSize, picSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        // отрисовка плейсмарка
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(picSize / 2, picSize / 2, picSize / 2, paint);
        // отрисовка текста
//        paint.setColor(Color.WHITE);
//        paint.setAntiAlias(true);
//        paint.setTextSize({Нужный размер текста});
//        paint.setTextAlign(Paint.Align.CENTER);
//        canvas.drawText(number, picSize / 2,
//                picSize / 2 - ((paint.descent() + paint.ascent()) / 2), paint);
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
        // отрисовка текста
//        paint.setColor(Color.WHITE);
//        paint.setAntiAlias(true);
//        paint.setTextSize({Нужный размер текста});
//        paint.setTextAlign(Paint.Align.CENTER);
//        canvas.drawText(number, picSize / 2,
//                picSize / 2 - ((paint.descent() + paint.ascent()) / 2), paint);
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

        mapView.getMap().setRotateGesturesEnabled(true);

        mapView.getMap().move(
                new CameraPosition(new Point(43.10562, 131.87353), 11.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH,0),
                null
        );

        MapKit mapKit = MapKitFactory.getInstance();
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

//        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
//        builder.setTitle("Пожалуйста, подтвердите проезд по неровности");
//        builder.setMessage("Если неровности не было - это сообщение исчезнет самостоятельно");
//        builder.setCancelable(true);
//        builder.setNegativeButton("Подтверждаю",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//
//        final AlertDialog dlg = builder.create();
//
//        dlg.show();
//
//        final Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            public void run() {
//                dlg.dismiss(); // when the task active then close the dialog
//                timer.cancel(); // also just top the timer thread, otherwise,
//                // you may receive a crash report
//            }
//        }, 5000);


        btnSetCameraLocation = view.findViewById(R.id.btnSetCameraLocation);
        btnSetCameraLocation.setOnClickListener(v->{
//            userLocationLayer.cameraPosition();
            moveCameraToPosition(userLocationLayer.cameraPosition().getTarget());

        });

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
    public void onObjectAdded(UserLocationView userLocationView) {
//        userLocationLayer.setAnchor(
//                new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.5)),
//                new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.83)));

        userLocationView.getArrow().setIcon(ImageProvider.fromResource(
                getContext(), R.drawable.user_location_arrow));

        mapView.getMap().getMapObjects().addPlacemark(new Point(43.10529, 131.87353), ImageProvider.fromBitmap(drawRedBump()));
        mapView.getMap().getMapObjects().addPlacemark(new Point(43.10790, 131.87920), ImageProvider.fromBitmap(drawOrangeBump()));


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
                        builder.setTitle("Пожалуйста, подтвердите проезд по неровности");
                        builder.setMessage("Если неровности не было - это сообщение исчезнет самостоятельно");
                        builder.setCancelable(true);
                        builder.setNegativeButton("Подтверждаю",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
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
}