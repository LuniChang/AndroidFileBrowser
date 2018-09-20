package com.ling.filebrowser.sort;

import com.ling.filebrowser.model.FileData;

import java.io.FileFilter;
import java.util.List;

public abstract class AbstractSortFileTask {


    public abstract void sort(FileData parentFile, FileFilter fileFilter);

    public abstract void sort(FileData parentFile);

    public abstract void stop();

    public interface OnGetSortedFilesListener {

        void onGetResult(List<FileData> result);
    }

    protected OnGetSortedFilesListener onGetSortedFilesListener;

    public OnGetSortedFilesListener getOnGetSortedFilesListener() {
        return onGetSortedFilesListener;
    }

    public void setOnGetSortedFilesListener(OnGetSortedFilesListener onGetSortedFilesListener) {
        this.onGetSortedFilesListener = onGetSortedFilesListener;
    }


//    public abstract void addSelectedFile(FileData srcFile);
//    public abstract void moveSelectedFile(FileData srcFile);


}
