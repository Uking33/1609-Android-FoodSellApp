package com.example.administrator.frozengoods;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShopSetting extends UActivity {
    ShopItem m_shop;
    UserItem m_user;
    byte[] m_head;
    String m_ad_province;//地址1
    String m_ad_city;//地址1
    String m_ad_district;//地址1
    String m_edit1,m_edit2,m_edit3,m_edit4;
    UTextEdit uEdit1,uEdit2,uEdit3,uEdit4;
    Button btn;

    //Create
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState,true);
        m_itemLayout.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(uEdit1!=null){
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
                        break;
                }
                return false;
            }
        });
        Intent intent=getIntent();
        m_user=intent.getParcelableExtra("user");
        m_shop=m_user.m_shop;
        if(m_id.equals("SHOP_NEW")){//注册
            m_head=Widget.DrawableToByte(R.drawable.default_logo);
            m_ad_province=m_user.m_province;
            m_ad_city=m_user.m_city;
            m_ad_district=m_user.m_district;
            m_edit1=m_user.m_name+"的店铺";
            m_edit2="";
            m_edit3=m_user.m_phone;
            m_edit4="";
        }
        else{
            m_head=m_shop.m_logo;
            m_ad_province=m_shop.m_ad_province;
            m_ad_city=m_shop.m_ad_city;
            m_ad_district=m_shop.m_ad_district;
            m_edit1=m_shop.m_shop;
            m_edit2=m_shop.m_ad_details;
            m_edit3=m_shop.m_phone;
            m_edit4=m_shop.m_adText;
        }
        Details();
    }
    @Override protected void onDestroy(){
        m_user=null;
        m_shop=null;
        m_head=null;
        m_ad_province=null;
        m_ad_city=null;
        m_ad_district=null;
        m_edit1=null;
        m_edit2=null;
        m_edit3=null;
        m_edit4=null;
        uEdit1=null;
        uEdit2=null;
        uEdit3=null;
        uEdit4=null;
        super.onDestroy();
        setContentView(R.layout.view_null);
    }

    //Turn
    @Override public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
    @Override public void Turn(String label){
        if(label.equals("店铺头像"))
            Logo();
        else if(label.equals("店铺地区"))
            Ad();
    }
    @Override public void Back(){
        switch(m_tab){
            case "Details":
                onBackPressed();
                break;
            default:
                Details();
                break;
        }
    }
    @Override public void Ok(){
        switch (m_tab){
            case "Ad":
                if (!m_ad_adItem1.m_value.isEmpty() && !m_ad_adItem2.m_value.isEmpty() && !m_ad_adItem3.m_value.isEmpty()){
                    m_ad_province=m_ad_adItem1.m_value;
                    m_ad_city=m_ad_adItem2.m_value;
                    m_ad_district=m_ad_adItem3.m_value;
                    Details();
                }
                else
                    Message("请把地址填写完整");
                break;
            case "Logo":
                Details();
                break;
            default:
                //save
                m_shop.m_logo=m_head;
                m_shop.m_ad_province=m_ad_province;
                m_shop.m_ad_city=m_ad_city;
                m_shop.m_ad_district=m_ad_district;
                m_shop.m_shop=m_edit1;
                m_shop.m_ad_details=m_edit2;
                m_shop.m_phone=m_edit3;
                m_shop.m_adText=m_edit4;
                if(DatabaseHelper.Act.UpdateData(m_user.m_user,m_shop))
                    Message("修改成功");
                else
                    Message("修改失败");
                //intent
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", m_user);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    void Clean() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(uEdit1!=null) imm.hideSoftInputFromWindow(uEdit1.m_edit.getWindowToken(), 0);
        if(uEdit2!=null) imm.hideSoftInputFromWindow(uEdit2.m_edit.getWindowToken(), 0);
        if(uEdit3!=null) imm.hideSoftInputFromWindow(uEdit3.m_edit.getWindowToken(), 0);
        if(uEdit4!=null) imm.hideSoftInputFromWindow(uEdit4.m_edit.getWindowToken(), 0);
        uEdit1=null;
        uEdit2=null;
        uEdit3=null;
        uEdit4=null;

        if (m_tab.equals("Ad")) {
            m_ok.setVisibility(View.VISIBLE);
            m_ok_over.setVisibility(View.VISIBLE);
        } else {
            m_ok.setVisibility(View.GONE);
            m_ok_over.setVisibility(View.GONE);
        }
        if (m_itemLayout.getChildCount() > 0) m_itemLayout.removeAllViews();
    }
    void Details() {
        m_tab = "Details";
        m_title.setText("店铺设置");
        Clean();

        initTextItem("ShopSetting", m_itemLayout, "店铺头像",  m_head, 157, 0);
        initTextItem("ShopSetting", m_itemLayout, "店铺地区", m_ad_province+m_ad_city+m_ad_district, 100, 0, true);

        uEdit1=initTextEdit(this, m_itemLayout, R.id.edit_shop_setting_1, "店铺名称", "请输入店铺名称", InputType.TYPE_CLASS_TEXT , 100, 30);
        uEdit2=initTextEdit(this, m_itemLayout, R.id.edit_shop_setting_2, "详细地址", "请输入店铺地址", InputType.TYPE_CLASS_TEXT , 100, 30);
        uEdit3=initTextEdit(this, m_itemLayout, R.id.edit_shop_setting_3, "手机号码", "请输入手机号码", InputType.TYPE_CLASS_TEXT , 100, 30);
        uEdit4=initTextEdit(this, m_itemLayout, R.id.edit_shop_setting_4, "留言", "留下店铺的宣传语吧", InputType.TYPE_CLASS_TEXT , 100, 30);
        uEdit1.m_edit.setText(m_edit1);
        uEdit2.m_edit.setText(m_edit2);
        uEdit3.m_edit.setText(m_edit3);
        uEdit4.m_edit.setText(m_edit4);
        initButton(this, m_itemLayout, R.id.btn_shop_setting_1, "完  成", 30, 200, new int[]{R.color.white,R.color.eghitygray}, new int[]{R.drawable.btn_blue,R.drawable.btn_blue_over});
        btn=(Button)findViewById(R.id.btn_shop_setting_1);

        uEdit1.m_edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        uEdit2.m_edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        uEdit3.m_edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        uEdit4.m_edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
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
                int length3 = m_edit3.length();
                if (!m_ad_province.isEmpty() && !m_ad_city.isEmpty() && !m_ad_district.isEmpty() && !m_edit1.isEmpty() && !m_edit2.isEmpty() && length3==11 && !m_edit4.isEmpty()) {
                    btn.setEnabled(true);
                    btn.setBackgroundResource(R.drawable.btn_blue);
                }
                else {
                    btn.setEnabled(false);
                    btn.setBackgroundResource(R.drawable.btn_blue_disactivity);
                }
            }
        };
        uEdit1.m_edit.addTextChangedListener(textWatcher);
        uEdit2.m_edit.addTextChangedListener(textWatcher);
        uEdit3.m_edit.addTextChangedListener(textWatcher);
        uEdit4.m_edit.addTextChangedListener(textWatcher);


        if (!m_ad_province.isEmpty() && !m_ad_city.isEmpty() && !m_ad_district.isEmpty() && !m_edit1.isEmpty() && !m_edit2.isEmpty() && m_edit3.length()==11 && !m_edit4.isEmpty()) {
            btn.setEnabled(true);
            btn.setBackgroundResource(R.drawable.btn_blue);
        }
        else {
            btn.setEnabled(false);
            btn.setBackgroundResource(R.drawable.btn_blue_disactivity);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ok();
            }
        });
    }

    //Head
    UDialog m_head_dialog;
    File m_head_tempFile;
    private static final int PHOTO_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int PHOTO_CLIP = 3;
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_hhMMss");
        return dateFormat.format(date) + ".jpg";
    }
    void Logo() {
        m_tab = "Logo";
        m_head_dialog = new UDialog(this, new UDialog.OnCustomDialogListener() {
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
            bmp.compress(Bitmap.CompressFormat.PNG, 10, stream);
            m_head = stream.toByteArray();
        }
        Ok();
    }
    //Ad
    ProvinceAreaHelper m_ad_provinceAreaHelper;
    private ShopSetting.AdItem m_ad_adItem1, m_ad_adItem2, m_ad_adItem3;
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
        m_ad_adItem1 = new ShopSetting.AdItem(this, adLayout, "Province", 121,s1, str1);
        m_ad_adItem2 = new ShopSetting.AdItem(this, adLayout, "City", 307, s2, str2);
        m_ad_adItem3 = new ShopSetting.AdItem(this, adLayout, "District", 501, s3, str3);
        LinearLayout.LayoutParams Params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 135);
        Params1.gravity = Gravity.CENTER_HORIZONTAL;
        m_itemLayout.addView(adLayout, Params1);
        //自动
        UButton uBtn=new UButton(this,R.id.btn_shop_setting_2,"自动定位当前位置",25,280, 60,new int[]{R.color.white,R.color.eghitygray}, new int[]{R.drawable.btn_blue,R.drawable.btn_blue_over});
        uBtn.addToView(m_itemLayout,0);
        Button btn1=(Button)findViewById(R.id.btn_shop_setting_2);
        assert btn1 != null;
        m_ad_autoing=false;
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(!m_ad_autoing) {
                ShopSetting.Location loc=new ShopSetting.Location();
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
        ShopSetting m_parent;
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
        AdItem(ShopSetting parent, FrameLayout adLayout, String state, int leftMargin, String firstText, final String[] strList) {
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
            BDLocationListener myListener = new ShopSetting.Location.MyLocationListener();
            info=new LocationInfo();
            mLocationClient = new LocationClient(ShopSetting.this.getApplicationContext());
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
    public UTextItem initTextItem(String parentClass, LinearLayout itemLayout, String label, byte[] pic, int height, int topMargin){//头像
        UTextItem uItem=new UTextItem(this,parentClass,label,pic,30,25);
        uItem.addToView(itemLayout,height,topMargin);
        return uItem;
    }
    public UTextItem initTextItem(String parentClass, LinearLayout itemLayout, String label, String tips, int height, int topMargin, boolean isTouch){//地区
        UTextItem uItem=new UTextItem(this,parentClass,label,tips,30,25,isTouch);
        uItem.addToView(itemLayout,height,topMargin);
        return uItem;
    }
    public UTextEdit initTextEdit(UActivity m_parent, LinearLayout itemLayout, int id, String label, String tips, int type, int height, int size){
        UTextEdit uEdit=new UTextEdit(m_parent,id,label,tips,type,size,470,152);
        uEdit.addToView(itemLayout,height,30);
        return uEdit;
    }
    public void initButton(UActivity m_parent, LinearLayout itemLayout, int id, String text, int size, int topMargin, int[] textColor, int[] backgroundColor){
        UButton uBtn=new UButton(m_parent,id,text,size,298, 72,textColor,backgroundColor);
        uBtn.addToView(itemLayout,topMargin);
    }
}
