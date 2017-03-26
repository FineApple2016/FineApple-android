package com.fineapple.fineapple.data;

/**
 * Created by kksd0900 on 2017. 3. 24..
 */

public class HitObject {
    public float accelerationValue;

    public HitObject() {
        this(0, 0, 0);
    }

    public HitObject(float x, float y, float z) {
        this.accelerationValue = (float) (Math.sqrt((x*x)+(y*y)+(z*z)));
    }

    @Override
    public String toString() {
        return "hit obj is " + accelerationValue;
    }
}
