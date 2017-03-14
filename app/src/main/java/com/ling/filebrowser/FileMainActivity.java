package com.ling.filebrowser;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.brt.log.AppLog;
import com.ling.filebrowser.model.FileData;
import com.ling.filebrowser.sort.AbstractSortFileTask;
import com.ling.filebrowser.sort.SortFileTask;
import com.ling.filebrowser.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileMainActivity extends AbstractFileActivity {
	private AppLog appLog = new AppLog(1, FileMainActivity.class.getName());

	
	private GridView gridViewContent;

	private MedioAdapter medioAdapter;
	private TextView textTitle;


	private TextView textBack;
	private TextView textSelect;
	private Button lastFileButton;

	private Button confirmButton;


	private boolean isShowCheckBox=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_main);
		initView();
		setData(savedInstanceState);
		
	}

	private void initView() {
		gridViewContent=(GridView) findViewById(R.id.gridView_content);
		textTitle= (TextView) findViewById(R.id.textView_title);
		lastFileButton =(Button) findViewById(R.id.button_back);
		confirmButton =(Button) findViewById(R.id.button_ok);
		textBack=(TextView) findViewById(R.id.textView_back);
		textSelect=(TextView) findViewById(R.id.textView_more);
	}



	private void setData(Bundle savedInstanceState) {

		initFileFiler(savedInstanceState);


		initRootPath(savedInstanceState);

		gridViewContent.setColumnWidth(getColumnWidth());
		gridViewContent.setNumColumns(3);
		gridViewContent.setHorizontalSpacing(8);
		gridViewContent.setVerticalSpacing(8);


		medioAdapter=new MedioAdapter(this, new ArrayList<FileData>());

		medioAdapter.setItemHeight(getColumnWidth());
		gridViewContent.setAdapter(medioAdapter);
		gridViewContent.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {


				onClickItem(position);
			}
			
		});
		
		lastFileButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				returnLastPath();
			}
		});
		confirmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callbackHadSeletedFiles(medioAdapter.getList());
			}
		});
		textBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				returnLastPath();
			}
		});
		textSelect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				isShowCheckBox=isShowCheckBox?false:true;
				medioAdapter.setShowCheckBox(isShowCheckBox);
				if(isShowCheckBox){
					textSelect.setText(R.string.cancel);
				}else {
					textSelect.setText(R.string.select);
				}
			}
		});

		updateFileShowList(getRootFile());
	}

	protected void returnLastPath() {
		File lastFile=popFile();
		textTitle.setText(lastFile.getName());
		updateFileShowList(lastFile);

	}

	private void onClickItem(int position) {
		if((position<0&&position>=medioAdapter.getList().size())||medioAdapter.getList().size()==0){
            return;
        }

		FileData fileDataItem=medioAdapter.getList().get(position);

		if(isShowCheckBox){
			fileDataItem.isSelected=fileDataItem.isSelected?false:true;
			medioAdapter.notifyDataSetChanged();
			return;
		}

		if(fileDataItem.getFileContent().isDirectory()){
            pushFile(fileDataItem.getFileContent());
			textTitle.setText(fileDataItem.getFileContent().getName());
            updateFileShowList(fileDataItem.getFileContent());
            gridViewContent.scrollTo(0,0);
            medioAdapter.notifyDataSetInvalidated();

            return;
        }

		try{
			Util.openFile(medioAdapter.getList().get(position).getFileContent(),FileMainActivity.this);
		}catch (Exception e){
			appLog.e(e.toString());
			Toast.makeText(FileMainActivity.this,"未安装相关应用",Toast.LENGTH_LONG).show();
		}

		medioAdapter.notifyDataSetChanged();
	}

	private SortFileTask sortFileTask;

	private void updateFileShowList(final File file) {
//		should be asyn and sort
//		if(fileFilter!=null){
//            medioAdapter.setList(FileData.fileArrayToFileDataList(file.listFiles()));
//
//        }else {
//            medioAdapter.setList(FileData.fileArrayToFileDataList(file.listFiles(fileFilter)));
//
//        }

		if(sortFileTask==null){
			sortFileTask=new SortFileTask();
			sortFileTask.setOnGetSortedFilesListener(new AbstractSortFileTask.OnGetSortedFilesListener() {
				@Override
				public void onGetResult(List<FileData> result) {
					medioAdapter.setList(result);
				}
			});
		}
		sortFileTask.sort(file,fileFilter);

	}

	private int getColumnWidth(){
		int w=getWindow().getWindowManager().getDefaultDisplay().getWidth();
		int columnWidth=(w-24)/3;
		return columnWidth;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(sortFileTask!=null){
			sortFileTask.stop();
		}
	}


//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.file_main, menu);
//		return false;
//	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
}
