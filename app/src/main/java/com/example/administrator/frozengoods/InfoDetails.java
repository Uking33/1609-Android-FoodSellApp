package com.example.administrator.frozengoods;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InfoDetails extends UActivity {
    private UTextEdit m_edit;
    private int m_state;
    private UserItem m_user;

    //Create
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState,true);
        m_itemLayout.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(m_edit!=null) imm.hideSoftInputFromWindow(m_edit.m_edit.getWindowToken(), 0);
                        break;
                }
                return false;
            }
        });

        m_user=getIntent().getParcelableExtra("user");
        if(m_id.equals("INFO_DETAILS")) {
            m_state=1;
            Info();
        }
        else if(m_id.equals("HOME_AD")) {
            m_state=2;
            Ad();
        }
    }
    @Override protected void onDestroy(){
        m_edit=null;
        m_user=null;
        m_head_tempFile=null;
        m_ad_provinceAreaHelper=null;
        m_ad_adItem1=null;
        m_ad_adItem2=null;
        m_ad_adItem3=null;
        m_sex_item1=null;
        m_sex_item2=null;
        m_sex_item3=null;
        m_sex_choose=null;
        super.onDestroy();
        setContentView(R.layout.view_null);
    }

    //Turn
    @Override public void onBackPressed() {
        if(m_state==2){
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
        else{
            Intent intent = new Intent();
            intent.putExtra("user", m_user);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
    @Override public void Back() {
        if (m_tab.equals("Info"))
            onBackPressed();
        else if(m_state==2){
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
        else if (m_tab.equals("Name") || m_tab.equals("Phone") || m_tab.equals("Ad") || m_tab.equals("Sex"))
            Info();
    }
    @Override public void Ok() {
        switch (m_tab) {
            case "Name":
                m_user.m_name = m_edit.m_edit.getText().toString();
                if(DatabaseHelper.Act.UpdateData(m_user.m_user, "name", m_user.m_name))
                    Message("修改成功");
                else
                    Message("修改失败");
                break;
            case "Phone":
                m_user.m_phone = m_edit.m_edit.getText().toString();
                if(DatabaseHelper.Act.UpdateData(m_user.m_user, "phone", m_user.m_phone))
                    Message("修改成功");
                else
                    Message("修改失败");
                break;
            case "Ad":
                if (m_ad_adItem1.m_value.isEmpty() || m_ad_adItem2.m_value.isEmpty() || m_ad_adItem3.m_value.isEmpty()) {
                    MainActivity.MainAct.Message("请填完整地址信息");
                    m_ok_over.getBackground().setAlpha(0);
                    return;
                }
                m_user.m_province = m_ad_adItem1.m_value;
                m_user.m_city = m_ad_adItem2.m_value;
                m_user.m_district = m_ad_adItem3.m_value;
                if(DatabaseHelper.Act.UpdateData(m_user.m_user, new String[]{"province", "city", "district"}, new String[]{m_ad_adItem1.m_value, m_ad_adItem2.m_value, m_ad_adItem3.m_value}))
                    Message("修改成功");
                else
                    Message("修改失败");
                if (m_state == 2) {
                    Intent intent = new Intent();
                    intent.putExtra("user", m_user);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else
                    Back();
                break;
            case "Sex":
                m_user.m_sex = m_sex_choose;
                if(DatabaseHelper.Act.UpdateData(m_user.m_user, "sex", m_user.m_phone))
                    Message("修改成功");
                else
                    Message("修改失败");
                break;
        }
        if(!m_tab.equals("Ad"))
            Info();
    }
    @Override public void Turn(String label) {
        if (m_tab.equals("Info")) {
            switch (label) {
                case "头像":
                    Head();
                    break;
                case "昵称":
                    Name();
                    break;
                case "联系方式":
                    Phone();
                    break;
                case "所在地址":
                    Ad();
                    break;
                case "性别":
                    Sex();
                    break;
            }
        }
    }

    //Layout
    void Clean() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(m_edit!=null) imm.hideSoftInputFromWindow(m_edit.m_edit.getWindowToken(), 0);
        m_edit=null;

        if (m_itemLayout.getChildCount() > 0) m_itemLayout.removeAllViews();
        if (m_tab.equals("Info")) {
            m_ok.setVisibility(View.GONE);
            m_ok_over.setVisibility(View.GONE);
        } else {
            m_ok.setVisibility(View.VISIBLE);
            m_ok_over.setVisibility(View.VISIBLE);
        }
    }
    //个人信息
    void Info() {
        m_tab = "Info";
        m_title.setText("个人信息");
        Clean();

        initTextItem("InfoDetails", m_itemLayout, "头像", m_user.m_head, 157, 0);
        initTextItem("InfoDetails", m_itemLayout, "用户名", m_user.m_user, 100, 0, false);
        initTextItem("InfoDetails", m_itemLayout, "联系方式", m_user.m_phone, 100, 0, false);
        initTextItem("InfoDetails", m_itemLayout, "昵称", m_user.m_name, 100, 0, true);
        initTextItem("InfoDetails", m_itemLayout, "所在地址", m_user.m_city, 100, 80, true);
        initTextItem("InfoDetails", m_itemLayout, "性别", m_user.m_sex, 100, 0, true);
    }
    //Head
    File m_head_tempFile;
    private static final int PHOTO_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int PHOTO_CLIP = 3;
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_hhMMss");
        return dateFormat.format(date) + ".jpg";
    }
    void Head() {
        m_tab = "Head";
        UDialog m_head_dialog = new UDialog(this, new UDialog.OnCustomDialogListener() {
            @Override
            public void back(String choose) {
                if (choose.equals("拍照")) {
                    getPicFromCamera();
                } else if (choose.equals("选择本地")) {
                    getPicFromPhoto();
                }
            }
        }, 2, new String[]{"拍照", "选择本地"});
        m_head_dialog.show();
        Info();
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
        m_head_tempFile = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(m_head_tempFile));
        startActivityForResult(intent, CAMERA_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_REQUEST:
                    startPhotoZoom(data.getData(), 150);
                    break;
                case CAMERA_REQUEST:
                    startPhotoZoom(Uri.fromFile(m_head_tempFile), 150);
                    break;
                case PHOTO_CLIP:
                    saveImage(data);
                    Ok();
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

            ByteArrayOutputStream stream = null ;
            try{
                stream = new ByteArrayOutputStream();
                assert bmp != null;
                bmp.compress(Bitmap.CompressFormat.PNG, 10, stream);
                byte[] byteArray = stream.toByteArray();
                m_user.m_head = byteArray;
                DatabaseHelper.Act.UpdateData(m_user.m_user,"head", byteArray);
            }finally{
                try {
                    if(stream != null)
                        stream.close() ;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //昵称
    void Name() {
        m_tab = "Name";
        m_title.setText("修改昵称");
        Clean();

        m_edit = initTextEdit(m_itemLayout, m_user.m_name);
        initTextView(m_itemLayout, "4-20个字符，可由中英文、数字、“_”、“-”组成", 18, 40);
    }
    //联系
    void Phone() {
        m_tab = "Phone";
        m_title.setText("修改联系方式");
        Clean();

        m_edit = initTextEdit(m_itemLayout, m_user.m_phone);
        initTextView(m_itemLayout, "11位的手机号码", 18, 40);
    }
    //所在地址
    ProvinceAreaHelper m_ad_provinceAreaHelper;
    private AdItem m_ad_adItem1, m_ad_adItem2, m_ad_adItem3;
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
        s1=m_user.m_province;
        s2=m_user.m_city;
        s3=m_user.m_district;
        String str1[] = m_ad_provinceAreaHelper.getProvinceData();
        String str2[] = {};
        if(!s1.isEmpty())
            str2=m_ad_provinceAreaHelper.updateCities(s1);
        String str3[] = {};
        if(!s2.isEmpty())
            str3=m_ad_provinceAreaHelper.updateAreas(s2);
        m_ad_adItem1 = new InfoDetails.AdItem(this, adLayout, "Province", 121,s1, str1);
        m_ad_adItem2 = new InfoDetails.AdItem(this, adLayout, "City", 307, s2, str2);
        m_ad_adItem3 = new InfoDetails.AdItem(this, adLayout, "District", 501, s3, str3);
        LinearLayout.LayoutParams Params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 135);
        Params1.gravity = Gravity.CENTER_HORIZONTAL;
        m_itemLayout.addView(adLayout, Params1);
        //自动
        UButton uBtn=new UButton(this,R.id.btn_info_details_1,"自动定位当前位置",25,280, 60,new int[]{R.color.white,R.color.eghitygray}, new int[]{R.drawable.btn_blue,R.drawable.btn_blue_over});
        uBtn.addToView(m_itemLayout,0);
        final Button btn1=(Button)findViewById(R.id.btn_info_details_1);
        assert btn1 != null;
        m_ad_autoing=false;
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!m_ad_autoing) {
                    InfoDetails.Location loc=new InfoDetails.Location();
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
                Message(info.Error);
        }
        m_ad_autoing=false;
    }
    public class AdItem {
        TextView m_text;
        View m_drop;
        FrameLayout m_adItem;
        FrameLayout m_pLayout;
        InfoDetails m_parent;
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
        AdItem(InfoDetails parent, FrameLayout adLayout, String state, int leftMargin, String firstText, final String[] strList) {
            m_parent = parent;
            m_strList = strList;
            m_state = state;

            m_pLayout = adLayout;
            m_adItem = new FrameLayout(parent);

            m_text = new TextView(parent);
            m_text.setIncludeFontPadding(false);

            SetText(firstText);

            m_text.setTextColor(ContextCompat.getColor(m_parent,R.color.thirtygray));
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
                        m_text.setTextColor(ContextCompat.getColor(m_parent,R.color.textblue));
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        m_drop.setBackgroundResource(R.drawable.icon_drop2_down_gray);
                        m_text.setTextColor(ContextCompat.getColor(m_parent,R.color.thirtygray));

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
                        m_text.setTextColor(ContextCompat.getColor(m_parent,R.color.thirtygray));
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
            BDLocationListener myListener = new InfoDetails.Location.MyLocationListener();
            info=new LocationInfo();
            mLocationClient = new LocationClient(InfoDetails.this.getApplicationContext());
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
    //性别
    UTextItem m_sex_item1,m_sex_item2,m_sex_item3;
    String m_sex_choose;
    void Sex(){
        m_tab="Sex";
        m_title.setText("修改性别");
        Clean();

        m_sex_choose=m_user.m_sex;
        m_sex_item1=new UTextItem(this,"InfoDetails","男",25);
        m_sex_item2=new UTextItem(this,"InfoDetails","女",25);
        m_sex_item3=new UTextItem(this,"InfoDetails","保密",25);
        m_sex_item1.addToView(m_itemLayout,100 ,0);
        m_sex_item2.addToView(m_itemLayout,100 ,0);
        m_sex_item3.addToView(m_itemLayout,100 ,0);
        switch (m_sex_choose) {
            case "男":
                m_sex_item1.m_layout.setBackgroundColor(ContextCompat.getColor(this,R.color.nintygray));
                break;
            case "女":
                m_sex_item2.m_layout.setBackgroundColor(ContextCompat.getColor(this,R.color.nintygray));
                break;
            case "保密":
                m_sex_item3.m_layout.setBackgroundColor(ContextCompat.getColor(this,R.color.nintygray));
                break;
        }

    }
    void Choose(String text){
        switch (text) {
            case "男":
                m_sex_item2.m_layout.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
                m_sex_item3.m_layout.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
                m_sex_choose = "男";
                break;
            case "女":
                m_sex_item1.m_layout.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
                m_sex_item3.m_layout.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
                m_sex_choose = "女";
                break;
            case "保密":
                m_sex_item1.m_layout.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
                m_sex_item2.m_layout.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
                m_sex_choose = "保密";
                break;
        }
    }

    //Item
    public UTextItem initTextItem(String parentClass, LinearLayout itemLayout, String label, String tips, int height, int topMargin, boolean isTouch){
        UTextItem uItem=new UTextItem(this,parentClass,label,tips,30,25,isTouch);
        uItem.addToView(itemLayout,height,topMargin);
        return uItem;
    }
    public UTextItem initTextItem(String parentClass, LinearLayout itemLayout, String label, byte[] pic, int height, int topMargin){
        UTextItem uItem=new UTextItem(this,parentClass,label,pic,30,25);
        uItem.addToView(itemLayout,height,topMargin);
        return uItem;
    }
    public UTextView initTextView(LinearLayout itemLayout, String label, int topMargin, int leftMargin){
        UTextView uText=new UTextView(this,label,24,R.color.eghitygray);
        uText.addToView(itemLayout,topMargin,leftMargin);
        return uText;
    }
    public UTextEdit initTextEdit(LinearLayout itemLayout, String value){
        UTextEdit uEdit=new UTextEdit(this,value,"",InputType.TYPE_CLASS_TEXT,30);
        uEdit.addToView(itemLayout,100,30);
        return uEdit;
    }
}
