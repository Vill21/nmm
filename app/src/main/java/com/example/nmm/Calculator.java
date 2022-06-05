package com.example.nmm;

import android.graphics.PointF;

public class Calculator {
    static float euclideanDistance(PointF first, PointF second) {
        float dx = first.x - second.x;
        float dy = first.y - second.y;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }
}
