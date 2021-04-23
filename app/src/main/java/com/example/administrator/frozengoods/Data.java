package com.example.administrator.frozengoods;

import android.os.Parcel;
import android.os.Parcelable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

class Data implements Parcelable {
    UserItem m_user;
    LocationInfo m_localInfo;
    ArrayList<ChatItems> m_chatList;
    ShopItem shop;

    public static final Creator<Data> CREATOR = new Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel in) {
            UserItem m_user = in.readParcelable(UserItem.class.getClassLoader());
            LocationInfo m_localInfo = in.readParcelable(LocationInfo.class.getClassLoader());
            ArrayList<ChatItems> m_chatList=new ArrayList<>();
            in.readTypedList(m_chatList,ChatItems.CREATOR);
            Data data = new Data();
            data.m_user = m_user;
            data.m_localInfo = m_localInfo;
            data.m_chatList = m_chatList;
            return data;
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(m_user,flags);
        dest.writeParcelable(m_localInfo,flags);
        dest.writeTypedList(m_chatList);
    }

    Data() {
        m_user = null;
        m_chatList = null;
        m_localInfo = null;
    }
    void clearPic(){
        if(m_user!=null){
            m_user.clearPic();
            for(int i=0;i<m_chatList.size();i++)
                m_chatList.get(i).clearPic();
        }
    }
//    void getPic(){
//        for(int i=0;i<m_chatList.size();i++)
//            m_chatList.get(i).getPic();
//        m_user.getPic();
//    }
    /*---接口---*/
    UserItem GetMine(){
        return m_user;
    }
    //登录
    boolean LogIn(String user) {
        m_user = DatabaseHelper.Act.GetDataUserItem(null, "where user=? or phone=?", new String[]{user, user});
        DatabaseHelper.Act.GetUserItemPic(m_user,"where user=? or phone=?", new String[]{user, user});
        if(m_user != null) {
            m_chatList = DatabaseHelper.Act.GetChatList(m_user.m_user);
        }
        else
            return false;
        return m_chatList!=null;
    }
    Boolean Logout(){
        m_user=null;
        m_chatList=null;
        return true;
    }
    //对话
    int GetUnReadNum() {
        int result=0;
        if(m_user!=null)
            for(int i=0;i<m_chatList.size();i++)
                result+=m_chatList.get(i).GetUnreadNum();
        return result;
    }
    int NewChat(String toUser){
        String fromUser=m_user.m_user;
        if(toUser.equals(fromUser)) {
            MainActivity.MainAct.Message("不能向自己发起消息");
            return -1;
        }
        int index;
        for(int i=0;i<m_chatList.size();i++) {
            if (m_chatList.get(i).toUser.equals(toUser)) {
                index = i;
                return index;
            }
        }
        String toName=DatabaseHelper.Act.GetUserName(toUser);
        ChatItem item = new ChatItem();
        item.user = fromUser;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        item.date = format.format(new Date());
        item.type = "text";
        item.data = null;
        item.words = "你向对方发起对话";
        item.state = "1";
        DatabaseHelper.Act.ChatSend(fromUser, toUser, toName, item);
        ChatItems items = new ChatItems();
        items.toUser=toUser;
        items.toName=toName;
        items.fromName=m_user.m_name;
        items.fromUser=m_user.m_user;
        items.items.add(item);
        m_chatList.add(items);
        return m_chatList.indexOf(items);
    }
}

class ChatItems implements Parcelable{
    String fromUser;
    String fromName;
    String toUser;
    String toName;
    ArrayList<ChatItem> items;
    ChatItems(){
        fromUser="";
        fromName="";
        toUser="";
        toName="";
        items=new ArrayList<>();
    }
    int GetUnreadNum(){
        int result=0;
        for(int i=0;i<items.size();i++)
            if(items.get(i).state.equals("0"))
                result++;
        return result;
    }
    void SetRead(){
        for(int i=0;i<items.size();i++)
            if(items.get(i).state.equals("0"))
                items.get(i).state="1";
    }
    String GetLastText() {
        if(items.size()>0) {
            String temp = items.get(items.size() - 1).type;
            if (temp.equals("text"))
                return items.get(items.size() - 1).words;
            else if (temp.equals("image"))
                return "[图片]";
        }
        return "";
    }
    String GetLastDate() {
        if(items.size()>0) {
            return items.get(items.size() - 1).date;
        }
        return "";
    }


