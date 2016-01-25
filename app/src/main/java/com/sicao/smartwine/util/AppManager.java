package com.sicao.smartwine.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;
import android.view.View;

/***
 * <p>
 * 应用管理
 * <p>
 * <a> 提供功能:为应用程序的包名,版本�?版本名称,以及安装应用,启动应用,卸载应用和正在运行的应用提供相关信息<br>
 * <ol>
 * <li>获取程序包信�?{@link AppManager#getPackageInfo(Context)}
 * <li>获取版本�?{@link AppManager#getVersionCode(Context)}
 * <li>获取版本名称 {@link AppManager#getVersionName(Context)}
 * <li>获取程序包名 {@link AppManager#getPackageName(Context)}
 * <li>获取设备上安装的程序包{@link AppManager#getInstallPkg(Context)}
 * <li>获取设备上安装的应用列表{@link AppManager#getInstallPkg(boolean, Context)}
 * <li>获取运行的应用列表{@link AppManager#getRunApps(Context)}
 * <li>获取当前的应用包信息{@link AppManager#getFirstRunningTask(Context)}
 * <li>安装应用 {@link AppManager#installPkg(String, Context)}<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;{@link AppManager#installApk(Context, File)}
 * <li>启动应用{@link AppManager#startApp(String, Context)}
 * <li>卸载应用{@link AppManager#uninstallPkg(String, Context)}
 * <li>应用是否安装{@link AppManager#isInstalled(String, Context)}
 * <li>应用是否在运行{@link AppManager#isunning(String, Context)}
 * <li>应用是否获取了某一项权限{@link AppManager#hasPermission(Context, String)}
 * </ol>
 * 
 * @author li'mingqi
 * 
 */
