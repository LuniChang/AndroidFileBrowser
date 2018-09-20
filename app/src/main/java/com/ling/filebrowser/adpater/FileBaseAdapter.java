package com.ling.filebrowser.adpater;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.LinearLayout;

import com.ling.filebrowser.R;
import com.ling.filebrowser.config.LruCacheConfig;
import com.ling.filebrowser.model.FileData;
import com.ling.filebrowser.pre.FileBitmapPreviewer;
import com.ling.filebrowser.pre.model.PreviewerResult;

import java.util.List;

/**
 * Created by Administrator on 2017/3/15.
 */
public abstract class FileBaseAdapter extends BaseAdapterFactory<FileData> {
    protected boolean showCheckBox = false;
    protected FileBitmapPreviewer fileBitmapPreviewer;
    protected int itemHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
    protected Bitmap fileBitmap;
    protected Bitmap dirBitmap;

    public FileBaseAdapter(Context context, List<FileData> list) {
        super(context, list);
        dirBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_dir);
        fileBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_unknow_file);
    }

    public int getItemHeight() {
        return itemHeight;
    }

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
        if (fileBitmapPreviewer != null) {
            fileBitmapPreviewer.setMaxBitmapHeight(itemHeight);
            fileBitmapPreviewer.setMaxBitmapWidth(itemHeight);
        }

    }

    //	private statit int preBitCount=0;
    protected void initPreviewer() {
        if (this.fileBitmapPreviewer == null) {
            this.fileBitmapPreviewer = new FileBitmapPreviewer(new FileBitmapPreviewer.OnGetPreviewerResultListener() {
                @Override
                public void onGetResult(PreviewerResult result) {

                    if (result != null && result.file != null) {
                        LruCacheConfig.getInstance(context).put(result.file.getPath(), result.bitmap);
                    }

                    FileBaseAdapter.this.notifyDataSetChanged();
                }
            });
            this.fileBitmapPreviewer.setMaxBitmapHeight(itemHeight);
            this.fileBitmapPreviewer.setMaxBitmapWidth(itemHeight);
        }

    }

    public boolean isShowCheckBox() {
        return showCheckBox;
    }

    public void setShowCheckBox(boolean showCheckBox) {
        this.showCheckBox = showCheckBox;
        this.notifyDataSetChanged();
    }

    @Override
    protected void finalize() throws Throwable {
        this.fileBitmapPreviewer.stopPre();
        super.finalize();
    }
}
