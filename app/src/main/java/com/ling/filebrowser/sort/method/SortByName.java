package com.ling.filebrowser.sort.method;

import com.ling.filebrowser.model.FileData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2017/3/14.
 */

public class SortByName implements SortMethod {

    private Comparator<FileData> comparator= new Comparator<FileData>() {
        @Override
        public int compare(FileData lhs, FileData rhs) {
             int lName=lhs.getFileContent().getName().toLowerCase().toCharArray()[0];
            int rName=rhs.getFileContent().getName().toLowerCase().toCharArray()[0];
            if(lName < rName )
            {
                return -1;
            }else if (lName > rName){

                return 1;
            }else
            {
                return 0;
            }
        }
    };

    @Override
    public ArrayList<FileData> doSort(List<FileData> src) {
        ArrayList<FileData> result = new ArrayList<>();
        if (src == null)
            return result;

        for (int i = 0; i < src.size(); ++i) {
            result.add(src.get(i));
        }

        Collections.sort(result, comparator);

//        for (int i = result.size() - 1; i > 0; --i) {
//            for (int j = 0; j < i; ++j) {
//                String nextName=result.get(j + 1).getFileContent().getName();
//                String preName=result.get(j).getFileContent().getName();
//                int next = nextName.toLowerCase().toCharArray()[0];
//                int pre = preName.toLowerCase().toCharArray()[0];
//                if (pre > next) {
//                    Collections.swap(result, j, j + 1);
//                }
//            }
//        }

        return result;
    }
}
