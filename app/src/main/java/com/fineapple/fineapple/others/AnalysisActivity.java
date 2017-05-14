package com.fineapple.fineapple.others;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.fineapple.fineapple.R;
import com.fineapple.fineapple.data.HitObject;
import com.fineapple.fineapple.data.SampleDataGenerator;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class AnalysisActivity extends AppCompatActivity implements OnChartValueSelectedListener {
    AnalysisActivity activity;
    SampleDataGenerator generator;
    LineChart chart;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        this.activity = this;

        index = getIntent().getIntExtra("data", 0);

        chart = (LineChart) findViewById(R.id.chart);
        initChartView();
        workThread.start();
    }

    Thread workThread = new Thread() {
        @Override
        public void run() {
            try {
                AnalysisActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        generator = new SampleDataGenerator(activity, index);
                        ArrayList<Entry> dataEntry = makeEntryData();
                        setChartData(dataEntry);
                    }
                });
            } catch (final Exception ex) {
                Log.i("hansjin","Exception in thread");
            }
        }
    };

    void initChartView() {
        chart.setOnChartValueSelectedListener(this);
        chart.setTouchEnabled(true);
        chart.setBackgroundColor(Color.WHITE);
        chart.animateXY(1000, 1000);

        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.SQUARE);
        l.setTextSize(11f);
        l.setTextColor(Color.DKGRAY);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.DKGRAY);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
    }

    ArrayList<Entry> makeEntryData() {
        ArrayList<Entry> set = new ArrayList();
        int index = 0;

        for (HitObject dataItem : generator.data) {
            set.add(new Entry(index, dataItem.accelerationValue));
            index++;
        }
        return set;
    }


    void setChartData(ArrayList<Entry> entries) {
        LineDataSet lineDataSet;
        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            lineDataSet = (LineDataSet) chart.getData().getDataSetByIndex(0);
            lineDataSet.setValues(entries);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            lineDataSet = new LineDataSet(entries, "Values");
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
            chart.setData(data);
            chart.getData().notifyDataChanged();
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        chart.centerViewToAnimated(e.getX(), e.getY(), chart.getData().getDataSetByIndex(h.getDataSetIndex()).getAxisDependency(), 500);
    }

    @Override
    public void onNothingSelected() { }
}
