package com.dryht.mobile.Fragment;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dryht.mobile.Activity.FdActivity;
import com.dryht.mobile.Activity.MainActivity;
import com.dryht.mobile.Adapter.ImageSelectGridAdapter;
import com.dryht.mobile.R;
import com.dryht.mobile.Util.Utils;
import com.dryht.mobile.utils.XToastUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.xuexiang.xpage.PageConfig;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.button.ButtonView;
import com.xuexiang.xui.widget.edittext.ClearEditText;
import com.xuexiang.xui.widget.edittext.MultiLineEditText;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Page
public class FriendSendFragment extends BaseFragment implements ImageSelectGridAdapter.OnAddPicClickListener {
    View view;
    private TitleBar titleBar;
    private RecyclerView recyclerView;
    private ClearEditText sendtitle;
    private MultiLineEditText sendintro;
    private ButtonView sendmess;
    private ImageSelectGridAdapter mAdapter;
    private Handler mHandler;
    private List<LocalMedia> mSelectList = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    public FriendSendFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_friend_send;
    }


    protected void initViews() {
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 4, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(mAdapter = new ImageSelectGridAdapter(getActivity(), this));
        mAdapter.setSelectList(mSelectList);
        mAdapter.setSelectMax(3);
        mAdapter.setOnItemClickListener(new ImageSelectGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                PictureSelector.create(FriendSendFragment.this).themeStyle(R.style.XUIPictureStyle).openExternalPreview(position, mSelectList);
            }
        });
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDestroy();
                getActivity().finish();
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friend_send, container, false);
        sharedPreferences= getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        sendtitle = view.findViewById(R.id.sendtitle);
        sendintro = view.findViewById(R.id.sendintro);
        sendmess = view.findViewById(R.id.send_share);
        titleBar = view.findViewById(R.id.newinfotitle);
        sendmess.setOnClickListener(new sendmessage());
        recyclerView = view.findViewById(R.id.send_recycler_view);
        initViews();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择
                    mSelectList = PictureSelector.obtainMultipleResult(data);
                    mAdapter.setSelectList(mSelectList);
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }


    public void onAddPicClick() {
        Utils.getPictureSelector(this)
                .selectionMedia(mSelectList)
                .maxSelectNum(3)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    private class sendmessage implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(sendtitle.getText().toString()==null||sendintro.getEditText().getText()==null)
            {
                Toast.makeText(getContext(),"标题和内容不能为空",Toast.LENGTH_LONG).show();
            }
            //获取图片和信息上传
            OkHttpClient mOkHttpClient=new OkHttpClient();
            MultipartBody.Builder builder=  new MultipartBody.Builder().setType(MultipartBody.FORM);
            for (int i = 0;i<mSelectList.size();i++) {
                RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), new File(String.valueOf(mSelectList.get(i).getPath())));
                //截取文件名
                String name =  mSelectList.get(i).getPath().substring(mSelectList.get(i).getPath().lastIndexOf(".")-1);
                builder.addFormDataPart("img",name,fileBody);
            }
            builder.addFormDataPart("auth",sharedPreferences.getString("auth","0"));
            builder.addFormDataPart("identity",sharedPreferences.getString("identity","0"));
            builder.addFormDataPart("name",sendtitle.getText().toString());
            builder.addFormDataPart("intro",sendintro.getEditText().getText().toString());
            Request mRequest=new Request.Builder()
                    .url(com.dryht.mobile.Util.Utils.generalUrl+"sendmessage/")
                    .post(builder.build())
                    .build();

            mOkHttpClient.newCall(mRequest).enqueue(new Callback(){
                @Override
                public void onResponse(@NotNull okhttp3.Call call, @NotNull Response response) throws IOException {
                    Looper.prepare();
                    //String转JSONObject
                    JSONObject result = null;
                    try {
                        result = new JSONObject(response.body().string());
                        System.out.println(result.get("status"));
                        //取数据
                        if(result.get("status").equals("1"))
                        {
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    onDestroy();
                                    getActivity().finish();
                                }
                            },0);

                        }
                        else
                        {
                            Toast.makeText(getContext(),"失败",Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Looper.loop();

                }

                @Override
                public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                    Looper.prepare();
                    Toast.makeText(getContext(),"网络请求失败",Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            });

        }
    }
}