package com.example.administrator.frozengoods;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Follow  extends Fragment {
    private View m_this;
    private MainActivity m_parent;
    private LinearLayout m_itemLayout;
    int m_chooseIndex;
    int m_index;
    private  String[] indexString = {"商品","采购","店铺"};
    private  int[] indexID = {R.id.index_follow_item,R.id.index_follow_buy,R.id.index_follow_shop};
    private  int[] textID = {R.id.index_follow_item_text,R.id.index_follow_buy_text,R.id.index_follow_shop_text};
    private  int[] lineID = {R.id.index_follow_item_line,R.id.index_follow_buy_line,R.id.index_follow_shop_line};

    ArrayList<GoodItem> m_List0;
    ArrayList<GetItem> m_List1;
    ArrayList<UserItem> m_List2;

    //Create
    @Override public void onAttach(Context context){
        m_parent=(MainActivity) context;
        super.onAttach(context);
    }
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_this = inflater.inflate(R.layout.follow, container, false);
        m_itemLayout = (LinearLayout)m_this.findViewById(R.id.follow_layout_items);
        m_index=0;
        initIndex();
        if(m_parent.m_data.m_user!=null)
            Login();
        else
            Logout();
        return m_this;
    }

    //Turn
    private void New(int i) {
        if(m_index==2)
            m_chooseIndex=m_parent.m_data.NewChat(m_List2.get(i).m_user);
        else if(m_index==1)
            m_chooseIndex=m_parent.m_data.NewChat(m_List1.get(i).user);

        if(m_chooseIndex>=0) {
            ChatItems chats2 = m_parent.m_data.m_chatList.get(m_chooseIndex);
            DatabaseHelper.Act.UpdateData("chat_" + m_parent.m_data.m_user.m_user, "state", "1", "user=? and state=?", new String[]{chats2.toUser, "0"});
            m_parent.m_data.m_chatList.get(m_chooseIndex).SetRead();
            Intent intent = m_parent.Turn1(InfoChat.class,"FOLLOW");
            intent.putExtra("name", chats2.toName);
            intent.putExtra("chat", chats2);
            intent.putExtra("toHead", DatabaseHelper.Act.GetHead(chats2.toUser));
            intent.putExtra("fromHead", DatabaseHelper.Act.GetHead(m_parent.m_data.m_user.m_user));
            m_parent.Turn2(intent, 30);
        }
    }
    void Cancel(int index) {
        switch(m_index){
            case 0:
                DatabaseHelper.Act.DelFollow(m_parent.m_data.m_user.m_user,m_List0.get(index).id,"goodItem");
                m_List0.remove(index);
                Update();
                m_parent.FollowUpdate();
                break;
            case 1:
                DatabaseHelper.Act.DelFollow(m_parent.m_data.m_user.m_user,m_List1.get(index).id,"getItem");
                m_List1.remove(index);
                m_parent.GetUpdate();
                Update();
                break;
            case 2:
                DatabaseHelper.Act.DelFollow(m_parent.m_data.m_user.m_user,m_List2.get(index).m_user, "shop") ;
                DatabaseHelper.Act.UpdateShopState(m_parent.m_data.m_user,m_List2.get(index).m_user,"followed",-1);
                m_List2.remove(index);
                Update();
                m_parent.FollowUpdate();
                break;
        }
    }
    private void Turn(int index){
        m_chooseIndex=index;
        Intent intent;
        switch(m_index){
            case 0:
                intent = m_parent.Turn1(ItemDetails.class,"Follow");
                m_parent.m_data.m_user.clearPic();
                intent.putExtra("data", m_parent.m_data);
                m_List0.get(index).clearPic();
                intent.putExtra("item", m_List0.get(index));
                m_parent.Turn2(intent,31);
                break;
            case 1:
                break;
            case 2:
                intent = m_parent.Turn1(ShopOther.class,"Follow");
                if(m_parent.m_data.m_user.m_user.equals(m_List2.get(index).m_user))
                    m_parent.m_data.m_user.m_shop.m_visited++;
                m_List2.get(index).m_shop.m_visited++;
                m_parent.m_data.clearPic();
                intent.putExtra("data", m_parent.m_data);
                intent.putExtra("shop", m_List2.get(index).m_shop);
                intent.putExtra("user",m_List2.get(index).m_user);
                intent.putExtra("isFollowShop",true);
                m_parent.Turn2(intent,33);
                break;
        }
    }

    //Update
    public void Update(){
        m_itemLayout.removeAllViews();
        switch (m_index) {
            case 0:
                if (m_List0 != null)
                    for (int i = 0; i < m_List0.size(); i++)
                        initItem0(i);
                break;
            case 1:
                if (m_List1 != null)
                    for (int i = 0; i < m_List1.size(); i++)
                        initItem1(i);
                break;
            case 2:
                if (m_List2 != null)
                    for (int i = 0; i < m_List2.size(); i++)
                        initItem2(i);
                break;
        }
    }
    public void Login(){
        Logout();
        m_index=0;
        initView();
    }
    public void Logout(){
        m_itemLayout.removeAllViews();
    }
    //Layout
    public void initView(){
        if(m_parent.m_data.m_user!=null) {
            m_List0 = new ArrayList<>();
            m_List1 = new ArrayList<>();
            m_List2 = new ArrayList<>();
            ArrayList<String> strList;
            switch (m_index) {
                case 0:
                    strList = DatabaseHelper.Act.GetFollow(m_parent.m_data.m_user.m_user, "goodItem");
                    if (strList != null)
                        m_List0 = DatabaseHelper.Act.GetGoodItemByID(strList);
                    break;
                case 1:
                    strList = DatabaseHelper.Act.GetFollow(m_parent.m_data.m_user.m_user, "getItem");
                    if (strList != null)
                        m_List1 = DatabaseHelper.Act.GetGetItemByID(strList);
                    break;
                case 2:
                    strList = DatabaseHelper.Act.GetFollow(m_parent.m_data.m_user.m_user, "shop");
                    if (strList != null)
                        m_List2 = DatabaseHelper.Act.GetUserItemByID(strList);
                    break;
            }
            Update();
        }
    }
    private void initIndex (){
        LinearLayout layout = (LinearLayout)m_this.findViewById(R.id.follow_layout_index);
        layout.setGravity(Gravity.CENTER);

        DisplayMetrics outMetrics=new DisplayMetrics();//获取屏幕的宽度
        m_parent.getWindow().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int screenWidth = outMetrics.widthPixels;


        for(int i=0;i<indexString.length;i++) {
            FrameLayout indexItemLayout = new FrameLayout(m_parent);
            indexItemLayout.setId(indexID[i]);
            indexItemLayout.setOnClickListener(new Follow.TabOnClickListener(i));
            //文字
            TextView indexText = new TextView(m_parent);
            indexText.setText(indexString[i]);
            indexText.setId(textID[i]);
            indexText.setTextSize(TypedValue.COMPLEX_UNIT_PX,28);
            indexText.setTextColor(ContextCompat.getColor(m_parent,R.color.thirtygray));
            if(i==0)
                indexText.setTextColor(ContextCompat.getColor(m_parent,R.color.textblue));
            FrameLayout.LayoutParams p1 = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            p1.gravity =Gravity.CENTER;
            indexItemLayout.addView(indexText,p1);
            //下划线
            View line=new View(m_parent);
            line.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.white));
            if(i==0)
                line.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.textblue));
            line.setId(lineID[i]);
            FrameLayout.LayoutParams p2 = new FrameLayout.LayoutParams(screenWidth /indexString.length, 4);
            p2.gravity =Gravity.BOTTOM;
            indexItemLayout.addView(line,p2);
            //布局
            LinearLayout.LayoutParams p3 = new LinearLayout.LayoutParams(screenWidth /indexString.length, LinearLayout.LayoutParams.MATCH_PARENT);
            layout.addView(indexItemLayout,p3);
        }
    }
    public class TabOnClickListener implements View.OnClickListener{
        private int index=0;
        TabOnClickListener(int i){
            index=i;
        }
        public void onClick(View v) {
            for(int i=0;i<indexString.length;i++) {
                if(i!=index) {
                    TextView t=((TextView) m_parent.findViewById(textID[i]));
                    assert t != null;
                    t.setTextColor(ContextCompat.getColor(m_parent,R.color.thirtygray));
                    View l=m_parent.findViewById(lineID[i]);
                    assert l != null;
                    l.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.white));
                }
                else {
                    TextView t=((TextView) m_parent.findViewById(textID[i]));
                    assert t != null;
                    t.setTextColor(ContextCompat.getColor(m_parent,R.color.textblue));
                    View l=m_parent.findViewById(lineID[i]);
                    assert l != null;
                    l.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.textblue));
                }
            }
            m_index=index;
            initView();
        }
    }

    //Item
    public void initItem0 (final int index){
        final GoodItem item;
        item=m_List0.get(index);
        FrameLayout layout = new FrameLayout(m_parent); //总框架
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
                            if(m_parent.m_data.m_user!=null)
                                Turn(index);
                            else{
                                Intent intent = new Intent(m_parent, InfoUser.class);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("data", m_parent.m_data);
                                bundle.putString("id", "Item_Find");
                                intent.putExtras(bundle);
                                startActivityForResult(intent, 0);
                            }
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

        LinearLayout layoutPic=new LinearLayout(m_parent);//图片
        LinearLayout layoutText=new LinearLayout(m_parent);//文字
        layoutPic.setOrientation(LinearLayout.HORIZONTAL);
        layoutPic.setGravity(Gravity.CENTER);
        layoutText.setOrientation(LinearLayout.VERTICAL);
        //图片1
        ImageView titleImage=new ImageView(m_parent);
        titleImage.setImageDrawable(Widget.ByteToDrawable(item.pic1));
        titleImage.setScaleType(ImageView.ScaleType.FIT_XY);
        layoutPic.addView(titleImage,158,158);
        //名称
        TextView titleText=new TextView(m_parent);
        titleText.setIncludeFontPadding(false);
        titleText.setText(item.name);
        titleText.setTextColor(ContextCompat.getColor(m_parent,R.color.tengray));
        titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX,27);
        LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutText.addView(titleText,p1);
        //price
        TextView picListItemPrice = new TextView(m_parent);
        picListItemPrice.setIncludeFontPadding(false);

        if(item.price>0)
            picListItemPrice.setText("价格：￥" + item.price + "/"+item.unit);
        else
            picListItemPrice.setText("价格：面谈");
        picListItemPrice.setTextColor(ContextCompat.getColor(m_parent,R.color.thirtygray));
        picListItemPrice.setTextSize(TypedValue.COMPLEX_UNIT_PX,25);
        LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        p2.topMargin=15;
        layoutText.addView(picListItemPrice, p2);
        //num
        TextView numText = new TextView(m_parent);
        numText.setIncludeFontPadding(false);
        numText.setText("数量：" + item.num +item.unit);
        numText.setTextColor(ContextCompat.getColor(m_parent,R.color.thirtygray));
        numText.setTextSize(TypedValue.COMPLEX_UNIT_PX,25);
        LinearLayout.LayoutParams p3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        p3.topMargin=14;
        layoutText.addView(numText, p3);
        //地址
        TextView adText=new TextView(m_parent);
        adText.setIncludeFontPadding(false);
        adText.setText("地址："+item.city+item.district);
        adText.setTextColor(ContextCompat.getColor(m_parent,R.color.thirtygray));
        adText.setTextSize(TypedValue.COMPLEX_UNIT_PX,25);
        LinearLayout.LayoutParams p4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        p4.topMargin=14;
        layoutText.addView(adText,p4);
        //分割线
        View line=new View(m_parent);
        line.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.nintygray));
        FrameLayout.LayoutParams p5 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1);
        p5.gravity =Gravity.BOTTOM;
        p5.leftMargin=30;
        p5.rightMargin=30;
        //follow
        final Button btnFollow=new Button(m_parent);
        btnFollow.setBackgroundResource(R.drawable.icon_item_yiguanzhu);
        btnFollow.setOnTouchListener(new View.OnTouchListener() {
            boolean click=false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        btnFollow.setBackgroundResource(R.drawable.icon_item_yiguanzhu_press);
                        click=true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if(click && event.getX()>0 && event.getX()<v.getWidth() &&
                                event.getY()>0 && event.getY()<v.getHeight()) {
                            Cancel(index);
                        }
                        btnFollow.setBackgroundResource(R.drawable.icon_item_yiguanzhu);
                        click = false;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if(click) {
                            click = false;
                            btnFollow.setBackgroundResource(R.drawable.icon_item_yiguanzhu);
                        }
                        break;
                }
                return true;
            }
        });
        FrameLayout.LayoutParams Params9 = new FrameLayout.LayoutParams(120,41);
        Params9.gravity=Gravity.END | Gravity.BOTTOM;
        Params9.bottomMargin=23;
        Params9.rightMargin=40;
        layout.addView(btnFollow,Params9);
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
    public void initItem1(final int index){
        String str;
        final GetItem item=m_List1.get(index);
        FrameLayout layout =new FrameLayout(m_parent);
        LinearLayout layoutLeft=new LinearLayout(m_parent);
        LinearLayout layoutRight=new LinearLayout(m_parent);
        layoutLeft.setOrientation(LinearLayout.VERTICAL);
        layoutRight.setOrientation(LinearLayout.VERTICAL);

        layout.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.white));
        layout.setOnTouchListener(new View.OnTouchListener() {
            boolean click=false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.eghitygray));
                        click=true;
                        break;
                    case MotionEvent.ACTION_UP:
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
        //标题
        TextView m_text1=new TextView(m_parent);
        m_text1.setIncludeFontPadding(false);
        str="求"+item.name;
        m_text1.setText(str);
        m_text1.setTextColor(ContextCompat.getColor(m_parent,R.color.tengray));
        m_text1.setTextSize(TypedValue.COMPLEX_UNIT_PX,48);
        LinearLayout.LayoutParams Params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params1.topMargin=30;
        layoutLeft.addView(m_text1,Params1);
        //数量
        TextView m_text2=new TextView(m_parent);
        m_text2.setIncludeFontPadding(false);
        m_text2.setText("数量："+item.num+item.unit);
        m_text2.setTextColor(ContextCompat.getColor(m_parent,R.color.thirtygray));
        m_text2.setTextSize(TypedValue.COMPLEX_UNIT_PX,27);
        LinearLayout.LayoutParams Params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params2.topMargin=20;
        layoutLeft.addView(m_text2,Params2);
        //价格
        TextView m_text3=new TextView(m_parent);
        m_text3.setIncludeFontPadding(false);
        if(item.price>0) {
            str = "价格：" + item.price;
            m_text3.setText(str);
        }
        else
            m_text3.setText("价格：面谈");
        m_text3.setTextColor(ContextCompat.getColor(m_parent,R.color.thirtygray));
        m_text3.setTextSize(TypedValue.COMPLEX_UNIT_PX,27);
        LinearLayout.LayoutParams Params3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params3.topMargin=18;
        layoutLeft.addView(m_text3,Params3);
        //店面
        TextView m_text4=new TextView(m_parent);
        m_text4.setIncludeFontPadding(false);
        str="店面："+item.shop;
        m_text4.setText(str);
        m_text4.setTextColor(ContextCompat.getColor(m_parent,R.color.thirtygray));
        m_text4.setTextSize(TypedValue.COMPLEX_UNIT_PX,27);
        LinearLayout.LayoutParams Params4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params4.topMargin=18;
        layoutLeft.addView(m_text4,Params4);
        //地址
        TextView m_text5=new TextView(m_parent);
        m_text5.setIncludeFontPadding(false);
        m_text5.setText("地址："+item.city+item.district);
        m_text5.setTextColor(ContextCompat.getColor(m_parent,R.color.thirtygray));
        m_text5.setTextSize(TypedValue.COMPLEX_UNIT_PX,27);
        LinearLayout.LayoutParams Params5 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params5.topMargin=18;
        layoutLeft.addView(m_text5,Params5);
        //时间
        TextView m_text6=new TextView(m_parent);
        m_text6.setIncludeFontPadding(false);
        str=item.id;
        m_text6.setText(str.substring(0,10));
        m_text6.setTextColor(ContextCompat.getColor(m_parent,R.color.sixtygray));
        m_text6.setTextSize(TypedValue.COMPLEX_UNIT_PX,20);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        p.gravity=Gravity.END;
        p.topMargin=40;
        layoutRight.addView(m_text6,p);
        //名字
        TextView m_text7=new TextView(m_parent);
        m_text7.setIncludeFontPadding(false);
        m_text7.setText(item.getterName+" "+item.getterPhone);
        m_text7.setTextColor(ContextCompat.getColor(m_parent,R.color.thirtygray));
        m_text7.setTextSize(TypedValue.COMPLEX_UNIT_PX,25);
        LinearLayout.LayoutParams Params6 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params6.topMargin=43;
        Params6.gravity=Gravity.END;
        layoutRight.addView(m_text7,Params6);
        //news
        final Button btnNews=new Button(m_parent);
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
                            New(index);
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
        LinearLayout.LayoutParams Params7 = new LinearLayout.LayoutParams(120,41);
        Params7.topMargin=14;
        Params7.gravity=Gravity.END;
        layoutRight.addView(btnNews,Params7);
        //btnPhone
        final String phone=item.getterPhone;
        final Button btnPhone=new Button(m_parent);
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
        LinearLayout.LayoutParams Params8 = new LinearLayout.LayoutParams(120,41);
        Params8.topMargin=7;
        Params8.gravity=Gravity.END;
        layoutRight.addView(btnPhone,Params8);
        //follow
        final Button btnFollow=new Button(m_parent);
        btnFollow.setBackgroundResource(R.drawable.icon_item_yiguanzhu);
        btnFollow.setOnTouchListener(new View.OnTouchListener() {
            boolean click=false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        btnFollow.setBackgroundResource(R.drawable.icon_item_yiguanzhu_press);
                        click=true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if(click && event.getX()>0 && event.getX()<v.getWidth() &&
                                event.getY()>0 && event.getY()<v.getHeight()) {
                            Cancel(index);
                            btnFollow.setBackgroundResource(R.drawable.icon_item_yiguanzhu);
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if(click) {
                            click = false;
                            btnFollow.setBackgroundResource(R.drawable.icon_item_yiguanzhu);
                        }
                        break;
                }
                return true;
            }
        });
        LinearLayout.LayoutParams Params9 = new LinearLayout.LayoutParams(120,41);
        Params9.gravity=Gravity.END;
        Params9.topMargin=7;
        layoutRight.addView(btnFollow,Params9);

        //line
        View line=new View(m_parent);
        line.setBackgroundResource(R.color.eghitygray);
        //layout
        FrameLayout.LayoutParams Params10 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params10.leftMargin=30;
        layout.addView(layoutLeft,Params10);
        FrameLayout.LayoutParams Params11 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params11.gravity=Gravity.END;
        Params11.rightMargin=40;
        layout.addView(layoutRight,Params11);
        FrameLayout.LayoutParams Params12 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1);
        Params12.gravity=Gravity.BOTTOM;
        Params12.leftMargin=30;
        Params12.rightMargin=30;
        layout.addView(line,Params12);

        LinearLayout.LayoutParams Params13 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,304);
        Params13.topMargin=0;
        m_itemLayout.addView(layout,Params13);
    }
    public void initItem2(final int index){
        ShopItem m_shop=m_List2.get(index).m_shop;
        FrameLayout layout=new FrameLayout(m_parent);
        FrameLayout layoutFollow=new FrameLayout(m_parent);
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
                            Turn(index);
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
        //logo
        ImageView logo=new ImageView(m_parent);
        logo.setImageDrawable(Widget.ByteToDrawable(m_shop.m_logo));
        FrameLayout.LayoutParams P1 = new FrameLayout.LayoutParams(138,138);
        layoutFollow.addView(logo,P1);
        //title1
        TextView title1=new TextView(m_parent);
        title1.setText(m_shop.m_shop);
        title1.setIncludeFontPadding(false);
        title1.setTextColor(ContextCompat.getColor(m_parent,R.color.tengray));
        title1.setTextSize(TypedValue.COMPLEX_UNIT_PX,36);
        FrameLayout.LayoutParams P2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        P2.leftMargin=155;
        P2.topMargin=3;
        layoutFollow.addView(title1,P2);
        //title2
        TextView title2=new TextView(m_parent);
        title2.setText(m_shop.m_shop);
        title2.setIncludeFontPadding(false);
        title2.setTextColor(ContextCompat.getColor(m_parent,R.color.tengray));
        title2.setTextSize(TypedValue.COMPLEX_UNIT_PX,24);
        FrameLayout.LayoutParams P3 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        P3.leftMargin=155;
        P3.topMargin=60;
        layoutFollow.addView(title2,P3);
        //title3
        TextView title3=new TextView(m_parent);
        title3.setText(m_shop.m_shop);
        title3.setIncludeFontPadding(false);
        title3.setTextColor(ContextCompat.getColor(m_parent,R.color.tengray));
        title3.setTextSize(TypedValue.COMPLEX_UNIT_PX,24);
        FrameLayout.LayoutParams P4 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        P4.leftMargin=155;
        P4.topMargin=110;
        layoutFollow.addView(title3,P4);

        //news
        final Button btnNews=new Button(m_parent);
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
                            New(index);
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
        final Button btnPhone=new Button(m_parent);
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
        Params8.topMargin=49;
        Params8.gravity=Gravity.END;
        layoutFollow.addView(btnPhone,Params8);
        //follow
        final Button btnFollow=new Button(m_parent);
        btnFollow.setBackgroundResource(R.drawable.icon_item_yiguanzhu);
        btnFollow.setOnTouchListener(new View.OnTouchListener() {
            boolean click=false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        btnFollow.setBackgroundResource(R.drawable.icon_item_yiguanzhu_press);
                        click=true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if(click && event.getX()>0 && event.getX()<v.getWidth() &&
                                event.getY()>0 && event.getY()<v.getHeight()) {
                            Cancel(index);
                        }
                        btnFollow.setBackgroundResource(R.drawable.icon_item_yiguanzhu);
                        click = false;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if(click) {
                            click = false;
                            btnFollow.setBackgroundResource(R.drawable.icon_item_yiguanzhu);
                        }
                        break;
                }
                return true;
            }
        });
        FrameLayout.LayoutParams Params9 = new FrameLayout.LayoutParams(120,41);
        Params9.gravity=Gravity.END;
        Params9.topMargin=97;
        layoutFollow.addView(btnFollow,Params9);
        //line
        View line=new View(m_parent);
        line.setBackgroundResource(R.color.eghitygray);
        //layout
        FrameLayout.LayoutParams p3 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,138);
        p3.gravity=Gravity.CENTER_VERTICAL;
        p3.rightMargin=45;
        p3.leftMargin=45;
        layout.addView(layoutFollow,p3);
        FrameLayout.LayoutParams p4 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1);
        p4.gravity=Gravity.BOTTOM;
        p4.rightMargin=45;
        p4.leftMargin=45;
        layout.addView(line,p4);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,196);
        m_itemLayout.addView(layout,p);
    }
}
