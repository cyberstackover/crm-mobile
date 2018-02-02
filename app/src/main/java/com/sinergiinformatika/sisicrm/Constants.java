package com.sinergiinformatika.sisicrm;

/**
 * Created by Mark on 12/16/2014.
 *
 * @author Mark
 *         <p/>
 *         Please update the author field if you are editing
 *         this file and your name is not written.
 */
public class Constants {
    public static final boolean DEBUG = true;
    public static final String ROOT_APP_TAG = "com.sinergiinformatika.sisicrm";
    /**
     * User-related
     */
    public static final String USER_ID = ROOT_APP_TAG + ".USER_ID";
    public static final String USER_NAME = ROOT_APP_TAG + ".USER_NAME";
    public static final String USER_FIRST_NAME = ROOT_APP_TAG + ".FIRST_NAME";
    public static final String USER_LAST_NAME = ROOT_APP_TAG + ".LAST_NAME";
    public static final String USER_ROLE = ROOT_APP_TAG + ".ROLE";
    public static final String USER_SESSION = ROOT_APP_TAG + ".SESSION";
    public static final String USER_AREA_NAME = ROOT_APP_TAG + ".AREA_NAME";
    public static final String USER_AREA_ID = ROOT_APP_TAG + ".AREA_ID";
    public static final String USER_DISTRIBUTOR_ID = ROOT_APP_TAG + ".DISTRIBUTOR_ID";
    public static final String USER_DISTRIBUTOR_NAME = ROOT_APP_TAG + ".DISTRIBUTOR";
    public static final String USER_SYNC_ERROR = ROOT_APP_TAG + ".SYNC_ERROR";
    public static final String USER_ALLOW_SURVEY_WITHOUT_CHECKIN = ROOT_APP_TAG + ".ALLOW_SURVEY_WITHOUT_CHECKIN";
    public static final String USER_MAX_CHECKIN_DISTANCE = ROOT_APP_TAG + ".MAX_CHECKIN_DISTANCE";
    public static final String USER_KEY = ROOT_APP_TAG + ".KEY";

    public static final String IMAGE_DIRECTORY_NAME = "CRM SiSi";
    public static final String DEFAULT_LOCALE = "in_ID";
    /**
     * JSON key responses
     */
    public static final String JSON_KEY_STATUS = "status";
    public static final String JSON_KEY_DATA = "data";
    public static final String JSON_KEY_ERROR = "error";
    public static final String JSON_KEY_ERROR_CODE = "code";
    public static final String JSON_KEY_ERROR_MESSAGE = "message";
    public static final String JSON_KEY_USER_ID = "user_id";
    public static final String JSON_KEY_USER_NAME = "user_name";
    public static final String JSON_KEY_USER_FIRST_NAME = "first_name";
    public static final String JSON_KEY_USER_LAST_NAME = "last_name";
    public static final String JSON_KEY_USER_ROLE = "role_name";
    public static final String JSON_KEY_USER_SESSION = "session";
    public static final String JSON_STATUS_OK = "success";

    public static final String ARG_DATA = "data";
    public static final String ARG_READ_ONLY = "read_only";

    public static final String DATE_DAY_FORMAT = "EEEE, dd MMMM yyyy";
    public static final String DATE_MONTH_FORMAT = "MMMM yyyy";
    public static final String DATE_TIME_DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_DEFAULT_FORMAT = "yyyy-MM-dd";
    public static final String DATE_IMAGE_FORMAT = "yyyyMMdd_HHmmss";
    public static final String DATE_HISTORY_FORMAT = "dd MMM yyyy HH:mm:ss";

    public static final String STORE_TYPE_RESELLER = "reseller";
    public static final String STORE_TYPE_CUSTOMER = "customer";
    public static final int STORE_CATEGORY_CODE_PLATINUM = 1;
    public static final int STORE_CATEGORY_CODE_GOLD = 2;
    public static final int STORE_CATEGORY_CODE_SILVER = 3;

    public static final String STORE_CATEGORY_LABEL_PLATINUM = "Platinum";
    public static final String STORE_CATEGORY_LABEL_GOLD = "Gold";
    public static final String STORE_CATEGORY_LABEL_SILVER = "Silver";

    public static final String STORE_STATUS_UNVERIFIED = "unverified";
    public static final String STORE_STATUS_VERIFIED = "verified";
    public static final String STORE_STATUS_ACTIVE = "active";

    public static final String TOKEN_KEY = "session";

    public static final int ERROR_CODE_INVALID_LOGIN = 103;
    public static final int ERROR_CODE_SESSION_EXPIRED = 105;
    public static final int ERROR_CODE_DATA_NOT_FOUND = 116;
    public static final int ERROR_CODE_UNAUTHORIZED = 116;
    public static final int ERROR_CODE_PASSWORD_NO_MATCH = 132;

    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_DETAIL = "detail";
    public static final String ACTION_CHECK_IN = "check_in";
    public static final String ACTION_CHECK_OUT = "check_out";
    public static final String ACTION_AGENDA = "agenda";
    public static final String ACTION_ORDER = "order";
    public static final String ACTION_SURVEY = "survey";
    public static final String ACTION_CAMERA = "camera";
    public static final String ACTION_GALLERY = "gallery";
    public static final int ACTION_ADD = 0;
    public static final int ACTION_EDIT = 1;

