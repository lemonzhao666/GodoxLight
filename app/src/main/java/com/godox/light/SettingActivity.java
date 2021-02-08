package com.godox.light;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LanguageUtils;
import com.blankj.utilcode.util.SPUtils;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.zlm.base.BaseBackActivity;
import com.zlm.phototable.PhotoTableActivity;

import java.util.Locale;

public class SettingActivity extends BaseBackActivity {

    private FrameLayout flGrid;
    private FrameLayout flClear;
    private FrameLayout flScale;
    private TextView tvScale;
    private TextView tvGrid;
    private FrameLayout flAlbum;
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    private int scale;
    private int lens;
    private QMUITipDialog dialog;
    private QMUITipDialog tipDialog;
    private SPUtils spUtils;
    final String[] scale_items = new String[]{"4:3", "16:9", "1:1"};
    String[] language_items ;

    private FrameLayout flAccount;
    private FrameLayout flConnect;
    private String[] lens_items;
    private FrameLayout flLanguage;
    private TextView tvLanguage;
    private int lang;

    @Override
    public int bindLayout() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        setTitle(getString(R.string.setting));
        setReturnIcon(R.mipmap.left);
        flGrid = findViewById(R.id.fl_grid);
        flClear = findViewById(R.id.fl_clear);
        flScale = findViewById(R.id.fl_scale);
        tvScale = findViewById(R.id.tv_scale);
        tvGrid = findViewById(R.id.tv_grid);
        tvLanguage = findViewById(R.id.tv_language);
        flAlbum = findViewById(R.id.fl_album);
        flAccount = findViewById(R.id.fl_account);
        flConnect = findViewById(R.id.fl_connect);
        flLanguage = findViewById(R.id.fl_language);
        lens_items = new String[]{getString(R.string.nulll), getString(R.string.gridd), getString(R.string.duijiaoxian), getString(R.string.center)};
    }

    @Override
    public void doBusiness() {
        language_items = new String[]{getString(R.string.cn), getString(R.string.en)};
        spUtils = SPUtils.getInstance();
        scale = spUtils.getInt("scale", scale);
        lens = spUtils.getInt("lens", lens);
        lang = spUtils.getInt("language", -1);
        if(lang == -1){
            Locale locale = LanguageUtils.getCurrentLocale();
            if(locale.getLanguage().equals(Locale.SIMPLIFIED_CHINESE.getLanguage())){
                lang = 0;
                tvLanguage.setText(R.string.cn);
            }else if(locale.getLanguage().equals(Locale.ENGLISH.getLanguage())){
                lang = 1;
                tvLanguage.setText(R.string.en);
            }
        }else if(lang == 0){
            tvLanguage.setText(R.string.cn);
        }else if(lang == 1){
            tvLanguage.setText(R.string.en);
        }

        tvScale.setText(scale_items[scale]);
        tvGrid.setText(lens_items[lens]);
    }

    @Override
    public void initListener() {
        flAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, LoginActivity.class));
            }
        });
        flConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, ConnectActivity.class));
            }
        });
        flClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipDialog = new QMUITipDialog.Builder(SettingActivity.this)
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .setTipWord(getString(R.string.cleanning))
                        .create();
                tipDialog.show();
                handler.sendEmptyMessageDelayed(0, 2000);
            }
        });
        flScale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QMUIDialog qmuiDialog = new QMUIDialog.CheckableDialogBuilder(SettingActivity.this)
                        .setCheckedIndex(scale)
                        .setSkinManager(QMUISkinManager.defaultInstance(SettingActivity.this))
                        .addItems(scale_items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tvScale.setText(scale_items[which]);
                                scale = which;
                                dialog.dismiss();
                                spUtils.put("scale", which);
                                dialog.dismiss();
                            }
                        })
                        .create(mCurrentDialogStyle);
                qmuiDialog.show();
            }
        });
        flLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QMUIDialog qmuiDialog = new QMUIDialog.CheckableDialogBuilder(SettingActivity.this)
                        .setCheckedIndex(lang)
                        .setSkinManager(QMUISkinManager.defaultInstance(SettingActivity.this))
                        .addItems(language_items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tvLanguage.setText(language_items[which]);
                                spUtils.put("language", which);
                                dialog.dismiss();
                                Resources resources = getResources();
                                DisplayMetrics dm = resources.getDisplayMetrics();
                                Configuration config = resources.getConfiguration();
                                if(which == 0){
                                    config.setLocale(Locale.SIMPLIFIED_CHINESE);
                                }else{
                                    config.setLocale(Locale.ENGLISH);
                                }
                                resources.updateConfiguration(config, dm);

                            }
                        })
                        .create(mCurrentDialogStyle);
                qmuiDialog.show();
            }
        });

        flGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new QMUIDialog.CheckableDialogBuilder(SettingActivity.this)
                        .setCheckedIndex(lens)
                        .setSkinManager(QMUISkinManager.defaultInstance(SettingActivity.this))
                        .addItems(lens_items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tvGrid.setText(lens_items[which]);
                                lens = which;
                                spUtils.put("lens", which);
                                dialog.dismiss();
                            }
                        })
                        .create(mCurrentDialogStyle).show();
            }
        });
        flAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, PhotoTableActivity.class));
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    tipDialog.dismiss();
                    dialog = new QMUITipDialog.Builder(SettingActivity.this)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                            .setTipWord(getString(R.string.clean_success))
                            .create();
                    dialog.show();
                    handler.sendEmptyMessageDelayed(1, 1000);
                    break;
                case 1:
                    dialog.dismiss();
                    break;
            }

        }
    };

    @Override
    protected void onDestroy() {
        Intent mIntent = new Intent();
        mIntent.putExtra("lens", 0);
        mIntent.putExtra("scale", 0);
        setResult(1, mIntent);
        super.onDestroy();
    }
}
