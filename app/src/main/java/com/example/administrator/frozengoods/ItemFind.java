package com.example.administrator.frozengoods;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class ItemFind extends AppCompatActivity {
    final ItemFind m_parent=this;
    private Data m_data;
    private int screenWidth;
    private View m_back_over;
    private LinearLayout m_itemLayout;
    private int m_index1;
    private int m_index2;
    private int m_index3;
    private TextView m_text1,m_text2,m_text3;
    private ArrayList<GoodItem> m_List;
    private EditText m_edit;
    private  String[] index1String = {"全部","家畜","家禽","加工品","海鲜","其他"};
    private  String[][] index2String = {{"全部"," "," "," "," "," "},
            {"全部","腿部","胸部","内脏","尾巴","其他"},
            {"全部","腿部","翅部","内脏","胸部","其他"},
            {"全部","肉丸","熟食","果蔬","小吃","其他"},
            {"全部","虾类","鱼类","贝类","海带","其他"},
            {"全部","灌肠","水发","调料","魔芋","其他"}};
    private  String[] index3String = {"距离↗","时间↘","价格口"};
    private  int[] indexID = {R.id.index_item1_1,R.id.index_item1_2,R.id.index_item1_3,R.id.index_item1_4,R.id.index_item1_5,R.id.index_item1_6};
    private  int[] textID = {R.id.index_item1_1_text,R.id.index_item1_2_text,R.id.index_item1_3_text,R.id.index_item1_4_text,R.id.index_item1_5_text,R.id.index_item1_6_text};
    private  int[] lineID = {R.id.index_item1_1_line,R.id.index_item1_2_line,R.id.index_item1_3_line,R.id.index_item1_4_line,R.id.index_item1_5_line,R.id.index_item1_6_line};
    //Create
    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_find);
        String index,id,context;
        Intent intent = getIntent();
        if(intent!=null) {
            m_data = intent.getParcelableExtra("data");
            id=intent.getStringExtra("id");
            index=intent.getStringExtra("index");
            context=intent.getStringExtra("context");
        }
        else {
            finish();
            return;
        }
        FrameLayout m_back = (FrameLayout) findViewById(R.id.btn_back);
        m_back_over = findViewById(R.id.btn_back_over);
        m_itemLayout = (LinearLayout) findViewById(R.id.layout_content);
        m_edit = (EditText) findViewById(R.id.item_edit_find);
        if(!context.isEmpty())
            m_edit.setText(context);

        ScrollView scroll=(ScrollView)findViewById(R.id.item_scroll);
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
        switch (id){
            case "HOME_CLASS":
                m_index1=Integer.parseInt(index)+1;
                break;
            default:
                m_index1 = 0;
                break;
        }
        initView();
    }
    @Override protected void onDestroy(){
        m_data=null;
        m_back_over=null;
        m_itemLayout=null;
        m_text1=null;
        m_text2=null;
        m_text3=null;
        m_List.clear();
        m_List=null;
        m_edit=null;
        index1String=null;
        index2String=null;
        index3String=null;
        indexID=null;
        textID=null;
        lineID=null;
        super.onDestroy();
        setContentView(R.layout.view_null);
    }

    //Turn
    @Override public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("data", m_data);
        setResult(RESULT_OK, intent);
        finish();
    }
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data==null)
            return ;
        if(resultCode==RESULT_OK) {
            switch(requestCode) {
                case 0: {
                    String str1 = data.getStringExtra("user");
                    if (m_data.LogIn(str1)) {
                        Message("登录成功");
                    }
                    break;
                }
                case 1:
                    m_data.m_chatList=data.getParcelableArrayListExtra("chats");
                    m_data.m_user.getPic();
                    break;
                case 2: {
                    m_data.m_chatList = data.getParcelableArrayListExtra("chats");
                    break;
                }
            }
            ImageView red=(ImageView)findViewById(R.id.item_find_news_point);
            if(m_data.GetUnReadNum()>0) {
                assert red != null;
                red.setVisibility(View.VISIBLE);
            }
            else {
                assert red != null;
                red.setVisibility(View.GONE);
            }
        }
    }
    public void Turn(String label) {
        switch(label){
            case "News":
                if (m_data.m_user != null){
                    Intent intent = new Intent(ItemFind.this, InfoNews.class);
                    intent.putExtra("data", m_data);
                    intent.putExtra("id", "ITEM_FIND");
                    startActivityForResult(intent, 1);
                }
                else{
                    Intent intent = new Intent(ItemFind.this, InfoUser.class);
                    intent.putExtra("id", "ITEM_FIND");
                    startActivityForResult(intent, 0);
                }
                break;
            case "Login":
                Intent intent = new Intent(ItemFind.this, InfoUser.class);
                intent.putExtra("id", "ITEM_FIND");
                startActivityForResult(intent, 0);
                break;
        }
    }
    public void Turn(int index) {
        Intent intent = new Intent(ItemFind.this,ItemDetails.class);
        intent.putExtra("data", m_data);
        intent.putExtra("id","ITEM_FIND");
        m_List.get(index).clearPic();
        intent.putExtra("item", m_List.get(index));
        startActivityForResult(intent,2);
    }

    //Update
    public void Update(){
        m_itemLayout.removeAllViews();
        String limit=m_edit.getText().toString();
        //layout
        String str=" where";
        List<String> strList=new ArrayList<>();
        if(m_data.m_user!=null && !m_data.m_user.m_city.isEmpty()) {
            str += " city=? and";
            strList.add(m_data.m_user.m_city);
        }
        if(m_index1!=0){
            str+=" class1=?";
            strList.add(index1String[m_index1]);
            if(m_index2!=0){
                str+=" and class2=?";
                strList.add(index2String[m_index1][m_index2]);
            }
            str+=" and";
        }
        if(m_text3.getText().toString().equals("价格口")){
            str+=" price=?";
            strList.add("-1");
        }
        else {
            str+=" price>?";
            strList.add("0");
        }
        if(!limit.isEmpty()){
            str+=" and (shop like '%"+limit+"%' or name like '%"+limit+"%')";
        }
        if(m_text2.getText().toString().equals("时间↘"))
            str+=" ORDER BY district,id ASC ";
        else
            str+=" ORDER BY district,id DESC ";


        String[] array=new String[strList.size()];
        for(int i=0;i<strList.size();i++){
            array[i]=strList.get(i);
        }
        m_List= DatabaseHelper.Act.GetGoodItem(str,array);
        if(m_List!=null) {
            DatabaseHelper.Act.GetGoodItemPic(m_List,str, array);
            for (int i = 0; i < m_List.size(); i++)
                initItem(i);
        }
    }

    //Layout
    void initView(){
        DisplayMetrics outMetrics = new DisplayMetrics();//获取屏幕的宽度
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        screenWidth = outMetrics.widthPixels;
        initIndex1();
        initIndex2();
        initIndex3 ();
        Update();
        //news
        ImageView red=(ImageView)findViewById(R.id.item_find_news_point);
        if(m_data.GetUnReadNum()>0) {
            assert red != null;
            red.setVisibility(View.VISIBLE);
        }
        else {
            assert red != null;
            red.setVisibility(View.GONE);
        }
        FrameLayout f=(FrameLayout)findViewById(R.id.item_find_news);
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
                            Turn("News");
                        }
                        break;
                }
                return true;
            }
        });
    }
    private void initIndex1 (){
        LinearLayout layout = (LinearLayout)findViewById(R.id.item_layout_index1);
        assert layout != null;
        layout.setGravity(Gravity.CENTER);
        for(int i=0;i<index1String.length;i++) {
            FrameLayout indexItemLayout = new FrameLayout(this);
            indexItemLayout.setId(indexID[i]);
            indexItemLayout.setOnClickListener(new ItemFind.Tab1OnClickListener(i));
            //文字
            TextView indexText = new TextView(this);
            indexText.setText(index1String[i]);
            indexText.setId(textID[i]);
            indexText.setTextSize(TypedValue.COMPLEX_UNIT_PX,28);
            indexText.setTextColor(ContextCompat.getColor(this,R.color.thirtygray));
            if(i==m_index1)
                indexText.setTextColor(ContextCompat.getColor(this,R.color.textblue));
            FrameLayout.LayoutParams p1 = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            p1.gravity =Gravity.CENTER;
            indexItemLayout.addView(indexText,p1);
            //下划线
            View line=new View(this);
            line.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
            if(i==m_index1)
                line.setBackgroundColor(ContextCompat.getColor(this,R.color.textblue));
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
                    TextView t1=((TextView) findViewById(textID[i]));
                    assert t1 != null;
                    t1.setTextColor(ContextCompat.getColor(m_parent,R.color.thirtygray));
                    View l1=(findViewById(lineID[i]));
                    assert l1 != null;
                    l1.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.white));
                }
                else {
                    TextView t1=((TextView) findViewById(textID[i]));
                    assert t1 != null;
                    t1.setTextColor(ContextCompat.getColor(m_parent,R.color.textblue));
                    View l1=(findViewById(lineID[i]));
                    assert l1 != null;
                    l1.setBackgroundColor(ContextCompat.getColor(m_parent,R.color.textblue));
                }
            }
            Update();
        }
    }
    private void initIndex2 (){
        LinearLayout layout = (LinearLayout)findViewById(R.id.item_layout_index2);
        assert layout != null;
        layout.removeAllViews();
        layout.setGravity(Gravity.CENTER);
        for(int i=0;i<index2String[m_index1].length;i++) {
            FrameLayout indexItemLayout = new FrameLayout(this);
            indexItemLayout.setOnClickListener(new ItemFind.Tab2OnClickListener(i));
            //文字
            TextView indexText = new TextView(this);
            indexText.setText(index2String[m_index1][i]);

            indexText.setId(getResources().getIdentifier("index_item2_"+(i+1), "id", getPackageName()));
            indexText.setTextSize(TypedValue.COMPLEX_UNIT_PX,28);
            indexText.setTextColor(Color.parseColor("#b2b2b2"));
            if(i==m_index2)
                indexText.setTextColor(ContextCompat.getColor(this,R.color.textblue));
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
                        TextView t1=(TextView) findViewById(getResources().getIdentifier("index_item2_" + (i + 1), "id", getPackageName()));
                        assert t1 != null;
                        t1.setTextColor(Color.parseColor("#b2b2b2"));
                    } else {
                        TextView t2=(TextView) findViewById(getResources().getIdentifier("index_item2_" + (i + 1), "id", getPackageName()));
                        assert t2 != null;
                        t2.setTextColor(ContextCompat.getColor(m_parent,R.color.textblue));
                    }
                }
            }
            Update();
        }
    }
    private void initIndex3 (){
        LinearLayout layout = (LinearLayout)findViewById(R.id.item_layout_index3);
        assert layout != null;
        layout.removeAllViews();
        layout.setGravity(Gravity.CENTER);
        m_text1=new TextView(this);
        m_text2=new TextView(this);
        m_text3=new TextView(this);
        for(int i=0;i<index3String.length;i++) {
            FrameLayout indexItemLayout = new FrameLayout(this);
            indexItemLayout.setOnClickListener(new ItemFind.Tab3OnClickListener(i));
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
                indexText.setTextColor(ContextCompat.getColor(this,R.color.textblue));
            else
                indexText.setTextColor(ContextCompat.getColor(this,R.color.thirtygray));
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
    public void initItem (final int index){
        final GoodItem item;
        item=m_List.get(index);
        FrameLayout layout = new FrameLayout(this); //总框架
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
                            if(m_data.m_user!=null)
                                Turn(index);
                            else
                                Turn("Login");
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

        LinearLayout layoutPic=new LinearLayout(this);//图片
        LinearLayout layoutText=new LinearLayout(this);//文字
        layoutPic.setOrientation(LinearLayout.HORIZONTAL);
        layoutPic.setGravity(Gravity.CENTER);
        layoutText.setOrientation(LinearLayout.VERTICAL);
        //图片1
        ImageView titleImage=new ImageView(this);
        titleImage.setImageDrawable(Widget.ByteToDrawable(item.pic1));
        titleImage.setScaleType(ImageView.ScaleType.FIT_XY);
        layoutPic.addView(titleImage,158,158);
        //名称
        TextView titleText=new TextView(this);
        titleText.setIncludeFontPadding(false);
        titleText.setText(item.name);
        titleText.setTextColor(ContextCompat.getColor(this,R.color.tengray));
        titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX,27);
        LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutText.addView(titleText,p1);
        //price
        TextView picListItemPrice = new TextView(this);
        picListItemPrice.setIncludeFontPadding(false);

        if(item.price>0)
            picListItemPrice.setText("价格：￥" + item.price + "/"+item.unit);
        else
            picListItemPrice.setText("价格：面谈");
        picListItemPrice.setTextColor(ContextCompat.getColor(this,R.color.thirtygray));
        picListItemPrice.setTextSize(TypedValue.COMPLEX_UNIT_PX,25);
        LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        p2.topMargin=15;
        layoutText.addView(picListItemPrice, p2);
        //num
        TextView numText = new TextView(this);
        numText.setIncludeFontPadding(false);
        numText.setText("数量：" + item.num +item.unit);
        numText.setTextColor(ContextCompat.getColor(this,R.color.thirtygray));
        numText.setTextSize(TypedValue.COMPLEX_UNIT_PX,25);
        LinearLayout.LayoutParams p3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        p3.topMargin=14;
        layoutText.addView(numText, p3);
        //地址
        TextView adText=new TextView(this);
        adText.setIncludeFontPadding(false);
        adText.setText("地址："+item.city+item.district);
        adText.setTextColor(ContextCompat.getColor(this,R.color.thirtygray));
        adText.setTextSize(TypedValue.COMPLEX_UNIT_PX,25);
        LinearLayout.LayoutParams p4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        p4.topMargin=14;
        layoutText.addView(adText,p4);
        //分割线
        View line=new View(this);
        line.setBackgroundColor(ContextCompat.getColor(this,R.color.nintygray));
        FrameLayout.LayoutParams p5 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1);
        p5.gravity =Gravity.BOTTOM;
        p5.leftMargin=30;
        p5.rightMargin=30;
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
    void Message(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
