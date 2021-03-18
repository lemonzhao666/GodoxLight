package com.godox.light;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.zlm.base.BaseBackActivity;

import java.util.Locale;

public class SelectLangActivity extends BaseBackActivity {


    private String[] items;
    private ListView listView;
    private MyAdapter myAdapter;
    private SPUtils spUtils;

    @Override
    public int bindLayout() {
        return R.layout.activity_select_lang;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        items = new String[]{ getString(R.string.SimplifiedChinese), "English"};
        listView = findViewById(R.id.listview);
        myAdapter = new MyAdapter(0);
        listView.setAdapter(myAdapter);
        String language = getResources().getConfiguration().locale.getLanguage();
        if(language.equals(Locale.SIMPLIFIED_CHINESE.getLanguage())){
            myAdapter.setCurrentPosition(0);
        }else if(language.equals(Locale.ENGLISH.getLanguage())){
            myAdapter.setCurrentPosition(1);
        }

    }

    @Override
    public void doBusiness() {

    }

    @Override
    public void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myAdapter.setCurrentPosition(position);
                Resources resources = getResources();
                DisplayMetrics dm = resources.getDisplayMetrics();
                Configuration config = resources.getConfiguration();
                switch (position){
                    case 0:
                        config.setLocale(Locale.SIMPLIFIED_CHINESE);
                        break;
                    case 1:
                        config.setLocale(Locale.ENGLISH);
                        break;
                }
                resources.updateConfiguration(config, dm);

            }
        });
    }

    class MyAdapter extends BaseAdapter {
        int position;

        public MyAdapter(int position) {
            this.position = position;
        }

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(mActivity, R.layout.item_lang, null);
            ImageView ivGou = view.findViewById(R.id.iv_gou);
            TextView tvLang = view.findViewById(R.id.tv_lang);
            tvLang.setText(items[position]);
            if (position == this.position) {
                ivGou.setVisibility(View.VISIBLE);
            } else {
                ivGou.setVisibility(View.INVISIBLE);
            }
            return view;
        }

        public void setCurrentPosition(int position) {
            this.position = position;
            notifyDataSetChanged();
        }
    }

    @Override
    public void finish() {
        super.finish();
        startActivity(new Intent(mActivity, SettingActivity.class));
    }
}
