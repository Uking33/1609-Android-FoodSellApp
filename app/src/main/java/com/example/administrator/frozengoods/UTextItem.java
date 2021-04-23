package com.example.administrator.frozengoods;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

class UTextItem {
    private AppCompatActivity m_parent;
    FrameLayout m_layout;
    private TextView m_hints;
    private TextView m_text;
    private TextView m_rightDrop;
    private View m_line;
    private final String m_label;
    private String m_parentClass;


    UTextItem(final AppCompatActivity parent, String parentClass, String label,  int num, int labelSize, int numSize){//消息
        m_parent=parent;
        m_parentClass=parentClass;
        m_label=label;
        //layout
        m_layout = new FrameLayout(m_parent);
        m_layout.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.white));
        //Label
        m_text=new TextView(m_parent);
        m_text.setIncludeFontPadding(false);
        m_text.setText(label);
        m_text.setTextColor(ContextCompat.getColor(m_parent,R.color.fortygray));
        m_text.setTextSize(TypedValue.COMPLEX_UNIT_PX,labelSize);
        FrameLayout.LayoutParams Params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params1.gravity = Gravity.START|Gravity.CENTER_VERTICAL;
        Params1.leftMargin=30;
        m_layout.addView(m_text,Params1);
        if(num>0)
        {
            //frame
            FrameLayout m_f=new FrameLayout(m_parent);
            m_f.setBackgroundResource(R.drawable.icon_redpoint);
            //num
            m_hints = new TextView(m_parent);
            m_hints.setIncludeFontPadding(false);
            m_hints.setText(String.valueOf(num));
            m_hints.setTextColor(ContextCompat.getColor(m_parent,R.color.white));
            m_hints.setTextSize(TypedValue.COMPLEX_UNIT_PX, numSize);
            FrameLayout.LayoutParams p2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            p2.gravity = Gravity.CENTER;
            m_f.addView(m_hints, p2);

            FrameLayout.LayoutParams Params3 = new FrameLayout.LayoutParams(27,27);
            Params3.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
            Params3.rightMargin = 50;
            m_layout.addView(m_f, Params3);
        }

        m_rightDrop = new TextView(m_parent);
        m_rightDrop.setIncludeFontPadding(false);
        m_rightDrop.setText(">");
        m_rightDrop.setTextColor(ContextCompat.getColor(m_parent,R.color.fortygray));
        m_rightDrop.setTextSize(TypedValue.COMPLEX_UNIT_PX, labelSize);
        FrameLayout.LayoutParams Params4 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Params4.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        Params4.rightMargin = 30;
        m_layout.addView(m_rightDrop, Params4);
        //line
        m_line = new View(m_parent);
        m_line.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.nintygray));
        FrameLayout.LayoutParams Params5 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        Params5.gravity = Gravity.BOTTOM;
        Params5.leftMargin = 30;
        Params5.rightMargin = 30;
        m_layout.addView(m_line, Params5);
        //Listening
        m_layout.setOnTouchListener(new View.OnTouchListener() {
            boolean click=false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.nintygray));
                        click=true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if(click && event.getX()>0 && event.getX()<v.getWidth() &&
                                event.getY()>0 && event.getY()<v.getHeight()) {
                            if(m_parentClass.equals("InfoNews"))
                                ((InfoNews) m_parent).Turn(m_label);
                        }
                        v.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.white));
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if(click) {
                            click = false;
                            v.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.white));
                        }
                        break;
                }
                return true;
            }
        });
    }

    UTextItem(final AppCompatActivity parent, String parentClass, String label, String tips, int labelSize, int hintsSize, boolean isTouch){//文字
        m_parent=parent;
        m_parentClass=parentClass;
        m_label=label;
        //layout
        m_layout = new FrameLayout(m_parent);
        m_layout.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.white));
        //Label
        m_text=new TextView(m_parent);
        m_text.setIncludeFontPadding(false);
        m_text.setText(label);
        m_text.setTextColor(ContextCompat.getColor(m_parent,R.color.fortygray));
        m_text.setTextSize(TypedValue.COMPLEX_UNIT_PX,labelSize);
        FrameLayout.LayoutParams Params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params1.gravity = Gravity.START|Gravity.CENTER_VERTICAL;
        Params1.leftMargin=30;
        m_layout.addView(m_text,Params1);
        //hints
        m_hints=new TextView(m_parent);
        m_hints.setIncludeFontPadding(false);
        m_hints.setText(tips);
        m_hints.setTextColor(ContextCompat.getColor(m_parent,R.color.seventygray));
        m_hints.setTextSize(TypedValue.COMPLEX_UNIT_PX,hintsSize);
        FrameLayout.LayoutParams Params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params2.gravity =Gravity.END|Gravity.CENTER_VERTICAL;
        Params2.rightMargin=70;
        m_layout.addView(m_hints,Params2);

        //rightDrop
        if(isTouch) {
            m_rightDrop = new TextView(m_parent);
            m_rightDrop.setIncludeFontPadding(false);
            m_rightDrop.setText(">");
            m_rightDrop.setTextColor(ContextCompat.getColor(m_parent,R.color.seventygray));
            m_rightDrop.setTextSize(TypedValue.COMPLEX_UNIT_PX, hintsSize);
            FrameLayout.LayoutParams Params3 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            Params3.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
            Params3.rightMargin = 30;
            m_layout.addView(m_rightDrop, Params3);
        }
        //line
        m_line = new View(m_parent);
        m_line.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.nintygray));
        FrameLayout.LayoutParams Params4 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        Params4.gravity = Gravity.BOTTOM;
        Params4.leftMargin = 30;
        Params4.rightMargin = 30;
        m_layout.addView(m_line, Params4);
        //Listening
        if(isTouch) {
            m_layout.setOnTouchListener(new View.OnTouchListener() {
                boolean click=false;
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            v.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.nintygray));
                            click=true;
                            break;
                        case MotionEvent.ACTION_UP:
                            if(click && event.getX()>0 && event.getX()<v.getWidth() &&
                                    event.getY()>0 && event.getY()<v.getHeight()) {
                                switch (m_parentClass) {
                                    case "InfoDetails":
                                        ((InfoDetails) m_parent).Turn(m_label);
                                        break;
                                    case "ShopSetting":
                                        ((ShopSetting) m_parent).Turn(m_label);
                                        break;
                                    case "BuyItem":
                                        ((BuyItem) m_parent).Turn(m_label);
                                        break;
                                }
                            }
                            v.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.white));
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            if(click) {
                                click = false;
                                v.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.white));
                            }
                            break;
                    }
                    return true;
                }
            });
        }
    }

    UTextItem(final AppCompatActivity parent, String parentClass, String label, byte[] pic, int labelSize, int hintsSize){//图
        m_parent=parent;
        m_parentClass=parentClass;
        m_label=label;
        //layout
        m_layout = new FrameLayout(m_parent);
        m_layout.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.white));
        //Label
        m_text=new TextView(m_parent);
        m_text.setIncludeFontPadding(false);
        m_text.setText(label);
        m_text.setTextColor(ContextCompat.getColor(m_parent,R.color.fortygray));
        m_text.setTextSize(TypedValue.COMPLEX_UNIT_PX,labelSize);
        FrameLayout.LayoutParams Params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params1.gravity = Gravity.START|Gravity.CENTER_VERTICAL;
        Params1.leftMargin=30;
        m_layout.addView(m_text,Params1);
        //pic
        ImageView m_pic = new ImageView(m_parent);
        m_pic.setImageDrawable(Widget.ByteToDrawable(pic));
        FrameLayout.LayoutParams Params2 = new FrameLayout.LayoutParams(132,132);
        Params2.gravity =Gravity.END|Gravity.CENTER_VERTICAL;
        Params2.rightMargin=70;
        m_layout.addView(m_pic,Params2);

        //rightDrop
        m_rightDrop = new TextView(m_parent);
        m_rightDrop.setIncludeFontPadding(false);
        m_rightDrop.setText(">");
        m_rightDrop.setTextColor(ContextCompat.getColor(m_parent,R.color.seventygray));
        m_rightDrop.setTextSize(TypedValue.COMPLEX_UNIT_PX, hintsSize);
        FrameLayout.LayoutParams Params3 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Params3.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        Params3.rightMargin = 30;
        m_layout.addView(m_rightDrop, Params3);
        //line
        m_line = new View(m_parent);
        m_line.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.nintygray));
        FrameLayout.LayoutParams Params4 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        Params4.gravity = Gravity.BOTTOM;
        Params4.leftMargin = 30;
        Params4.rightMargin = 30;
        m_layout.addView(m_line, Params4);
        //Listening
        m_layout.setOnTouchListener(new View.OnTouchListener() {
            boolean click=false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.nintygray));
                        click=true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if(click && event.getX()>0 && event.getX()<v.getWidth() &&
                                event.getY()>0 && event.getY()<v.getHeight()) {
                            if(m_parentClass.equals("InfoDetails"))
                                ((InfoDetails) m_parent).Turn(m_label);
                            else if(m_parentClass.equals("ShopSetting"))
                                ((ShopSetting) m_parent).Turn(m_label);
                        }
                        v.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.white));
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if(click) {
                            click = false;
                            v.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.white));
                        }
                        break;
                }
                return true;
            }
        });
    }


    UTextItem(final AppCompatActivity parent, final String parentClass, String label, int size){//只有标题的选择项(可以选中)
        m_parent=parent;
        m_parentClass=parentClass;
        m_label=label;
        //layout
        m_layout = new FrameLayout(m_parent);
        m_layout.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.white));
        //Label
        m_text=new TextView(m_parent);
        m_text.setIncludeFontPadding(false);
        m_text.setText(label);
        m_text.setTextColor(ContextCompat.getColor(m_parent,R.color.fortygray));
        m_text.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
        FrameLayout.LayoutParams Params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params1.gravity = Gravity.START|Gravity.CENTER_VERTICAL;
        Params1.leftMargin=30;
        m_layout.addView(m_text,Params1);
        //line
        m_line = new View(m_parent);
        m_line.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.nintygray));
        FrameLayout.LayoutParams Params4 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        Params4.gravity = Gravity.BOTTOM;
        Params4.leftMargin = 30;
        Params4.rightMargin = 30;
        m_layout.addView(m_line, Params4);
        //Listening
        m_layout.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.nintygray));
                        if(m_parentClass.equals("InfoDetails"))
                            ((InfoDetails)m_parent).Choose(m_text.getText().toString());
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                }
                return true;
            }
        });
    }

    public void addToView(LinearLayout itemLayout, int height, int topMargin){
        //layout
        LinearLayout.LayoutParams Params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height);
        Params1.topMargin=topMargin;
        itemLayout.addView(m_layout,Params1);
    }
}
