package com.fineapple.fineapple.data;

import android.content.Context;
import android.util.Log;

import com.fineapple.fineapple.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by kksd0900 on 2017. 3. 24..
 */

public class SampleDataGenerator {
    private Context context;
    public ArrayList<HitObject> data;

    public SampleDataGenerator(Context mContext) {
        this.context = mContext;
        data = new ArrayList<HitObject>();
        try {
            readData(context, R.raw.hittestdata10_1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readData(Context context, int file) throws IOException {
        InputStream is = context.getResources().openRawResource(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String mLine = reader.readLine();

        while (mLine != null) {
            sb.append(mLine);
            mLine = reader.readLine();
            StringTokenizer parser = new StringTokenizer(mLine, ",");

            int index = 0;
            float x = 0, y = 0, z = 0;
            while(parser.hasMoreTokens()) {
                if (index == 6) {
                    x = Float.parseFloat(parser.nextToken());
                } else if (index == 7) {
                    y = Float.parseFloat(parser.nextToken());
                } else if (index == 8) {
                    z = Float.parseFloat(parser.nextToken());
                } else {
                    parser.nextToken();
                }
                index++;
            }
            HitObject hitObject = new HitObject(x, y, z);
            Log.d("hansjin", hitObject.toString());
            data.add(hitObject);
        }
        reader.close();
    }
}