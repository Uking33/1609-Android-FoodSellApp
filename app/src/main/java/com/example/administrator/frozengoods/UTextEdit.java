package com.example.administrator.frozengoods;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

class UTextEdit {
    private AppCompatActivity m_parent;
    FrameLayout m_layout;
    EditText m_edit;
    private TextView m_text;
    View m_line;
    //带标题输入框
    UTextEdit(AppCompatActivity parent, int id, String label, String tips, int type, int size, int editWidth, int spacing){
        this(parent,id,label,tips,type,size,editWidth,spacing,true);
    }
    UTextEdit(final AppCompatActivity parent, int id, String label, String tips, int type, int size, int editWidth, int spacing,boolean isEdit){
        m_parent=parent;
        //layout
        m_layout = new FrameLayout(m_parent);
        //Label
        m_text=new TextView(m_parent);
        m_text.setIncludeFontPadding(false);
        m_text.setText(label);
        m_text.setTextColor(ContextCompat.getColor(m_parent,R.color.fortygray));
        m_text.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
        FrameLayout.LayoutParams Params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params1.gravity = Gravity.START|Gravity.CENTER_VERTICAL;
        m_layout.addView(m_text,Params1);
        //Edit
        m_edit=new EditText(m_parent);
        m_edit.setId(id);
        m_edit.setSingleLine(true);
        m_edit.setHint(tips);
        m_edit.setInputType(type);
        m_edit.setHintTextColor(ContextCompat.getColor(m_parent,R.color.eghitygray));
        m_edit.setTextColor(ContextCompat.getColor(m_parent,R.color.fortygray));
        m_edit.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
        m_edit.setWidth(editWidth);
        //noinspection deprecation
        m_edit.setBackgroundDrawable(null);
        if(type == (InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD)) {//password
            m_edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
            m_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText edit=(EditText)v;
                    edit.setText(edit.getText().toString());
                    edit.selectAll();
                }
            });
        }
        FrameLayout.LayoutParams Params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params2.gravity =Gravity.START|Gravity.CENTER_VERTICAL;
        Params2.leftMargin=spacing;
        m_layout.addView(m_edit,Params2);
        //line
        m_line =new View(m_parent);
        m_line.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.eghitygray));
        FrameLayout.LayoutParams Params3 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1);
        Params3.gravity =Gravity.BOTTOM;
        m_layout.addView(m_line,Params3);
        //Listening
        if(isEdit) {
            m_edit.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        m_line.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.textblue));
                    } else {
                        m_line.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.eghitygray));
                    }
                }
            });
        }
        else{
            m_edit.setFocusable(false);
            m_edit.setFocusableInTouchMode(false);
        }
    }

    //无标题输入框
    UTextEdit(final AppCompatActivity parent, String value, String tips, int type, int size){
        m_parent=parent;
        //layout
        m_layout = new FrameLayout(m_parent);
        //Label
        m_text=null;
        //Edit
        m_edit=new EditText(m_parent);
        m_edit.setSingleLine(true);
        m_edit.setHint(tips);
        m_edit.setText(value);
        m_edit.setSingleLine(true);
        m_edit.setInputType(type);
        m_edit.setHintTextColor(ContextCompat.getColor(m_parent,R.color.eghitygray));
        m_edit.setTextColor(ContextCompat.getColor(m_parent,R.color.fortygray));
        m_edit.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
        //noinspection deprecation
        m_edit.setBackgroundDrawable(null);
        if(type == (InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD)) {//password
            m_edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
            m_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText edit=(EditText)v;
                    edit.setText(edit.getText().toString());
                    edit.selectAll();
                }
            });
        }
        FrameLayout.LayoutParams Params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params2.gravity =Gravity.START|Gravity.CENTER_VERTICAL;
        m_layout.addView(m_edit,Params2);
        //line
        m_line =new View(m_parent);
        m_line.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.eghitygray));
        FrameLayout.LayoutParams Params3 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1);
        Params3.gravity =Gravity.BOTTOM;
        m_layout.addView(m_line,Params3);
        //Listening
        m_edit.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    m_line.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.textblue));
                } else {
                    m_line.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.eghitygray));
                }
            }
        });
    }

    public void addToView(LinearLayout itemLayout, int height, int margin){
        //layout
        LinearLayout.LayoutParams Params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height);
        Params1.leftMargin=margin;
        Params1.rightMargin=margin;
        itemLayout.addView(m_layout,Params1);
    }
    public void addToView(FrameLayout itemLayout, int width, int height, int gravity){
        //layout
        FrameLayout.LayoutParams Params1 = new FrameLayout.LayoutParams(width,height);
        Params1.gravity=gravity;
        itemLayout.addView(m_layout,Params1);
    }
}
