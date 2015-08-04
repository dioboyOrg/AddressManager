package com.example.diotek.addressmanager;

import java.util.Comparator;

/**
 * Created by diotek on 2015-07-22.
 */
public interface BaseVariables {

    public static final String CREATE_FILE_NAME = "Address";
    public static final String DB_ADDRESS = "Address.db";
    public static final String TABLE_ADDRESS = "Address";
    public static final int DB_VERSION = 1;

    public static final String DB_INDEX = "_index";
    public static final String DB_NAME = "name";
    public static final String DB_PHONE_NUMBER = "phone_number";
    public static final String DB_HOME_NUMBER = "home_number";
    public static final String DB_COMPANY_NUMBER = "company_number";
    public static final String DB_E_MAIL = "e_mail";

    public static final int INSERT_ADDRESS_ACTIVITY = 0;
    public static final int DETAIL_ADDRESS_ACTIVITY = 1;
    public static final int MODIFY_ADDRESS_ACTIVITY = 2;
    public static final int DELETE_ADDRESS_ACTIVITY = 3;
    public static final int CLOUD_ACTIVITY = 4;

    public static final int INSERT_RESULT_OK = -2;
    public static final int DELETE_RESULT_OK = -3;
    public static final int DETAIL_RESULT_OK = -4;
    public static final int MODIFY_RESULT_OK = -5;
    public static final int CLOUD_RESULT_OK = -6;

    public static final String MY_ADDRESS = "내 연락처";
    public static final String LIST_INDEX_KEY = "individualIndex";
    public static final String LIST_POSITION_KEY = "individualPosition";
    public static final String LIST_INDIVIDUAL_DATA_KEY = "individualData";

    public static final String MODIFY_DATA_KEY = "modifyData";
    public static final String MODIFY_INDEX_KEY = "modifyIndex";
    public static final String MODIFY_POSITION_KEY = "modifyPosition";
    public static final String MODIFY_NAME_KEY = "modifyName";
    public static final String MODIFY_PHONE_NUMBER_KEY = "modifyPhoneNumber";
    public static final String MODIFY_HOME_NUMBER_KEY = "modifyHomeNumber";
    public static final String MODIFY_COMPANY_NUMBER_KEY = "modifyCompanyNumber";
    public static final String MODIFY_E_MAIL_KEY = "modifyEmail";

    public static final String MODIFIED_DATA_KEY = "modifiedData";
    public static final String MODIFIED_NAME_KEY = "modifiedName";
    public static final String MODIFIED_PHONE_NUMBER_KEY = "modifiedPhoneNumber";
    public static final String MODIFIED_HOME_NUMBER_KEY = "modifiedHomeNumber";
    public static final String MODIFIED_COMPANY_NUMBER_KEY = "modifiedCompanyNumber";
    public static final String MODIFIED_E_MAIL_KEY = "modifiedEmail";

    public static final String INSERT_DATA_KEY = "insertData";
    public static final String INSERT_INDEX_KEY = "insertIndex";
    public static final String INSERT_NAME_KEY = "insertName";

    public static final int UPLOAD = 0;
    public static final String UPLOAD_ING_MESSAGE = "업로드 중...";
    public static final String UPLOAD_SUCCESS_MESSAGE = "업로드 완료";
    public static final String UPLOAD_FAIL_MESSAGE = "업로드 실패";

    public static final int DOWNLOAD = 0;
    public static final String DOWNLOAD_ING_MESSAGE = "다운로드 중...";
    public static final String DOWNLOAD_SUCCESS_MESSAGE = "다운로드 완료";
    public static final String DOWNLOAD_FAIL_MESSAGE = "다운로드 실패";

    public static final int SYNCHRONIZATION = 1;
    public static final String SYNCHRONIZATION_ING_MESSAGE = "동기화 중...";
    public static final String SYNCHRONIZATION_SUCCESS_MESSAGE = "동기화 완료";
    public static final String SYNCHRONIZATION_FAIL_MESSAGE = "동기화 실패";

    public static final String THERE_IS_NO_FILE_IN_DRIVE = "Drive에 파일이 없습니다.";

    public static final String DELETE_ING_MESSAGE = "삭제 중...";
    public static final String DELETE_SUCCESS_MESSAGE = "삭제 완료";
    public static final String DELETE_FAIL_MESSAGE = "삭제 실패";

    public static final String[] COLUMNS = new String[] {"_index", "name","phone_number","home_number","company_number", "e_mail"};

    public class Item{
        Item(long aIndex, String aName){
            mIndex = aIndex;
            mName = aName;
        }

        public long getmIndex() {
            return mIndex;
        }

        public String getmName() {
            return mName;
        }

        private long mIndex;
        private String mName;
    }

    public class AddressNameCompare implements Comparator<Item> {
        @Override
        public int compare(Item arg0, Item arg1) {
            return arg0.getmName().compareTo(arg1.getmName());
        }
    }
}
