package com.example.administrator.frozengoods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class UActivity extends AppCompatActivity {
    protected TextView m_title;
    protected FrameLayout m_back;
    protected View m_back_over;
    protected Button m_ok;
    protected View m_ok_over;
    protected LinearLayout m_itemLayout;
    protected String m_tab;
    protected String m_id;

    protected void onCreate(Bundle savedInstanceState, boolean isOK){
        super.onCreate(savedInstanceState);
        m_tab = "";
        Intent intent = getIntent();
        if(intent!=null) {
            m_id = intent.getStringExtra("id");
        }
        else {
            finish();
            return;
        }
        if(!isOK){
            setContentView(R.layout.widget_back);
            m_title = (TextView) findViewById(R.id.title);
            m_back = (FrameLayout) findViewById(R.id.btn_back);
            m_back_over = findViewById(R.id.btn_back_over);
            m_itemLayout = (LinearLayout) findViewById(R.id.layout_content);
        }
        else{
            setContentView(R.layout.widget_back_ok);
            m_title = (TextView) findViewById(R.id.title);
            m_back = (FrameLayout) findViewById(R.id.btn_back);
            m_back_over = findViewById(R.id.btn_back_over);
            m_ok = (Button) findViewById(R.id.btn_ok);
            m_ok_over = findViewById(R.id.btn_ok_over);
            m_itemLayout = (LinearLayout) findViewById(R.id.layout_content);
        }

        m_back_over.getBackground().setAlpha(0);
        m_back.setOnTouchListener(new View.OnTouchListener() {
            boolean click = false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        m_back_over.getBackground().setAlpha(51);
                        click = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                                event.getY() > 0 && event.getY() < v.getHeight()) {
                            Back();
                        }
                        m_back_over.getBackground().setAlpha(0);
                        break;
                }
                return true;
            }
        });

        if(isOK) {
            m_ok_over.getBackground().setAlpha(0);
            m_ok.setOnTouchListener(new View.OnTouchListener() {
                boolean click = false;

                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //按钮按下逻辑
                            m_ok_over.getBackground().setAlpha(51);
                            click = true;
                            break;
                        case MotionEvent.ACTION_UP:
                            //按钮弹起逻辑
                            if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                                    event.getY() > 0 && event.getY() < v.getHeight()) {
                                Ok();
                            }
                            m_ok_over.getBackground().setAlpha(0);
                            break;
                    }
                    return true;
                }
            });
        }
    }
    protected void onDestroy(){
        m_title=null;
        m_back=null;
        m_back_over=null;
        m_ok=null;
        m_ok_over=null;
        m_itemLayout=null;
        m_tab=null;
        m_id=null;
        super.onDestroy();
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void Back() {
    }
    public void Ok() {
    }
    public void Turn(String label) {
    }

    void Message(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
