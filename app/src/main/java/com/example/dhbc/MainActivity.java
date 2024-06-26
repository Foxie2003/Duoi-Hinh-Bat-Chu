package com.example.dhbc;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements ItemClick_dapan, ItemClick_cauhoi, OnUserEarnedRewardListener {
    RecyclerView listcauhoi,dapan;
    SharedPreferences prefs;
    ImageView help,shop,layout_2,back;
    TextView level,slgRuby;
    MediaPlayer mp, mp1;
    Button nextdemo;
    CSDL csdl;
    ThongTinNguoiChoi thongTinNguoiChoi;
    CauHoi ch;
    List<Integer> vi_tri_dau_cach;
    List<Integer> trogiup;
    ArrayList<String> cautraloi;
    ArrayList<Integer> vitrioDapAn;
    ArrayList<String> arr2,arr;
    int index=0;
    CauHoiAdapter adapter;
    DapAnAdapter adap;
    TableLayout tb;
    private boolean nhacback;
    private boolean nhacXB;
    float volumn1,volumn2;
    LinearLayout adContainerView;
    private RewardedInterstitialAd rewardedInterstitialAd;
    private InterstitialAd mInterstitialAd;
    private String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Đặt cờ cho cửa sổ
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Ẩn thanh công cụ (navigation bar)
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.activity_main);

        help = findViewById(R.id.help);
        shop = findViewById(R.id.napvip);
        layout_2 = findViewById(R.id.layout_2);
        level = findViewById(R.id.level);
        slgRuby = findViewById(R.id.ruby);
        back = findViewById(R.id.back1);
        csdl = new CSDL(getApplicationContext());
        thongTinNguoiChoi=csdl.HienThongTinNhanVat();

        tb = findViewById(R.id.deme);
        adContainerView = findViewById(R.id.layoutAd);

        mp = new MediaPlayer();
        mp1 = new MediaPlayer();


        prefs= getSharedPreferences("game", MODE_PRIVATE);
        nhacback = prefs.getBoolean("isMute", true);
        nhacXB = prefs.getBoolean("isXB", true);
        volumn1=prefs.getFloat("volumnBack",1);
        volumn2=prefs.getFloat("volumnXB",1);

        loadTrang();
        //Quảng cáo
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                loadAd();
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });


        loadBanner();

