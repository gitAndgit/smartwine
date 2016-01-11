package com.sicao.smartwine.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.sicao.smartwine.AppContext;
import com.sicao.smartwine.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * @类名:CropImage
 * @功能描述:图片获得和裁剪
 * @作者:XuanKe'Huang
 * @时间:2014-12-16 上午10:08:03
 * @Copyright 2014
 */
public class CropImage extends Activity {
	public static final String have_image = "result";
	public static final String image_path = "image_path";

	public static final String aspectX = "aspectX";
	public static final String aspectY = "aspectY";
	public static final String outputX = "outputX";
	public static final String outputY = "outputY";

	private static final int NONE = 0;
	private static final int PHOTO_GRAPH = 1;// 拍照
	private static final int PHOTO_ZOOM = 2; // 缩放
	private static final int PHOTO_RESOULT = 3;// 结果
	private static final String IMAGE_UNSPECIFIED = "image/*";

	private int aspX = 0;
	private int aspY = 0;
	private int outX = 100;
	private int outY = 100;

	private String photoPath = Environment.getExternalStorageDirectory()
			.getPath() + "/demo/";
	private String imagePath = Environment.getExternalStorageDirectory()
			.getPath() + "/demo/image/";
	private String tempimage = null;
	private String imageurl = null;
	private boolean ifTailorFlag = true;// 是否启动裁剪,默认启动
	private String type = "";// 是否再跳转回去

