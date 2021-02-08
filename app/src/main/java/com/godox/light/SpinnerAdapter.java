package com.godox.light;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class SpinnerAdapter extends BaseAdapter {
    List<String> ListArr;
    Context context;

    public SpinnerAdapter(Context context, List<String> ListArr) {
        this.context = context;
        this.ListArr = ListArr;
    }

    @Override
    public int getCount() {
        return ListArr.size();
    }

    @Override
    public Object getItem(int position) {
        return ListArr.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.spinner_item, null);
        TextView textView = view.findViewById(R.id.text);
        textView.setTextColor(context.getResources().getColor(android.R.color.white));
        textView.setText(ListArr.get(position));
        return view;
    }
}