//        verifyStoragePemission(MainActivity.this);

        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogHelp();
            }
        });
        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogShop();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialogSettings();

            }
        });

        ktraAmthanh();

    }
    private void loadInterstitialAds() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });
    }
    private void showDialogChoiLai() {
        Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Dialog);
        dialog.setContentView(R.layout.dialog_choilai);
        dialog.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        Button close=dialog.findViewById(R.id.tuchoi);
        Button ok=dialog.findViewById(R.id.chapnhan);
        close.setVisibility(View.GONE);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                csdl.ChoiLai(MainActivity.this);
                recreate();
            }
        });
        dialog.show();

    }
    public void loadTrang(){
        vi_tri_dau_cach=new ArrayList<>();
        trogiup=new ArrayList<>();
        vitrioDapAn=new ArrayList<>();
        ch= csdl.HienCSDL(getApplicationContext());
//        slgRuby1= csdl.HienRuby(MainActivity.this);

        slgRuby1=csdl.HienThongTinNhanVat().getRuby();
        slgRuby.setText(String.valueOf(slgRuby1));
        trogiup12[0]=false;
        if(ch.getId()==-1){

            tb.setVisibility(View.GONE);
            level.setText("Thanh Binh");
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("Xác nhận");
//            builder.setMessage("Bạn đã chơi hết các level, bạn có muốn chơi lại không?");
//            builder.setCancelable(false);
//            // Nút xác nhận
//            builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    // Xử lý khi người dùng nhấn nút xác nhận
//                    // Thêm code xử lý ở đây
//                    csdl.ChoiLai(MainActivity.this);
//                    recreate();
//                }
//            });
//
//            AlertDialog dialog = builder.create();
//            dialog.show();
            showDialogChoiLai();
        }
        else {
            level.setText(String.valueOf(ch.getId()));

            String fileName = ch.getHinhAnh().toString(); // Lấy tên tệp ảnh từ đối tượng baiHat
            int resId = getResources().getIdentifier(fileName, "drawable", getPackageName()); // Tìm ID tài nguyên dựa trên tên tệp ảnh
            if (resId != 0) {
                layout_2.setImageResource(resId);
                layout_2.setBackgroundColor(Color.WHITE);// Thiết lập hình ảnh cho ImageView
            } else {
                // Xử lý trường hợp không tìm thấy tệp ảnh
            }
            dapAn = ch.getDapAn();
            dapAn2 = ch.getHinhAnh();
            arr2= new ArrayList<>();

            for (int i = 0; i < dapAn2.length(); i++) {
                // Convert each character to a string and add it to the ArrayList

                arr2.add(String.valueOf(dapAn2.charAt(i)));

            }

            Random random = new Random();
            for (int i = 0; i < random.nextInt(2) + 6; i++) {
                // Generate a random character between 'a' and 'z'
                char randomChar = (char) (random.nextInt(26) + 'a');
                arr2.add(String.valueOf(randomChar));
            }
            Collections.shuffle(arr2);

            arr = new ArrayList<String>();
            cautraloi=new ArrayList<>();
            for (int i = 0; i < dapAn.length(); i++) {
                // Convert each character to a string and add it to the ArrayList

                // nếu ký tự cách
                if(dapAn.charAt(i)==' ') {
                    arr.add(String.valueOf(""));
                    vi_tri_dau_cach.add(i);
                    trogiup.add(2);
                    cautraloi.add("");
                    vitrioDapAn.add(-2);
                }
                else {
                    arr.add(String.valueOf("1"));
                    cautraloi.add("1");
                    vitrioDapAn.add(-1);
                    trogiup.add(0);
                }

            }

            level.setText(String.valueOf(ch.getId()));
            listcauhoi = findViewById(R.id.listcauhoi);
            dapan = findViewById(R.id.dapan);
            adapter = new CauHoiAdapter(this, arr,this);
            adap = new DapAnAdapter(this, arr2,this);

            layoutManager = new FlexboxLayoutManager(getApplicationContext());
            layoutManager.setFlexDirection(FlexDirection.ROW);
            layoutManager.setJustifyContent(JustifyContent.FLEX_START);

            layoutManager2 = new FlexboxLayoutManager(getApplicationContext());
            layoutManager.setFlexDirection(FlexDirection.ROW);
            layoutManager.setJustifyContent(JustifyContent.FLEX_START);


            listcauhoi.setLayoutManager(layoutManager);
            dapan.setLayoutManager(layoutManager2);
            listcauhoi.setAdapter(adapter);
            dapan.setAdapter(adap);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(nhacXB){
                        int[] list_daylagi={R.raw.daylagi0,R.raw.daylagi1,R.raw.daylagi2,R.raw.daylagi3,R.raw.daylagi4};
                        Random random1 = new Random();
                        int randomIndex = random1.nextInt(list_daylagi.length);
                        int randomItem = list_daylagi[randomIndex];
//                        Toast.makeText(MainActivity.this, "bài: "+randomIndex, Toast.LENGTH_SHORT).show();
                        try {
                            mp1.reset();
                            mp1.setDataSource(getResources().openRawResourceFd(randomItem));
                            mp1.setVolume(volumn2,volumn2);
                            mp1.prepare();
                            mp1.start();


                            mp1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {

                                    mp1.reset();
                                    mp1.setVolume(volumn2, volumn2);
                                }
                            });

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            },2000);

        }



    }
    @Override
    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
        Toast.makeText(this, "Nhận thưởng thành công", Toast.LENGTH_SHORT).show();
        csdl.UpdateRuby(MainActivity.this,10);
        loadAd();
        slgRuby1= csdl.HienThongTinNhanVat().getRuby();
        slgRuby.setText(String.valueOf(slgRuby1));
    }

    private AdSize getAdSize() {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = adContainerView.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    private void loadBanner() {

        // Create a new ad view.
        AdView adView = new AdView(this);
        adView.setAdSize(getAdSize());
        adView.setAdUnitId("ca-app-pub-3940256099942544/9214589741");

        // Replace ad container with new ad view.
        adContainerView.removeAllViews();
        adContainerView.addView(adView);

        // Start loading the ad in the background.
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
    public void loadAd() {
        // Use the test ad unit ID to load an ad.
        rewardedInterstitialAd.load(MainActivity.this, "ca-app-pub-3940256099942544/5354046379",
                new AdRequest.Builder().build(),  new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(RewardedInterstitialAd ad) {
                        Log.d(TAG, "Ad was loaded.");
                        rewardedInterstitialAd = ad;
                    }
                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        Log.d(TAG, loadAdError.toString());
                        rewardedInterstitialAd = null;
                    }
                });
    }
    private void ShareLinkApp(){
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Thay đổi thành URI của liên kết bạn muốn chia sẻ
        Uri uri = Uri.parse("https://drive.google.com/drive/folders/1j1AV8odUsTbpCG3Zhma5Kqj-_QB8TULn?usp=sharing");

        // Đặt nội dung của Intent thành liên kết
        intent.putExtra(Intent.EXTRA_TEXT, uri.toString());

        // Thêm cờ cho phép ứng dụng đính kèm xử lý dữ liệu từ URI được cung cấp
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Đặt loại dữ liệu của Intent thành "text/plain"
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent,"Share to..."));
    }
    private SeekBar volumeSeekBar1,volumeSeekBar2;
    private void ktraAmthanh() {
        if (nhacback) {
            back.setImageResource(R.drawable.loa);
            try {
                mp.reset();
                mp.setDataSource(getResources().openRawResourceFd(R.raw.nhacback));
                mp.setVolume(volumn1,volumn1);
                mp.prepare();
                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mp.start();
                    }
                });


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
    private void showDialogSettings() {
        Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Dialog);
        dialog.setContentView(R.layout.dialog_settings);
        dialog.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView cham=dialog.findViewById(R.id.cham);
        TextView volumn1a=dialog.findViewById(R.id.volumn1);
        TextView volumn2a=dialog.findViewById(R.id.volumn2);
        ImageView nhacback12=dialog.findViewById(R.id.nhacback);
        ImageView nhacXB12=dialog.findViewById(R.id.nhacxb);
        ImageView sharelink=dialog.findViewById(R.id.sharelink);
        sharelink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareLinkApp();
            }
        });
        Animation blinkk=AnimationUtils.loadAnimation(this,R.anim.blink2);
        cham.setAnimation(blinkk);
        cham.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        volumeSeekBar1 = dialog.findViewById(R.id.seek1);
        volumeSeekBar2 = dialog.findViewById(R.id.seek2);
        volumeSeekBar1.setMax(100);
        int progress1 = (int) (100 - Math.pow(10, (1 - volumn1) * Math.log10(100)));
        volumn1a.setText(String.valueOf(progress1));
        volumeSeekBar1.setProgress(progress1); // Thiết lập mức âm lượng mặc định
        volumeSeekBar2.setMax(100);
        int progress2 = (int) (100 - Math.pow(10, (1 - volumn2) * Math.log10(100)));
        volumn2a.setText(String.valueOf(progress2));
        volumeSeekBar2.setProgress(progress2);
        // nhạc nền
        volumeSeekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress!=0){
                    nhacback12.setImageResource(R.drawable.notnhac);
                    nhacback=true;
                }
                else {
                    nhacback12.setImageResource(R.drawable.notnhac_mute);
                    nhacback=false;
                }
                 volumn1 = (float) (1 - (Math.log(100 - progress) / Math.log(100)));
                mp.setVolume(volumn1, volumn1); // Thiết lập âm lượng của MediaPlayer
                volumn1a.setText(String.valueOf(progress));
                SharedPreferences.Editor editor = prefs.edit();
                editor.putFloat("volumnBack", volumn1);
                editor.putBoolean("isMute",nhacback);
                editor.apply();
                ktraAmthanh();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        //tiếng xuân bắc
        volumeSeekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress!=0){
                    nhacXB12.setImageResource(R.drawable.voicexb);
                    nhacXB=true;
                }
                else {
                    nhacXB12.setImageResource(R.drawable.voicexb_mute);
                    nhacXB=false;
                }
                volumn2 = (float) (1 - (Math.log(100 - progress) / Math.log(100)));
                mp1.setVolume(volumn2, volumn2);
                volumn2a.setText(String.valueOf(progress));
                SharedPreferences.Editor editor = prefs.edit();
                editor.putFloat("volumnXB", volumn2);
                editor.putBoolean("isXB",nhacXB);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        dialog.show();
    }


    private void showDialogChucMung() {


        Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Dialog);
        dialog.setContentView(R.layout.dialog_chucmung_dapan);
        dialog.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        AppCompatButton close=dialog.findViewById(R.id.tieptuc);
        TextView tv=dialog.findViewById(R.id.dapan);
        ImageView img=dialog.findViewById(R.id.asdang);
        tv.setText(dapAn.toUpperCase());
        Animation xoayxoay= AnimationUtils.loadAnimation(this, R.anim.laclubtn);
        Animation blink= AnimationUtils.loadAnimation(this, R.anim.blink2);
        AnimationSet animSet = new AnimationSet(true);

        animSet.addAnimation(xoayxoay);
        animSet.addAnimation(blink);
        img.setAnimation(animSet);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                // Thực hiện cập nhật CSDL và tải lại trang
                csdl.Update(MainActivity.this,ch.getId());
                csdl.UpdateRuby(MainActivity.this,3);
                csdl.UpdateThongTin(ch.getId(),thongTinNguoiChoi.getLevel());
                loadTrang();
            }
        });

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(nhacXB){

                    try {
                        mp1.reset();
                        mp1.setDataSource(getResources().openRawResourceFd(R.raw.xinchucmung));
                        mp1.setVolume(volumn2,volumn2);
                        mp1.prepare();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mp1.start();
                            }
                        }, 2000);


                        mp1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {

                                mp1.reset();
                                mp1.setVolume(volumn2, volumn2);
                            }
                        });

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, 1000);
        dialog.show();

    }

    String dapAn,dapAn2;
    private void showDialogShop() {
        Dialog dialog = new Dialog(MainActivity.this,android.R.style.Theme_Dialog );

        dialog.setContentView(R.layout.dialog_shop);
        ImageView xemQC=dialog.findViewById(R.id.xemvid);
        ImageView mua1=dialog.findViewById(R.id.h200k);
        ImageView mua2=dialog.findViewById(R.id.h100k);
        ImageView mua3=dialog.findViewById(R.id.h20k);
        TextView cham=dialog.findViewById(R.id.cham);
        Animation blinkk=AnimationUtils.loadAnimation(this,R.anim.blink2);
        cham.setAnimation(blinkk);
        xemQC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rewardedInterstitialAd != null) {
                    rewardedInterstitialAd.show(MainActivity.this, MainActivity.this);
                }
            }
        });
        mua1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


            }
        });
        mua2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        mua3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        cham.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        dialog.show();

    }

    int slgRuby1=0;
    FlexboxLayoutManager layoutManager,layoutManager2;

    //click đáp án
    @Override
    public void onItemClick(int position) {
        mp3=new MediaPlayer();
        try {
            mp3.reset();
            mp3.setDataSource(getResources().openRawResourceFd(R.raw.chamnuoc));
            mp3.setVolume(volumn1,volumn1);
            mp3.prepare();
            mp3.start();



        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int dem=0;
        // kiểm tra xem đã có bao nhiêu ký tự trong câu trả lời của người chơi
        for(int i=0;i<cautraloi.size();i++){
            if(cautraloi.get(i).toUpperCase()!="1" && !cautraloi.get(i).trim().isEmpty()){
                dem++;
            }
        }
//        Toast.makeText(this, "vị trí: "+dem+vi_tri_dau_cach.size(), Toast.LENGTH_SHORT).show();
        System.out.println(dem+vi_tri_dau_cach.size());
        String s=arr2.get(position).toString().toUpperCase();
        // nếu chọn ký tự trong listdapan !="" và biến dem < listcauhoi.size()
        if(s.trim().length()>0 &&s!=""&&s!=null && dem+vi_tri_dau_cach.size()<arr.size()){

            //set vị trí đó trong arr2 => ""
            arr2.set(position," ");
            // biến dùng để chỉ set myAnswer 1 lần
            boolean foundNegativeIndex = false;
            //lập vòng for vitriodapan
            for (int j = 0; j < vitrioDapAn.size(); j++) {

                // nếu foundNegativeIndex=false và có ký tự "" trong cautraloi
                if (!foundNegativeIndex && vitrioDapAn.get(j) < 0) {
                    // Nếu chưa tìm thấy phần tử âm và vitrioDapAn[j] nhỏ hơn 0, cập nhật arr và đánh dấu đã tìm thấy


                    if (vitrioDapAn.get(j) == -1 ) {
                            vitrioDapAn.set(j, position);
                            foundNegativeIndex = true;
                            cautraloi.set(j, s);
                            index++;


                    } else if(vitrioDapAn.get(j) == -2 && vitrioDapAn.get(j+1)==-1) {
                        // th: nếu vị trí là dấu cách
                        cautraloi.set(j, " ");
                        cautraloi.set(j + 1, s);

                        vitrioDapAn.set(j+1,position);
                        index+=1;
                        break;

                    }

                }


            }
            KiemTraDapAn();



            adap.notifyDataSetChanged();
//            adap.notifyDataSetChanged();
//            listcauhoi.setLayoutManager(layoutManager);
            listcauhoi.setAdapter( new CauHoiAdapter(this,cautraloi,this));
            dapan.setAdapter( new DapAnAdapter(this,arr2,this));
        }
    }
    private void KiemTraDapAn(){

        int dem=0;
        // kiểm tra xem đã có bao nhiêu ký tự trong câu trả lời của người chơi
        for(int i=0;i<cautraloi.size();i++){
            if(cautraloi.get(i).toUpperCase()!="1" && !cautraloi.get(i).trim().isEmpty()){
                dem++;
            }
        }

        // nếu vị trí textview cuối cùng đã có ký tự
        if(dem+vi_tri_dau_cach.size()>=arr.size()){

            String dapan1 = dapAn.toUpperCase();
            StringBuilder result = new StringBuilder();
            for (String item : cautraloi) {
                result.append(item);
            }
            String dapan2 = result.toString();
            String dapan1KhongDau = removeDiacritics(dapan1);
            String dapan2KhongDau = removeDiacritics(dapan2);

            System.out.println(dapan1KhongDau);
            System.out.println(dapan2KhongDau);

            if(dapan1KhongDau.equals(dapan2KhongDau)){
                if(nhacXB){
                    int[] list_daylagi={R.raw.chinhxac1,R.raw.chinhxac6,R.raw.chinhxac7,R.raw.chinhxac8,R.raw.chinhxac9};
                    Random random1 = new Random();
                    int randomIndex = random1.nextInt(list_daylagi.length);
                    int randomItem = list_daylagi[randomIndex];
                    try {
                        mp1.reset();
                        mp1.setVolume(volumn2, volumn2);
                        mp1.setDataSource(getResources().openRawResourceFd(randomItem));
                        mp1.prepare();
                        mp1.start();

                        mp1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                if (mInterstitialAd != null) {
                                    mInterstitialAd.show(MainActivity.this);
                                } else {
                                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                                }
                                loadInterstitialAds();
                                showDialogChucMung();
                                mp1.reset();
                                mp1.setVolume(volumn2, volumn2);
                            }
                        });

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
//                    Toast.makeText(this, "mày giỏi đúng r đó con chóa", Toast.LENGTH_SHORT).show();

                }
                else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Đáp án hoàn toàn chính xác", Toast.LENGTH_SHORT).show();

                        }
                    }, 1000);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mInterstitialAd != null) {
                                mInterstitialAd.show(MainActivity.this);
                            } else {
                                Log.d("TAG", "The interstitial ad wasn't ready yet.");
                            }
                            loadInterstitialAds();
                            showDialogChucMung();
                        }
                    }, 1500);
                }

            }
            else {
                if(nhacXB){
                    int[] list_daylagi={R.raw.chuachinhxac0,R.raw.chuachinhxac1,R.raw.chuachinhxac2,R.raw.chuachinhxac3,R.raw.chuachinhxac4,R.raw.chuachinhxac5,R.raw.chuachinhxac6};
                    Random random1 = new Random();
                    int randomIndex = random1.nextInt(list_daylagi.length);
                    int randomItem = list_daylagi[randomIndex];
                    try {
                        mp1.setDataSource(getResources().openRawResourceFd(randomItem));
                        mp1.setVolume(volumn2, volumn2);
                        mp1.prepare();
                        mp1.start();

                        mp1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp1.reset();
                                mp1.setVolume(volumn2, volumn2);
                            }
                        });

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    Toast.makeText(this, "Đáp án chưa chính xac, tiếp tục", Toast.LENGTH_SHORT).show();
                }

