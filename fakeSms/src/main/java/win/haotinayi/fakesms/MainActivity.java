package win.haotinayi.fakesms;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import win.haotinayi.fakesms.strategy.HighStrategy;
import win.haotinayi.fakesms.strategy.LowStrategy;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R2.id.et_phone)
    TextInputEditText mEtPhone;
    @BindView(R2.id.et_time)
    TextInputEditText mEtTime;
    @BindView(R2.id.et_context)
    TextInputEditText mEtContext;
    @BindView(R2.id.insert_sms)
    Button mInsertSms;
    @BindView(R2.id.til_time)
    TextInputLayout mTilTime;
    @BindView(R2.id.til_content)
    TextInputLayout mTilContent;
    @BindView(R2.id.btn)
    Button mBtn;
    @BindView(R2.id.tv_hint)
    TextView mTvHint;

    private String systemSms;
    private SharedPreferences mIndex;
    private HighStrategy mHighStrategy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();

        mTilTime.setCounterEnabled(true);
        mTilTime.setError("请输入正确的时间格式");

        mTilContent.setCounterEnabled(true);
        mTilContent.setError("请输入确保短信内容在140字符之内");

        initListener();

        mIndex = getSharedPreferences("index", MODE_PRIVATE);

//      添加策略模式
        if (Build.VERSION.SDK_INT >= 20) {
            mHighStrategy = new HighStrategy(MainActivity.this, mBtn);
            mHighStrategy.editSharedPreference(getSharedPreferences("index", MODE_PRIVATE));
        } else {
            new LowStrategy().setButtonStatus(mBtn).setHintStatus(mTvHint);
        }

    }


    /**
     * 监听事件是否正确
     */
    private void initListener() {
        mEtTime.addTextChangedListener(new SmsWatcher(new SmsWatcher.TextChangingListener() {
            @Override
            public void onChange(Editable s) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = s.toString();
                try {
                    long timeStart = sdf.parse(time).getTime();
                    mTilTime.setErrorEnabled(false);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }));

        mEtContext.addTextChangedListener(new SmsWatcher(new SmsWatcher.TextChangingListener() {
            @Override
            public void onChange(Editable s) {
                if (s.toString().length() < 140) {
                    mTilContent.setErrorEnabled(false);
                }
            }
        }));


    }

    public void insertSms(View view) {

        if (Build.VERSION.SDK_INT >= 20) {
            if (!getPackageName().equals(getSystemDefaultSms())) {
                setDefaultSms(getPackageName());
            }
        }


        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = mEtTime.getText().toString();

            ContentValues values = new ContentValues();
            long timeStart = sdf.parse(time).getTime();
            values.put("date", new Long(timeStart));

            values.put("address", mEtPhone.getText().toString());
            values.put("body", mEtContext.getText().toString());
            values.put("type", "2");
            values.put("read", "1");//"1"means has read ,1表示已读

            System.out.println(mEtPhone.getText().toString() + "----------------");

            if (mEtPhone.getText().toString().isEmpty()) {
                Toast.makeText(MainActivity.this, "请输入电话号码", Toast.LENGTH_SHORT).show();
                throw new Exception("error 内容是空");
            }

            if (mEtPhone.getText().toString().isEmpty()) {
                Toast.makeText(MainActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
                throw new Exception("error 内容是空");
            }


            getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
            Toast.makeText(MainActivity.this, "短信插入成功，部分手机的收件箱有延迟，请等候", Toast.LENGTH_SHORT).show();

        } catch (ParseException e) {
            Toast.makeText(MainActivity.this, "时间输入异常，请重新尝试", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String getSystemDefaultSms() {
        return Telephony.Sms.getDefaultSmsPackage(this);
    }

    /**
     * 设置默认的短信
     *
     * @param packageName
     */
    public void setDefaultSms(String packageName) {
        mHighStrategy.setDefaultSms(packageName);
    }

    /**
     * 按钮的变化
     * @param view
     */
    public void onclickSetSms(View view) {
        mHighStrategy.onclickSetSms(mIndex);
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item_android clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Toast.makeText(MainActivity.this, "如果您觉得本软件好用，请到GitHub给一个星", Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse("https://github.com/HaoTianYi/");
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(it);
        } else if (id == R.id.nav_gallery) {
            Toast.makeText(MainActivity.this, "如果您觉得本软件好用，请到GitHub给一个星", Toast.LENGTH_SHORT).show();
            Uri uri1 = Uri.parse("http://blog.csdn.net/simaxiaochen/");
            Intent it1 = new Intent(Intent.ACTION_VIEW, uri1);
            startActivity(it1);
        } else if (id == R.id.nav_slideshow) {
            Toast.makeText(MainActivity.this, "如果您觉得本软件好用，请到GitHub给一个星", Toast.LENGTH_SHORT).show();
            Uri uri2 = Uri.parse("http://gold.xitu.io/user/5821a7c2d20309005514b249/");
            Intent it2 = new Intent(Intent.ACTION_VIEW, uri2);
            startActivity(it2);
        } else if (id == R.id.nav_manage) {
            Toast.makeText(MainActivity.this, "如果您觉得本软件好用，请到GitHub给一个星", Toast.LENGTH_SHORT).show();
            Uri uri3 = Uri.parse("http://www.haotianyi.win/");
            Intent it3 = new Intent(Intent.ACTION_VIEW, uri3);
            startActivity(it3);
        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(MainActivity.this, UseActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
