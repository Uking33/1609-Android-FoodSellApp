package com.example.administrator.frozengoods;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import java.util.regex.Pattern;

public class InfoUser extends UActivity {
    private Button btn1, btn2, btn3;
    private EditText edit1, edit2, edit3;
    private int m_state;
    private String tempStr1,tempStr2;
    private CountDownTimer countdown;
    private String m_user;
    //Create
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState,false);
        m_itemLayout.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(edit1!=null) imm.hideSoftInputFromWindow(edit1.getWindowToken(), 0);
                        if(edit2!=null) imm.hideSoftInputFromWindow(edit2.getWindowToken(), 0);
                        if(edit3!=null) imm.hideSoftInputFromWindow(edit3.getWindowToken(), 0);
                        break;
                }
                return false;
            }
        });
        if (m_id.equals("INFO_PASSWORDS")) {
            m_state = 2;
            m_user=getIntent().getStringExtra("user");
            EditPasswords();
        }
        else{
            m_state = 1;
            Login();
        }
    }
    @Override protected void onDestroy(){
        btn1=null;
        btn2=null;
        btn3=null;
        edit1=null;
        edit2=null;
        edit3=null;
        tempStr1=null;
        tempStr2=null;
        countdown=null;
        m_user=null;
        super.onDestroy();
        setContentView(R.layout.view_null);
    }

    //Turn
    @Override public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
    @Override public void Back() {
        switch (m_tab){
            case "EditPasswords":
                onBackPressed();
                break;
            case "Login":
                onBackPressed();
                break;
            case "Register1":
                if(countdown!=null) countdown.cancel();
                Login();
                break;
            case "Register2":
                Register1();
                break;
            case "Forget1":
                if(countdown!=null) countdown.cancel();
                switch (m_state) {
                    case 1:
                        Login();
                        break;
                    case 2:
                        EditPasswords();
                        break;
                }
                break;
            case "Forget2":
                Forget1();
                break;
        }
        m_back_over.getBackground().setAlpha(0);
    }
    void Login(String user,String passwords){
        switch (DatabaseHelper.Act.IsLogIn(user,passwords)) {
            case -3:
                Message("账号或密码不能为空");
                break;
            case -2:
                Message("登录出错，请联系客服");
                break;
            case -1:
                Message("账号不存在");
                break;
            case 0:
                Message("帐号或密码不正确");
                break;
            case 1:
                Message("登录成功");
                Intent intent = new Intent();
                intent.putExtra("user", user);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    //Layout
    void Clean() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(edit1!=null) imm.hideSoftInputFromWindow(edit1.getWindowToken(), 0);
        if(edit2!=null) imm.hideSoftInputFromWindow(edit2.getWindowToken(), 0);
        if(edit3!=null) imm.hideSoftInputFromWindow(edit3.getWindowToken(), 0);
        edit1=null;
        edit2=null;
        edit3=null;
        btn1=null;
        btn2=null;
        btn3=null;
        m_tab="";
        m_itemLayout.removeAllViews();
        View view=new View(this);
        FrameLayout.LayoutParams Params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,82);
        m_itemLayout.addView(view,Params);
    }
    //修改密码
    void EditPasswords() {
        Clean();
        m_tab="EditPasswords";
        m_title.setText("修改密码");
        initTextEdit(this, m_itemLayout, R.id.edit_info_user_1, "原密码", "请输入您的原密码", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD, 85, 30);
        initTextEdit(this, m_itemLayout, R.id.edit_info_user_2, "新密码", "由6-20字符组成", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD, 85, 30);
        initTextEdit(this, m_itemLayout, R.id.edit_info_user_3, "确定密码", "请再次输入新密码", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD, 85, 30);
        initButton(this, m_itemLayout, R.id.btn_info_user_1, "完  成", 30, 87, new int[]{R.color.white,R.color.eghitygray}, new int[]{R.drawable.btn_green,R.drawable.btn_green_over});
        initButton(this, m_itemLayout, R.id.btn_info_user_2, "忘记密码？", 20, 0, new int[]{R.color.sixtygray, R.color.sixtygray}, new int[]{R.color.white, R.color.white});


        edit1 = (EditText) findViewById(R.id.edit_info_user_1);
        edit2 = (EditText) findViewById(R.id.edit_info_user_2);
        edit3 = (EditText) findViewById(R.id.edit_info_user_3);
        edit1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        edit2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        edit3.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        btn1 = (Button) findViewById(R.id.btn_info_user_1);
        btn2 = (Button) findViewById(R.id.btn_info_user_2);
        btn1.setBackgroundResource(R.drawable.btn_green_disactivity);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                int length1 = edit1.getText().toString().length();
                int length2 = edit2.getText().toString().length();
                int length3 = edit2.getText().toString().length();
                if (length1 >= 6 && length1 <= 20 && length2 >= 6 && length2 <= 20 && length3 >= 6 && length2 <= 20) {
                    btn1.setEnabled(true);
                    btn1.setBackgroundResource(R.drawable.btn_green);
                }
                else {
                    btn1.setEnabled(false);
                    btn1.setBackgroundResource(R.drawable.btn_green_disactivity);
                }
            }
        };
        edit1.addTextChangedListener(textWatcher);
        edit2.addTextChangedListener(textWatcher);
        edit3.addTextChangedListener(textWatcher);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*String str="[a-zA-Z0-9.~!@#$%^&*?]+$";
                Pattern pattern = Pattern.compile(str);
                if (!pattern.matcher(edit1.getText().toString()).matches()) {
                    m_parent.Message("帐号必须由由6-20字符组成");
                    return;
                }*/
                switch (DatabaseHelper.Act.EditPasswords(m_user,edit1.getText().toString(), edit2.getText().toString(), edit3.getText().toString())) {
                    case -3:
                        Message("更新数据出错，请联系客服");
                        break;
                    case -2:
                        Message("新密码与原密码不能相同");
                        break;
                    case -1:
                        Message("原密码错误");
                        break;
                    case 0:
                        Message("新密码不一致");
                        break;
                    case 1:
                        Message("修改密码成功");
                        finish();
                        break;
                }
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Forget1();
            }
        });
    }
    //登录界面
    public void Login() {
        Clean();
        m_tab = "Login";
        m_title.setText("帐 号 登 录");
        initTextEdit(this, m_itemLayout, R.id.edit_info_user_1, "帐  号", "用户名/手机号", InputType.TYPE_CLASS_TEXT, 100, 35);
        initTextEdit(this, m_itemLayout, R.id.edit_info_user_2, "密  码", "请输入密码", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD, 100, 35);
        initButton(this, m_itemLayout, R.id.btn_info_user_1, "登  录", 30, 87, new int[]{R.color.white, R.color.eghitygray}, new int[]{R.drawable.btn_green, R.drawable.btn_green_over});
        initButton(this, m_itemLayout, R.id.btn_info_user_2, "注  册", 30, 16, new int[]{R.color.sixtygray, R.color.fiftygray}, new int[]{R.drawable.btn_white, R.drawable.btn_white_over});
        initButton(this, m_itemLayout, R.id.btn_info_user_3, "忘记密码？", 20, 0, new int[]{R.color.sixtygray, R.color.sixtygray}, new int[]{R.color.white, R.color.white});

        edit1 = (EditText) findViewById(R.id.edit_info_user_1);
        edit2 = (EditText) findViewById(R.id.edit_info_user_2);

        btn1 = (Button) findViewById(R.id.btn_info_user_1);
        btn2 = (Button) findViewById(R.id.btn_info_user_2);
        btn3 = (Button) findViewById(R.id.btn_info_user_3);

        edit1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        edit2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login(edit1.getText().toString(), edit2.getText().toString());
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register1();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Forget1();
            }
        });
    }
    //注册界面1
    void Register1() {
        Clean();
        m_tab="Register1";
        m_title.setText("注册");
        initTextEdit(this, m_itemLayout, R.id.edit_info_user_1, "帐号", "由6-20数字和字母和下划线组成", InputType.TYPE_CLASS_TEXT, 100, 35);
        initTextEdit(this, m_itemLayout, R.id.edit_info_user_2, "手机号", "请输入手机号码", InputType.TYPE_CLASS_NUMBER, 100, 35);
        UTextEdit uEdit=initTextEdit(this, m_itemLayout, R.id.edit_info_user_3, "验证码", "请输入验证码", InputType.TYPE_CLASS_TEXT, 100, 35);
        initButton(this, m_itemLayout, R.id.btn_info_user_1, "下一步", 30, 87, new int[]{R.color.white,R.color.eghitygray}, new int[]{R.drawable.btn_green,R.drawable.btn_green_over});
        initButton(this, uEdit.m_layout, R.id.btn_info_user_2, "获取验证码", 30, 0, new int[]{R.color.white,R.color.eghitygray}, new int[]{R.drawable.btn_blue,R.drawable.btn_blue_over});
        btn1 = (Button) findViewById(R.id.btn_info_user_1);
        btn2 = (Button) findViewById(R.id.btn_info_user_2);


        btn1.setEnabled(false);
        btn2.setEnabled(true);
        btn1.setBackgroundResource(R.drawable.btn_green_disactivity);
        edit1 = (EditText) findViewById(R.id.edit_info_user_1);
        edit2 = (EditText) findViewById(R.id.edit_info_user_2);
        edit3 = (EditText) findViewById(R.id.edit_info_user_3);
        edit1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        edit2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        edit3.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                int length = edit1.getText().toString().length();
                if (length >= 6 && length <= 20 && !edit2.getText().toString().isEmpty() && !edit3.getText().toString().isEmpty()) {
                    btn1.setEnabled(true);
                    btn1.setBackgroundResource(R.drawable.btn_green);
                }
                else {
                    btn1.setEnabled(false);
                    btn1.setBackgroundResource(R.drawable.btn_green_disactivity);
                }
            }
        };
        edit1.addTextChangedListener(textWatcher);
        edit2.addTextChangedListener(textWatcher);
        edit3.addTextChangedListener(textWatcher);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否正确
                String str="^[a-zA-Z]\\w{5,19}$";
                Pattern pattern = Pattern.compile(str);
                if (!pattern.matcher(edit1.getText().toString()).matches()) {
                    Message("帐号必须由由6-20数字和字母和下划线组成,只能字母");
                    return;
                }
                if (edit2.getText().length() != 11) {
                    Message("手机号有误");
                    return;
                }
                if (edit3.getText().length() != 4) {
                    Message("验证码不正确");
                    return;
                }
                switch(DatabaseHelper.Act.Register1(edit1.getText().toString(), edit2.getText().toString(), edit3.getText().toString())){
                    case -2:
                        Message("登录出错，请联系客服");
                        break;
                    case -1:
                        if (countdown != null) countdown.cancel();
                        tempStr1 = edit1.getText().toString();
                        tempStr2 = edit2.getText().toString();
                        Register2();
                        break;
                    case 0:
                        Message("手机号已被注册");
                        break;
                    case 1:
                        Message("账号已被使用");
                        break;
                    default:
                        break;
                }
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countdown= new CountDownTimer(30000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {// 倒计时中的方法
                        btn2.setEnabled(false);
                        String str=String.valueOf(millisUntilFinished/1000)+"秒后可重发";
                        btn2.setText(str);
                        btn2.setBackgroundResource(R.drawable.btn_blue_disactivity);
                    }
                    @Override
                    public void onFinish() {
                        btn2.setEnabled(true);
                        btn2.setText("获取验证码");
                        btn2.setBackgroundResource(R.drawable.btn_blue);
                    }
                }.start();// 开始倒计时的方法
            }
        });
    }
    //注册界面2
    void Register2() {
        Clean();
        m_tab="Register2";
        m_title.setText("设置密码");
        initTextEdit(this, m_itemLayout, R.id.edit_info_user_1, "密码", "由6-20字符组成", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD, 100, 35);
        initTextEdit(this, m_itemLayout, R.id.edit_info_user_2, "确认密码", "请再次输入密码", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD, 100, 35);
        initButton(this, m_itemLayout, R.id.btn_info_user_1, "确定", 30, 87, new int[]{R.color.white,R.color.eghitygray}, new int[]{R.drawable.btn_green,R.drawable.btn_green_over});
        btn1 = (Button) findViewById(R.id.btn_info_user_1);
        assert btn1 != null;
        btn1.setBackgroundResource(R.drawable.btn_green_disactivity);
        edit1 = (EditText) findViewById(R.id.edit_info_user_1);
        edit2 = (EditText) findViewById(R.id.edit_info_user_2);
        edit1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        edit2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                int length1 = edit1.getText().toString().length();
                int length2 = edit2.getText().toString().length();
                if (length1 >= 6 && length1 <= 20 && length2 >= 6 && length2 <= 20) {
                    btn1.setEnabled(true);
                    btn1.setBackgroundResource(R.drawable.btn_green);
                }
                else {
                    btn1.setEnabled(false);
                    btn1.setBackgroundResource(R.drawable.btn_green_disactivity);
                }
            }
        };
        edit1.addTextChangedListener(textWatcher);
        edit2.addTextChangedListener(textWatcher);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (DatabaseHelper.Act.Register2(tempStr1, tempStr2, edit1.getText().toString(), edit2.getText().toString())) {
                    case 1:
                        Message(tempStr1 + "帐号注册成功");
                        Login(tempStr1, edit1.getText().toString());
                        break;
                    case 0:
                        Message("密码不一致");
                        break;
                }
            }
        });
    }
    //忘记密码1
    void Forget1() {
        Clean();
        m_tab="Forget1";
        m_title.setText("忘记密码");
        initTextEdit(this, m_itemLayout, R.id.edit_info_user_1, "手机号", "请输入手机号码", InputType.TYPE_CLASS_NUMBER, 100, 35);
        UTextEdit uEdit=initTextEdit(this, m_itemLayout, R.id.edit_info_user_2, "验证码", "请输入验证码", InputType.TYPE_CLASS_TEXT, 100, 35);
        initButton(this, m_itemLayout, R.id.btn_info_user_1, "下一步", 30, 87, new int[]{R.color.white,R.color.eghitygray}, new int[]{R.drawable.btn_green,R.drawable.btn_green_over});
        initButton(this, uEdit.m_layout, R.id.btn_info_user_2, "获取验证码", 30, 0, new int[]{R.color.white,R.color.eghitygray}, new int[]{R.drawable.btn_blue,R.drawable.btn_blue_over});
        edit1 = (EditText) findViewById(R.id.edit_info_user_1);
        edit2 = (EditText) findViewById(R.id.edit_info_user_2);
        edit1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        edit2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        btn1 = (Button) findViewById(R.id.btn_info_user_1);
        btn2 = (Button) findViewById(R.id.btn_info_user_2);
        btn1.setBackgroundResource(R.drawable.btn_green_disactivity);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!edit1.getText().toString().isEmpty() && !edit2.getText().toString().isEmpty()) {
                    btn1.setEnabled(true);
                    btn1.setBackgroundResource(R.drawable.btn_green);
                }
                else {
                    btn1.setEnabled(false);
                    btn1.setBackgroundResource(R.drawable.btn_green_disactivity);
                }
            }
        };
        edit1.addTextChangedListener(textWatcher);
        edit2.addTextChangedListener(textWatcher);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否正确
                if (edit1.getText().length() != 11) {
                    Message("手机号有误");
                    return;
                }
                if (edit2.getText().length() != 4) {
                    Message("验证码不正确");
                    return;
                }

                switch(DatabaseHelper.Act.Forget1(edit1.getText().toString(), edit2.getText().toString())){
                    case -2:
                        Message("登录出错，请联系客服");
                        break;
                    case -1:
                    case 0:
                        Message("手机号未被注册");
                        break;
                    case 1:
                        if (countdown != null) countdown.cancel();
                        tempStr1 = edit1.getText().toString();
                        Forget2();
                        break;
                }
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countdown= new CountDownTimer(30000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {// 倒计时中的方法
                        btn2.setEnabled(false);
                        String str=millisUntilFinished / 1000+"秒后可重发";
                        btn2.setText(str);
                        btn2.setBackgroundResource(R.drawable.btn_blue_disactivity);
                    }
                    @Override
                    public void onFinish() {
                        btn2.setEnabled(true);
                        btn2.setText("获取验证码");
                        btn2.setBackgroundResource(R.drawable.btn_blue);
                    }
                }.start();// 开始倒计时的方法
            }
        });
    }
    //忘了密码2
    void Forget2() {
        Clean();
        m_tab="Forget2";
        m_title.setText("忘记密码");
        initTextEdit(this, m_itemLayout, R.id.edit_info_user_1, "密码", "由6-20字符组成", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD, 100, 35);
        initTextEdit(this, m_itemLayout, R.id.edit_info_user_2, "确认密码", "请再次输入密码", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD, 100, 35);
        initButton(this, m_itemLayout, R.id.btn_info_user_1, "确定", 30, 87, new int[]{R.color.white,R.color.eghitygray}, new int[]{R.drawable.btn_green,R.drawable.btn_green_over});
        btn1 = (Button) findViewById(R.id.btn_info_user_1);
        assert btn1 != null;
        btn1.setBackgroundResource(R.drawable.btn_green_disactivity);
        edit1 = (EditText) findViewById(R.id.edit_info_user_1);
        edit2 = (EditText) findViewById(R.id.edit_info_user_2);
        edit1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        edit2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                int length1 = edit1.getText().toString().length();
                int length2 = edit1.getText().toString().length();
                if (length1 >= 6 && length1 <= 20 && length2 >= 6 && length2 <= 20) {
                    btn1.setEnabled(true);
                    btn1.setBackgroundResource(R.drawable.btn_green);
                }
                else {
                    btn1.setEnabled(false);
                    btn1.setBackgroundResource(R.drawable.btn_green_disactivity);
                }
            }
        };
        edit1.addTextChangedListener(textWatcher);
        edit2.addTextChangedListener(textWatcher);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (DatabaseHelper.Act.Forget2(tempStr1, edit1.getText().toString(),edit2.getText().toString())) {
                    case -2:
                        Message("更新数据出错，请联系客服");
                        break;
                    case -1:
                        Message("新密码与原密码不能相同");
                        break;
                    case 0:
                        Message("新密码不一致");
                        break;
                    case 1:
                        switch (m_state) {
                            case 1:
                                Login();
                                break;
                            case 2:
                                EditPasswords();
                                break;
                        }
                        break;
                }
            }
        });
    }

    //Item
    public UTextEdit initTextEdit(UActivity m_parent, LinearLayout itemLayout, int id, String label, String tips, int type, int height, int size){
        UTextEdit uEdit=new UTextEdit(m_parent,id,label,tips,type,size,470,152);
        uEdit.addToView(itemLayout,height,46);
        return uEdit;
    }
    public void initButton(UActivity m_parent, FrameLayout itemLayout, int id, String text, int size, int rightMargin, int[] textColor, int[] backgroundColor){
        UButton uBtn=new UButton(m_parent,id,text,size,190, 52,textColor,backgroundColor);
        uBtn.addToView(itemLayout,rightMargin);
    }
    public void initButton(UActivity m_parent, LinearLayout itemLayout, int id, String text, int size, int topMargin, int[] textColor, int[] backgroundColor){
        UButton uBtn=new UButton(m_parent,id,text,size,298, 72,textColor,backgroundColor);
        uBtn.addToView(itemLayout,topMargin);
    }
}
