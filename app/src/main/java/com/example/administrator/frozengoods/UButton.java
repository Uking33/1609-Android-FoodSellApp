package com.example.administrator.frozengoods;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

class UButton {
    private AppCompatActivity m_parent;
    Button m_btn;
    private int[] m_textColor;
    private int[] m_backgroundColor;
    private int m_height,m_width;

    UButton(final AppCompatActivity parent, int id, String text, int size,int width, int height, int[] textColor, int[] backgroundColor){
        m_parent=parent;
        //btn
        m_btn=new Button(m_parent);
        m_btn.setPadding(0,0,0,0);
        m_height=height;
        m_width=width;
        if(width!=0) m_btn.setWidth(width);
        if(height!=0){
            m_btn.setHeight(height);
        }
        m_btn.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
        m_btn.setText(text);
        m_btn.setId(id);
        m_textColor=textColor;
        m_backgroundColor=backgroundColor;
        m_btn.setTextColor(ContextCompat.getColor(m_parent,m_textColor[0]));
        m_btn.setBackgroundResource(m_backgroundColor[0]);
        if(m_textColor[1]!=0){
            m_btn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction()==MotionEvent.ACTION_DOWN){
                        ((Button)v).setTextColor(ContextCompat.getColor(m_parent,m_textColor[1]));
                        v.setBackgroundResource(m_backgroundColor[1]);
                    }else if(event.getAction()==MotionEvent.ACTION_UP){
                        ((Button)v).setTextColor(ContextCompat.getColor(m_parent,m_textColor[0]));
                        v.setBackgroundResource(m_backgroundColor[0]);
                    }else if(event.getAction()==MotionEvent.ACTION_CANCEL){
                        ((Button)v).setTextColor(ContextCompat.getColor(m_parent,m_textColor[0]));
                        v.setBackgroundResource(m_backgroundColor[0]);
                    }
                    return false;
                }
            });
        }
        else{
            m_btn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction()==MotionEvent.ACTION_DOWN){
                        ((Button)v).setTextColor(ContextCompat.getColor(m_parent,m_textColor[0]));
                        v.setBackgroundResource(m_backgroundColor[0]);
                    }else if(event.getAction()==MotionEvent.ACTION_UP){
                        ((Button)v).setTextColor(ContextCompat.getColor(m_parent,m_textColor[0]));
                        v.setBackgroundResource(m_backgroundColor[0]);
                    }
                    return false;
                }
            });
        }
    }
    public void addToView(LinearLayout itemLayout, int topmargin){
        LinearLayout.LayoutParams Params1 = new LinearLayout.LayoutParams(m_width,m_height);
        Params1.gravity = Gravity.CENTER;
        Params1.topMargin=topmargin;
        itemLayout.addView(m_btn,Params1);
    }
    public void addToView(FrameLayout itemLayout, int rightmargin){
        //layout
        FrameLayout.LayoutParams Params1 = new FrameLayout.LayoutParams(m_width,m_height);
        Params1.gravity =Gravity.END|Gravity.CENTER_VERTICAL;
        Params1.rightMargin=rightmargin;
        itemLayout.addView(m_btn,Params1);
    }
}