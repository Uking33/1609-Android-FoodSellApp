package com.example.administrator.frozengoods;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ShopSelf extends UActivity{
    UTextEdit uEdit1,uEdit2,uEdit3,uEdit4,uEdit5;
    EditText uEdit6;
    String m_edit1,m_edit2,m_edit3,m_edit4,m_edit5,m_edit6;
    byte[] pp1,pp2,pp3;
    Button btn1,btn2,btn3,btn4,btn5;
    ArrayList<GoodItem> m_onList,m_offList;
    int m_state;
    int m_index;
    private  String[] classString = {"全部","家畜","家禽","加工品","海鲜","其他"};
    private  String[][] class2String = {{"全部"},
            {"全部","腿部","胸部","内脏","尾巴","其他"},
            {"全部","腿部","翅部","内脏","胸部","其他"},
            {"全部","肉丸","熟食","果蔬","小吃","其他"},
            {"全部","虾类","鱼类","贝类","海带","其他"},
            {"全部","灌肠","水发","调料","魔芋","其他"}};
    private UDialog m_dialog;
    int m_class1;
    int m_class2;
    UserItem m_user;
    //Create
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState,false);
        m_itemLayout.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(uEdit1!=null) {
                            imm.hideSoftInputFromWindow(uEdit1.m_edit.getWindowToken(), 0);
                            uEdit1.m_edit.clearFocus();
                        }
                        if(uEdit2!=null){
                            imm.hideSoftInputFromWindow(uEdit2.m_edit.getWindowToken(), 0);
                            uEdit2.m_edit.clearFocus();
                        }
                        if(uEdit3!=null){
                            imm.hideSoftInputFromWindow(uEdit3.m_edit.getWindowToken(), 0);
                            uEdit3.m_edit.clearFocus();
                        }
                        if(uEdit4!=null){
                            imm.hideSoftInputFromWindow(uEdit4.m_edit.getWindowToken(), 0);
                            uEdit4.m_edit.clearFocus();
                        }
                        if(uEdit5!=null){
                            imm.hideSoftInputFromWindow(uEdit5.m_edit.getWindowToken(), 0);
                            uEdit5.m_edit.clearFocus();
                        }
                        if(uEdit6!=null){
                            imm.hideSoftInputFromWindow(uEdit6.getWindowToken(), 0);
                            uEdit6.clearFocus();
                        }
                        break;
                }
                return false;
            }
        });
        Intent intent=getIntent();
        if(intent==null)
            return;
        m_user=intent.getParcelableExtra("user");
        m_index=intent.getIntExtra("index",-1);
        m_onList=intent.getParcelableArrayListExtra("onList");
        m_offList=intent.getParcelableArrayListExtra("offList");

        initBtn("新品上架");
        btn1=((Button)findViewById(R.id.btn_shop_self_1));

        switch (m_id){
            case "SHOP_ON_SELF":
                DatabaseHelper.Act.GetGoodItemPic(m_onList,"where user=? and state=? ORDER BY id DESC", new String[]{m_user.m_user, "On"});
                if(m_index==-1){
                    m_state=1;
                    OnSelf();
                }
                else{
                    m_state=3;
                    GoodItem item=m_onList.get(m_index);
                    pp1=item.pic1;
                    pp2=item.pic2;
                    pp3=item.pic3;
                    m_edit1=item.name;
                    m_edit2=Integer.toString(item.num);
                    if(item.price>0)
                        m_edit3=Integer.toString(item.price);
                    else
                        m_edit3="";
                    m_edit4=item.unit;
                    m_edit5=item.shop;
                    m_edit6=item.details;
                    m_class1=0;
                    m_class2=0;
                    for(int i=0;i<classString.length;i++)
                        if(classString[i].equals(item.class1))
                            m_class1 = i;
                    for(int i=0;i<class2String[m_class1].length;i++)
                        if(class2String[m_class1][i].equals(item.class2))
                            m_class2=i;
                    EditOn();
                }
                break;
            case "SHOP_OFF_SELF":
                DatabaseHelper.Act.GetGoodItemPic(m_offList,"where user=? and state=?  ORDER BY id DESC", new String[]{m_user.m_user, "Off"});
                assert btn1 != null;
                btn1.setVisibility(View.VISIBLE);
                if(m_index==-1){
                    m_state=2;
                    OffSelf();
                }
                else{
                    m_state=4;
                    GoodItem item=m_offList.get(m_index);
                    pp1=item.pic1;
                    pp2=item.pic2;
                    pp3=item.pic3;
                    m_edit1=item.name;
                    m_edit2=Integer.toString(item.num);
                    if(item.price>0)
                        m_edit3=Integer.toString(item.price);
                    else
                        m_edit3="";
                    m_edit4=item.unit;
                    m_edit5=item.shop;
                    m_edit6=item.details;
                    m_class1=0;
                    m_class2=0;
                    for(int i=0;i<classString.length;i++)
                        if(classString[i].equals(item.class1))
                            m_class1 = i;
                    for(int i=0;i<class2String[m_class1].length;i++)
                        if(class2String[m_class1][i].equals(item.class2))
                            m_class2=i;
                    EditOff();
                }
                break;
        }
    }
    @Override protected void onDestroy(){
        m_user=null;
        m_onList=null;
        m_offList=null;
        classString=null;
        class2String=null;
        pp1=null;
        pp2=null;
        pp3=null;
        m_edit1=null;
        m_edit2=null;
        m_edit3=null;
        m_edit4=null;
        m_edit5=null;
        m_edit6=null;

        m_dialog=null;
        uEdit1=null;
        uEdit2=null;
        uEdit3=null;
        uEdit4=null;
        uEdit5=null;
        uEdit6=null;
        btn2=null;
        btn3=null;
        btn4=null;

        m_pic_dialog=null;
        m_tempFile=null;
        picTab=null;
        super.onDestroy();
        setContentView(R.layout.view_null);
    }

    //Turn
    @Override public void onBackPressed() {
        Intent intent = new Intent();
        for(int i=0;i<m_onList.size();i++) {
            m_onList.get(i).pic1 = null;
            m_onList.get(i).pic2 = null;
            m_onList.get(i).pic3 = null;
        }
        for(int i=0;i<m_offList.size();i++) {
            m_offList.get(i).pic1 = null;
            m_offList.get(i).pic2 = null;
            m_offList.get(i).pic3 = null;
        }
        intent.putExtra("onList",m_onList);
        intent.putExtra("offList",m_offList);
        setResult(RESULT_OK, intent);
        finish();
    }
    @Override public void Back(){
        switch(m_tab){
            case "OnSelf":
            case "OffSelf":
                onBackPressed();
                break;
            default:
                switch (m_state){
                    case 1:
                        OnSelf();
                        break;
                    case 2:
                        OffSelf();
                        break;
                    case 3:
                    case 4:
                        onBackPressed();
                        break;
                }
                break;
        }
    }
    @Override public void Ok(){
        String str;
        switch (m_tab){
            case "New":
                //item
                GoodItem item=new GoodItem();
                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss_'"+m_user.m_id+"'");
                item.id=dateFormat.format(date);
                item.user=m_user.m_user;
                item.province=m_user.m_shop.m_ad_province;
                item.city=m_user.m_shop.m_ad_city;
                item.district=m_user.m_shop.m_ad_district;
                item.state="On";
                if(pp1!=null) {
                    if (pp2 != null) {
                        if (pp3 != null)
                            item.pic_num = 3;
                        else
                            item.pic_num = 2;
                    }
                    else
                        item.pic_num = 1;
                }
                else
                    item.pic_num = 0;
                item.pic1=pp1;
                item.pic2=pp2;
                item.pic3=pp3;
                item.class1=classString[m_class1];
                item.class2=class2String[m_class1][m_class2];
                item.name=m_edit1;
                item.num=Integer.parseInt(m_edit2);
                if(m_edit3.isEmpty())
                    item.price=-1;
                else
                    item.price=Integer.parseInt(m_edit3);
                item.unit=m_edit4;
                item.shop=m_edit5;
                item.details=m_edit6;
                //db
                if(DatabaseHelper.Act.NewItem(item)) {
                    Message("上架成功");
                    m_onList.add(0,item);
                }
                else
                    Message("更新数据出错，请联系客服");
                Back();
                break;
            case "EditOn":
                //item
                GoodItem item1=m_onList.get(m_index);
                str=item1.id;
                Date date1 = new Date(System.currentTimeMillis());
                SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss_'"+m_user.m_id+"'");
                item1.id=dateFormat1.format(date1);
                item1.state="On";
                if(pp1!=null) {
                    if (pp2 != null) {
                        if (pp3 != null)
                            item1.pic_num = 3;
                        else
                            item1.pic_num = 2;
                    }
                    else
                        item1.pic_num = 1;
                }
                else
                    item1.pic_num = 0;
                item1.pic1=pp1;
                item1.pic2=pp2;
                item1.pic3=pp3;
                item1.class1=classString[m_class1];
                item1.class2=class2String[m_class1][m_class2];
                item1.name=m_edit1;
                item1.num=Integer.parseInt(m_edit2);
                if(m_edit3.isEmpty())
                    item1.price=-1;
                else
                    item1.price=Integer.parseInt(m_edit3);
                item1.unit=m_edit4;
                item1.shop=m_edit5;
                item1.details=m_edit6;
                //db
                if(DatabaseHelper.Act.EditItem(item1,str)) {
                    Message("重新上架成功");
                    m_onList.remove(m_index);
                    m_onList.add(0,item1);
                }
                else
                    Message("更新数据出错，请联系客服");
                Back();
                break;

            case "Down":
                //item
                GoodItem item2=m_onList.get(m_index);
                item2.state="Off";
                //db
                if(DatabaseHelper.Act.DownItem(item2)) {
                    Message("下架成功");
                    m_onList.remove(m_index);
                    m_offList.add(0,item2);
                }
                else
                    Message("更新数据出错，请联系客服");
                Back();
                break;

            case "EditOff":
                //item
                GoodItem item3=m_offList.get(m_index);
                str=item3.id;
                Date date3 = new Date(System.currentTimeMillis());
                SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss_'"+m_user.m_id+"'");
                item3.id=dateFormat3.format(date3);
                item3.state="On";
                if(pp1!=null) {
                    if (pp2 != null) {
                        if (pp3 != null)
                            item3.pic_num = 3;
                        else
                            item3.pic_num = 2;
                    }
                    else
                        item3.pic_num = 1;
                }
                else
                    item3.pic_num = 0;
                item3.pic1=pp1;
                item3.pic2=pp2;
                item3.pic3=pp3;
                item3.class1=classString[m_class1];
                item3.class2=class2String[m_class1][m_class2];
                item3.name=m_edit1;
                item3.num=Integer.parseInt(m_edit2);
                if(m_edit3.isEmpty())
                    item3.price=-1;
                else
                    item3.price=Integer.parseInt(m_edit3);
                item3.unit=m_edit4;
                item3.shop=m_edit5;
                item3.details=m_edit6;
                //db
                DatabaseHelper.Act.UpGoodItem(item3,str);
                if(DatabaseHelper.Act.UpGoodItem(item3,str)) {
                    Message("重新上架成功");
                    m_offList.remove(m_index);
                    m_onList.add(0,item3);
                }
                else
                    Message("更新数据出错，请联系客服");
                Back();
                break;

            default:
                //intent
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    //Layout
    void Clean() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(uEdit1!=null) imm.hideSoftInputFromWindow(uEdit1.m_edit.getWindowToken(), 0);
        if(uEdit2!=null) imm.hideSoftInputFromWindow(uEdit2.m_edit.getWindowToken(), 0);
        if(uEdit3!=null) imm.hideSoftInputFromWindow(uEdit3.m_edit.getWindowToken(), 0);
        if(uEdit4!=null) imm.hideSoftInputFromWindow(uEdit4.m_edit.getWindowToken(), 0);
        if(uEdit5!=null) imm.hideSoftInputFromWindow(uEdit5.m_edit.getWindowToken(), 0);
        if(uEdit6!=null) imm.hideSoftInputFromWindow(uEdit6.getWindowToken(), 0);
        uEdit1=null;
        uEdit2=null;
        uEdit3=null;
        uEdit4=null;
        uEdit5=null;
        uEdit6=null;
        btn2=null;
        btn3=null;
        btn4=null;
        switch(m_tab){
            case "OnSelf":
                btn1.setVisibility(View.VISIBLE);
                break;
            default:
                btn1.setVisibility(View.GONE);
                break;
        }
        if (m_itemLayout.getChildCount() > 0) m_itemLayout.removeAllViews();
    }
    void OnSelf() {
        m_tab = "OnSelf";
        m_title.setText("上架商品");
        Clean();
        for(int i=0;i<m_onList.size();i++)
            initItem(i);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pp1=null;
                pp2=null;
                pp3=null;
                m_edit1="";
                m_edit2="";
                m_edit3="";
                m_edit4="斤";
                m_edit5=m_user.m_shop.m_shop;
                m_edit6="";
                m_class1=0;
                m_class2=0;
                New();
            }
        });
    }
    void OffSelf() {
        m_tab = "OffSelf";
        m_title.setText("下架商品");
        Clean();
        for(int i=0;i<m_offList.size();i++)
            initItem(i);
    }
    void New(){
        m_tab = "New";
        m_title.setText("新品上架");
        Clean();
        initEdit();
        initButton(R.id.btn_shop_self_2, "上架新品", 30,60,100, new int[]{R.color.white,R.color.eghitygray}, new int[]{R.drawable.btn_blue,R.drawable.btn_blue_over});
        btn2=(Button)findViewById(R.id.btn_shop_self_2);
        btn3=(Button)findViewById(R.id.btn_shop_self_3);
        btn4=(Button)findViewById(R.id.btn_shop_self_4);

        uEdit1.m_edit.setText(m_edit1);
        uEdit2.m_edit.setText(m_edit2);
        uEdit3.m_edit.setText(m_edit3);
        uEdit4.m_edit.setText(m_edit4);
        uEdit5.m_edit.setText(m_edit5);
        uEdit6.setText(m_edit6);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                m_edit1=uEdit1.m_edit.getText().toString();
                m_edit2=uEdit2.m_edit.getText().toString();
                m_edit3=uEdit3.m_edit.getText().toString();
                m_edit4=uEdit4.m_edit.getText().toString();
                m_edit5=uEdit5.m_edit.getText().toString();
                m_edit6=uEdit6.getText().toString();
                if (pp1!=null && !m_edit1.isEmpty() && (!m_edit2.isEmpty()&&Integer.parseInt(m_edit2)>0) && (m_edit3.isEmpty() || Integer.parseInt(m_edit3)>0) && !m_edit4.isEmpty()) {
                    btn2.setEnabled(true);
                    btn2.setBackgroundResource(R.drawable.btn_blue);
                }
                else {
                    btn2.setEnabled(false);
                    btn2.setBackgroundResource(R.drawable.btn_blue_disactivity);
                }
            }
        };
        uEdit1.m_edit.addTextChangedListener(textWatcher);
        uEdit2.m_edit.addTextChangedListener(textWatcher);
        uEdit3.m_edit.addTextChangedListener(textWatcher);
        uEdit4.m_edit.addTextChangedListener(textWatcher);
        uEdit5.m_edit.addTextChangedListener(textWatcher);
        uEdit6.addTextChangedListener(textWatcher);

        if (pp1!=null && !m_edit1.isEmpty() && (!m_edit2.isEmpty()&&Integer.parseInt(m_edit2)>0) && (m_edit3.isEmpty() || Integer.parseInt(m_edit3)>0) && !m_edit4.isEmpty()) {
            btn2.setEnabled(true);
            btn2.setBackgroundResource(R.drawable.btn_blue);
        }
        else {
            btn2.setEnabled(false);
            btn2.setBackgroundResource(R.drawable.btn_blue_disactivity);
        }

        final ShopSelf parent=this;

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_tab="New";
                Ok();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_dialog = new UDialog(parent, new UDialog.OnCustomDialogListener() {
                    @Override
                    public void back(String choose) {
                        for(int i=0;i<classString.length;i++)
                            if(classString[i].equals(choose)) {
                                m_class1 = i;
                                btn3.setText(classString[m_class1]);
                                btn4.setText(classString[0]);
                            }
                    }
                }, classString.length, classString);
                m_dialog.show();
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_dialog = new UDialog(parent, new UDialog.OnCustomDialogListener() {
                    @Override
                    public void back(String choose) {
                        for(int i=0;i<class2String.length;i++)
                            if(class2String[m_class1][i].equals(choose)){
                                m_class2=i;
                                btn4.setText(class2String[m_class1][m_class2]);
                            }
                    }
                }, class2String[m_class1].length, class2String[m_class1]);
                m_dialog.show();
            }
        });
    }
    void EditOn(){
        m_tab = "EditOn";
        m_title.setText("商品编辑");
        Clean();
        initEdit();
        initButton(R.id.btn_shop_self_2, "重新上架", 30,60,0, new int[]{R.color.white,R.color.eghitygray}, new int[]{R.drawable.btn_blue,R.drawable.btn_blue_over});
        initButton(R.id.btn_shop_self_5, "下架商品", 25,0,100, new int[]{R.color.pink,R.color.pink}, new int[]{R.drawable.btn_white,R.drawable.btn_white});
        btn2=(Button)findViewById(R.id.btn_shop_self_2);
        btn3=(Button)findViewById(R.id.btn_shop_self_3);
        btn4=(Button)findViewById(R.id.btn_shop_self_4);
        btn5=(Button)findViewById(R.id.btn_shop_self_5);

        uEdit1.m_edit.setText(m_edit1);
        uEdit2.m_edit.setText(m_edit2);
        uEdit3.m_edit.setText(m_edit3);
        uEdit4.m_edit.setText(m_edit4);
        uEdit5.m_edit.setText(m_edit5);
        uEdit6.setText(m_edit6);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                m_edit1=uEdit1.m_edit.getText().toString();
                m_edit2=uEdit2.m_edit.getText().toString();
                m_edit3=uEdit3.m_edit.getText().toString();
                m_edit4=uEdit4.m_edit.getText().toString();
                m_edit5=uEdit5.m_edit.getText().toString();
                m_edit6=uEdit6.getText().toString();
                if (pp1!=null && !m_edit1.isEmpty() && (!m_edit2.isEmpty()&&Integer.parseInt(m_edit2)>0)  && (m_edit3.isEmpty() || Integer.parseInt(m_edit3)>0) && !m_edit4.isEmpty()) {
                    btn2.setEnabled(true);
                    btn2.setBackgroundResource(R.drawable.btn_blue);
                }
                else {
                    btn2.setEnabled(false);
                    btn2.setBackgroundResource(R.drawable.btn_blue_disactivity);
                }
            }
        };
        uEdit1.m_edit.addTextChangedListener(textWatcher);
        uEdit2.m_edit.addTextChangedListener(textWatcher);
        uEdit3.m_edit.addTextChangedListener(textWatcher);
        uEdit4.m_edit.addTextChangedListener(textWatcher);
        uEdit5.m_edit.addTextChangedListener(textWatcher);
        uEdit6.addTextChangedListener(textWatcher);


        if (pp1!=null && !uEdit1.m_edit.getText().toString().isEmpty() && Integer.parseInt(uEdit2.m_edit.getText().toString())>0 && (uEdit3.m_edit.getText().toString().isEmpty() || Integer.parseInt(uEdit3.m_edit.getText().toString())>0) && !uEdit4.m_edit.getText().toString().isEmpty()) {
            btn2.setEnabled(true);
            btn2.setBackgroundResource(R.drawable.btn_blue);
        }
        else {
            btn2.setEnabled(false);
            btn2.setBackgroundResource(R.drawable.btn_blue_disactivity);
        }

        final ShopSelf parent=this;

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_tab="EditOn";
                Ok();
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_tab="Down";
                Ok();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_dialog = new UDialog(parent, new UDialog.OnCustomDialogListener() {
                    @Override
                    public void back(String choose) {
                        for(int i=0;i<classString.length;i++)
                            if(classString[i].equals(choose)) {
                                m_class1 = i;
                                btn3.setText(classString[m_class1]);
                                btn4.setText(classString[0]);
                            }
                    }
                }, classString.length, classString);
                m_dialog.show();
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_dialog = new UDialog(parent, new UDialog.OnCustomDialogListener() {
                    @Override
                    public void back(String choose) {
                        for(int i=0;i<class2String.length;i++)
                            if(class2String[m_class1][i].equals(choose)){
                                m_class2=i;
                                btn4.setText(class2String[m_class1][m_class2]);
                            }
                    }
                }, class2String[m_class1].length, class2String[m_class1]);
                m_dialog.show();
            }
        });
    }
    void EditOff(){
        m_tab = "EditOff";
        m_title.setText("重新上架");
        Clean();
        initEdit();
        initButton(R.id.btn_shop_self_2, "重新上架", 30,60,100, new int[]{R.color.white,R.color.eghitygray}, new int[]{R.drawable.btn_blue,R.drawable.btn_blue_over});
        btn2=(Button)findViewById(R.id.btn_shop_self_2);
        btn3=(Button)findViewById(R.id.btn_shop_self_3);
        btn4=(Button)findViewById(R.id.btn_shop_self_4);

        uEdit1.m_edit.setText(m_edit1);
        uEdit2.m_edit.setText(m_edit2);
        uEdit3.m_edit.setText(m_edit3);
        uEdit4.m_edit.setText(m_edit4);
        uEdit5.m_edit.setText(m_edit5);
        uEdit6.setText(m_edit6);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                m_edit1=uEdit1.m_edit.getText().toString();
                m_edit2=uEdit2.m_edit.getText().toString();
                m_edit3=uEdit3.m_edit.getText().toString();
                m_edit4=uEdit4.m_edit.getText().toString();
                m_edit5=uEdit5.m_edit.getText().toString();
                m_edit6=uEdit6.getText().toString();
                if (pp1!=null && !m_edit1.isEmpty() && (!m_edit2.isEmpty()&&Integer.parseInt(m_edit2)>0)  && (m_edit3.isEmpty() || Integer.parseInt(m_edit3)>0) && !m_edit4.isEmpty()) {
                    btn2.setEnabled(true);
                    btn2.setBackgroundResource(R.drawable.btn_blue);
                }
                else {
                    btn2.setEnabled(false);
                    btn2.setBackgroundResource(R.drawable.btn_blue_disactivity);
                }
            }
        };
        uEdit1.m_edit.addTextChangedListener(textWatcher);
        uEdit2.m_edit.addTextChangedListener(textWatcher);
        uEdit3.m_edit.addTextChangedListener(textWatcher);
        uEdit4.m_edit.addTextChangedListener(textWatcher);
        uEdit5.m_edit.addTextChangedListener(textWatcher);
        uEdit6.addTextChangedListener(textWatcher);


        if (pp1!=null && !uEdit1.m_edit.getText().toString().isEmpty() && Integer.parseInt(uEdit2.m_edit.getText().toString())>0 && (uEdit3.m_edit.getText().toString().isEmpty() || Integer.parseInt(uEdit3.m_edit.getText().toString())>0) && !uEdit4.m_edit.getText().toString().isEmpty()) {
            btn2.setEnabled(true);
            btn2.setBackgroundResource(R.drawable.btn_blue);
        }
        else {
            btn2.setEnabled(false);
            btn2.setBackgroundResource(R.drawable.btn_blue_disactivity);
        }

        final ShopSelf parent=this;

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_tab="EditOff";
                Ok();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_dialog = new UDialog(parent, new UDialog.OnCustomDialogListener() {
                    @Override
                    public void back(String choose) {
                        for(int i=0;i<classString.length;i++)
                            if(classString[i].equals(choose)) {
                                m_class1 = i;
                                btn3.setText(classString[m_class1]);
                                btn4.setText(classString[0]);
                            }
                    }
                }, classString.length, classString);
                m_dialog.show();
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_dialog = new UDialog(parent, new UDialog.OnCustomDialogListener() {
                    @Override
                    public void back(String choose) {
                        for(int i=0;i<class2String.length;i++)
                            if(class2String[m_class1][i].equals(choose)){
                                m_class2=i;
                                btn4.setText(class2String[m_class1][m_class2]);
                            }
                    }
                }, class2String[m_class1].length, class2String[m_class1]);
                m_dialog.show();
            }
        });
    }

    //Item
    public void initItem (final  int index){
        final GoodItem item;
        if(m_tab.equals("OnSelf"))
            item=m_onList.get(index);
        else
            item=m_offList.get(index);
        FrameLayout layout = new FrameLayout(this); //总框架
        layout.setOnTouchListener(new View.OnTouchListener() {
            boolean click=false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundColor(Color.parseColor("#E6E6E6"));
                        click=true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if(click && event.getX()>0 && event.getX()<v.getWidth() &&
                                event.getY()>0 && event.getY()<v.getHeight()) {
                            m_index=index;
                            pp1=item.pic1;
                            pp2=item.pic2;
                            pp3=item.pic3;
                            m_edit1=item.name;
                            m_edit2=Integer.toString(item.num);
                            if(item.price>0)
                                m_edit3=Integer.toString(item.price);
                            else
                                m_edit3="";
                            m_edit4=item.unit;
                            m_edit5=item.shop;
                            m_edit6=item.details;
                            m_class1=0;
                            m_class2=0;
                            for(int i=0;i<classString.length;i++)
                                if(classString[i].equals(item.class1))
                                    m_class1 = i;
                            for(int i=0;i<class2String[m_class1].length;i++)
                                if(class2String[m_class1][i].equals(item.class2))
                                    m_class2=i;
                            if(m_tab.equals("OnSelf"))
                                EditOn();
                            else
                                EditOff();
                        }
                        v.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if(click) {
                            click = false;
                            v.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        }
                        break;
                }
                return true;
            }
        });

        LinearLayout layoutPic=new LinearLayout(this);//图片
        LinearLayout layoutText=new LinearLayout(this);//文字
        layoutPic.setOrientation(LinearLayout.HORIZONTAL);
        layoutPic.setGravity(Gravity.CENTER);
        layoutText.setOrientation(LinearLayout.VERTICAL);
        //图片1
        ImageView titleImage=new ImageView(this);
        titleImage.setImageDrawable(Widget.ByteToDrawable(item.pic1));
        titleImage.setScaleType(ImageView.ScaleType.FIT_XY);
        layoutPic.addView(titleImage,158,158);
        //名称
        TextView titleText=new TextView(this);
        titleText.setIncludeFontPadding(false);
        titleText.setText(item.name);
        titleText.setTextColor(ContextCompat.getColor(this,R.color.tengray));
        titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX,27);
        LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutText.addView(titleText,p1);
        //price
        TextView picListItemPrice = new TextView(this);
        picListItemPrice.setIncludeFontPadding(false);

        if(item.price>0)
            picListItemPrice.setText("价格：￥" + item.price + "/"+item.unit);
        else
            picListItemPrice.setText("面谈");
        picListItemPrice.setTextColor(ContextCompat.getColor(this,R.color.thirtygray));
        picListItemPrice.setTextSize(TypedValue.COMPLEX_UNIT_PX,25);
        LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        p2.topMargin=15;
        layoutText.addView(picListItemPrice, p2);
        //num
        TextView numText = new TextView(this);
        numText.setIncludeFontPadding(false);
        numText.setText("数量：" + item.num +item.unit);
        numText.setTextColor(ContextCompat.getColor(this,R.color.thirtygray));
        numText.setTextSize(TypedValue.COMPLEX_UNIT_PX,25);
        LinearLayout.LayoutParams p3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        p3.topMargin=14;
        layoutText.addView(numText, p3);
        //地址
        TextView adText=new TextView(this);
        adText.setIncludeFontPadding(false);
        adText.setText("地址："+item.city+item.district);
        adText.setTextColor(ContextCompat.getColor(this,R.color.thirtygray));
        adText.setTextSize(TypedValue.COMPLEX_UNIT_PX,25);
        LinearLayout.LayoutParams p4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        p4.topMargin=14;
        layoutText.addView(adText,p4);
        //分割线
        View line=new View(this);
        line.setBackgroundColor(ContextCompat.getColor(this,R.color.nintygray));
        FrameLayout.LayoutParams p5 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1);
        p5.gravity =Gravity.BOTTOM;
        p5.leftMargin=30;
        p5.rightMargin=30;
        //Layout
        FrameLayout.LayoutParams p6 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        p6.topMargin=30;
        p6.leftMargin=30;
        layout.addView(layoutPic,p6);
        FrameLayout.LayoutParams p7 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,158);
        p7.topMargin=30;
        p7.leftMargin=233;
        layout.addView(layoutText,p7);
        layout.addView(line,p5);

        LinearLayout.LayoutParams p8 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,218);
        m_itemLayout.addView(layout,p8);
    }
    public void initBtn(String text){
        UButton uBtn=new UButton(this,R.id.btn_shop_self_1,text,30,298,72,new int[]{R.color.white,R.color.eghitygray}, new int[]{R.drawable.btn_blue,R.drawable.btn_blue_over});

        RelativeLayout.LayoutParams Params1 = new RelativeLayout.LayoutParams(298,72);
        Params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        Params1.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        Params1.bottomMargin=100;
        RelativeLayout rl=(RelativeLayout)this.findViewById(R.id.all_context);
        if(rl!=null) rl.addView(uBtn.m_btn,Params1);
    }
    public void initEdit(){
        //展示图片
        initPicList("展示图片：",30);
        //商品名称
        uEdit1=new UTextEdit(this,R.id.edit_shop_self_1,"商品名称：", "填写商品名称",InputType.TYPE_CLASS_TEXT,30,470,152);
        uEdit1.addToView(m_itemLayout,100,30);
        uEdit1.m_edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        //商品分类
        initClass();
        //商品数量
        uEdit2=new UTextEdit(this,R.id.edit_shop_self_2,"商品数量", "填写商品数量",InputType.TYPE_CLASS_NUMBER,30,470,152);
        uEdit2.addToView(m_itemLayout,100,30);
        uEdit2.m_edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        //商品价格
        uEdit3=new UTextEdit(this,R.id.edit_shop_self_3,"价格(元)：", "面谈",InputType.TYPE_CLASS_NUMBER,30,470,152);
        uEdit3.addToView(m_itemLayout,100,30);
        uEdit3.m_edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        //价格单位
        uEdit4=new UTextEdit(this,R.id.edit_shop_self_4,"价格单位：", "斤",InputType.TYPE_CLASS_TEXT,30,470,152);
        uEdit4.addToView(m_itemLayout,100,30);
        uEdit4.m_edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
        //店铺名称
        uEdit5=new UTextEdit(this,R.id.edit_shop_self_5,"店铺名称：", "填写店铺名称",InputType.TYPE_CLASS_TEXT,30,470,152,false);
        uEdit5.addToView(m_itemLayout,100,30);
        uEdit5.m_edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        //商品介绍
        uEdit6=initTextEditMul(R.id.edit_shop_self_6,"商品介绍","留下你的商品介绍吧",30);
        uEdit6.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});

    }
    public void initButton(int id, String text, int size, int topMargin, int bottomMargin, int[] textColor, int[] backgroundColor){
        UButton uBtn=new UButton(this,id,text,size,298, 72,textColor,backgroundColor);
        LinearLayout.LayoutParams Params1 = new LinearLayout.LayoutParams(298,72);
        Params1.gravity = Gravity.CENTER_HORIZONTAL;
        Params1.topMargin=topMargin;
        Params1.bottomMargin=bottomMargin;
        m_itemLayout.addView(uBtn.m_btn,Params1);
    }
    public void initPicList(String label, int size){
        //layout
        FrameLayout m_layout = new FrameLayout(this);
        //Label
        TextView m_text=new TextView(this);
        m_text.setIncludeFontPadding(false);
        m_text.setText(label);
        m_text.setTextColor(ContextCompat.getColor(this,R.color.fortygray));
        m_text.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
        FrameLayout.LayoutParams Params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params1.topMargin=46;
        m_layout.addView(m_text,Params1);
        //Pics
        LinearLayout picList=new LinearLayout(this);
        picList.setOrientation(LinearLayout.HORIZONTAL);
        FrameLayout.LayoutParams Params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,158);
        Params2.topMargin=112;
        m_layout.addView(picList,Params2);
        iniPic(picList);
        //line
        View m_line =new View(this);
        m_line.setBackgroundColor(ContextCompat.getColor(this,R.color.eghitygray));
        FrameLayout.LayoutParams Params3 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1);
        Params3.gravity=Gravity.BOTTOM;
        m_layout.addView(m_line,Params3);
        //layout
        LinearLayout.LayoutParams Params4= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,300);
        Params4.leftMargin=30;
        Params4.rightMargin=30;
        m_itemLayout.addView(m_layout,Params4);
    }
    public void iniPic(LinearLayout picList) {
        final ShopSelf parent=this;
        //pic1
        ImageView pic1=new ImageView(this);
        if(pp1==null)
            pic1.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.icon_pic));
        else {
            pic1.setImageDrawable(Widget.ByteToDrawable(pp1));
        }
        pic1.setScaleType(ImageView.ScaleType.FIT_XY);
        LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(158,158);
        picList.addView(pic1,p1);
        pic1.setOnTouchListener(new View.OnTouchListener() {
            boolean click = false;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (pp1 == null)
                        ((ImageView) v).setImageDrawable(ContextCompat.getDrawable(parent, R.drawable.icon_pic_press));
                    click = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (pp1 == null) {
                        if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                                event.getY() > 0 && event.getY() < v.getHeight()) {
                            switch (m_tab){
                                case "New":
                                    PicList("NewPic1");
                                    break;
                                case "EditOn":
                                    PicList("EditOnPic1");
                                    break;
                                case "EditOff":
                                    PicList("EditOffPic1");
                                    break;
                            }
                        }
                        ((ImageView) v).setImageDrawable(ContextCompat.getDrawable(parent, R.drawable.icon_pic));
                    } else if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                            event.getY() > 0 && event.getY() < v.getHeight()) {
                        pp1 = pp2;
                        pp2 = pp3;
                        pp3 = null;
                        switch (m_tab){
                            case "New":
                                New();
                                break;
                            case "EditOn":
                                EditOn();
                                break;
                            case "EditOff":
                                EditOff();
                                break;
                        }
                    }
                    click = false;
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    if (pp1 == null)
                        ((ImageView) v).setImageDrawable(ContextCompat.getDrawable(parent, R.drawable.icon_pic));
                    click = false;
                }
                return true;
            }
        });
        if(pp1==null) return;
        //pic2
        ImageView pic2=new ImageView(this);
        if(pp2==null)
            pic2.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.icon_pic));
        else{
            pic2.setImageDrawable(Widget.ByteToDrawable(pp2));
        }
        pic2.setScaleType(ImageView.ScaleType.FIT_XY);
        LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(158,158);
        p2.leftMargin=35;
        picList.addView(pic2,p2);


        pic2.setOnTouchListener(new View.OnTouchListener() {
            boolean click = false;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    if(pp2==null)
                        ((ImageView)v).setImageDrawable(ContextCompat.getDrawable(parent,R.drawable.icon_pic_press));
                    click = true;
                }else if(event.getAction()==MotionEvent.ACTION_UP) {
                    if (pp2 == null) {
                        if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                                event.getY() > 0 && event.getY() < v.getHeight()) {
                            switch (m_tab) {
                                case "New":
                                    PicList("NewPic2");
                                    break;
                                case "EditOn":
                                    PicList("EditOnPic2");
                                    break;
                                case "EditOff":
                                    PicList("EditOffPic2");
                                    break;
                            }
                            ((ImageView) v).setImageDrawable(ContextCompat.getDrawable(parent, R.drawable.icon_pic));
                        }
                    }else if (click && event.getX() > 0 && event.getX() < v.getWidth() && event.getY() > 0 && event.getY() < v.getHeight()) {
                        pp2 = pp3;
                        pp3 = null;
                        switch (m_tab){
                            case "New":
                                New();
                                break;
                            case "EditOn":
                                EditOn();
                                break;
                            case "EditOff":
                                EditOff();
                                break;
                        }
                    }
                    click = false;
                }
                else if(event.getAction()==MotionEvent.ACTION_CANCEL) {
                    if(pp2==null)
                        ((ImageView) v).setImageDrawable(ContextCompat.getDrawable(parent, R.drawable.icon_pic));
                    click = false;
                }
                return true;
            }
        });

        if(pp2==null) return;
        //pic3
        ImageView pic3=new ImageView(this);
        if(pp3==null)
            pic3.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.icon_pic));
        else {
            pic3.setImageDrawable(Widget.ByteToDrawable(pp3));
        }
        pic3.setScaleType(ImageView.ScaleType.FIT_XY);
        LinearLayout.LayoutParams p3 = new LinearLayout.LayoutParams(158,158);
        p3.leftMargin=35;
        picList.addView(pic3,p3);
        pic3.setOnTouchListener(new View.OnTouchListener() {
            boolean click = false;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    if(pp3==null)
                        ((ImageView)v).setImageDrawable(ContextCompat.getDrawable(parent,R.drawable.icon_pic_press));
                    click = true;
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    if(pp3==null) {
                        if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                                event.getY() > 0 && event.getY() < v.getHeight()) {
                            switch (m_tab){
                                case "New":
                                    PicList("NewPic3");
                                    break;
                                case "EditOn":
                                    PicList("EditOnPic3");
                                    break;
                                case "EditOff":
                                    PicList("EditOffPic3");
                                    break;
                            }
                        }
                        ((ImageView) v).setImageDrawable(ContextCompat.getDrawable(parent, R.drawable.icon_pic));
                    }
                    else if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                            event.getY() > 0 && event.getY() < v.getHeight()) {
                        pp3=null;
                        switch (m_tab){
                            case "New":
                                New();
                                break;
                            case "EditOn":
                                EditOn();
                                break;
                            case "EditOff":
                                EditOff();
                                break;
                        }
                    }
                    click = false;
                }
                else if(event.getAction()==MotionEvent.ACTION_CANCEL) {
                    if(pp3==null)
                        ((ImageView) v).setImageDrawable(ContextCompat.getDrawable(parent, R.drawable.icon_pic));
                    click = false;
                }
                return true;
            }
        });
    }
    public EditText initTextEditMul(int id, String label, String tips, int size){
        //layout
        LinearLayout m_layout = new LinearLayout(this);
        m_layout.setOrientation(LinearLayout.VERTICAL);
        //Label
        TextView m_text=new TextView(this);
        m_text.setIncludeFontPadding(false);
        m_text.setText(label);
        m_text.setTextColor(ContextCompat.getColor(this,R.color.fortygray));
        m_text.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
        LinearLayout.LayoutParams Params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params1.topMargin=37;
        m_layout.addView(m_text,Params1);
        //Edit
        EditText m_edit=new EditText(this);
        m_edit.setId(id);
        m_edit.setSingleLine(true);
        m_edit.setHint(tips);
        m_edit.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        m_edit.setHintTextColor(ContextCompat.getColor(this,R.color.eghitygray));
        m_edit.setTextColor(ContextCompat.getColor(this,R.color.fortygray));
        m_edit.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
        m_edit.setHeight(200);
        m_edit.setGravity(Gravity.TOP|Gravity.START);
        m_edit.setBackgroundResource(R.drawable.bg_edit);
        m_edit.setPadding(15,15,15,15);
        LinearLayout.LayoutParams Params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,200);
        Params2.topMargin=31;
        m_layout.addView(m_edit,Params2);
        //layout
        LinearLayout.LayoutParams Params3= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params3.leftMargin=30;
        Params3.rightMargin=30;
        m_itemLayout.addView(m_layout,Params3);
        return m_edit;
    }
    public void initClass(){
        //layout
        FrameLayout m_layout = new FrameLayout(this);
        //Label
        TextView m_text=new TextView(this);
        m_text.setIncludeFontPadding(false);
        m_text.setText("商品分类：");
        m_text.setTextColor(ContextCompat.getColor(this,R.color.fortygray));
        m_text.setTextSize(TypedValue.COMPLEX_UNIT_PX,30);
        FrameLayout.LayoutParams Params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params1.gravity = Gravity.START|Gravity.CENTER_VERTICAL;
        m_layout.addView(m_text,Params1);
        //Btn1
        UButton uBtn1=new UButton(this,R.id.btn_shop_self_3,classString[m_class1],25,107, 48,new int[]{R.color.white,R.color.eghitygray}, new int[]{R.drawable.btn_blue,R.drawable.btn_blue_over});
        FrameLayout.LayoutParams Params2 = new FrameLayout.LayoutParams(107,48);
        Params2.gravity =Gravity.START|Gravity.CENTER_VERTICAL;
        Params2.leftMargin=152;
        m_layout.addView(uBtn1.m_btn,Params2);
        //View
        TextView text=new TextView(this);
        text.setIncludeFontPadding(false);
        text.setText("-");
        text.setTextColor(ContextCompat.getColor(this,R.color.fortygray));
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX,25);
        text.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams Params3 = new FrameLayout.LayoutParams(43,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params3.gravity = Gravity.START|Gravity.CENTER_VERTICAL;
        Params3.leftMargin=259;
        m_layout.addView(text,Params3);
        //Btn2
        UButton uBtn2=new UButton(this,R.id.btn_shop_self_4,class2String[m_class1][m_class2],25,107, 48,new int[]{R.color.white,R.color.eghitygray}, new int[]{R.drawable.btn_blue,R.drawable.btn_blue_over});
        FrameLayout.LayoutParams Params4 = new FrameLayout.LayoutParams(107,48);
        Params4.gravity =Gravity.START|Gravity.CENTER_VERTICAL;
        Params4.leftMargin=302;
        m_layout.addView(uBtn2.m_btn,Params4);
        //line
        View m_line =new View(this);
        m_line.setBackgroundColor(ContextCompat.getColor(this,R.color.eghitygray));
        FrameLayout.LayoutParams Params5 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1);
        Params5.gravity =Gravity.BOTTOM;
        m_layout.addView(m_line,Params5);
        //add
        LinearLayout.LayoutParams Params6 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,100);
        Params6.rightMargin=30;
        Params6.leftMargin=30;
        m_itemLayout.addView(m_layout,Params6);
    }
    UDialog m_pic_dialog;
    File m_tempFile;
    String picTab;
    private static final int PHOTO_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int PHOTO_CLIP = 3;
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_hhMMss");
        return dateFormat.format(date) + ".jpg";
    }
    void PicList(String tab) {
        picTab = tab;
        m_pic_dialog = new UDialog(this, new UDialog.OnCustomDialogListener() {
            @Override
            public void back(String choose) {
                switch (choose) {
                    case "拍照":
                        getPicFromCamera();
                        break;
                    case "选择本地":
                        getPicFromPhoto();
                        break;
                }
            }
        }, 2, new String[]{"拍照", "选择本地"});
        m_pic_dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                switch (picTab) {
                    case "NewPic1":
                    case "NewPic2":
                    case "NewPic3":
                        New();
                        break;
                    case "EditOnPic1":
                    case "EditOnPic2":
                    case "EditOnPic3":
                        EditOn();
                        break;
                    case "EditOffPic1":
                    case "EditOffPic2":
                    case "EditOffPic3":
                        EditOff();
                        break;
                }
            }
        });
        m_pic_dialog.show();
    }
    public void getPicFromPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        startActivityForResult(intent, PHOTO_REQUEST);
    }
    public void getPicFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("autoFocus", true);// 自动对焦
        intent.putExtra("fullScreen", false);// 全屏
        intent.putExtra("showActionIcons", false);
        // 指定调用相机拍照后照片的储存路径
        m_tempFile = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(m_tempFile));
        startActivityForResult(intent, CAMERA_REQUEST);
    }
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_REQUEST:
                    startPhotoZoom(data.getData(), 150);
                    break;
                case CAMERA_REQUEST:
                    startPhotoZoom(Uri.fromFile(m_tempFile), 150);
                    break;
                case PHOTO_CLIP:
                    saveImage(data);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);


        startActivityForResult(intent, PHOTO_CLIP);
    }
    private void saveImage(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            final Bitmap bmp = bundle.getParcelable("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            assert bmp != null;
            bmp.compress(Bitmap.CompressFormat.PNG, 50, stream);

            switch (picTab){
                case "NewPic1":
                case "EditOnPic1":
                case "EditOffPic1":
                    pp1=stream.toByteArray();
                    break;
                case "EditOnPic2":
                case "EditOffPic2":
                case "NewPic2":
                    pp2=stream.toByteArray();
                    break;
                case "NewPic3":
                case "EditOffPic3":
                case "EditOnPic3":
                    pp3=stream.toByteArray();
                    break;
            }
            switch (picTab){
                case "NewPic1":
                case "NewPic2":
                case "NewPic3":
                    New();
                    break;
                case "EditOnPic1":
                case "EditOnPic2":
                case "EditOnPic3":
                    EditOn();
                    break;
                case "EditOffPic1":
                case "EditOffPic2":
                case "EditOffPic3":
                    EditOff();
                    break;
            }
        }
    }
}
