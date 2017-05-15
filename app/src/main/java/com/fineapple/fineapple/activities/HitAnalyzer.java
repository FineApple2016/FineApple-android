package com.fineapple.fineapple.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kksd0900 on 2017. 5. 13..
 */

public class HitAnalyzer {
    Context context;
    Activity activity;

    double ignoreUnit = 0.06f;
    int intervalSize = 15;


    public HitAnalyzer(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void analysis(ArrayList<Float> data, TextView resultTV, TextView hitTV, Button btn_hit, Button btn_swing) {
        if (data == null || data.size() == 0) {
            resultTV.setText("ERROR");
            return;
        }
        ArrayList<Integer> vectors = transformToVectors(data);
        int hitIndex = isHit(data, vectors, hitTV, btn_hit, btn_swing);
        resultTV.setText(vectorsToString(vectors, hitIndex));
    }

    int isHit(ArrayList<Float> data, ArrayList<Integer> vectors, TextView hitTV, Button btn_hit, Button btn_swing) {
        /*
            vector= 0 // 감소
                    1 // 변화 없음
                    2 // 증가
        */

        int indexOfMaxIntervalSize = 0;
        int maxCountChangeVector = 0;
        for (int i=0; i<vectors.size()-intervalSize; i++) {
            int countChangeVector = 0;
            int compareVector = vectors.get(i);
            for (int j=1; j<intervalSize; j++) {
                int currentVector = vectors.get(i+j);

                if (currentVector == 1) {
                    continue;
                }
                if (currentVector != compareVector) {
                    countChangeVector++;
                    compareVector = currentVector;
                }
            }
            if (maxCountChangeVector < countChangeVector) {
                maxCountChangeVector = countChangeVector;
                indexOfMaxIntervalSize = i;
            }
        }
        double totalDifference = 0;
        for (int i=1; i<intervalSize; i++) {
            float previousValue = data.get(indexOfMaxIntervalSize+i-1);
            float currentValue = data.get(indexOfMaxIntervalSize+i);
            double differenceOfTwoValue = Math.abs(currentValue - previousValue);
            totalDifference += differenceOfTwoValue;
        }
        hitTV.setText("진동 발생 인덱스 : " + indexOfMaxIntervalSize +
                ", 진동수 : " + maxCountChangeVector +
                ", 총 진동값 : " + totalDifference);

        if (indexOfMaxIntervalSize > 50 &&
                indexOfMaxIntervalSize < 70 &&
                maxCountChangeVector > 3 &&
                totalDifference > 10.0) {
            setHit(btn_hit, btn_swing);
        } else {
            setSwing(btn_hit, btn_swing);
        }

        return indexOfMaxIntervalSize;
    }

    ArrayList<Integer> transformToVectors(ArrayList<Float> data) {
        ArrayList<Integer> vectors = new ArrayList();

        for (int i=1; i<data.size(); i++) {
            float previousSVM = data.get(i - 1);
            float currentSVM = data.get(i);

            if (Math.abs(previousSVM - currentSVM) < ignoreUnit) {
                vectors.add(1); // 변화 없음
                continue;
            }

            if (previousSVM <= currentSVM) {
                vectors.add(2); // 증
            } else {
                vectors.add(0); // 감
            }
        }
        return vectors;
    }

    String vectorsToString(ArrayList<Integer> vectors, int hitIndex) {
        String str = "[";
        for (int i=0; i<vectors.size(); i++) {
            if (i == hitIndex) {
                str += "*";
            }
            if (i == hitIndex + intervalSize) {
                str += "*";
            }
            if (vectors.get(i) == 0) {
                str += ("▽");
            } else if (vectors.get(i) == 1) {
                str += ("▲");
            } else if (vectors.get(i) == 2) {
                str += ("▲");
            }
        }
        return str += "]";
    }

    void setHit(Button btn_hit, Button btn_swing) {
        btn_hit.setBackgroundColor(Color.rgb(255, 65, 129));
        btn_hit.setTextColor(Color.WHITE);
        btn_swing.setBackgroundColor(Color.WHITE);
        btn_swing.setTextColor(Color.BLACK);
    }

    void setSwing(Button btn_hit, Button btn_swing) {
        btn_hit.setBackgroundColor(Color.WHITE);
        btn_hit.setTextColor(Color.BLACK);
        btn_swing.setBackgroundColor(Color.rgb(255, 65, 129));
        btn_swing.setTextColor(Color.WHITE);
    }
}
