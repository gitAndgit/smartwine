package com.sicao.smartwine.widget.emoj;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.sicao.smartwine.R;
import com.sicao.smartwine.shop.entity.CommentList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmoticonsTextView extends TextView {

	public EmoticonsTextView(Context context) {
		super(context);
	}

	public EmoticonsTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public EmoticonsTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		if (!TextUtils.isEmpty(text)) {
			super.setText(replace(text.toString()), type);
		} else {
			super.setText(text, type);
		}
	}
	private Context mContext;
	private CommentList mcomment;
	//参数传进来
	private String namea,nameb;
	public void setComment(String namea,String nameb,Context context,CommentList mcomment){
		this.mContext=context;
		this.namea=namea;
		this.nameb=nameb;
		this.mcomment=mcomment;
	}

	private Pattern buildPattern() {
		return Pattern.compile("\\\\ue[a-z0-9]{3}", Pattern.CASE_INSENSITIVE);
	}

	private CharSequence replace(String text) {
		try {
			SpannableString spannableString = new SpannableString(text);
			int start = 0;
			Pattern pattern = buildPattern();
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				String faceText = matcher.group();
				String key = faceText.substring(1);
				BitmapFactory.Options options = new BitmapFactory.Options();
				Bitmap bitmap = BitmapFactory.decodeResource(
						getContext().getResources(),
						getContext().getResources().getIdentifier(key,
								"drawable", getContext().getPackageName()),
						options);
				ImageSpan imageSpan = new ImageSpan(getContext(), bitmap);
				int startIndex = text.indexOf(faceText, start);
				int endIndex = startIndex + faceText.length();
				if (startIndex >= 0)
					spannableString.setSpan(imageSpan, startIndex, endIndex,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				start = (endIndex - 1);
			}
			/*spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#f15899")), 
					0, namea.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#f15899")), 
					namea.length()+2, namea.length()+1+nameb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);*/
			//评论者的姓名点击事件
			spannableString.setSpan(new ClickableSpan(){

				@Override
				public void onClick(View widget) {
//					if (!UserInfoUtil.getLogin(mContext)) {
//						UIHelper.startLoginActivity(mContext, false);
//						return;
//					}
//					Intent intent = new Intent(mContext, UserCenterActivity.class);
//					if (UserInfoUtil.getUID(mContext).equals(mcomment.getUida())) {
//						intent.putExtra("uid", UserInfoUtil.getUID(mContext));
//					} else {
//						intent.putExtra("uid", mcomment.getUida());
//					}
//					mContext.startActivity(intent);
				}
				
			      }, 
					0, namea.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			//被评论者的姓名点击事件
			spannableString.setSpan(new ClickableSpan(){
				@Override
				public void onClick(View widget) {
//					if (!UserInfoUtil.getLogin(mContext)) {
//						UIHelper.startLoginActivity(mContext, false);
//						return;
//					}
//					Intent intent = new Intent(mContext, UserCenterActivity.class);
//					if (UserInfoUtil.getUID(mContext).equals(mcomment.getUida())) {
//						intent.putExtra("uid", UserInfoUtil.getUID(mContext));
//					} else {
//						intent.putExtra("uid", mcomment.getUidb());
//					}
//					mContext.startActivity(intent);
				}
				
			}, 
					namea.length()+4, namea.length()+4+nameb.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.baseColor)),
					0, namea.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.baseColor)),
					namea.length()+4, namea.length()+4+nameb.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			return spannableString;
		} catch (Exception e) {
			return text;
		}
	}
}
