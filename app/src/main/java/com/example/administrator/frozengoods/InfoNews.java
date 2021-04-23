package com.example.administrator.frozengoods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InfoNews extends UActivity {
    int m_state;
    int m_index;
    private Data m_data;

    //Create
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState,false);
        Intent intent = getIntent();
        m_data=intent.getParcelableExtra("data");
        m_data.m_user.getPic();
        m_index=-1;
        switch (m_id){
            case "SHOP_RECENT_NEWS":
                m_state=2;
                List1();
                break;
            default:
                m_state=1;
                All();
        }
    }
    @Override protected void onDestroy(){
        m_data=null;
        super.onDestroy();
        setContentView(R.layout.view_null);
    }

    //Turn
    @Override public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("chats", m_data.m_chatList);
        setResult(RESULT_OK, intent);
        finish();
    }
    @Override public void Back(){
        if (m_tab.equals("List1") || m_tab.equals("List2") || m_tab.equals("List3")) {
            if(m_state==1)
                All();
            else
                onBackPressed();
        }
        else if(m_tab.equals("All")){
            onBackPressed();
        }
    }
    @Override public void Turn(String label) {
        if (m_tab.equals("All")) {
            switch (label) {
                case "聊天窗":
                    List1();
                    break;
                case "系统消息":
                    List2();
                    break;
                case "推广":
                    List3();
                    break;
            }
        }
    }
    public void Turn(String user,String name,byte[] head) {
        if (m_tab.equals("List1") || m_tab.equals("List2") || m_tab.equals("List3")) {
            Intent intent = new Intent(this, InfoChat.class);
            intent.putExtra("name", name);
            intent.putExtra("toHead", head);
            intent.putExtra("fromHead", m_data.m_user.m_head);
            switch (m_tab) {
                case "List1":
                    for (int i = 0; i < m_data.m_chatList.size(); i++)
                        if (m_data.m_chatList.get(i).toUser.equals(user)) {
                            DatabaseHelper.Act.UpdateData("chat_" + m_data.m_user.m_user, "state", "1", "user=? and state=?", new String[]{user, "0"});
                            m_data.m_chatList.get(i).SetRead();
                            m_index = i;
                            intent.putExtra("chat", m_data.m_chatList.get(i));
                            startActivityForResult(intent, 1);
                            break;
                        }
                    break;
                case "List2":
                    for (int i = 0; i < m_data.m_chatList.size(); i++)
                        if (m_data.m_chatList.get(i).toUser.equals(user)) {
                            DatabaseHelper.Act.UpdateData("chat_" + m_data.m_user.m_user, "state", "1", "user=? and state=?", new String[]{user, "0"});
                            m_data.m_chatList.get(i).SetRead();
                            m_index = i;
                            intent.putExtra("chat", m_data.m_chatList.get(i));
                            startActivityForResult(intent, 2);
                            break;
                        }
                    break;
                case "List3":
                    m_index = -1;
                    startActivityForResult(intent, 3);
                    break;
            }
        }
    }
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data==null)
            return ;
        if(resultCode==RESULT_OK && m_index>=0) {
            ChatItems c= data.getParcelableExtra("chat");
            m_data.m_chatList.set(m_index,c);
        }
        switch (requestCode) {
            case 1:
                List1();
                break;
            case 2:
                List2();
                break;
            case 3:
                List3();
                break;
            default :
                break;
        }
    }

    //Layout
    void Clean() {
        if (m_itemLayout.getChildCount() > 0) m_itemLayout.removeAllViews();
    }
    void All() {
        m_tab = "All";
        m_title.setText("我的消息");
        Clean();

        int num1=0,num2=0,num3=0;
        for(int i=0;i<m_data.m_chatList.size();i++)
            if(m_data.m_chatList.get(i).toUser.equals("system"))
                num2=m_data.m_chatList.get(i).GetUnreadNum();
            else
                num1+=m_data.m_chatList.get(i).GetUnreadNum();
        initTextItem1("InfoNews", m_itemLayout, "聊天窗", num1, 100, 0);
        initTextItem1("InfoNews", m_itemLayout, "系统消息", num2, 100, 0);
        initTextItem1("InfoNews", m_itemLayout, "推广", num3, 100, 0);
    }
    void List1() {
        m_tab = "List1";
        m_title.setText("聊天窗");
        Clean();
        for(int i=0;i<m_data.m_chatList.size();i++)
            if(!m_data.m_chatList.get(i).toUser.equals("system")){
                ChatItems temp=m_data.m_chatList.get(i);
                initTextItem2(m_itemLayout, DatabaseHelper.Act.GetHead(temp.toUser), temp.toUser, temp.toName, temp.GetLastText(),temp.GetLastDate(),temp.GetUnreadNum(), 0);
            }
    }
    void List2() {
        m_tab = "List2";
        m_title.setText("系统消息");
        Clean();
        for(int i=0;i<m_data.m_chatList.size();i++)
            if(m_data.m_chatList.get(i).toUser.equals("system")) {
                ChatItems temp=m_data.m_chatList.get(i);
                initTextItem2(m_itemLayout, DatabaseHelper.Act.GetHead(temp.toUser),temp.toUser, temp.toName, temp.GetLastText(),temp.GetLastDate(),temp.GetUnreadNum(), 0);
                break;
            }
    }
    void List3() {
        m_tab = "List3";
        m_title.setText("推广");
        Clean();
    }

    //Item
    public UTextItem initTextItem1(String parentClass, LinearLayout itemLayout, String label, int num, int height, int topMargin){
        UTextItem uItem=new UTextItem(this,parentClass,label,num,30,25);
        uItem.addToView(itemLayout,height,topMargin);
        return uItem;
    }
    public FrameLayout initTextItem2(LinearLayout itemLayout, final byte[] head,final String user,final String name,String lastText,String lastDate, int num, int topMargin){
        //layout
        FrameLayout m_layout = new FrameLayout(this);
        m_layout.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
        //Head
        ImageView m_head=new ImageView(this);
        if(head!=null)
            m_head.setImageDrawable(Widget.ByteToDrawable(head));
        else
            m_head.setImageResource(R.drawable.default_logo);
        FrameLayout.LayoutParams Params1 = new FrameLayout.LayoutParams(93,93);
        Params1.gravity=Gravity.CENTER_VERTICAL;
        Params1.leftMargin=30;
        m_layout.addView(m_head,Params1);
        //Label
        TextView m_text=new TextView(this);
        m_text.setIncludeFontPadding(false);
        m_text.setText(name);
        m_text.setTextColor(ContextCompat.getColor(this,R.color.thirtygray));
        m_text.setTextSize(TypedValue.COMPLEX_UNIT_PX,30);
        FrameLayout.LayoutParams Params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Params2.leftMargin=144;
        Params2.topMargin=35;
        m_layout.addView(m_text,Params2);
        //hint
        TextView m_hints = new TextView(this);
        m_hints.setIncludeFontPadding(false);
        m_hints.setText(lastText);
        m_hints.setTextColor(ContextCompat.getColor(this,R.color.seventygray));
        m_hints.setTextSize(TypedValue.COMPLEX_UNIT_PX, 27);
        FrameLayout.LayoutParams Params3 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Params3.leftMargin=144;
        Params3.topMargin=85;
        m_layout.addView(m_hints, Params3);

        if(num>0)
        {
            //frame
            FrameLayout m_f=new FrameLayout(this);
            m_f.setBackgroundResource(R.drawable.icon_redpoint);
            //num
            TextView m_num = new TextView(this);
            m_num.setIncludeFontPadding(false);
            String str=""+num;
            m_num.setText(str);
            m_num.setTextColor(ContextCompat.getColor(this,R.color.white));
            m_num.setTextSize(TypedValue.COMPLEX_UNIT_PX, 20);
            FrameLayout.LayoutParams p2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            p2.gravity = Gravity.CENTER;
            m_f.addView(m_num, p2);

            FrameLayout.LayoutParams p3 = new FrameLayout.LayoutParams(37, 37);
            p3.leftMargin = 102;
            p3.topMargin = 16;
            m_layout.addView(m_f, p3);
        }
        //date
        TextView m_date = new TextView(this);
        m_date.setIncludeFontPadding(false);
        m_date.setText(lastDate);
        m_date.setTextColor(ContextCompat.getColor(this,R.color.seventygray));
        m_date.setTextSize(TypedValue.COMPLEX_UNIT_PX, 25);
        FrameLayout.LayoutParams Params4 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Params4.gravity = Gravity.END;
        Params4.topMargin = 35;
        Params4.rightMargin = 30;
        m_layout.addView(m_date, Params4);
        //line
        View m_line = new View(this);
        m_line.setBackgroundColor(ContextCompat.getColor(this,R.color.nintygray));
        FrameLayout.LayoutParams Params5 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        Params5.gravity = Gravity.BOTTOM;
        Params5.leftMargin = 30;
        Params5.rightMargin = 30;
        m_layout.addView(m_line, Params5);
        //Listening
        m_layout.setOnTouchListener(new View.OnTouchListener() {
            boolean click=false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundColor(ContextCompat.getColor(InfoNews.this,R.color.nintygray));
                        click=true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if(click && event.getX()>0 && event.getX()<v.getWidth() &&
                                event.getY()>0 && event.getY()<v.getHeight()) {
                                Turn(user,name,head);
                        }
                        v.setBackgroundColor(ContextCompat.getColor(InfoNews.this,R.color.white));
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if(click) {
                            click = false;
                            v.setBackgroundColor(ContextCompat.getColor(InfoNews.this,R.color.white));
                        }
                        break;
                }
                return true;
            }
        });

        LinearLayout.LayoutParams Params10 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,151);
        Params10.topMargin=topMargin;
        itemLayout.addView(m_layout,Params10);
        return m_layout;
    }
}
