package com.dryht.mobile.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dryht.mobile.Activity.NoticeInfoActivity;
import com.dryht.mobile.Bean.Notice;
import com.dryht.mobile.R;
import com.dryht.mobile.Util.OnRecyclerItemClickListener;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.squareup.picasso.Picasso;
import com.xuexiang.xui.widget.imageview.RadiusImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class RecycleViewNoticeAdapter extends RecyclerView.Adapter<RecycleViewNoticeAdapter.Holder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<Notice> mData;
    private int mLayoutId;
//声明自定义的监听接口
private OnRecyclerItemClickListener monItemClickListener;

public RecycleViewNoticeAdapter(Context context, List<Notice> data, int layoutId) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mData = data;
        this.mLayoutId = layoutId;
        }

@Override
public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(mLayoutId, parent, false);
        return new Holder(view);
        }

@Override
public void onBindViewHolder(@NonNull Holder holder, int position){
    holder.noticec_name.setText(this.mData.get(position).getCname());
    holder.notice_time.setText(this.mData.get(position).getDate().toString());
    holder.noticet_name.setText(this.mData.get(position).getTname());
    holder.notice_intro.setText(this.mData.get(position).getName());
    holder.cardView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                Intent intent = new Intent(mContext, NoticeInfoActivity.class);
                intent.putExtra("infoid",mData.get(position).getInfoid());
                mContext.startActivity(intent);
        }
    });
}
@Override
public int getItemCount() {
        return mData.size();
        }




class Holder extends RecyclerView.ViewHolder  {
    TextView noticec_name,noticet_name,notice_time,notice_intro;
    CardView cardView;
    public Holder(View itemView) {
        super(itemView);
        noticec_name = itemView.findViewById(R.id.noticec_name);
        noticet_name = itemView.findViewById(R.id.noticet_name);
        notice_time = itemView.findViewById(R.id.notice_time);
        notice_intro = itemView.findViewById(R.id.notice_intro);
        cardView = itemView.findViewById(R.id.card_view2);
    }

}
}

