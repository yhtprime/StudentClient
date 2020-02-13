package com.dryht.mobile.Util;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Utils {
    public final static String generalUrl = "http://cd.dryht.cn:25200/android/";

    private List<String> randomColors = new ArrayList<>();

    public int createRandomColor() {
        String R, G, B;
        int r, g, b;
        Random random = new Random();
        r = random.nextInt(256);
        g = random.nextInt(256);
        b = random.nextInt(256);
        if (r * 0.299 + g * 0.578 + b * 0.114 < 115)
            return createRandomColor();
        R = Integer.toHexString(r).toUpperCase();
        G = Integer.toHexString(g).toUpperCase();
        B = Integer.toHexString(b).toUpperCase();
        R = R.length() == 1 ? "0" + R : R;
        G = G.length() == 1 ? "0" + G : G;
        B = B.length() == 1 ? "0" + B : B;
        String color = "#CC" + R + G + B;
        for (String s : randomColors)
            if (s.equals(color))
                return createRandomColor();
        randomColors.add(color);
        return Color.parseColor(color);
    }

}
