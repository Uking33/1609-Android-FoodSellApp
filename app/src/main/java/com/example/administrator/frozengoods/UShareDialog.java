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
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

class UShareDialog extends Dialog {
    private Button btn;
    private Context m_context;
    private UShareDialog.OnCustomDialogListener customDialogListener;

    public interface OnCustomDialogListener{
        void back(String choose);
    }
    UShareDialog(Context context,UShareDialog.OnCustomDialogListener customDialogListener) {
        super(context);
        m_context=context;
        this.customDialogListener = customDialogListener;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_share);
        //Window
        Window window = getWindow();
        assert window != null;
        window.setGravity( Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialogBottom);
        window.getDecorView().setPadding(0, 0, 0, 0);
        android.view.WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        //Btn
        List<ImageView> Btn = new ArrayList<>();
        Btn.add((ImageView) findViewById(R.id.share_btn1));
        Btn.add((ImageView) findViewById(R.id.share_btn2));
        Btn.add((ImageView) findViewById(R.id.share_btn3));
        Btn.add((ImageView) findViewById(R.id.share_btn4));
        Btn.add((ImageView) findViewById(R.id.share_btn5));
        Btn.add((ImageView) findViewById(R.id.share_btn6));
        Btn.add((ImageView) findViewById(R.id.share_btn7));
        for(int i = 0; i< Btn.size(); i++)
            Btn.get(i).setOnTouchListener(new View.OnTouchListener() {
                boolean click=false;
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            click=true;
                            break;
                        case MotionEvent.ACTION_UP:
                            if(click && event.getX()>0 && event.getX()<v.getWidth() &&
                                    event.getY()>0 && event.getY()<v.getHeight()) {
                                UShareDialog.this.dismiss();
                                customDialogListener.back("");
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
        //cancel
        btn = (Button) findViewById(R.id.btn_cancel);
        btn.setBackgroundColor(ContextCompat.getColor(m_context,R.color.white));
        btn.setOnTouchListener(new View.OnTouchListener() {
            boolean click=false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        click=true;
                        btn.setBackgroundColor(ContextCompat.getColor(m_context,R.color.nintygray));
                        break;
                    case MotionEvent.ACTION_UP:
                        if(click && event.getX()>0 && event.getX()<v.getWidth() &&
                                event.getY()>0 && event.getY()<v.getHeight()) {
                            UShareDialog.this.dismiss();
                        }
                        btn.setBackgroundColor(ContextCompat.getColor(m_context,R.color.white));
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if(click) {
                            click = false;
                            btn.setBackgroundColor(ContextCompat.getColor(m_context,R.color.white));
                        }
                        break;
                }
                return true;
            }
        });
    }
}