//                Toast.makeText(this, "lew lew gà", Toast.LENGTH_SHORT).show();
            }

        }
    }
    public static String removeDiacritics(String str) {
        // chuyê unicode thành tiếng anh
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(str).replaceAll("").replaceAll("đ", "d").replaceAll("Đ", "D");
    }
    MediaPlayer mp3;
    //click câu hỏi
    @Override
    public void onItemCauHoiClick(int position) {
        // lấy ra text của textview đã click
        mp3=new MediaPlayer();
        try {
            mp3.reset();
            mp3.setDataSource(getResources().openRawResourceFd(R.raw.chamnuoc));
            mp3.setVolume(volumn1,volumn1);
            mp3.prepare();
            mp3.start();



        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String s=cautraloi.get(position).toString().toUpperCase();
            // nếu s != ""
            if(s.trim().length()>0 &&s!=""&&s!=null && s!="1"){
                // set vị trí đó trở thành ""
                cautraloi.set(position,"");

                // set lại vị trí cữ của từ đó vào arr2
                // vị trí cũ đã được lưu ở mảng vitriodapan
                cautraloi.set(position,"1");
                arr2.set(vitrioDapAn.get(position),s);
                // vị trí đó ở mảng vitriodapan -> =1 (-1: chưa có ký tự; >-1: đã có ký tự)
                vitrioDapAn.set(position,-1);

                //set lại adapter
            listcauhoi.setAdapter( new CauHoiAdapter(this,cautraloi,this));
//            listcauhoi.setLayoutManager(layoutManager);
            dapan.setAdapter( new DapAnAdapter(this,arr2,this));
        }
    }
    ImageView ngthan,mochu1,motu1,motoanbo;
    boolean[] trogiup12 = {false};
    private void showDialogHelp() {
        Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Dialog);
        dialog.setContentView(R.layout.dialog_help);
        TextView close=dialog.findViewById(R.id.cham);
        ngthan=dialog.findViewById(R.id.hshare);
        mochu1=dialog.findViewById(R.id.h1ktu);
        motu1=dialog.findViewById(R.id.h1tu);
        motoanbo=dialog.findViewById(R.id.htoanbo);

        mochu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if(slgRuby1>=5){
                    for(int i=0;i<dapAn.length();i++){
                        if(removeDiacritics(String.valueOf(dapAn.charAt(i))).equalsIgnoreCase(String.valueOf(cautraloi.get(i)))
                                && !String.valueOf(cautraloi.get(i)).trim().equals("")
                        ) {
                            trogiup.set(i, 1);
                        }
                        else if(vitrioDapAn.get(i)!=-2
                                && vitrioDapAn.get(i)!=-1){
//                Toast.makeText(this,"vitri:"+ vitrioDapAn.get(i)+"ký tự:"+ cautraloi.get(i), Toast.LENGTH_SHORT).show();

                            arr2.set(vitrioDapAn.get(i),cautraloi.get(i));
                            cautraloi.set(i,"1");
                            vitrioDapAn.set(i,-1);
                            listcauhoi.setAdapter( new CauHoiAdapter(MainActivity.this,cautraloi,MainActivity.this));
                            dapan.setAdapter( new DapAnAdapter(MainActivity.this,arr2,MainActivity.this));
                        }
                        if(vitrioDapAn.get(i)==-2){
                            cautraloi.set(i," ");
                        }

                    }
                    HienTroGiup();
                    csdl.UpdateRuby(MainActivity.this,-5);
                    slgRuby1=csdl.HienThongTinNhanVat().getRuby();
                    slgRuby.setText(String.valueOf(slgRuby1));
                }
                else {
                    Toast.makeText(MainActivity.this, "Số lượng ruby không đủ", Toast.LENGTH_SHORT).show();
                }

            }
        });

        motu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(trogiup12[0] ==true){
                    Toast.makeText(MainActivity.this, "Trợ giúp chỉ được sử dụng 1 lần", Toast.LENGTH_SHORT).show();

                }
                else {
                    dialog.dismiss();
                    if(slgRuby1>=15){

                        for(int i=0;i<dapAn.length();i++){
                            if(removeDiacritics(String.valueOf(dapAn.charAt(i))).equalsIgnoreCase(String.valueOf(cautraloi.get(i)))
                                    && !String.valueOf(cautraloi.get(i)).trim().equals("")
                            ) {
                                trogiup.set(i, 1);
                            }
                            else if(vitrioDapAn.get(i)!=-2
                                    && vitrioDapAn.get(i)!=-1){
//                Toast.makeText(this,"vitri:"+ vitrioDapAn.get(i)+"ký tự:"+ cautraloi.get(i), Toast.LENGTH_SHORT).show();

                                arr2.set(vitrioDapAn.get(i),cautraloi.get(i));
                                cautraloi.set(i,"1");
                                vitrioDapAn.set(i,-1);
                                listcauhoi.setAdapter( new CauHoiAdapter(MainActivity.this,cautraloi,MainActivity.this));
                                dapan.setAdapter( new DapAnAdapter(MainActivity.this,arr2,MainActivity.this));
                            }
                            if(vitrioDapAn.get(i)==-2){
                                cautraloi.set(i," ");
                            }

                        }
                        for(int i=0;i<trogiup.size();i++){
                            int vt=trogiup.size()-1;
                            Log.e("TAG", "onClick: "+ trogiup.get(i));
                            if(i>0) {
                                if(trogiup.get(i)==0){
                                    HienTroGiup();
                                }
                                if(trogiup.get(i)==2){
                                    trogiup12[0] =true;
                                    break;
                                }
                            }
                            else {
                                HienTroGiup();
                            }


                        }
                        csdl.UpdateRuby(MainActivity.this,-15);
                        slgRuby1=csdl.HienThongTinNhanVat().getRuby();
                        slgRuby.setText(String.valueOf(slgRuby1));

                    }
                    else {
                        Toast.makeText(MainActivity.this, "Số lượng ruby không đủ", Toast.LENGTH_SHORT).show();
                    }
                }




            }
        });
        motoanbo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if(slgRuby1>=30){
//                    loadTrang();
                    for(int i=0;i<trogiup.size();i++){

                        HienTroGiup();
                    }
                    csdl.UpdateRuby(MainActivity.this,-30);
                    slgRuby1=csdl.HienThongTinNhanVat().getRuby();
                    slgRuby.setText(String.valueOf(slgRuby1));

                }
                else {
                    Toast.makeText(MainActivity.this, "Số lượng ruby không đủ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ngthan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap b=takescreenshotOfRootView(tb);
                File savedFile = saveBitmapToFile(b);
                if (savedFile != null) {
                    Toast.makeText(MainActivity.this, "Đã chụp màn hình để chia sẻ", Toast.LENGTH_SHORT).show();
                    try {
                        savedFile.setReadable(true,false);

                        //Đây là hành động của Intent được sử dụng để chia sẻ nội dung.
                        // Trong trường hợp này, nó được sử dụng để chia sẻ một file ảnh.
                        final Intent intent=new Intent(Intent.ACTION_SEND);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        //Đây là cách để nhận một Uri cho một file từ một FileProvider.
                        // Điều này cần thiết khi chia sẻ file với ứng dụng khác trên Android Nougat (API level 24) trở lên.
                        Uri uri= FileProvider.getUriForFile(getApplicationContext(),getApplication().getPackageName()+".provider",savedFile);
                        intent.putExtra(Intent.EXTRA_STREAM,uri);

                        //ây là cờ được sử dụng để cho phép ứng dụng mà Intent được gửi tới đọc dữ liệu từ Uri đã được cung cấp.
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setType("image/*");

                        startActivity(Intent.createChooser(intent,"Share to..."));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Failed to save screenshot", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Animation blinkk=AnimationUtils.loadAnimation(this,R.anim.blink2);
        close.setAnimation(blinkk);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        dialog.show();

    }
    private void HienTroGiup(){

        // kiểm tra vị trí trợ giúp
        //nếu KiemTraViTri_trogiup()>-1 (còn phần tử cần trợ giúp)
        if (KiemTraViTri_trogiup()>-1){
            // ktu là từ cần hiện khi nhấn trợ giúp
            String ktu= removeDiacritics(String.valueOf(dapAn.charAt( KiemTraViTri_trogiup()))).toUpperCase();

            // tìm vị trí của kyu ở trong arr2
            int vitrioda=KiemTraViTri_dapAn(ktu);

            // set ktu đó vào đáp án của người chơi
                cautraloi.set(KiemTraViTri_trogiup(),ktu);

            // set vị trí của ktu đó vào mảng vitrdapan
                vitrioDapAn.set(KiemTraViTri_trogiup(),vitrioda);

            // set vị trí vừa trợ giúp trong mảng trợ giúp thành 1 (0- chưa trợ giúp; 1- đã trợ giúp)
                trogiup.set(KiemTraViTri_trogiup(),1);

                // nếu là dấu cách thì set " "
                if(KiemTraViTri_trogiup()>=1){
                    if(vitrioDapAn.get(KiemTraViTri_trogiup()-1) == -2 && vitrioDapAn.get(KiemTraViTri_trogiup())==-1) {
                        cautraloi.set(KiemTraViTri_trogiup()-1," ");
                    }
                }
                //set vị trí ở trong arr2 ="" (các từ ô dưới)
                arr2.set(vitrioda,"");
                //set lại adapter
                dapan.setAdapter( new DapAnAdapter(MainActivity.this,arr2,MainActivity.this));
                listcauhoi.setAdapter( new CauHoiAdapter(MainActivity.this,cautraloi,MainActivity.this));

                KiemTraDapAn();



        }
        else {
            Toast.makeText(MainActivity.this, "Ô chữ đã được hoành thành", Toast.LENGTH_SHORT).show();
        }
    }

    public int KiemTraViTri_trogiup(){
        int position=-1;
        //lập vòng for trong mảng trợ giúp
        //nếu tìm thấy phần tử có giá trị =0 thì break và return ra vị trí của phần tử ấy
        for(int i=0;i<trogiup.size();i++){
            if (trogiup.get(i)==0){
                position=i;
                break;
            }
        }
        return position;
    }
    public int KiemTraViTri_dapAn(String ktu){
        int position=-1;
        // lập vòng for trong lisdapan
        // nếu tìm thấy phần tử có giá trị = ký tự thì break và return vị trí phần tử ấy
        for(int i=0;i<arr2.size();i++){
            if (arr2.get(i).equalsIgnoreCase(ktu)){
                position=i;
                break;
            }
        }
        return position;
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Kiểm tra xem audio có được tạm dừng không và nếu có thì tiếp tục phát
        if (mp != null && !mp.isPlaying() && nhacback) {
            mp.start();
        }
        if (mp1 != null && !mp1.isPlaying() && nhacXB) {
            mp1.start();



        }
    }
    @Override
    protected void onPause() {
        super.onPause();

        // Kiểm tra nếu audio đang phát
        if (mp != null && mp.isPlaying()&& nhacback) {
            // Tạm dừng audio
            mp.pause();
        }
        if (mp1 != null && mp1.isPlaying() && nhacXB) {
            // Tạm dừng audio
            mp1.pause();
        }
    }
    public static  Bitmap takescreenshot(View v){
        //Bật bộ đệm vẽ của View.
        // Khi được bật, View sẽ giữ một bản sao bitmap của nội dung hiện tại của nó trong bộ đệm vẽ.
        v.setDrawingCacheEnabled(true);

        //Xây dựng bộ đệm vẽ của View.
        // Điều này đảm bảo rằng bitmap được tạo ra sau đó sẽ là một bản sao của nội dung hiện tại của View.
        v.buildDrawingCache(true);

        // Tạo một bản sao của bitmap trong bộ đệm vẽ của View.
        Bitmap b=Bitmap.createBitmap(v.getDrawingCache());

        //Tắt bộ đệm vẽ của View, giải phóng bộ nhớ.
        v.setDrawingCacheEnabled(false);
        return b;
    }
    private static Bitmap takescreenshotOfRootView(View v){
        //Phương thức này nhận một View làm đối số và chụp ảnh của nó.
        return takescreenshot(v.getRootView());
    }
    public File saveBitmapToFile(Bitmap bitmap) {
        //khởi tạo biến để lưu đường dẫn ảnh
        String savedImagePath = null;
        //Tạo tên file ảnh mới dựa trên thời gian hiện tại, để đảm bảo tính duy nhất của tên file.
        String imageFileName = "IMG_" + System.currentTimeMillis() + ".jpg";

        //Xác định thư mục lưu trữ bên ngoài để lưu file ảnh
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "/YourAppName");

        // Create directory if it doesn't exist
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        // Create the image file
        File imageFile = new File(storageDir, imageFileName);
        savedImagePath = imageFile.getAbsolutePath();

        try {
            // Save the image to storage
            //Mở một luồng ghi vào file ảnh
            FileOutputStream fos = new FileOutputStream(imageFile);

            //Nén bitmap và ghi dữ liệu nén vào luồng ghi.
            // Trong trường hợp này, định dạng JPEG được sử dụng với chất lượng 100 (tốt nhất).
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            //Đóng luồng ghi sau khi hoàn thành.
            fos.close();

            //Cập nhật cơ sở dữ liệu MediaStore để hiển thị ảnh mới trong Gallery của thiết bị.
            MediaStore.Images.Media.insertImage(getContentResolver(), imageFile.getAbsolutePath(), imageFile.getName(), imageFile.getName());
        } catch (IOException e) {
            e.printStackTrace();
            savedImagePath = null;
        }
        return imageFile;
    }



}