    public static final Creator<ChatItems> CREATOR = new Creator<ChatItems>() {
        @Override
        public ChatItems createFromParcel(Parcel in) {
            String fromUser = in.readString();
            String fromName = in.readString();
            String toUser = in.readString();
            String toName = in.readString();
            ArrayList<ChatItem> items=new ArrayList<>();
            in.readTypedList(items,ChatItem.CREATOR);
            ChatItems chat = new ChatItems();
            chat.fromUser = fromUser;
            chat.fromName = fromName;
            chat.toUser = toUser;
            chat.toName = toName;
            chat.items = items;
            return chat;
        }

        @Override
        public ChatItems[] newArray(int size) {
            return new ChatItems[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fromUser);
        dest.writeString(fromName);
        dest.writeString(toUser);
        dest.writeString(toName);
        dest.writeTypedList(items);
    }

    void clearPic(){
        for(int i=0;i<items.size();i++)
            items.get(i).data=null;
    }
    void getPic(){
        DatabaseHelper.Act.GetChatItemsPic(ChatItems.this," where toUser=? order by toUser,date ASC LIMIT "+items.size(),new String[]{toUser});
    }
}

class ChatItem implements Parcelable{
    String date;
    String user;
    String state;
    String type;
    String words;
    byte[] data;
    ChatItem(){
        date="2016-01-01 00:00:00";
        user="";
        state="";
        type="";
        words="";
        data=null;
    }
    private ChatItem(Parcel in) {
        date = in.readString();
        user = in.readString();
        state = in.readString();
        type = in.readString();
        words = in.readString();
        int length = in.readInt() ;
        data=null;
        if(length>0) {
            data=new byte[length];
            in.readByteArray(data);
        }
    }
    public static final Creator<ChatItem> CREATOR = new Creator<ChatItem>() {
        @Override
        public ChatItem createFromParcel(Parcel in) {
            return new ChatItem(in);
        }

        @Override
        public ChatItem[] newArray(int size) {
            return new ChatItem[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeString(user);
        dest.writeString(state);
        dest.writeString(type);
        dest.writeString(words);
        if(data==null)
            dest.writeInt(0);
        else
            dest.writeInt(data.length);
        if(data!=null)
            dest.writeByteArray(data);
    }
}

class UserItem implements Parcelable{
    int m_id;
    String m_user;
    String m_passwords;
    String m_name;
    String m_phone;
    byte[] m_head;
    String m_sex;
    ShopItem m_shop;

    String m_province;
    String m_city;
    String m_district;
    UserItem(){
        m_user="";
        m_phone="";
        m_name="";
        m_passwords="";
        m_sex="";
        m_province="";
        m_city="";
        m_district="";
        m_head=null;
        m_shop=new ShopItem();
    }

    private UserItem(Parcel in) {
        m_id = in.readInt();
        m_user = in.readString();
        m_passwords = in.readString();
        m_name = in.readString();
        m_phone = in.readString();
        m_sex = in.readString();
        m_province = in.readString();
        m_city = in.readString();
        m_district = in.readString();
        int length = in.readInt() ;
        m_head=null;
        if(length>0) {
            m_head=new byte[length];
            in.readByteArray(m_head);
        }

        m_shop = in.readParcelable(ShopItem.class.getClassLoader());

    }
    public static final Creator<UserItem> CREATOR = new Creator<UserItem>() {
        @Override
        public UserItem createFromParcel(Parcel in) {
            return new UserItem(in);
        }

        @Override
        public UserItem[] newArray(int size) {
            return new UserItem[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(m_id);
        dest.writeString(m_user);
        dest.writeString(m_passwords);
        dest.writeString(m_name);
        dest.writeString(m_phone);
        dest.writeString(m_sex);
        dest.writeString(m_province);
        dest.writeString(m_city);
        dest.writeString(m_district);
        if(m_head==null)
            dest.writeInt(0);
        else
            dest.writeInt(m_head.length);
        if(m_head!=null)
            dest.writeByteArray(m_head);
        dest.writeParcelable(m_shop,flags);
    }
    void clearPic(){
        m_shop.m_logo=null;
        m_head=null;
    }
    void getPic(){
        DatabaseHelper.Act.GetUserItemPic(UserItem.this," where user=?",new String[]{m_user});
    }
}

class GoodItem implements Parcelable {
    String id;
    String user;
    String state;
    String province;
    String city;
    String district;

    int pic_num;
    String name;
    String class1;
    String class2;
    int num;
    int price;
    String unit;
    String shop;//店名
    String details;//商品介绍
    byte[] pic1;
    byte[] pic2;
    byte[] pic3;


    GoodItem(){
        id="";
        user="";
        state="";
        province="";
        city="";
        district="";
        pic_num=0;
        name="";
        class1="";
        class2="";
        num=0;
        price=-1;
        unit="";
        shop="";
        details="";
        pic1=null;
        pic2=null;
        pic3=null;
    }
    private GoodItem(Parcel in) {
        id = in.readString();
        user = in.readString();
        state = in.readString();
        province = in.readString();
        city = in.readString();
        district = in.readString();
        pic_num = in.readInt();
        name = in.readString();
        class1 = in.readString();
        class2 = in.readString();
        num = in.readInt();
        price = in.readInt();
        unit = in.readString();
        shop = in.readString();
        details = in.readString();

        int length1 = in.readInt() ;
        pic1=null;
        if(length1>0) {
            pic1=new byte[length1];
            in.readByteArray(pic1);
        }
        int length2 = in.readInt() ;
        pic2=null;
        if(length2>0) {
            pic2=new byte[length2];
            in.readByteArray(pic2);
        }
        int length3 = in.readInt() ;
        pic3=null;
        if(length3>0) {
            pic3=new byte[length3];
            in.readByteArray(pic3);
        }
    }
    public static final Creator<GoodItem> CREATOR = new Creator<GoodItem>() {
        @Override
        public GoodItem createFromParcel(Parcel in) {
            return new GoodItem(in);
        }

        @Override
        public GoodItem[] newArray(int size) {
            return new GoodItem[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(user);
        dest.writeString(state);
        dest.writeString(province);
        dest.writeString(city);
        dest.writeString(district);
        dest.writeInt(pic_num);
        dest.writeString(name);
        dest.writeString(class1);
        dest.writeString(class2);
        dest.writeInt(num);
        dest.writeInt(price);
        dest.writeString(unit);
        dest.writeString(shop);
        dest.writeString(details);
        if(pic1==null)
            dest.writeInt(0);
        else {
            dest.writeInt(pic1.length);
            dest.writeByteArray(pic1);
        }

        if(pic2==null)
            dest.writeInt(0);
        else{
            dest.writeInt(pic2.length);
            dest.writeByteArray(pic2);
        }

        if(pic3==null)
            dest.writeInt(0);
        else{
            dest.writeInt(pic3.length);
            dest.writeByteArray(pic3);
        }
    }
    void clearPic(){
        pic1=null;
        pic2=null;
        pic3=null;
    }
    void getPic(){
        DatabaseHelper.Act.GetGoodItemPic(new ArrayList<GoodItem>(){{add(GoodItem.this);}}," where id=?",new String[]{id});
    }
}

class ShopItem implements Parcelable{
    String m_shop;//店名
    String m_ad_province;//地址1
    String m_ad_city;//地址1
    String m_ad_district;//地址1
    String m_ad_details;//地址2
    String m_adText;//标语
    String m_phone;
    int m_followed;//关注数
    int m_visited;//被浏览
    int m_recommend;//推荐数
    byte[] m_logo ;//icon
    ShopItem(){
        m_shop="";
        m_ad_province="";
        m_ad_city="";
        m_ad_district="";
        m_ad_details="";
        m_adText="";
        m_phone="";
        m_followed=0;
        m_visited=0;
        m_recommend=0;
        m_logo=null;
    }
    void setValue(String shop,String ad_province,String ad_city,String ad_district,String ad_details,String adText,String phone,byte[] logo,int followed,int visited,int recommend){
        m_shop=shop;
        m_ad_province=ad_province;
        m_ad_city=ad_city;
        m_ad_district=ad_district;
        m_ad_details=ad_details;
        m_adText=adText;
        m_phone=phone;
        m_followed=followed;
        m_visited=visited;
        m_recommend=recommend;
        m_logo=logo;
    }
    private ShopItem(Parcel in) {
        m_shop = in.readString();
        m_ad_province = in.readString();
        m_ad_city = in.readString();
        m_ad_district = in.readString();
        m_ad_details = in.readString();
        m_adText = in.readString();
        m_phone = in.readString();
        m_followed = in.readInt();
        m_visited = in.readInt();
        m_recommend = in.readInt();
        int length = in.readInt() ;
        m_logo=null;
        if(length>0) {
            m_logo=new byte[length];
            in.readByteArray(m_logo);
        }
    }
    public static final Creator<ShopItem> CREATOR = new Creator<ShopItem>() {
        @Override
        public ShopItem createFromParcel(Parcel in) {
            return new ShopItem(in);
        }

        @Override
        public ShopItem[] newArray(int size) {
            return new ShopItem[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(m_shop);
        dest.writeString(m_ad_province);
        dest.writeString(m_ad_city);
        dest.writeString(m_ad_district);
        dest.writeString(m_ad_details);
        dest.writeString(m_adText);
        dest.writeString(m_phone);
        dest.writeInt(m_followed);
        dest.writeInt(m_visited);
        dest.writeInt(m_recommend);
        if(m_logo==null)
            dest.writeInt(0);
        else{
            dest.writeInt(m_logo.length);
            dest.writeByteArray(m_logo);
        }
    }
}

class GetItem implements Parcelable{
    String id;
    String user;
    String province;
    String city;
    String district;

    String class1;
    String class2;
    String name;
    int num;
    int price;
    String unit;
    String getterName;
    String getterPhone;
    String shop;

    GetItem(){
        id="";
        user="";

        province="";
        city="";
        district="";

        class1="全部";
        class2="全部";
        name="";
        num=0;
        price=-1;
        unit="斤";
        getterName="";
        getterPhone="";
        shop="";
    }
    private GetItem(Parcel in) {
        id = in.readString();
        user = in.readString();
        province = in.readString();
        city = in.readString();
        district = in.readString();
        class1 = in.readString();
        class2 = in.readString();
        name = in.readString();
        num = in.readInt();
        price = in.readInt();
        unit = in.readString();
        getterName = in.readString();
        getterPhone = in.readString();
        shop = in.readString();
    }
    public static final Creator<GetItem> CREATOR = new Creator<GetItem>() {
        @Override
        public GetItem createFromParcel(Parcel in) {
            return new GetItem(in);
        }

        @Override
        public GetItem[] newArray(int size) {
            return new GetItem[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(user);
        dest.writeString(province);
        dest.writeString(city);
        dest.writeString(district);
        dest.writeString(class1);
        dest.writeString(class2);
        dest.writeString(name);
        dest.writeInt(num);
        dest.writeInt(price);
        dest.writeString(unit);
        dest.writeString(getterName);
        dest.writeString(getterPhone);
        dest.writeString(shop);
    }
}

class LocationInfo  implements Parcelable {
    String Province;
    String City;
    String District;
    String Error;
    LocationInfo(){
        Province="";
        City="";
        District="";
        Error="";
    }

    private LocationInfo(Parcel in) {
        Province = in.readString();
        City = in.readString();
        District = in.readString();
        Error = in.readString();
    }
    public static final Creator<LocationInfo> CREATOR = new Creator<LocationInfo>() {
        @Override
        public LocationInfo createFromParcel(Parcel in) {
            return new LocationInfo(in);
        }

        @Override
        public LocationInfo[] newArray(int size) {
            return new LocationInfo[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Province);
        dest.writeString(City);
        dest.writeString(District);
        dest.writeString(Error);
    }
}