	TextView tv_album;
	TextView tv_camera;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_select_pic);
		tv_album = (TextView) findViewById(R.id.tv_album);
		tv_camera = (TextView) findViewById(R.id.tv_camera);
		try {
			// 创建保存路径
			isFolderExists(photoPath);
			if (isFolderExists(imagePath)) {
				try {
					new File(imagePath, ".nomedia").createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Intent i = getIntent();
			Bundle bundle = i.getExtras();
			if (bundle != null) {
				ifTailorFlag = bundle.getBoolean("ifTailorFlag");
				type = bundle.getString("type");// 是否要跳转回去
			}

			aspX = i.getIntExtra(aspectX, 0);
			if (aspX == 0) {
				aspX = Integer.valueOf(i.getStringExtra(aspectX));
			}
			aspY = i.getIntExtra(aspectY, 0);
			if (aspY == 0) {
				aspY = Integer.valueOf(i.getStringExtra(aspectY));
			}
			outX = i.getIntExtra(outputX, 0);
			if (outX == 0) {
				outX = Integer.valueOf(i.getStringExtra(outputX));
			}
			outY = i.getIntExtra(outputY, 0);
			if (outY == 0) {
				outY = Integer.valueOf(i.getStringExtra(outputY));
			}

			// 判断是否传入值。未传入则直接返回
			// if (aspX != 0 && aspY != 0 && outX != 0 && outY != 0) {
			showPicSelectDialog();
			// } else {
			// result(null);
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 打开选择图片对话框
	 */
	private void showPicSelectDialog() {
		tv_camera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 打开相机
				tempimage = getRandomString(7) + ".jpg";
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra("return-data", false);
				intent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(new File(photoPath, tempimage)));
				startActivityForResult(intent, PHOTO_GRAPH);
			}
		});
		tv_album.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 打开图库
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.putExtra("return-data", false);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						IMAGE_UNSPECIFIED);
				startActivityForResult(intent, PHOTO_ZOOM);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if (resultCode == Activity.RESULT_CANCELED) {
				try {
					result(null);
					CropImage.this.finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (resultCode == Activity.RESULT_OK) {
				String sdStatus = Environment.getExternalStorageState();
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
					Log.i("TestFile",
							"SD card is not avaiable/writable right now");
					return;
				} else if (resultCode == NONE) {
					try {
						result(null);
						CropImage.this.finish();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (requestCode == PHOTO_GRAPH) {// 拍照
					File picture = new File(photoPath + tempimage);// 设置文件保存路径
					try {
						// 压缩之前进行图片旋转处理
						float f = (float) readPictureDegree(picture
								.getAbsolutePath());
						if (f != 0f) {
							// 说明需要旋转图片
							String tagerPath = null;
							try {
								tagerPath = getImage1(
										picture.getAbsolutePath(), f);
							} catch (IOException e) {
								e.printStackTrace();
							}
							if (tagerPath != null) {
								File mfile = new File(tagerPath);
								startPhotoZoom(Uri.fromFile(mfile));
								if (picture.exists()) {
									picture.delete();
								}
							} else {
								startPhotoZoom(Uri.fromFile(picture));
							}
						} else {
							startPhotoZoom(Uri.fromFile(picture));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (requestCode == PHOTO_ZOOM) {// 读取相册缩放图片
					//startPhotoZoom(data.getData());
					Uri uri = data.getData();
					if (uri != null && uri.toString().contains("content://")) {
						ContentResolver cr = this.getContentResolver();
						Cursor cursor = cr.query(uri, null, null, null, null);
						if (cursor != null) {
							cursor.moveToFirst();
							/*startPhotoZoom(Uri.fromFile(new File(cursor
									.getString(1))));*/
							try {
								// 压缩之前进行图片旋转处理
								File file=new File(cursor.getString(1));
								float f = (float) readPictureDegree(file.getAbsolutePath());
								if (f != 0f) {
									// 说明需要旋转图片
									String tagerPath = null;
									try {
										tagerPath = getImage1(
												file.getAbsolutePath(), f);
									} catch (IOException e) {
										e.printStackTrace();
									}
									if (tagerPath != null) {
										File mfile = new File(tagerPath);
										startPhotoZoom(Uri.fromFile(mfile));
									} else {
										startPhotoZoom(Uri.fromFile(file));
									}
								} else {
									startPhotoZoom(Uri.fromFile(new File(cursor
											.getString(1))));
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					} else {
						try {
							//startPhotoZoom(data.getData());
							// 压缩之前进行图片旋转处理
							String path=data.getData().toString();
							File file=new File(path);
							float f = (float) readPictureDegree(file.getAbsolutePath());
							if (f != 0f) {
								// 说明需要旋转图片
								String tagerPath = null;
								try {
									tagerPath = getImage1(
											file.getAbsolutePath(), f);
								} catch (IOException e) {
									e.printStackTrace();
								}
								if (tagerPath != null) {
									File mfile = new File(tagerPath);
									startPhotoZoom(Uri.fromFile(mfile));
									
								} else {
									startPhotoZoom(Uri.fromFile(file));
								}
							} else {
								startPhotoZoom(data.getData());
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else if (requestCode == PHOTO_RESOULT) { // 处理结果
					result(imageurl);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startPhotoZoom(Uri uri) {
		imageurl = imagePath + getRandomString(7) + ".jpg";

		try {// 压缩图片
			File file = new File(uri.getPath());
			if (file.exists()) {// 图片大于50kb就压缩
				if (file.length() > 51200) {
					Bitmap bitmap = optimizeBitmap(uri.getPath(),
							AppContext.metrics.widthPixels,
							AppContext.metrics.heightPixels);
					OutputStream os = new FileOutputStream(uri.getPath());
					bitmap.compress(CompressFormat.JPEG, 70, os);
					os.flush();
					os.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (ifTailorFlag) {// 如果有启动裁剪
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
			intent.putExtra("crop", "false");
			intent.putExtra("aspectX", aspX);
			intent.putExtra("aspectY", aspY);
			intent.putExtra("outputX", outX);
			intent.putExtra("outputY", outY);
			intent.putExtra("scale", true);
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(imageurl)));
			intent.putExtra("return-data", false);
			intent.putExtra("outputFormat",
					CompressFormat.JPEG.toString());
			intent.putExtra("noFaceDetection", true); // no face detection
			startActivityForResult(intent, PHOTO_RESOULT);
		} else {// 如果没有启动裁剪:没有启动裁剪直接压缩图片并返回
			result(uri.getPath());
		}
	}

	public static Bitmap optimizeBitmap(String pathName, int maxWidth,
			int maxHeight) {
		Bitmap result = null;
		try {
			// 图片配置对象，该对象可以配置图片加载的像素获取个数
			BitmapFactory.Options options = new BitmapFactory.Options();
			// 表示加载图像的原始宽高
			options.inJustDecodeBounds = true;
			result = BitmapFactory.decodeFile(pathName, options);
			// Math.ceil表示获取与它最近的整数（向上取值 如：4.1->5 4.9->5）
			int widthRatio = (int) Math.ceil(options.outWidth / maxWidth);
			int heightRatio = (int) Math.ceil(options.outHeight / maxHeight);
			// 设置最终加载的像素比例，表示最终显示的像素个数为总个数的
			if (widthRatio > 1 || heightRatio > 1) {
				if (widthRatio > heightRatio) {
					options.inSampleSize = widthRatio;
				} else {
					options.inSampleSize = heightRatio;
				}
			}
			// 解码像素的模式，在该模式下可以直接按照option的配置取出像素点
			options.inPreferredConfig = Config.RGB_565;
			options.inJustDecodeBounds = false;
			result = BitmapFactory.decodeFile(pathName, options);

		} catch (Exception e) {
		}
		return result;
	}

	boolean isFolderExists(String strFolder) {
		File file = new File(strFolder);
		if (!file.exists()) {
			if (file.mkdirs()) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	@SuppressLint("NewApi")
	public void result(String image_path) {
		try {
			Intent i = new Intent();
			if (null != image_path && !"".equals(image_path)) {
				i.putExtra(have_image, true);
				i.putExtra(CropImage.image_path, image_path);

			} else {
				i.putExtra(have_image, false);
			}
			this.setResult(RESULT_OK, i);
			finish();
		} catch (Exception e) {
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			result("");
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
	}

	public static String getRandomString(int length) { // length表示生成字符串的长度
		String base = "abcdefghijklmnopqrstuvwxyz0123456789"; // 生成字符串从此序列中取
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	// 相机照相图片旋转角度的问题
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	// 返回bitmap
	public String getImage1(String path, float angle) throws IOException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		int srcWidth = options.outWidth;
		int srcHeight = options.outHeight;
		int[] newWH = new int[2];
		newWH[0] = srcWidth / 2;
		newWH[1] = (newWH[0] * srcHeight) / srcWidth;

		int inSampleSize = 1;
		while (srcWidth / 2 >= newWH[0]) {
			srcWidth /= 2;
			srcHeight /= 2;
			inSampleSize *= 2;
		}

		options.inJustDecodeBounds = false;
		options.inDither = false;
		options.inSampleSize = inSampleSize;
		options.inScaled = false;
		options.inPreferredConfig = Config.ARGB_8888;
		Bitmap sampledSrcBitmap = BitmapFactory.decodeFile(path, options);
		ExifInterface exif = new ExifInterface(path);
		String s = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
		System.out.println("Orientation>>>>>>>>>>>>>>>>>>>>" + s);
		Matrix matrix = new Matrix();
		if (angle != 0f) {
			matrix.preRotate(angle);
		}
		Bitmap pqr = Bitmap.createBitmap(sampledSrcBitmap, 0, 0,
				sampledSrcBitmap.getWidth(), sampledSrcBitmap.getHeight(),
				matrix, true);
		return saveBitmapFile(pqr);
	}

	// 图片bitmap转换成路径
	public String saveBitmapFile(Bitmap bitmap) {
		// 创建文件路径
		String path = Environment.getExternalStorageDirectory().toString()
				+ "/putaoji/pictures";
		File path1 = new File(path);
		if (!path1.exists()) {
			path1.mkdirs();
		}
		File file = new File(path1, AppContext.getRandomString(7) + ".jpg");
		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(file));
			bitmap.compress(CompressFormat.JPEG, 70, bos);
			bos.flush();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (file.exists()) {
			return file.toString();
		}
		return null;
	}
}
