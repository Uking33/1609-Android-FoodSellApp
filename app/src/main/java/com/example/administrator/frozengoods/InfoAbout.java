package com.example.administrator.frozengoods;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class InfoAbout extends AppCompatActivity {
    protected FrameLayout m_back;
    private View m_back_over;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_about);
        m_back = (FrameLayout) findViewById(R.id.info_about_btn_back);
        m_back_over = findViewById(R.id.info_about_btn_back_over);

        assert m_back_over != null;
        m_back_over.getBackground().setAlpha(0);
        m_back.setOnTouchListener(new View.OnTouchListener() {
            boolean click = false;

            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //按钮按下逻辑
                        m_back_over.getBackground().setAlpha(51);
                        click = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        //按钮弹起逻辑
                        if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                                event.getY() > 0 && event.getY() < v.getHeight()) {
                            finish();
                        }
                        m_back_over.getBackground().setAlpha(0);
                        break;
                }
                return true;
            }
        });
    }
}
