package com.fineapple.fineapple.bt;

/**
 * Created by kksd0900 on 2017. 4. 18..
 */

import android.os.Handler;

/**
 * Parse stream and extract accel data
 * @author Administrator
 */
public class TransactionReceiver {
    private static final String TAG = "TransactionReceiver";

    private static final int PARSE_MODE_ERROR = 0;
    private static final int PARSE_MODE_WAIT_START_BYTE = 1;
    private static final int PARSE_MODE_WAIT_COMMAND = 2;
    private static final int PARSE_MODE_WAIT_DATA = 3;
    private static final int PARSE_MODE_WAIT_END_BYTE = 4;
    private static final int PARSE_MODE_COMPLETED = 101;

    private Handler mHandler = null;



    public TransactionReceiver(Handler h) {
        mHandler = h;
        reset();
    }


    /**
     * Reset transaction receiver.
     */
    public void reset() {
    }

    /**
     * Set bytes to parse
     * This method automatically calls parseStream()
     * @param buffer
     * @param count
     */
    public void setByteArray(byte[] buffer, int count) {
        parseStream(buffer, count);
    }

    /**
     * After parsing bytes received, transaction receiver makes object instance.
     * This method returns parsed results
     * @return	Object		parsed object
     */
    public Object getObject() {
        // TODO: return what you want
        return null;
    }

    /**
     * Caching received stream and parse byte array
     * @param buffer		byte array to parse
     * @param count			byte array size
     */
    public void parseStream(byte[] buffer, int count) {
        if(buffer != null && buffer.length > 0 && count > 0) {
            for(int i=0; i < buffer.length && i < count; i++) {

                // Parse received data
                // Protocol description -----------------------------------------------------------
                // Describe brief info about protocol

                // TODO: parse buffer


            }	// End of for loop
        }	// End of if()
    }	// End of parseStream()


}
