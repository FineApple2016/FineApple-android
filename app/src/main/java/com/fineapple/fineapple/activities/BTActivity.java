package com.fineapple.fineapple.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fineapple.fineapple.R;
import com.fineapple.fineapple.bt.MyService;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * Created by kksd0900 on 2017. 4. 18..
 */

public class BTActivity extends Activity implements OnChartValueSelectedListener {
    private static final String TAG = "bluetooth2";

    Handler handler;
    LineChart chart, lineChart;
    TextView resultTV;
    Button btn_hit, btn_swing;

    final int RECIEVE_MESSAGE = 1;
    private BluetoothAdapter btAdapter = null;
    private MyService mService;
    private BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder();

    private ConnectedThread mConnectedThread;

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static String address = "20:16:06:29:83:05"; //HC-06 (블루투스 모듈) 의 맥어드레스 입니다

    boolean isLimitMode = false;
    ArrayList<Float> limitArray = new ArrayList();
    int sizeOfLimit = 150;
    int streamOfLimit = 0;

    int totalCount = 0;
    int successCount = 0;
    int errorFormat = 0;
    int errorCasting = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt);

        resultTV = (TextView) findViewById(R.id.resultTV);

        btn_hit = (Button) findViewById(R.id.btn_hit);
        btn_hit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResultHIT();
            }
        });
        btn_swing = (Button) findViewById(R.id.btn_swing);
        btn_swing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResultSwing();
            }
        });

        initLineChartView();
        initChartView();

        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);
                        sb.append(strIncom);

                        if (sb.toString().charAt(sb.toString().length()-1) == '/') {
                            String inputs = sb.toString();
                            sb = new StringBuilder();
//                            Log.d("hansjin", inputs);
                            inputs = inputs.replaceAll(" ", "");

                            StringTokenizer stringTokenizer = new StringTokenizer(inputs, "/");
                            while(stringTokenizer.hasMoreTokens()) {
                                String token = stringTokenizer.nextToken();

                                int countChar = 0;

                                for (int i=0; i<token.length(); i++) {
                                    if (token.charAt(i) == '.') {
                                        countChar++;
                                    }
                                }
                                if (countChar == 1) {
                                    try {
                                        float value = Float.parseFloat(token);
                                        addEntry(value);
                                        successCount++;


                                        // LINE ANAL CHART
                                        limitArray.add(value);
                                        if (limitArray.size() > sizeOfLimit) {
                                            limitArray.remove(0);
                                        }
                                        if (!isLimitMode && value > 9.0f) {
                                            isLimitMode = true;
                                        }
                                        if (isLimitMode) {
                                            if (streamOfLimit > 100) {
                                                streamOfLimit = 0;
                                                isLimitMode = false;

                                                ArrayList<Float> lineData = new ArrayList();
                                                for (float a : limitArray) {
                                                    lineData.add(a);
                                                }
                                                Log.d("hansjin", "LIMIT = " + lineData.get(0));
                                                setLineChartData(makeLineEntryData(lineData));

                                                limitArray.clear();
                                                limitArray = new ArrayList();
                                            }
                                            streamOfLimit++;
                                        }
                                    } catch (Exception e) {
                                        errorCasting++;
                                        Log.d("hansjin", "errorCasting : " + token);
                                    }
                                } else {
                                    errorFormat++;
                                    Log.d("hansjin", "errorFormat : " + token);
                                }
                                totalCount++;
                            }
                            resultTV.setText(
                                    "receiving data.. " +
                                    "\n\nTotal:\t"+totalCount +
                                    "\nSuccess:\t" + successCount +
                                    "\nERROR(PARSE): " + errorFormat +
                                    "\nERROR(CAST): " + errorCasting);
                        }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();        // get Bluetooth adapter
        checkBTState();
    }

    void setResultHIT() {
        btn_hit.setBackgroundColor(Color.rgb(255, 65, 129));
        btn_hit.setTextColor(Color.WHITE);
        btn_swing.setBackgroundColor(Color.WHITE);
        btn_swing.setTextColor(Color.BLACK);
    }

    void setResultSwing() {
        btn_hit.setBackgroundColor(Color.WHITE);
        btn_hit.setTextColor(Color.BLACK);
        btn_swing.setBackgroundColor(Color.rgb(255, 65, 129));
        btn_swing.setTextColor(Color.WHITE);
    }

    private void addEntry(float value) {
        LineData data = chart.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), value), 0);
            data.notifyDataChanged();

            chart.notifyDataSetChanged();
            // wide
            chart.setVisibleXRangeMaximum(200);
            chart.moveViewToX(data.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            // chart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    int lineColor = Color.rgb(80,80,80);
    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "SVM values");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(lineColor);
        set.setCircleColor(lineColor);
        set.setLineWidth(2f);
        set.setCircleRadius(1.5f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(lineColor);
        set.setValueTextColor(lineColor);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }


    void initLineChartView() {
        lineChart = (LineChart) findViewById(R.id.lineChart);
        lineChart.setBackgroundColor(Color.argb(1, 1, 1, 1));

        lineChart.setOnChartValueSelectedListener(this);
        lineChart.setTouchEnabled(true);
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.animateXY(1000, 1000);
        lineChart.getDescription().setEnabled(false);

        Legend l = lineChart.getLegend();
        l.setForm(Legend.LegendForm.SQUARE);
        l.setTextSize(11f);
        l.setTextColor(Color.DKGRAY);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.DKGRAY);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
    }

    ArrayList<Entry> makeLineEntryData(ArrayList<Float> list) {
        ArrayList<Entry> set = new ArrayList();
        int index = 0;

        for (float dataItem : list) {
            set.add(new Entry(index, dataItem));
            index++;
        }
        return set;
    }


    void setLineChartData(ArrayList<Entry> entries) {
        LineDataSet lineDataSet;
        if (lineChart.getData() != null && lineChart.getData().getDataSetCount() > 0) {
            lineDataSet = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            lineDataSet.setValues(entries);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            lineDataSet = new LineDataSet(entries, "SVM Values");
            lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            lineDataSet.setColor(Color.DKGRAY);
            lineDataSet.setCircleColor(Color.DKGRAY);
            lineDataSet.setLineWidth(0.5f);
            lineDataSet.setCircleRadius(2.0f);
            lineDataSet.setFillAlpha(65);
            lineDataSet.setFillColor(ColorTemplate.colorWithAlpha(Color.DKGRAY, 200));
            lineDataSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            lineDataSet.setDrawCircleHole(false);
            lineDataSet.setHighLightColor(Color.rgb(244, 117, 117));

            LineData data = new LineData(lineDataSet);
            data.setValueTextSize(12f);
            data.setHighlightEnabled(false);
            lineChart.setData(data);
            lineChart.getData().notifyDataChanged();
        }
        lineChart.invalidate();
    }

    void initChartView() {
        chart = (LineChart) findViewById(R.id.chart);
        chart.setOnChartValueSelectedListener(this);

        // enable description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);

        // set an alternative background color
        chart.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(lineColor);

        // add empty data
        chart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(lineColor);

        XAxis xl = chart.getXAxis();
        xl.setTextColor(lineColor);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(lineColor);
        leftAxis.setAxisMaximum(20f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
    }



    @Override
    public void onValueSelected(Entry e, Highlight h) {
        chart.centerViewToAnimated(e.getX(), e.getY(), chart.getData().getDataSetByIndex(h.getDataSetIndex()).getAxisDependency(), 500);
    }

    @Override
    public void onNothingSelected() { }




    /***********************
     *
     * BLUETOOTH MODULES...
     * @param device
     * @return
     * @throws IOException
     */

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    @Override
    public void onResume() {
        super.onResume();


        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
        }


        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting...");
        try {
            btSocket.connect();
            Log.d(TAG, "....Connection ok...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Create Socket...");

        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
    }




    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");
        /**  try     {
         btSocket.close();
         } catch (IOException e2) {
         errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
         }

         **/
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);		// Get number of bytes and message in "buffer"
                    handler.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();		// Send to message queue Handler
                    sleep(50);
                } catch (Exception e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
            }
        }
    }
}