package com.olc.printcilico;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileNotFoundException;

import Printer.PrintHelper.PrintType;

import static android.app.Activity.RESULT_OK;

import com.olc.Utils;

/************************************************************
 * Copyright 2000-2066 Olc Corp., Ltd.
 * All rights reserved.
 * Description     : The Main activity for  PrintChecker
 * History        :( ID, Date, Author, Description)
 * v1.0, 2017/3/17,  zhangyong, create
 ************************************************************/

public class PrintImageFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 33;
    private static final int SHOW_PICTURE = 23;
    Printer.PrintHelper mPrinter = new Printer.PrintHelper();
    private Button mSelectImageBtn;
    private ImageView showImageView;
    private Button mPrintBtn;
    private Button mResetBtn;
    private Bitmap mResultBitmap;
    private static final String IMAGE_FILE_LOCATION = "file:///" + Environment.getExternalStorageDirectory().getPath() + "/temp";
    private Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);
    private View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_print_image, null);
        mSelectImageBtn = (Button) mView.findViewById(R.id.btn_select_image);
        showImageView = (ImageView) mView.findViewById(R.id.igv_show_image);
        mPrintBtn = (Button) mView.findViewById(R.id.btn_print);
        mResetBtn = (Button) mView.findViewById(R.id.btn_reset);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            mPrinter.Open(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mSelectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
                //BitmapSplitter(BitmapFactory.decodeResource(getResources(),R.drawable.test));
                /** 打印长图片问题测试修改：
                 Bitmap bitmap3= BitmapFactory.decodeResource(getResources(),R.drawable.test);
                 int h=bitmap3.getHeight();
                 Bitmap bitmap0=null;
                 Bitmap bitmapp=null;
                 //y + height must be <= bitmap.height()
                 for(int i=0;i<bitmap3.getHeight();){
                 bitmap0= Bitmap.createBitmap(bitmap3, 0, i,
                 bitmap3.getWidth(), 480);
                 if(bitmap0!=null&&i<=bitmap3.getHeight()) {
                 bitmapp = Bitmap.createScaledBitmap(bitmap0, 384, (int) ((384 * bitmap0.getHeight()) / (bitmap0.getWidth() * 1.0f)), true);
                 if (bitmapp != null) {
                 mPrinter.PrintBitmap(bitmapp);
                 i = i + 480;
                 }
                 }
                 }
                 mPrinter.printBlankLine(60);*/
            }
        });
        mPrintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        Bitmap gray2Binary = Utils.getGray2Binary(mResultBitmap);
                        BitmapSplitter(gray2Binary);
//                        BitmapSplitter(mResultBitmap);
                        //mPrinter.PrintBitmap(mResultBitmap);
                    }
                }).start();

                mPrinter.PrintLineInit(24);
                mPrinter.PrintStringEx("\r\n\r", 24, false, false, PrintType.Centering);
                mPrinter.PrintLineEnd();
                mPrinter.SetGrayLevel((byte) 0x05);
            }
        });
        mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageView.setImageBitmap(null);
                showImageView.setVisibility(View.GONE);
                mPrintBtn.setVisibility(View.GONE);
                mSelectImageBtn.setVisibility(View.VISIBLE);
                mResetBtn.setVisibility(View.GONE);
            }
        });
    }

//    @Override
//    public void restoreGrayLevel() {
//        mPrinter.SetGrayLevel((byte) 0x05);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGE_REQUEST:
                    startPhotoZoom(data.getData());
                    break;
                case SHOW_PICTURE: // 取得裁剪后的图片
                    try {
                        mResultBitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                        mResultBitmap = Bitmap.createScaledBitmap(mResultBitmap, 384, (int) ((384 * mResultBitmap.getHeight()) / (mResultBitmap.getWidth() * 1.0f)), true);
                        showImageView.setImageBitmap(mResultBitmap);
                        showImageView.setVisibility(View.VISIBLE);
                        mPrintBtn.setVisibility(View.VISIBLE);
                        mSelectImageBtn.setVisibility(View.GONE);
                        mResetBtn.setVisibility(View.VISIBLE);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.i("---log----", e + "");
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
//        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
//        intent.putExtra("crop", "true");
//        intent.putExtra("scale", true);
        //是否返回bitmap对象
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//输出图片的格式
        startActivityForResult(intent, SHOW_PICTURE);
    }


    public void BitmapSplitter(Bitmap origbitmap) {
        //origbitmap= BitmapFactory.decodeResource(getResources(),R.drawable.pp);
        int h = origbitmap.getHeight();
        Bitmap bitmap0 = null;
        Bitmap bitmapp = null;
        //y + height must be <= bitmap.height()
        for (int i = 0; i < h; ) {
            if (i + 48 < h) {
                bitmap0 = Bitmap.createBitmap(origbitmap, 0, i, origbitmap.getWidth(), 48);
                if (bitmap0 != null && i <= h) {
                    bitmapp = Bitmap.createScaledBitmap(bitmap0, 384, (int) ((384 * bitmap0.getHeight()) / (bitmap0.getWidth() * 1.0f)), true);
                    if (bitmapp != null) {
                        mPrinter.PrintBitmap(bitmapp);
                        i = i + 48;
                    }
                }
            }
        }

        if (bitmap0 != null) {
            bitmap0.recycle();
            bitmap0 = null;
        }
        if (bitmapp != null) {
            bitmapp.recycle();
            bitmapp = null;
        }
    }
}
