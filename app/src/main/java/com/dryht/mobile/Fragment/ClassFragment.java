package com.dryht.mobile.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.dryht.mobile.Activity.MainActivity;
import com.dryht.mobile.R;
import com.dryht.mobile.Util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.devmeteor.tableview.Lesson;
import cn.devmeteor.tableview.LessonView;
import cn.devmeteor.tableview.TableView;

public class ClassFragment extends Fragment {
    private View view;
    private TableView tableView;
    public ClassFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_class, container, false);
        tableView=view.findViewById(R.id.main_table);

        tableView.setLessons(getLessons(),getBgMap(), new LessonView.LessonClickListener() {
            @Override
            public void onClick(Lesson lesson) {
                Toast.makeText(getContext(),lesson.toString(),Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    //bgMap示例
    private Map<String,Integer> getBgMap(){
        Map<String,Integer> bgMap=new HashMap<>();
        Utils randcolor = new Utils();
        bgMap.put("高等数学A2",  randcolor.createRandomColor());
        bgMap.put("体育", Color.parseColor("#CC4CAF50"));
        bgMap.put("计算机基础及应用2", Color.parseColor("#CC4FAFA7"));
        bgMap.put("英国小说家与原著", Color.parseColor("#CCA8AF4C"));
        bgMap.put("大学外语", Color.parseColor("#CCAF4C7A"));
        bgMap.put("大学物理", Color.parseColor("#CCAF544C"));
        bgMap.put("大学物理实验", Color.parseColor("#CC5FBD2C"));
        bgMap.put("电路分析基础", Color.parseColor("#CCA6E252"));
        bgMap.put("思想道德修养与法律基础", Color.parseColor("#CC5271E2"));
        bgMap.put("电路分析实验", Color.parseColor("#CCE25263"));
        return bgMap;
    }

    //模拟生成课程数据，自定义课程bean可直接替换
    private List<Lesson> getLessons(){
        List<Lesson> lessons=new ArrayList<>();
        lessons.add(new Lesson("2018-2019-2", "第1周", "高等数学A2","mon",1,2, "崇师"));
        lessons.add(new Lesson("2018-2019-2", "第1周", "体育","mon",3,4, "足球场"));
        lessons.add(new Lesson("2018-2019-2", "第1周", "计算机基础及应用2","mon",5,6, "行知"));
        lessons.add(new Lesson("2018-2019-2", "第1周", "英国小说家与原著","mon",9,10, "崇师"));
        lessons.add(new Lesson("2018-2019-2", "第1周", "大学外语","tue",1,2, "理二"));
        lessons.add(new Lesson("2018-2019-2", "第1周", "大学物理","tue",3,4, "理二"));
        lessons.add(new Lesson("2018-2019-2", "第1周", "大学物理实验","tue",5,10, "理二"));
        lessons.add(new Lesson("2018-2019-2", "第1周", "高等数学A2","wed",1,2, "理二"));
        lessons.add(new Lesson("2018-2019-2", "第1周", "电路分析基础","wed",3,4, "理二"));
        lessons.add(new Lesson("2018-2019-2", "第1周", "思想道德修养与法律基础","thu",1,2, "崇师"));
        lessons.add(new Lesson("2018-2019-2", "第1周", "大学物理","thu",5,6, "理二"));
        lessons.add(new Lesson("2018-2019-2", "第1周", "电路分析基础","fri",1,2, "理二"));
        lessons.add(new Lesson("2018-2019-2", "第1周", "大学外语","fri",3,4, "理二"));
        lessons.add(new Lesson("2018-2019-2", "第1周", "电路分析实验","fri",5,6, "理二"));
        return lessons;
    }
}
