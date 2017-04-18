package com.fineapple.fineapple.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fineapple.fineapple.R;
import com.fineapple.fineapple.bt.MyService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * Created by kksd0900 on 2017. 4. 18..
 */

public class BTActivity extends Activity {
    private static final String TAG = "bluetooth2";

    TextView txtArduino;
    ScrollView scroll;
    Handler h;

    final int RECIEVE_MESSAGE = 1;
    private BluetoothAdapter btAdapter = null;
    private MyService mService;
    private BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder();

    private ConnectedThread mConnectedThread;

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static String address = "20:16:06:29:83:05"; //HC-06 (블루투스 모듈) 의 맥어드레스 입니다
    int qq = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt);
        txtArduino = (TextView) findViewById(R.id.txtArduino);        // for display the received data from the Arduino

        scroll = (ScrollView) findViewById(R.id.scroll);

        txtArduino.setText("Ready..");
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                qq++;
                int xyzIndex = 0;
                float x, y, z;

                switch (msg.what) {
                    case RECIEVE_MESSAGE:
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);
                        sb.append(strIncom);
                        Log.d("hansjin", sb.toString());
                        if (strIncom.contains("\n")) {

                            String inputs = sb.toString();
                            StringTokenizer stringTokenizer = new StringTokenizer(inputs, "\n");
                            while(stringTokenizer.hasMoreTokens()) {
                                xyzIndex = 0;
                                x = -10.0f;
                                y = -10.0f;
                                z = -10.0f;

                                String oneLine = stringTokenizer.nextToken();
                                oneLine = oneLine.replaceAll(" ", "");

                                StringTokenizer innerToken = new StringTokenizer(oneLine, ",");
                                while(innerToken.hasMoreTokens()) {
                                    try {
                                        if (xyzIndex == 0) {
                                            x = Float.parseFloat(innerToken.nextToken());
                                        } else if (xyzIndex == 1) {
                                            y = Float.parseFloat(innerToken.nextToken());
                                        } else if (xyzIndex == 2) {
                                            z = Float.parseFloat(innerToken.nextToken());
                                        } else {
                                            break;
                                        }
                                        if (x != -10.0f && y != -10.0f && z != -10.0f) {
                                            String obj = "input : " + x + " , " + y + " , " + z;
                                            float objValue = (float) (Math.sqrt((x*x)+(y*y)+(z*z)));
                                            Log.d("hansjin", obj);
                                            txtArduino.setText("sequence : " + qq+"\n" + obj + "+\n" + "value:" + objValue);
                                        } else {
                                            Log.d("hansjin", "parse ERROR, go to the next token");
                                        }
                                        xyzIndex++;
                                    } catch (Exception e) {
                                        Log.d("hansjin", "parse ERROR, go to the next token");
                                    }
                                }
                            }





                            sb = new StringBuilder();
                        }
                        break;
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();        // get Bluetooth adapter
        checkBTState();
    }











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
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);		// Get number of bytes and message in "buffer"
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();		// Send to message queue Handler
                } catch (IOException e) {
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