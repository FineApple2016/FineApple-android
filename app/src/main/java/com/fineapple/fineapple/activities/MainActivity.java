package com.fineapple.fineapple.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fineapple.fineapple.R;

public class MainActivity extends AppCompatActivity {
    Button realTime, dataBtn1, dataBtn2, dataBtn3, dataBtn4, dataBtn5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realTime = (Button) findViewById(R.id.real_time);
        realTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BTActivity.class);
                intent.putExtra("data", 1);
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

        dataBtn2 = (Button) findViewById(R.id.data_btn2);
        dataBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AnalysisActivity.class);
                intent.putExtra("data", 2);
                startActivity(intent);
            }
        });
        dataBtn3 = (Button) findViewById(R.id.data_btn3);
        dataBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AnalysisActivity.class);
                intent.putExtra("data", 3);
                startActivity(intent);
            }
        });
        dataBtn4 = (Button) findViewById(R.id.data_btn4);
        dataBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AnalysisActivity.class);
                intent.putExtra("data", 4);
                startActivity(intent);
            }
        });
        dataBtn5 = (Button) findViewById(R.id.data_btn5);
        dataBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AnalysisActivity.class);
                intent.putExtra("data", 5);
                startActivity(intent);
            }
        });
    }
}
