package com.example.administrator.frozengoods;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Info  extends Fragment {
    private View m_this;
    private MainActivity m_parent;
    //Create
    @Override  public void onAttach(Context context){
        m_parent=(MainActivity) context;
        super.onAttach(context);
    }
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_this = inflater.inflate(R.layout.info, container, false);
        initView();
        return m_this;
    }

    //Turn
    public void Turn(String title){
        Intent intent;
        if(m_parent.m_data.m_user==null){
            switch (title) {
                case "反馈意见":
                    m_parent.Turn(InfoAdvice.class, "INFO_ADVICE");
                    break;
                case "关于":
                    m_parent.Turn(InfoAbout.class,"INFO_ABOUT");
                    break;
                default:
                    intent=m_parent.Turn1(InfoUser.class, "INFO_LOGIN");
                    m_parent.Turn2(intent,50);
                    break;
            }
        }
        else {
            switch (title){
                case "修改密码":
                    intent=m_parent.Turn1(InfoUser.class, "INFO_PASSWORDS");
                    String str=m_parent.m_data.m_user.m_user;
                    intent.putExtra("user",str);
                    m_parent.Turn2(intent,51);
                    break;
                case "个人信息":
                case "头部":
                    intent=m_parent.Turn1(InfoDetails.class, "INFO_DETAILS");
                    intent.putExtra("user",m_parent.m_data.m_user);
                    m_parent.Turn2(intent,52);
                    break;
                case "我的关注":
                    m_parent.mViewPager.setCurrentItem(2);
                    break;
                case "我的消息":
                    intent=m_parent.Turn1(InfoNews.class, "INFO_NEWS");
                    m_parent.m_data.m_user.clearPic();
                    intent.putExtra("data",m_parent.m_data);
                    m_parent.Turn2(intent,53);
                    break;
                case "反馈意见":
                    m_parent.Turn(InfoAdvice.class,"INFO_ADVICE");
                    break;
                case "关于":
                    m_parent.Turn(InfoAbout.class,"INFO_ABOUT");
                    break;
                case "登录":
                case "注销":
                    m_parent.m_data.Logout();
                    m_parent.Logout();
                    m_parent.Message("帐号已注销");
                    break;
            }
        }
    }

    //Update
    public void Login(){
        ((FrameLayout)m_this.findViewById(R.id.info_layout_top)).removeAllViews();
        initHead((FrameLayout) m_this.findViewById(R.id.info_layout_top), m_parent.m_data.GetMine());
        ((TextView)m_this.findViewById(R.id.btn_info_log)).setText("注销");
    }
    public void Logout(){
        ((FrameLayout)m_this.findViewById(R.id.info_layout_top)).removeAllViews();
        initHead((FrameLayout) m_this.findViewById(R.id.info_layout_top), m_parent.m_data.GetMine());
        ((TextView)m_this.findViewById(R.id.btn_info_log)).setText("登录");
    }
    public void Update(){
        ((FrameLayout)m_this.findViewById(R.id.info_layout_top)).removeAllViews();
        initHead((FrameLayout) m_this.findViewById(R.id.info_layout_top), m_parent.m_data.m_user);
    }
    //Layout
    private void initView(){
        ((FrameLayout)m_this.findViewById(R.id.info_layout_top)).removeAllViews();
        ((LinearLayout)m_this.findViewById(R.id.info_layout_content)).removeAllViews();
        ((LinearLayout)m_this.findViewById(R.id.info_layout_others)).removeAllViews();

        initHead((FrameLayout) m_this.findViewById(R.id.info_layout_top), m_parent.m_data.GetMine());

        if(m_parent.m_data.m_user==null){
            initTextCenter(m_this, R.id.info_layout_content, R.id.btn_info_details, "个人信息", 85, 30);
            initTextCenter(m_this, R.id.info_layout_content, R.id.btn_info_passwords, "修改密码", 85, 30);
            initTextCenter(m_this, R.id.info_layout_content, R.id.btn_info_follow, "我的关注", 85, 30);
            initTextCenter(m_this, R.id.info_layout_content, R.id.btn_info_news, "我的消息", 85, 30);

            initTextCenter(m_this, R.id.info_layout_others, R.id.btn_info_advice, "反馈意见", 85, 30);
            initTextCenter(m_this, R.id.info_layout_others, R.id.btn_info_about, "关于", 85, 30);
            initTextCenter(m_this, R.id.info_layout_others, R.id.btn_info_log, "登录", 85, 30);
        }
        else {
            initTextCenter(m_this, R.id.info_layout_content, R.id.btn_info_details, "个人信息", 85, 30);
            initTextCenter(m_this, R.id.info_layout_content, R.id.btn_info_passwords, "修改密码", 85, 30);
            initTextCenter(m_this, R.id.info_layout_content, R.id.btn_info_follow, "我的关注", 85, 30);
            initTextCenter(m_this, R.id.info_layout_content, R.id.btn_info_news, "我的消息", 85, 30);

            initTextCenter(m_this, R.id.info_layout_others, R.id.btn_info_advice, "反馈意见", 85, 30);
            initTextCenter(m_this, R.id.info_layout_others, R.id.btn_info_about, "关于", 85, 30);
            initTextCenter(m_this, R.id.info_layout_others, R.id.btn_info_log, "注销", 85, 30);
        }
    }
    //Item
    private void initTextCenter(View m_this,int itemID,final int id,final  String title, int height, int size){//选项
        LinearLayout itemLayout = (LinearLayout)m_this.findViewById(itemID);
        //layout
        FrameLayout layout = new FrameLayout(m_parent);
        layout.setOnTouchListener(new View.OnTouchListener() {
            boolean click=false;
            int mLastMotionY=0;
            int mLastMotionX=0;
            public boolean onTouch(View v, MotionEvent event) {
                int y = (int) event.getRawY();
                int x = (int) event.getRawX();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundColor(Color.parseColor("#E6E6E6"));
                        mLastMotionY = y;
                        mLastMotionX = x;
                        click=true;
                        break;
                    case MotionEvent.ACTION_UP:
                        v.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        if(click && event.getX()>0 && event.getX()<v.getWidth() &&
                                event.getY()>0 && event.getY()<v.getHeight()) {
                            Turn(title);
                        }
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
        //center
        TextView text=new TextView(m_parent);
        text.setId(id);
        text.setIncludeFontPadding(false);
        text.setText(title);
        text.setTextColor(ContextCompat.getColor(m_parent,R.color.textblue));
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
        FrameLayout.LayoutParams Params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params1.gravity =Gravity.CENTER;
        layout.addView(text,Params1);
        //line
        View line=new View(m_parent);
        line.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.nintygray));
        FrameLayout.LayoutParams Params3 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1);
        Params3.gravity =Gravity.BOTTOM;
        Params3.leftMargin=30;
        Params3.rightMargin=30;
        layout.addView(line,Params3);
        //layout
        FrameLayout.LayoutParams Params4 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height);
        itemLayout.addView(layout,Params4);
    }
    private void initHead (FrameLayout layout, UserItem user){//头
        layout.setBackgroundResource(R.drawable.default_background);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Turn("头部");
            }
        });
        //文字框
        FrameLayout textLayout=new FrameLayout(m_parent);
            //头像
            ImageView titleImage=new ImageView(m_parent);
            if(user!=null && user.m_head!=null)
                titleImage.setImageDrawable(Widget.ByteToDrawable(user.m_head));
            else
                titleImage.setImageResource(R.drawable.default_logo);
            FrameLayout.LayoutParams Params1 = new FrameLayout.LayoutParams(120,120);
            textLayout.addView(titleImage,Params1);

        if (user != null) {
            //人名
            TextView nameText=new TextView(m_parent);
            nameText.setIncludeFontPadding(false);
            nameText.setText(user.m_name);
            nameText.setTextColor(ContextCompat.getColor(m_parent,R.color.nintygray));
            nameText.setTextSize(TypedValue.COMPLEX_UNIT_PX,36);
            FrameLayout.LayoutParams Params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            Params2.leftMargin=150;
            Params2.topMargin=4;
            textLayout.addView(nameText,Params2);
            //电话
            TextView telText=new TextView(m_parent);
            telText.setIncludeFontPadding(false);
            telText.setText(user.m_phone);
            telText.setTextColor(ContextCompat.getColor(m_parent,R.color.nintygray));
            telText.setTextSize(TypedValue.COMPLEX_UNIT_PX,24);
            FrameLayout.LayoutParams Params3 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            Params3.leftMargin=150;
            Params3.topMargin=52;
            textLayout.addView(telText,Params3);
            //店名
            TextView shopText=new TextView(m_parent);
            shopText.setIncludeFontPadding(false);
            shopText.setText(user.m_shop.m_shop);
            shopText.setTextColor(ContextCompat.getColor(m_parent,R.color.nintygray));
            shopText.setTextSize(TypedValue.COMPLEX_UNIT_PX,24);
            FrameLayout.LayoutParams Params4 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            Params4.leftMargin=150;
            Params4.topMargin=87;
            textLayout.addView(shopText,Params4);
        }
        else{
            TextView nameText=new TextView(m_parent);
            nameText.setIncludeFontPadding(false);
            nameText.setText("未登录");
            nameText.setTextColor(ContextCompat.getColor(m_parent,R.color.nintygray));
            nameText.setTextSize(TypedValue.COMPLEX_UNIT_PX,36);
            FrameLayout.LayoutParams Params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            Params2.leftMargin=150;
            Params2.topMargin=42;
            textLayout.addView(nameText,Params2);
        }
        //layout
        FrameLayout.LayoutParams Params8 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params8.leftMargin=200;
        Params8.topMargin=42;
        layout.addView(textLayout,Params8);

        if(user!=null) {
            LinearLayout adLayout = new LinearLayout(m_parent);
            adLayout.setOrientation(LinearLayout.HORIZONTAL);
            //地址标
            ImageView adImage = new ImageView(m_parent);
            adImage.setImageDrawable(ContextCompat.getDrawable(m_parent,R.drawable.icon_ad));
            LinearLayout.LayoutParams Params5 = new LinearLayout.LayoutParams(21, 23);
            Params5.gravity = Gravity.CENTER_VERTICAL;
            adLayout.addView(adImage, Params5);
            //地址
            TextView adText = new TextView(m_parent);
            adText.setIncludeFontPadding(false);
            if (!user.m_province.isEmpty() && !user.m_city.isEmpty() && !user.m_district.isEmpty())
                adText.setText("  " + user.m_province + user.m_city + user.m_district);
            else
                adText.setText("  请到个人信息填写地址信息");
            adText.setTextColor(ContextCompat.getColor(m_parent,R.color.white));
            adText.setTextSize(TypedValue.COMPLEX_UNIT_PX, 24);
            LinearLayout.LayoutParams Params6 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            Params6.gravity = Gravity.CENTER_VERTICAL;
            adLayout.addView(adText, Params6);
            //layout
            FrameLayout.LayoutParams Params7 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            Params7.gravity = Gravity.CENTER_HORIZONTAL;
            Params7.topMargin = 185;
            layout.addView(adLayout, Params7);
        }
    }
}
