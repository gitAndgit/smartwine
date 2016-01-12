package com.sicao.smartwine.shop.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sicao.smartwine.AppContext;
import com.sicao.smartwine.R;
import com.sicao.smartwine.shop.entity.Comment;
import com.sicao.smartwine.shop.entity.CommentList;
import com.sicao.smartwine.util.UserInfoUtil;
import com.sicao.smartwine.widget.NoScrollListView;
import com.sicao.smartwine.widget.emoj.EmoticonsTextViewnew;
import com.sicao.smartwine.shop.adapter.CommentList2Adapter.CommentOnclick;
import java.util.ArrayList;

/**
 * 评论列表适配器
 * 
 * @author mingqi'li
 */
public class SnsCommentAdapter extends BaseAdapter {
	private ArrayList<Comment> list;
	private Context context;
	private ViewHolder viewHolder = null;

	/**
	 * 文章评论列表适配器
	 * 
	 * @param list
	 *            评论列表
	 * @param context
	 *            上下文
	 */
	public SnsCommentAdapter(ArrayList<Comment> list, Context context) {
		super();
		this.list = list;
		this.context = context;
	}

	public void setList(ArrayList<Comment> list) {
		this.list = list;
	}

	public void update(ArrayList<Comment> list) {
		setList(list);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.comment_list_item,
					null);
			viewHolder = new ViewHolder();
			viewHolder.iv_photo = (ImageView) convertView
					.findViewById(R.id.iv_photo);
			viewHolder.tv_nickname = (TextView) convertView
					.findViewById(R.id.tv_nickname);
			viewHolder.tv_content = (EmoticonsTextViewnew) convertView
					.findViewById(R.id.tv_content);
			viewHolder.tv_time = (TextView) convertView
					.findViewById(R.id.tv_time);
			viewHolder.ll_mjk = (LinearLayout) convertView
					.findViewById(R.id.ll_mjk);
			viewHolder.tv_mjk = (TextView) convertView
					.findViewById(R.id.tv_mjk);
			viewHolder.iv_mjk = (ImageView) convertView
					.findViewById(R.id.iv_mjk);
			viewHolder.mCommentList = (NoScrollListView) convertView
					.findViewById(R.id.list_comment_2);
			viewHolder.mBig = (ImageView) convertView
					.findViewById(R.id.imageView1);
			viewHolder.addComment = (ImageView) convertView
					.findViewById(R.id.item_iv_comment);
			viewHolder.addSupport = (ImageView) convertView
					.findViewById(R.id.item_iv_support);
			viewHolder.commentNum = (TextView) convertView
					.findViewById(R.id.item_tv_comment);
			viewHolder.supportNum = (TextView) convertView
					.findViewById(R.id.item_tv_support);
			viewHolder.time = (TextView) convertView
					.findViewById(R.id.tv_talktopic);

			// 评论时间
			viewHolder.tv_talktopic = (TextView) convertView
					.findViewById(R.id.tv_talktopic);
			// 点评星级
			viewHolder.one = (ImageView) convertView
					.findViewById(R.id.imageView2);
			viewHolder.two = (ImageView) convertView
					.findViewById(R.id.imageView3);
			viewHolder.there = (ImageView) convertView
					.findViewById(R.id.imageView4);
			viewHolder.four = (ImageView) convertView
					.findViewById(R.id.imageView5);
			viewHolder.five = (ImageView) convertView
					.findViewById(R.id.imageView6);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final Comment comment = list.get(position);

