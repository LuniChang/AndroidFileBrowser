package com.ling.filebrowser;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public abstract class BaseAdapterFactory<T> extends BaseAdapter {

	protected Context context;
	protected LayoutInflater layoutInflater;
	protected List<T> list;

	/*
	 * listkey (String faceURL,String name,String mark,boolean collectstatus)
	 */

	public BaseAdapterFactory(Context context, List<T> list) {
		super();
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.list = list;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
        if(this.list != null&&this.list.size()==0)
            return 1;
		return this.list != null ? this.list.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (position >= list.size() || position <= 0)
			return new Object();
		return this.list.size();

	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
        if(list.size()==0){
            convertView=this.layoutInflater.inflate(R.layout.adapter_text_only,null);
            TextView textview_content=(TextView)convertView.findViewById(R.id.textview_content);
			textview_content.setText("无数据");
            return convertView;
        }
		return createItemView(position, convertView, parent);
	}

	public abstract View createItemView(int position, View convertView,
			ViewGroup parent);

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
		this.notifyDataSetInvalidated();
    }
}
