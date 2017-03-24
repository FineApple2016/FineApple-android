package com.fineapple.fineapple.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.fineapple.fineapple.R;
import com.fineapple.fineapple.data.SampleDataGenerator;

public class AnalysisActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        SampleDataGenerator generator = new SampleDataGenerator(this);
        Log.d("hansjin", "data size is " + generator.data.size());
    }

}
