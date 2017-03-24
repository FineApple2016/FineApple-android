package com.fineapple.fineapple.data;

/**
 * Created by kksd0900 on 2017. 3. 24..
 */

public class HitObject {
    float x, y, z;
    float accelerationValue;

    public HitObject() {
        this(0, 0, 0);
    }

    public HitObject(float mX, float mY, float mZ) {
        this.x = mX;
        this.y = mY;
        this.z = mZ;
        this.accelerationValue = (float) (Math.sqrt((x*x)+(y*y)+(z*z))-85.0f) * 1000;
    }

    @Override
    public String toString() {
        return "hit obj is " + accelerationValue + "(" + x + " / " + y + " / " + z + ")";
    }
}