public class AppManager {
	/**
	 * 获取程序包信�?
	 * 
	 * @param context
	 * @return
	 */
	public static PackageInfo getPackageInfo(Context context) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
		return pi;
	}

	/***
	 * 获取内部版本号
	 * 
	 * @param context
	 *            上下文对�?
	 * @return 版本�?
	 */
	public static int getVersionCode(Context context) {
		return getPackageInfo(context).versionCode;
	}

	/***
	 * 获取版本名称
	 * 
	 * @param context
	 *            上下文对�?
	 * @return 版本名称
	 */
	public static String getVersionName(Context context) {
		return getPackageInfo(context).versionName;
	}

	/**
	 * 程序包名中最后一个单�?�?com.unisoft.ptl则返回ptl
	 * 
	 * @param context
	 *            上下文对�?
	 * @return
	 * 
	 */
	public static String getPackageLastName(Context context) {
		String pkgName = getPackageName(context);
		return pkgName.substring(pkgName.lastIndexOf(".") + 1);
	}

	/**
	 * 程序包名
	 * 
	 * @param context
	 *            上下文对�?
	 * @return 程序包名
	 */
	public static String getPackageName(Context context) {
		String pkgName = getPackageInfo(context).packageName;
		return pkgName;
	}

	/**
	 * 查询设备上所有的安装�?
	 * 
	 * @return
	 */
	public static List<PackageInfo> getInstallPkg(Context context) {
		PackageManager pm = context.getPackageManager();
		// 查询�?��已经安装的应用程�?
		List<PackageInfo> packs = pm.getInstalledPackages(0);
		return packs;
	}

	/****
	 * 获取正在运行的应�?
	 * 
	 * @param context
	 * @return
	 */
	public static String getRunningActivityName(Context context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		@SuppressWarnings("deprecation")
		String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity
				.getClassName();
		return runningActivity;
	}

	/**
	 * 获取正在运行的应�?
	 * 
	 * @return
	 */
	public static List<RunningAppProcessInfo> getRunningProcs(
			Context context) {
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程
		List<RunningAppProcessInfo> appProcessList = mActivityManager
				.getRunningAppProcesses();
		return appProcessList;
	}

	/**
	 * 获取正在运行的应�?
	 * 
	 * @return
	 */
	public static Set<String> getRunningPkgs(Context context) {
		Set<String> apppSet = new HashSet<String>();
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 应用程序位于堆栈的顶�?
		@SuppressWarnings("deprecation")
		List<RunningTaskInfo> tasksInfos = mActivityManager.getRunningTasks(20);
		for (RunningTaskInfo tasksInfo : tasksInfos) {
			apppSet.add(tasksInfo.baseActivity.getPackageName());// 获得运行在该进程里的�?��应用程序�?
		}
		/*
		 * ActivityManager mActivityManager = (ActivityManager) context
		 * .getSystemService(Context.ACTIVITY_SERVICE); //
		 * 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程
		 * List<ActivityManager.RunningAppProcessInfo> appProcessList =
		 * mActivityManager .getRunningAppProcesses(); for
		 * (ActivityManager.RunningAppProcessInfo appProcess : appProcessList) {
		 * String[] pkgNameList = appProcess.pkgList; // 获得运行在该进程里的�?��应用程序�?for
		 * (int i = 0; i < pkgNameList.length; i++) {
		 * apppSet.add(pkgNameList[i]); } }
		 */
		return apppSet;
	}

	/**
	 * 获取当前的应用，RunningTaskInfo
	 * 
	 * @return
	 */
	public static RunningTaskInfo getFirstRunningTask(Context context) {
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 应用程序位于堆栈的顶�?
		@SuppressWarnings("deprecation")
		List<RunningTaskInfo> tasksInfo = mActivityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			return tasksInfo.get(0);
		}
		return null;
	}

	/**
	 * 获取安装的应用列�?
	 * 
	 * @param type
	 *            判断是否显示系统应用程序
	 * @return
	 */
	public static ArrayList<Map<String, Object>> getInstallPkg(
			boolean noSysPkg, Context context) {
		ArrayList<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		PackageManager pm = context.getPackageManager();
		// 得到系统安装的所有程序包的PackageInfo对象
		List<PackageInfo> packs = pm.getInstalledPackages(0);
		for (PackageInfo pi : packs) {
			// 显示用户安装的应用程序，而不显示系统程序
			if (noSysPkg
					&& ((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
					|| (pi.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
				continue;
			}
			HashMap<String, Object> map = new HashMap<String, Object>();
			// map.put("icon", pi.applicationInfo.loadIcon(pm));// 图标
			map.put("appName", pi.applicationInfo.loadLabel(pm));// 应用程序名称
			map.put("packageName", pi.applicationInfo.packageName);// 应用程序包名
			// 循环读取并存到HashMap中，再增加到ArrayList上，�?��HashMap就是�?��
			items.add(map);
		}
		return items;
	}

	/**
	 * 获取运行的应用列�?MAP
	 * 
	 * @return
	 */
	public static ArrayList<Map<String, Object>> getRunApps(Context context) {
		PackageManager pm = context.getPackageManager();
		// 保存�?��正在运行的包�?以及它所在的进程信息
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程
		List<RunningAppProcessInfo> appProcessList = mActivityManager
				.getRunningAppProcesses();
		Map<String, RunningAppProcessInfo> pgkProcessAppMap = new HashMap<String, RunningAppProcessInfo>();
		for (RunningAppProcessInfo appProcess : appProcessList) {
			String[] pkgNameList = appProcess.pkgList; // 获得运行在该进程里的�?��应用程序�?
			// 输出�?��应用程序的包�?
			for (int i = 0; i < pkgNameList.length; i++) {
				String pkgName = pkgNameList[i];
				// 加入至map对象�?
				pgkProcessAppMap.put(pkgName, appProcess);
			}
		}
		// 保存�?��正在运行的应用程序信�?
		// 查询�?��已经安装的应用程�?
		List<ApplicationInfo> listAppcations = pm
				.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
		Collections.sort(listAppcations,
				new ApplicationInfo.DisplayNameComparator(pm));// 排序
		ArrayList<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		for (ApplicationInfo app : listAppcations) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			// 如果该包名存�?则构造一个RunningAppInfo对象
			if (pgkProcessAppMap.containsKey(app.packageName)) {
				// 获得该packageName�?pid �?processName
				// int pid = pgkProcessAppMap.get(app.packageName).pid;
				String processName = pgkProcessAppMap.get(app.packageName).processName;
				map.put("appName", processName);// 应用程序名称
				map.put("packageName", app.packageName);// 应用程序包名
				items.add(map);
			}
		}
		return items;
	}

	/***
	 * 卸载应用
	 * 
	 * @param pkName
	 *            应用包名
	 * @param context
	 *            上下文对�?
	 * @return
	 */
	public static boolean uninstallPkg(String pkName, Context context) {
		Uri uri = Uri.parse("package:" + pkName);
		Intent intent = new Intent(Intent.ACTION_DELETE, uri);
		context.startActivity(intent);
		return true;
	}

	/***
	 * 系统安装应用 pkName .apk路径
	 * 
	 * @param pkName
	 *            要安装的apk包路�?
	 * @param context
	 *            上下文对�?
	 * @return
	 */
	public static boolean installPkg(String pkName, Context context) {
		// 通过启动�?��Intent让系统来帮你安装新的APK
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(pkName)),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
		return true;
	}

	/***
	 * 启动应用
	 * 
	 * @param pkName
	 *            要启动的程序包名
	 * @param context
	 *            上下文对�?
	 * @return
	 */
	public static boolean startApp(String pkName, Context context) {
		if (!isunning(pkName, context)) {// 是否在运�?
			PackageManager packageManager = context.getPackageManager();
			Intent intent = new Intent();
			intent = packageManager.getLaunchIntentForPackage(pkName);
			context.startActivity(intent);
			return true;
		} else {
			return false;
		}
	}

	/***
	 * 停止运行应用
	 * 
	 * @param pkgName
	 *            包名
	 * @param context
	 *            上下文对�?
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean stopApp(String pkgName, Context context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		activityManager.restartPackage(pkgName);
		return true;
	}

	/**
	 * �?��是否安装
	 * 
	 * @param pkName
	 *            应用包名
	 * @return
	 */
	public static boolean isInstalled(String pkName, Context context) {
		PackageInfo packageInfo = null;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(pkName, 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
		}
		if (packageInfo == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * �?��是否运行
	 * 
	 * @param pkName
	 *            应用包名
	 * @param context
	 *            上下文对�?
	 * @return
	 */
	public static boolean isunning(String pkName, Context context) {
		boolean bRunning = false;
		// 保存�?��正在运行的包�?以及它所在的进程信息
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程
		List<RunningAppProcessInfo> appProcessList = mActivityManager
				.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcessList) {
			String[] pkgNameList = appProcess.pkgList; // 获得运行在该进程里的�?��应用程序�?
			// 输出�?��应用程序的包�?
			for (int i = 0; i < pkgNameList.length; i++) {
				if (pkName.equalsIgnoreCase(pkgNameList[i])) {
					bRunning = true;
					break;
				}
			}
		}
		return bRunning;
	}

	/**
	 * 从apk文件里面获取应用相关信息：包名等
	 * 
	 * @param apkName
	 * @param context
	 * @return
	 */
	public static String getPkNameByApk(String apkName, Context context) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(apkName,
				PackageManager.GET_ACTIVITIES);
		if (info != null) {
			ApplicationInfo appInfo = info.applicationInfo;
			// String appName = pm.getApplicationLabel(appInfo).toString();
			String packageName = appInfo.packageName; // 得到安装包名�?
			// String version=info.versionName; //得到版本信息

			return packageName;
		}
		return null;
	}

	/**
	 * 获取桌面进程
	 * 
	 * @param context
	 * @return
	 */
	public static String getSysLauncherName(Context context) {
		// 1切换到HOME
		Intent backhome = new Intent("android.intent.action.MAIN");
		backhome.addCategory("android.intent.category.HOME");
		backhome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(backhome);
		// 获取桌面任务
		RunningTaskInfo tasksInfo = getFirstRunningTask(context);
		if (tasksInfo == null)
			return "null";
		String pkgName = tasksInfo.baseActivity.getPackageName();
		return pkgName;
	}

	/***
	 * 获取正在运行的应用的进程名称
	 * 
	 * @param pkgName
	 * @param context
	 * @return
	 */
	public static String getProcessName(String pkgName, Context context) {
		String procName = pkgName;
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程
		List<RunningAppProcessInfo> appProcessList = mActivityManager
				.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcessList) {
			String[] pkgNameList = appProcess.pkgList; // 获得运行在该进程里的�?��应用程序�?
			for (int i = 0; i < pkgNameList.length; i++) {
				if (pkgName.equalsIgnoreCase(pkgNameList[i])) {
					procName = appProcess.processName;
					break;
				}
			}
		}
		if (procName == null)
			procName = pkgName;
		return procName;
	}

	/***
	 * 切换到HOME界面
	 * 
	 * @param ctx
	 */
	public static void backToHome(Context ctx) {
		Intent backhome = new Intent("android.intent.action.MAIN");
		backhome.addCategory("android.intent.category.HOME");
		backhome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(backhome);
	}

	/***
	 * 安装apk
	 * 
	 * @param context
	 *            上下文对�?
	 * @param file
	 *            新版本apk文件
	 */
	public static void installApk(Context context, File file) {
		Intent intent = new Intent();
		// 执行动作
		intent.setAction(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 执行的数据类�?
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/***
	 * 获取签名
	 * 
	 * @param context
	 * @return li'mingqi 2014-8-19
	 */
	public static byte[] getSign(Context context) {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> apps = pm
				.getInstalledPackages(PackageManager.GET_SIGNATURES);
		Iterator<PackageInfo> iter = apps.iterator();

		while (iter.hasNext()) {
			PackageInfo info = iter.next();
			String packageName = info.packageName;
			// 按包�?取签�?
			if (packageName.equals("com.zy.tour")) {
				try {
					String sign = new String(info.signatures[0].toCharsString()
							.getBytes(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				return info.signatures[0].toByteArray();

			}
		}
		return null;
	}

	/***
	 * 获取公钥
	 * 
	 * @param signature
	 * @return li'mingqi 2014-8-19
	 */
	public static String getPublicKey(byte[] signature) {
		try {

			CertificateFactory certFactory = CertificateFactory
					.getInstance("X.509");
			X509Certificate cert = (X509Certificate) certFactory
					.generateCertificate(new ByteArrayInputStream(signature));

			String publickey = cert.getPublicKey().toString();
			publickey = publickey.substring(publickey.indexOf("modulus: ") + 9,
					publickey.indexOf("\n", publickey.indexOf("modulus:")));
			return publickey;
		} catch (CertificateException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressLint("InlinedApi")
	public static void hideBottom(Activity context) {
		context.getWindow().getDecorView()
				.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
	}

	/**
	 * 判断一个程序是否显示在前端;
	 * 
	 * @param packageName 程序包名
	 * @param context 上下文环境
	 * @return true--->在前端,false--->不在前端
	 */
	public static boolean isApplicationShowing(String packageName,
			Context context) {
		boolean result = false;
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
		if (appProcesses != null) {
			for (RunningAppProcessInfo runningAppProcessInfo : appProcesses) {
				if (runningAppProcessInfo.processName.equals(packageName)) {
					int status = runningAppProcessInfo.importance;
					if (status == RunningAppProcessInfo.IMPORTANCE_VISIBLE
							|| status == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
						result = true;
					}
				}
			}
		}
		return result;
	}

	/***
	 * 判断应用是否获取了某一项权限
	 * 
	 * @param context
	 *            上下文对象
	 * @param permission
	 *            权限名
	 * @return 是否已获取该权限
	 */
	public static boolean hasPermission(Context context, String permission) {
		PackageManager manager = context.getPackageManager();
		return (PackageManager.PERMISSION_GRANTED == manager.checkPermission(
				permission, getPackageName(context)));
	}
	/***
	 * 清理内存
	 * @param context
	 */
	public static void clean(Context context){
		ActivityManager activityManger=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> list=activityManger.getRunningAppProcesses();
        if(list!=null)
        for(int i=0;i<list.size();i++)
        {
            RunningAppProcessInfo apinfo=list.get(i);
            Log.i("huahua", "pid="+apinfo.pid+";       processName="+apinfo.processName+";         importance="+apinfo.importance);
            String[] pkgList=apinfo.pkgList;
            
            if(apinfo.importance> RunningAppProcessInfo.IMPORTANCE_SERVICE)
            {
               // Process.killProcess(apinfo.pid);
                for(int j=0;j<pkgList.length;j++)
                {
                    //2.2以上是过时的,请用killBackgroundProcesses代替
//                    activityManger.restartPackage(pkgList[j]);
                	activityManger.killBackgroundProcesses(pkgList[j]);
                } 
            }
	}
        }
}
