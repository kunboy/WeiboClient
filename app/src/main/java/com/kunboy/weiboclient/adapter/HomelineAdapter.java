package com.kunboy.weiboclient.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.kunboy.weiboclient.R;
import com.kunboy.weiboclient.debug.DebugLog;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class HomelineAdapter extends RecyclerView.Adapter<HomelineAdapter.ViewHolder> {

    public static final String TAG = "com.kunboy.weiboclient.adapter.HomelineAdapter";
    private ArrayList<Status> mDatas ;
	private OnItemClickListener mOnItemClickListener;
    private Context mContext;

	// Provide a suitable constructor (depends on the kind of dataset)
	public HomelineAdapter(Context context,ArrayList<Status> datas) {
        this.mContext = context;
        this.mDatas = datas;
	}

	public ArrayList<Status> getData() {
		return mDatas;
	}

	public void setData(ArrayList<Status> datas) {
		mDatas = datas;
	}

	// Provide a reference to the views for each data item
	// Complex data items may need more than one view per item, and
	// you provide access to all the views for a data item in a view holder
	public static class ViewHolder extends RecyclerView.ViewHolder {
		// each data item is just a string in this case
		public TextView mStatus_info;
        public ImageView mUserImg;
        public TextView mUserName;
        public TextView mStatusTime;
        public ImageView mStatusImg;
        public TextView mRepostsButton;
        public TextView mCommentsButton;

		public ViewHolder(View v) {
			super(v);
		}
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return mDatas == null ? 0 :mDatas.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, final int arg1) {
        viewHolder.mStatus_info.setText(mDatas.get(arg1).text);
        viewHolder.mUserName.setText(mDatas.get(arg1).user.name);
        viewHolder.mStatusTime.setText(mDatas.get(arg1).created_at);
        Picasso.with(mContext).load(mDatas.get(arg1).user.avatar_large).into(viewHolder.mUserImg);
        String statusImgUrl = mDatas.get(arg1).bmiddle_pic;
        viewHolder.mRepostsButton.setText("转发+"+mDatas.get(arg1).reposts_count);
        viewHolder.mCommentsButton.setText("评论+"+mDatas.get(arg1).comments_count);
        viewHolder.mStatusImg.setImageBitmap(null);
        if (mDatas.get(arg1).pic_urls!=null && mDatas.get(arg1).pic_urls.size() > 1){
            if(!TextUtils.isEmpty(statusImgUrl)){
                DebugLog.d(TAG,"onBindViewHolder","statusImgUrl:"+statusImgUrl);
                Picasso.with(mContext).load(mDatas.get(arg1).bmiddle_pic).into(viewHolder.mStatusImg);
            }
        }else{
            if(!TextUtils.isEmpty(statusImgUrl)){
                DebugLog.d(TAG,"onBindViewHolder","statusImgUrl:"+statusImgUrl);
                Picasso.with(mContext).load(mDatas.get(arg1).bmiddle_pic).into(viewHolder.mStatusImg);
            }
        }
//        Status retweeted_status = mDatas.statusList.get(arg1).retweeted_status;
//        if(retweeted_status != null){
//
//        }


    }

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup arg0, final int arg1) {
		// TODO Auto-generated method stub
		// create a new view
		View v = LayoutInflater.from(arg0.getContext()).inflate(
				R.layout.status_card_view, arg0, false);
		ViewHolder vh = new ViewHolder(v);
        vh.mStatus_info = (TextView) v.findViewById(R.id.status_info_text);
        vh.mUserImg = (ImageView) v.findViewById(R.id.status_user_img);
        vh.mUserName = (TextView) v.findViewById(R.id.status_user_name);
        vh.mStatusTime = (TextView) v.findViewById(R.id.status_time);
        vh.mStatusImg = (ImageView) v.findViewById(R.id.status_img);
        vh.mRepostsButton = (TextView) v.findViewById(R.id.status_reposts_count);
        vh.mCommentsButton = (TextView) v.findViewById(R.id.status_comments_count);
		return vh;
	}

	public interface OnItemClickListener {
		public void onItemClick(View view);
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}

}
