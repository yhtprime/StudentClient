package com.dryht.mobile.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.dryht.mobile.Adapter.RecycleViewNewAdapter;
import com.dryht.mobile.R;
import com.dryht.mobile.Util.OnRecyclerItemClickListener;
import com.dryht.mobile.Util.Utils;
import com.dryht.mobile.utils.XToastUtils;
import com.squareup.picasso.Picasso;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import net.nightwhistler.htmlspanner.HtmlSpanner;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewInfoActivity extends AppCompatActivity {
    private TextView title,type,new_time;
    private Handler mHandler;
    private String newid;
    private WebView webview_showpage;
    private TitleBar mTitleBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_info);
        newid = getIntent().getStringExtra("newid");
        webview_showpage = findViewById(R.id.newintro);
        title= findViewById(R.id.new_title);
        type= findViewById(R.id.new_type);
        new_time= findViewById(R.id.new_time);
        mTitleBar = findViewById(R.id.newinfotitle);
        mTitleBar.setBackground(getResources().getDrawable(R.color.thiscolor));
        //设置顶部导航栏
        StatusBarUtils.setStatusBarDarkMode(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.thiscolor));
        mHandler = new Handler();
        initTitleBar();
        getinformation();
    }
//初始化titlebar
    private void initTitleBar() {
        mTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //富文本设置
    private void setWebView(String content) {
        webview_showpage.getSettings().setJavaScriptEnabled(true);
        webview_showpage.getSettings().setBuiltInZoomControls(true);
        webview_showpage.getSettings().setDisplayZoomControls(false);
        webview_showpage.setWebChromeClient(new WebChromeClient());
        webview_showpage.setWebViewClient(new WebViewClient());
        webview_showpage.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); //取消滚动条白边效果
        webview_showpage.getSettings().setDefaultTextEncodingName("UTF-8") ;
        webview_showpage.getSettings().setBlockNetworkImage(false);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            webview_showpage.getSettings().setMixedContentMode(webview_showpage.getSettings()
                    .MIXED_CONTENT_ALWAYS_ALLOW);  //注意安卓5.0以上的权限
        }
        webview_showpage.loadDataWithBaseURL(null, getNewContent(content), "text/html", "UTF-8", null);
    }


    private String getNewContent(String htmltext) {
        Document doc = Jsoup.parse(htmltext);
        Elements elements = doc.getElementsByTag("img");
        for (Element element : elements) {
            element.attr("width", "100%").attr("max-width", "100%").attr("height", "auto");
        }
        return doc.toString();
    }


    private void getinformation(){
        OkHttpClient mOkHttpClient=new OkHttpClient();
        FormBody mFormBody=new FormBody.Builder().add("newid",newid).build();
        Request mRequest=new Request.Builder()
                .url(Utils.generalUrl+"getANew/")
                .post(mFormBody)
                .build();

        mOkHttpClient.newCall(mRequest).enqueue(new Callback(){
            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull Response response) throws IOException {
                //String转JSONObject
                JSONObject result = null;
                try {
                    result = new JSONObject(response.body().string());
                    //取数据
                    if(result.get("status").equals("1"))
                    {

                        final JSONObject finalResult = new JSONObject(result.get("data").toString());
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    title.setText(finalResult.getString("name"));
                                    type.setText(finalResult.getString("cname"));
                                    new_time.setText(finalResult.getString("time"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },0);
                        final String res = finalResult.getString("intro").toString();
                        NewInfoActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                // 显示到 TextView上
                                setWebView(res);
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                XToastUtils.toast("获取新闻信息失败");
            }
        });
    }
}
