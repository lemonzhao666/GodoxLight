package com.zlm.base;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

public abstract class BaseBackActivity extends BaseActivity {
    protected LinearLayout rootLayout;
    protected Toolbar mToolbar;
    protected FrameLayout flActivityContainer;
    protected TextView tvTitle;
    protected TextView tvRight;
    public ImageButton ivRight;
    protected RadioGroup radioGroup;
    protected RadioButton rbtnConnected;
    protected RadioButton rbtnUnConnected;

    @Override
    protected void setBaseView(int layoutId) {
        contentView = LayoutInflater.from(this).inflate(R.layout.activity_top_back, null);
        setContentView(contentView);
        rootLayout = findViewById(R.id.root_layout);
        mToolbar = findViewById(R.id.toolbar);
        tvTitle = findViewById(R.id.tv_title);
        tvRight = findViewById(R.id.tv_right);
        ivRight = findViewById(R.id.iv_right);
        radioGroup = findViewById(R.id.rg_device);
        rbtnConnected = findViewById(R.id.rbtn_connected);
        rbtnUnConnected = findViewById(R.id.rbtn_unconnected);
        flActivityContainer = findViewById(R.id.activity_container);
        flActivityContainer.addView(LayoutInflater.from(this).inflate(layoutId, flActivityContainer, false));
        setSupportActionBar(mToolbar);
        getToolBar().setDisplayHomeAsUpEnabled(true);
        getToolBar().setDisplayShowTitleEnabled(false);
        setReturnIcon(R.mipmap.left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    protected ActionBar getToolBar() {
        return getSupportActionBar();
    }

    protected void setTitle(String title) {
        tvTitle.setText(title);
    }

    protected void setRightText(String text) {
        tvRight.setText(text);
        tvRight.setVisibility(View.VISIBLE);
    }


    protected void setBoldTitle(boolean isbold) {
        TextPaint paint = tvTitle.getPaint();
        tvTitle.setTextSize(16);
        paint.setFakeBoldText(isbold);
    }

    protected void setRightIcon(int rightIcon) {
        ivRight.setBackgroundResource(rightIcon);
        ivRight.setVisibility(View.VISIBLE);
    }

    protected void setReturnIcon(int iconId) {
        Drawable upArrow = getResources().getDrawable(iconId);
        upArrow.setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

}
