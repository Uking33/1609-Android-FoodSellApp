package com.example.administrator.frozengoods;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


public class UnKnow  extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unclock);
        TextView itemLayout = (TextView)findViewById(R.id.unlock_table);
        assert itemLayout != null;
        itemLayout.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //按钮按下逻辑
                        v.setBackgroundColor(Color.parseColor("#E6E6E6"));
                        break;
                    case MotionEvent.ACTION_UP:
                        //按钮弹起逻辑
                        v.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        finish();
                        break;
                }
                return true;
            }
        });
    }
}
