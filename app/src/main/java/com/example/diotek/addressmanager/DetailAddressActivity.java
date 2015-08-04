package com.example.diotek.addressmanager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by diotek on 2015-07-21.
 */
public class DetailAddressActivity extends Activity implements BaseVariables {
    public DBManage mDbManager = null;

    Button mBackBtn = null;
    Button mModifyBtn = null;
    Button mDeleteBtn = null;

    TextView mNameText = null;
    TextView mPhoneNumberText =null;
    TextView mHomeNumberText = null;
    TextView mCompanyNumberText = null;
    TextView mEmailText = null;

    String mDetailName = null;
    String mDetailPhoneNumber = null;
    String mDetailHomeNumber = null;
    String mDetailCompanyNumber = null;
    String mDetailEmail = null;

    Intent mIntent = null;
    Bundle mBundleData = null;
    long mDetailAddressIndex = 0;
    int mDetailAddressPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailaddress);

        mDbManager = DBManage.getInstance(this);
        mBackBtn = (Button)findViewById(R.id.backButton);
        mModifyBtn = (Button)findViewById(R.id.modifyButton);
        mDeleteBtn = (Button)findViewById(R.id.deleteButton);

        mNameText = (TextView)findViewById(R.id.addressNameText);
        mPhoneNumberText = (TextView)findViewById(R.id.addressPhoneNumberText);
        mHomeNumberText = (TextView)findViewById(R.id.addressHomeNumberText);
        mCompanyNumberText = (TextView)findViewById(R.id.addressCompanyNumberText);
        mEmailText = (TextView)findViewById(R.id.addressEmailText);

        mBackBtn.setOnClickListener(mBackClickListener);
        mModifyBtn.setOnClickListener(mModifyClickListener);
        mDeleteBtn.setOnClickListener(mDeleteClickListener);

        mIntent = getIntent();
        mBundleData = mIntent.getBundleExtra(LIST_INDIVIDUAL_DATA_KEY);
        mDetailAddressIndex = mBundleData.getLong(LIST_INDEX_KEY);
        mDetailAddressPosition = mBundleData.getInt(LIST_POSITION_KEY);

        setDetailAddress();
    }

    private void setDetailAddress (){
        Cursor c = mDbManager.query(COLUMNS, null, null, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                long index = c.getLong(c.getColumnIndex(DB_INDEX));
                if(index == mDetailAddressIndex) {
                    mDetailName = c.getString(c.getColumnIndex(DB_NAME));
                    mNameText.setText(mDetailName);

                    mDetailPhoneNumber = c.getString(c.getColumnIndex(DB_PHONE_NUMBER));
                    mPhoneNumberText.setText(mDetailPhoneNumber);

                    mDetailHomeNumber = c.getString(c.getColumnIndex(DB_HOME_NUMBER));
                    mHomeNumberText.setText(mDetailHomeNumber);

                    mDetailCompanyNumber = c.getString(c.getColumnIndex(DB_COMPANY_NUMBER));
                    mCompanyNumberText.setText(mDetailCompanyNumber);

                    mDetailEmail = c.getString(c.getColumnIndex(DB_E_MAIL));
                    mEmailText.setText(mDetailEmail);

                    break;
                }
            }
        }
    }

    private View.OnClickListener mBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private View.OnClickListener mModifyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setDetailAddress();
            Bundle modifyBundleData = new Bundle();

            modifyBundleData.putLong(MODIFY_INDEX_KEY,mDetailAddressIndex);
            modifyBundleData.putInt(MODIFY_POSITION_KEY,mDetailAddressPosition);
            modifyBundleData.putString(MODIFY_NAME_KEY,mDetailName);
            modifyBundleData.putString(MODIFY_PHONE_NUMBER_KEY,mDetailPhoneNumber);
            modifyBundleData.putString(MODIFY_HOME_NUMBER_KEY, mDetailHomeNumber);
            modifyBundleData.putString(MODIFY_COMPANY_NUMBER_KEY, mDetailCompanyNumber);
            modifyBundleData.putString(MODIFY_E_MAIL_KEY, mDetailEmail);
            mIntent.putExtra(MODIFY_DATA_KEY,modifyBundleData);

            mIntent.setClass(DetailAddressActivity.this, ModifyAddressActivity.class);
            startActivityForResult(mIntent, MODIFY_ADDRESS_ACTIVITY);
        }
    };

    private View.OnClickListener mDeleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mDbManager.delete( DB_INDEX + "=" + mDetailAddressIndex, null);
            setResult(DETAIL_RESULT_OK, mIntent);
            finish();
        }
    };

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent intent){
        if(requestCode == MODIFY_ADDRESS_ACTIVITY){
            if(resultCode == MODIFY_RESULT_OK){
                Bundle bundleData = intent.getBundleExtra(MODIFIED_DATA_KEY);

                mNameText.setText(bundleData.getString(MODIFIED_NAME_KEY));
                mPhoneNumberText.setText(bundleData.getString(MODIFIED_PHONE_NUMBER_KEY));
                mHomeNumberText.setText(bundleData.getString(MODIFIED_HOME_NUMBER_KEY));
                mCompanyNumberText.setText(bundleData.getString(MODIFIED_COMPANY_NUMBER_KEY));
                mEmailText.setText(bundleData.getString(MODIFIED_E_MAIL_KEY));

                setResult(DETAIL_RESULT_OK, mIntent);
            }
        }
    }
}
