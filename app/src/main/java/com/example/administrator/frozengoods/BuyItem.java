package com.example.administrator.frozengoods;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class BuyItem extends UActivity{
    UTextEdit uEdit1,uEdit2,uEdit3,uEdit4,uEdit5,uEdit6,uEdit7;
    String m_edit1,m_edit2,m_edit3,m_edit4,m_edit5,m_edit6,m_edit7;
    Button btn1,btn2,btn3,btn4;
    String m_ad_province;
    String m_ad_city;
    String m_ad_district;
    int m_state;
    GetItem m_item;
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
    //Init
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState,true);
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
                            imm.hideSoftInputFromWindow(uEdit6.m_edit.getWindowToken(), 0);
                            uEdit6.m_edit.clearFocus();
                        }
                        if(uEdit7!=null){
                            imm.hideSoftInputFromWindow(uEdit7.m_edit.getWindowToken(), 0);
                            uEdit7.m_edit.clearFocus();
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
        switch (m_id){
            case "New":
                m_state=1;
                m_item=new GetItem();
                m_class1=0;
                m_class2=0;
                m_edit1="";
                m_edit2="";
                m_edit3="";
                m_edit4="";
                m_edit5="";
                m_edit6=m_user.m_phone;
                if(m_user.m_shop!=null)
                    m_edit7=m_user.m_shop.m_shop;
                else
                    m_edit7="";
                m_ad_province=m_user.m_province;
                m_ad_city=m_user.m_city;
                m_ad_district=m_user.m_district;
                New();
                break;
            case "Edit":
                m_state=2;
                m_item=intent.getParcelableExtra("item");
                m_class1=0;
                m_class2=0;
                for(int i=0;i<classString.length;i++)
                    if(classString[i].equals(m_item.class1))
                        m_class1 = i;
                for(int i=0;i<class2String[m_class1].length;i++)
                    if(class2String[m_class1][i].equals(m_item.class2))
                        m_class2=i;
                m_edit1=m_item.name;
                m_edit2=String.valueOf(m_item.num);
                m_edit3=m_item.unit;
                if(m_item.price>0)
                    m_edit4=String.valueOf(m_item.price);
                else
                    m_edit4="";
                m_edit5=m_item.getterName;
                m_edit6=m_item.getterPhone;
                m_edit7=m_item.shop;
                m_ad_province=m_item.province;
                m_ad_city=m_item.city;
                m_ad_district=m_item.district;
                Edit();
                break;
        }
    }
    @Override protected void onDestroy(){
        uEdit1=null;
        uEdit2=null;
        uEdit3=null;
        uEdit4=null;
        uEdit5=null;
        uEdit6=null;
        uEdit7=null;
        btn1=null;
        btn2=null;
        btn3=null;
        btn4=null;
        m_item=null;
        m_dialog=null;
        m_user=null;
        super.onDestroy();
        setContentView(R.layout.view_null);
    }

    //Turn
    @Override public void Turn(String label){
        switch(label){
            case "所在地区":
                Ad();
                break;
        }
    }
    @Override public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
    @Override public void Back(){
        switch(m_tab){
            case "New":
            case "Edit":
            case "Down":
                onBackPressed();
                break;
            case "Ad":
                if(m_state==1)
                    New();
                else if(m_state==2)
                    Edit();
                break;
            default:
                break;
        }
    }
    @Override public void Ok(){
        switch (m_tab) {
            case "New": {
                //item
                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss_'" + m_user.m_id + "'");
                m_item.id = dateFormat.format(date);
                m_item.user = m_user.m_user;

                m_item.class1 = classString[m_class1];
                m_item.class2 = class2String[m_class1][m_class2];

                m_item.name = m_edit1;
                m_item.num = Integer.parseInt(m_edit2);
                m_item.unit = m_edit3;
                if (m_edit4.isEmpty())
                    m_item.price = -1;
                else
                    m_item.price = Integer.parseInt(m_edit4);
                m_item.getterName = m_edit5;
                m_item.getterPhone = m_edit6;
                m_item.shop = m_edit7;
                m_item.province = m_ad_province;
                m_item.city = m_ad_city;
                m_item.district = m_ad_district;
                //db
                if (DatabaseHelper.Act.NewItem(m_item))
                    Message("发布成功");
                else
                    Message("发布失败");
                Intent intent = new Intent();
                intent.putExtra("item",m_item);
                if(m_tab.equals("Down"))
                    intent.putExtra("isDown",true);
                else
                    intent.putExtra("isDown",false);
                setResult(RESULT_OK, intent);
                finish();
                break;
            }
            case "Edit": {
                //item
                String id=m_item.id;
                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss_'" + m_user.m_id + "'");
                m_item.id = dateFormat.format(date);
                m_item.class1 = classString[m_class1];
                m_item.class2 = class2String[m_class1][m_class2];
                m_item.name = m_edit1;
                m_item.num = Integer.parseInt(m_edit2);
                m_item.unit = m_edit3;
                if (m_edit4.isEmpty())
                    m_item.price = -1;
                else
                    m_item.price = Integer.parseInt(m_edit4);
                m_item.getterName = m_edit5;
                m_item.getterPhone = m_edit6;
                m_item.shop = m_edit7;
                m_item.province = m_ad_province;
                m_item.city = m_ad_city;
                m_item.district = m_ad_district;
                //db
                if (DatabaseHelper.Act.EditItem(m_item,id))
                    Message("重新发布成功");
                else
                    Message("重新发布失败");
                Intent intent = new Intent();
                intent.putExtra("item",m_item);
                if(m_tab.equals("Down"))
                    intent.putExtra("isDown",true);
                else
                    intent.putExtra("isDown",false);
                setResult(RESULT_OK, intent);
                finish();
                break;
            }
            case "Down":{
                //db
                if (DatabaseHelper.Act.DownItem(m_item))
                    Message("求购已取消");
                else
                    Message("求购取消失败");
                Intent intent = new Intent();
                intent.putExtra("item",m_item);
                if(m_tab.equals("Down"))
                    intent.putExtra("isDown",true);
                else
                    intent.putExtra("isDown",false);
                setResult(RESULT_OK, intent);
                finish();
                break;
            }
            case "Ad":
                if (!m_ad_adItem1.m_value.isEmpty() && !m_ad_adItem2.m_value.isEmpty() && !m_ad_adItem3.m_value.isEmpty()){
                    m_ad_province=m_ad_adItem1.m_value;
                    m_ad_city=m_ad_adItem2.m_value;
                    m_ad_district=m_ad_adItem3.m_value;
                    Back();
                }
                else
                    Message("请把地址填写完整");
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
        if(uEdit6!=null) imm.hideSoftInputFromWindow(uEdit6.m_edit.getWindowToken(), 0);
        if(uEdit7!=null) imm.hideSoftInputFromWindow(uEdit7.m_edit.getWindowToken(), 0);
        uEdit1=null;
        uEdit2=null;
        uEdit3=null;
        uEdit4=null;
        uEdit5=null;
        uEdit6=null;
        uEdit7=null;
        btn1=null;
        btn2=null;
        if (m_tab.equals("Ad")) {
            m_ok.setVisibility(View.VISIBLE);
            m_ok_over.setVisibility(View.VISIBLE);
        } else {
            m_ok.setVisibility(View.GONE);
            m_ok_over.setVisibility(View.GONE);
        }
        if (m_itemLayout.getChildCount() > 0) m_itemLayout.removeAllViews();
    }
    void New(){
        m_tab = "New";
        m_title.setText("发布求购");
        Clean();
        initEdit();
        initButton(R.id.btn_buy_item_1, "发布", 30,60,100, new int[]{R.color.white,R.color.eghitygray}, new int[]{R.drawable.btn_blue,R.drawable.btn_blue_over});
        btn1=(Button)findViewById(R.id.btn_buy_item_1);
        btn2=(Button)findViewById(R.id.btn_buy_item_2);
        btn3=(Button)findViewById(R.id.btn_buy_item_3);

        uEdit1.m_edit.setText(m_edit1);
        uEdit2.m_edit.setText(m_edit2);
        uEdit3.m_edit.setText(m_edit3);
        uEdit4.m_edit.setText(m_edit4);
        uEdit5.m_edit.setText(m_edit5);
        uEdit6.m_edit.setText(m_edit6);
        uEdit7.m_edit.setText(m_edit7);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                m_edit1=uEdit1.m_edit.getText().toString();
                m_edit2=uEdit2.m_edit.getText().toString();
                m_edit3=uEdit3.m_edit.getText().toString();
                m_edit4=uEdit4.m_edit.getText().toString();
                m_edit5=uEdit5.m_edit.getText().toString();
                m_edit6=uEdit6.m_edit.getText().toString();
                m_edit7=uEdit7.m_edit.getText().toString();
                if (!Objects.equals(m_ad_province, "") && !Objects.equals(m_ad_city, "") && !Objects.equals(m_ad_district, "") && !m_edit1.isEmpty() && !m_edit2.isEmpty() && !m_edit3.isEmpty() && (m_edit4.isEmpty()||Integer.parseInt(m_edit4)>0) && !m_edit5.isEmpty() && !m_edit6.isEmpty()) {
                    btn1.setEnabled(true);
                    btn1.setBackgroundResource(R.drawable.btn_blue);
                }
                else {
                    btn1.setEnabled(false);
                    btn1.setBackgroundResource(R.drawable.btn_blue_disactivity);
                }
            }
        };
        uEdit1.m_edit.addTextChangedListener(textWatcher);
        uEdit2.m_edit.addTextChangedListener(textWatcher);
        uEdit3.m_edit.addTextChangedListener(textWatcher);
        uEdit4.m_edit.addTextChangedListener(textWatcher);
        uEdit5.m_edit.addTextChangedListener(textWatcher);
        uEdit6.m_edit.addTextChangedListener(textWatcher);
        uEdit7.m_edit.addTextChangedListener(textWatcher);

        if (!m_ad_province.isEmpty() && !m_ad_city.isEmpty() && !m_ad_district.isEmpty() && !m_edit1.isEmpty() && !m_edit2.isEmpty() && !m_edit3.isEmpty() && (m_edit4.isEmpty()||Integer.parseInt(m_edit4)>0) && !m_edit5.isEmpty() && !m_edit6.isEmpty()) {
            btn1.setEnabled(true);
            btn1.setBackgroundResource(R.drawable.btn_blue);
        }
        else {
            btn1.setEnabled(false);
            btn1.setBackgroundResource(R.drawable.btn_blue_disactivity);
        }

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_tab="New";
                Ok();
            }
        });
        final BuyItem parent=this;
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_dialog = new UDialog(parent, new UDialog.OnCustomDialogListener() {
                    @Override
                    public void back(String choose) {
                        for(int i=0;i<classString.length;i++)
                            if(classString[i].equals(choose)) {
                                m_class1 = i;
                                btn2.setText(classString[m_class1]);
                                btn3.setText(classString[0]);
                            }
                    }
                }, classString.length, classString);
                m_dialog.show();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_dialog = new UDialog(parent, new UDialog.OnCustomDialogListener() {
                    @Override
                    public void back(String choose) {
                        for(int i=0;i<class2String.length;i++)
                            if(class2String[m_class1][i].equals(choose)){
                                m_class2=i;
                                btn3.setText(class2String[m_class1][m_class2]);
                            }
                    }
                }, class2String[m_class1].length, class2String[m_class1]);
                m_dialog.show();
            }
        });
    }
    void Edit(){
        m_tab = "Edit";
        m_title.setText("发布求购");
        Clean();
        initEdit();
        initButton(R.id.btn_buy_item_1, "重新发布", 30,60,0, new int[]{R.color.white,R.color.eghitygray}, new int[]{R.drawable.btn_blue,R.drawable.btn_blue_over});
        initButton(R.id.btn_buy_item_4, "取消发布", 25,0,100, new int[]{R.color.pink,R.color.pink}, new int[]{R.drawable.btn_white,R.drawable.btn_white});
        btn1=(Button)findViewById(R.id.btn_buy_item_1);
        btn2=(Button)findViewById(R.id.btn_buy_item_2);
        btn3=(Button)findViewById(R.id.btn_buy_item_3);
        btn4=(Button)findViewById(R.id.btn_buy_item_4);

        uEdit1.m_edit.setText(m_edit1);
        uEdit2.m_edit.setText(m_edit2);
        uEdit3.m_edit.setText(m_edit3);
        uEdit4.m_edit.setText(m_edit4);
        uEdit5.m_edit.setText(m_edit5);
        uEdit6.m_edit.setText(m_edit6);
        uEdit7.m_edit.setText(m_edit7);

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
                m_edit6=uEdit6.m_edit.getText().toString();
                m_edit7=uEdit7.m_edit.getText().toString();
                if (!m_ad_province.isEmpty() && !m_ad_city.isEmpty() && !m_ad_district.isEmpty() && !m_edit1.isEmpty() && !m_edit2.isEmpty() && !m_edit3.isEmpty() && (m_edit4.isEmpty()||Integer.parseInt(m_edit4)>0) && !m_edit5.isEmpty() && !m_edit6.isEmpty()) {
                    btn1.setEnabled(true);
                    btn1.setBackgroundResource(R.drawable.btn_blue);
                }
                else {
                    btn1.setEnabled(false);
                    btn1.setBackgroundResource(R.drawable.btn_blue_disactivity);
                }
            }
        };
        uEdit1.m_edit.addTextChangedListener(textWatcher);
        uEdit2.m_edit.addTextChangedListener(textWatcher);
        uEdit3.m_edit.addTextChangedListener(textWatcher);
        uEdit4.m_edit.addTextChangedListener(textWatcher);
        uEdit5.m_edit.addTextChangedListener(textWatcher);
        uEdit6.m_edit.addTextChangedListener(textWatcher);
        uEdit7.m_edit.addTextChangedListener(textWatcher);

        if (!m_ad_province.isEmpty() && !m_ad_city.isEmpty() && !m_ad_district.isEmpty() && !m_edit1.isEmpty() && !m_edit2.isEmpty() && !m_edit3.isEmpty() && (m_edit4.isEmpty()||Integer.parseInt(m_edit4)>0) && !m_edit5.isEmpty() && !m_edit6.isEmpty()) {
            btn1.setEnabled(true);
            btn1.setBackgroundResource(R.drawable.btn_blue);
        }
        else {
            btn1.setEnabled(false);
            btn1.setBackgroundResource(R.drawable.btn_blue_disactivity);
        }

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_tab="Edit";
                Ok();
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_tab="Down";
                Ok();
            }
        });
        final BuyItem parent=this;
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_dialog = new UDialog(parent, new UDialog.OnCustomDialogListener() {
                    @Override
                    public void back(String choose) {
                        for(int i=0;i<classString.length;i++)
                            if(classString[i].equals(choose)) {
                                m_class1 = i;
                                btn2.setText(classString[m_class1]);
                                btn3.setText(classString[0]);
                            }
                    }
                }, classString.length, classString);
                m_dialog.show();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_dialog = new UDialog(parent, new UDialog.OnCustomDialogListener() {
                    @Override
                    public void back(String choose) {
                        for(int i=0;i<class2String.length;i++)
                            if(class2String[m_class1][i].equals(choose)){
                                m_class2=i;
                                btn3.setText(class2String[m_class1][m_class2]);
                            }
                    }
                }, class2String[m_class1].length, class2String[m_class1]);
                m_dialog.show();
            }
        });
    }
    //Ad
    ProvinceAreaHelper m_ad_provinceAreaHelper;
    private BuyItem.AdItem m_ad_adItem1, m_ad_adItem2, m_ad_adItem3;
    boolean m_ad_autoing;
    void Ad() {
        m_tab = "Ad";
        m_title.setText("地址");
        Clean();
        //地址
        m_ad_provinceAreaHelper = new ProvinceAreaHelper(this);
        m_ad_provinceAreaHelper.initProvinceData();
        FrameLayout adLayout = new FrameLayout(this);
        String s1,s2,s3;

        s1=m_ad_province;
        s2=m_ad_city;
        s3=m_ad_district;
        String str1[] = m_ad_provinceAreaHelper.getProvinceData();
        String str2[] = {};
        if(!s1.isEmpty())
            str2=m_ad_provinceAreaHelper.updateCities(s1);
        String str3[] = {};
        if(!s2.isEmpty())
            str3=m_ad_provinceAreaHelper.updateAreas(s2);
        m_ad_adItem1 = new BuyItem.AdItem(this, adLayout, "Province", 121,s1, str1);
        m_ad_adItem2 = new BuyItem.AdItem(this, adLayout, "City", 307, s2, str2);
        m_ad_adItem3 = new BuyItem.AdItem(this, adLayout, "District", 501, s3, str3);
        LinearLayout.LayoutParams Params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 135);
        Params1.gravity = Gravity.CENTER_HORIZONTAL;
        m_itemLayout.addView(adLayout, Params1);
        //自动
        UButton uBtn=new UButton(this,R.id.btn_buy_item_3,"自动定位当前位置",25,280, 60,new int[]{R.color.white,R.color.eghitygray}, new int[]{R.drawable.btn_blue,R.drawable.btn_blue_over});
        uBtn.addToView(m_itemLayout,0);
        Button btn1=(Button)findViewById(R.id.btn_buy_item_3);
        assert btn1 != null;
        m_ad_autoing=false;
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(!m_ad_autoing) {
                BuyItem.Location loc=new BuyItem.Location();
                loc.getData();
                m_ad_autoing=true;
            }
            }
        });
    }
    public void returnLocation(LocationInfo info){
        if (info.Error.equals("定位成功")){
            m_ad_adItem1.SetText(info.Province);
            m_ad_adItem2.SetText(info.City);
            m_ad_adItem3.SetText(info.District);
        }
        else {
            if (!info.Error.isEmpty())
                MainActivity.MainAct.Message(info.Error);
        }
        m_ad_autoing=false;
    }
    public class AdItem {
        TextView m_text;
        View m_drop;
        FrameLayout m_adItem;
        FrameLayout m_pLayout;
        BuyItem m_parent;
        String[] m_strList;
        String m_state;
        String m_value;

        void SetText(String str){
            m_value=str;
            switch (m_state){
                case "Province":
                    str=str.equals("") ? "省份" : str;
                    break;
                case "City":
                    str=str.equals("") ? "城市" : str;
                    break;
                case "District":
                    str=str.equals("") ? "地区" : str;
                    break;
            }
            m_text.setText(str.length()>=3?str.substring(0,3):str);
        }
        AdItem(BuyItem parent, FrameLayout adLayout, String state, int leftMargin, String firstText, final String[] strList) {
            m_parent = parent;
            m_strList = strList;
            m_state = state;

            m_pLayout = adLayout;
            m_adItem = new FrameLayout(parent);

            m_text = new TextView(parent);
            m_text.setIncludeFontPadding(false);

            SetText(firstText);

            m_text.setTextColor(ContextCompat.getColor(parent, R.color.thirtygray));
            m_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);
            FrameLayout.LayoutParams Params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            Params1.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
            m_adItem.addView(m_text, Params1);
            //drop
            m_drop = new View(parent);
            m_drop.setBackgroundResource(R.drawable.icon_drop2_down_gray);
            FrameLayout.LayoutParams Params2 = new FrameLayout.LayoutParams(15, 8);
            Params2.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
            m_adItem.addView(m_drop, Params2);
            //layout
            FrameLayout.LayoutParams Params3 = new FrameLayout.LayoutParams(122, 100);
            Params3.gravity = Gravity.CENTER_VERTICAL;
            Params3.leftMargin = leftMargin;
            m_pLayout.addView(m_adItem, Params3);
            //listening
            m_adItem.setOnTouchListener(new View.OnTouchListener() {
                boolean click = false;
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        click = true;
                        m_drop.setBackgroundResource(R.drawable.icon_drop2_down_blue);
                        m_text.setTextColor(ContextCompat.getColor(m_parent, R.color.textblue));
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        m_drop.setBackgroundResource(R.drawable.icon_drop2_down_gray);
                        m_text.setTextColor(ContextCompat.getColor(m_parent, R.color.thirtygray));

                        if (click && m_strList!=null && m_strList.length!=0 && event.getX() > 0 && event.getX() < v.getWidth() &&
                                event.getY() > 0 && event.getY() < v.getHeight()) {
                            UDialog dialog = new UDialog(m_parent, new UDialog.OnCustomDialogListener() {
                                @Override
                                public void back(String choose) {
                                    switch (m_state){
                                        case "Province":
                                            SetText(choose);
                                            m_parent.m_ad_adItem2.SetText("");
                                            m_parent.m_ad_adItem3.SetText("");
                                            m_parent.m_ad_adItem2.m_strList=m_parent.m_ad_provinceAreaHelper.updateCities(m_value);
                                            m_parent.m_ad_adItem3.m_strList=null;
                                            break;
                                        case "City":
                                            SetText(choose);
                                            m_ad_adItem3.SetText("");
                                            m_parent.m_ad_adItem3.m_strList=m_parent.m_ad_provinceAreaHelper.updateAreas(m_value);
                                            break;
                                        case "District":
                                            SetText(choose);
                                            break;
                                    }
                                }
                            }, m_strList.length, m_strList);
                            dialog.show();
                        }
                        click = false;
                    } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                        if (click) {
                            click = false;
                        }
                        m_drop.setBackgroundResource(R.drawable.icon_drop2_down_gray);
                        m_text.setTextColor(ContextCompat.getColor(m_parent, R.color.thirtygray));
                    }
                    return true;
                }
            });
        }
    }
    class Location {
        private LocationClient mLocationClient = null;
        public LocationInfo info;
        private LocationClientOption option;
        Location(){
            BDLocationListener myListener = new BuyItem.Location.MyLocationListener();
            info=new LocationInfo();
            mLocationClient = new LocationClient(BuyItem.this.getApplicationContext());
            mLocationClient.registerLocationListener( myListener );
            initLocation();
        }
        void getData(){
            mLocationClient.start();
            mLocationClient.requestLocation();// 开始请求位置
        }
        private void initLocation(){
            option = new LocationClientOption();
            option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
            option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
            option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            mLocationClient.setLocOption(option);
        }
        private class MyLocationListener implements BDLocationListener {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    info.Province = location.getProvince();
                    info.City = location.getCity();
                    info.District = location.getDistrict();
                    info.Error = "定位成功";
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    info.Province = location.getProvince();
                    info.City = location.getCity();
                    info.District = location.getDistrict();
                    info.Error = "定位成功";
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    info.Error = "服务端网络定位失败,请联系客服";
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    info.Error = "网络不同导致定位失败，请检查网络是否通畅";
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    info.Error = "无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机";
                }
                if(!info.Error.equals("定位成功")){
                    info.Province = "";
                    info.City = "";
                    info.District = "";
                }
                returnLocation(info);
            }
        }
    }
    //Item
    public void initEdit(){
        //商品分类
        initClass();
        //商品名称
        uEdit1=new UTextEdit(this,R.id.edit_buy_item_1,"名称：", "填写商品名称", InputType.TYPE_CLASS_TEXT,30,470,152);
        uEdit1.addToView(m_itemLayout,100,30);
        uEdit1.m_edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        //商品数量
        uEdit2=new UTextEdit(this,R.id.edit_buy_item_2,"数量：", "填写数量",InputType.TYPE_CLASS_NUMBER,30,470,152);
        uEdit2.addToView(m_itemLayout,100,30);
        uEdit2.m_edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        //单位
        uEdit3=new UTextEdit(this,R.id.edit_buy_item_3,"单位：", "填写单位",InputType.TYPE_CLASS_TEXT,30,470,152);
        uEdit3.addToView(m_itemLayout,100,30);
        uEdit3.m_edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
        //价格
        uEdit4=new UTextEdit(this,R.id.edit_buy_item_4,"价格(元)：", "面谈",InputType.TYPE_CLASS_NUMBER,30,470,152);
        uEdit4.addToView(m_itemLayout,100,30);
        uEdit4.m_edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        //联系人
        uEdit5=new UTextEdit(this,R.id.edit_buy_item_5,"联系人：", "填写联系人姓名",InputType.TYPE_CLASS_TEXT,30,470,152);
        uEdit5.addToView(m_itemLayout,100,30);
        uEdit5.m_edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        //联系方式
        uEdit6=new UTextEdit(this,R.id.edit_buy_item_6,"手机号码：", "填写联系手机号码",InputType.TYPE_CLASS_TEXT,30,470,152);
        uEdit6.addToView(m_itemLayout,100,30);
        uEdit6.m_edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        //店铺名称
        uEdit7=new UTextEdit(this,R.id.edit_buy_item_7,"店铺名称：", "无店铺",InputType.TYPE_CLASS_TEXT,30,470,152,false);
        uEdit7.addToView(m_itemLayout,100,30);
        uEdit7.m_edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        //所在地址
        initTextItem("BuyItem", m_itemLayout, "所在地区", m_ad_province+m_ad_city+m_ad_district, 100, 0, true);
    }
    public UTextItem initTextItem(String parentClass, LinearLayout itemLayout, String label, String tips, int height, int topMargin, boolean isTouch){//地区
        UTextItem uItem=new UTextItem(this,parentClass,label,tips,30,25,isTouch);
        uItem.addToView(itemLayout,height,topMargin);
        return uItem;
    }
    public void initButton(int id, String text, int size, int topMargin, int bottomMargin, int[] textColor, int[] backgroundColor){
        UButton uBtn=new UButton(this,id,text,size,298, 72,textColor,backgroundColor);
        LinearLayout.LayoutParams Params1 = new LinearLayout.LayoutParams(298,72);
        Params1.gravity = Gravity.CENTER_HORIZONTAL;
        Params1.topMargin=topMargin;
        Params1.bottomMargin=bottomMargin;
        m_itemLayout.addView(uBtn.m_btn,Params1);
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
        UButton uBtn1=new UButton(this,R.id.btn_buy_item_2,classString[m_class1],25,107, 48,new int[]{R.color.white,R.color.eghitygray}, new int[]{R.drawable.btn_blue,R.drawable.btn_blue_over});
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
        UButton uBtn2=new UButton(this,R.id.btn_buy_item_3,class2String[m_class1][m_class2],25,107, 48,new int[]{R.color.white,R.color.eghitygray}, new int[]{R.drawable.btn_blue,R.drawable.btn_blue_over});
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
}
