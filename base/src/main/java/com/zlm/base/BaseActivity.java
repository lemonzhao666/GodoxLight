package com.zlm.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.telink.ble.mesh.foundation.Event;
import com.telink.ble.mesh.foundation.EventListener;
import com.telink.ble.mesh.foundation.MeshService;
import com.telink.ble.mesh.foundation.event.BluetoothEvent;
import com.telink.ble.mesh.foundation.event.ScanEvent;
import com.telink.ble.mesh.util.MeshLogger;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity implements IBaseView, EventListener<String> {
    protected View contentView;
    protected Activity mActivity;
    protected String TAG;
    private KProgressHUD mHud;
    protected NetService netService;
    protected Toast toast;
    private AlertDialog locationWarningDialog;
    protected boolean showDialog = true;
    private AlertDialog bleStateDialog;
    private TextView waitingTip;
    private AlertDialog.Builder confirmDialogBuilder;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowStatusBarColor(R.color.bg_black);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mActivity = this;
        TAG = getClass().getSimpleName();
        Bundle bundle = getIntent().getExtras();
        initData(bundle);
        setBaseView(bindLayout());
        ButterKnife.bind(this);
        TelinkMeshApplication.getInstance().addEventListener(ScanEvent.EVENT_TYPE_SCAN_LOCATION_WARNING, this);
        TelinkMeshApplication.getInstance().addEventListener(BluetoothEvent.EVENT_TYPE_BLUETOOTH_STATE_CHANGE, this);
        initView(savedInstanceState, contentView);
        netService = RetrofitHelper.getService();
        doBusiness();
        initListener();
    }

    protected void setBaseView(int layoutId) {
        setContentView(contentView = LayoutInflater.from(this).inflate(layoutId, null));
    }

    protected void setFullWindow() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void initData(Bundle bundle) {
    }

    public void setWindowStatusBarColor(int colorResId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(colorResId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void showProgress() {
        mHud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false);
        mHud.show();
    }

    protected void dissmissProgress() {
        mHud.dismiss();
    }
//    protected boolean validateNormalStart(Bundle savedInstanceState) {
//        if (savedInstanceState != null) {
//            MeshLogger.w(TAG + " application recreate");
//            Intent intent = new Intent(this, SplashActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            finish();
//            return false;
//        }
//        return true;
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TelinkMeshApplication.getInstance().removeEventListener(ScanEvent.EVENT_TYPE_SCAN_LOCATION_WARNING, this);
        TelinkMeshApplication.getInstance().removeEventListener(BluetoothEvent.EVENT_TYPE_BLUETOOTH_STATE_CHANGE, this);
        MeshLogger.w(TAG + " onDestroy");
    }

    @Override
    public void finish() {
        super.finish();
        MeshLogger.w(TAG + " finish");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MeshLogger.w(TAG + " onResume");
    }

    public void toastMsg(CharSequence s) {

        if (this.toast != null) {
            this.toast.setView(this.toast.getView());
            this.toast.setDuration(Toast.LENGTH_SHORT);
            this.toast.setText(s);
            this.toast.show();
        }
    }
       public void showConfirmDialog(String msg, DialogInterface.OnClickListener confirmClick) {
        if (confirmDialogBuilder == null) {
            confirmDialogBuilder = new AlertDialog.Builder(this);
            confirmDialogBuilder.setCancelable(true);
            confirmDialogBuilder.setTitle("Warning");
//            confirmDialogBuilder.setMessage(msg);
            confirmDialogBuilder.setPositiveButton("Confirm", confirmClick);

            confirmDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        confirmDialogBuilder.setMessage(msg);
        confirmDialogBuilder.show();
    }


    public void showLocationDialog() {
        if (locationWarningDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Warning");
            builder.setMessage(R.string.message_location_disabled_warning);
            builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent enableLocationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(enableLocationIntent, 1);
                }
            });
            builder.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setNeutralButton("Never Mind", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferenceHelper.setLocationIgnore(BaseActivity.this, true);
                    dialog.dismiss();
                }
            });
            locationWarningDialog = builder.create();
            locationWarningDialog.show();
        } else if (!locationWarningDialog.isShowing()) {
            locationWarningDialog.show();
        }
    }

    private void showBleStateDialog() {
        if (bleStateDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
//            builder.setTitle("Warning");
            builder.setMessage(R.string.message_ble_state_disabled);
            builder.setPositiveButton(getString(R.string.enable), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MeshService.getInstance().enableBluetooth();
                }
            });
            builder.setNegativeButton(getString(R.string.ignore), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setNeutralButton(getString(R.string.goSetting), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent enableLocationIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                    startActivityForResult(enableLocationIntent, 1);
                }
            });
            bleStateDialog = builder.create();
            bleStateDialog.show();
        } else if (!bleStateDialog.isShowing()) {
            bleStateDialog.show();
        }
    }

    private void dismissBleStateDialog() {
        if (bleStateDialog != null && bleStateDialog.isShowing()) {
            bleStateDialog.dismiss();
        }
    }


    @Override
    public void performed(Event<String> event) {
        if (event.getType().equals(ScanEvent.EVENT_TYPE_SCAN_LOCATION_WARNING)) {
            if (!SharedPreferenceHelper.isLocationIgnore(this)) {
                if (showDialog) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showLocationDialog();
                        }
                    });
                }
            }
        } else if (event.getType().equals(BluetoothEvent.EVENT_TYPE_BLUETOOTH_STATE_CHANGE)) {
            int state = ((BluetoothEvent) event).getState();
            if (state == BluetoothAdapter.STATE_OFF) {
                showBleStateDialog();
            } else if (state == BluetoothAdapter.STATE_ON) {
                dismissBleStateDialog();
            }
        }
    }



}

