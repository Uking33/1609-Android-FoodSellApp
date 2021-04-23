package com.example.administrator.frozengoods;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class ItemDetails extends AppCompatActivity {
    final ItemDetails m_parent = this;
    private Data m_data;
    private View m_back_over;
    private LinearLayout m_itemLayout;
    private GoodItem m_item;
    private ShopItem m_shop;
    private boolean isFollowShop,isFollowItem;
    private ArrayList<GoodItem> m_list;
    String m_id;
    Button m_shopFollow;
    int m_index;
    //Create
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details);
        Intent intent = getIntent();
        if (intent != null) {
            m_data = intent.getParcelableExtra("data");
            m_id = intent.getStringExtra("id");
            m_item= intent.getParcelableExtra("item");
            m_item.getPic();

            DatabaseHelper.Act.GetGoodItemPic(new ArrayList<GoodItem>(){{add(m_item);}} ," where id=? LIMIT 1", new String[]{m_id});
            m_shop=DatabaseHelper.Act.GetShopItem(" where user=? LIMIT 1", new String[]{m_item.user});
            isFollowItem=DatabaseHelper.Act.IsFollow(m_data.m_user.m_user,m_item.id,"goodItem");
            isFollowShop=DatabaseHelper.Act.IsFollow(m_data.m_user.m_user,m_item.user,"shop");
            m_list=null;
        } else {
            finish();
            return;
        }
        m_itemLayout=(LinearLayout)findViewById(R.id.layout_content);
        FrameLayout m_back = (FrameLayout) findViewById(R.id.btn_back);
        m_back_over = findViewById(R.id.btn_back_over);
        assert m_back_over != null;
        m_back_over.getBackground().setAlpha(0);
        assert m_back != null;
        m_back.setOnTouchListener(new View.OnTouchListener() {
            boolean click = false;

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        m_back_over.getBackground().setAlpha(51);
                        click = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                                event.getY() > 0 && event.getY() < v.getHeight()) {
                            onBackPressed();
                        }
                        m_back_over.getBackground().setAlpha(0);
                        break;
                }
                return true;
            }
        });
        initView();
    }
    @Override protected void onDestroy(){
        m_data=null;
        m_back_over=null;
        m_itemLayout=null;
        m_item=null;
        m_shop=null;
        m_list=null;
        super.onDestroy();
        setContentView(R.layout.view_null);
    }

    //Turn
    @Override public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("chats", m_data.m_chatList);
        intent.putExtra("isFollow",isFollowItem);
        if(m_data.m_user!=null && String.valueOf(m_data.m_user.m_id).equals(m_item.id)) {
            intent.putExtra("m_followed", m_shop.m_followed);
            intent.putExtra("m_visited", m_shop.m_visited);
        }
        else{
            intent.putExtra("m_followed", 0);
            intent.putExtra("m_visited", 0);
        }
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data==null)
            return ;
        if(resultCode!=RESULT_OK) {
            return;
        }
        switch ( requestCode ) {
            case 1: {//news
                m_data.m_chatList = data.getParcelableArrayListExtra("chats");
                ImageView red = (ImageView) findViewById(R.id.item_news_point);
                if (m_data.GetUnReadNum() > 0) {
                    assert red != null;
                    red.setVisibility(View.VISIBLE);
                } else {
                    assert red != null;
                    red.setVisibility(View.GONE);
                }
                if(!isFollowShop)
                    m_shopFollow.setBackgroundResource(R.drawable.icon_item_guanzhu);
                else
                    m_shopFollow.setBackgroundResource(R.drawable.icon_item_yiguanzhu);
                break;
            }
            case 2: {//shop
                m_data.m_chatList = data.getParcelableArrayListExtra("chats");
                String id = data.getStringExtra("id");
                boolean is=data.getBooleanExtra("isFollow",isFollowShop);
                if(!is && isFollowShop)
                    m_shop.m_followed--;
                if(is && !isFollowShop)
                    m_shop.m_followed++;
                isFollowShop=is;
                if (!id.isEmpty()) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            ScrollView scroll = (ScrollView) findViewById(R.id.item_scroll);
                            assert scroll != null;
                            scroll.fullScroll(ScrollView.FOCUS_UP);
                        }
                    };
                    runnable.run();
                    m_itemLayout.removeAllViews();
                    m_item = DatabaseHelper.Act.GetGoodItem(" where id=? LIMIT 1", new String[]{id}).get(0);
                    DatabaseHelper.Act.GetGoodItemPic(new ArrayList<GoodItem>(){{add(m_item);}}," where id=? LIMIT 1", new String[]{id});
                    isFollowItem=DatabaseHelper.Act.IsFollow(m_data.m_user.m_user,m_item.id,"goodItem");
                    m_list=null;
                    initPic();
                    initItem();
                    initShop();
                    initComment();
                    initLike();
                }
                if(!isFollowShop)
                    m_shopFollow.setBackgroundResource(R.drawable.icon_item_guanzhu);
                else
                    m_shopFollow.setBackgroundResource(R.drawable.icon_item_yiguanzhu);
                ImageView red = (ImageView) findViewById(R.id.item_news_point);
                if (m_data.GetUnReadNum() > 0) {
                    assert red != null;
                    red.setVisibility(View.VISIBLE);
                } else {
                    assert red != null;
                    red.setVisibility(View.GONE);
                }
                break;
            }
            case 3://shopChat
                ChatItems chat=data.getParcelableExtra("chat");
                m_data.m_chatList.set(m_index,chat);
                break;
            default:
                break;
        }
    }
    void Turn(String label) {
        switch (label){
            case "FollowItem":
                if(!isFollowItem) {
                    if (DatabaseHelper.Act.AddFollow(m_data.m_user.m_user,m_item.id, "goodItem"))
                        isFollowItem = true;
                }
                else {
                    if (DatabaseHelper.Act.DelFollow(m_data.m_user.m_user,m_item.id, "goodItem"))
                        isFollowItem = false;
                }
                break;
            case "FollowShop":
                if(!isFollowShop) {
                    if (DatabaseHelper.Act.AddFollow(m_data.m_user.m_user,m_item.user, "shop") && DatabaseHelper.Act.UpdateShopState(m_parent.m_data.m_user,m_item.user,"followed",1)) {
                        isFollowShop = true;
                        m_shop.m_followed++;
                    }
                }
                else {
                    if (DatabaseHelper.Act.DelFollow(m_data.m_user.m_user,m_item.user, "shop") && DatabaseHelper.Act.UpdateShopState(m_parent.m_data.m_user,m_item.user,"followed",-1)) {
                        isFollowShop = false;
                        m_shop.m_followed--;
                    }
                }
                break;
            case "News":
                m_index=m_data.NewChat(m_item.user);
                if(m_index>=0) {
                    ChatItems chats2=m_data.m_chatList.get(m_index);
                    DatabaseHelper.Act.UpdateData("chat_" + m_data.m_user.m_user, "state", "1", "user=? and state=?", new String[]{chats2.toUser, "0"});
                    m_data.m_chatList.get(m_index).SetRead();
                    Intent intent2=new Intent(ItemDetails.this,InfoChat.class);
                    intent2.putExtra("id", "ITEM_DETAILS");
                    intent2.putExtra("name",chats2.toName);
                    intent2.putExtra("chat", chats2);
                    intent2.putExtra("toHead",DatabaseHelper.Act.GetHead(chats2.toUser));
                    intent2.putExtra("fromHead",DatabaseHelper.Act.GetHead(m_data.m_user.m_user));
                    startActivityForResult(intent2, 3);
                }
                break;
            case "Shop":
                Intent intent1 = new Intent(this,ShopOther.class);
                m_shop.m_visited++;
                intent1.putExtra("data", m_data);
                intent1.putExtra("shop", m_shop);
                intent1.putExtra("id","ITEM_DETAILS");
                intent1.putExtra("user",m_item.user);
                intent1.putExtra("isFollowShop",isFollowShop);
                startActivityForResult(intent1,2);
                break;
            default:
                Intent intent = new Intent(this,UnKnow.class);
                startActivity(intent);
                break;
        }
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    void Turn(String label, int index) {
        switch (label){
            case "可能喜欢":
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        ScrollView scroll=(ScrollView)findViewById(R.id.item_scroll);
                        assert scroll != null;
                        scroll.fullScroll(ScrollView.FOCUS_UP);
                    }
                };
                runnable.run();
                m_itemLayout.removeAllViews();
                m_item=DatabaseHelper.Act.GetGoodItem(" where id=? LIMIT 1", new String[]{m_list.get(index).id}).get(0);
                DatabaseHelper.Act.GetGoodItemPic(new ArrayList<GoodItem>(){{add(m_item);}}," where id=? LIMIT 1", new String[]{m_list.get(index).id});
                isFollowItem=DatabaseHelper.Act.IsFollow(m_data.m_user.m_user,m_item.id,"goodItem");
                m_list=null;
                initPic();
                initItem();
                initShop();
                initComment();
                initLike();
                break;
            default:
                Intent intent = new Intent(this,UnKnow.class);
                startActivity(intent);
                break;
        }
    }

    //Layout
    void initView(){
        //news
        ImageView red=(ImageView)findViewById(R.id.item_news_point);
        if(m_data.GetUnReadNum()>0) {
            assert red != null;
            red.setVisibility(View.VISIBLE);
        }
        else {
            assert red != null;
            red.setVisibility(View.GONE);
        }
        FrameLayout f=(FrameLayout)findViewById(R.id.item_news);
        assert f != null;
        f.setOnTouchListener(new View.OnTouchListener() {
            boolean click = false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        click = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                                event.getY() > 0 && event.getY() < v.getHeight()) {
                            if (m_data.m_user != null){
                                Intent intent = new Intent(ItemDetails.this, InfoNews.class);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("data", m_data);
                                bundle.putString("id", "ITEM_DETAILS");
                                intent.putExtras(bundle);
                                startActivityForResult(intent, 1);
                            }
                        }
                        break;
                }
                return true;
            }
        });

        initPic();
        initItem();
        initShop();
        initComment();
        initLike();
        initBottom();
    }
    //Layout-Pic
    private ArrayList<View> picList;
    private ImageView[] picImageViews;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initPic() {
        //---绑定控件initView---
        ViewPager picViewPager = (ViewPager) findViewById(R.id.item_details_pic_viewPager);
        ViewGroup picGroup = (ViewGroup) findViewById(R.id.item_details_pic_viewGroup);
        assert picGroup != null;
        picGroup.removeAllViews();
        assert picViewPager != null;
        picViewPager.removeAllViewsInLayout();
        //添加到集合中
        picList = new ArrayList<>();
        View page1 = new View(this);
        page1.setBackground(Widget.ByteToDrawable(m_item.pic1));
        picList.add(page1);
        if(m_item.pic_num>=2){
            View page2 = new View(this);
            page2.setBackground(Widget.ByteToDrawable(m_item.pic2));
            picList.add(page2);
        }
        if(m_item.pic_num>=3){
            View page3 = new View(this);
            page3.setBackground(Widget.ByteToDrawable(m_item.pic3));
            picList.add(page3);
        }
        PagerAdapter picAdapter = new PagerAdapter() {
            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }

            @Override
            public int getCount() {
                return picList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = picList.get(position);
                container.addView(view);
                return view;
            }
        };
        picImageViews = new ImageView[picList.size()];
        for (int i = 0; i < picImageViews.length; i++) {
            ImageView picImageView = new ImageView(m_parent);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            picImageView.setLayoutParams(lp);
            picImageViews[i] = picImageView;
            if (i == 0) {
                picImageViews[i].setBackgroundResource(R.drawable.icon_page_focused);
            } else {
                picImageViews[i].setBackgroundResource(R.drawable.icon_page_unfocused);
            }
            LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(12,12);
            l.bottomMargin=17;
            l.rightMargin=8;
            picGroup.addView(picImageViews[i],l);
        }
        picViewPager.setAdapter(picAdapter);
        picViewPager.addOnPageChangeListener(new ItemDetails.GuidePageChangeListener());
    }
    public class GuidePageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
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
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
    //Layout-Others
    void initItem(){
        FrameLayout f1=new FrameLayout(this);
        FrameLayout f2=new FrameLayout(this);
        FrameLayout f3=new FrameLayout(this);
        //text1
        TextView text1=new TextView(this);
        text1.setText(m_item.name);
        text1.setIncludeFontPadding(false);
        text1.setTextColor(ContextCompat.getColor(this,R.color.tengray));
        text1.setTextSize(TypedValue.COMPLEX_UNIT_PX,36);
        FrameLayout.LayoutParams p11 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        p11.gravity= Gravity.CENTER_VERTICAL;
        f1.addView(text1,p11);
        //btn1
        Button btn1=new Button(this);
        if(!isFollowItem)
            btn1.setBackgroundResource(R.drawable.icon_item_guanzhu);
        else
            btn1.setBackgroundResource(R.drawable.icon_item_yiguanzhu);
        FrameLayout.LayoutParams p12 = new FrameLayout.LayoutParams(120,41);
        p12.gravity= Gravity.CENTER_VERTICAL|Gravity.END;
        f1.addView(btn1,p12);
        btn1.setOnTouchListener(new View.OnTouchListener() {
            boolean click = false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if(!isFollowItem)
                            v.setBackgroundResource(R.drawable.icon_item_guanzhu_press);
                        else
                            v.setBackgroundResource(R.drawable.icon_item_yiguanzhu_press);
                        click = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                                event.getY() > 0 && event.getY() < v.getHeight()) {
                            Turn("FollowItem");
                        }
                        click = false;
                        if(!isFollowItem)
                            v.setBackgroundResource(R.drawable.icon_item_guanzhu);
                        else
                            v.setBackgroundResource(R.drawable.icon_item_yiguanzhu);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        click = false;
                        if(!isFollowItem)
                            v.setBackgroundResource(R.drawable.icon_item_guanzhu);
                        else
                            v.setBackgroundResource(R.drawable.icon_item_yiguanzhu);
                        break;
                }
                return true;
            }
        });
        //line1
        View line1=new View(this);
        line1.setBackgroundResource(R.color.eghitygray);
        FrameLayout.LayoutParams p13 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1);
        p13.gravity= Gravity.BOTTOM;
        f1.addView(line1,p13);
        //layout1
        LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,112);
        p1.rightMargin=45;
        p1.leftMargin=45;
        m_itemLayout.addView(f1,p1);

        //text2
        TextView text2=new TextView(this);
        if(m_item.price>0) {
            String str="价格："+m_item.price;
            text2.setText(str);
        }
        else
            text2.setText("价格：面谈");
        text2.setIncludeFontPadding(false);
        text2.setTextColor(Color.parseColor("#f98050"));
        text2.setTextSize(TypedValue.COMPLEX_UNIT_PX,30);
        FrameLayout.LayoutParams p21 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        p21.gravity= Gravity.CENTER_VERTICAL;
        f2.addView(text2,p21);
        //line2
        View line2=new View(this);
        line2.setBackgroundResource(R.color.eghitygray);
        FrameLayout.LayoutParams p22 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1);
        p22.gravity= Gravity.BOTTOM;
        f2.addView(line2,p22);
        //layout2
        LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,93);
        p2.rightMargin=45;
        p2.leftMargin=45;
        m_itemLayout.addView(f2,p2);

        //text3
        TextView text3=new TextView(this);
        text3.setText("商品介绍");
        text3.setIncludeFontPadding(false);
        text3.setTextColor(ContextCompat.getColor(this,R.color.tengray));
        text3.setTextSize(TypedValue.COMPLEX_UNIT_PX,30);
        FrameLayout.LayoutParams p31 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        p31.topMargin=27;
        f3.addView(text3,p31);
        //ad
        TextView text31=new TextView(this);
        if(!m_item.details.equals(""))
            text31.setText(m_item.details);
        else {
            text31.setText("- 暂无商品介绍 -");
        }
        text31.setIncludeFontPadding(false);
        text31.setTextColor(ContextCompat.getColor(this,R.color.sixtygray));
        text31.setTextSize(TypedValue.COMPLEX_UNIT_PX,24);
        FrameLayout.LayoutParams p32;
        if(!m_item.details.equals("")) {
            p32 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        else{
            p32= new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            p32.gravity=Gravity.CENTER_HORIZONTAL;
        }
        p32.topMargin=91;
        p32.bottomMargin=43;
        f3.addView(text31,p32);
        //line3
        View line3=new View(this);
        line3.setBackgroundResource(R.color.eghitygray);
        FrameLayout.LayoutParams p33 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1);
        p33.gravity= Gravity.BOTTOM;
        f3.addView(line3,p33);
        //layout3
        LinearLayout.LayoutParams p3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        p3.rightMargin=45;
        p3.leftMargin=45;
        m_itemLayout.addView(f3,p3);
    }
    void initShop(){
        FrameLayout layout=new FrameLayout(this);
        FrameLayout layoutFollow=new FrameLayout(this);

        layout.setOnTouchListener(new View.OnTouchListener() {
            boolean click = false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundResource(R.color.eghitygray);
                        click = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                                event.getY() > 0 && event.getY() < v.getHeight()) {
                            Turn("Shop");
                        }
                        click = false;
                        v.setBackgroundResource(R.color.white);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        click = false;
                        v.setBackgroundResource(R.color.white);
                        break;
                }
                return true;
            }
        });
        //text1
        TextView text1=new TextView(this);
        text1.setText("商家详情");
        text1.setIncludeFontPadding(false);
        text1.setTextColor(ContextCompat.getColor(this,R.color.tengray));
        text1.setTextSize(TypedValue.COMPLEX_UNIT_PX,30);
        FrameLayout.LayoutParams p1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        p1.topMargin=27;
        p1.leftMargin=45;
        layout.addView(text1,p1);
        //text2
        TextView text2=new TextView(this);
        text2.setText("更多详情 >");
        text2.setIncludeFontPadding(false);
        text2.setTextColor(ContextCompat.getColor(this,R.color.eghitygray));
        text2.setTextSize(TypedValue.COMPLEX_UNIT_PX,22);
        FrameLayout.LayoutParams p2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        p2.gravity=Gravity.END;
        p2.topMargin=35;
        p2.rightMargin=45;
        layout.addView(text2,p2);
        //logo
        ImageView logo=new ImageView(this);
        logo.setImageDrawable(Widget.ByteToDrawable(m_shop.m_logo));
        FrameLayout.LayoutParams P1 = new FrameLayout.LayoutParams(138,138);
        layoutFollow.addView(logo,P1);
        //title1
        TextView title1=new TextView(this);
        title1.setText(m_shop.m_shop);
        title1.setIncludeFontPadding(false);
        title1.setTextColor(ContextCompat.getColor(this,R.color.tengray));
        title1.setTextSize(TypedValue.COMPLEX_UNIT_PX,36);
        FrameLayout.LayoutParams P2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        P2.leftMargin=155;
        P2.topMargin=3;
        layoutFollow.addView(title1,P2);
        //title2
        TextView title2=new TextView(this);
        title2.setText(m_shop.m_shop);
        title2.setIncludeFontPadding(false);
        title2.setTextColor(ContextCompat.getColor(this,R.color.tengray));
        title2.setTextSize(TypedValue.COMPLEX_UNIT_PX,24);
        FrameLayout.LayoutParams P3 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        P3.leftMargin=155;
        P3.topMargin=60;
        layoutFollow.addView(title2,P3);
        //title3
        TextView title3=new TextView(this);
        title3.setText(m_shop.m_shop);
        title3.setIncludeFontPadding(false);
        title3.setTextColor(ContextCompat.getColor(this,R.color.tengray));
        title3.setTextSize(TypedValue.COMPLEX_UNIT_PX,24);
        FrameLayout.LayoutParams P4 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        P4.leftMargin=155;
        P4.topMargin=110;
        layoutFollow.addView(title3,P4);

        //news
        final Button btnNews=new Button(this);
        btnNews.setBackgroundResource(R.drawable.icon_item_chat);
        btnNews.setOnTouchListener(new View.OnTouchListener() {
            boolean click=false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        btnNews.setBackgroundResource(R.drawable.icon_item_chat_press);
                        click=true;
                        break;
                    case MotionEvent.ACTION_UP:
                        btnNews.setBackgroundResource(R.drawable.icon_item_chat);
                        if(click && event.getX()>0 && event.getX()<v.getWidth() &&
                                event.getY()>0 && event.getY()<v.getHeight()) {
                                Turn("News");
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if(click) {
                            click = false;
                            btnNews.setBackgroundResource(R.drawable.icon_item_chat);
                        }
                        break;
                }
                return true;
            }
        });
        FrameLayout.LayoutParams Params7 = new FrameLayout.LayoutParams(120,41);
        Params7.topMargin=0;
        Params7.gravity=Gravity.END;
        layoutFollow.addView(btnNews,Params7);
        //btnPhone
        final String phone=m_shop.m_phone;
        final Button btnPhone=new Button(this);
        btnPhone.setBackgroundResource(R.drawable.icon_item_phone);
        btnPhone.setOnTouchListener(new View.OnTouchListener() {
            boolean click=false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        btnPhone.setBackgroundResource(R.drawable.icon_item_phone_press);
                        click=true;
                        break;
                    case MotionEvent.ACTION_UP:
                        btnPhone.setBackgroundResource(R.drawable.icon_item_phone);
                        if(click && event.getX()>0 && event.getX()<v.getWidth() &&
                                event.getY()>0 && event.getY()<v.getHeight()) {
                            UPhoneDialog m_share_dialog = new UPhoneDialog(m_parent,new UPhoneDialog.OnCustomDialogListener() {
                                @Override
                                public void back(String phone) {
                                    Intent intent=new Intent();
                                    intent.setAction(Intent.ACTION_VIEW);
                                    Uri url=Uri.parse("tel:"+ phone);
                                    intent.setData(url);
                                    startActivity(intent);
                                }
                            },phone);
                            m_share_dialog.show();
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if(click) {
                            click = false;
                            btnPhone.setBackgroundResource(R.drawable.icon_item_phone);
                        }
                        break;
                }
                return true;
            }
        });
        FrameLayout.LayoutParams Params8 = new FrameLayout.LayoutParams(120,41);
        Params8.topMargin=48;
        Params8.gravity=Gravity.END;
        layoutFollow.addView(btnPhone,Params8);
        //follow
        m_shopFollow =new Button(this);
        if(!isFollowShop)
            m_shopFollow.setBackgroundResource(R.drawable.icon_item_guanzhu);
        else
            m_shopFollow.setBackgroundResource(R.drawable.icon_item_yiguanzhu);
        m_shopFollow.setOnTouchListener(new View.OnTouchListener() {
            boolean click=false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if(!isFollowShop)
                            m_shopFollow.setBackgroundResource(R.drawable.icon_item_guanzhu_press);
                        else
                            m_shopFollow.setBackgroundResource(R.drawable.icon_item_yiguanzhu_press);
                        click=true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if(click && event.getX()>0 && event.getX()<v.getWidth() &&
                                event.getY()>0 && event.getY()<v.getHeight()) {
                            Turn("FollowShop");
                        }
                        if(!isFollowShop)
                            m_shopFollow.setBackgroundResource(R.drawable.icon_item_guanzhu);
                        else
                            m_shopFollow.setBackgroundResource(R.drawable.icon_item_yiguanzhu);
                        click = false;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if(click) {
                            click = false;
                            if(!isFollowShop)
                                m_shopFollow.setBackgroundResource(R.drawable.icon_item_guanzhu);
                            else
                                m_shopFollow.setBackgroundResource(R.drawable.icon_item_yiguanzhu);
                        }
                        break;
                }
                return true;
            }
        });
        FrameLayout.LayoutParams Params9 = new FrameLayout.LayoutParams(120,41);
        Params9.gravity=Gravity.END;
        Params9.topMargin=96;
        layoutFollow.addView(m_shopFollow,Params9);
        //line
        View line=new View(m_parent);
        line.setBackgroundResource(R.color.eghitygray);
        //layout
        FrameLayout.LayoutParams p3 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,138);
        p3.topMargin=79;
        p3.rightMargin=45;
        p3.leftMargin=45;
        layout.addView(layoutFollow,p3);
        FrameLayout.LayoutParams p4 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1);
        p4.gravity=Gravity.BOTTOM;
        p4.rightMargin=45;
        p4.leftMargin=45;
        layout.addView(line,p4);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,260);
        m_itemLayout.addView(layout,p);
    }
    void initComment(){
        FrameLayout layout=new FrameLayout(this);
        //title1
        TextView title1=new TextView(this);
        title1.setText("评价");
        title1.setIncludeFontPadding(false);
        title1.setTextColor(ContextCompat.getColor(this,R.color.tengray));
        title1.setTextSize(TypedValue.COMPLEX_UNIT_PX,30);
        FrameLayout.LayoutParams p1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        p1.topMargin=27;
        layout.addView(title1,p1);
        //title2
        TextView title2=new TextView(this);
        title2.setText("好评0% >");
        title2.setIncludeFontPadding(false);
        title2.setTextColor(ContextCompat.getColor(this,R.color.eghitygray));
        title2.setTextSize(TypedValue.COMPLEX_UNIT_PX,22);
        FrameLayout.LayoutParams p2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        p2.gravity=Gravity.END;
        p2.topMargin=35;
        layout.addView(title2,p2);
        //title3
        TextView title3=new TextView(this);
        title3.setText("- 暂无评论 -");
        title3.setIncludeFontPadding(false);
        title3.setTextColor(ContextCompat.getColor(this,R.color.eghitygray));
        title3.setTextSize(TypedValue.COMPLEX_UNIT_PX,24);
        FrameLayout.LayoutParams p3 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        p3.gravity=Gravity.CENTER;
        layout.addView(title3,p3);
        //line
        View line=new View(m_parent);
        line.setBackgroundResource(R.color.eghitygray);
        FrameLayout.LayoutParams p4 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1);
        p4.gravity=Gravity.BOTTOM;
        layout.addView(line,p4);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,260);
        p.rightMargin=45;
        p.leftMargin=45;
        m_itemLayout.addView(layout,p);

    }
    void initLike(){
        final String title="可能喜欢";
        FrameLayout layout=new FrameLayout(this);
        LinearLayout.LayoutParams P = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,300);
        P.leftMargin=45;
        P.rightMargin=45;
        m_itemLayout.addView(layout,P);
        m_list = DatabaseHelper.Act.GetGoodItem("where user=? and state=? and (not id=?) LIMIT 3", new String[]{m_item.user, "On",m_item.id});
        DatabaseHelper.Act.GetGoodItemPic(m_list,"where user=? and state=? and (not id=?) LIMIT 3", new String[]{m_item.user, "On",m_item.id});
        FrameLayout picListText=new FrameLayout(m_parent);
        FrameLayout picListItems=new FrameLayout(m_parent);
        //标题
        TextView titleText=new TextView(m_parent);
        titleText.setIncludeFontPadding(false);
        titleText.setText(title);
        titleText.setTextColor(ContextCompat.getColor(m_parent,R.color.tengray));
        titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX,30);
        FrameLayout.LayoutParams Params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        picListText.addView(titleText,Params2);
        //Item
        if(m_list.size()>0) {
            for (int i = 0; i < 3; i++) {
                LinearLayout picListItem = new LinearLayout(m_parent);
                picListItem.setOrientation(LinearLayout.VERTICAL);
                if (i + 1 <= m_list.size()) {
                    ImageView picListItemBtn = new ImageView(m_parent);
                    picListItemBtn.setImageDrawable(Widget.ByteToDrawable(m_list.get(i).pic1));
                    picListItemBtn.setScaleType(ImageView.ScaleType.FIT_XY);
                    picListItem.addView(picListItemBtn, 155, 155);
                    final int index = i;
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
                                        Turn(title, index);
                                    }
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

                    TextView picListItemText = new TextView(m_parent);
                    picListItemText.setIncludeFontPadding(false);
                    if (m_list.get(i).name.length() <= 7)
                        picListItemText.setText(m_list.get(i).name);
                    else {
                        String str = m_list.get(i).name;
                        str = str.substring(0, 6) + "...";
                        picListItemText.setText(str);
                    }
                    picListItemText.setTextColor(ContextCompat.getColor(m_parent, R.color.twentygray));
                    picListItemText.setTextSize(TypedValue.COMPLEX_UNIT_PX, 18);
                    FrameLayout.LayoutParams p1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    p1.topMargin = 9;
                    picListItem.addView(picListItemText, p1);

                    TextView picListItemPrice = new TextView(m_parent);
                    picListItemPrice.setIncludeFontPadding(false);
                    if(m_list.get(i).price>0)
                        picListItemPrice.setText("￥" + m_list.get(i).price + "/"+m_list.get(i).unit);
                    else
                        picListItemPrice.setText("面谈");
                    picListItemPrice.setTextColor(ContextCompat.getColor(m_parent, R.color.priceorange));
                    picListItemPrice.setTextSize(TypedValue.COMPLEX_UNIT_PX, 18);
                    FrameLayout.LayoutParams p2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    p2.topMargin = 7;
                    picListItem.addView(picListItemPrice, p2);
                }
                FrameLayout.LayoutParams Params5 = new FrameLayout.LayoutParams(155, ViewGroup.LayoutParams.MATCH_PARENT);
                Params5.leftMargin = 32 + i * 220;
                picListItems.addView(picListItem, Params5);
            }
        }
        else{
            TextView t1 = new TextView(m_parent);
            t1.setIncludeFontPadding(false);
            t1.setText("- 暂无商品信息 -");
            t1.setTextColor(ContextCompat.getColor(m_parent, R.color.seventygray));
            t1.setTextSize(TypedValue.COMPLEX_UNIT_PX, 25);
            t1.setGravity(Gravity.CENTER);
            FrameLayout.LayoutParams p1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            p1.gravity=Gravity.CENTER;
            picListItems.addView(t1, p1);
        }
        //Layout
        FrameLayout.LayoutParams Params6 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params6.gravity =Gravity.TOP;
        Params6.topMargin=24;
        layout.addView(picListText,Params6);
        FrameLayout.LayoutParams Params7 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        Params7.gravity =Gravity.TOP;
        Params7.topMargin=75;
        layout.addView(picListItems,Params7);
        //分割线
        View line=new View(m_parent);
        line.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.nintygray));
        FrameLayout.LayoutParams Params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1);
        Params1.gravity =Gravity.BOTTOM;
        layout.addView(line,Params1);
    }
    void initBottom(){
        TextView btn1=(TextView)findViewById(R.id.item_details_bottom_shop);
        TextView btn2=(TextView)findViewById(R.id.item_details_bottom_share);
        ImageView btn3=(ImageView)findViewById(R.id.item_details_bottom_phone);
        ImageView btn4=(ImageView)findViewById(R.id.item_details_bottom_chat);
        assert btn1 != null;
        btn1.setBackgroundResource(R.color.white);
        assert btn2 != null;
        btn2.setBackgroundResource(R.color.white);
        assert btn3 != null;
        btn3.setBackgroundResource(R.drawable.icon_item_chat2);
        assert btn4 != null;
        btn4.setBackgroundResource(R.drawable.icon_item_phone2);
        btn1.setOnTouchListener(new View.OnTouchListener() {
            boolean click = false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundResource(R.color.eghitygray);
                        click = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                                event.getY() > 0 && event.getY() < v.getHeight()) {
                            Turn("Shop");
                        }
                        click = false;
                        v.setBackgroundResource(R.color.white);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        click = false;
                        v.setBackgroundResource(R.color.white);
                        break;
                }
                return true;
            }
        });
        btn2.setOnTouchListener(new View.OnTouchListener() {
            boolean click = false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundResource(R.color.eghitygray);
                        click = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                                event.getY() > 0 && event.getY() < v.getHeight()) {
                            UShareDialog m_share_dialog = new UShareDialog(m_parent,new UShareDialog.OnCustomDialogListener() {
                                @Override
                                public void back(String choose) {
                                }
                            });
                            m_share_dialog.show();
                        }
                        click = false;
                        v.setBackgroundResource(R.color.white);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        click = false;
                        v.setBackgroundResource(R.color.white);
                        break;
                }
                return true;
            }
        });
        btn3.setOnTouchListener(new View.OnTouchListener() {
            boolean click = false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundResource(R.drawable.icon_item_chat2_press);
                        click = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                                event.getY() > 0 && event.getY() < v.getHeight()) {
                            Turn("News");
                        }
                        click = false;
                        v.setBackgroundResource(R.drawable.icon_item_chat2);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        click = false;
                        v.setBackgroundResource(R.drawable.icon_item_chat2);
                        break;
                }
                return true;
            }
        });
        btn4.setOnTouchListener(new View.OnTouchListener() {
            boolean click = false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundResource(R.drawable.icon_item_phone2_press);
                        click = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                                event.getY() > 0 && event.getY() < v.getHeight()) {

                            UPhoneDialog m_share_dialog = new UPhoneDialog(m_parent,new UPhoneDialog.OnCustomDialogListener() {
                                @Override
                                public void back(String phone) {
                                    Intent intent=new Intent();
                                    intent.setAction(Intent.ACTION_VIEW);
                                    Uri url=Uri.parse("tel:"+ phone);
                                    intent.setData(url);
                                    startActivity(intent);
                                }
                            },m_shop.m_phone);
                            m_share_dialog.show();
                        }
                        click = false;
                        v.setBackgroundResource(R.drawable.icon_item_phone2);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        click = false;
                        v.setBackgroundResource(R.drawable.icon_item_phone2);
                        break;
                }
                return true;
            }
        });
    }
    void Message(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}