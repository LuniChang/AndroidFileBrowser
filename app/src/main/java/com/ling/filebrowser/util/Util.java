package com.ling.filebrowser.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

public class Util {


	public static String getExceptionStackInfo(Exception e) {
		StringWriter out = new StringWriter();
		e.printStackTrace(new PrintWriter(out));
		return out.toString();
	}
	public static String getSDcardPath() {
		Environment.getExternalStorageState();
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); 
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
		} else {
			return "/mnt/sdcard";
		}

		return sdDir.toString();
	}
	
	
	public static boolean checkFileType(File file,String type) {
	
		String filePath=file.getPath();
		
		String fileType=filePath.substring(filePath.lastIndexOf("/"),filePath.length());
		
		return fileType.equals(type);
		
	}
	
	public static String getFileType(File file) {
		
		String filePath=file.getPath();
		
		
		return getFileType(filePath);
		
	}
	public static String getFileType(String filePath) {
		
		String fileType=filePath.substring(filePath.lastIndexOf(".")+1,filePath.length());
		
		return fileType;
		
	}
	/**
	 * 压缩bitmap
	 */
	public static final Bitmap getDecodeBitmap(String srcPath,int maxBitmapWidth,int maxBitmaHeight) {
		System.gc();// 内存回收
		// 预处理
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(srcPath, opt);
		int w = opt.outWidth;
		int h = opt.outHeight;
		// int wh = w * h;
		int decodesize = 1;
		// if (wh > 204800) {
		// decodesize = wh / 204800;
		// }

		if (w > h && w > maxBitmapWidth) {
			decodesize = w / maxBitmapWidth;
		} else if (w < h && h > maxBitmaHeight) {
			decodesize = h / maxBitmaHeight;
		}
		opt.inSampleSize = decodesize;

		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inJustDecodeBounds = false;
		Bitmap srcbitmap = BitmapFactory.decodeFile(srcPath, opt);
		return srcbitmap;
	}

	/**
	 * 获取bitmap大小
	 * 
	 * @param bitmap
	 * @return
	 */
	@SuppressLint("NewApi")
	public static int getBitmapSize(Bitmap bitmap) {
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){ //API 19
		// return bitmap.getAllocationByteCount();
		// }
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {// API
																			// 12
			return bitmap.getByteCount();
		}
		return bitmap.getRowBytes() * bitmap.getHeight(); // earlier version
	}
	
	/**
	 * 压缩图片到 指定大小
	 * @param image
	 * @param size
	 * @return
	 */
	public static Bitmap compressImageBySize(Bitmap image,int size) {  
		  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
        int options = 100;  
        while ( baos.toByteArray().length / 1024>size) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
            baos.reset();//重置baos即清空baos  
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
            options -= 10;//每次都减少10  
        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
        return bitmap;  
    } 
	
	/**
	 * 压缩图片并生成文件
	 * @param path 路径
	 * @return 路径
	 */
	public static String compressImageToFile(String path) {  
		return compressImageToFile(path,path.substring(0, path.lastIndexOf("."))+".jpg");
				
    } 
	
	/**
	 * 压缩图片并生成文件
	 * @param path 路径
	 * @return 路径
	 */
	public static String compressImageToFile(String inPath,String outPath) {  
		  
		// 预处理
				BitmapFactory.Options opt = new BitmapFactory.Options();
				opt.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(inPath, opt);
				opt.inPreferredConfig = Bitmap.Config.RGB_565;
				opt.inJustDecodeBounds = false;
				Bitmap srcbitmap = BitmapFactory.decodeFile(inPath, opt);
				FileOutputStream out;
//				path=path.substring(0, path.lastIndexOf("."))+".jpg";
				try {
					out = new FileOutputStream(outPath);
					srcbitmap.compress(Bitmap.CompressFormat.JPEG, 30, out);
					out.flush();
					out.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					
				}
				return outPath;
				
				
    }





	/**
	 * 打开文件
	 * @param file
	 */
	public static  void openFile(File file, Context context) throws  Exception{

			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(Intent.ACTION_VIEW);
			String type = getMIMEType(file);
			intent.setDataAndType(Uri.fromFile(file), type);
			context.startActivity(intent);

	}

	public static String getMIMEType(File file) {

		String type="*/*";
		String fileName = file.getName();
		int dotIndex = fileName.lastIndexOf(".");
		if(dotIndex < 0){
			return type;
		}
		String end=fileName.substring(dotIndex,fileName.length()).toLowerCase();
		if(end=="")return type;
		for(int i=0;i<MIME_MapTable.length;i++){
			if(end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}

	public static final String[][] MIME_MapTable={
//{后缀名，MIME类型}
			{".3gp", "video/3gpp"},
			{".apk", "application/vnd.android.package-archive"},
			{".asf", "video/x-ms-asf"},
			{".avi", "video/x-msvideo"},
			{".bin", "application/octet-stream"},
			{".bmp", "image/bmp"},
			{".c", "text/plain"},
			{".class", "application/octet-stream"},
			{".conf", "text/plain"},
			{".cpp", "text/plain"},
			{".doc", "application/msword"},
			{".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
			{".xls", "application/vnd.ms-excel"},
			{".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
			{".exe", "application/octet-stream"},
			{".gif", "image/gif"},
			{".gtar", "application/x-gtar"},
			{".gz", "application/x-gzip"},
			{".h", "text/plain"},
			{".htm", "text/html"},
			{".html", "text/html"},
			{".jar", "application/java-archive"},
			{".java", "text/plain"},
			{".jpeg", "image/jpeg"},
			{".jpg", "image/jpeg"},
			{".js", "application/x-JavaScript"},
			{".log", "text/plain"},
			{".m3u", "audio/x-mpegurl"},
			{".m4a", "audio/mp4a-latm"},
			{".m4b", "audio/mp4a-latm"},
			{".m4p", "audio/mp4a-latm"},
			{".m4u", "video/vnd.mpegurl"},
			{".m4v", "video/x-m4v"},
			{".mov", "video/quicktime"},
			{".mp2", "audio/x-mpeg"},
			{".mp3", "audio/x-mpeg"},
			{".mp4", "video/mp4"},
			{".mpc", "application/vnd.mpohun.certificate"},
			{".mpe", "video/mpeg"},
			{".mpeg", "video/mpeg"},
			{".mpg", "video/mpeg"},
			{".mpg4", "video/mp4"},
			{".mpga", "audio/mpeg"},
			{".msg", "application/vnd.ms-outlook"},
			{".ogg", "audio/ogg"},
			{".pdf", "application/pdf"},
			{".png", "image/png"},
			{".pps", "application/vnd.ms-powerpoint"},
			{".ppt", "application/vnd.ms-powerpoint"},
			{".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
			{".prop", "text/plain"},
			{".rc", "text/plain"},
			{".rmvb", "audio/x-pn-realaudio"},
			{".rtf", "application/rtf"},
			{".sh", "text/plain"},
			{".tar", "application/x-tar"},
			{".tgz", "application/x-compressed"},
			{".txt", "text/plain"},
			{".wav", "audio/x-wav"},
			{".wma", "audio/x-ms-wma"},
			{".wmv", "audio/x-ms-wmv"},
			{".wps", "application/vnd.ms-works"},
			{".xml", "text/plain"},
			{".z", "application/x-compress"},
			{".zip", "application/x-zip-compressed"},
			{"", "*/*"}
	};

}
