package com.example.administrator.frozengoods;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

class UTextView {
    private TextView m_text;
    UTextView(final AppCompatActivity parent, String label, int size, int color){//文字
        m_text=new TextView(parent);
        m_text.setIncludeFontPadding(false);
        m_text.setText(label);
        m_text.setTextColor(ContextCompat.getColor(parent,color));
        m_text.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
        FrameLayout.LayoutParams Params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params1.gravity = Gravity.START|Gravity.CENTER_VERTICAL;
        Params1.leftMargin=30;
    }
    public void addToView(LinearLayout itemLayout, int topMargin,int leftMargin){
        LinearLayout.LayoutParams Params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params1.topMargin=topMargin;
        Params1.leftMargin=leftMargin;
        itemLayout.addView(m_text,Params1);
    }
}
