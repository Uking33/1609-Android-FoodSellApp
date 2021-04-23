package com.example.administrator.frozengoods;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InfoChat extends UActivity implements OnClickListener {
    private ListView m_ListView;
    private Button m_record;
    private UTextEdit m_uEdit;
    private EditText m_edit;
    private Button m_send;
    private ImageView m_sound;
    private ImageView m_more;
    private LinearLayout m_items;
    private ImageView m_camera;
    private ImageView m_photo;
    private ImageView m_location;

    private ChatMsgViewAdapter mAdapter;// 消息视图的Adapter
    private List<ChatMsgEntity> mDataArrays = new ArrayList<>();// 消息对象数组

    private ChatItems m_chat;
    byte[] m_fromHead;
    byte[] m_toHead;

    //Create
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_chat);

        initView();
        initData();
        InitPhotoACamera();
    }
    @Override protected void onDestroy(){
        m_chat=null;
        m_fromHead=null;
        m_toHead=null;
        m_ListView=null;
        m_record=null;
        m_edit=null;
        m_send=null;
        m_sound=null;
        m_more=null;
        m_items=null;
        m_camera=null;
        m_photo=null;
        m_location=null;

        mAdapter=null;
        mDataArrays=null;
        m_head_tempFile=null;

        m_uEdit=null;
        super.onDestroy();
        setContentView(R.layout.view_null);
    }

    //Turn
    @Override public void onBackPressed() {
        Intent intent = new Intent();
        m_chat.clearPic();
        intent.putExtra("chat", m_chat);
        setResult(RESULT_OK, intent);
        finish();
    }
    @Override public void Back(){
        onBackPressed();
    }

    //Item
    public UTextEdit initTextEdit(UActivity m_parent, FrameLayout itemLayout, int id, String label, String tips, int type, int height, int size){//聊天输入框
        UTextEdit uEdit=new UTextEdit(m_parent,label,tips,type,size);
        uEdit.m_edit.setId(id);
        uEdit.addToView(itemLayout,548, height , Gravity.CENTER_VERTICAL);
        return uEdit;
    }

    //Layout
    public void initView() {
        m_title = (TextView) findViewById(R.id.info_chat_title);
        m_ListView = (ListView) findViewById(R.id.listview);
        m_back = (FrameLayout) findViewById(R.id.info_chat_btn_back);
        m_back_over = findViewById(R.id.info_chat_btn_back_over);
        m_sound =(ImageView) findViewById(R.id.info_chat_bottom_sound);
        m_record =(Button) findViewById(R.id.info_chat_bottom_record);
        m_uEdit=initTextEdit(this, (FrameLayout) findViewById(R.id.info_chat_bottom_edit_layout), R.id.info_chat_bottom_edit, "", "", InputType.TYPE_CLASS_TEXT, 64, 28);
        m_edit = (EditText) findViewById(R.id.info_chat_bottom_edit);
        m_send = (Button) findViewById(R.id.info_chat_bottom_send);
        m_more = (ImageView) findViewById(R.id.info_chat_bottom_more);
        m_items = (LinearLayout) findViewById(R.id.info_chat_bottom_items);


        m_camera = (ImageView) findViewById(R.id.info_chat_bottom_camera);
        m_photo = (ImageView) findViewById(R.id.info_chat_bottom_photo);
        m_location = (ImageView) findViewById(R.id.info_chat_bottom_location);

        m_send.setOnClickListener(this);
        m_back_over.getBackground().setAlpha(0);
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

        m_ListView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        m_items.setVisibility(View.GONE);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(m_edit.getWindowToken(), 0);
                        break;
                }
                return false;
            }
        });
        m_sound.setOnTouchListener(new View.OnTouchListener() {
            boolean click = false;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        m_sound.setImageResource(R.drawable.icon_sound_press);
                        click = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                                event.getY() > 0 && event.getY() < v.getHeight()) {
                            if(m_items.getVisibility()==View.VISIBLE)
                                m_items.setVisibility(View.GONE);
                            if(m_uEdit.m_layout.getVisibility()==View.VISIBLE) {
                                m_record.setVisibility(View.VISIBLE);
                                m_uEdit.m_layout.setVisibility(View.GONE);
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(m_edit.getWindowToken(), 0);

                                m_send.setVisibility(View.GONE);
                                m_more.setVisibility(View.VISIBLE);
                            } else {
                                m_record.setVisibility(View.GONE);
                                m_uEdit.m_layout.setVisibility(View.VISIBLE);
                            }
                        }
                        m_sound.setImageResource(R.drawable.icon_sound);
                        break;
                }
                return true;
            }
        });

        m_edit.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    if(m_uEdit!=null) m_uEdit.m_line.setBackgroundColor(ContextCompat.getColor(InfoChat.this,R.color.textblue));
                    m_items.setVisibility(View.GONE);
                    if(m_edit.getText().length()!=0){
                        m_send.setVisibility(View.VISIBLE);
                        m_more.setVisibility(View.GONE);
                    }
                    else {
                        m_send.setVisibility(View.GONE);
                        m_more.setVisibility(View.VISIBLE);
                    }
                } else {
                    if(m_uEdit!=null) m_uEdit.m_line.setBackgroundColor(ContextCompat.getColor(InfoChat.this,R.color.eghitygray));
                }
            }
        });

        m_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(m_edit.getText().length()!=0){
                    m_send.setVisibility(View.VISIBLE);
                    m_more.setVisibility(View.GONE);
                }
                else {
                    m_send.setVisibility(View.GONE);
                    m_more.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }

        });

        m_more.setOnTouchListener(new View.OnTouchListener() {
            boolean click = false;
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        m_more.setImageResource(R.drawable.icon_more_press);
                        click = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                                event.getY() > 0 && event.getY() < v.getHeight()) {
                            m_record.setVisibility(View.GONE);
                            if(m_uEdit!=null) m_uEdit.m_layout.setVisibility(View.VISIBLE);
                            if(m_items.getVisibility()==View.VISIBLE) {
                                m_items.setVisibility(View.GONE);
                            } else {
                                m_items.setVisibility(View.VISIBLE);
                            }
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(m_edit.getWindowToken(), 0);
                            if(m_uEdit!=null) m_uEdit.m_layout.setVisibility(View.GONE);
                            if(m_uEdit!=null) m_uEdit.m_layout.setVisibility(View.VISIBLE);
                        }
                        m_more.setImageResource(R.drawable.icon_more);
                        break;
                }
                return true;
            }
        });



        m_location.setOnTouchListener(new View.OnTouchListener() {
            boolean click = false;
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        m_location.setImageResource(R.drawable.icon_location_press);
                        click = true;
                        break;
                    case MotionEvent.ACTION_UP:
//                        if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
//                                event.getY() > 0 && event.getY() < v.getHeight()) {
//                        }
                        m_location.setImageResource(R.drawable.icon_location);
                        break;
                }
                return true;
            }
        });
        m_record.setOnTouchListener(new View.OnTouchListener() {
            boolean click = false;
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        m_record.setText("松开取消");
                        m_record.setBackgroundResource(R.drawable.btn_white_soild);
                        click = true;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
//                        if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
//                                event.getY() > 0 && event.getY() < v.getHeight()) {
//                        }
                        m_record.setText("长按录音");
                        m_record.setBackgroundResource(R.drawable.btn_white_soild_over);
                        break;
                }
                return true;
            }
        });
    }
    public void initData() {
        //title
        Intent intent=getIntent();
        m_title.setText(intent.getStringExtra("name"));
        //data
        m_toHead = intent.getByteArrayExtra("toHead");
        m_fromHead = intent.getByteArrayExtra("fromHead");
        m_chat = intent.getParcelableExtra("chat");
        m_chat.getPic();

        if(m_chat!=null) {
            for (int i = 0; i < m_chat.items.size(); i++) {
                ChatMsgEntity entity = new ChatMsgEntity();
                entity.setDate(m_chat.items.get(i).date);
                if ((m_chat.fromUser.equals("system")&&m_chat.toUser.equals("system")) || m_chat.items.get(i).user.equals(m_chat.fromUser)) {
                    entity.setMsgType(false);// 自己发送的消息
                    entity.setHead(m_fromHead);
                } else {
                    entity.setMsgType(true);// 收到的消息
                    entity.setHead(m_toHead);
                }
                String str=m_chat.items.get(i).type;
                if(str.equals("text"))
                    entity.setContext("text",m_chat.items.get(i).words);
                else if(!str.isEmpty())
                    entity.setContext(str,m_chat.items.get(i).data);
                mDataArrays.add(entity);
            }
        }

        mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
        m_ListView.setAdapter(mAdapter);
        m_ListView.setSelection(mAdapter.getCount() - 1);
    }

    //Send
    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.info_chat_bottom_send:// 发送按钮点击事件
                send("text");
                break;
        }
    }
    private void send(String type) {
        ChatMsgEntity entity = new ChatMsgEntity();
        if(type.equals("text")) {
            String contString = m_edit.getText().toString();
            if (contString.length() > 0) {
                //data
                ChatItem item = new ChatItem();
                item.user = m_chat.fromUser;
                item.date=getDate();
                item.type="text";
                item.data=null;
                item.words=contString;
                item.state="1";
                m_chat.items.add(item);
                if(m_chat.fromUser.equals("system")&&m_chat.toUser.equals("system")){
                    DatabaseHelper.Act.ChatSend(m_chat.fromUser, m_chat.toUser, m_chat.toName, item);
                    item.state = "0";
                    DatabaseHelper.Act.ChatSend(m_chat.toUser, m_chat.toName, item);
                }
                else {
                    DatabaseHelper.Act.ChatSend(m_chat.fromUser, m_chat.toUser, m_chat.toName, item);
                    item.state = "0";
                    DatabaseHelper.Act.ChatSend(m_chat.toUser, m_chat.fromUser, m_chat.fromName, item);
                }
                item.state = "1";
                //view
                entity.setDate(item.date);
                entity.setContext("text",contString);
                entity.setMsgType(false);
                entity.setHead(m_fromHead);

                mDataArrays.add(entity);
                mAdapter.notifyDataSetChanged();// 通知ListView，数据已发生改变
                m_edit.setText("");// 清空编辑框数据
            }
        }
        m_ListView.setSelection(m_ListView.getCount() - 1);// 发送一条消息时，ListView显示选择最后一项
    }
    private void send(String type,byte[] data) {
        ChatMsgEntity entity = new ChatMsgEntity();
        if(type.equals("image")){
            if (data!=null) {
                //data
                ChatItem item = new ChatItem();
                item.user = m_chat.fromUser;
                item.date=getDate();
                item.type="image";

                item.data=data;
                item.words="";
                item.state="1";
                m_chat.items.add(item);
                if(m_chat.fromUser.equals("system")&&m_chat.toUser.equals("system")){
                    DatabaseHelper.Act.ChatSend(m_chat.fromUser, m_chat.toUser, m_chat.toName, item);
                    item.state = "0";
                    DatabaseHelper.Act.ChatSend(m_chat.fromUser, m_chat.fromName, item);
                }
                else {
                    DatabaseHelper.Act.ChatSend(m_chat.fromUser, m_chat.toUser, m_chat.toName, item);
                    item.state = "0";
                    DatabaseHelper.Act.ChatSend(m_chat.toUser, m_chat.fromUser, m_chat.fromName, item);
                }
                item.state = "1";
                //view
                entity.setDate(item.date);
                entity.setContext("image",data);
                entity.setMsgType(false);
                entity.setHead(m_fromHead);

                mDataArrays.add(entity);
                mAdapter.notifyDataSetChanged();// 通知ListView，数据已发生改变
            }
        }
        m_ListView.setSelection(m_ListView.getCount() - 1);// 发送一条消息时，ListView显示选择最后一项
    }
    private String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return format.format(new Date());
    }

    //Pic
    File m_head_tempFile;
    private static final int PHOTO_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_hhMMss");
        return dateFormat.format(date) + ".jpg";
    }
    void InitPhotoACamera(){
        m_camera.setOnTouchListener(new View.OnTouchListener() {
            boolean click = false;
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        m_camera.setImageResource(R.drawable.icon_camera_press);
                        click = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                                event.getY() > 0 && event.getY() < v.getHeight()) {
                            getPicFromCamera();
                        }
                        m_camera.setImageResource(R.drawable.icon_camera);
                        break;
                }
                return true;
            }
        });
        m_photo.setOnTouchListener(new View.OnTouchListener() {
            boolean click = false;
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        m_photo.setImageResource(R.drawable.icon_photo_press);
                        click = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (click && event.getX() > 0 && event.getX() < v.getWidth() &&
                                event.getY() > 0 && event.getY() < v.getHeight()) {
                            getPicFromPhoto();
                        }
                        m_photo.setImageResource(R.drawable.icon_photo);
                        break;
                }
                return true;
            }
        });
    }
    public void getPicFromPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        startActivityForResult(intent, PHOTO_REQUEST);
    }
    public void getPicFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("autoFocus", true);// 自动对焦
        intent.putExtra("fullScreen", false);// 全屏
        intent.putExtra("showActionIcons", false);
        // 指定调用相机拍照后照片的储存路径
        m_head_tempFile = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(m_head_tempFile));
        startActivityForResult(intent, CAMERA_REQUEST);
    }
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_REQUEST:
                    sendImage(changeUriToPath(data.getData()));
                    break;
                case CAMERA_REQUEST:
                    sendImage(m_head_tempFile.getPath());
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private String changeUriToPath(Uri uri){
        String[] pro={MediaStore.Images.Media.DATA};
        Cursor actualImageCursor=getContentResolver().query(uri, pro, null, null, null);
        assert actualImageCursor != null;
        int actual_image_column_index=actualImageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualImageCursor.moveToFirst();
        String result=actualImageCursor.getString(actual_image_column_index);
        actualImageCursor.close();
        return result;
    }
    private void sendImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap;
        options.inJustDecodeBounds = false; //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = (int)(options.outHeight / (float)320);
        if (be <= 0)
            be = 1;
        options.inSampleSize = be; //重新读入图片，注意此时已经把 options.inJustDecodeBounds 设回 false 了
        bitmap=BitmapFactory.decodeFile(path,options);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        send("image",byteArray);
    }
}

