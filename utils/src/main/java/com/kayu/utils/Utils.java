/**
 * 
 */
package com.kayu.utils;

import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;

import java.io.File;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;


public class Utils {

	public static String GetExternalStroragePath(Context context) {
		// 得到存储卡路径
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); // 判断sd卡
		// 或可存储空间是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取sd卡或可存储空间的跟目录
			LogUtil.e("main","得到的根目录路径:"+sdDir);
			return sdDir.toString();
		}

		return null;
	}

	private static boolean checkfile(String path){

		File file = new File(path);
//    	return createDirectory(file);
		if (!file.isDirectory()) {
			if(!file.mkdirs())
			{
				return false;
			}else
			{
				try {
					file.setExecutable(true, false);
					file.setWritable(true, false);
					file.setReadable(true, false);
				} catch (NoSuchMethodError e) {
					e.printStackTrace();
				}
				return true;
			}
		}
		else{
			try {
				file.setExecutable(true, false);
				file.setWritable(true, false);
				file.setReadable(true, false);
			} catch (NoSuchMethodError e) {
				e.printStackTrace();
			}
			return true;
		}

	}

	public static String getEnaviBaseStorage(Context context) {
		String map_base_path = "";

		map_base_path = GetExternalStroragePath(context);
		if (map_base_path != null && map_base_path.length() > 2) {
			map_base_path = map_base_path
					+ File.separator
					+ Constants.PATH_ROOT + File.separator;
			if (checkfile(map_base_path)) {
				return map_base_path;
			}
		}

		map_base_path = context.getFilesDir().toString();
		if (map_base_path != null && map_base_path.length() > 2) {
			File file = new File(map_base_path);
			if (file.isDirectory()) {
				return map_base_path;
			}
		}
		return map_base_path;
	}

	public static String ByteToM(long bytes){
		long M = bytes / 1024 / 1024;
		long p = bytes / 1024 - (bytes / 1024 / 1024 * 1024);
		long pd = 0;
		if (p > 950){
			M += 1;
		}else if (p >= 100){
			pd = p / 100;
		}else if(M == 0){
			pd = 1;
		}
		if (pd == 0)
			return M+"M";
		else
			return M + "." + pd+"M";
	}



	public static void setListViewHeightBasedOnChildren(GridView listView) {
		// 获取listview的adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		// 固定列宽，有多少列
		int col = 4;// listView.getNumColumns();
		int totalHeight = 0;
		// i每次加4，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，
		// listAdapter.getCount()小于等于8时计算两次高度相加
		for (int i = 0; i < listAdapter.getCount(); i += col) {
			// 获取listview的每一个item
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			// 获取item的高度和
			totalHeight += listItem.getMeasuredHeight();
		}
		// 获取listview的布局参数
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		// 设置高度
		params.height = totalHeight;
		// 设置margin
		((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10);
		// 设置参数
		listView.setLayoutParams(params);
	}


	public static String readAssert(Context context,  String fileName) {
		String jsonString="";
		String resultString="";
		try {
			InputStream inputStream=context.getResources().getAssets().open(fileName);
			byte[] buffer=new byte[inputStream.available()];
			inputStream.read(buffer);
			resultString=new String(buffer,"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultString;
	}

    /**
     * 比较是否是同一天
     * @param currentTime
     * @param lastTime
     * @return
     */
	public static boolean isSameData(Date currentTime, Date lastTime) {
		try {
			Calendar nowCal = Calendar.getInstance();
			Calendar dataCal = Calendar.getInstance();
			nowCal.setTime(currentTime);
			dataCal.setTime(lastTime);
			return isSameDay(nowCal, dataCal);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isSameDay(Calendar cal1, Calendar cal2) {
		if(cal1 != null && cal2 != null) {
			return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
					&& cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
					&& cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
		} else {
			return false;
		}
	}
}
