package com.ling.filebrowser.sort.method;

import com.ling.filebrowser.model.FileData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2017/3/14.
 */

public class SortByDir implements SortMethod{


    private Comparator<FileData> comparator= new Comparator<FileData>() {
        @Override
        public int compare(FileData lhs, FileData rhs) {
            if(lhs.getFileContent().isDirectory() && !rhs.getFileContent().isDirectory())
            {
                return -1;
            }else if (!lhs.getFileContent().isDirectory() && rhs.getFileContent().isDirectory()){

                return 1;
            }else
            {
                return 0;
            }
        }
    };
    @Override
    public ArrayList<FileData> doSort(List<FileData> src) {
        ArrayList<FileData> result=new ArrayList<>();
        if (src==null)
            return result;

        SortByName sortByName=new SortByName();
        result=sortByName.doSort(src);

        Collections.sort(result, comparator);

//        SortByName sortByName=new SortByName();
//
//        ArrayList<FileData> resultByName=sortByName.doSort(src);
//
//
//        ArrayList<FileData> fileList=new ArrayList<>();
//        for (int i=0;i<src.size();++i){
//
//            if(src.get(i).getFileContent().isDirectory()){
//                result.add(src.get(i));
//            }else {
//                fileList.add(src.get(i));
//            }
//        }
//
//        //add no dir file
//        for (int i=0;i<fileList.size();++i){
//            result.add(fileList.get(i));
//        }

        return result;
    }
}
