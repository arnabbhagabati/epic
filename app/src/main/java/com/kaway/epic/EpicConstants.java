package com.kaway.epic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EpicConstants {

    public static final String VID_ID = "vidId";
    public static final String COMMENTS_DATA = "cmntsData";

    public static final String VID_ID_LIST_TABLE_PK_COL = "id";
    public static final String VID_ID_LIST_TABLE_VID_SET_COL = "vidSet";
    public static final String VID_ID_LIST_TABLE_NAME = "VidList";

    public static final String VID_DATA_TABLE_NAME = "VidData";
    public static final String VID_DATA_TABLE_PK_COL = "vidId";
    public static final String VID_DATA_TABLE_VID_DATA_COL = "videoData";
    

    public static String EPIC_TABLE_NAME  = "VID_ID_LIST";

    public static String EPIC_LOG_TAG  = "epic.log";

    public static Set<String> DEFAULT_VID_ID_SET = Set.of("jzHVnptr4sc", "3U9P4-ac0Lc", "BOsm3I8jdlQ","bO7Os1Zu8Z4");
    public static String DEFAULT_VID_ID_SET_KEY = "VID_ID_SET_KEY";


    public static final String VID_KEY_0 = "VID_KEY_0";
    public static final String VID_KEY_1 = "VID_KEY_1";
    public static final String VID_KEY_2 = "VID_KEY_2";
    public static final String VID_KEY_3 = "VID_KEY_3";

    public static String LAST_VID_SET_KEY_IDX = "LAST_VID_SET_KEY_IDX";

    public static int MIN_VID_LIST_SIZE_FOR_FETCH = 20;

    public static String FIRST_VIDSET_ID_KEY = "FIRST_VIDSET_ID_KEY";

    public static String RETRIEVED_VID_SET_SET_KEY = "RETRIEVED_VID_SET_SET";


}
