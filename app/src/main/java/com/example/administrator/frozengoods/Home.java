package com.example.administrator.frozengoods;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.ArrayList;

public class Home extends Fragment {
    private View m_this;
    private  MainActivity m_parent;
    private int screenWidth;
    private ArrayList<View> picList;
    private ImageView[] picImageViews;
    LinearLayout classLayout;
    int classContext[]={R.drawable.index_class_1,R.drawable.index_class_2,R.drawable.index_class_3,R.drawable.index_class_4,R.drawable.index_class_5};
    private EditText m_edit;

    ArrayList<GoodItem> m_hotList;
    ArrayList<GoodItem> m_adList;
    //Create
    @Override public void onAttach(Context context){
        m_parent=(MainActivity) context;
        super.onAttach(context);
    }
    @Override public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        m_this = inflater.inflate(R.layout.home, container, false);
        initView();
        Home.Location loc=new Home.Location();
        loc.getData();
        return m_this;
    }

    //Turn
    private void  Turn(String label) {
        Intent intent;
        switch(label){
            case "HOME_AD":
                if(m_parent.m_data.m_user!=null) {
                    intent=m_parent.Turn1(InfoDetails.class, label);
                    intent.putExtra("user",m_parent.m_data.m_user);
                    m_parent.Turn2(intent,11);
                }
                else {
                    intent = m_parent.Turn1(InfoUser.class, label);
                    m_parent.Turn2(intent, 10);
                }
                break;
            case "HOME_FIND":
                intent = new Intent(m_parent,ItemFind.class);
                Bundle bundle = new Bundle();
                m_parent.m_data.clearPic();
                bundle.putParcelable("data", m_parent.m_data);
                bundle.putString("id","HOME_FIND");
                bundle.putString("index","0");
                bundle.putString("context","");
                intent.putExtras(bundle);
                m_parent.Turn2(intent, 12);
                break;
            case "HOME_NEWS":
                if(m_parent.m_data.m_user!=null) {
                    intent=m_parent.Turn1(InfoNews.class, label);
                    m_parent.m_data.m_user.clearPic();
                    intent.putExtra("data",m_parent.m_data);
                    m_parent.Turn2(intent,13);
                }
                else{
                    intent = m_parent.Turn1(InfoUser.class, label);
                    m_parent.Turn2(intent, 10);
                }
                break;
        }
    }

    //Update
    public void Update(String label){
        switch(label){
            case "Ad"://地址修改
                String str = m_parent.m_data.m_user.m_city;
                ((TextView) m_this.findViewById(R.id.home_index_ad_text)).setText(str.substring(0, 2));
                break;
            case "New"://消息修改
                ImageView red=(ImageView)m_this.findViewById(R.id.home_news_point);
                if(m_parent.m_data.GetUnReadNum()>0)
                    red.setVisibility(View.VISIBLE);
                else
                    red.setVisibility(View.GONE);
                break;
            case "List":
                ((FrameLayout)m_this.findViewById(R.id.home_layout_hot)).removeAllViews();
                ((FrameLayout)m_this.findViewById(R.id.home_layout_ad)).removeAllViews();
                m_hotList = DatabaseHelper.Act.GetGoodItem(" ORDER BY id DESC LIMIT 4", null);
                m_adList = DatabaseHelper.Act.GetGoodItem(" ORDER BY id DESC LIMIT 4", null);
                if (m_hotList == null) m_hotList = new ArrayList<>();
                if (m_adList == null) m_adList = new ArrayList<>();
                DatabaseHelper.Act.GetGoodItemPic(m_hotList," ORDER BY id DESC LIMIT 4", null);
                DatabaseHelper.Act.GetGoodItemPic(m_adList," ORDER BY id DESC LIMIT 4", null);
                initItemList((FrameLayout)m_this.findViewById(R.id.home_layout_hot),"热卖场","更多热卖冻品 >",m_hotList);
                initItemList((FrameLayout)m_this.findViewById(R.id.home_layout_ad),"推销冻品","更多推销冻品 >",m_adList);
                break;
        }
    }
    public void Login(){
        //AD
        if (m_parent.m_data.m_localInfo!=null && m_parent.m_data.m_localInfo.Error.equals("定位成功")){
            if(m_parent.m_data.m_user!=null) {
                m_parent.m_data.m_user.m_province = m_parent.m_data.m_localInfo.Province;
                m_parent.m_data.m_user.m_city = m_parent.m_data.m_localInfo.City;
                m_parent.m_data.m_user.m_district = m_parent.m_data.m_localInfo.District;
                DatabaseHelper.Act.UpdateData(m_parent.m_data.m_user.m_user,new String[]{"province", "city", "district"}, new String[]{m_parent.m_data.m_localInfo.Province, m_parent.m_data.m_localInfo.City, m_parent.m_data.m_localInfo.District});
            }
        }
        else if(!m_parent.m_data.m_user.m_city.equals("")) {
            String str = m_parent.m_data.m_user.m_city;
            ((TextView) m_this.findViewById(R.id.home_index_ad_text)).setText(str.substring(0, 2));
        }
        //News
        ImageView red=(ImageView)m_this.findViewById(R.id.home_news_point);
        if(m_parent.m_data.GetUnReadNum()>0)
            red.setVisibility(View.VISIBLE);
        else
            red.setVisibility(View.GONE);
    }
    public void Logout(){
        //News
        ImageView red=(ImageView)m_this.findViewById(R.id.home_news_point);
        if(m_parent.m_data.GetUnReadNum()>0)
            red.setVisibility(View.VISIBLE);
        else
            red.setVisibility(View.GONE);
    }
    //Layout
    public void initView() {
        ((ViewPager) m_this.findViewById(R.id.pic_viewPager)).removeAllViews();
        ((ViewGroup) m_this.findViewById(R.id.pic_viewGroup)).removeAllViews();
        ((LinearLayout)m_this.findViewById(R.id.home_layout_class_item)).removeAllViews();
        ((FrameLayout)m_this.findViewById(R.id.home_layout_hot)).removeAllViews();
        ((FrameLayout)m_this.findViewById(R.id.home_layout_ad)).removeAllViews();
        DisplayMetrics outMetrics=new DisplayMetrics();//获取屏幕的宽度
        m_parent.getWindow().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        screenWidth=outMetrics.widthPixels;
        initTitle();
        initPicRun();//滚动图片
        initClass();//分类
        m_hotList = DatabaseHelper.Act.GetGoodItem(" ORDER BY id DESC LIMIT 4", null);
        m_adList = DatabaseHelper.Act.GetGoodItem(" ORDER BY id DESC LIMIT 4", null);
        if (m_hotList == null) m_hotList = new ArrayList<>();
        if (m_adList == null) m_adList = new ArrayList<>();
        DatabaseHelper.Act.GetGoodItemPic(m_hotList," ORDER BY id DESC LIMIT 4", null);
        DatabaseHelper.Act.GetGoodItemPic(m_adList," ORDER BY id DESC LIMIT 4", null);
        initItemList((FrameLayout)m_this.findViewById(R.id.home_layout_hot),"热卖场","更多热卖冻品 >",m_hotList);
        initItemList((FrameLayout)m_this.findViewById(R.id.home_layout_ad),"推销冻品","更多推销冻品 >",m_adList);
    }
    //Layout-Title
    private void initTitle(){
        //AD
        FrameLayout m_ad = ((FrameLayout) m_this.findViewById(R.id.home_index_ad));
        m_ad.setOnTouchListener(new View.OnTouchListener() {
            boolean click = false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        click = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                                event.getY() > 0 && event.getY() < v.getHeight()) {
                            Turn("HOME_AD");
                        }
                        break;
                }
                return true;
            }
        });
        //Edit
        m_edit=((EditText)m_this.findViewById(R.id.home_edit_find));
        m_edit.clearFocus();
        m_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)  {
                if (actionId== EditorInfo.IME_ACTION_SEARCH ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER))
                {
                    InputMethodManager imm = (InputMethodManager) m_parent.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(m_edit.getWindowToken(), 0);
                    m_edit.clearFocus();
                    Turn("HOME_FIND");
                    return true;
                }
                return false;
            }
        });
        m_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!m_edit.hasFocus()) {
                    InputMethodManager imm = (InputMethodManager) m_parent.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(m_edit.getWindowToken(), 0);
                    m_edit.clearFocus();
                }
            }
        });
        //news
        ImageView red=(ImageView)m_this.findViewById(R.id.home_news_point);
        if(m_parent.m_data.GetUnReadNum()>0)
            red.setVisibility(View.VISIBLE);
        else
            red.setVisibility(View.GONE);
        m_this.findViewById(R.id.home_news).setOnTouchListener(new View.OnTouchListener() {
            boolean click = false;

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        click = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                                event.getY() > 0 && event.getY() < v.getHeight()) {
                            Turn("HOME_NEWS");
                        }
                        break;
                }
                return true;
            }
        });
    }
    public void returnLocation(LocationInfo localInfo) {//定位
        m_parent.m_data.m_localInfo = localInfo;
        if (localInfo.Error.equals("定位成功")){
            String str=localInfo.City;
            ((TextView) m_this.findViewById(R.id.home_index_ad_text)).setText(str.substring(0,2));

            if(m_parent.m_data.m_user!=null) {
                m_parent.m_data.m_user.m_province = localInfo.Province;
                m_parent.m_data.m_user.m_city = localInfo.City;
                m_parent.m_data.m_user.m_district = localInfo.District;
                DatabaseHelper.Act.UpdateData(m_parent.m_data.m_user.m_user,new String[]{"province", "city", "district"}, new String[]{localInfo.Province, localInfo.City, localInfo.District});
            }
        }
        else {
            ((TextView) m_this.findViewById(R.id.home_index_ad_text)).setText("地址");
            if (!localInfo.Error.isEmpty())
                m_parent.Message(localInfo.Error);
            if(m_parent.m_data.m_user!=null) {
                m_parent.m_data.m_localInfo.Province=m_parent.m_data.m_user.m_province;
                m_parent.m_data.m_localInfo.City=m_parent.m_data.m_user.m_city;
                m_parent.m_data.m_localInfo.District=m_parent.m_data.m_user.m_district;
                m_parent.m_data.m_localInfo.Error="定位成功";
                String str=m_parent.m_data.m_localInfo.City;
                ((TextView) m_this.findViewById(R.id.home_index_ad_text)).setText(str.substring(0,2));
            }
        }
    }
    class Location {
        private LocationClient mLocationClient = null;
        public LocationInfo info;
        private LocationClientOption option;
        Location(){
            BDLocationListener myListener = new Home.Location.MyLocationListener();
            info=new LocationInfo();
            mLocationClient = new LocationClient(m_parent.getApplicationContext());
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
    //Layout-Class
    private void initClass(){
        classLayout = (LinearLayout)m_this.findViewById(R.id.home_layout_class_item);//实例化LinearLayout对象
        for (int i = 0; i < 5; i++) {//设置循环创建Button对系那个
            LinearLayout classItem=new LinearLayout(m_parent);
            classItem.setGravity(Gravity.CENTER);

            final int index=i;
            classItem.setOnTouchListener(new View.OnTouchListener() {
                boolean click=false;
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            click=true;
                            break;
                        case MotionEvent.ACTION_UP:
                            if(click && event.getX()>0 && event.getX()<v.getWidth() &&
                                    event.getY()>0 && event.getY()<v.getHeight()) {
                                Intent intent = new Intent(m_parent,ItemFind.class);
                                Bundle bundle = new Bundle();
                                m_parent.m_data.clearPic();
                                bundle.putParcelable("data", m_parent.m_data);
                                bundle.putString("id","HOME_CLASS");
                                bundle.putString("index",String.valueOf(index));
                                bundle.putString("context","");
                                intent.putExtras(bundle);
                                m_parent.Turn2(intent, 12);
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
            //图
            ImageView classButton=new ImageView(m_parent);
            classButton.setBackgroundResource(classContext[i]);
            classItem.addView(classButton,104,124);
            //layout
            LinearLayout.LayoutParams Params1 = new LinearLayout.LayoutParams((screenWidth-60)/classContext.length,ViewGroup.LayoutParams.MATCH_PARENT);
            classLayout.addView(classItem,Params1);
        }
    }
    //Layout-Pic
    private void initPicRun() {
        //---绑定控件initView---
        ViewPager picViewPager = (ViewPager) m_this.findViewById(R.id.pic_viewPager);
        ViewGroup picGroup = (ViewGroup) m_this.findViewById(R.id.pic_viewGroup);

        //---初始化ViewPager---
        //对于这几个想要动态载入的page页面，使用LayoutInflater.inflate()来找到其布局文件，并实例化为View对象
        LayoutInflater inflater = m_parent.getLayoutInflater();
        View page1=new View(m_parent);
        View page2=new View(m_parent);
        page1.setBackgroundResource(R.drawable.run_pic1);
        page2.setBackgroundResource(R.drawable.run_pic2);
        //添加到集合中
        picList = new ArrayList<>();
        picList.add(page1);
        picList.add(page2);
        PagerAdapter picAdapter = new PagerAdapter() {
            //获取当前界面个数
            @Override
            public int getCount() {
                return picList.size();
            }

            //判断是否由对象生成页面
            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(picList.get(position));
            }

            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = picList.get(position);
                container.addView(view);
                return view;
            }
        };

        //---initPageAdapter---

        //---初始化下面的小圆点的方法initPointer---
        //有多少个界面就new多长的数组
        picImageViews = new ImageView[picList.size()];
        for (int i = 0; i < picImageViews.length; i++) {
            ImageView picImageView = new ImageView(m_parent);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            picImageView.setLayoutParams(lp);
            picImageViews[i] = picImageView;
            //初始化第一个page页面的图片的原点为选中状态
            if (i == 0) {
                //表示当前图片
                picImageViews[i].setBackgroundResource(R.drawable.icon_page_focused);
                //在java代码中动态生成ImageView的时候
                //要设置其BackgroundResource属性才有效
                //设置ImageResource属性无效
            } else {
                picImageViews[i].setBackgroundResource(R.drawable.icon_page_unfocused);
            }
            LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(12,12);
            l.bottomMargin=17;
            l.rightMargin=8;
            picGroup.addView(picImageViews[i],l);
        }
        //---为控件绑定事件,绑定适配器initEvent---
        picViewPager.setAdapter(picAdapter);
        picViewPager.addOnPageChangeListener(new GuidePageChangeListener());
    }
    public class GuidePageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        //页面滑动完成后执行
        @Override
        public void onPageSelected(int position) {
            //判断当前是在那个page，就把对应下标的ImageView原点设置为选中状态的图片
            for (int i = 0; i < picImageViews.length; i++) {
                picImageViews[position].setBackgroundResource(R.drawable.icon_page_focused);
                if (position != i) {
                    picImageViews[i].setBackgroundResource(R.drawable.icon_page_unfocused);
                }
            }
        }
        //监听页面的状态，0--静止 1--滑动  2--滑动完成
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
    //Layout-List
    private void initItemList(final FrameLayout layout,final String title,final String tip, final ArrayList<GoodItem> item){
        FrameLayout picListText=new FrameLayout(m_parent);
        LinearLayout picListItems=new LinearLayout(m_parent);
        picListItems.setOrientation(LinearLayout.HORIZONTAL);
        picListItems.setGravity(Gravity.CENTER_VERTICAL);

        layout.setOnTouchListener(new View.OnTouchListener() {
            boolean click=false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        click=true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if(click && event.getX()>0 && event.getX()<v.getWidth() &&
                                event.getY()>0 && event.getY()<v.getHeight()) {
                            Intent intent = new Intent(m_parent,ItemFind.class);
                            Bundle bundle = new Bundle();
                            m_parent.m_data.clearPic();
                            bundle.putParcelable("data", m_parent.m_data);
                            bundle.putString("id","HOME_FIND");
                            bundle.putString("index","0");
                            bundle.putString("context","");
                            intent.putExtras(bundle);
                            m_parent.Turn2(intent, 12);
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
        //标题
        TextView titleText=new TextView(m_parent);
        titleText.setIncludeFontPadding(false);
        titleText.setText(title);
        titleText.setTextColor(ContextCompat.getColor(m_parent,R.color.textblue));
        titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX,30);
        FrameLayout.LayoutParams Params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params2.gravity=Gravity.START;
        picListText.addView(titleText,Params2);
        //tip
        TextView tipText=new TextView(m_parent);
        tipText.setIncludeFontPadding(false);
        tipText.setText(tip);
        tipText.setTextColor(ContextCompat.getColor(m_parent,R.color.eghitygray));
        tipText.setTextSize(TypedValue.COMPLEX_UNIT_PX,23);
        FrameLayout.LayoutParams Params3 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params3.gravity=Gravity.END|Gravity.CENTER_VERTICAL;
        picListText.addView(tipText,Params3);
        //Item
        if(item!=null && item.size()!=0) {
            for (int i = 0; i < item.size(); i++) {
                LinearLayout picListItem = new LinearLayout(m_parent);
                picListItem.setOrientation(LinearLayout.VERTICAL);
                //pic
                ImageView picListItemBtn = new ImageView(m_parent);
                picListItemBtn.setScaleType(ImageView.ScaleType.FIT_XY);
                picListItemBtn.setImageDrawable(Widget.ByteToDrawable(item.get(i).pic1));
                picListItem.addView(picListItemBtn, 140, 140);
                //label
                TextView picListItemText = new TextView(m_parent);
                picListItemText.setIncludeFontPadding(false);
                if (item.get(i).name.length() <= 7)
                    picListItemText.setText(item.get(i).name);
                else {
                    String str=item.get(i).name.substring(0, 6) + "...";
                    picListItemText.setText(str);
                }
                picListItemText.setTextColor(ContextCompat.getColor(m_parent,R.color.twentygray));
                picListItemText.setTextSize(TypedValue.COMPLEX_UNIT_PX, 20);
                LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                p1.topMargin = 17;
                picListItem.addView(picListItemText, p1);
                //value
                TextView picListItemPrice = new TextView(m_parent);
                picListItemPrice.setIncludeFontPadding(false);
                if(item.get(i).price>0)
                    picListItemPrice.setText("￥" + item.get(i).price + "/斤");
                else
                    picListItemPrice.setText("面谈");
                picListItemPrice.setTextColor(ContextCompat.getColor(m_parent,R.color.priceorange));
                picListItemPrice.setTextSize(TypedValue.COMPLEX_UNIT_PX, 20);
                LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                p2.topMargin = 7;
                picListItem.addView(picListItemPrice, p2);

                LinearLayout.LayoutParams Params5 = new LinearLayout.LayoutParams(140, ViewGroup.LayoutParams.MATCH_PARENT);
                Params5.rightMargin = 33;
                picListItems.addView(picListItem, Params5);
                final int index=i;
                picListItem.setOnTouchListener(new View.OnTouchListener() {
                    boolean click = false;
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                click = true;
                                break;
                            case MotionEvent.ACTION_UP:
                                if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                                        event.getY() > 0 && event.getY() < v.getHeight()) {
                                    Intent intent;
                                    if(m_parent.m_data.m_user==null) {
                                        intent=m_parent.Turn1(ItemFind.class,"LIST_ITEM");
                                        m_parent.m_data.clearPic();
                                        intent.putExtra("data", m_parent.m_data);
                                        intent.putExtra("index","0");
                                        intent.putExtra("context","");
                                        m_parent.Turn2(intent,12);
                                    }
                                    else{
                                        intent=m_parent.Turn1(ItemDetails.class,"LIST_ITEM");
                                        m_parent.m_data.clearPic();
                                        intent.putExtra("data", m_parent.m_data);
                                        if(title.equals("热卖场")){
                                            intent.putExtra("item",m_hotList.get(index));
                                        }
                                        else{
                                            intent.putExtra("item",m_adList.get(index));
                                        }
                                        m_parent.Turn2(intent,13);
                                    }
                                }
                                click = false;
                                break;
                            case MotionEvent.ACTION_CANCEL:
                                if (click) {
                                    click = false;
                                }
                                break;
                        }
                        return true;
                    }
                });
            }
        }
        else{
            TextView t1 = new TextView(m_parent);
            t1.setIncludeFontPadding(false);
            t1.setText("- 暂无商品信息 -");
            t1.setTextColor(ContextCompat.getColor(m_parent, R.color.seventygray));
            t1.setTextSize(TypedValue.COMPLEX_UNIT_PX, 25);
            t1.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 204);
            p1.gravity=Gravity.CENTER;
            picListItems.addView(t1, p1);
        }

        //Layout
        FrameLayout.LayoutParams Params6 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params6.gravity =Gravity.TOP;
        Params6.topMargin=26;
        layout.addView(picListText,Params6);
        FrameLayout.LayoutParams Params7 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params7.gravity =Gravity.TOP;
        Params7.topMargin=74;
        layout.addView(picListItems,Params7);
        //分割线
        View line=new View(m_parent);
        line.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.nintygray));
        FrameLayout.LayoutParams Params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1);
        Params1.gravity =Gravity.BOTTOM;
        layout.addView(line,Params1);
    }
}
