package com.ling.filebrowser.config;

/**
 * for  start activity intent param's key
 *
 */

public class FileConfig {

    /**
     * key for set root path
     */
    public final static String KEY_ROOT_PATH = "KEY_ROOT_PATH";

    /**
     * key for set filefilter
     * value sample: "mp4;mp3"   or "mp4"
     */
    public final static String KEY_FILE_FILTER_NAME = "KEY_FILE_FILTER_NAME";

    /**
     * key for result had selected files' path(String[])
     */
    public final static String KEY_RESULT_SELECTED_FILES = "KEY_RESULT_SELECTED_FILES";


    /**
     * config result for aysn or syn ,defualt aysn
     */
    public final static String KEY_RESULT_FOR_ASYN = "KEY_RESULT_FOR_ASYN";
    /**
     * value sample for img
     */
    public final static String FILTER_IMG = "png;jpg;jpeg;gif;bmp";
    /**
     * value sample for vedio
     */
    public final static String FILTER_VEDIO = "avi;rmvb;rm;asf;divx;mpg;mpeg;mpe;wmv;mp4;mkv;vob";

    /**
     * can select files' max num
     */
    public final static String KEY_MAX_SELECT_NUM = "KEY_MAX_SELECT_NUM";

}
