package com.kunboy.weiboclient.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;


import com.kunboy.weiboclient.R;

import java.util.ArrayList;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

	private ArrayList<String> mDataset = new ArrayList<String>();
	private OnItemClickListener mOnItemClickListener;

	// Provide a suitable constructor (depends on the kind of dataset)
	public MyAdapter(String[] myDataset) {
		mDataset.clear();
		mDataset.addAll(mDataset);
	}

	public ArrayList<String> getData() {
		return mDataset;
	}

	public void setData(String[] myDataset) {
		mDataset.clear();
		mDataset.addAll(mDataset);
	}

	// Provide a reference to the views for each data item
	// Complex data items may need more than one view per item, and
	// you provide access to all the views for a data item in a view holder
	public static class ViewHolder extends RecyclerView.ViewHolder {
		// each data item is just a string in this case
		public TextView mTextView;

		public ViewHolder(View v) {
			super(v);
		}
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return mDataset.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder arg0, final int arg1) {
		// TODO Auto-generated method stub
		arg0.mTextView.setText(mDataset.get(arg1));
		arg0.itemView.setTag(mDataset.get(arg1));
		arg0.itemView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onItemClick(v);
				}
			}
		});
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup arg0, final int arg1) {
		// TODO Auto-generated method stub
		// create a new view
		View v = LayoutInflater.from(arg0.getContext()).inflate(
				R.layout.my_card_view, arg0, false);
		TextView mytext = (TextView) v.findViewById(R.id.info_text);
		// set the view's size, margins, paddings and layout parameters
		ViewHolder vh = new ViewHolder(v);
		vh.mTextView = (TextView) v.findViewById(R.id.info_text);
		return vh;
	}

	public interface OnItemClickListener {
		public void onItemClick(View view);
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}

}
