package com.godox.light;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zlm.base.model.NodeInfo;

import java.util.List;

public class ConnectedNodeAdapter extends BaseAdapter {
    List<NodeInfo> ListArr;
    Context context;

    public ConnectedNodeAdapter(Context context, List<NodeInfo> ListArr) {
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
        View view = View.inflate(context, R.layout.simple_list_item, null);
        TextView textView = view.findViewById(R.id.tv_name);
        textView.setText(ListArr.get(position).getName());
        return view;
    }
}
