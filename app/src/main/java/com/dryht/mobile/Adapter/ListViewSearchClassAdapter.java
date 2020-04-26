package com.dryht.mobile.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.dryht.mobile.R;
import com.dryht.mobile.Bean.Lesson;

import java.util.List;

public class ListViewSearchClassAdapter extends ArrayAdapter {
    private Lesson lesson; // 获取当前项的Fruit实例
    private View view;
    private ViewHolder viewHolder;
    private int resourceId;

    public ListViewSearchClassAdapter(Context context, int textViewResourceId, List<Lesson> className) {
        super(context, textViewResourceId, className);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        lesson = (Lesson) getItem(position); // 获取当前项的Fruit实例
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            view.setTag(viewHolder); // 将ViewHolder存储在View中
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
        }
        viewHolder.classitem = view.findViewById(R.id.class_item);
        viewHolder.classitem.setText(lesson.getName()+" "+lesson.getPlace()+" "+lesson.getTeacher());
        return view;
    }
    class ViewHolder {
        TextView classitem;
    }
}
