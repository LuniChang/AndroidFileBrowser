package com.ling.filebrowser.pre;


import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;


import com.brt.log.AppLog;
import com.ling.filebrowser.model.FileData;
import com.ling.filebrowser.util.Util;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ling.filebrowser.pre.model.PreviewerResult;

public class FileBitmapPreviewer implements FilePreviewerInterface {

    private AppLog appLog = new AppLog(1, FileBitmapPreviewer.class.getName());


    private int maxBitmapWidth = 240;
    private int maxBitmapHeight = 400;
    private static final int waitTime = 2000;


    public interface OnGetPreviewerResultListener {

        void onGetResult(PreviewerResult result);
    }

    public FileBitmapPreviewer(OnGetPreviewerResultListener onGetPreviewerResultListener) {
        this.onGetPreviewerResultListener = onGetPreviewerResultListener;
    }

    private volatile boolean runFlag = false;


    private OnGetPreviewerResultListener onGetPreviewerResultListener;


    private BlockingQueue<FileData> waitProcessQueue = new LinkedBlockingQueue<FileData>();
    private List<FileData> waitProcessList = new ArrayList<FileData>();

//    private static FileBitmapPreviewer instance;

    private PreBitmapHandler preBitmapHandler = new PreBitmapHandler();

    private class PreBitmapHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            try {
                Bundle bundle = msg.getData();
                Bitmap bitmap = bundle.getParcelable("bitmap");
                File file = (File) bundle.getSerializable("file");
                PreviewerResult result = new PreviewerResult();
                result.bitmap = bitmap;
                result.file = file;

                if (onGetPreviewerResultListener != null)
                    onGetPreviewerResultListener.onGetResult(result);
            } catch (Exception e) {
                appLog.e(Util.getExceptionStackInfo(e));
            }

        }
    }

    private PreBitmapThread preBitmapThread;


    private int indexList(List<FileData> srcList, FileData data) {
        for (int i = 0; i < srcList.size(); ++i) {

            if (srcList.get(i).getFileContent().getPath().
                    equals(data.getFileContent().getPath())) {
                return i;
            }

        }

        return -1;
    }

    private class PreBitmapThread extends Thread {
        @Override
        public void run() {
            try {


//                while (waitProcessQueue != null && waitProcessQueue.size()!=0 && runFlag) {
                while (waitProcessList != null && runFlag) {

                    try {

                        while (waitProcessQueue != null && waitProcessQueue.size() != 0) {

                            FileData tmpData = waitProcessQueue.poll();
                            if (indexList(waitProcessList, tmpData) < 0) {
                                waitProcessList.add(tmpData);
                            }

                        }

                        if (waitProcessList.size() == 0) {
                            break;
                        }

                        File file = waitProcessList.get(0).getFileContent();
                        waitProcessList.remove(0);
                        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(file.getPath(),
                                MediaStore.Video.Thumbnails.MICRO_KIND);

                        if (bitmap == null)
                            bitmap = ThumbnailUtils.createVideoThumbnail(file.getPath(),
                                    MediaStore.Images.Thumbnails.MICRO_KIND);

                        if (bitmap == null) {
                            bitmap = Util.getDecodeBitmap(file.getPath(), maxBitmapWidth, maxBitmapHeight);
                        } else {
                            int bitWidth = bitmap.getWidth();
                            int bitHeight = bitmap.getHeight();

                            if (bitWidth > maxBitmapWidth) {
                                bitWidth = maxBitmapWidth;
                            }
                            if (bitHeight > maxBitmapHeight) {
                                bitHeight = maxBitmapHeight;
                            }

                            Bitmap scaleBitmap =
                                    Bitmap.createBitmap(bitmap, 0, 0, bitWidth,
                                            bitHeight);

                            bitmap.recycle();
                            bitmap = scaleBitmap;
                        }


                        Message msg = new Message();

                        Bundle bundle = new Bundle();
                        bundle.putParcelable("bitmap", bitmap);
                        bundle.putSerializable("file", file);
                        msg.setData(bundle);
                        msg.setTarget(preBitmapHandler);
                        msg.sendToTarget();

                        if (waitProcessList.size() == 0) {
                            Thread.sleep(waitTime);
                        }

                    } catch (Exception e) {
                        appLog.e(Util.getExceptionStackInfo(e));
                    }


                }

            } catch (Exception e) {
                appLog.e(Util.getExceptionStackInfo(e));
            } finally {
                runFlag = false;
                preBitmapThread = null;
            }


        }
    }


    public int getMaxBitmapWidth() {
        return maxBitmapWidth;
    }

    public void setMaxBitmapWidth(int maxBitmapWidth) {
        this.maxBitmapWidth = maxBitmapWidth;
    }

    public int getMaxBitmapHeight() {
        return maxBitmapHeight;
    }

    public void setMaxBitmapHeight(int maxBitmapHeight) {
        this.maxBitmapHeight = maxBitmapHeight;
    }

    @Override
    public synchronized boolean addPreFile(FileData file) {

        if (!runFlag || preBitmapThread == null) {
            runFlag = true;
            if (preBitmapThread == null) {
                preBitmapThread = new PreBitmapThread();
                preBitmapThread.start();
            } else if (preBitmapThread.getState() == Thread.State.WAITING) {
                preBitmapThread.notify();
            }
        }

        boolean offerSuccess = this.waitProcessQueue.offer(file);
//        boolean offerSuccess=this.waitProcessList.add(file);

        if (!offerSuccess) {
            appLog.w("offerSuccess=false");
        }

        return offerSuccess;
    }

    @Override
    public synchronized void stopPre() {

        runFlag = false;
        if (preBitmapThread != null) {
            preBitmapThread = null;
        }

    }

}
