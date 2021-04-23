package com.example.administrator.frozengoods;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

class UDialog extends Dialog {
    LinearLayout layout;
    MainActivity m_parent;

    public interface OnCustomDialogListener{
        void back(String choose);
    }

    private OnCustomDialogListener customDialogListener;
    int i;
    int num;
    private String[] texts;

    UDialog(Context context,OnCustomDialogListener customDialogListener,int num, String[] texts) {
        super(context);
        this.customDialogListener = customDialogListener;
        this.num=num;
        this.texts=texts;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(num>=6)
            setContentView(R.layout.dialog_normal_layout_six);
        else
            setContentView(R.layout.dialog_normal_layout);
        m_parent=MainActivity.MainAct;
        layout = (LinearLayout) findViewById(R.id.dialog_choose);
        initBtn(num,texts);
    }
    private void initBtn(int num, String[] texts){
        Button[] Btn = new Button[num];
        for(i=0;i<num;i++) {
            Btn[i]=new Button(m_parent);
            Btn[i].setWidth(427);
            Btn[i].setHeight(111);
            Btn[i].setBackgroundColor(ContextCompat.getColor(m_parent,R.color.white));
            Btn[i].setTextColor(ContextCompat.getColor(m_parent,R.color.textblue));
            Btn[i].setText(texts[i]);
            Btn[i].setTextSize(TypedValue.COMPLEX_UNIT_PX,40);
            Btn[i].setOnTouchListener(new View.OnTouchListener() {
                boolean click=false;
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //按钮按下逻辑
                            v.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.nintygray));
                            click=true;
                            break;
                        case MotionEvent.ACTION_UP:
                            v.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.white));
                            //按钮弹起逻辑
                            if(click && event.getX()>0 && event.getX()<v.getWidth() &&
                                    event.getY()>0 && event.getY()<v.getHeight()) {
                                customDialogListener.back(((Button)v).getText().toString());
                                UDialog.this.dismiss();
                            }
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            //按钮弹起逻辑
                            if(click) {
                                click = false;
                                v.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.white));
                            }
                            break;
                    }
                    return true;
                }
            });
            layout.addView(Btn[i]);
        }
    }
}