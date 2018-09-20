package com.ling.filebrowser;

import android.app.Activity;
import android.content.Intent;

import com.ling.filebrowser.config.FileConfig;
import com.ling.filebrowser.model.FileData;
import com.ling.filebrowser.util.Util;
import com.xu.backgroundtask.DoTaskOnBackground;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Administrator on 2017/3/13.
 */
public class AbstractFileActivity extends Activity {
    protected Stack<FileData> historyFileStack = new Stack<FileData>();
    protected FileFilter fileFilter;
    protected String fileFilteName;
    private FileData rootFile = new FileData(Util.getSDcardPath());

    protected void initFileFiler() {
        if (getIntent().getExtras() == null) {
            return;
        }
        fileFilteName = getIntent().getExtras().getString(FileConfig.KEY_FILE_FILTER_NAME, FileConfig.FILTER_IMG);
//        fileFilteName=FileConfig.FILTER_IMG;
        if (fileFilteName != null) {
            fileFilter = new FileFilter() {
                @Override
                public boolean accept(File pathname) {

                    if (pathname.isDirectory())
                        return true;


                    String[] filerNames = fileFilteName.split(";");

                    if (filerNames != null) {
                        String name = pathname.getName();
                        for (String filerName : filerNames) {
                            if ("*".equals(filerName)) {
                                return true;
                            }

                            String fileType=name.substring(name.lastIndexOf(".")+1);
                            if(filerName.equals(fileType.toLowerCase())){
                                return true;
                            }
//                            if (!name.endsWith(filerName.toLowerCase()))
//                                return false;
                        }
                    }
                    return false;
                }
            };
        }
    }

    protected void initRootPath() {
        String rootFilePath = null;
        if (getIntent().getExtras() == null
                || (rootFilePath = getIntent().getExtras().getString(FileConfig.KEY_ROOT_PATH)) == null) {
            rootFile = new FileData(Util.getSDcardPath());
            rootFile = rootFile.getParentFile();
            pushFile(rootFile);
            return;
        }
        rootFile = new FileData(rootFilePath);
        pushFile(rootFile);
    }


    protected void pushFile(FileData file) {
        historyFileStack.push(file);
    }

    protected FileData popFile() {
        FileData file = getRootFile();
        if (!historyFileStack.isEmpty()) {
            file = historyFileStack.pop().getParentFile();
        }
        return file;

    }


    protected FileData getRootFile() {
        if (rootFile == null) {
            rootFile = new FileData(Util.getSDcardPath());
//            rootFile = rootFile.getParentFile();
        }
        return rootFile;
    }

    protected void callbackHadSeletedFiles(List<FileData> srcList) {

        boolean isResultAsyn = true;
        if (getIntent() != null && getIntent().getExtras() != null)
            isResultAsyn = getIntent().getExtras().getBoolean(FileConfig.KEY_RESULT_FOR_ASYN, true);

        if (isResultAsyn) {
            DoTaskOnBackground taskOnBackground = new DoTaskOnBackground() {
                @Override
                public void doTask() throws Exception {
                    List<FileData> datas = getBundle().getParcelableArrayList("data");
                    String[] paths = getSelectedPathArray(datas);
                    getBundle().putStringArray("result", paths);
                }

                @Override
                protected void doOnTaskFinish() {

                    Intent intent = new Intent();
                    intent.putExtra(FileConfig.KEY_RESULT_SELECTED_FILES, getBundle().getStringArray("result"));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            };
            taskOnBackground.getBundle().putParcelableArrayList("data", (ArrayList<FileData>) srcList);
            taskOnBackground.run(null);
            return;
        }


        Intent intent = new Intent();
        intent.putExtra(FileConfig.KEY_RESULT_SELECTED_FILES, getSelectedPathArray(srcList));
        setResult(RESULT_OK, intent);
        finish();
    }

    private String[] getSelectedPathArray(List<FileData> srcList) {
        if (srcList == null || srcList.size() == 0) {
            return null;
        }


        List<String> hadSelectList = new ArrayList<String>();
        for (FileData item : srcList) {
            if (item.isSelected) {
                hadSelectList.add(item.getFileContent().getPath());
            }
        }

        if (hadSelectList.size() == 0) {
            return null;
        }

        String[] pathArray = new String[hadSelectList.size()];

        for (int i = 0; i < hadSelectList.size(); ++i) {
            pathArray[i] = hadSelectList.get(i);
        }

        return pathArray;
    }

    protected String getPwd() {
        if (!historyFileStack.isEmpty())
            return historyFileStack.peek().getFileContent().getPath();

        return rootFile.getFileContent().getPath();
    }

}
