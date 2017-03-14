package com.ling.filebrowser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ling.filebrowser.config.FileConfig;
import com.ling.filebrowser.model.FileData;
import com.ling.filebrowser.util.Util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Administrator on 2017/3/13.
 */
public class AbstractFileActivity extends Activity {
    protected Stack<FileData> historyFileStack=new Stack<FileData>();
    protected FileFilter fileFilter;
    protected String fileFilteName;
    private FileData rootFile = new FileData(Util.getSDcardPath());

    protected void initFileFiler(Bundle savedInstanceState) {
        if(savedInstanceState==null){
            return;
        }
        fileFilteName=savedInstanceState.getString(FileConfig.KEY_FILE_FILTER_NAME,null);
        if(fileFilteName!=null){
            fileFilter=new FileFilter() {
                @Override
                public boolean accept(File pathname) {

                    if(pathname.isDirectory())
                        return true;


                        String[] filerNames=fileFilteName.split("|");

                    if(filerNames!=null){
                        String name = pathname.getName();
                        for (String filerName:filerNames){
                            if("*".equals(filerName)){
                                return true;
                            }
                            if(!name.endsWith(filerName))
                                return false;
                        }
                    }
                    return true;
                }
            };
        }
    }

    protected void initRootPath(Bundle savedInstanceState) {
        String rootFilePath = null;
        if(savedInstanceState==null
                ||(rootFilePath = savedInstanceState.getString(FileConfig.KEY_ROOT_PATH))==null){
            rootFile=new FileData(Util.getSDcardPath());
            rootFile=rootFile.getParentFile();
            pushFile(rootFile);
            return;
        }
        rootFile=new FileData(rootFilePath);
        pushFile(rootFile);
    }


    protected void pushFile(FileData file){
        historyFileStack.push(file);
    }

    protected FileData popFile(){
        FileData file=new FileData(rootFile.getFileContent());
        if(!historyFileStack.isEmpty()){
            file=historyFileStack.pop().getParentFile();
        }
        return file;

    }


    protected FileData getRootFile(){
        if(rootFile==null){
            rootFile=new FileData(Util.getSDcardPath());
            rootFile=rootFile.getParentFile();
        }
        return rootFile;
    }

    protected void callbackHadSeletedFiles(List<FileData> srcList){
        Intent intent=new Intent();

        intent.putExtra(FileConfig.KEY_SELECTED_FILES,getSelectedPathArray(srcList));

        setResult(RESULT_OK,intent);
        finish();
    }

    private String[] getSelectedPathArray(List<FileData> srcList){
        if(srcList==null||srcList.size()==0){
            return null;
        }



        List<String> hadSelectList=new ArrayList<String>();
        for (FileData item:srcList){
            if(item.isSelected){
                hadSelectList.add(item.getFileContent().getPath());
            }
        }

        if(hadSelectList.size()==0){
            return null;
        }

        String[] pathArray=new String[hadSelectList.size()];

        for(int i=0;i<hadSelectList.size();++i){
            pathArray[i]=hadSelectList.get(i);
        }

        return pathArray;
    }

}
