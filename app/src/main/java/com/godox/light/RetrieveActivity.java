package com.godox.light;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.zlm.base.BaseBackActivity;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrieveActivity extends BaseBackActivity {


    @Bind(R.id.et_email)
    EditText etEmail;
    @Bind(R.id.et_code)
    EditText etCode;
    @Bind(R.id.tv_get_code)
    TextView tvGetCode;
    @Bind(R.id.et_password)
    EditText etPassword;
    @Bind(R.id.ibtn_eye1)
    ImageButton ibtnEye1;
    @Bind(R.id.ibtn_reset)
    ImageButton ibtnReset;
    @Bind(R.id.et_password_again)
    EditText etPasswordAgain;
    @Bind(R.id.ibtn_eye2)
    ImageButton ibtnEye2;
    private boolean mIsGetYzm;

    @Override
    public int bindLayout() {
        return R.layout.activity_retrieve;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {

    }

    @Override
    public void doBusiness() {

    }

    @Override
    public void initListener() {
        ibtnEye1.setOnTouchListener(new View.OnTouchListener() {
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
        ibtnEye2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    etPasswordAgain.setInputType(128);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    etPasswordAgain.setInputType(129);
                }
                etPasswordAgain.setSelection(etPasswordAgain.getText().length());
                return false;
            }
        });
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mIsGetYzm = false;
                tvGetCode.setText(getString(R.string.get_code));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick({R.id.tv_get_code, R.id.ibtn_reset})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_get_code:
                if (mIsGetYzm) {
                    Toast.makeText(mActivity, R.string.please_not_repet, Toast.LENGTH_SHORT).show();
                    return;
                }
                String email = etEmail.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(mActivity, R.string.email_not_null, Toast.LENGTH_SHORT).show();
                    return;
                }
                netService.validateCode(email).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String smsResult = new String(response.body().bytes());
                            JSONObject smsResultObject = new JSONObject(smsResult);
                            int status = smsResultObject.getInt("status");
                            String msg = smsResultObject.getString("msg");
                            if (status != 0) {
                                ibtnReset.setClickable(false);
                                ToastUtils.showShort(msg);
                            } else {
                                ToastUtils.showShort(getString(R.string.code_has_sent_email));
                                tvGetCode.setText(R.string.has_sent);
                                mIsGetYzm = true;
                                ibtnReset.setClickable(true);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
                break;
            case R.id.ibtn_reset:
                String emaill = etEmail.getText().toString();
                String code = etCode.getText().toString();
                String password = etPassword.getText().toString();
                String passwordAgain = etPasswordAgain.getText().toString();
                if (!TextUtils.isEmpty(emaill) && !TextUtils.isEmpty(code) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(passwordAgain)) {
                    if (password.equals(passwordAgain)) {
                        netService.getBackPwd(emaill, code, password).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    String result = new String(response.body().bytes());
                                    JSONObject resultObject = new JSONObject(result);
                                    int status = resultObject.getInt("status");
                                    String msg = resultObject.getString("msg");
                                    if (status != 0) {
                                        ToastUtils.showShort(msg);
                                    } else {
                                        ToastUtils.showShort(getString(R.string.modified_successfully));
                                        finish();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                ToastUtils.showShort(getString(R.string.modified_fail));
                            }
                        });
                    }else{
                        ToastUtils.showShort(getString(R.string.modified_diffrent));
                    }
                }else{
                    ToastUtils.showShort(getString(R.string.input_not_nulll));
                }
                break;
        }
    }
}
