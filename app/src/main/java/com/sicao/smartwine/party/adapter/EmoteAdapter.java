package com.sicao.smartwine.party.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sicao.smartwine.R;
import com.sicao.smartwine.widget.emoj.FaceText;
import java.util.List;


public class EmoteAdapter extends BaseArrayListAdapter {
	/**
	 * 显示emoj表情适配器
	 * 
	 * @param context
	 *            上下文
	 * @param datas
	 *            表情组(单叶)
	 */
	public EmoteAdapter(Context context, List<FaceText> datas) {
		super(context, datas);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_face_text, null);
			holder = new ViewHolder();
			holder.mIvImage = (ImageView) convertView
					.findViewById(R.id.v_face_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		FaceText faceText = (FaceText) getItem(position);
		String key = faceText.text.substring(1);
		Drawable drawable = mContext.getResources().getDrawable(
				mContext.getResources().getIdentifier(key, "drawable",
						mContext.getPackageName()));
		holder.mIvImage.setImageDrawable(drawable);
		return convertView;
	}

	class ViewHolder {
		ImageView mIvImage;
	}
}
