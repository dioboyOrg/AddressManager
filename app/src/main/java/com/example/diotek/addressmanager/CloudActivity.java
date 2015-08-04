package com.example.diotek.addressmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;

/**
 * Created by diotek on 2015-07-28.
 */
public class CloudActivity extends BaseDemoActivity implements BaseVariables {
    private static final String TAG = "CloudActivity";

    Button mBackBtn = null;
    Button mUpLoadBtn = null;
    Button mDownLoadBtn = null;
    Button mSynchronizationBtn = null;
    Button mDeleteBtn = null;
    TextView mCloudText = null;

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud);

        mCloudText = (TextView)findViewById(R.id.cloudTextView);
        mBackBtn = (Button)findViewById(R.id.backButton);
        mUpLoadBtn = (Button)findViewById(R.id.uploadButton);
        mDownLoadBtn = (Button)findViewById(R.id.downloadButton);
        mSynchronizationBtn = (Button)findViewById(R.id.synchronizationButton);
        mDeleteBtn = (Button)findViewById(R.id.deleteButton);

        mBackBtn.setOnClickListener(mBackClickListener);
        mUpLoadBtn.setOnClickListener(mUpLoadClickListener);
        mDownLoadBtn.setOnClickListener(mDownLoadClickListener);
        mSynchronizationBtn.setOnClickListener(mSynchronizationClickListener);
        mDeleteBtn.setOnClickListener(mDeleteClickListener);
    }

    private View.OnClickListener mUpLoadClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new Upload(
                    CloudActivity.this, getGoogleApiClient(), getDatabasePath(DB_ADDRESS), mCloudText, UPLOAD);
        }
    };

    private View.OnClickListener mDownLoadClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new Download(
                    CloudActivity.this, getGoogleApiClient(), getDatabasePath(DB_ADDRESS), mCloudText, DOWNLOAD );
        }
    };

    private View.OnClickListener mSynchronizationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new Synchronization(
                    CloudActivity.this, getGoogleApiClient(), getDatabasePath(DB_ADDRESS),mCloudText);
        }
    };

    private View.OnClickListener mDeleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new DeleteFilesInDrive(getGoogleApiClient(), mCloudText);
        }
    };

    private View.OnClickListener mBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setResult(CLOUD_RESULT_OK);
            finish();
        }
    };

}
