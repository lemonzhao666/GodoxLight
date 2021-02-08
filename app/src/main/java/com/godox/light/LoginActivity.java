package com.godox.light;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.zlm.base.BaseBackActivity;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseBackActivity {

    @Bind(R.id.et_email)
    EditText etEmail;
    @Bind(R.id.et_password)
    EditText etPassword;
    @Bind(R.id.ibtn_eye)
    ImageButton ibtnEye;
    @Bind(R.id.iv_modify_password)
    TextView ivModifyPassword;
    @Bind(R.id.ibtn_login)
    ImageButton ibtnLogin;
    @Bind(R.id.ibtn_create_account)
    TextView ibtnCreateAccount;

    @Override
    public int bindLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {

    }

    @Override
    public void doBusiness() {

    }

    @Override
    public void initListener() {
        ibtnEye.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    etPassword.setInputType(128);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    etPassword.setInputType(129);
                }
                etPassword.setSelection(etPassword.getText().length());
                return false;
            }
        });
    }


    @OnClick({R.id.iv_modify_password, R.id.ibtn_login,R.id.ibtn_create_account})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_modify_password:
                startActivity(new Intent(mActivity, RetrieveActivity.class));
                break;
            case R.id.ibtn_login:
                String email = etEmail.getText().toString();
                String pass = etPassword.getText().toString();
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {
                    netService.login(email, pass).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String result = new String(response.body().bytes());
                                JSONObject resultObj = new JSONObject(result);
                                int status = resultObj.getInt("status");
                                String msg = resultObj.getString("msg");
                                if (status != 0) {
                                    ToastUtils.showShort(msg);
                                } else {
                                    ToastUtils.showShort(getString(R.string.login_success));
                                    finish();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            ToastUtils.showShort(getString(R.string.login_success));
                        }
                    });
                }else{
                    ToastUtils.showShort(getString(R.string.input_null));
                }
                break;
            case R.id.ibtn_create_account:
                startActivity(new Intent(mActivity, RegisterActivity.class));
                break;
        }
    }
}
