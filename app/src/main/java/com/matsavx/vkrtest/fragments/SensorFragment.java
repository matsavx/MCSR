package com.matsavx.vkrtest.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vkrtest.R;

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
    private boolean flag = false;
    private Handler handler;

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
            flag = true;
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

        TextView tvGX, tvGY, tvGZ, tvAX, tvAY, tvAZ, tvAResult, tvGResult;
        tvGX = view.findViewById(R.id.tvGX);
        tvGY = view.findViewById(R.id.tvGY);
        tvGZ = view.findViewById(R.id.tvGZ);
        tvAX = view.findViewById(R.id.tvAX);
        tvAY = view.findViewById(R.id.tvAY);
        tvAZ = view.findViewById(R.id.tvAZ);
        tvAResult = view.findViewById(R.id.tvAResult);
        tvGResult = view.findViewById(R.id.tvGResult);

        if (gyroscopeSensor == null) {
            Toast.makeText(getContext(), "The device has no gyroscope", Toast.LENGTH_SHORT).show();
            return view;
        }

        if (accelerometerSensor == null) {
            Toast.makeText(getContext(), "The device has no accelerometer", Toast.LENGTH_SHORT).show();
            return view;
        }

        final int[] count = {0};

        gyroscopeSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                if (flag) {
                    tvGX.setText(String.valueOf((int)event.values[0]));
                    tvGY.setText(String.valueOf((int)event.values[1]));
                    tvGZ.setText(String.valueOf((int)event.values[2]));

                    if (event.values[0] > 3 || event.values[1] > 3 || event.values[2] > 3) {
                        count[0]++;
                        tvGResult.setText("Считано неровностей " + count[0]);
                    }

                    flag = false;
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        accelerometerSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                tvAX.setText(String.valueOf((int)event.values[0]));
                tvAY.setText(String.valueOf((int)event.values[1]));
                tvAZ.setText(String.valueOf((int)event.values[2]));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        return view;
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
}