package com.ling.filebrowser.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileData {
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
}
