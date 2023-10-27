package com.olc.printcilico;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private File file1;
    private File ffile;
    private int copies=1;
    private String fname="";
    private Context mContext = null;
    private PrintHelper.PrintType mTitleType = PrintHelper.PrintType.Centering;
    private boolean titleBold = true;
    private int mTitleTextSize = 40;
    private int mLineTextSize = 18;
    private int mValueTextSize = 20;
    private int mKeyTextSize = 24;
    private String mTitleStr = "Registro de lectura";
    private int mOffsetX = 210;
    PrintHelper printHelper = null;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10000);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 20000);
        }

        try {

            mTabArrays = new String[]
                    {getResources().getString(R.string.check_title),
                            getResources().getString(R.string.scan_title),
                            getResources().getString(R.string.print_images),
                            getResources().getString(R.string.web_title)
                    };
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("PrinterDriver");
            toolbar.setTitleTextColor(Color.WHITE);
            ImageButton helpButton = (ImageButton) findViewById(R.id.btn_help);
            helpButton.setOnClickListener(this);
            TabPageIndicator tabPageIndicator = (TabPageIndicator) findViewById(R.id.indicator);

            Bundle bundle = getIntent().getExtras();
            processBundle(bundle);

            mViewPager = (ViewPager) findViewById(R.id.viewpager);
            mFragmentList = new ArrayList<>();
            PrintCheckFragment printCheckFragment = new PrintCheckFragment();
            printCheckFragment.fname = fname;
            printCheckFragment.copies = copies;
            mFragmentList.add(printCheckFragment);
            mMyFrageStatePagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList);

            mViewPager.setAdapter(mMyFrageStatePagerAdapter);
            tabPageIndicator.setViewPager(mViewPager);
            mViewPager.setCurrentItem(1);

        } catch (Exception e) {
            showException(e);
        }
    }

    private void processBundle(Bundle b) {

        String flog= Environment.getExternalStorageDirectory()+"/loglectura.txt";
        String ss="";
        String lf="\r\n";

        try {

            try {
                fname=b.getString("fname");ss=fname;
            } catch (Exception e) {
                fname="";ss=e.getMessage();
            }

            try {
                copies=b.getInt("copies");ss=""+copies;
            } catch (Exception e) {
                copies=1;ss=e.getMessage();
            }

        } catch (Exception e) {
            fname="";ss="ERR : "+e.getMessage();
            toast("err4 : "+ss);
        }

        if (fname.isEmpty()) fname=this.getFilesDir()+"/lectura.txt";

    }
    public void toast(String msg) {
        Toast toast= Toast.makeText(mContext,msg,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void showException(Exception e) {
        try {
            String msg;
            msg = e.toString();
            toast(msg);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPrinterhelp.Open(this);
    }

    private void restart() {

        try {

            Intent intent = this.getPackageManager().getLaunchIntentForPackage("com.olc.printcilico");
            intent.putExtra("fname", Environment.getExternalStorageDirectory()+"/lectura.txt");
            intent.putExtra("askprint",1);
            intent.putExtra("copies",copies);
            this.startActivity(intent);

        } catch (Exception e) {
            toast("Pendiente impresión :\n"+e.getMessage());
        }
    }

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