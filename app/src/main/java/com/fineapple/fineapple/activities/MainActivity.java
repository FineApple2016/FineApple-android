package com.fineapple.fineapple.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fineapple.fineapple.R;

public class MainActivity extends AppCompatActivity {
    Button realTime, graphBtn, dataBtn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realTime = (Button) findViewById(R.id.real_time);
        realTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BTActivity.class);
                startActivity(intent);
            }
        });

        graphBtn = (Button) findViewById(R.id.graph_btn);
        graphBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GraphActivity.class);
                startActivity(intent);
            }
        });

        dataBtn1 = (Button) findViewById(R.id.data_btn1);
        dataBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AnalysisActivity.class);
                intent.putExtra("data", 1);
                startActivity(intent);
            }
        });
    }
}
