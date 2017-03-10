package com.ling.filebrowser.util;

import com.ling.filebrowser.model.FileData;

/**
 * Created by Administrator on 2017/3/10.
 */
public interface FilePreviewerInterface {


    boolean addPreFile(FileData file);

    void stopPre();

    public interface OnGetPreviewerResultListener {

        void onGetResult(FileBitmapPreviewer.PreviewerResult result);
    }
}
