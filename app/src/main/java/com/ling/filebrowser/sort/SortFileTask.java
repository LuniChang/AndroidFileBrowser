package com.ling.filebrowser.sort;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ling.filebrowser.model.FileData;
import com.ling.filebrowser.sort.method.SortByDir;
import com.ling.filebrowser.sort.method.SortMethod;
import com.ling.filebrowser.util.Util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import com.brt.log.AppLog;

public  class SortFileTask extends AbstractSortFileTask {

    private AppLog appLog = new AppLog(1, SortFileTask.class.getName());
    private File waitSortParent;
    private SortThread sortThread;
    private boolean isRun=true;



    private SortHandler sortHandler=new SortHandler();

    private class SortHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            try {
                Bundle bundle = msg.getData();
                ArrayList<FileData> result =  bundle.getParcelableArrayList("list");


                if(onGetSortedFilesListener!=null){
                    onGetSortedFilesListener.onGetResult(result);
                }



            } catch (Exception e) {
                appLog.e(Util.getExceptionStackInfo(e));
            }

        }
    }


    private class SortThread extends Thread {

        @Override
        public void run() {
            try {

                while (isRun){

                    if (waitSortParent!=null){

                        List<FileData> src=null;
                        if(sortFileFilter!=null){
                            src=FileData.fileArrayToFileDataList(waitSortParent.listFiles(sortFileFilter));
                        }else {
                            src=FileData.fileArrayToFileDataList(waitSortParent.listFiles());
                        }

                        ArrayList<FileData> result=doSort(src);
                        noticHandler(result);

                    }else {
                        noticHandler(new ArrayList<FileData>());
                    }


                    synchronized (sortThread){
                        sortThread.wait();
                    }

                }


            }catch (Exception e){
                noticHandler(new ArrayList<FileData>());
                appLog.e(Util.getExceptionStackInfo(e));
            }finally {
                isRun=false;
            }


        }
    }


    protected void noticHandler(ArrayList<FileData> src){
        Message message=new Message();
        Bundle data=new Bundle();
        data.putParcelableArrayList("list",src);
        message.setData(data);
        message.setTarget(sortHandler);
        message.sendToTarget();
    }

    private  FileFilter sortFileFilter;

    @Override
    public void sort(File parentFile, FileFilter fileFilter){
        this.waitSortParent= parentFile;
        this.sortFileFilter=fileFilter;
        isRun=true;
        if(sortThread==null){
            sortThread=new SortFileTask.SortThread();
            sortThread.start();
        }
        if(sortThread.getState()== Thread.State.WAITING){
            synchronized (sortThread){
                sortThread.notify();
            }
        }
    }



    private SortMethod sortMethod=new SortByDir();

    protected  ArrayList<FileData> doSort(List<FileData> src){

        if(sortMethod!=null){
            return sortMethod.doSort(src);
        }

        return new ArrayList<>();
    }


    public void setSortMethod(SortMethod sortMethod) {
        this.sortMethod = sortMethod;
    }

    @Override
    public void stop() {
        isRun=false;
        if(sortThread.getState()== Thread.State.WAITING){
            sortThread.notify();
        }
        sortThread=null;
    }

    @Override
    public void sort(File parentFile) {
        sort(parentFile,null);
    }
}
