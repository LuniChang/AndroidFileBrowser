package com.ling.filebrowser.adpater;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ling.filebrowser.R;
import com.ling.filebrowser.config.LruCacheConfig;
import com.ling.filebrowser.model.FileData;

import java.util.List;

public class MedioAdapter extends FileBaseAdapter {


    public MedioAdapter(Context context, List<FileData> list) {
        super(context, list);
        initPreviewer();

    }

    @Override
    public View createItemView(int position, View convertView, ViewGroup parent) {

        FileData fileData = list.get(position);
        if (convertView == null) {

            convertView = layoutInflater.inflate(R.layout.adapter_fb_medio, null);
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        if (viewHolder == null) {
            convertView = layoutInflater.inflate(R.layout.adapter_fb_medio, null);
            initView(convertView, fileData);
            return convertView;
        }
        if (viewHolder.hadNullField()) {
            initView(convertView, fileData);
            return convertView;
        }


        setData(viewHolder, fileData);

        return convertView;
    }

    protected void initView(View convertView, FileData item) {
        final ViewHolder viewHolder = new ViewHolder();
        viewHolder.textview_content = (TextView) convertView.findViewById(R.id.textview_content);
        viewHolder.imageview_content = (ImageView) convertView.findViewById(R.id.imageview_content);
        viewHolder.checkbox_content = (CheckBox) convertView.findViewById(R.id.checkbox_content);
        viewHolder.imageview_content.setLayoutParams(new LinearLayout.LayoutParams(itemHeight, itemHeight));
        convertView.setTag(viewHolder);

        setData(viewHolder, item);

    }



    protected void setData(ViewHolder viewHolder, FileData fileItem) {
        Bitmap preBitmap = LruCacheConfig.getInstance(context).get(fileItem.getFileContent().getPath());
        if (preBitmap == null) {
            initPreviewer();
            if (!fileItem.getFileContent().isDirectory()) {
                this.fileBitmapPreviewer.addPreFile(fileItem);
                viewHolder.imageview_content.setImageBitmap(fileBitmap);
            } else {
                viewHolder.imageview_content.setImageBitmap(dirBitmap);
            }

        } else {
            viewHolder.imageview_content.setImageBitmap(preBitmap);
        }


        viewHolder.checkbox_content.setVisibility(showCheckBox ? View.VISIBLE : View.GONE);

        viewHolder.checkbox_content.setChecked(fileItem.isSelected);


        viewHolder.textview_content.setText(fileItem.getFileContent().getName());


    }

    public final class ViewHolder {

        ImageView imageview_content;
        CheckBox checkbox_content;
        TextView textview_content;

        public boolean hadNullField() {
            return imageview_content == null
                    || checkbox_content == null
                    || textview_content == null
                    ;


        }
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (fileBitmap != null) {
            fileBitmap.recycle();
        }

        if (dirBitmap != null) {
            dirBitmap.recycle();
        }
    }
}
