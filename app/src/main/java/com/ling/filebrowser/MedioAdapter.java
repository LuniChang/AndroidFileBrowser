package com.ling.filebrowser;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ling.filebrowser.model.FileData;
import com.ling.filebrowser.util.FileBitmapPreviewer;
import com.ling.filebrowser.config.LruCacheConfig;

public class MedioAdapter extends BaseAdapterFactory<FileData>{







	private FileBitmapPreviewer fileBitmapPreviewer;
	private int itemHeight=LinearLayout.LayoutParams.WRAP_CONTENT;

	public int getItemHeight() {
		return itemHeight;
	}

	public void setItemHeight(int itemHeight) {
		this.itemHeight = itemHeight;
		if(fileBitmapPreviewer!=null){
			fileBitmapPreviewer.setMaxBitmapHeight(itemHeight);
			fileBitmapPreviewer.setMaxBitmapWidth(itemHeight);
		}

	}

	public MedioAdapter(Context context, List<FileData> list) {
		super(context, list);
		initPreviewer();
	}

	@Override
	public View createItemView(int position, View convertView, ViewGroup parent) {

		FileData fileData = list.get(position);
		if (convertView == null) {

			convertView = layoutInflater.inflate(R.layout.adapter_medio, null);
		}

		ViewHolder viewHolder = (ViewHolder) convertView.getTag();

		if (viewHolder == null) {
			convertView = layoutInflater.inflate(R.layout.adapter_medio, null);
			initView(convertView, fileData);
			return convertView;
		}
		if (viewHolder.hadNullField()) {
			initView(convertView, fileData);
			return convertView;
		}


		setData(viewHolder, fileData);

		setViewCase(viewHolder, fileData);
		
		return convertView;
	}

	protected  void initView(View convertView, FileData item) {
		final ViewHolder viewHolder = new ViewHolder();
		viewHolder.textview_content = (TextView) convertView.findViewById(R.id.textview_content);
		viewHolder.imageview_content = (ImageView) convertView.findViewById(R.id.imageview_content);
		viewHolder.checkbox_content = (CheckBox) convertView.findViewById(R.id.checkbox_content);
		viewHolder.imageview_content.setLayoutParams(new LinearLayout.LayoutParams( itemHeight, itemHeight));

		convertView.setTag(viewHolder);


		setData(viewHolder, item);
	}

	protected void setViewCase(final ViewHolder viewHolder, final FileData item) {
		viewHolder.checkbox_content.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				item.isSelected=isChecked;
				MedioAdapter.this.notifyDataSetChanged();
			}
		});
	}

	protected void setData(ViewHolder viewHolder, FileData fileItem) {
		Bitmap preBitmap = LruCacheConfig.getInstance(context).get(fileItem.getFileContent().getPath());
		if (preBitmap == null) {
			initPreviewer();
//			viewHolder.textview_content.setVisibility(View.VISIBLE);
			if(!fileItem.getFileContent().isDirectory()){
				this.fileBitmapPreviewer.addPreFile(fileItem);
			}
			viewHolder.imageview_content.setImageResource(R.drawable.ic_launcher);
		} else {
//			viewHolder.textview_content.setVisibility(View.GONE);
			viewHolder.imageview_content.setImageBitmap(preBitmap);
		}


		if(fileItem.isSelected){
			viewHolder.checkbox_content.setChecked(true);
			viewHolder.checkbox_content.setVisibility(View.VISIBLE);
		}else{
			viewHolder.checkbox_content.setChecked(false);
			viewHolder.checkbox_content.setVisibility(View.GONE);
		}

		viewHolder.textview_content.setText(fileItem.getFileContent().getName());


	}

//	private statit int preBitCount=0;
	private void initPreviewer() {
		if (this.fileBitmapPreviewer == null) {
			this.fileBitmapPreviewer = new FileBitmapPreviewer(new FileBitmapPreviewer.OnGetPreviewerResultListener() {
				@Override
				public void onGetResult(FileBitmapPreviewer.PreviewerResult result) {

					if (result != null && result.file != null) {
						LruCacheConfig.getInstance(context).put(result.file.getPath(), result.bitmap);
					}

					MedioAdapter.this.notifyDataSetChanged();
				}
			});
			this.fileBitmapPreviewer.setMaxBitmapHeight(itemHeight);
			this.fileBitmapPreviewer.setMaxBitmapWidth(itemHeight);
		}

	}

	public final class ViewHolder {

		ImageView imageview_content;
		CheckBox checkbox_content;
		TextView textview_content;

		public boolean hadNullField(){
			return imageview_content ==null
					||checkbox_content==null
					||textview_content==null
					;


		}
	}
	
	

}
