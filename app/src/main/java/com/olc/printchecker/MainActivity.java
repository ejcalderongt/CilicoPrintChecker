package com.olc.printchecker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;

import Printer.PrintHelper;
import viewpagerindicator.TabPageIndicator;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private String[] mTabArrays;
    private ViewPager mViewPager;
    private ArrayList<Fragment> mFragmentList;
    private MyFragmentPagerAdapter mMyFrageStatePagerAdapter;
    private static boolean isOutPaper = false;
    PrintHelper mPrinterhelp = new PrintHelper();

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10000);

        }

        mTabArrays = new String[]
                {getResources().getString(R.string.check_title),
                        getResources().getString(R.string.scan_title),
                        getResources().getString(R.string.print_images),
                        getResources().getString(R.string.web_title)
                };
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("PrintChecker");
        toolbar.setTitleTextColor(Color.WHITE);
        ImageButton helpButton = (ImageButton) findViewById(R.id.btn_help);
        helpButton.setOnClickListener(this);
        TabPageIndicator tabPageIndicator = (TabPageIndicator) findViewById(R.id.indicator);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mFragmentList = new ArrayList<Fragment>();
        mFragmentList.add(new PrintCheckFragment());
        mFragmentList.add(new ScanPrintFragment());
        mFragmentList.add(new PrintImageFragment());
        mFragmentList.add(new WebPrintFragment());
        mMyFrageStatePagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(mMyFrageStatePagerAdapter);
        tabPageIndicator.setViewPager(mViewPager);
        mViewPager.setCurrentItem(1);

        // 设备缺纸检测 (医慧科技)
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("com.printer.printerror");
//        registerReceiver(receiver, filter);
//        Log.i("PrintStater", "注册广播");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPrinterhelp.Open(this);

        // 缺纸检测
//        mPrinterhelp.getPageStatus();
    }

    // 缺纸检测广播
    /*private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("PrintStater", "收到广播");
            String action = intent.getAction();
            String printerror = intent.getStringExtra("printerror");
            Log.i("PrintStater", "action" + action);
            if ("paper".equals(printerror)) {
                Log.i("PrintStater", "paper");
                // Toast.makeText(MainActivity.this, "paper", Toast.LENGTH_SHORT).show();
            } else if ("nopaper".equals(printerror)) {
                Log.i("PrintStater", "nopaper");
                Toast.makeText(MainActivity.this, "缺纸 nopaper", Toast.LENGTH_SHORT).show();
            } else if ("ready".equals(printerror)) {
                Log.i("PrintStater", "ready");
                // Toast.makeText(MainActivity.this, "ready", Toast.LENGTH_SHORT).show();
            } else if ("printing".equals(printerror)) {
                Log.i("PrintStater", "printing");
                // Toast.makeText(MainActivity.this, "printing", Toast.LENGTH_SHORT).show();
            } else if ("busy".equals(printerror)) {
                Log.i("PrintStater", "busy");
                // Toast.makeText(MainActivity.this, "busy", Toast.LENGTH_SHORT).show();
            } else if ("finish".equals(printerror)) {
                Log.i("PrintStater", "finish");
                // Toast.makeText(MainActivity.this, "finish", Toast.LENGTH_SHORT).show();
            }
        }
    };*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_help:
                Intent helpIntent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(helpIntent);
                break;
        }
    }

    class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> list;

        public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
            super(fm);
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Fragment getItem(int arg0) {
            return list.get(arg0);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabArrays[position];
        }

        @Override
        public float getPageWidth(int position) {
            return super.getPageWidth(position);
        }
    }
}
