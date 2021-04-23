package com.example.administrator.frozengoods;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;


public class ShopOther extends AppCompatActivity {
    final ShopOther m_parent = this;
    private Data m_data;
    private View m_back_over;
    private ShopItem m_shop;
    private String m_user;
    private boolean isFollowShop;
    private EditText m_edit;
    private LinearLayout m_itemLayout;
    private ArrayList<GoodItem> m_List;
    private int m_index;

    //Create
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_other);
        Intent intent = getIntent();
        if (intent != null) {
            m_data = intent.getParcelableExtra("data");
            m_shop = intent.getParcelableExtra("shop");
            m_user = intent.getStringExtra("user");
            isFollowShop=intent.getBooleanExtra("isFollowShop",isFollowShop);
            DatabaseHelper.Act.UpdateShopState(m_data.m_user,m_user,"visited",1);
        } else {
            finish();
            return;
        }
        FrameLayout m_back = (FrameLayout) findViewById(R.id.btn_back);
        m_back_over = findViewById(R.id.btn_back_over);
        m_itemLayout = (LinearLayout) findViewById(R.id.layout_content);
        m_edit = (EditText) findViewById(R.id.shop_edit_find);
        ScrollView scroll=(ScrollView)findViewById(R.id.shop_scroll);
        assert scroll != null;
        scroll.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(m_edit!=null){
                            imm.hideSoftInputFromWindow(m_edit.getWindowToken(), 0);
                            m_edit.clearFocus();
                        }
                }
                return false;
            }
        });
        m_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)  {
                if (actionId== EditorInfo.IME_ACTION_SEARCH ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER))
                {
                    Update();
                    return true;
                }
                return false;
            }
        });

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
        m_shop=null;
        m_user=null;
        m_edit=null;
        m_itemLayout=null;
        m_List=null;
        super.onDestroy();
        setContentView(R.layout.view_null);
    }

    //Turn
    @Override public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("chats", m_data.m_chatList);
        intent.putExtra("id","");
        intent.putExtra("isFollow",isFollowShop);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
    void Turn(String label) {
        switch (label){
            case "FollowShop":
                if(!isFollowShop) {
                    if (DatabaseHelper.Act.AddFollow(m_parent.m_data.m_user.m_user,m_user, "shop") && DatabaseHelper.Act.UpdateShopState(m_data.m_user,m_user,"followed",1)){
                        m_shop.m_followed+=1;
                        isFollowShop = true;
                    }
                }
                else {
                    if (DatabaseHelper.Act.DelFollow(m_parent.m_data.m_user.m_user,m_user, "shop") && DatabaseHelper.Act.UpdateShopState(m_data.m_user,m_user,"followed",-1)) {
                        m_shop.m_followed-=1;
                        isFollowShop = false;
                    }
                }
                initTab();
                break;
            default:
                Intent intent = new Intent(this,UnKnow.class);
                startActivity(intent);
                break;
        }
    }
    public void Turn(int row,int index) {
        int num=row*3+index;
        Intent intent = new Intent();
        intent.putExtra("chats", m_data.m_chatList);
        intent.putExtra("id", m_List.get(num).id);
        intent.putExtra("isFollow",isFollowShop);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data==null)
            return ;
        if(resultCode!=RESULT_OK) {
            return ;
        }
        switch ( requestCode ) {
            case 0:
                m_data.m_chatList = data.getParcelableArrayListExtra("chats");
                break;
            case 1:
                ChatItems chat=data.getParcelableExtra("chat");
                m_data.m_chatList.set(m_index,chat);
                break;
            default :
                break;
        }
        ImageView red=(ImageView)findViewById(R.id.shop_news_point);
        if(m_data.GetUnReadNum()>0) {
            assert red != null;
            red.setVisibility(View.VISIBLE);
        }
        else {
            assert red != null;
            red.setVisibility(View.GONE);
        }
    }

    //Update
    public void Update(){
        m_itemLayout.removeAllViews();
        //layout
        String limit=m_edit.getText().toString();
        if(limit.isEmpty()) {
            m_List = DatabaseHelper.Act.GetGoodItem(" where user=? ORDER BY id DESC ", new String[]{m_user});
            DatabaseHelper.Act.GetGoodItemPic(m_List," where user=? ORDER BY id DESC ", new String[]{m_user});
        }
        else {
            m_List = DatabaseHelper.Act.GetGoodItem(" where user=? and name like '%" + limit + "%' ORDER BY id DESC ", new String[]{m_user});
            DatabaseHelper.Act.GetGoodItemPic(m_List," where user=? and name like '%" + limit + "%' ORDER BY id DESC ", new String[]{m_user});
        }

        FrameLayout picListText=new FrameLayout(m_parent);
        //title
        TextView titleText=new TextView(m_parent);
        titleText.setIncludeFontPadding(false);
        titleText.setText("上架冻品");
        titleText.setTextColor(ContextCompat.getColor(m_parent,R.color.textblue));
        titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX,30);
        FrameLayout.LayoutParams Params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        picListText.addView(titleText,Params2);
        //总数
        TextView numText=new TextView(m_parent);
        numText.setIncludeFontPadding(false);
        String str="冻品数量："+String.valueOf(m_List.size());
        numText.setText(str);
        numText.setTextColor(ContextCompat.getColor(m_parent,R.color.fiftygray));
        numText.setTextSize(TypedValue.COMPLEX_UNIT_PX,24);
        FrameLayout.LayoutParams Params9 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params9.gravity=Gravity.CENTER_VERTICAL|Gravity.END;
        picListText.addView(numText,Params9);
        //layout
        LinearLayout.LayoutParams Params6 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params6.gravity =Gravity.TOP;
        Params6.topMargin=24;
        Params6.bottomMargin=10;
        Params6.leftMargin=30;
        Params6.rightMargin=30;
        m_itemLayout.addView(picListText,Params6);
        //items
        List<GoodItem> temp=new ArrayList<>();
        if(m_List!=null) {
            int i;
            for (i = 0; i < m_List.size(); i++) {
                if (i % 3 == 0)
                    temp = new ArrayList<>();
                temp.add(m_List.get(i));
                if (i % 3 == 2)
                    initRow(i / 3, temp);
            }
            if(i%3!=0) initRow(i / 3, temp);
        }
        //分割线
        View line=new View(m_parent);
        line.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.nintygray));
        LinearLayout.LayoutParams Params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1);
        Params1.leftMargin=30;
        Params1.rightMargin=30;
        m_itemLayout.addView(line,Params1);
    }

    //Layout
    void initView(){
        //news
        ImageView red=(ImageView)findViewById(R.id.shop_news_point);
        if(m_data.GetUnReadNum()>0) {
            assert red != null;
            red.setVisibility(View.VISIBLE);
        }
        else {
            assert red != null;
            red.setVisibility(View.GONE);
        }
        FrameLayout f=(FrameLayout)findViewById(R.id.shop_news);
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
                                Intent intent = new Intent(ShopOther.this, InfoNews.class);
                                intent.putExtra("id", "SHOP_OTHER");
                                intent.putExtra("data", m_data);
                                startActivityForResult(intent, 0);
                            }
                        }
                        break;
                }
                return true;
            }
        });
        initHead((FrameLayout) findViewById(R.id.shop_layout_top));
        initTab();
        Update();
        initBottom();
    }
    public void initHead (FrameLayout layout){
        //背景
        layout.setBackgroundResource(R.drawable.default_background);
        //图片
        ImageView titleImage=new ImageView(m_parent);
        titleImage.setImageDrawable(Widget.ByteToDrawable(m_shop.m_logo));
        FrameLayout.LayoutParams Params1 = new FrameLayout.LayoutParams(152,152);
        Params1.leftMargin=30;
        Params1.topMargin=31;
        layout.addView(titleImage,Params1);
        //店名
        TextView nameText=new TextView(m_parent);
        nameText.setIncludeFontPadding(false);
        nameText.setText(m_shop.m_shop);
        nameText.setTextColor(ContextCompat.getColor(m_parent,R.color.white));
        nameText.setTextSize(TypedValue.COMPLEX_UNIT_PX,36);
        FrameLayout.LayoutParams Params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params2.leftMargin=212;
        Params2.topMargin=43;
        layout.addView(nameText,Params2);
        //标语
        TextView detailText=new TextView(m_parent);
        detailText.setIncludeFontPadding(false);
        detailText.setText(m_shop.m_adText);
        detailText.setTextColor(ContextCompat.getColor(m_parent,R.color.white));
        detailText.setTextSize(TypedValue.COMPLEX_UNIT_PX,24);
        FrameLayout.LayoutParams Params3 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params3.leftMargin=212;
        Params3.topMargin=105;
        layout.addView(detailText,Params3);
        //地址标
        ImageView adImage=new ImageView(m_parent);
        adImage.setImageDrawable(ContextCompat.getDrawable(m_parent,R.drawable.icon_ad));
        FrameLayout.LayoutParams Params4 = new FrameLayout.LayoutParams(21,23);
        Params4.leftMargin=212;
        Params4.topMargin=155;
        layout.addView(adImage,Params4);
        //地址
        TextView adText=new TextView(m_parent);
        adText.setIncludeFontPadding(false);
        adText.setText(m_shop.m_ad_province+m_shop.m_ad_city+m_shop.m_ad_district);
        adText.setTextColor(ContextCompat.getColor(m_parent,R.color.white));
        adText.setTextSize(TypedValue.COMPLEX_UNIT_PX,24);
        FrameLayout.LayoutParams Params5 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params5.leftMargin=237;
        Params5.topMargin=153;
        layout.addView(adText,Params5);
        //关注
        final Button btnFollow=new Button(this);
        if(!isFollowShop)
            btnFollow.setBackgroundResource(R.drawable.icon_item_guanzhu);
        else
            btnFollow.setBackgroundResource(R.drawable.icon_item_yiguanzhu);
        FrameLayout.LayoutParams Params9 = new FrameLayout.LayoutParams(120,41);
        Params9.gravity=Gravity.END;
        Params9.topMargin=43;
        Params9.rightMargin=32;
        layout.addView(btnFollow,Params9);

        final TextView btn1=(TextView)findViewById(R.id.shop_other_bottom_shop);
        assert btn1 != null;
        btn1.setBackgroundResource(R.color.white);
        if(!isFollowShop)
            btn1.setText("关注");
        else
            btn1.setText("取消");

        btnFollow.setOnTouchListener(new View.OnTouchListener() {
            boolean click=false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if(!isFollowShop) {
                            btnFollow.setBackgroundResource(R.drawable.icon_item_guanzhu_press);
                            btn1.setText("关注");
                        }
                        else {
                            btnFollow.setBackgroundResource(R.drawable.icon_item_yiguanzhu_press);
                            btn1.setText("取消");
                        }
                        click=true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if(click && event.getX()>0 && event.getX()<v.getWidth() &&
                                event.getY()>0 && event.getY()<v.getHeight()) {
                            Turn("FollowShop");
                        }
                        if(!isFollowShop) {
                            btnFollow.setBackgroundResource(R.drawable.icon_item_guanzhu);
                            btn1.setText("关注");
                        }
                        else {
                            btnFollow.setBackgroundResource(R.drawable.icon_item_yiguanzhu);
                            btn1.setText("取消");
                        }
                        click = false;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if(click) {
                            click = false;
                            if(!isFollowShop) {
                                btnFollow.setBackgroundResource(R.drawable.icon_item_guanzhu);
                                btn1.setText("关注");
                            }
                            else {
                                btnFollow.setBackgroundResource(R.drawable.icon_item_yiguanzhu);
                                btn1.setText("取消");
                            }
                        }
                        break;
                }
                return true;
            }
        });

        btn1.setOnTouchListener(new View.OnTouchListener() {
            boolean click = false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundResource(R.color.eghitygray);
                        if(!isFollowShop) {
                            btn1.setText("关注");
                        }
                        else {
                            btn1.setText("取消");
                        }
                        click = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                                event.getY() > 0 && event.getY() < v.getHeight()) {
                            Turn("FollowShop");
                        }
                        if(!isFollowShop) {
                            btnFollow.setBackgroundResource(R.drawable.icon_item_guanzhu);
                            btn1.setText("关注");
                        }
                        else {
                            btnFollow.setBackgroundResource(R.drawable.icon_item_yiguanzhu);
                            btn1.setText("取消");
                        }
                        click = false;
                        v.setBackgroundResource(R.color.white);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        click = false;
                        v.setBackgroundResource(R.color.white);
                        if(!isFollowShop) {
                            btnFollow.setBackgroundResource(R.drawable.icon_item_guanzhu);
                            btn1.setText("关注");
                        }
                        else {
                            btnFollow.setBackgroundResource(R.drawable.icon_item_yiguanzhu);
                            btn1.setText("取消");
                        }
                        break;
                }
                return true;
            }
        });
    }
    private void initTab(){
        //关注数
        TextView followText=(TextView)findViewById(R.id.shop_num_follow);
        //被浏览
        TextView visitsText=(TextView)findViewById(R.id.shop_num_visits);
        //推荐数
        TextView recommendText=(TextView)findViewById(R.id.shop_num_recommend);

        String str;
        str=m_shop.m_followed+"";
        if(followText!=null) followText.setText(str);
        str=m_shop.m_visited+"";
        if(visitsText!=null)visitsText.setText(str);
        str=m_shop.m_recommend+"";
        if(recommendText!=null)recommendText.setText(str);
    }
    void initRow(final int row,List<GoodItem> m_list){
        FrameLayout layout=new FrameLayout(this);
        LinearLayout.LayoutParams P = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,232);
        P.leftMargin=30;
        P.rightMargin=30;
        m_itemLayout.addView(layout,P);

        FrameLayout picListItems=new FrameLayout(m_parent);
        //Item
        for (int i = 0; i < 3; i++) {
            LinearLayout picListItem = new LinearLayout(m_parent);
            picListItem.setOrientation(LinearLayout.VERTICAL);
            if (i < m_list.size()) {
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
                                    Turn(row, index);
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
        //Layout
        FrameLayout.LayoutParams Params7 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        Params7.gravity =Gravity.TOP;
        Params7.topMargin=13;
        layout.addView(picListItems,Params7);
    }
    void initBottom(){
        TextView btn2=(TextView)findViewById(R.id.shop_other_bottom_share);
        ImageView btn3=(ImageView)findViewById(R.id.shop_other_bottom_phone);
        ImageView btn4=(ImageView)findViewById(R.id.shop_other_bottom_chat);
        assert btn2 != null;
        btn2.setBackgroundResource(R.color.white);
        assert btn3 != null;
        btn3.setBackgroundResource(R.drawable.icon_item_chat2);
        assert btn4 != null;
        btn4.setBackgroundResource(R.drawable.icon_item_phone2);

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
                            m_index=m_data.NewChat(m_user);
                            if(m_index>=0) {
                                ChatItems chats2=m_data.m_chatList.get(m_index);
                                DatabaseHelper.Act.UpdateData("chat_" + m_data.m_user.m_user, "state", "1", "user=? and state=?", new String[]{chats2.toUser, "0"});
                                m_data.m_chatList.get(m_index).SetRead();
                                Intent intent2=new Intent(ShopOther.this,InfoChat.class);
                                intent2.putExtra("id", "ITEM_DETAILS");
                                intent2.putExtra("name",chats2.toName);
                                intent2.putExtra("chat", chats2);
                                intent2.putExtra("toHead",DatabaseHelper.Act.GetHead(chats2.toUser));
                                intent2.putExtra("fromHead",DatabaseHelper.Act.GetHead(m_data.m_user.m_user));
                                startActivityForResult(intent2, 1);
                            }
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