/*---聊天---*/
class ChatMsgViewAdapter extends BaseAdapter {
    private Context m_parent;
    interface IMsgViewType {
        int ITEM_COM_MSG = 0;// 收到对方的消息
        int ITEM_TO_MSG = 1;// 自己发送出去的消息
    }

    private static final int ITEM_COUNT = 2;// 消息类型的总数
    private List<ChatMsgEntity> coll;// 消息对象数组
    private LayoutInflater mInflater;

    ChatMsgViewAdapter(Context context, List<ChatMsgEntity> coll) {
        this.coll = coll;
        m_parent=context;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return coll.size();
    }

    public Object getItem(int position) {
        return coll.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public int getItemViewType(int position) {
        ChatMsgEntity entity = coll.get(position);

        if (entity.getMsgType()) {//收到的消息
            return IMsgViewType.ITEM_COM_MSG;
        } else {//自己发送的消息
            return IMsgViewType.ITEM_TO_MSG;
        }
    }

    public int getViewTypeCount() {
        return ITEM_COUNT;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        final ChatMsgEntity entity = coll.get(position);
        boolean isComMsg = entity.getMsgType();

        ViewHolder viewHolder;
        if (convertView == null) {
            if (isComMsg) {
                convertView = mInflater.inflate(
                        R.layout.chat_item_left, null);
            } else {
                convertView = mInflater.inflate(
                        R.layout.chat_item_right, null);
            }

            viewHolder = new ViewHolder();
            viewHolder.SendTime = (TextView) convertView
                    .findViewById(R.id.chat_sendtime);
            viewHolder.Text = (TextView) convertView
                    .findViewById(R.id.chat_chattext);
            viewHolder.Image = (ImageView) convertView
                    .findViewById(R.id.chat_chatimage);
            viewHolder.Head = (ImageView) convertView
                    .findViewById(R.id.chat_userhead);
            viewHolder.isComMsg = isComMsg;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.SendTime.setText(entity.getDate());
        if(entity.getType().equals("text")) {
            viewHolder.Text.setVisibility(View.VISIBLE);
            viewHolder.Image.setVisibility(View.GONE);
            viewHolder.Text.setText(entity.getText());
            convertView.setPadding(30, 10,30, 10);
        }
        else if(entity.getType().equals("image")){
            viewHolder.Text.setVisibility(View.GONE);
            viewHolder.Image.setVisibility(View.VISIBLE);
            viewHolder.Image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            viewHolder.Image.setImageDrawable(Widget.ByteToDrawable(entity.getImage()));
            Bitmap bitmap = BitmapFactory.decodeByteArray(entity.getImage(), 0, entity.getImage().length);
            ViewGroup.LayoutParams lp = viewHolder.Image.getLayoutParams();
            lp.width = 200;
            lp.height = bitmap.getHeight()*200/bitmap.getWidth();
            viewHolder.Image.setLayoutParams(lp);
            viewHolder.Image.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    UImageDialog dialog = new UImageDialog(m_parent, Widget.ByteToDrawable(entity.getImage()));
                    dialog.show();
                    DisplayMetrics outMetrics=new DisplayMetrics();//获取屏幕的宽度
                    ((InfoChat)m_parent).getWindow().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
                    android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
                    p.height = (outMetrics.heightPixels);
                    p.width = (outMetrics.widthPixels);
                    dialog.getWindow().setAttributes(p);
                }
            });
            convertView.setPadding(30, 10,30, 10);
        }
        viewHolder.Head.setImageDrawable(Widget.ByteToDrawable(entity.getHead()));
        return convertView;
    }

    private static class ViewHolder {
        TextView SendTime;
        TextView Text;
        ImageView Head;
        ImageView Image;
        boolean isComMsg = true;
    }

}
class ChatMsgEntity {
    private String date;//消息日期
    private String type;//消息内容
    private String text;//消息内容
    private byte[] image;//消息内容
    private byte[] head;//消息头像
    private boolean isComMeg = true;// 是否为收到的消息

    String getType(){
        return type;
    }

    String getDate() {
        return date;
    }

    void setDate(String date) {
        this.date = date;
    }

    byte[] getHead() {
        return head;
    }

    void setHead(byte[] head) {
        this.head = head;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public byte[] getImage(){
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    boolean getMsgType() {
        return isComMeg;
    }

    void setMsgType(boolean isComMsg) {
        isComMeg = isComMsg;
    }

    ChatMsgEntity() {
    }

    void setContext(String type,String text){
        this.type=type;
        setText(text);
    }

    void setContext(String type,byte[] data){
        this.type=type;
        if(type.equals("image"))
            setImage(data);
    }
}