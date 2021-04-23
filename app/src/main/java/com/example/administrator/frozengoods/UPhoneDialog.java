package com.example.administrator.frozengoods;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

class UPhoneDialog extends Dialog {
    private Button btn1,btn2;
    private Context m_context;
    String m_phone;
    private UPhoneDialog.OnCustomDialogListener customDialogListener;

    public interface OnCustomDialogListener{
        void back(String choose);
    }
    UPhoneDialog(Context context, UPhoneDialog.OnCustomDialogListener customDialogListener,String phone) {
        super(context);
        m_context=context;
        m_phone=phone;
        this.customDialogListener = customDialogListener;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_phone);
        //Window
        Window window = getWindow();
        assert window != null;
        window.setGravity( Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialogBottom);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        //call
        btn1 = (Button) findViewById(R.id.btn_call);
        btn1.setText(m_phone);
        btn1.setBackgroundColor(ContextCompat.getColor(m_context,R.color.white));
        btn1.setOnTouchListener(new View.OnTouchListener() {
            boolean click=false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        click=true;
                        btn1.setBackgroundColor(ContextCompat.getColor(m_context,R.color.nintygray));
                        break;
                    case MotionEvent.ACTION_UP:
                        if(click && event.getX()>0 && event.getX()<v.getWidth() &&
                                event.getY()>0 && event.getY()<v.getHeight()) {
                            customDialogListener.back(((Button)v).getText().toString());
                            UPhoneDialog.this.dismiss();
                        }
                        btn1.setBackgroundColor(ContextCompat.getColor(m_context,R.color.white));
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if(click) {
                            click = false;
                            btn1.setBackgroundColor(ContextCompat.getColor(m_context,R.color.white));
                        }
                        break;
                }
                return true;
            }
        });
        //cancel
        btn2 = (Button) findViewById(R.id.btn_cancel);
        btn2.setBackgroundColor(ContextCompat.getColor(m_context,R.color.white));
        btn2.setOnTouchListener(new View.OnTouchListener() {
            boolean click=false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        click=true;
                        btn2.setBackgroundColor(ContextCompat.getColor(m_context,R.color.nintygray));
                        break;
                    case MotionEvent.ACTION_UP:
                        if(click && event.getX()>0 && event.getX()<v.getWidth() &&
                                event.getY()>0 && event.getY()<v.getHeight()) {
                            UPhoneDialog.this.dismiss();
                        }
                        btn2.setBackgroundColor(ContextCompat.getColor(m_context,R.color.white));
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if(click) {
                            click = false;
                            btn2.setBackgroundColor(ContextCompat.getColor(m_context,R.color.white));
                        }
                        break;
                }
                return true;
            }
        });
    }
}