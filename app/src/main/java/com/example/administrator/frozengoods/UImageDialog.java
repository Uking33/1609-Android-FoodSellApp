package com.example.administrator.frozengoods;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

class UImageDialog extends Dialog {
    Drawable drawable;


    UImageDialog(Context context, Drawable drawable) {
        super(context);
        this.drawable=drawable;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_normal_image);
        ImageView image = (ImageView) findViewById(R.id.dialog_imageView);
        image.setImageDrawable(drawable);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);


        image.setOnTouchListener(new View.OnTouchListener() {
            boolean click=false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        click=true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if(click && event.getX()>0 && event.getX()<v.getWidth() &&
                                event.getY()>0 && event.getY()<v.getHeight()) {
                            UImageDialog.this.dismiss();
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if(click) {
                            click = false;
                        }
                        break;
                }
                return true;
            }
        });
    }


}