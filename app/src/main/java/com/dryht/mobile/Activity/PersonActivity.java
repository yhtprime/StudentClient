package com.dryht.mobile.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dryht.mobile.R;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

public class PersonActivity extends AppCompatActivity {

private SuperTextView changeinfo,showlike,changepasswd;
    private TitleBar titleBar;
private Intent intent = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        changeinfo = findViewById(R.id.changeinfo);
        showlike = findViewById(R.id.showlike);
        changepasswd = findViewById(R.id.changepasswd);
        //设置顶部导航栏
        StatusBarUtils.setStatusBarDarkMode(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.thiscolor));
        changeinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(PersonActivity.this,ChangeInfoActivity.class);
                startActivity(intent);
            }
        });
        showlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(PersonActivity.this,PersonLikeActivity.class);
                startActivity(intent);
            }
        });
        changepasswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(PersonActivity.this,ChangePwdActivity.class);
                startActivity(intent);
            }
        });
        titleBar = findViewById(R.id.person_titlebar);
        titleBar.setBackground(getResources().getDrawable(R.color.thiscolor));
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        }).setLeftImageDrawable(getResources().getDrawable(R.drawable.back));
    }

}
