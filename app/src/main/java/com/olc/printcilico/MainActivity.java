package com.olc.printcilico;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
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

import com.google.gson.Gson;
import com.olc.printcilico.clases.clsBeLecturaImp;
import com.olc.printcilico.clases.proforma.clsBeProformaImp;
import com.olc.printcilico.clases.proforma.clsBeProforma_detalle;
import com.olc.printcilico.clases.proforma.clsBeTmpProformaUs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

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
    private clsBeLecturaImp lectura;
    private clsBeProformaImp proforma;
    private Context mContext = null;
    private PrintHelper.PrintType mTitleType = PrintHelper.PrintType.Centering;
    private boolean titleBold = true;
    private int mTitleTextSize = 40;
    private int mLineTextSize = 18;
    private int mValueTextSize = 20;
    private int mKeyTextSize = 24;
    private String mTitleStr = "Registro de lectura";
    private int mOffsetX = 210;
    private BufferedWriter writer = null;
    private FileWriter wfile;
    private int seplen,prwq,prwt,prwh,decimp, prw = 40;
    private double aux;
    private String frmstr,ts;
    private String cursym="Q";
    private DecimalFormat decfrm;
    public ArrayList<String> items=new ArrayList<String>();
    public static int tipoImp = 1;
    PrintHelper printHelper = null;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        prwq=(int) Math.floor(prw/4);
        prwt=(int) Math.floor(prw/3);
        prwh=(int) Math.floor(prw/2);
        seplen = prw;
        decfrm = new DecimalFormat("#,##0.00");
        tipoImp = 1;

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
                String lecturaJson = "", proformaJson = "";
                lecturaJson = b.getString("lectura");
                proformaJson = b.getString("proforma");

                Gson gson = new Gson();
                if (lecturaJson != null) {
                    lectura = gson.fromJson(lecturaJson, clsBeLecturaImp.class);
                    if (lectura.Notificacion.equals("1")){
                        tipoImp = 3;
                    }
                } else if (proformaJson != null) {
                    tipoImp = 2;
                    proforma = gson.fromJson(proformaJson, clsBeProformaImp.class);
                }


            } catch (Exception e) {
               ss=e.getMessage();
                clsBeLecturaImp lectura2 = new clsBeLecturaImp();

                lectura2.Nit= String.valueOf(20);
                lectura2.Direccion= String.valueOf(20);
                lectura2.Fecha= String.valueOf(20);
                lectura2.Ruta= String.valueOf(20);
                lectura2.Itinerario= String.valueOf(20);
                lectura2.Tecnico= String.valueOf(20);
                lectura2.Usuario= String.valueOf(20);
                lectura2.Contador= String.valueOf(20);
                lectura2.LecturaAnterior= String.valueOf(20);
                lectura2.LecturaActual= String.valueOf(20);
                lectura2.Consumo= String.valueOf(20);
                lectura2.CorreoEmp= String.valueOf(20);

                lectura = lectura2;
            }

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

        if (fname.isEmpty() || fname.equals("lect") || fname.equals("proforma")) fname="/data/data/com.olc.printcilico/lectura.txt";

        if (lectura!=null){
            saverep();
        }

        if (proforma != null) {
            saveProforma();
        }

    }

    public boolean saveProforma() {
        //String observacion="";
        try {
            //observacion = "SU PROFORMA GENERA ORDEN DE CORTE A PARTIR DEL 04/05/2024. Si ya realizó su pago, favor hacer caso omiso.";

            addc("NIT: 112388302 Teléfono 7922-1101");
            add("");

            add2string("Fecha notificación: ", proforma.Fecha_notificacion);
            add("");
            add("No. proforma: " + proforma.proforma.serie_proforma + " - " + (int) proforma.proforma.num_proforma+"");
            add("Usuario: " + proforma.Contribuyente);
            add("Dirección: " + proforma.Direccion);
            add("Contador: " + proforma.Contador);
            add("");

            add("Meses que adeuda:");
            add("Meses   Consumo  Consumo  Monto");
            add("               (Kwh)    (Kw)         ");
            line();

            for (clsBeTmpProformaUs obj: proforma.pendientes) {
                String mes = obj.NoMes <= 9 ? "0"+obj.NoMes:obj.NoMes+"";
                add4lrrT(mes+" "+obj.Anio, (int)obj.Consumo+"", obj.Demanda+"", obj.Cantidad);
            }
            add("");

            add2string("Total a cancelar","Monto en (Q)");
            line();

            for (clsBeProforma_detalle obj: proforma.proforma.detalle) {
                if (!obj.descripcion.equals("IVA")) {
                    add(obj.descripcion);
                    addtotal(obj.monto_gravable);
                } else {
                    addtot(obj.descripcion, obj.cantidad);
                }

            }

            if (proforma.aporteInde > 0) {
                add("- Aporte Social Inde + IVA");
                addTotalNegativo(proforma.aporteInde);
            }

            line();
            addtot("Monto total:", proforma.Total);
            add("");

            if (!proforma.proforma.observacion.isEmpty()) {
                add("Observaciones:");
                if (proforma.proforma.observacion.contains("SU PROFORMA GENERA")){
                    addc(proforma.proforma.observacion.substring(0,27));
                    addc(proforma.proforma.observacion.substring(28,58));
                    addc(proforma.proforma.observacion.substring(59,80));
                    addc(proforma.proforma.observacion.substring(81,proforma.proforma.observacion.length()));
                }else{
                    add(proforma.proforma.observacion);
                }
                add("");
            }
            /*else{
                add("Observaciones:");
                add(observacion.substring(0,27));
                add(observacion.substring(28,57));
                add(observacion.substring(58,observacion.length()));
                add("");
            }*/
            add(proforma.LecturaAnterior);
            add(proforma.LecturaActual);
            add("");
            addc("Últimos consumos (Kwh)");
            line();

            if (proforma.consumos.size()>0){
                if (proforma.consumos.size()==1){
                    add3lrr(proforma.consumos.get(0).Mes, "", "");
                    add3lrr((int)proforma.consumos.get(0).Consumo+"","0", "0");
                }else if (proforma.consumos.size()==2) {
                    add3lrr(proforma.consumos.get(0).Mes, proforma.consumos.get(1).Mes, "");
                    add3lrr((int) proforma.consumos.get(0).Consumo + "", (int)proforma.consumos.get(1).Consumo+"", "0");
                }else if (proforma.consumos.size()>2){
                    add3lrr(proforma.consumos.get(0).Mes, proforma.consumos.get(1).Mes, proforma.consumos.get(2).Mes);
                    add3lrr((int)proforma.consumos.get(0).Consumo+"",(int)proforma.consumos.get(1).Consumo+"", (int)proforma.consumos.get(2).Consumo+"");
                }
            }

            save();
        } catch (Exception e) {
            Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public boolean saverep(){
        String s;

        try {

            wfile=new FileWriter(fname);
            writer = new BufferedWriter(wfile);
            writer.write(lectura.Nit);
            writer.write("\r\n");
            writer.write(lectura.Direccion);
            writer.write("\r\n");
            writer.write("\r\n");
            writer.write(lectura.Fecha);
            writer.write("\r\n");
            writer.write("\r\n");
            writer.write(lectura.Ruta);
            writer.write("\r\n");
            writer.write(lectura.Itinerario);
            writer.write("\r\n");
            writer.write(lectura.Tecnico);
            writer.write("\r\n");
            writer.write("\r\n");
            writer.write(lectura.Usuario);
            writer.write("\r\n");
            writer.write(lectura.Contador);
            writer.write("\r\n");
            writer.write(lectura.LecturaAnterior);
            writer.write("\r\n");
            writer.write(lectura.LecturaActual);
            writer.write("\r\n");
            writer.write(lectura.Consumo);

            if (!lectura.Texto.isEmpty()) {
                writer.write("\r\n");
                writer.write("\r\n");
                writer.write(lectura.Texto);
            }

            writer.write("\r\n");
            writer.write("\r\n");
            writer.write("\r\n");
            writer.write(lectura.CorreoEmp);

            writer.close();

        } catch(Exception e){
            Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
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

    public void empty() {
        items.add(" ");
    }

    public void add(String tn){
        items.add(tn);
    }

    public void addc(String tn){
        items.add(ctrim(tn));
    }

    public void add2string(String s1,String s2) {
        ts=ltrim(s1,prwh+1)+rtrim(s2,prwh-1);
        items.add(ts);
    }

    public void add3lrr(String s1,String s2,String s3) {
        ts=ltrim(s1,prwt)+rtrim(s2,prwt-6)+rtrim(s3,prwt+5);
        items.add(ts);
    }

    public void line() {
        char[] fill = new char[seplen];
        Arrays.fill(fill, '-');
        String s = new String(fill);

        items.add(s);
    }

    public void add4lrrT(String s1,String s2,String s3,double v3) {
        String stot,scom,str,error,error1;
        int tot,rest;

        try {
            if(s1.length()>=12){
                rest=s1.length()-11;
                str = s1.substring(0, s1.length()-rest);
                str = str+" ";
                s1 = str;
            } else if(s1.length()<12 && !s1.isEmpty()){
                str = s1.substring(0, s1.length());
                s1 = str;
            }

            scom=cursym+decfrm.format(v3);

            ts=ltrim(s1,prwt-2)+rtrim(s2,prwt-6)+rtrim(s3,prwt-2)+rtrim(scom,prwt-2);
            items.add(ts);

        } catch (Exception e){
            error=e+"";error1=error;
        }

    }

    public void addtotal(double val) {
        String sval;

        sval=cursym+decfrm.format(val);
        ts=rtrim(""+sval,40);
        items.add(ts);
    }

    public void addTotalNegativo(double val) {
        String sval;

        sval="-"+ cursym + decfrm.format(val);
        ts=rtrim(""+sval,40);
        items.add(ts);
    }

    public void addtot(String s1,double val) {
        String sval;

        sval=cursym+decfrm.format(val);
        ts=ltrim(s1,prw-13)+" "+rtrim(sval,12);
        items.add(ts);
    }

    public void addtotdet(String s1,double val) {
        String sval;

        sval=cursym+decfrm.format(val);
        ts=ltrim(s1,prw-7)+" "+rtrim(sval,6);
        items.add(ts);
    }

    public boolean save(){
        return saverep1();
    }

    public boolean saverep1(){
        String s;
        int lns=0;

        if (items.size()==0) return true;

        try {

            wfile=new FileWriter(fname);
            writer = new BufferedWriter(wfile);

            for (int i = 0; i < items.size(); i++) {
                try {
                    s = items.get(i);
                } catch (Exception e) {
                    s = "";
                }

                /*if (s.length() > prw) {
                    int largo = s.length();
                    String sOriginal = s;
                    while (largo > prw) {
                        writer.write(s.substring(0, prw));
                        writer.write("\r\n");
                        s = sOriginal.substring(prw, sOriginal.length());
                        sOriginal = s;
                        largo = s.length();
                    }
                }*/

                writer.write(s);
                writer.write("\r\n");
                lns++;
            }

            writer.close();
            items.clear();

        } catch(Exception e){
            return false;
        }

        return true;
    }

    public String ctrim(String ss) {
        int l=ss.length();
        if (l>prw) {
            ss=ss.substring(0,prw);
        } else {
            int ldisp=(prw-l)/2;
            frmstr=fillempty(ldisp);
            ss=frmstr+ss;
        }

        return ss;
    }

    private String fillempty(int cn) {
        char[] fill = new char[cn];
        Arrays.fill(fill, ' ');
        String s = new String(fill);

        return s;
    }

    private String trim(String ss) {
        int l=ss.length();
        if (l>prw) ss=ss.substring(0,prw);
        return ss;
    }

    public String ltrim(String ss,int sw) {
        int l=ss.length();
        if (l>sw) {
            ss=ss.substring(0,sw);
        } else {
            frmstr="%-"+sw+"s";
            ss=String.format(frmstr,ss);
        }

        return ss;
    }

    public String rtrim(String ss,int sw) {
        int l;

        ss=ss.trim();
        l=ss.length();

        if (l>=sw) {
            ss=ss.substring(0,sw);
        } else {
            frmstr="%"+sw+"s";
            ss=String.format(frmstr,ss);
        }

        return ss;
    }
}