		final int index = position;
		if (comment != null && comment.getUser() != null) {
			AppContext.imageLoader.displayImage(comment.getUser().getAvatar(),
					viewHolder.iv_photo, AppContext.gallery);
			viewHolder.tv_nickname
					.setText(comment.getUser().getNickname() == null
							|| "".equals(comment.getUser().getNickname()) ? "游客"
							: comment.getUser().getNickname());
			viewHolder.tv_content.setText(comment.getContent().replaceAll(
					"&nbsp", " "));
			// 楼层？
			if (comment.isGoods()) {
				// 当是商品的评论时，不显示楼层，显示星级、
				viewHolder.tv_time.setVisibility(View.VISIBLE);
				viewHolder.tv_time.setText("评价：");
				int star = Integer.parseInt(comment.getStar());
				if (star == 1) {
					// 一星
					viewHolder.one.setVisibility(View.VISIBLE);
					viewHolder.two.setVisibility(View.VISIBLE);
					viewHolder.there.setVisibility(View.VISIBLE);
					viewHolder.four.setVisibility(View.VISIBLE);
					viewHolder.five.setVisibility(View.VISIBLE);
					viewHolder.one.setImageResource(R.drawable.prisent_hava);
					viewHolder.two.setImageResource(R.drawable.prisent_no);
					viewHolder.there.setImageResource(R.drawable.prisent_no);
					viewHolder.four.setImageResource(R.drawable.prisent_no);
					viewHolder.five.setImageResource(R.drawable.prisent_no);
				} else if (star == 2) {
					// 二星
					viewHolder.one.setVisibility(View.VISIBLE);
					viewHolder.two.setVisibility(View.VISIBLE);
					viewHolder.there.setVisibility(View.VISIBLE);
					viewHolder.four.setVisibility(View.VISIBLE);
					viewHolder.five.setVisibility(View.VISIBLE);
					viewHolder.one.setImageResource(R.drawable.prisent_hava);
					viewHolder.two.setImageResource(R.drawable.prisent_hava);
					viewHolder.there.setImageResource(R.drawable.prisent_no);
					viewHolder.four.setImageResource(R.drawable.prisent_no);
					viewHolder.five.setImageResource(R.drawable.prisent_no);
				} else if (star == 3) {
					// 三星
					viewHolder.one.setVisibility(View.VISIBLE);
					viewHolder.two.setVisibility(View.VISIBLE);
					viewHolder.there.setVisibility(View.VISIBLE);
					viewHolder.four.setVisibility(View.VISIBLE);
					viewHolder.five.setVisibility(View.VISIBLE);
					viewHolder.one.setImageResource(R.drawable.prisent_hava);
					viewHolder.two.setImageResource(R.drawable.prisent_hava);
					viewHolder.there.setImageResource(R.drawable.prisent_hava);
					viewHolder.four.setImageResource(R.drawable.prisent_no);
					viewHolder.five.setImageResource(R.drawable.prisent_no);
				} else if (star == 4) {
					// 四星
					viewHolder.one.setVisibility(View.VISIBLE);
					viewHolder.two.setVisibility(View.VISIBLE);
					viewHolder.there.setVisibility(View.VISIBLE);
					viewHolder.four.setVisibility(View.VISIBLE);
					viewHolder.five.setVisibility(View.VISIBLE);
					viewHolder.one.setImageResource(R.drawable.prisent_hava);
					viewHolder.two.setImageResource(R.drawable.prisent_hava);
					viewHolder.there.setImageResource(R.drawable.prisent_hava);
					viewHolder.four.setImageResource(R.drawable.prisent_hava);
					viewHolder.five.setImageResource(R.drawable.prisent_no);
				} else if (star == 5) {
					// 五星
					viewHolder.one.setVisibility(View.VISIBLE);
					viewHolder.two.setVisibility(View.VISIBLE);
					viewHolder.there.setVisibility(View.VISIBLE);
					viewHolder.four.setVisibility(View.VISIBLE);
					viewHolder.five.setVisibility(View.VISIBLE);
					viewHolder.one.setImageResource(R.drawable.prisent_hava);
					viewHolder.two.setImageResource(R.drawable.prisent_hava);
					viewHolder.there.setImageResource(R.drawable.prisent_hava);
					viewHolder.four.setImageResource(R.drawable.prisent_hava);
					viewHolder.five.setImageResource(R.drawable.prisent_hava);
				}
			} else {
				viewHolder.tv_time.setText((list.size() - position) + "楼");
				viewHolder.one.setVisibility(View.GONE);
				viewHolder.two.setVisibility(View.GONE);
				viewHolder.there.setVisibility(View.GONE);
				viewHolder.four.setVisibility(View.GONE);
				viewHolder.five.setVisibility(View.GONE);
			}

			// 评论时间
			viewHolder.time.setText(comment.getCreate_time() + "");

			viewHolder.tv_talktopic.setText(comment.getCreate_time());

			viewHolder.iv_photo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
//					Intent intent = new Intent(context,
//							UserCenterActivity.class);
//					intent.putExtra("uid", comment.getUser().getUid());
//					context.startActivity(intent);
				}
			});
			// 插入的美酒刊/话题/美酒推荐布局
			int ctype = comment.getType();
			if (-1 == ctype) {
				// 用户上传图片+文本回复
				viewHolder.ll_mjk.setVisibility(View.GONE);
				viewHolder.mBig.setVisibility(View.VISIBLE);
				viewHolder.mBig
						.setMaxWidth(AppContext.metrics.widthPixels * 3 / 4);
				viewHolder.mBig.setMaxHeight(AppContext.metrics.widthPixels); 
				//String URL=compressImage(comment.getImgUrl());
				AppContext.imageLoader.displayImage(comment.getImgUrl(),
						viewHolder.mBig, AppContext.gallery);
//				viewHolder.mBig.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						AppContext.startPhotoGalleryActivity(context,
//								comment.getImgUrl());
//					}
//				});
			} else if (0 == ctype) {
				// 纯文本
				viewHolder.mBig.setVisibility(View.GONE);
				viewHolder.ll_mjk.setVisibility(View.GONE);

			} else if (1 == ctype) {
				// 美酒刊
				viewHolder.mBig.setVisibility(View.GONE);
				viewHolder.ll_mjk.setVisibility(View.VISIBLE);
				viewHolder.tv_mjk.setText(comment.getTitle());
				AppContext.imageLoader.displayImage(comment.getImgUrl(),
						viewHolder.iv_mjk, AppContext.gallery);
//				viewHolder.ll_mjk.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						Intent intent = new Intent(context,
//								SnsDetailActivity.class);
//						intent.putExtra("info", comment.getAid());
//						context.startActivity(intent);
//					}
//				});
			} else if (2 == ctype) {
				// 话题

			} else if (3 == ctype) {
				// 美酒推荐
				viewHolder.mBig.setVisibility(View.GONE);
			}
			// 设置点赞数
			viewHolder.supportNum.setText(comment.getSupport() + "");
			// 设置是否已经点赞
			if (comment.isSupport()) {
				viewHolder.addSupport.setImageResource(R.drawable.ic_support_p);
			} else {
				viewHolder.addSupport.setImageResource(R.drawable.ic_support_n);
			}
			// 二级回复
			ArrayList<CommentList> comment2list = comment.getmLists();
			if (!comment2list.isEmpty()) {
				// 评论的评论数
				viewHolder.commentNum.setText(comment2list.size() + "");
				//
				viewHolder.mCommentList.setVisibility(View.VISIBLE);
				CommentList2Adapter adapter = new CommentList2Adapter(context,
						comment2list);
				viewHolder.mCommentList.setAdapter(adapter);
				/*
				 * viewHolder.mCommentList .setOnItemClickListener(new
				 * OnItemClickListener() {
				 * 
				 * @Override public void onItemClick(AdapterView<?> parent, View
				 * view, int position, long id) { if (null !=
				 * childItemClickListener) { if
				 * (!UserInfoUtil.getLogin(context)) {
				 * UIHelper.startLoginActivity(context, false); return; }
				 * childItemClickListener .onClick( index, (CommentList) parent
				 * .getItemAtPosition(position)); } } });
				 */
				adapter.setCommentOnclickListener(new CommentOnclick() {

					@Override
					public void setCommentOnclick(CommentList mCommentList) {
						childItemClickListener.onClick(index, mCommentList);

					}

				});
			} else {
				viewHolder.mCommentList.setVisibility(View.GONE);
			}

		}
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != ItemClickListener) {
					if (!UserInfoUtil.getLogin(context)) {
//						UIHelper.startLoginActivity(context, false);
//						return;
					}
					//
					ItemClickListener.onClick(index, comment);
				}
			}
		});
		// 添加回复
		viewHolder.addComment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != ItemClickListener) {
					if (comment.getUser().getUid()
							.equals(UserInfoUtil.getUID(context))) {
//						LToastUtil.show(context, "不能回复自己哦");
					} else {
//						if (!UserInfoUtil.getLogin(context)) {
//							UIHelper.startLoginActivity(context, false);
//							return;
//						}
						ItemClickListener.onClick(index, comment);
					}
				}
			}
		});
		// 添加点赞
		viewHolder.addSupport.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != supportListener) {
//					if (!comment.isSupport()) {
//						if (!UserInfoUtil.getLogin(context)) {
//							AppContext.startLoginActivity(context, false);
//							return;
//						}
//						AppContext.addAnimation(v);
//						supportListener.addSupport(index);
//					} else {
//						if (!UserInfoUtil.getLogin(context)) {
//							AppContext.startLoginActivity(context, false);
//							return;
//						}
//						cancleSupportListener.cancleSupport(index);
//					}
				}
			}
		});
		return convertView;
	}

	static class ViewHolder {
		ImageView iv_photo;
		TextView tv_nickname;
		EmoticonsTextViewnew tv_content;
		TextView tv_time, time;
		LinearLayout ll_mjk;
		ImageView iv_mjk;
		TextView tv_mjk;
		// 二层回复列表
		NoScrollListView mCommentList;
		// 图片+文字类型的图片控件
		ImageView mBig;

		// 对评论点赞
		ImageView addSupport;
		// 对评论评论
		ImageView addComment;
		// 评论的评论数
		TextView supportNum;
		// 评论的点赞数
		TextView commentNum;
		// 评论时间
		TextView tv_talktopic;
		// 星级
		ImageView one, two, there, four, five;
		TextView tv_star;// 星级评价
	}

	public CommentItemChildClickListener childItemClickListener;

	public void setChildItemChildClickListener(
			CommentItemChildClickListener childItemClickListener) {
		this.childItemClickListener = childItemClickListener;
	}

	public interface CommentItemChildClickListener {
		public void onClick(int position, CommentList comment);
	}

	public interface CommentItemClickListener {
		public void onClick(int position, Comment comment);
	}

	public CommentItemClickListener ItemClickListener;

	public void setCommentItemClickListener(
			CommentItemClickListener ItemClickListener) {
		this.ItemClickListener = ItemClickListener;
	}

	public interface SupportListener {
		public void addSupport(int position);
	}

	public SupportListener supportListener;

	public void setSupportListener(SupportListener supportListener) {
		this.supportListener = supportListener;
	}

	// 取消点赞的接口回调
	public interface CancleSupportListener {
		public void cancleSupport(int position);
	}

	public CancleSupportListener cancleSupportListener;

	public void setCancleSupport(CancleSupportListener cancleSupportListener) {
		this.cancleSupportListener = cancleSupportListener;
	}

	// 隐藏视图的开关
	public interface HintFeelDrinkListener {
		public void hintfeelDrink(int type);
	}

	public HintFeelDrinkListener drinkListener;

	public void setHintFeelDrinkListener(HintFeelDrinkListener drinkListener) {
		this.drinkListener = drinkListener;
	}

	/*private String compressImage(String url) {
		try {
			Bitmap bitmap = null;
			if (null != url) {
				
				bitmap = returnBitmap(url);// url转化成bitmap
				ByteArrayOutputStream baos = new ByteArrayOutputStream(); // 初始化字节数组
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				if (baos.toByteArray().length / 1024 > 20) {
					// 得到比例
					int i = (baos.toByteArray().length / 1024) / 20;
					int n=((i*10-i)/(i*10))*100;
					baos.reset();// 重置baos即清空baos
					bitmap.compress(Bitmap.CompressFormat.JPEG, n, baos);
					byte[] bs=baos.toByteArray();
					return Base64.encodeToString(bs, Base64.DEFAULT);
				}
			}
		} catch (Exception e) {
			
		}
		return null;
	}
	*//**
	 * 根据图片的url路径获得Bitmap对象
	 * @param url
	 * @return
	 *//*
	private Bitmap returnBitmap(final String url) {
        new Thread(){
        	public void run() {
        		Message message=new Message();
        		try  
        		{  
        			Bitmap bitmap = null;  
        			InputStream in = null;  
        			BufferedOutputStream out = null;  
        			URL url2=new URL(url);
        			InputStream stream=url2.openStream();
        			new Thread(){}.start();
        			in = new BufferedInputStream(stream, 1024);  
        			ByteArrayOutputStream dataStream = new ByteArrayOutputStream();  
        			out = new BufferedOutputStream(dataStream, 1024);  
        			out.flush();  
        			byte[] data = dataStream.toByteArray();  
        			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);  
        			data = null; 
        			message.what=0;
        			message.obj=bitmap;
        			
        		}  
        		catch (IOException e)  
        		{  
        			e.printStackTrace();  
        		}
        	};
        }.start();
	 return null;
	}*/
	
}
