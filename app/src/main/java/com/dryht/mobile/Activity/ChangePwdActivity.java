package com.dryht.mobile.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.dryht.mobile.R;
import com.dryht.mobile.Util.TokenUtils;
import com.dryht.mobile.utils.XToastUtils;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.button.ButtonView;
import com.xuexiang.xui.widget.dialog.DialogLoader;
/*
找回密码
 */
public class ChangePwdActivity extends AppCompatActivity {

    private TitleBar titleBar;
    private EditText change_account,change_pwd,change_pwd1;
    private ButtonView changepwd_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        titleBar = findViewById(R.id.changeinfo_bar);
        change_account = findViewById(R.id.change_account);
        change_pwd = findViewById(R.id.change_pwd);
        change_pwd1 = findViewById(R.id.change_pwd1);
        changepwd_btn = findViewById(R.id.changepwd_btn);
        //设置顶部导航栏
        StatusBarUtils.setStatusBarDarkMode(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.thiscolor));
        changepwd_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(change_account.getText().toString().equals(""))
                {
                    XToastUtils.error("请输入用户名");
                }
                else if (change_pwd1.getText().toString().equals("")||!(change_pwd.getText().toString().equals(change_pwd1.getText().toString())))
                {
                    XToastUtils.error("两次输入的密码不相同");
                    change_pwd.setText("");
                    change_pwd1.setText("");
                }
                else {
                    DialogLoader.getInstance().showConfirmDialog(
                            ChangePwdActivity.this,
                            getString(R.string.face_confirm),
                            getString(R.string.lab_yes),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //创建Intent对象
                                    Intent intent=new Intent();
                                    //将参数放入intent
                                    intent.putExtra("flag", 1);
                                    intent.putExtra("passwd", change_pwd.getText().toString());
                                    intent.putExtra("account", change_account.getText().toString());
                                    //跳转到指定的Activity
                                    intent.setClass(ChangePwdActivity.this, FdActivity.class);
                                    //启动Activity
                                    startActivity(intent);
                                    finish();
                                }
                            },
                            getString(R.string.lab_no),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }
                    );
                }

            }
        });
        titleBar.setBackground(getResources().getDrawable(R.color.thiscolor));
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        }).setLeftImageDrawable(getResources().getDrawable(R.drawable.back));
    }
}
