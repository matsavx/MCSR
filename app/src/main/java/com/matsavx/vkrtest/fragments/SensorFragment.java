package com.matsavx.vkrtest.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vkrtest.R;
import com.matsavx.vkrtest.database.DBHelper;
import com.matsavx.vkrtest.database.DbManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SensorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SensorFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private SensorEventListener gyroscopeSensorEventListener;
    private Sensor accelerometerSensor;
    private SensorEventListener accelerometerSensorEventListener;
    private int interval = 300;
    private boolean flagg = false;
    private boolean flaga = false;
    private Handler handler;
    private boolean loopFlagA = false;
    private boolean loopFlagG = false;
    private float accelerometerCalibrateValueX = 0;
    private DbManager dbManager;

    private float accToDb;
    private float gyrToDb;

    public SensorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SensorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SensorFragment newInstance(String param1, String param2) {
        SensorFragment fragment = new SensorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private final Runnable processSensors = new Runnable() {
        @Override
        public void run() {
            flagg = true;
            flaga = true;
            handler.postDelayed(this, interval);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sensor, container, false);
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        handler = new Handler();

//        DBHelper dbHelper = new DBHelper(getContext());
//        SQLiteDatabase database = dbHelper.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
        dbManager = new DbManager(getContext());

        TextView tvGX, tvGY, tvGZ, tvAX, tvAY, tvAZ, tvAResult, tvGResult, tvAccDb, tvGyrDb;
        Button btnCalibrateAccelerometer, btnClearSensorCounter, btnInsertToDb;
        tvGX = view.findViewById(R.id.tvGX);
        tvGY = view.findViewById(R.id.tvGY);
        tvGZ = view.findViewById(R.id.tvGZ);
        tvAX = view.findViewById(R.id.tvAX);
        tvAY = view.findViewById(R.id.tvAY);
        tvAZ = view.findViewById(R.id.tvAZ);
        tvAResult = view.findViewById(R.id.tvAResult);
        tvGResult = view.findViewById(R.id.tvGResult);
        tvAccDb = view.findViewById(R.id.dbAcc);
        tvGyrDb = view.findViewById(R.id.dbGyr);
        btnCalibrateAccelerometer = view.findViewById(R.id.btnCalibrateAccelerometer);
        btnClearSensorCounter = view.findViewById(R.id.btnClearSensorCounter);
        btnInsertToDb = view.findViewById(R.id.btnInsertDB);

        if (gyroscopeSensor == null) {
            Toast.makeText(getContext(), "The device has no gyroscope", Toast.LENGTH_SHORT).show();
            return view;
        }

        if (accelerometerSensor == null) {
            Toast.makeText(getContext(), "The device has no accelerometer", Toast.LENGTH_SHORT).show();
            return view;
        }

        final int[] countg = {0};
        final int[] counta = {0};
        accToDb = 0;
        gyrToDb = 0;

//        int gyroscopeValueX, gyroscopeValueY, gyroscopeValueZ;

        gyroscopeSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
//                if (flagg) {
                    float gyroscopeValueX = (event.values[0]);
                    int gyroscopeValueY = (int)(event.values[1]);
                    int gyroscopeValueZ = (int)(event.values[2]);
                    tvGX.setText(String.valueOf(gyroscopeValueX));
                    tvGY.setText(String.valueOf(gyroscopeValueY));
                    tvGZ.setText(String.valueOf(gyroscopeValueZ));

                    if ((gyroscopeValueX < 2 && gyroscopeValueX >= 0) || (gyroscopeValueX > -2 && gyroscopeValueX <= 0)) {
                        loopFlagG = false;
                    }

                    if ((gyroscopeValueX > 2 || gyroscopeValueX < -2) && !loopFlagG) {
                        countg[0]++;
                        loopFlagG = true;
                        tvGResult.setText("Считано неровностей " + countg[0]);
                        gyrToDb = gyroscopeValueX;
                    }

//                    flagg = false;
//                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        accelerometerSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                btnCalibrateAccelerometer.setOnClickListener(v -> accelerometerCalibrateValueX = event.values[2]);
//                if (flaga) {
                    tvAX.setText(String.valueOf((int)event.values[0]));
                    tvAY.setText(String.valueOf((int)event.values[1]));
                    tvAZ.setText(String.valueOf((int)(event.values[2]-accelerometerCalibrateValueX)));

                    if ((event.values[2] < 2 && event.values[2]>=0) || (event.values[2] > -2 && event.values[2] <= 0)) {
                        loopFlagA = false;
                    }

                    if ((event.values[2] > 2 || event.values[2] < -2) && !loopFlagA) {
                        counta[0]++;
                        loopFlagA = true;
                        tvAResult.setText("Считано неровностей " + counta[0]);
                        accToDb = event.values[2];
                    }

//                    flaga = false;
//                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

//        btnClearSensorCounter.setOnClickListener(v -> {
//            Cursor cursor = database.query(DBHelper.TABLE_BUMPS, null, null,null,null,null,null);
//            if(cursor.moveToFirst()){
//                int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
//                int idA = cursor.getColumnIndex(DBHelper.KEY_ACCELEROMETER);
//                int idG = cursor.getColumnIndex(DBHelper.KEY_GYROSCOPE);
//                do{
//                    Log.d("mLog", "ID = " + cursor.getInt(idIndex)
//                            + ", acc = " + cursor.getInt(idA)
//                            + ", gyro = " + cursor.getInt(idG));
//                } while (cursor.moveToNext());
//                cursor.close();
//            }
//        });
        btnClearSensorCounter.setOnClickListener(v->{
            counta[0] = 0;
            countg[0] = 0;
            tvAResult.setText(String.valueOf(counta[0]));
            tvGResult.setText(String.valueOf(countg[0]));
        });

        btnInsertToDb.setOnClickListener(v->{
            dbManager.insertToDb(accToDb, gyrToDb, 0,0,0);
            for (float acc : dbManager.getFromDb()) {
                tvAccDb.append(String.valueOf(acc));
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(gyroscopeSensorEventListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(accelerometerSensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

        dbManager.openDb();

        handler.post(processSensors);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(gyroscopeSensorEventListener);
        sensorManager.unregisterListener(accelerometerSensorEventListener);

        handler.removeCallbacks(processSensors);
    }
}