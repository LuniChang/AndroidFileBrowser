package com.ling.filebrowser.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileData implements Parcelable {
	public boolean isSelected=false;
	private File fileContent;


	public File getFileContent() {
		return fileContent;
	}

	public void setFileContent(File fileContent) {
		this.fileContent = fileContent;
	}
	
	
	
	public static List<FileData> fileArrayToFileDataList(File files[]){
		
		
		List<FileData> result=new ArrayList<FileData>();
		if(files!=null)
		for(int i=0;i<files.length;i++){
			FileData item=new FileData();
			item.setFileContent(files[i]);
			result.add(item);
		}
		
		return result;
		
	}


	public FileData()
	{

	}

	private FileData(Parcel in)
	{

		fileContent=(File) in.readSerializable();
		isSelected=in.readByte()==1?true:false;
	}

	public int describeContents()
	{
		return 0;
	}

	public void writeToParcel(Parcel out, int flags)
	{
		out.writeSerializable(fileContent);
		out.writeByte((byte) (isSelected?1:0));
	}

	public static final Parcelable.Creator<FileData> CREATOR = new Parcelable.Creator<FileData>()
	{
		public FileData createFromParcel(Parcel in)
		{
			return new FileData(in);
		}

		public FileData[] newArray(int size)
		{
			return new FileData[size];
		}
	};


	public FileData getParentFile(){
		FileData fileData=new FileData(this.getFileContent().getParentFile());
		return fileData;
	}

	public FileData(File fileContent) {
		this.fileContent = fileContent;
	}
	public FileData(String path) {
		this.fileContent = new File(path);
	}
}
