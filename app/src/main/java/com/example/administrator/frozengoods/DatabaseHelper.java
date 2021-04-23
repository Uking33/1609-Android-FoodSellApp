package com.example.administrator.frozengoods;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class DatabaseHelper extends SQLiteOpenHelper{
    static DatabaseHelper Act;
    private SQLiteDatabase mDefaultReadableDatabase = null;
    private DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        Act=this;
    }
    private DatabaseHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }
    DatabaseHelper(Context context, String name) {
        this(context, name, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase m_database) {
        //检查创建
        try
        {
            m_database.execSQL("create table if not exists all_users (id integer, user text(20), passwords text(20), name text(20), phone text(11), head blob, sex text," +
                    "shop text(50), adProvince text,adCity text,adDistrict text, adDetails text(50), adText text(100), shopPhone text, logo blob, followed integer, visited integer, recommend integer," +
                    "province text,city text,district text)");
            m_database.execSQL("create table if not exists good_items (id text,user text(20), province text, city text, district text, state text," +
                    "picNum integer, pic1 blob, pic2 blob, pic3 blob," +
                    "name text, class1 text, class2 text, num integer, price integer, unit text, shop text, details text)" );
            m_database.execSQL("create table if not exists get_items (id text,user text(20), province text, city text, district text," +
                    "class1 text,class2 text," +
                    "name text, num integer, price integer, unit text, getterName text, getterPhone text, shop text)" );
            m_database.execSQL("create table if not exists follow_items (user text,id text,type text)" );
        }
        catch(Exception e)
        {
            System.out.println("Error initDatabase:"+e);
        }
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        final SQLiteDatabase db;
        if(mDefaultReadableDatabase != null){
            db = mDefaultReadableDatabase;
        } else {
            db = super.getReadableDatabase();
        }
        return db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /*---数据库---*/
    private  boolean CreateUserItem(DatabaseHelper helper,UserItem user){
        if(helper==null)
            helper=DatabaseHelper.Act;
        SQLiteDatabase db = helper.getWritableDatabase();
        //检查创建
        try
        {
            db.execSQL("create table if not exists chat_"+user.m_user+" (toUser text(20), toName text(20), date text(20), user text(20), state text," +
                    "type text, words text, data blob)" );
            db.close();
            return true;
        }
        catch(Exception e)
        {
            System.out.println("Error initDatabase:"+e);
        }
        return false;
    }

    //---Get---
    //Get-String
    private List<String> GetDataALLUserItemUser(DatabaseHelper helper){//获取信息
        if(helper==null)
            helper=DatabaseHelper.Act;
        try
        {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select user from all_users ",null);

            List<String> userList=new ArrayList<>();
            if(cursor!=null) {
                while (cursor.moveToNext()) {
                    String temp=cursor.getString(0);
                    if(!temp.equals("system"))
                        userList.add(temp);
                }
                cursor.close();
                return userList;
            }
            db.close();
        }
        catch(Exception e)
        {
            System.out.println("Error GetData:"+e);
        }
        return null;
    }
    private String GetDataUserString(DatabaseHelper helper,String type,String details){//获取信息
        if(helper==null)
            helper=DatabaseHelper.Act;
        try
        {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select name from all_users where "+type+"=?",new String[]{details});
            if( cursor!=null && cursor.moveToFirst()) {
                String str = cursor.getString(0);
                cursor.close();
                db.close();
                return str;
            }
            db.close();
        }
        catch(Exception e)
        {
            System.out.println("Error GetData:"+e);
        }
        return null;
    }
    //Get-byte[]
    private byte[] GetDataUserData(DatabaseHelper helper,String type,String where, String[] whereValue){//获取信息
        if(helper==null)
            helper=DatabaseHelper.Act;
        try
        {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select "+type+" from all_users "+where,whereValue);
            if( cursor!=null && cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                cursor.close();
                db.close();
                return data;
            }
            db.close();
        }
        catch(Exception e)
        {
            System.out.println("Error GetData:"+e);
        }
        return null;
    }
    //Get-pic
    private boolean GetDataUserPic(DatabaseHelper helper,UserItem user,String where,String[] value){//顺序不能错
        if(helper==null)
            helper=DatabaseHelper.Act;if(helper==null)
            helper=DatabaseHelper.Act;
        try
        {
            //List
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select head,logo from all_users "+where,value);
            if( cursor!=null) {
                cursor.moveToFirst();
                user.m_head = cursor.getBlob(0);
                if(!user.m_shop.m_shop.isEmpty())
                    user.m_shop.m_logo = cursor.getBlob(1);
                cursor.close();
            }
            db.close();
            return true;
        }
        catch(Exception e)
        {
            System.out.println("Error GetData:"+e);
        }
        return false;
    }
    private boolean GetDataGoodItemPic(DatabaseHelper helper,ArrayList<GoodItem> list,String where,String[] value){//顺序不能错
        if(helper==null)
            helper=DatabaseHelper.Act;if(helper==null)
            helper=DatabaseHelper.Act;
        try
        {
            //List
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select pic1,pic2,pic3 from good_items "+where,value);
            GoodItem item;
            int i=-1;
            if( cursor!=null) {
                while (cursor.moveToNext()) {
                    i++;
                    item=list.get(i);
                    item.pic1 = cursor.getBlob(0);
                    item.pic2 = cursor.getBlob(1);
                    item.pic3 = cursor.getBlob(2);
                }
                cursor.close();
            }
            db.close();
            return true;
        }
        catch(Exception e)
        {
            System.out.println("Error GetData:"+e);
        }
        return false;
    }
    private boolean GetDataChatItemsPic(DatabaseHelper helper,ChatItems chats,String where,String[] value){//顺序不能错
        if(helper==null)
            helper=DatabaseHelper.Act;if(helper==null)
            helper=DatabaseHelper.Act;
        try
        {
            //List
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select data from chat_"+chats.fromUser+" "+where,value);
            int i=-1;
            ChatItem chat;
            if( cursor!=null) {
                while (cursor.moveToNext()) {
                    i++;
                    chat=chats.items.get(i);
                    chat.data = cursor.getBlob(0);
                }
                cursor.close();
            }
            db.close();
            return true;
        }
        catch(Exception e)
        {
            System.out.println("Error GetData:"+e);
        }
        return false;
    }
    //Get-Item-ByID
    private ArrayList<UserItem> GetDataUserItemByID(DatabaseHelper helper,List<String> strList){//获取信息
        if(helper==null)
            helper=DatabaseHelper.Act;
        try
        {
            //List
            SQLiteDatabase db = helper.getReadableDatabase();
            ArrayList<UserItem> itemsList=new ArrayList<>();

            Cursor cursor;
            for(int i=0;i<strList.size();i++) {
                cursor = db.rawQuery("select * from all_users where user=?", new String[]{strList.get(i)});
                if (cursor != null) {
                    cursor.moveToFirst();
                    UserItem user=new UserItem();
                    user.m_id = cursor.getInt(0);
                    user.m_user = cursor.getString(1);
                    user.m_passwords = cursor.getString(2);
                    user.m_name = cursor.getString(3);
                    user.m_phone = cursor.getString(4);
                    user.m_head=null;
                    user.m_sex = cursor.getString(6);
                    user.m_shop = new ShopItem();
                    user.m_shop.setValue(cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10),cursor.getString(11),cursor.getString(12),cursor.getString(13),cursor.getBlob(14),cursor.getInt(15),cursor.getInt(16),cursor.getInt(17));
                    user.m_province=cursor.getString(18);user.m_city=cursor.getString(19);user.m_district=cursor.getString(20);
                    itemsList.add(user);
                    cursor.close();
                }
            }
            db.close();
            return itemsList;
        }
        catch(Exception e)
        {
            System.out.println("Error GetData:"+e);
        }
        return null;
    }
    private ArrayList<GoodItem> GetDataGoodItemByID(DatabaseHelper helper,List<String> strList){//获取信息
        if(helper==null)
            helper=DatabaseHelper.Act;
        try
        {
            //List
            SQLiteDatabase db = helper.getReadableDatabase();
            ArrayList<GoodItem> itemsList=new ArrayList<>();

            Cursor cursor;
            for(int i=0;i<strList.size();i++) {
                cursor = db.rawQuery("select * from good_items where id=?", new String[]{strList.get(i)});
                if (cursor != null) {
                    cursor.moveToFirst();
                    GoodItem item = new GoodItem();
                    item.id = cursor.getString(0);
                    item.user = cursor.getString(1);
                    item.province = cursor.getString(2);
                    item.city = cursor.getString(3);
                    item.district = cursor.getString(4);
                    item.state = cursor.getString(5);
                    item.pic_num = cursor.getInt(6);
                    item.pic1 = cursor.getBlob(7);
                    item.pic2 = cursor.getBlob(8);
                    item.pic3 = cursor.getBlob(9);
                    item.name = cursor.getString(10);
                    item.class1 = cursor.getString(11);
                    item.class2 = cursor.getString(12);
                    item.num = cursor.getInt(13);
                    item.price = cursor.getInt(14);
                    item.unit = cursor.getString(15);
                    item.shop = cursor.getString(16);
                    item.details = cursor.getString(17);
                    itemsList.add(item);
                    cursor.close();
                }
            }
            db.close();
            return itemsList;
        }
        catch(Exception e)
        {
            System.out.println("Error GetData:"+e);
        }
        return null;
    }
    private ArrayList<GetItem> GetDataGetItemByID(DatabaseHelper helper,List<String> strList){//获取信息
        if(helper==null)
            helper=DatabaseHelper.Act;
        try
        {
            //List
            SQLiteDatabase db = helper.getReadableDatabase();
            ArrayList<GetItem> itemsList=new ArrayList<>();

            Cursor cursor;
            for(int i=0;i<strList.size();i++) {
                cursor = db.rawQuery("select * from get_items where id=?", new String[]{strList.get(i)});
                if (cursor != null) {
                    cursor.moveToFirst();
                    GetItem item=new GetItem();
                    item.id=cursor.getString(0);
                    item.user = cursor.getString(1);
                    item.province = cursor.getString(2);
                    item.city = cursor.getString(3);
                    item.district = cursor.getString(4);

                    item.class1 = cursor.getString(5);
                    item.class2 = cursor.getString(6);
                    item.name = cursor.getString(7);
                    item.num = cursor.getInt(8);
                    item.price = cursor.getInt(9);
                    item.unit = cursor.getString(10);
                    item.getterName = cursor.getString(11);
                    item.getterPhone = cursor.getString(12);
                    item.shop = cursor.getString(13);
                    itemsList.add(item);
                    cursor.close();
                }
            }
            db.close();
            return itemsList;
        }
        catch(Exception e)
        {
            System.out.println("Error GetData:"+e);
        }
        return null;
    }
    //Get-Item
    UserItem GetDataUserItem(DatabaseHelper helper,String where, String[] whereValue){//获取信息
        if(helper==null)
            helper=DatabaseHelper.Act;
        try
        {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from all_users "+where,whereValue);
            if( cursor!=null && cursor.moveToFirst()) {
                UserItem user=new UserItem();
                user.m_id = cursor.getInt(0);
                user.m_user = cursor.getString(1);
                user.m_passwords = cursor.getString(2);
                user.m_name = cursor.getString(3);
                user.m_phone = cursor.getString(4);
                user.m_head=null;
                user.m_sex = cursor.getString(6);
                user.m_shop = new ShopItem();
                user.m_shop.setValue(cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10),cursor.getString(11),cursor.getString(12),cursor.getString(13),null,cursor.getInt(15),cursor.getInt(16),cursor.getInt(17));
                user.m_province=cursor.getString(18);user.m_city=cursor.getString(19);user.m_district=cursor.getString(20);
                cursor.close();
                db.close();
                return user;
            }
            db.close();
        }
        catch(Exception e)
        {
            System.out.println("Error GetData:"+e);
        }
        return null;
    }
    private ShopItem GetDataShopItemList(DatabaseHelper helper, String where, String[] value) {

        if(helper==null)
            helper=DatabaseHelper.Act;
        try
        {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from all_users "+where,value);
            if( cursor!=null && cursor.moveToFirst()) {
                ShopItem shop = new ShopItem();
                shop.setValue(cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10),cursor.getString(11),cursor.getString(12),cursor.getString(13),cursor.getBlob(14),cursor.getInt(15),cursor.getInt(16),cursor.getInt(17));
                cursor.close();
                db.close();
                return shop;
            }
            db.close();
        }
        catch(Exception e)
        {
            System.out.println("Error GetData:"+e);
        }
        return null;
    }
    private ArrayList<GoodItem> GetDataGoodItemList(DatabaseHelper helper,String where,String[] value){//获取信息
        if(helper==null)
            helper=DatabaseHelper.Act;
        try
        {
            //List
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from good_items "+where,value);
            ArrayList<GoodItem> itemsList=new ArrayList<>();
            if( cursor!=null) {
                while (cursor.moveToNext()) {
                    GoodItem item=new GoodItem();
                    item.id=cursor.getString(0);
                    item.user = cursor.getString(1);
                    item.province = cursor.getString(2);
                    item.city = cursor.getString(3);
                    item.district = cursor.getString(4);
                    item.state = cursor.getString(5);
                    item.pic_num = cursor.getInt(6);
                    item.name = cursor.getString(10);
                    item.class1 = cursor.getString(11);
                    item.class2 = cursor.getString(12);
                    item.num = cursor.getInt(13);
                    item.price = cursor.getInt(14);
                    item.unit = cursor.getString(15);
                    item.shop = cursor.getString(16);
                    item.details = cursor.getString(17);
                    itemsList.add(item);
                }
                cursor.close();
            }
            db.close();
            return itemsList;
        }
        catch(Exception e)
        {
            System.out.println("Error GetData:"+e);
        }
        return null;
    }
    private ArrayList<GetItem> GetDataGetItemList(DatabaseHelper helper,String where,String[] value){//获取信息
        if(helper==null)
            helper=DatabaseHelper.Act;
        try
        {
            //List
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from get_items "+where,value);
            ArrayList<GetItem> itemsList=new ArrayList<>();
            if( cursor!=null) {
                while (cursor.moveToNext()) {
                    GetItem item=new GetItem();
                    item.id=cursor.getString(0);
                    item.user = cursor.getString(1);
                    item.province = cursor.getString(2);
                    item.city = cursor.getString(3);
                    item.district = cursor.getString(4);

                    item.class1 = cursor.getString(5);
                    item.class2 = cursor.getString(6);
                    item.name = cursor.getString(7);
                    item.num = cursor.getInt(8);
                    item.price = cursor.getInt(9);
                    item.unit = cursor.getString(10);
                    item.getterName = cursor.getString(11);
                    item.getterPhone = cursor.getString(12);
                    item.shop = cursor.getString(13);
                    itemsList.add(item);
                }
                cursor.close();
            }
            db.close();
            return itemsList;
        }
        catch(Exception e)
        {
            System.out.println("Error GetData:"+e);
        }
        return null;
    }
    private ArrayList<ChatItems> GetDataChatItemsList(DatabaseHelper helper,String fromUser){//获取信息
        String fromName;
        if(helper==null)
            helper=DatabaseHelper.Act;
        try
        {
            //Name
            SQLiteDatabase db0 = helper.getReadableDatabase();
            Cursor cursor0 = db0.rawQuery("select * from all_users where user=?",new String[]{fromUser});
            if( cursor0!=null && cursor0.moveToFirst()) {
                fromName = cursor0.getString(3);
                cursor0.close();
            }
            else{
                db0.close();
                return null;
            }
            cursor0.close();
            db0.close();
            //List
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from chat_"+fromUser+" order by toUser,date ASC",null);
            ArrayList<ChatItems> chatsList=new ArrayList<>();
            if( cursor!=null) {
                String last="";
                ChatItems chats = new ChatItems();
                while (cursor.moveToNext()) {
                    String temp=cursor.getString(0);
                    if(!temp.equals(last)) {//新的人
                        last=temp;
                        chats = new ChatItems();
                        chats.fromName = fromName;
                        chats.fromUser = fromUser;
                        chats.toUser = temp;
                        chats.toName = cursor.getString(1);
                        chatsList.add(chats);
                    }

                    ChatItem chat=new ChatItem();
                    chat.date = cursor.getString(2);
                    chat.user = cursor.getString(3);
                    chat.state = cursor.getString(4);
                    chat.type = cursor.getString(5);
                    chat.words = cursor.getString(6);
                    //chat.data = cursor.getBlob(7);
                    chats.items.add(chat);
                }
                cursor.close();
            }
            db.close();
            return chatsList;
        }
        catch(Exception e)
        {
            System.out.println("Error GetData:"+e);
        }
        return null;
    }
    private ArrayList<String> GetFollowList(DatabaseHelper helper, String where, String[] value) {
        if(helper==null)
            helper=DatabaseHelper.Act;
        try
        {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select id from follow_items "+where,value);
            ArrayList<String> list = new ArrayList<>();
            if( cursor!=null) {
                while (cursor.moveToNext()) {
                    list.add(cursor.getString(0));
                }
                cursor.close();
                db.close();
                return list;
            }
            db.close();
        }
        catch(Exception e)
        {
            System.out.println("Error GetData:"+e);
        }
        return null;
    }

    //---Insert---
    private void InsertData(DatabaseHelper helper,UserItem user) {
        if(helper==null)
            helper=DatabaseHelper.Act;
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("id", user.m_id);
        cv.put("user", user.m_user);
        cv.put("passwords", user.m_passwords);
        cv.put("name", user.m_name);
        cv.put("phone", user.m_phone);
        cv.put("head", user.m_head);
        cv.put("sex", user.m_sex);
        cv.put("shop", user.m_shop.m_shop);
        cv.put("adProvince", user.m_shop.m_ad_province);
        cv.put("adCity", user.m_shop.m_ad_city);
        cv.put("adDistrict", user.m_shop.m_ad_district);
        cv.put("adDetails", user.m_shop.m_ad_details);
        cv.put("adText", user.m_shop.m_adText);
        cv.put("shopPhone", user.m_shop.m_phone);
        cv.put("logo", user.m_shop.m_logo);
        cv.put("followed", user.m_shop.m_followed);
        cv.put("visited", user.m_shop.m_visited);
        cv.put("recommend", user.m_shop.m_recommend);
        cv.put("province", user.m_province);
        cv.put("city", user.m_city);
        cv.put("district", user.m_district);
        db.insert("all_users", "id", cv);
        db.close();
        CreateUserItem(null,user);
        //系统
        ChatItem item = new ChatItem();
        item.user = "system";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        item.date = format.format(new Date());
        item.type = "text";

        item.data = null;
        item.words = "欢迎使用冰栈！";
        item.state = "0";
        ChatSend(user.m_user, "system", "系统消息", item);
    }
    private boolean InsertData(DatabaseHelper helper,GoodItem item) {
        if(helper==null)
            helper=DatabaseHelper.Act;
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("id", item.id);
        cv.put("user", item.user);
        cv.put("province", item.province);
        cv.put("city", item.city);
        cv.put("district", item.district);
        cv.put("state", item.state);
        cv.put("picNum", item.pic_num);
        cv.put("pic1", item.pic1);
        cv.put("pic2", item.pic2);
        cv.put("pic3", item.pic3);
        cv.put("name", item.name);
        cv.put("class1", item.class1);
        cv.put("class2", item.class2);
        cv.put("num", item.num);
        cv.put("price", item.price);
        cv.put("unit", item.unit);
        cv.put("shop", item.shop);
        cv.put("details", item.details);
        db.insert("good_items", "id", cv);
        db.close();
        return true;
    }
    private boolean InsertData(DatabaseHelper helper,GetItem item) {
        if(helper==null)
            helper=DatabaseHelper.Act;
        try
        {
            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues cv=new ContentValues();
            cv.put("id", item.id);
            cv.put("user", item.user);
            cv.put("province", item.province);
            cv.put("city", item.city);
            cv.put("district", item.district);
            cv.put("class1", item.class1);
            cv.put("class2", item.class2);
            cv.put("name", item.name);
            cv.put("num", item.num);
            cv.put("price", item.price);
            cv.put("unit", item.unit);
            cv.put("getterName", item.getterName);
            cv.put("getterPhone", item.getterPhone);
            cv.put("shop", item.shop);
            db.insertOrThrow("get_items", "id", cv);
            db.close();
            return true;
        }
        catch(Exception e)
        {
            System.out.println("Error InsertData:"+e);
        }
        return false;
    }
    private boolean InsertData(DatabaseHelper helper,String id,String type, String user) {
        if(helper==null)
            helper=DatabaseHelper.Act;
        try
        {
            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues cv=new ContentValues();
            cv.put("user", user);
            cv.put("id", id);
            cv.put("type", type);
            db.insertOrThrow("follow_items", "id", cv);
            db.close();
            return true;
        }
        catch(Exception e)
        {
            System.out.println("Error InsertData:"+e);
        }
        return false;
    }
    private boolean InsertData(DatabaseHelper helper,ChatItem item,String table,String toUser,String toName) {
        if(helper==null)
            helper=DatabaseHelper.Act;
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("toUser", toUser);
        cv.put("toName", toName);
        cv.put("date", item.date);
        cv.put("user", item.user);
        cv.put("state",item.state);
        cv.put("type",item.type);
        cv.put("words",item.words);
        cv.put("data",item.data);
        db.insert(table, "toUser", cv);
        db.close();
        return true;
    }

    //---Delete---
    private boolean DeleteData(DatabaseHelper helper,String id,String type, String user) {
        if(helper==null)
            helper=DatabaseHelper.Act;
        try
        {
            SQLiteDatabase db = helper.getWritableDatabase();
            if(user.isEmpty())
                db.delete("follow_items","id=? and type=?", new String[]{id,type});
            else
                db.delete("follow_items","id=? and type=? and user=?", new String[]{id,type,user});
            db.close();
            return true;
        }
        catch(Exception e)
        {
            System.out.println("Error InsertData:"+e);
        }
        return false;
    }
    private boolean DeleteData(DatabaseHelper helper,GetItem item) {
        if(helper==null)
            helper=DatabaseHelper.Act;
        try
        {
            SQLiteDatabase db = helper.getWritableDatabase();
            db.delete("get_items","id=?", new String[]{item.id});
            db.close();
            return true;
        }
        catch(Exception e)
        {
            System.out.println("Error InsertData:"+e);
        }
        return false;
    }

    //---Update---
    private boolean DataUpdate(DatabaseHelper helper, String table,String[] details, String[] value, String idType, String[] id) {
        if(helper==null)
            helper=DatabaseHelper.Act;
        try
        {
            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues cv=new ContentValues();
            for(int i=0;i<details.length;i++){
                cv.put(details[i],value[i]);
            }
            db.update(table, cv, idType, id);
            db.close();
            return true;
        }
        catch(Exception e)
        {
            System.out.println("Error UpData:"+e);
        }
        return false;
    }
    private boolean DataUpdate(DatabaseHelper helper, String table,String[] details, String[] value, String idType, String id) {
        if(helper==null)
            helper=DatabaseHelper.Act;
        try
        {
            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues cv=new ContentValues();
            for(int i=0;i<details.length;i++){
                cv.put(details[i],value[i]);
            }
            db.update(table, cv, idType+"=?", new String[]{id});
            db.close();
            return true;
        }
        catch(Exception e)
        {
            System.out.println("Error UpData:"+e);
        }
        return false;
    }
    private boolean DataUpdate(DatabaseHelper helper, String table,String[] details, int[] value, String idType, String id) {
        if(helper==null)
            helper=DatabaseHelper.Act;
        try
        {
            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues cv=new ContentValues();
            for(int i=0;i<details.length;i++){
                cv.put(details[i],value[i]);
            }
            db.update(table, cv, idType+"=?", new String[]{id});
            db.close();
            return true;
        }
        catch(Exception e)
        {
            System.out.println("Error UpData:"+e);
        }
        return false;
    }
    private boolean DataUpdate(DatabaseHelper helper, String table,String details, byte[] value, String idType, String id) {
        if(helper==null)
            helper=DatabaseHelper.Act;
        try
        {
            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues cv=new ContentValues();
            cv.put(details,value);
            db.update(table, cv, idType+"=?", new String[]{id});
            db.close();
            return true;
        }
        catch(Exception e)
        {
            System.out.println("Error UpData:"+e);
        }
        return false;
    }

    //---Others---
    private int GetDataInt(DatabaseHelper helper, String table,String details,String where, String[] whereValue) {
        if(helper==null)
            helper=DatabaseHelper.Act;
        try
        {
            int re=-1;
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select "+details+" from "+table+" "+where,whereValue);
            if(cursor != null && cursor.moveToFirst()) {
                re = cursor.getInt(0);
                cursor.close();
            }
            db.close();
            if(re==-1)//查无
                return -1;
            else
                return re;
        }
        catch(Exception e)
        {
            System.out.println("Error GetData:"+e);
        }
        return -2;
    }
    private short DataIsSame(DatabaseHelper helper, String table,String details, String value,String where, String[] whereValue) {
        if(helper==null)
            helper=DatabaseHelper.Act;
        try
        {
            String temp="☺";
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select "+details+" from "+table+" "+where,whereValue);
            if(cursor != null && cursor.moveToFirst()) {
                temp = cursor.getString(0);
                cursor.close();
            }
            db.close();
            if(temp.equals("☺"))//查无
                return -1;
            if(value.equals(temp))//相等
                return 1;
            else//不相等
                return 0;
        }
        catch(Exception e)
        {
            System.out.println("Error DataIsSame:"+e);
        }
        return -2;
    }


    //登录
    short IsLogIn(String user, String passwords){//登录
        short re;
        if(user.isEmpty() || passwords.isEmpty())
            return -3;
        else{
            re = DataIsSame(null, "all_users", "passwords", passwords, "where user=? or phone=?", new String[]{user, user});
        }
        return re;
    }
    short Register1(String user,String phone,String temp) {//注册
        if(temp.length()>=0)
            return DataIsSame(null,"all_users","user",user,"where user=? or phone=?",new String[]{user,phone});
        return 0;
    }
    short Register2(String user,String phone,String passwords1,String password2) {//注册
        if(passwords1.equals(password2)) {
            //新建
            UserItem uu=new UserItem();
            int re=GetDataInt(null,"all_users","id","",null);
            if(re==-1)
                uu.m_id = 1;
            else
                uu.m_id = re+1;
            uu.m_user = user;
            uu.m_phone=phone;
            uu.m_passwords = passwords1;
            uu.m_name = "小冰栈";
            uu.m_sex = "保密";
            uu.m_head = Widget.DrawableToByte(R.drawable.default_logo);

            if(!uu.m_user.equals("system")) {
                uu.m_province="";
                uu.m_city="";
                uu.m_district="";
            }
            else{
                uu.m_province="广东省";
                uu.m_city="广州市";
                uu.m_district="番禺区";
                uu.m_shop.m_logo=Widget.DrawableToByte(R.drawable.default_logo);
                uu.m_shop.m_ad_province="广东省";
                uu.m_shop.m_ad_city="广州市";
                uu.m_shop.m_ad_district="番禺区";
                uu.m_shop.m_shop="数媒冰栈公司";
                uu.m_shop.m_ad_details="大学城广工东区东五512";
                uu.m_shop.m_phone="18826139918";
                uu.m_shop.m_adText="做最好的冻品";
            }
            //写入数据库
            InsertData(null,uu);
            return 1;
        }
        else
            return 0;
    }
    short Forget1(String phone,String temp) {//忘记密码
        if(temp.length()>0)
            return DataIsSame(null,"all_users","phone",phone,"where phone=?",new String[]{phone});
        return 0;
    }
    short Forget2(String phone,String passwords1,String password2) {//忘记密码
        if(passwords1.equals(password2)) {
            if(DataIsSame(null,"all_users","passwords",passwords1,"where phone=?",new String[]{phone})==1)
                return -1;
            if(DataUpdate(null,"all_users",new String[]{"passwords"},new String[]{passwords1},"phone",phone)) //更新数据库
                return 1;
            else
                return -2;
        }
        else
            return 0;
    }
    short EditPasswords(String user,String passwords0,String passwords1,String password2) {//editPassword
        if(passwords1.equals(password2)) {
            if(DataIsSame(null,"all_users","passwords",passwords0,"where user=?",new String[]{user})!=1)
                return -1;
            if(DataIsSame(null,"all_users","passwords",passwords1,"where user=?",new String[]{user})==1)
                return -2;
            if(DataUpdate(null,"all_users",new String[]{"passwords"},new String[]{passwords1},"user",user))
                return 1;
            else
                return -3;
        }
        else
            return 0;
    }

    //个人信息修改
    boolean UpdateData(String user,String details,String value){
        return DataUpdate(null, "all_users", new String[]{details}, new String[]{value}, "user", user);
    }
    boolean UpdateData(String user,String[] details,String[] value){
        return DataUpdate(null, "all_users", details, value, "user", user);
    }
    boolean UpdateData(String user,String details,byte[] value){
        return DataUpdate(null, "all_users", details, value, "user", user);
    }
    boolean GetUserItemPic(UserItem user,String where,String[] value){
        return GetDataUserPic(null,user,where,value);
    }

    //对话
    String GetUserName(String user){
        String name=GetDataUserString(null,"user",user);
        if(name!=null)
            return name;
        else
            return null;
    }
    ArrayList<ChatItems> GetChatList(String from){
        return GetDataChatItemsList(null,from);
    }
    boolean ChatSend(String toUser,String toName,ChatItem item){
        List<String> strList=GetDataALLUserItemUser(null);
        assert strList != null;
        for(int i = 0; i<strList.size(); i++) {
            if (!InsertData(null, item, "chat_" + strList.get(i), toUser, toName))
                return false;
        }
        return true;
    }
    boolean ChatSend(String fromUser,String toUser,String toName,ChatItem item){
        return InsertData(null, item, "chat_" + fromUser, toUser, toName);
    }
    boolean UpdateData(String table, String details,String value,String idType,String[] id){
        return DataUpdate(null, table, new String[]{details}, new String[]{value}, idType, id);
    }
    byte[] GetHead(String toUser) {
        return GetDataUserData(null,"head"," where user=?",new String[]{toUser});
    }
    boolean GetChatItemsPic(ChatItems chats,String where,String[] value){
        return GetDataChatItemsPic(null,chats,where,value);
    }

    //店铺
    ShopItem GetShopItem(String where,String[] value){
        ShopItem shop=GetDataShopItemList(null,where,value);
        if(shop!=null)
            return shop;
        else
            return null;
    }
    ArrayList<UserItem> GetUserItemByID(ArrayList<String> strList){
        ArrayList<UserItem> list=GetDataUserItemByID(null,strList);
        if(list!=null)
            return list;
        else
            return null;
    }
    boolean UpdateData(String user,ShopItem shop){
        return DataUpdate(null, "all_users", new String[]{"shop", "adProvince", "adCity", "adDistrict", "adDetails", "adText", "shopPhone"}, new String[]{shop.m_shop, shop.m_ad_province, shop.m_ad_city, shop.m_ad_district, shop.m_ad_details, shop.m_adText, shop.m_phone}, "user", user)
                && DataUpdate(null, "all_users", new String[]{"followed", "visited", "recommend"}, new int[]{shop.m_followed, shop.m_visited, shop.m_recommend}, "user", user)
                && DataUpdate(null, "all_users", "logo", shop.m_logo, "user", user);
    }
    boolean UpdateShopState(UserItem m_user,String user,String details,int value){
        int num;
        DatabaseHelper helper=DatabaseHelper.Act;
        try
        {
            SQLiteDatabase db1 = helper.getReadableDatabase();
            Cursor cursor = db1.rawQuery("select "+details+" from all_users where user=?",new String[]{user});
            if( cursor!=null && cursor.moveToFirst()) {
                num=cursor.getInt(0);
                cursor.close();
                db1.close();
            }
            else{
                assert cursor != null;
                cursor.close();
                db1.close();
                return false;
            }
            cursor.close();
            db1.close();
            //write
            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues cv=new ContentValues();
            cv.put(details,num+value);
            db.update("all_users", cv, "user=?", new String[]{user});
            db.close();
            switch (details){
                case "followed":
                    m_user.m_shop.m_followed+=value;
                    break;
                case "visited":
                    m_user.m_shop.m_visited+=value;
                    break;
            }
            return true;
        }
        catch(Exception e)
        {
            System.out.println("Error GetData:"+e);
        }
        return false;
    }

    //商品
    ArrayList<GoodItem> GetGoodItem(String where,String[] value){
        ArrayList<GoodItem> list=GetDataGoodItemList(null,where,value);
        if(list!=null)
            return list;
        else
            return null;
    }
    ArrayList<GoodItem> GetGoodItemByID(List<String> strList){
        ArrayList<GoodItem> list=GetDataGoodItemByID(null,strList);
        if(list!=null)
            return list;
        else
            return null;
    }
    boolean GetGoodItemPic(ArrayList<GoodItem> list,String where,String[] value){
        return GetDataGoodItemPic(null,list,where,value);
    }
    boolean NewItem(GoodItem item){
        return InsertData(null, item);
    }
    boolean EditItem(GoodItem item,String id){
        return DataUpdate(null, "good_items", new String[]{"picNum", "num", "price"}, new int[]{item.pic_num, item.num, item.price}, "id", id)
                && DataUpdate(null, "good_items", "pic1", item.pic1, "id", id)
                && DataUpdate(null, "good_items", "pic2", item.pic2, "id", id)
                && DataUpdate(null, "good_items", "pic3", item.pic3, "id", id)
                && DataUpdate(null, "good_items", new String[]{"name", "class1", "class2", "unit", "shop", "details", "id"}, new String[]{item.name, item.class1, item.class2, item.unit, item.shop, item.details, item.id}, "id", id);
    }
    boolean DownItem(GoodItem item){
        return DataUpdate(null, "good_items", new String[]{"state"}, new String[]{item.state}, "id", item.id) && DelFollow("",item.id,"goodItem");
    }
    boolean UpGoodItem(GoodItem item,String id){
        return DataUpdate(null, "good_items", new String[]{"picNum", "num", "price"}, new int[]{item.pic_num, item.num, item.price}, "id", id)
                && DataUpdate(null, "good_items", "pic1", item.pic1, "id", id)
                && DataUpdate(null, "good_items", "pic2", item.pic2, "id", id)
                && DataUpdate(null, "good_items", "pic3", item.pic3, "id", id)
                && DataUpdate(null, "good_items", new String[]{"name", "class1", "class2", "unit", "shop", "details", "state", "id"}, new String[]{item.name, item.class1, item.class2, item.unit, item.shop, item.details, item.state, item.id}, "id", id);
    }

    //关注
    ArrayList<String> GetFollow(String user,String type){
        ArrayList<String> list=GetFollowList(null," where type=? and user=?",new String[]{type,user});
        if(list!=null) {
            return list;
        }
        else {
            return null;
        }
    }
    boolean IsFollow(String user,String id,String type){
        List<String> list=GetFollowList(null, " where id=? and type=? and user=? LIMIT 1 ", new String[]{id, type, user});
        return (list!=null) && 0 != list.size();
    }
    boolean AddFollow(String user,String id,String type){
        return InsertData(null, id, type, user);
    }
    boolean DelFollow(String user,String id,String type){
        return DeleteData(null, id, type, user);
    }
    void UpdateFollow(UserItem user) {
        DatabaseHelper helper=DatabaseHelper.Act;
        try
        {
            //List
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor;
            cursor = db.rawQuery("select followed,visited  from all_users where user=?", new String[]{user.m_user});
            if (cursor != null) {
                cursor.moveToFirst();
                user.m_shop.m_followed=cursor.getInt(0);
                user.m_shop.m_visited=cursor.getInt(1);
                cursor.close();
            }
            db.close();
        }
        catch(Exception e)
        {
            System.out.println("Error GetData:"+e);
        }
    }
    //求购
    boolean NewItem(GetItem item){
        return item != null && InsertData(null,item);
    }
    ArrayList<GetItem> GetGetItemByID(ArrayList<String> strList){
        ArrayList<GetItem> list=GetDataGetItemByID(null,strList);
        if(list!=null)
            return list;
        else
            return null;
    }
    ArrayList<GetItem> GetGetItem(String where,String[] value){
        ArrayList<GetItem> list=GetDataGetItemList(null,where,value);
        if(list!=null)
            return list;
        else
            return null;
    }
    boolean EditItem(GetItem item,String id){
        return  DataUpdate(null, "get_items", new String[]{"num", "price"}, new int[]{item.num, item.price}, "id", id)
                && DataUpdate(null, "get_items", new String[]{"name", "unit", "getterName", "getterPhone", "shop","id"}, new String[]{item.name, item.unit, item.getterName, item.getterPhone, item.shop,item.id}, "id", id);
    }
    boolean DownItem(GetItem item){
        return DeleteData(null, item) && DelFollow("",item.id,"getItem");
    }

}
