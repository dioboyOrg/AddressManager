package com.example.diotek.addressmanager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by diotek on 2015-07-21.
 */
public class ModifyAddressActivity extends Activity implements BaseVariables{
    public DBManage mDbManager = null;
    Button individualCancelBtn = null;
    Button individualSaveBtn = null;

    EditText mModifyNameEdit = null;
    EditText mModifyPhoneNumberEdit = null;
    EditText mModifyHomeNumberEdit = null;
    EditText mModifyCompanyNumberEdit = null;
    EditText mModifyEmailEdit = null;

    Intent mIntent = null;
    Bundle mBundleData = null;

    long mModifyAddressIndex = 0;
    int mModifyAddressPosition = 0;

    ContentValues mAddRowValue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertaddress);

        mDbManager = DBManage.getInstance(this);

        mIntent = getIntent();
        mBundleData = mIntent.getBundleExtra(MODIFY_DATA_KEY);
        mModifyAddressIndex = mBundleData.getLong(MODIFY_INDEX_KEY);
        mModifyAddressPosition = mBundleData.getInt(MODIFY_POSITION_KEY);

        individualCancelBtn = (Button) findViewById(R.id.individualCancelButton);
        individualSaveBtn = (Button) findViewById(R.id.individualSaveButton);

        individualSaveBtn.setOnClickListener(mModifySaveClickListener);
        individualCancelBtn.setOnClickListener(mModifyCancelClickListener);

        mModifyNameEdit = (EditText) findViewById(R.id.insertAddressNameEdit);
        mModifyPhoneNumberEdit = (EditText) findViewById(R.id.insertAddressPhoneNumberEdit);
        mModifyHomeNumberEdit = (EditText) findViewById(R.id.insertAddressHomeNumberEdit);
        mModifyCompanyNumberEdit = (EditText) findViewById(R.id.insertAddressCompanyNumberEdit);
        mModifyEmailEdit = (EditText) findViewById(R.id.insertAddressEmailEdit);

        mModifyNameEdit.setText(mBundleData.getString(MODIFY_NAME_KEY));
        mModifyPhoneNumberEdit.setText(mBundleData.getString(MODIFY_PHONE_NUMBER_KEY));
        mModifyHomeNumberEdit.setText(mBundleData.getString(MODIFY_HOME_NUMBER_KEY));
        mModifyCompanyNumberEdit.setText(mBundleData.getString(MODIFY_COMPANY_NUMBER_KEY));
        mModifyEmailEdit.setText(mBundleData.getString(MODIFY_E_MAIL_KEY));
    }

    private View.OnClickListener mModifySaveClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Bundle modifiedBundleData = new Bundle();

            String name = mModifyNameEdit.getText().toString();
            String phone_number =  mModifyPhoneNumberEdit.getText().toString();
            String home_number = mModifyHomeNumberEdit.getText().toString();
            String company_number = mModifyCompanyNumberEdit.getText().toString();
            String e_mail = mModifyEmailEdit.getText().toString();

            mAddRowValue = new ContentValues();

            mAddRowValue.put(DB_NAME,name);
            mAddRowValue.put(DB_PHONE_NUMBER,phone_number);
            mAddRowValue.put(DB_HOME_NUMBER, home_number);
            mAddRowValue.put(DB_COMPANY_NUMBER, company_number);
            mAddRowValue.put(DB_E_MAIL, e_mail);

            mDbManager.update(mAddRowValue, DB_INDEX + "=" + mModifyAddressIndex, null);

            modifiedBundleData.putString(MODIFIED_NAME_KEY, name);
            modifiedBundleData.putString(MODIFIED_PHONE_NUMBER_KEY,phone_number);
            modifiedBundleData.putString(MODIFIED_HOME_NUMBER_KEY,home_number);
            modifiedBundleData.putString(MODIFIED_COMPANY_NUMBER_KEY,company_number);
            modifiedBundleData.putString(MODIFIED_E_MAIL_KEY,e_mail);

            mIntent.putExtra(MODIFIED_DATA_KEY, modifiedBundleData);
            setResult(MODIFY_RESULT_OK, mIntent);

            finish();
        }
    };

    private View.OnClickListener mModifyCancelClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            finish();
        }
    };
}
