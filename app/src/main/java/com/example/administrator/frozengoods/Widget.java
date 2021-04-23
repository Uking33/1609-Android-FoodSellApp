package com.example.administrator.frozengoods;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import java.io.ByteArrayOutputStream;

public class Widget {
    static Drawable ByteToDrawable(byte[] in){
        Bitmap bm= BitmapFactory.decodeByteArray(in, 0, in.length);
        return new BitmapDrawable(MainActivity.MainAct.getResources(),bm);
    }
    static byte[] DrawableToByte(int id){
        ByteArrayOutputStream bao=new ByteArrayOutputStream();
        Bitmap bitmap=((BitmapDrawable) ContextCompat.getDrawable(MainActivity.MainAct,id)).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bao);
        return bao.toByteArray();
    }
}
