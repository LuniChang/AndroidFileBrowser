package com.ling.filebrowser.pre;

import com.ling.filebrowser.model.FileData;

/**
 * Created by Administrator on 2017/3/10.
 */
public interface FilePreviewerInterface {


    boolean addPreFile(FileData file);

    void stopPre();


}