    public static final String TAG_ACTION_BUTTONS = "action_buttons";
    public static final String TAG_LOADING = "loading";

    public static final String EXTRA_STORE = "store";
    public static final String EXTRA_ORDER = "order";
    public static final String EXTRA_SURVEY = "survey";
    public static final String EXTRA_ACTION = "action";
    public static final String EXTRA_STORE_NAME = "store_name";
    public static final String EXTRA_STORE_DB_ID = "store_db_id";
    public static final String EXTRA_STORE_ID = "store_id";
    public static final String EXTRA_AGENDA_ID = "agenda_id";
    public static final String EXTRA_LOCATION = "location";
    public static final String EXTRA_AGENDA_DATES = "agenda_dates";
    public static final String EXTRA_SYNC_PROGRESS = "sync_progress";

    public static final boolean IS_ALLOW_SURVEY_WITHOUT_CHECKIN = false;
    public static final double MAX_ALLOWED_DISTANCE = 1000;
    public static final int MAX_IMAGE_COUNT = 3;

    public static final String ROLE_NAME_SALES = "sales";
    public static final String ROLE_NAME_AM = "am";//area manager
//    public static final String ROLE_NAME_HOLDING = "holding";
//    public static final String ROLE_NAME_EXECUTIVE = "executive";

    public static final int REQ_CODE_ADD_STORE = 1;
    public static final int REQ_CODE_GALLERY = 2;
    public static final int REQ_CODE_CAMERA = 3;
    public static final int REQ_CODE_LOCATION = 4;

    public static final String PRODUCT_PACKAGE_40 = "40";
    public static final String PRODUCT_PACKAGE_50 = "50";

    public static final int FLAG_TRUE = 1;
    public static final int FLAG_FALSE = 0;

    public static final String UNIT_TON = "ton";
    public static final String UNIT_SAK = "sak";

    public static final int DIP_HEIGHT_PROFILE_PICTURE = 100;
    public static final int DIP_WIDTH_PROFILE_PICTURE = 70;
    public static final int DIP_HEIGHT_ITEM_THUMB = 300;
    public static final int DIP_WIDTH_ITEM_THUMB = 400;
    public static final int DIP_HEIGHT_ITEM_LARGE = 512;
    public static final int DIP_WIDTH_ITEM_LARGE = 512;

    public static final String INTENT_ACTION_IMAGE_DOWNLOADED =
            "com.sinergiinformatika.sisicrm.IMAGE_DOWNLOADED";
    public static final String INTENT_ACTION_SYNC = "com.sinergiinformatika.sisicrm.SYNC";
    public static final String INTENT_ACTION_PUSH = "com.sinergiinformatika.sisicrm.PUSH";

    public static final String IMAGE_ID_EXTRA = "image_id";
    public static final String IMAGE_TYPE_EXTRA = "image_type";
    public static final String IMAGE_EXTRA_ID_EXTRA = "image_extra_id";
    public static final String IMAGE_FILENAME = "image_path";

    public static final int ZIP_CODE_LENGTH = 5;

    public static final String SYNC_STATUS_PENDING = "pending";
    public static final String SYNC_STATUS_SENDING = "sending";
    public static final String SYNC_STATUS_SENT = "sent";
    public static final String SYNC_STATUS_FAILED = "failed";

    public static final int SYNC_PROGRESS_FINISHED = 0;
    public static final int SYNC_PROGRESS_STARTED = 1;

    public static final String DATA_DOWNLOADED_INTENT_ACTION = "com.sinergiinformatika.sisicrm.DATA_DOWNLOADED";
    public static final String PROGRESS_COUNTER_ID_EXTRA = "progress_counter";
    public static final String DOWNLOAD_ERROR_ID_EXTRA = "download_error";
    public static final String DOWNLOAD_ERROR_MESSAGE_EXTRA = "download_error_message";

    public static final int PASSWORD_MIN_LENGTH = 5;

    public static final int PROGRESS_STATUS_START = 0;
    public static final int PROGRESS_STATUS_FINISH = 8;

    public static final String HISTORY_FORMAT_DATE = "d MMM yyyy HH:mm";
    public static final int REQ_CODE_STORE_DETAIL = 5;

    public static final String NOTE_TYPE_COMPLAIN = "complain_note";
    public static final String NOTE_TYPE_COMPETITOR = "competitor_note";
    public static final String DATA_UPLOADED_INTENT_ACTION = "com.sinergiinformatika.sisicrm.DATA_UPLOADED";

    public static final String SYNC_KEY_PERIODIC = "periodic_sync";

    public static int HISTORY_DAYS_AGO_FOR_SALES = 30;
    public static int HISTORY_DAYS_AGO_FOR_AREA_MANAGER = 30;
    public static final String PREFERENCES_NAME = "crmPrefs";

}

