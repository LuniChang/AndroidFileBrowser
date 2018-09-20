package com.ling.filebrowser;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ling.filebrowser.adpater.FileBaseAdapter;
import com.ling.filebrowser.adpater.LineMedioAdapter;
import com.ling.filebrowser.adpater.MedioAdapter;
import com.ling.filebrowser.config.FileConfig;
import com.ling.filebrowser.model.FileData;
import com.ling.filebrowser.sort.AbstractSortFileTask;
import com.ling.filebrowser.sort.SortFileTask;
import com.ling.filebrowser.util.Util;
import com.xu.Log.AppLog;


import java.util.ArrayList;
import java.util.List;

/**
 * note: to use FileConfig for control,see{@link com.ling.filebrowser.config.FileConfig}
 *
 */
public class FileMainActivity extends AbstractFileActivity {
    private AppLog appLog = new AppLog(1, FileMainActivity.class.getName());


    private GridView gridViewContent;

    private FileBaseAdapter fileBaseAdapter;

    private FileBaseAdapter listAdapter;
    private FileBaseAdapter gridAdapter;
    private TextView textTitle;


    private HorizontalScrollView horizontalScrollView_title;

    private TextView textViewRight;
    private TextView textSelect;
    private TextView textViewBack;
    private TextView textViewTitleLeft;
    private Button confirmButton;


    private ImageView imageview_grid;
    private ImageView imageview_list;


    private boolean isShowCheckBox = false;
    private int columnNum = 1;
    private int spacing = 12;

