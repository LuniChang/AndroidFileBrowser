package com.ling.filebrowser;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.app.Activity;
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
import com.ling.filebrowser.util.Util;

public class FileMainActivity extends Activity {

	
	private GridView gridViewContent;
	private FileData fileList;

	private MedioAdapter medioAdapter;
	private Stack<File> historyFileStack=new Stack<File>();
	private TextView textTitle;
	private Button lastFileButton;

	private Button confirmButton;

	private FileFilter fileFilter;
	private String fileFilteName;


	private String rootPath=Util.getSDcardPath();
	
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

		fileFilteName=savedInstanceState.getString(FileConfig.KEY_FILE_FILTER_NAME,null);
		if(fileFilteName!=null){
			fileFilter=new FileFilter() {
				@Override
				public boolean accept(File pathname) {

					if(pathname.isDirectory())
						return true;


					    String[] filerNames=fileFilteName.split("|");

					if(filerNames!=null){
						String name = pathname.getName();
						for (String filerName:filerNames){
							if(!name.endsWith(filerName))
								return false;
						}
					}
					return true;
				}
			};
		}


		rootPath=savedInstanceState.getString(FileConfig.KEY_ROOT_PATH,Util.getSDcardPath());

		gridViewContent.setColumnWidth(getColumnWidth());
		gridViewContent.setNumColumns(3);
		gridViewContent.setHorizontalSpacing(8);
		gridViewContent.setVerticalSpacing(8);

		File sdcardPath=new File(rootPath);
		pushFile(sdcardPath.getParentFile());
		medioAdapter=new MedioAdapter(this, FileData.fileArrayToFileDataList(sdcardPath.getParentFile().listFiles()));
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
				backLastPath();
			}
		});
		confirmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callbackHadSeletedFiles();
			}
		});

	}

	protected void backLastPath() {
		File popFile=popFile();
		updateFileShowList(popFile);
		medioAdapter.notifyDataSetChanged();
	}

	private void onClickItem(int position) {
		if(position<0&&position>medioAdapter.getList().size()){
            return;
        }

		FileData fileDataItem=medioAdapter.getList().get(position);

		if(fileDataItem.getFileContent().isDirectory()){
            pushFile(fileDataItem.getFileContent());

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
	
//	private boolean isImgType(String fileType){
//		return "jpg".equals(fileType)||"png".equals(fileType)||"3gp".equals(fileType);
//	}
	
	private void pushFile(File file){
		textTitle.setText(file.getName());
		historyFileStack.push(file);
	}
	
	private File popFile(){
		File sdcardPath=new File(rootPath);
		File file=sdcardPath.getParentFile(); 
		if(!historyFileStack.isEmpty()){
			 file=historyFileStack.pop().getParentFile();
		}
		textTitle.setText(file.getName());
		return file;
		
	}




	private void callbackHadSeletedFiles(){
		Intent intent=new Intent();

		intent.putExtra(FileConfig.KEY_SELECTED_FILES,getSelectedPathArray(medioAdapter.getList()));

		setResult(RESULT_OK,intent);
		finish();
	}


	private String[] getSelectedPathArray(List<FileData> srcList){
		if(srcList==null||srcList.size()==0){
			return null;
		}



		List<String> hadSelectList=new ArrayList<String>();
		for (FileData item:srcList){
			if(item.isSelected){
				hadSelectList.add(item.getFileContent().getPath());
			}
		}

		if(hadSelectList.size()==0){
			return null;
		}

		String[] pathArray=new String[hadSelectList.size()];

		for(int i=0;i<hadSelectList.size();++i){
			pathArray[i]=hadSelectList.get(i);
		}

		return pathArray;
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
