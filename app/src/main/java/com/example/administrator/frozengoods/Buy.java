package com.example.administrator.frozengoods;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.content.Context;
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

public class Buy  extends Fragment {
    private View m_this;
    ImageView btn1;
    private MainActivity m_parent;
    private int m_indexChat;
    private int m_indexEdit;
    private int screenWidth;
    public int m_index;
    private int m_index1;
    private int m_index2;
    private int m_index3;
    private LinearLayout m_itemLayout;
    private TextView m_text1,m_text2,m_text3;
    private List<GetItem> m_ownList;
    private  String[] indexString = {"采购","求购"};
    private  String[] index1String = {"全部","家畜","家禽","加工品","海鲜","其他"};
    private  String[][] index2String = {{"全部"," "," "," "," "," "},
            {"全部","腿部","胸部","内脏","尾巴","其他"},
            {"全部","腿部","翅部","内脏","胸部","其他"},
            {"全部","肉丸","熟食","果蔬","小吃","其他"},
            {"全部","虾类","鱼类","贝类","海带","其他"},
            {"全部","灌肠","水发","调料","魔芋","其他"}};
    private  String[] index3String = {"距离↗","时间↘","价格口"};
    private  int[] indexID = {R.id.index_buy1_1,R.id.index_buy1_2,R.id.index_buy1_3,R.id.index_buy1_4,R.id.index_buy1_5,R.id.index_buy1_6};
    private  int[] textID = {R.id.index_buy1_1_text,R.id.index_buy1_2_text,R.id.index_buy1_3_text,R.id.index_buy1_4_text,R.id.index_buy1_5_text,R.id.index_buy1_6_text};
    private  int[] lineID = {R.id.index_buy1_1_line,R.id.index_buy1_2_line,R.id.index_buy1_3_line,R.id.index_buy1_4_line,R.id.index_buy1_5_line,R.id.index_buy1_6_line};
    private  int[] index0ID = {R.id.index_buy_1,R.id.index_buy_2};
    private  int[] text0ID = {R.id.index_buy_1_text,R.id.index_buy_2_text};
    private boolean[] m_isFollow;
    //Create
    @Override public void onAttach(Context context){
        m_parent=(MainActivity) context;
        super.onAttach(context);
    }
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_this = inflater.inflate(R.layout.buy, container, false);
        m_itemLayout=(LinearLayout)m_this.findViewById(R.id.layout_content);
        initView();
        return m_this;
    }
    //Turn
    public void Turn(String label){
        Intent intent;
        switch(label){
            case "New":
                intent = m_parent.Turn1(BuyItem.class,"New");
                m_parent.m_data.m_user.clearPic();
                intent.putExtra("user", m_parent.m_data.m_user);
                m_parent.Turn2(intent,20);
                break;
            case "Edit":
                intent = m_parent.Turn1(BuyItem.class,"Edit");
                m_parent.m_data.m_user.clearPic();
                intent.putExtra("user", m_parent.m_data.m_user);
                intent.putExtra("item", m_ownList.get(m_indexEdit));
                m_parent.Turn2(intent,21);
                break;
            case "Chat":
                if(m_indexChat>=0) {
                    ChatItems chats2=m_parent.m_data.m_chatList.get(m_indexChat);
                    m_parent.m_data.m_chatList.get(m_indexChat).SetRead();
                    DatabaseHelper.Act.UpdateData("chat_" + m_parent.m_data.m_user.m_user, "state", "1", "user=? and state=?", new String[]{chats2.toUser, "0"});
                    intent=m_parent.Turn1(InfoChat.class,"ITEM_DETAILS");
                    intent.putExtra("name",chats2.toName);
                    intent.putExtra("chat", chats2);
                    intent.putExtra("toHead",DatabaseHelper.Act.GetHead(chats2.toUser));
                    intent.putExtra("fromHead",m_parent.m_data.m_user.m_head);
                    m_parent.Turn2(intent,22);
                }
        }
    }
    public void Update(String label,Intent data){
        switch(label) {
            case "New":{
                GetItem item = data.getParcelableExtra("item");
                if (m_ownList == null)
                    m_ownList=new ArrayList<>();
                m_ownList.add(0, item);
                m_itemLayout.removeAllViews();
                for (int i = 0; i < m_ownList.size(); i++)
                    initItem2(m_ownList.get(i), i);
                break;
            }
            case "Edit": {
                GetItem item = data.getParcelableExtra("item");
                boolean isDown=data.getBooleanExtra("isDown",false);
                if(isDown)
                    m_ownList.remove(m_indexEdit);
                else {
                    m_ownList.remove(m_indexEdit);
                    m_ownList.add(0, item);
                }
                m_itemLayout.removeAllViews();
                for (int i = 0; i < m_ownList.size(); i++)
                    initItem2(m_ownList.get(i), i);
                break;
            }
            case "Chat":
                if(m_indexChat>=0) {
                    ChatItems chat=data.getParcelableExtra("chat");
                    chat.clearPic();
                    m_parent.m_data.m_chatList.set(m_indexChat, chat);
                }
        }
    }
    //Update
    public void Update(){
        m_itemLayout.removeAllViews();
        if(0==m_index){
            (m_this.findViewById(R.id.buy_index1)).setVisibility(View.VISIBLE);
            (m_this.findViewById(R.id.buy_index2)).setVisibility(View.VISIBLE);
            (m_this.findViewById(R.id.buy_index3)).setVisibility(View.VISIBLE);
            (m_this.findViewById(R.id.buy_btn)).setVisibility(View.GONE);
            //layout
            String str;
            List<String> strList=new ArrayList<>();
            if(!m_parent.m_data.m_user.m_city.isEmpty()) {
                str=" where city=? and ";
                strList.add(m_parent.m_data.m_user.m_city);
            }
            else
                str=" where ";
            if(m_index1!=0){
                str+=" class1=? and";
                strList.add(index1String[m_index1]);
                if(m_index2!=0){
                    str+=" class2=? and";
                    strList.add(index2String[m_index1][m_index2]);
                }
            }
            if(m_text3.getText().toString().equals("价格口")){
                str+=" price=?";
                strList.add("-1");
            }
            else {
                str+=" price>?";
                strList.add("0");
            }
            if(m_text2.getText().toString().equals("时间↘"))
                str+=" ORDER BY district,id ASC ";
            else
                str+=" ORDER BY district,id DESC ";
            String[] array;
            ArrayList<GetItem> m_othersList;
            array=new String[strList.size()];
            for (int i = 0; i < strList.size(); i++) {
                array[i] = strList.get(i);
            }
            m_othersList = DatabaseHelper.Act.GetGetItem(str, array);
            m_isFollow=new boolean[m_othersList.size()];
            for(int i = 0; i< m_othersList.size(); i++)
                initItem1(i, m_othersList.get(i));

        }
        else if(1==m_index){
            (m_this.findViewById(R.id.buy_index1)).setVisibility(View.GONE);
            (m_this.findViewById(R.id.buy_index2)).setVisibility(View.GONE);
            (m_this.findViewById(R.id.buy_index3)).setVisibility(View.GONE);
            (m_this.findViewById(R.id.buy_btn)).setVisibility(View.VISIBLE);
            initBtn();
            //layout
            m_ownList= DatabaseHelper.Act.GetGetItem("where user=? ORDER BY id DESC",new String[]{m_parent.m_data.m_user.m_user});
            if(m_ownList!=null)
                for(int i=0;i<m_ownList.size();i++)
                    initItem2(m_ownList.get(i),i);
        }
    }
    //Layout
    public void initView(){
        if(m_parent.m_data.m_user!=null) {
            DisplayMetrics outMetrics = new DisplayMetrics();//获取屏幕的宽度
            m_parent.getWindow().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
            screenWidth = outMetrics.widthPixels;

            ((LinearLayout)m_this.findViewById(R.id.buy_layout_index)).removeAllViews();
            ((LinearLayout)m_this.findViewById(R.id.buy_layout_index1)).removeAllViews();
            ((LinearLayout)m_this.findViewById(R.id.buy_layout_index2)).removeAllViews();
            ((LinearLayout)m_this.findViewById(R.id.buy_layout_index3)).removeAllViews();
            m_index1 = 0;
            initIndex();
            initIndex1();
            initIndex2();
            initIndex3 ();
            Update();
        }
        else {
            ( m_this.findViewById(R.id.buy_btn)).setVisibility(View.GONE);
        }
    }
    //Index
    private void initIndex (){
        LinearLayout layout = (LinearLayout)m_this.findViewById(R.id.buy_layout_index);
        layout.setGravity(Gravity.CENTER);
        for(int i=0;i<indexString.length;i++) {
            FrameLayout indexItemLayout = new FrameLayout(m_parent);
            indexItemLayout.setId(index0ID[i]);
            indexItemLayout.setOnClickListener(new Buy.TabOnClickListener(i));
            if(i==m_index)
                indexItemLayout.setBackgroundResource(R.color.white);
            else
                indexItemLayout.setBackgroundColor(Color.parseColor("#d9d9d9"));
            //文字
            TextView indexText = new TextView(m_parent);
            indexText.setText(indexString[i]);
            indexText.setId(text0ID[i]);
            indexText.setTextSize(TypedValue.COMPLEX_UNIT_PX,30);
            if(i==m_index)
                indexText.setTextColor(ContextCompat.getColor(m_parent,R.color.textblue));
            else
                indexText.setTextColor(ContextCompat.getColor(m_parent,R.color.thirtygray));
            FrameLayout.LayoutParams p1 = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            p1.gravity =Gravity.CENTER;
            indexItemLayout.addView(indexText,p1);
            //布局
            LinearLayout.LayoutParams p3 = new LinearLayout.LayoutParams(screenWidth/indexString.length, LinearLayout.LayoutParams.MATCH_PARENT);
            layout.addView(indexItemLayout,p3);
        }
    }
    public class TabOnClickListener implements View.OnClickListener {
        int index;

        TabOnClickListener(int i) {
            index = i;
        }

        public void onClick(View v) {
            m_index = index;
            Update();
            for (int i = 0; i < indexString.length; i++) {
                if (i != index) {
                    TextView t1=((TextView) m_parent.findViewById(text0ID[i]));
                    assert t1 != null;
                    t1.setTextColor(ContextCompat.getColor(m_parent,R.color.thirtygray));
                    View b1= m_parent.findViewById(index0ID[i]);
                    assert b1 != null;
                    b1.setBackgroundColor(Color.parseColor("#d9d9d9"));
                } else {
                    TextView t1=((TextView) m_parent.findViewById(text0ID[i]));
                    assert t1 != null;
                    t1.setTextColor(ContextCompat.getColor(m_parent,R.color.textblue));
                    View b1= m_parent.findViewById(index0ID[i]);
                    assert b1 != null;
                    b1.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.white));
                }
            }
        }
    }
    private void initIndex1 (){
        LinearLayout layout = (LinearLayout)m_this.findViewById(R.id.buy_layout_index1);
        layout.setGravity(Gravity.CENTER);
        for(int i=0;i<index1String.length;i++) {
            FrameLayout indexItemLayout = new FrameLayout(m_parent);
            indexItemLayout.setId(indexID[i]);
            indexItemLayout.setOnClickListener(new Buy.Tab1OnClickListener(i));
            //文字
            TextView indexText = new TextView(m_parent);
            indexText.setText(index1String[i]);
            indexText.setId(textID[i]);
            indexText.setTextSize(TypedValue.COMPLEX_UNIT_PX,28);
            indexText.setTextColor(ContextCompat.getColor(m_parent,R.color.thirtygray));
            if(i==m_index1)
                indexText.setTextColor(ContextCompat.getColor(m_parent,R.color.textblue));
            FrameLayout.LayoutParams p1 = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            p1.gravity =Gravity.CENTER;
            indexItemLayout.addView(indexText,p1);
            //下划线
            View line=new View(m_parent);
            line.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.white));
            if(i==m_index1)
                line.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.textblue));
            line.setId(lineID[i]);
            FrameLayout.LayoutParams p2 = new FrameLayout.LayoutParams(screenWidth/index1String.length, 4);
            p2.gravity =Gravity.BOTTOM;
            indexItemLayout.addView(line,p2);
            //布局
            LinearLayout.LayoutParams p3 = new LinearLayout.LayoutParams(screenWidth/index1String.length, LinearLayout.LayoutParams.MATCH_PARENT);
            layout.addView(indexItemLayout,p3);
        }
    }
    public class Tab1OnClickListener implements View.OnClickListener{
        int index;
        Tab1OnClickListener(int i){
            index=i;
        }
        public void onClick(View v) {
            m_index1=index;
            m_index2 = 0;
            initIndex2();
            for(int i=0;i<index1String.length;i++) {
                if(i!=index) {
                    TextView t1=((TextView) m_parent.findViewById(textID[i]));
                    assert t1 != null;
                    t1.setTextColor(ContextCompat.getColor(m_parent,R.color.thirtygray));
                    View b1= m_parent.findViewById(lineID[i]);
                    assert b1 != null;
                    b1.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.white));
                }
                else {
                    TextView t1=((TextView) m_parent.findViewById(textID[i]));
                    assert t1 != null;
                    t1.setTextColor(ContextCompat.getColor(m_parent,R.color.textblue));
                    View b1= m_parent.findViewById(lineID[i]);
                    assert b1 != null;
                    b1.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.textblue));
                }
            }
            Update();
        }
    }
    private void initIndex2 (){
        LinearLayout layout = (LinearLayout)m_this.findViewById(R.id.buy_layout_index2);
        layout.removeAllViews();
        layout.setGravity(Gravity.CENTER);
        for(int i=0;i<index2String[m_index1].length;i++) {
            FrameLayout indexItemLayout = new FrameLayout(m_parent);
            indexItemLayout.setOnClickListener(new Buy.Tab2OnClickListener(i));
            //文字
            TextView indexText = new TextView(m_parent);
            indexText.setText(index2String[m_index1][i]);

            indexText.setId(m_parent.getResources().getIdentifier("index_buy2_"+(i+1), "id", m_parent.getPackageName()));
            indexText.setTextSize(TypedValue.COMPLEX_UNIT_PX,28);
            indexText.setTextColor(Color.parseColor("#b2b2b2"));
            if(i==m_index2)
                indexText.setTextColor(ContextCompat.getColor(m_parent,R.color.textblue));
            FrameLayout.LayoutParams p1 = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            p1.gravity =Gravity.CENTER;
            indexItemLayout.addView(indexText,p1);
            //布局
            LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(691/index2String[m_index1].length, LinearLayout.LayoutParams.MATCH_PARENT);
            layout.addView(indexItemLayout,p2);
        }
    }
    public class Tab2OnClickListener implements View.OnClickListener{
        int index;
        Tab2OnClickListener(int i){
            index=i;
        }
        public void onClick(View v) {
            m_index2=index;
            if(!index2String[m_index1][m_index2].equals(" ")) {
                for (int i = 0; i < index2String[m_index1].length; i++) {
                    if (i != m_index2) {
                        TextView t1=((TextView) m_parent.findViewById(m_parent.getResources().getIdentifier("index_buy2_" + (i + 1), "id", m_parent.getPackageName())));
                        assert t1 != null;
                        t1.setTextColor(Color.parseColor("#b2b2b2"));
                    } else {
                        TextView t1=((TextView) m_parent.findViewById(m_parent.getResources().getIdentifier("index_buy2_" + (i + 1), "id", m_parent.getPackageName())));
                        assert t1 != null;
                        t1.setTextColor(ContextCompat.getColor(m_parent,R.color.textblue));
                    }
                }
            }
            Update();
        }
    }
    private void initIndex3 (){
        LinearLayout layout = (LinearLayout)m_this.findViewById(R.id.buy_layout_index3);
        layout.removeAllViews();
        layout.setGravity(Gravity.CENTER);
        m_text1=new TextView(m_parent);
        m_text2=new TextView(m_parent);
        m_text3=new TextView(m_parent);
        for(int i=0;i<index3String.length;i++) {
            FrameLayout indexItemLayout = new FrameLayout(m_parent);
            indexItemLayout.setOnClickListener(new Buy.Tab3OnClickListener(i));
            //文字
            TextView indexText;
            switch (i){
                case 0:
                    indexText=m_text1;
                    break;
                case 1:
                    indexText=m_text2;
                    break;
                case 2:
                    indexText=m_text3;
                    break;
                default:
                    indexText=m_text3;
                    break;
            }
            indexText.setText(index3String[i]);
            indexText.setTextSize(TypedValue.COMPLEX_UNIT_PX,30);
            if(i==m_index3 && i!=2)
                indexText.setTextColor(ContextCompat.getColor(m_parent,R.color.textblue));
            else
                indexText.setTextColor(ContextCompat.getColor(m_parent,R.color.thirtygray));
            FrameLayout.LayoutParams p1 = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            p1.gravity =Gravity.CENTER;
            indexItemLayout.addView(indexText,p1);
            //布局
            LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(screenWidth/index3String.length, LinearLayout.LayoutParams.MATCH_PARENT);
            layout.addView(indexItemLayout,p2);
        }
    }
    public class Tab3OnClickListener implements View.OnClickListener{
        int index;
        Tab3OnClickListener(int i){
            index=i;
        }
        public void onClick(View v) {
            for (int i = 0; i < index3String.length; i++) {
                if (i != index && index!=2) {
                    switch (i){
                        case 0:
                            m_text1.setTextColor(ContextCompat.getColor(m_parent,R.color.thirtygray));
                            break;
                        case 1:
                            m_text2.setTextColor(ContextCompat.getColor(m_parent,R.color.thirtygray));
                            break;
                    }
                } else {
                    switch (i){
                        case 0:
                            if(index!=2)
                                m_text1.setTextColor(ContextCompat.getColor(m_parent,R.color.textblue));
                            if(m_index3==index) {
                                if (m_text1.getText().toString().equals("距离↗"))
                                    m_text1.setText("距离↘");
                                else
                                    m_text1.setText("距离↗");
                            }
                            break;
                        case 1:
                            if(index!=2)
                                m_text2.setTextColor(ContextCompat.getColor(m_parent,R.color.textblue));
                            if(m_index3==index) {
                                if (m_text2.getText().toString().equals("时间↗"))
                                    m_text2.setText("时间↘");
                                else
                                    m_text2.setText("时间↗");
                            }
                            break;
                        case 2:
                            if (m_text3.getText().toString().equals("价格√"))
                                m_text3.setText("价格口");
                            else
                                m_text3.setText("价格√");
                            break;
                    }
                }
            }
            if(index!=2)
                m_index3=index;
            Update();
        }
    }
    //Item
    public void initBtn(){
        btn1=((ImageView)m_this.findViewById(R.id.buy_btn));
        btn1.setOnTouchListener(new View.OnTouchListener() {
            boolean click=false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        btn1.setBackgroundResource(R.drawable.icon_add_press);
                        click=true;
                        break;
                    case MotionEvent.ACTION_UP:
                        btn1.setBackgroundResource(R.drawable.icon_add);
                        if(click && event.getX()>0 && event.getX()<v.getWidth() &&
                                event.getY()>0 && event.getY()<v.getHeight()) {
                            Turn("New");
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if(click) {
                            click = false;
                            btn1.setBackgroundResource(R.drawable.icon_add);
                        }
                        break;
                }
                return true;
            }
        });
    }
    public void initItem1(final int index,final GetItem item){
        String str;
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
        final TextView m_text1=new TextView(m_parent);
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
        if(item.price>0){
            str="价格："+item.price;
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
                                m_indexChat=m_parent.m_data.NewChat(item.user);
                                Turn("Chat");
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
        Params8.gravity=    Gravity.END;
        layoutRight.addView(btnPhone,Params8);
        //follow
        final Button btnFollow=new Button(m_parent);
        m_isFollow[index]=DatabaseHelper.Act.IsFollow(m_parent.m_data.m_user.m_user,item.id,"getItem");
        if(!m_isFollow[index])
            btnFollow.setBackgroundResource(R.drawable.icon_item_guanzhu);
        else
            btnFollow.setBackgroundResource(R.drawable.icon_item_yiguanzhu);
        btnFollow.setOnTouchListener(new View.OnTouchListener() {
            boolean click=false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if(!m_isFollow[index])
                            btnFollow.setBackgroundResource(R.drawable.icon_item_guanzhu_press);
                        else
                            btnFollow.setBackgroundResource(R.drawable.icon_item_yiguanzhu_press);
                        click=true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if(click && event.getX()>0 && event.getX()<v.getWidth() &&
                                event.getY()>0 && event.getY()<v.getHeight()) {
                            if(!m_isFollow[index]) {
                                if (DatabaseHelper.Act.AddFollow(m_parent.m_data.m_user.m_user,item.id, "getItem"))
                                    m_isFollow[index] = true;
                            }
                            else {
                                if (DatabaseHelper.Act.DelFollow(m_parent.m_data.m_user.m_user,item.id, "getItem"))
                                    m_isFollow[index] = false;
                            }
                            m_parent.GetUpdate();
                        }
                        click = false;
                        if(!m_isFollow[index])
                            btnFollow.setBackgroundResource(R.drawable.icon_item_guanzhu);
                        else
                            btnFollow.setBackgroundResource(R.drawable.icon_item_yiguanzhu);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if(click) {
                            click = false;
                            if(!m_isFollow[index])
                                btnFollow.setBackgroundResource(R.drawable.icon_item_guanzhu);
                            else
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
    public void initItem2(GetItem item,final int index){
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
                        if(click && event.getX()>0 && event.getX()<v.getWidth() &&
                                event.getY()>0 && event.getY()<v.getHeight()) {
                            m_indexEdit=index;
                            Turn("Edit");
                        }
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
        String str="求"+item.name;
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
            str="价格："+item.price;
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
}
