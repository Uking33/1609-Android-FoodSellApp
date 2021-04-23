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

import java.util.ArrayList;
import java.util.List;

public class Shop  extends Fragment {
    View m_this;
    private MainActivity m_parent;
    private LinearLayout m_itemLayout;
    ArrayList<GoodItem> m_onList, m_offList;

    //Create
    @Override public void onAttach(Context context) {
        m_parent = (MainActivity) context;
        super.onAttach(context);
    }
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_this = inflater.inflate(R.layout.shop, container, false);
        m_itemLayout = (LinearLayout) m_this.findViewById(R.id.shop_layout_others);
        if (m_parent.m_data.m_user != null)
            Login();
        else
            Logout();
        return m_this;
    }

    //Turn
    public void Login() {
        if (m_parent.m_data.m_user != null && !m_parent.m_data.m_user.m_shop.m_shop.isEmpty()) {
            m_this.findViewById(R.id.shop_layout_tab).setVisibility(View.VISIBLE);
            initHead((FrameLayout) m_this.findViewById(R.id.shop_layout_top), m_parent.m_data.m_user);
            initTab(m_parent.m_data.m_user);

            m_onList=DatabaseHelper.Act.GetGoodItem("where user=? and state=?  ORDER BY id DESC", new String[]{m_parent.m_data.m_user.m_user, "On"});
            m_offList=DatabaseHelper.Act.GetGoodItem("where user=? and state=?  ORDER BY id DESC ", new String[]{m_parent.m_data.m_user.m_user, "Off"});
            if (m_onList == null) m_onList = new ArrayList<>();
            if (m_offList == null) m_offList = new ArrayList<>();
            DatabaseHelper.Act.GetGoodItemPic(m_onList,"where user=? and state=?  ORDER BY id DESC LIMIT 3", new String[]{m_parent.m_data.m_user.m_user, "On"});
            DatabaseHelper.Act.GetGoodItemPic(m_offList,"where user=? and state=?  ORDER BY id DESC LIMIT 3", new String[]{m_parent.m_data.m_user.m_user, "Off"});
            initItemList((FrameLayout) m_this.findViewById(R.id.shop_layout_shelf), "上架冻品", "更多上架冻品>", m_onList);
            initItemList((FrameLayout) m_this.findViewById(R.id.shop_layout_unshelf), "下架冻品", "更多下架冻品>", m_offList);

            initTextItem(m_itemLayout, R.id.btn_shop_last, "最近联系", 74, 30);
            initTextItem(m_itemLayout, R.id.btn_shop_setting, "店铺设置", 74, 30);
            initTextItem(m_itemLayout, R.id.btn_shop_news, "分享我的店铺", 74, 30);
        }
    }
    public void Logout() {
        ((FrameLayout)m_this.findViewById(R.id.shop_layout_top)).removeAllViews();
        m_itemLayout.removeAllViews();
        (m_this.findViewById(R.id.shop_layout_top)).setBackgroundResource(R.color.white);
        ((FrameLayout) m_this.findViewById(R.id.shop_layout_shelf)).removeAllViews();
        ((FrameLayout) m_this.findViewById(R.id.shop_layout_unshelf)).removeAllViews();
        m_this.findViewById(R.id.shop_layout_tab).setVisibility(View.GONE);
    }
    void Turn(String label) {
        switch (label) {
            case "NEW":
                Intent intent0 = m_parent.Turn1(ShopSetting.class, "SHOP_NEW");
                intent0.putExtra("user", m_parent.m_data.m_user);
                m_parent.Turn2(intent0, 40);
                break;
            case "最近联系":
                Intent intent1 = m_parent.Turn1(InfoNews.class, "SHOP_RECENT_NEWS");
                m_parent.m_data.m_user.clearPic();
                intent1.putExtra("data", m_parent.m_data);
                m_parent.Turn2(intent1, 41);
                break;
            case "Head":
            case "店铺设置":
                Intent intent2 = m_parent.Turn1(ShopSetting.class, "SHOP_SETTING");
                intent2.putExtra("user", m_parent.m_data.m_user);
                m_parent.Turn2(intent2, 42);
                break;
            case "分享我的店铺":
                UShareDialog m_share_dialog = new UShareDialog(m_parent, new UShareDialog.OnCustomDialogListener() {
                    @Override
                    public void back(String choose) {
                        Turn("Share");
                    }
                });
                m_share_dialog.show();
                break;
            default:
                m_parent.Turn(UnKnow.class,label);
                break;
        }
    }
    void Turn(String label, int index) {
        Intent intent=null;
        switch (label) {
            case "上架冻品":
                intent = m_parent.Turn1(ShopSelf.class, "SHOP_ON_SELF");
                break;
            case "下架冻品":
                intent = m_parent.Turn1(ShopSelf.class, "SHOP_OFF_SELF");
                break;
            default:
                m_parent.Turn(UnKnow.class, "SHOP_ITEM");
                break;
        }
        switch (label) {
            case "上架冻品":
            case "下架冻品":
                if(intent!=null) {
                    intent.putExtra("index", index);
                    intent.putExtra("user", m_parent.m_data.m_user);
                    for(int i=0;i<(m_onList.size()<3?m_onList.size():3);i++) {
                        m_onList.get(i).pic1 = null;
                        m_onList.get(i).pic2 = null;
                        m_onList.get(i).pic3 = null;
                    }
                    for(int i=0;i<(m_offList.size()<3?m_offList.size():3);i++) {
                        m_offList.get(i).pic1 = null;
                        m_offList.get(i).pic2 = null;
                        m_offList.get(i).pic3 = null;
                    }
                    intent.putExtra("onList", m_onList);
                    intent.putExtra("offList", m_offList);
                    m_parent.Turn2(intent, 43);
                }
                break;
        }
    }

    //Update
    public void Update(String type, Intent data) {
        if (m_parent.m_data.m_user == null)
            return;
        switch (type) {
            case "Shop":
                if (m_parent.m_data.m_user.m_shop.m_shop.isEmpty())
                    return;
                ShopItem shop = m_parent.m_data.m_user.m_shop;
                titleImage.setImageDrawable(Widget.ByteToDrawable(shop.m_logo));
                nameText.setText(shop.m_shop);
                detailText.setText(shop.m_ad_details);
                adText.setText(shop.m_adText);
                break;
            case "New":
                if (m_parent.m_data.m_user.m_shop.m_shop.isEmpty())
                    return;
                if (m_parent.m_data.GetUnReadNum() > 0)
                    newsPointImage.setVisibility(View.VISIBLE);
                else
                    newsPointImage.setVisibility(View.GONE);
                break;
            case "Follow":
                initTab(m_parent.m_data.m_user);
                break;
            case "Items":
                if (data != null) {
                    m_onList=data.getParcelableArrayListExtra("onList");
                    m_offList=data.getParcelableArrayListExtra("offList");
                    DatabaseHelper.Act.GetGoodItemPic(m_onList,"where user=? and state=?  ORDER BY id DESC LIMIT 3", new String[]{m_parent.m_data.m_user.m_user, "On"});
                    DatabaseHelper.Act.GetGoodItemPic(m_offList,"where user=? and state=?  ORDER BY id DESC LIMIT 3", new String[]{m_parent.m_data.m_user.m_user, "Off"});
                    ((FrameLayout) m_this.findViewById(R.id.shop_layout_shelf)).removeAllViews();
                    ((FrameLayout) m_this.findViewById(R.id.shop_layout_unshelf)).removeAllViews();
                    initItemList((FrameLayout) m_this.findViewById(R.id.shop_layout_shelf), "上架冻品", "更多上架冻品>", m_onList);
                    initItemList((FrameLayout) m_this.findViewById(R.id.shop_layout_unshelf), "下架冻品", "更多下架冻品>", m_offList);
                }
                break;
        }

    }

    //Layout
    ImageView newsPointImage;
    ImageView titleImage;
    TextView nameText;
    TextView detailText;
    TextView adText;
    public void initHead(FrameLayout layout, UserItem user) {
        if (user == null)
            return;
        ShopItem item = user.m_shop;
        //背景
        layout.setBackgroundResource(R.drawable.default_background);
        layout.setOnTouchListener(new View.OnTouchListener() {
            boolean click = false;

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        click = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                                event.getY() > 0 && event.getY() < v.getHeight()) {
                            Turn("Head");
                        }
                        break;
                }
                return true;
            }
        });
        //图片
        titleImage = new ImageView(m_parent);
        titleImage.setImageDrawable(Widget.ByteToDrawable(item.m_logo));
        FrameLayout.LayoutParams Params1 = new FrameLayout.LayoutParams(152, 152);
        Params1.leftMargin = 30;
        Params1.topMargin = 31;
        layout.addView(titleImage, Params1);
        //店名
        nameText = new TextView(m_parent);
        nameText.setIncludeFontPadding(false);
        nameText.setText(item.m_shop);
        nameText.setTextColor(ContextCompat.getColor(m_parent, R.color.white));
        nameText.setTextSize(TypedValue.COMPLEX_UNIT_PX, 36);
        FrameLayout.LayoutParams Params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Params2.leftMargin = 212;
        Params2.topMargin = 43;
        layout.addView(nameText, Params2);
        //标语
        detailText = new TextView(m_parent);
        detailText.setIncludeFontPadding(false);
        detailText.setText(item.m_adText);
        detailText.setTextColor(ContextCompat.getColor(m_parent, R.color.white));
        detailText.setTextSize(TypedValue.COMPLEX_UNIT_PX, 24);
        FrameLayout.LayoutParams Params3 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Params3.leftMargin = 212;
        Params3.topMargin = 105;
        layout.addView(detailText, Params3);
        //地址标
        ImageView adImage = new ImageView(m_parent);
        adImage.setImageDrawable(ContextCompat.getDrawable(m_parent, R.drawable.icon_ad));
        FrameLayout.LayoutParams Params4 = new FrameLayout.LayoutParams(21, 23);
        Params4.leftMargin = 212;
        Params4.topMargin = 155;
        layout.addView(adImage, Params4);
        //地址
        adText = new TextView(m_parent);
        adText.setIncludeFontPadding(false);
        adText.setText(item.m_ad_province + item.m_ad_city + item.m_ad_district);
        adText.setTextColor(ContextCompat.getColor(m_parent, R.color.white));
        adText.setTextSize(TypedValue.COMPLEX_UNIT_PX, 24);
        FrameLayout.LayoutParams Params5 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Params5.leftMargin = 237;
        Params5.topMargin = 153;
        layout.addView(adText, Params5);
        //通知
        FrameLayout news = new FrameLayout(m_parent);
        //通知图标
        ImageView newsImage = new ImageView(m_parent);
        newsImage.setImageDrawable(ContextCompat.getDrawable(m_parent, R.drawable.icon_news));
        FrameLayout.LayoutParams Params10 = new FrameLayout.LayoutParams(28, 30);
        Params10.gravity = Gravity.END;
        Params10.rightMargin = 42;
        Params10.topMargin = 50;
        news.addView(newsImage, Params10);
        //通知红点
        newsPointImage = new ImageView(m_parent);
        newsPointImage.setImageDrawable(ContextCompat.getDrawable(m_parent, R.drawable.icon_redpoint));
        FrameLayout.LayoutParams Params11 = new FrameLayout.LayoutParams(13, 13);
        Params11.gravity = Gravity.END;
        Params11.rightMargin = 39;
        Params11.topMargin = 46;
        news.addView(newsPointImage, Params11);
        //通知
        FrameLayout.LayoutParams Params12 = new FrameLayout.LayoutParams(106, 106);
        Params12.gravity = Gravity.END;
        layout.addView(news, Params12);
        if (m_parent.m_data.GetUnReadNum() > 0)
            newsPointImage.setVisibility(View.VISIBLE);
        else
            newsPointImage.setVisibility(View.GONE);
        news.setOnTouchListener(new View.OnTouchListener() {
            boolean click = false;

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        click = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                                event.getY() > 0 && event.getY() < v.getHeight()) {
                            if (m_parent.m_data.m_user != null){
                                Intent intent1 = m_parent.Turn1(InfoNews.class, "SHOP_NEWS");
                                m_parent.m_data.m_user.clearPic();
                                intent1.putExtra("data", m_parent.m_data);
                                m_parent.Turn2(intent1, 41);
                            }
                        }
                        break;
                }
                return true;
            }
        });

    }
    private void initTab(UserItem user) {
        DatabaseHelper.Act.UpdateFollow(m_parent.m_data.m_user);
        //关注数
        TextView followText = (TextView) m_this.findViewById(R.id.shop_num_follow);
        //被浏览
        TextView visitsText = (TextView) m_this.findViewById(R.id.shop_num_visits);
        //推荐数
        TextView recommendText = (TextView) m_this.findViewById(R.id.shop_num_recommend);

        if (user == null || user.m_shop == null)
            return;
        ShopItem item = user.m_shop;
        String str;
        str = item.m_followed + "";
        if (followText != null) followText.setText(str);
        str = item.m_visited + "";
        if (visitsText != null) visitsText.setText(str);
        str = item.m_recommend + "";
        if (recommendText != null) recommendText.setText(str);
    }
    private void initItemList(FrameLayout layout, final String title, String tip, List<GoodItem> items) {
        FrameLayout picListText = new FrameLayout(m_parent);
        FrameLayout picListItems = new FrameLayout(m_parent);

        layout.setOnTouchListener(new View.OnTouchListener() {
            boolean click = false;

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        click = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                                event.getY() > 0 && event.getY() < v.getHeight()) {
                            Turn(title,-1);
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
        //标题
        TextView titleText = new TextView(m_parent);
        titleText.setIncludeFontPadding(false);
        titleText.setText(title);
        titleText.setTextColor(ContextCompat.getColor(m_parent, R.color.textblue));
        titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);
        FrameLayout.LayoutParams Params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        picListText.addView(titleText, Params2);
        //总数
        TextView numText = new TextView(m_parent);
        numText.setIncludeFontPadding(false);
        String str="冻品数量：" + String.valueOf(items.size());
        numText.setText(str);
        numText.setTextColor(ContextCompat.getColor(m_parent, R.color.fiftygray));
        numText.setTextSize(TypedValue.COMPLEX_UNIT_PX, 24);
        FrameLayout.LayoutParams Params9 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Params9.gravity = Gravity.CENTER_VERTICAL;
        Params9.leftMargin = 153;
        picListText.addView(numText, Params9);
        //tip
        TextView tipText = new TextView(m_parent);
        tipText.setIncludeFontPadding(false);
        tipText.setText(tip);
        tipText.setTextColor(ContextCompat.getColor(m_parent, R.color.eghitygray));
        tipText.setTextSize(TypedValue.COMPLEX_UNIT_PX, 18);
        FrameLayout.LayoutParams Params3 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Params3.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        picListText.addView(tipText, Params3);
        //Item
        if (items.size() > 0) {
            for (int i = 0; i < 3; i++) {
                LinearLayout picListItem = new LinearLayout(m_parent);
                picListItem.setOrientation(LinearLayout.VERTICAL);
                if (i + 1 <= items.size()) {
                    ImageView picListItemBtn = new ImageView(m_parent);
                    picListItemBtn.setImageDrawable(Widget.ByteToDrawable(items.get(i).pic1));
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
                    if (items.get(i).name.length() <= 7)
                        picListItemText.setText(items.get(i).name);
                    else {
                        str = items.get(i).name;
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
                    if (items.get(i).price > 0)
                        picListItemPrice.setText("￥" + items.get(i).price + "/" + items.get(i).unit);
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
        } else {
            TextView t1 = new TextView(m_parent);
            t1.setIncludeFontPadding(false);
            t1.setText("- 暂无商品信息 -");
            t1.setTextColor(ContextCompat.getColor(m_parent, R.color.seventygray));
            t1.setTextSize(TypedValue.COMPLEX_UNIT_PX, 25);
            t1.setGravity(Gravity.CENTER);
            FrameLayout.LayoutParams p1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            p1.gravity = Gravity.CENTER;
            picListItems.addView(t1, p1);
        }
        //Layout
        FrameLayout.LayoutParams Params6 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Params6.gravity = Gravity.TOP;
        Params6.topMargin = 24;
        layout.addView(picListText, Params6);
        FrameLayout.LayoutParams Params7 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        Params7.gravity = Gravity.TOP;
        Params7.topMargin = 75;
        layout.addView(picListItems, Params7);
        //分割线
        View line = new View(m_parent);
        line.setBackgroundColor(ContextCompat.getColor(m_parent, R.color.nintygray));
        FrameLayout.LayoutParams Params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        Params1.gravity = Gravity.BOTTOM;
        layout.addView(line, Params1);
    }
    public void initTextItem(LinearLayout itemLayout, final int id, final String title, int height, int size) {//单一按钮
        FrameLayout layout = new FrameLayout(m_parent);
        layout.setId(id);
        layout.setOnTouchListener(new View.OnTouchListener() {
            boolean click = false;

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundColor(Color.parseColor("#E6E6E6"));
                        click = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                                event.getY() > 0 && event.getY() < v.getHeight()) {
                            Turn(title);
                        }
                        v.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if (click) {
                            click = false;
                            v.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        }
                        break;
                }
                return true;
            }
        });
        //left
        TextView text = new TextView(m_parent);
        text.setIncludeFontPadding(false);
        text.setText(title);
        text.setTextColor(ContextCompat.getColor(m_parent, R.color.textblue));
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        FrameLayout.LayoutParams Params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Params1.gravity = Gravity.START;
        Params1.leftMargin = 30 * (size / 30);
        Params1.topMargin = (height - size) / 2;
        layout.addView(text, Params1);
        //right
        ImageView rightDrop = new ImageView(m_parent);
        rightDrop.setImageDrawable(ContextCompat.getDrawable(m_parent, R.drawable.icon_drop1_right_blue));
        FrameLayout.LayoutParams Params2 = new FrameLayout.LayoutParams(14 * (size / 30), 24 * (size / 30));
        Params2.gravity = Gravity.END;
        Params2.rightMargin = 30 * (size / 30);
        Params2.topMargin = (height - 24 * (size / 30)) / 2;
        layout.addView(rightDrop, Params2);
        //line
        View line = new View(m_parent);
        line.setBackgroundColor(ContextCompat.getColor(m_parent, R.color.nintygray));
        FrameLayout.LayoutParams Params3 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        Params3.gravity = Gravity.BOTTOM;
        Params3.leftMargin = 30 * (size / 30);
        Params3.rightMargin = 30 * (size / 30);
        layout.addView(line, Params3);
        //layout
        FrameLayout.LayoutParams Params4 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        itemLayout.addView(layout, Params4);
    }

}
