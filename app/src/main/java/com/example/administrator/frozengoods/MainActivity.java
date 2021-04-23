package com.example.administrator.frozengoods;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static MainActivity MainAct;
    private LinearLayout mainLayoutIndex;

    public ViewPager mViewPager;
    public Data m_data;

    public Home frameHome;
    public Buy frameBuy;
    public Follow frameFollow;
    public Shop frameShop;
    public Info frameInfo;
    //Create
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainAct = this;
        initData();
        initView();
    }

    //Turn
    public void Turn(Class next,String re){
        Intent intent = new Intent(MainActivity.this,next);
        Bundle bundle = new Bundle();
        bundle.putString("id",re);
        intent.putExtras(bundle);
        startActivityForResult(intent,0);
    }
    public void Turn(Class next,String id,String index){
        Intent intent = new Intent(MainActivity.this,next);
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", m_data);
        bundle.putString("id",id);
        bundle.putString("index",index);
        intent.putExtras(bundle);
        startActivityForResult(intent,0);
    }
    public Intent Turn1(Class next,String id){
        Intent intent = new Intent(MainActivity.this,next);
        intent.putExtra("id",id);
        return intent;
    }
    public void Turn2(Intent intent,int requestCode){
        startActivityForResult(intent,requestCode);
    }

    //Update
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data==null)
            return;
        if(resultCode==RESULT_OK) {
            String str1;
            switch (requestCode) {
                case 10://登陆
                    str1=data.getStringExtra("user");
                    if(m_data.LogIn(str1)) {
                        Message("登录成功");
                        Login();
                    }
                    break;
                case 11://地址
                    m_data.m_user=data.getParcelableExtra("user");
                    frameHome.Update("Ad");
                    frameInfo.Update();
                    break;
                case 12://搜索
                    Data dd=data.getParcelableExtra("data");
                    if(dd.m_user!=null && m_data.m_user==null) {
                        m_data=dd;
                        m_data.m_user.getPic();
                        Login();
                    }
                    if(dd.m_user!=null &&  dd.m_user != null){
                        m_data=dd;
                        m_data.m_user.getPic();
                        NewsUpdate();
                    }
                    frameFollow.initView();
                    frameShop.Update("Follow",null);
                    break;
                case 13://消息
                    m_data.m_chatList=data.getParcelableArrayListExtra("chats");
                    m_data.m_user.getPic();
                    NewsUpdate();
                    frameFollow.initView();
                    frameShop.Update("Follow",null);
                    break;
                case 20:
                    m_data.m_user.getPic();
                    frameBuy.Update("New",data);
                    break;
                case 21:
                    m_data.m_user.getPic();
                    frameBuy.Update("Edit",data);
                    break;
                case 22://news
                    frameBuy.Update("Chat",data);
                    if(frameHome.isAdded()) frameHome.Update("New");
                    if(frameShop.isAdded()) frameShop.Update("New",null);
                    break;
                case 30:
                    ChatItems chat=data.getParcelableExtra("chat");
                    m_data.m_chatList.set(frameFollow.m_chooseIndex,chat);
                    NewsUpdate();
                case 31:
                    m_data.m_chatList=data.getParcelableArrayListExtra("chats");
                    m_data.m_user.getPic();
                    NewsUpdate();
                    FollowUpdate();
                    break;
                case 32:
                    break;
                case 33:
                    m_data.m_chatList=data.getParcelableArrayListExtra("chats");
                    String id = data.getStringExtra("id");
                    boolean isFollowShop=data.getBooleanExtra("isFollow",true);
                    if(!isFollowShop)
                        frameFollow.Cancel(frameFollow.m_chooseIndex);
                    m_data.m_user.getPic();
                    NewsUpdate();
                    if (!id.isEmpty()) {
                        GoodItem item = DatabaseHelper.Act.GetGoodItem(" where id=? LIMIT 1", new String[]{id}).get(0);
                        Intent intent = Turn1(ItemDetails.class,"Follow");
                        intent.putExtra("data", m_data);
                        m_data.m_user.clearPic();
                        intent.putExtra("item", item);
                        Turn2(intent,31);
                    }
                    else
                        FollowUpdate();
                    break;
                case 40:
                    m_data.m_user=data.getParcelableExtra("user");
                    frameShop.Login();
                    mViewPager.setCurrentItem(3);
                    break;
                case 41:
                    m_data.m_chatList=data.getParcelableArrayListExtra("chats");
                    m_data.m_user.getPic();
                    NewsUpdate();
                    break;
                case 42:
                    m_data.m_user=data.getParcelableExtra("user");
                    frameShop.Update("Shop",null);
                    break;
                case 43:
                    m_data.m_user.getPic();
                    frameShop.Update("Items",data);
                    frameHome.Update("List");
                    FollowUpdate();
                    break;
                case 50://登陆
                    str1=data.getStringExtra("user");
                    if(m_data.LogIn(str1)) {
                        Message("登录成功");
                        Login();
                    }
                    break;
                case 51://修改密码
                    break;
                case 52://个人信息
                    m_data.m_user=data.getParcelableExtra("user");
                    frameInfo.Update();
                    break;
                case 53://消息
                    m_data.m_chatList=data.getParcelableArrayListExtra("chats");
                    m_data.m_user.getPic();
                    NewsUpdate();
                    break;
                default:
                    break;
            }
        }
        else if(resultCode==RESULT_CANCELED) {
            switch (requestCode) {
                case 40:
                    mViewPager.setCurrentItem(4);
                    break;
                default:
                    break;
            }
        }
    }
    void Login(){
        if(frameHome.isAdded()) frameHome.Login();
        if(frameBuy.isAdded()) frameBuy.initView();
        if(frameFollow.isAdded()) frameFollow.Login();
        if(frameShop.isAdded()) frameShop.Login();
        if(frameInfo.isAdded()) frameInfo.Login();
    }
    void Logout(){
        if(frameHome.isAdded()) frameHome.Logout();
        if(frameBuy.isAdded()) frameBuy.initView();
        if(frameFollow.isAdded()) frameFollow.Logout();
        if(frameShop.isAdded()) frameShop.Logout();
        if(frameInfo.isAdded()) frameInfo.Logout();
    }
    void NewsUpdate(){
        if(frameHome.isAdded()) frameHome.Update("New");
        if(frameShop.isAdded()) frameShop.Update("New",null);
    }
    void GetUpdate() {
        if(frameBuy.isAdded() && frameBuy.m_index==0 && mViewPager.getCurrentItem()!=1) frameBuy.Update();
        if(frameFollow.isAdded() && frameFollow.m_index==1 && mViewPager.getCurrentItem()!=2) frameFollow.initView();
    }
    void FollowUpdate() {
        if(frameFollow.isAdded() && frameFollow.m_index!=1 && mViewPager.getCurrentItem()!=2)
            frameFollow.initView();
        if(frameShop.isAdded() && mViewPager.getCurrentItem()!=3)
            frameShop.Update("Follow",null);
    }

    //Layout
    private void initData() {
        new DatabaseHelper(this,"users.db");
        m_data=new Data();
        switch (DatabaseHelper.Act.Register1("system", "00000000000","1234")){
            case -1:
                initSystem();
                m_data.m_user=null;
                break;
            case 0:
            case 1:
                break;
            default:
                Message("初始化数据出错！正在重新初始化请勿操作");
                Thread thread=new Thread(new Runnable(){
                    @Override
                    public void run(){
                        boolean isRun=true;
                        while (isRun){
                            if(DatabaseHelper.Act.Register1("system", "00000000000","1234")==1) {
                                initSystem();
                                m_data.m_user = null;
                                isRun=false;
                                Message("初始化成功");
                            }
                        }
                    }
                });
                thread.start();
                break;
        }
    }
    private boolean initSystem(){
        if(DatabaseHelper.Act.Register2("system", "18826139918", "123456", "123456")==1) {
            //goodItem1
            GoodItem item = new GoodItem();
            item.id = "2016-12-01_00:00:00_'" + 0 + "'";
            item.user = "system";
            item.province = "广东省";
            item.city = "广州市";
            item.district = "番禺区";
            item.state = "On";
            item.pic_num = 1;
            item.pic1 = Widget.DrawableToByte(R.drawable.default_item);
            item.pic2 = null;
            item.pic3 = null;
            item.class1 = "全部";
            item.class2 = "全部";
            item.name = "肉1(有价格)";
            item.num = 100;
            item.price = 100;
            item.unit = "斤";
            item.shop = "冰栈";
            item.details = "这是系统推出的一个商品";
            if (!DatabaseHelper.Act.NewItem(item))
                return false;
            //goodItem2
            item.id = "2016-12-02_00:00:00_'" + 0 + "'";
            item.name = "肉2(有价格)";
            item.price = 50;
            item.num = 50;
            item.details = "这是系统推出的一个商品\n这是系统推出的一个商品\n这是系统推出的一个商品\n这是系统推出的一个商品\n这是系统推出的一个商品";
            item.pic_num = 2;
            item.pic2 = Widget.DrawableToByte(R.drawable.default_item);
            item.pic3 = null;
            if (!DatabaseHelper.Act.NewItem(item))
                return false;
            //goodItem3
            item.id = "2016-12-03_00:00:00_'" + 0 + "'";
            item.name = "肉3(无价格)";
            item.price = -1;
            item.num = 100;
            item.details = "";
            item.pic_num = 1;
            item.pic2 = null;
            item.pic3 = null;
            if (!DatabaseHelper.Act.NewItem(item))
                return false;
            //goodItem4
            item.id = "2016-12-04_00:00:00_'" + 0 + "'";
            item.name = "肉4(无价格)";
            item.price = -1;
            item.num = 50;
            item.details = "这是系统推出的一个商品\n这是系统推出的一个商品\n这是系统推出的一个商品\n这是系统推出的一个商品\n这是系统推出的一个商品";
            item.pic_num = 3;
            item.pic2 = Widget.DrawableToByte(R.drawable.default_item);
            item.pic3 = Widget.DrawableToByte(R.drawable.default_item);
            if (!DatabaseHelper.Act.NewItem(item))
                return false;
            //getItem1
            GetItem getItem=new GetItem();
            getItem.id = "2016-12-01_00:00:00_'" + 0 + "'";
            getItem.user = "system";
            getItem.class1 = "全部";
            getItem.class2 = "全部";
            getItem.name = "肉1(有价格)";
            getItem.num = 1;
            getItem.unit = "个";
            getItem.price = 100;
            getItem.getterName = "李先生";
            getItem.getterPhone = "18826139918";
            getItem.shop = "数媒冰栈公司";
            getItem.province = "广东省";
            getItem.city = "广州市";
            getItem.district = "番禺区";
            if (!DatabaseHelper.Act.NewItem(getItem))
                return false;
            //getItem2
            getItem.id = "2016-12-02_00:00:00_'" + 0 + "'";
            getItem.name = "肉2(有价格)";
            getItem.unit = "斤";
            getItem.price = 20;
            if (!DatabaseHelper.Act.NewItem(getItem))
                return false;
            //getItem3
            getItem.id = "2016-12-03_00:00:00_'" + 0 + "'";
            getItem.name = "肉3(无价格)";
            getItem.unit = "个";
            getItem.price = -1;
            if (!DatabaseHelper.Act.NewItem(getItem))
                return false;
            //getItem4
            getItem.id = "2016-12-04_00:00:00_'" + 0 + "'";
            getItem.name = "肉4(无价格)";
            getItem.unit = "斤";
            getItem.price = -1;
            if (!DatabaseHelper.Act.NewItem(getItem))
                return false;
        }
        return true;
    }
    private void initView() {
        mainLayoutIndex=(LinearLayout) findViewById(R.id.main_layout_index);
        //tab按钮
        DisplayMetrics outMetrics=new DisplayMetrics();//获取屏幕的宽度
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int screenWidth = outMetrics.widthPixels;
        initIndexItem(0, screenWidth /5,R.id.index_home,R.drawable.index_home_true);
        initIndexItem(1, screenWidth /5,R.id.index_buy,R.drawable.index_buy_false);
        initIndexItem(2, screenWidth /5,R.id.index_follow,R.drawable.index_follow_false);
        initIndexItem(3, screenWidth /5,R.id.index_shop,R.drawable.index_shop_false);
        initIndexItem(4, screenWidth /5,R.id.index_info,R.drawable.index_info_false);
        //窗口
        frameHome=new Home();
        frameBuy=new Buy();
        frameFollow=new Follow();
        frameShop=new Shop();
        frameInfo=new Info();
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(frameHome);
        fragments.add(frameBuy);
        fragments.add(frameFollow);
        fragments.add(frameShop);
        fragments.add(frameInfo);
        //适配器
        FragmentAdapter mAdapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
        mViewPager=(ViewPager) findViewById(R.id.container);
        assert mViewPager != null;
        mViewPager.setOffscreenPageLimit(5);
        if(mViewPager != null) {
            mViewPager.setAdapter(mAdapter);
            mViewPager.addOnPageChangeListener(new TabOnPageChangeListener());
        }
    }
    private void initIndexItem(int num,int width,int ID,int picID){
        LinearLayout indexItemLayout=new LinearLayout(this);
        //图片
        ImageButton itemItemSrc = new ImageButton(this);
        itemItemSrc.setBackgroundResource(picID);
        itemItemSrc.setOnClickListener(new TabOnClickListener(num));
        itemItemSrc.setId(ID);
        LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(48,70);
        indexItemLayout.addView(itemItemSrc,p1);
        //布局
        indexItemLayout.setOnClickListener(new TabOnClickListener(num));
        indexItemLayout.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(width,LinearLayout.LayoutParams.MATCH_PARENT);
        mainLayoutIndex.addView(indexItemLayout,p2);
    }
    //Layout-Tab
    public class TabOnClickListener implements View.OnClickListener{//点击事件
        private int index=0;
        TabOnClickListener(int i){
            index=i;
        }
        public void onClick(View v) {
            if(index!=0 && m_data.m_user==null) {
                if(index!=4)
                    Message("请先登录");
                mViewPager.setCurrentItem(4);
            }
            else if(index==3 && m_data.m_user.m_shop.m_shop.equals("")) {
                frameShop.Turn("NEW");
            }
            else
                mViewPager.setCurrentItem(index);
        }
    }
    public class TabOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int state) {
        }
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){
        }
        @Override
        public void onPageSelected(int position) {
            if(m_data.m_user==null) {
                if(position!=0){
                    if(position!=4)
                        Message("请先登录");
                    mViewPager.setCurrentItem(4);
                    position=4;
                }
            }
            else if(position==3 && m_data.m_user.m_shop.m_shop.equals("")) {
                frameShop.Turn("NEW");
            }

            View view1=findViewById(R.id.index_home);
            View view2=findViewById(R.id.index_buy);
            View view3=findViewById(R.id.index_follow);
            View view4=findViewById(R.id.index_shop);
            View view5=findViewById(R.id.index_info);
            if(view1!=null) view1.setBackgroundResource(R.drawable.index_home_false);
            if(view2!=null) view2.setBackgroundResource(R.drawable.index_buy_false);
            if(view3!=null) view3.setBackgroundResource(R.drawable.index_follow_false);
            if(view4!=null) view4.setBackgroundResource(R.drawable.index_shop_false);
            if(view5!=null) view5.setBackgroundResource(R.drawable.index_info_false);
            switch (position) {
                case 0:
                    if(view1!=null) view1.setBackgroundResource(R.drawable.index_home_true);
                    break;
                case 1:
                    if(view2!=null) view2.setBackgroundResource(R.drawable.index_buy_true);
                    break;
                case 2:
                    if(view3!=null) view3.setBackgroundResource(R.drawable.index_follow_true);
                    break;
                case 3:
                    if(view4!=null) view4.setBackgroundResource(R.drawable.index_shop_true);
                    break;
                case 4:
                    if(view5!=null) view5.setBackgroundResource(R.drawable.index_info_true);
                    break;
            }
        }
    }
    public class FragmentAdapter extends FragmentPagerAdapter {//FragmentPagerAdapter
        private List<Fragment> fragments;
        FragmentAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        public Fragment getItem(int fragment) {
            return fragments.get(fragment);
        }

        public int getCount() {
            return fragments.size();
        }

    }
    //接口
    public void Message(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
