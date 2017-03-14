package com.ling.filebrowser.sort.method;

import com.ling.filebrowser.model.FileData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/14.
 */

public interface SortMethod {
     ArrayList<FileData> doSort(List<FileData> src);
}