    private int selectedCount=0;
    /**
     * When maxSelectNum<0,do nothing
     */
    private int maxSelectNum =-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_main);
        initView();
        setData(savedInstanceState);
        setViewCase();
    }

    private void initView() {
        gridViewContent = (GridView) findViewById(R.id.gridView_content);
        textTitle = (TextView) findViewById(R.id.textView_title);
        textViewBack = (TextView) findViewById(R.id.textView_back);
        confirmButton = (Button) findViewById(R.id.button_ok);
        textViewTitleLeft = (TextView) findViewById(R.id.textView_left);
        textViewRight = (TextView) findViewById(R.id.textView_right);
        textSelect = (TextView) findViewById(R.id.textView_select);
        horizontalScrollView_title=(HorizontalScrollView)findViewById(R.id.horizontalScrollView_title);

        imageview_grid = (ImageView) findViewById(R.id.imageview_grid);
        imageview_list = (ImageView) findViewById(R.id.imageview_list);
    }


    private void setData(Bundle savedInstanceState) {

        initFileFiler();
        initRootPath();

        initFlield();
        setGridViewColumn();

        updateTitle();

        updateFileShowList(getRootFile());
    }

    private void initFlield() {
        if (getIntent().getExtras() == null) {
            return;
        }
        maxSelectNum = getIntent().getExtras().getInt(FileConfig.KEY_MAX_SELECT_NUM, -1);

    }

    private void setGridViewColumn() {
        gridViewContent.setColumnWidth(getColumnWidth());
        gridViewContent.setNumColumns(columnNum);
        gridViewContent.setHorizontalSpacing(spacing);
        gridViewContent.setVerticalSpacing(spacing);


        List files= new ArrayList<>();
        if(fileBaseAdapter!=null){
            files=fileBaseAdapter.getList();
        }

        if (columnNum > 1) {
            if(gridAdapter==null){
                gridAdapter= new MedioAdapter(this, files);
            }
            fileBaseAdapter = gridAdapter;
            fileBaseAdapter.setItemHeight(getColumnWidth());
        } else {
            if(listAdapter==null){
                listAdapter= new LineMedioAdapter(this, files);
            }
            fileBaseAdapter =listAdapter;
            fileBaseAdapter.setItemHeight(getWindow().getWindowManager().getDefaultDisplay().getWidth()/5);
        }

        fileBaseAdapter.setList(files);
        gridViewContent.setAdapter(fileBaseAdapter);
    }


    private void updateTitle() {
        textTitle.setText(getPwd());
        horizontalScrollView_title.fullScroll(View.FOCUS_RIGHT);
    }

    private void setViewCase() {
        gridViewContent.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                onClickItem(position);
            }

        });

        gridViewContent.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                switchSelectBox();
                return false;
            }
        });

        textViewBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                returnLastPath();
            }
        });

        textViewTitleLeft.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                returnLastPath();
            }
        });
        confirmButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(selectedCount<=0){
                    finish();
                    return;
                }
                callbackHadSeletedFiles(fileBaseAdapter.getList());
            }
        });
        textViewRight.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        textSelect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                switchSelectBox();
            }
        });

        imageview_grid.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                imageview_list.setVisibility(View.VISIBLE);
                imageview_grid.setVisibility(View.GONE);
                columnNum=3;
                setGridViewColumn();
            }
        });
        imageview_list.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                imageview_grid.setVisibility(View.VISIBLE);
                imageview_list.setVisibility(View.GONE);
                columnNum=1;
                setGridViewColumn();
            }
        });
    }

    protected void switchSelectBox() {
        isShowCheckBox = isShowCheckBox ? false : true;
        fileBaseAdapter.setShowCheckBox(isShowCheckBox);
//        selectedCount=0;
        if (isShowCheckBox) {
            textSelect.setText(R.string.cancel);
        } else {
            textSelect.setText(R.string.select);

        }
        clearFileSelection();
    }

    protected void returnLastPath() {
        FileData lastFile = popFile();
//        textTitle.setText(lastFile.getFileContent().getName());
        updateTitle();
        updateFileShowList(lastFile);

    }

    protected void updateConfirmText(){
        if(selectedCount>0)
            confirmButton.setText("确定("+selectedCount+")");
        else
            confirmButton.setText("确定");
    }

    private void onClickItem(int position) {
        if ((position < 0 && position >= fileBaseAdapter.getList().size()) || fileBaseAdapter.getList().size() == 0) {
            return;
        }

        FileData fileDataItem = fileBaseAdapter.getList().get(position);

        if (isShowCheckBox) {

            if(maxSelectNum >=0&&selectedCount>= maxSelectNum){
                if(!fileDataItem.isSelected){
                    Toast.makeText(FileMainActivity.this,"超过可选数量"+ maxSelectNum,Toast.LENGTH_LONG).show();
                    return;
                }
            }

            fileDataItem.isSelected = !fileDataItem.isSelected;
            if ( fileDataItem.isSelected){
                ++selectedCount;
            }else {
                --selectedCount;
            }
            updateConfirmText();
            fileBaseAdapter.notifyDataSetChanged();
            return;
        }

        if (fileDataItem.getFileContent().isDirectory()) {
            pushFile(fileDataItem);
//            textTitle.setText(fileDataItem.getFileContent().getName());
            updateTitle();
            updateFileShowList(fileDataItem);
            gridViewContent.scrollTo(0, 0);
            fileBaseAdapter.notifyDataSetInvalidated();

            return;
        }

        try {
            Util.openFile(fileBaseAdapter.getList().get(position).getFileContent(), FileMainActivity.this);
        } catch (Exception e) {
            appLog.e(e.toString());
            Toast.makeText(FileMainActivity.this, "未安装相关应用", Toast.LENGTH_LONG).show();
        }

        fileBaseAdapter.notifyDataSetChanged();
    }

    private SortFileTask sortFileTask;

    private void updateFileShowList(final FileData file) {

        if (sortFileTask == null) {
            sortFileTask = new SortFileTask();
            sortFileTask.setOnGetSortedFilesListener(new AbstractSortFileTask.OnGetSortedFilesListener() {
                @Override
                public void onGetResult(List<FileData> result) {
                    FileMainActivity.this.fileBaseAdapter.setList(result);
                }
            });
        }
        sortFileTask.sort(file, fileFilter);

    }

    private int getColumnWidth() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int w = metric.widthPixels;
        int columnWidth = (w - spacing * (columnNum - 1)) / columnNum;
        return columnWidth;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sortFileTask != null) {
            sortFileTask.stop();
        }
    }


    private void clearFileSelection(){
        for (int i = 0; i < fileBaseAdapter.getList().size(); ++i) {
            fileBaseAdapter.getList().get(i).isSelected=false;
        }
        fileBaseAdapter.notifyDataSetChanged();
        selectedCount=0;
        updateConfirmText();
    }

    @Override
    public void onBackPressed() {
        if(historyFileStack.isEmpty()){
            super.onBackPressed();
            return;
        }else {
           returnLastPath();
        }


    }
}
