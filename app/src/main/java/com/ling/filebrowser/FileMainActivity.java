package com.ling.filebrowser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.ling.filebrowser.config.FileConfig;
import com.ling.filebrowser.model.FileData;

public class FileMainActivity extends AbstractFileActivity {

	
	private GridView gridViewContent;

	private MedioAdapter medioAdapter;
	private TextView textTitle;
	private Button lastFileButton;

	private Button confirmButton;


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

	}



	private void setData(Bundle savedInstanceState) {

		initFileFiler(savedInstanceState);


		initRootPath(savedInstanceState);

		gridViewContent.setColumnWidth(getColumnWidth());
		gridViewContent.setNumColumns(3);
		gridViewContent.setHorizontalSpacing(8);
		gridViewContent.setVerticalSpacing(8);


		medioAdapter=new MedioAdapter(this, FileData.fileArrayToFileDataList(getRootFile().listFiles()));
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

	}

	protected void returnLastPath() {
		File lastFile=popFile();
		textTitle.setText(lastFile.getName());
		updateFileShowList(lastFile);
		medioAdapter.notifyDataSetChanged();
	}

	private void onClickItem(int position) {
		if(position<0&&position>medioAdapter.getList().size()){
            return;
        }

		FileData fileDataItem=medioAdapter.getList().get(position);

		if(fileDataItem.getFileContent().isDirectory()){
            pushFile(fileDataItem.getFileContent());
			textTitle.setText(fileDataItem.getFileContent().getName());
            updateFileShowList(fileDataItem.getFileContent());
            gridViewContent.scrollTo(0,0);
            medioAdapter.notifyDataSetInvalidated();

            return;
        }

		medioAdapter.getList().get(position).isSelected=
                    medioAdapter.getList().get(position).isSelected?false:true;

		medioAdapter.notifyDataSetChanged();
	}

	private void updateFileShowList(File file) {
		//TODO should be asyn
		if(fileFilter!=null){
            medioAdapter.setList(FileData.fileArrayToFileDataList(file.listFiles()));
        }else {
            medioAdapter.setList(FileData.fileArrayToFileDataList(file.listFiles(fileFilter)));
        }
	}

	private int getColumnWidth(){
		int w=getWindow().getWindowManager().getDefaultDisplay().getWidth();
		int columnWidth=(w-24)/3;
		return columnWidth;
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
