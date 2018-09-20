package com.ling.filebrowser.sort.method;

import com.ling.filebrowser.model.FileData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class SortByDate implements SortMethod {

    private Comparator<FileData> comparator = new Comparator<FileData>() {
        @Override
        public int compare(FileData lhs, FileData rhs) {
            long lDate = lhs.getFileContent().lastModified();
            long rDate = rhs.getFileContent().lastModified();
            if (lDate < rDate) {
                return -1;
            } else if (lDate > rDate) {

                return 1;
            } else {
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


        return result;
    }

}
