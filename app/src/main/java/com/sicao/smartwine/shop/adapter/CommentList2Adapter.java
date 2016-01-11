package com.sicao.smartwine.shop.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sicao.smartwine.R;
import com.sicao.smartwine.shop.entity.CommentList;
import com.sicao.smartwine.widget.emoj.EmoticonsTextView;

import java.util.ArrayList;


/***
 * 二层回复列表适配器
 * 
 * @author mingqi'li
 * 
 */
public class CommentList2Adapter extends BaseAdapter {

	private Context mContext;

	private ArrayList<CommentList> mList;

	private LayoutInflater mInflater;

	private ViewHolder mHolder;

	public CommentList2Adapter(Context context, ArrayList<CommentList> list) {
		this.mContext = context;
		this.mList = list;
		this.mInflater = LayoutInflater.from(mContext);
	}

	public void setmList(ArrayList<CommentList> mList) {
		this.mList = mList;
	}

	public void update(ArrayList<CommentList> list) {

		setmList(list);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public CommentList getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mList.get(position).hashCode();
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			mHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.comment_list_2_item, null);
			mHolder.name = (EmoticonsTextView) convertView
					.findViewById(R.id.comment_list_2_item_name);
			mHolder.nameb = (TextView) convertView
					.findViewById(R.id.comment_list_2_item_nameb);
			mHolder.test = (EmoticonsTextView) convertView
					.findViewById(R.id.test7);
			convertView.setTag(mHolder);
		} else {

			mHolder = (ViewHolder) convertView.getTag();
		}
		// 二层回复数据组合
		final CommentList comment = mList.get(position);
		String nameb = (comment.getUnameb() == null || "".equals(comment
				.getUnameb())) ? "游客:" : comment.getUnameb() + ":";
		String namea = comment.getUnamea().toString().trim();
		//spanner要封装的字体
		String son=namea + " 回复 " + nameb.trim()
				+ comment.getContent();
		/**
		 * ismyself = intent.getBooleanExtra("ismyself", false); // uid uid =
		 * intent.getStringExtra("uid");
		 */
		mHolder.name.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null!=mCommentOnclick) {
					mCommentOnclick.setCommentOnclick(comment);
				}
			}
		});
		
		mHolder.name.setComment(namea, nameb, mContext, comment);
		mHolder.name.setText(son);
		mHolder.name.setMovementMethod(LinkMovementMethod.getInstance());
		return convertView;
	}

	class ViewHolder {
		TextView  nameb;
		EmoticonsTextView name, content, test;
	}
	public interface CommentOnclick{
		public void setCommentOnclick(CommentList mCommentList);
	}
	public CommentOnclick mCommentOnclick;
	public void setCommentOnclickListener(CommentOnclick commentOnclick){
		this.mCommentOnclick=commentOnclick;
	}
}
