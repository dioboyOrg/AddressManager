package com.example.diotek.addressmanager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by diotek on 2015-07-28.
 */
public class Upload implements BaseVariables {

    private static final String TAG = "Upload";

    private GoogleApiClient mGoogleApiClient;
    private Context mContext;
    private File mDatabaseFile;
    private TextView mCloudTextView;
    private int mUpload;

    public Upload(Context context, GoogleApiClient googleApiClient, File databaseFile, TextView cloudTextView, int upload) {
        mContext = context;
        mGoogleApiClient = googleApiClient;
        mDatabaseFile = databaseFile;
        mCloudTextView = cloudTextView;
        mUpload = upload;

        if(upload == UPLOAD) {
            mCloudTextView.setText(UPLOAD_ING_MESSAGE);
        } else {
            mCloudTextView.setText(SYNCHRONIZATION_ING_MESSAGE);
        }

        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, CREATE_FILE_NAME))
                .build();
        Drive.DriveApi.query(mGoogleApiClient, query)
                .setResultCallback(uploadMetadataCallback);
    }

    final private ResultCallback<DriveApi.MetadataBufferResult> uploadMetadataCallback = new
            ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        mCloudTextView.setText("Problem while retrieving results");
                        return;
                    }
                    Metadata metadata = getMetaData(result.getMetadataBuffer());
                    if(metadata == null) {
                        // create new contents resource
                        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                                .setResultCallback(uploadNewDriveContentsCallback);
                    }
                    else {
                        //showMessage("Exist File!!!");
                        Drive.DriveApi.fetchDriveId(mGoogleApiClient,
                                metadata.getDriveId().getResourceId())
                                .setResultCallback(uploadEditIdCallback);
                    }
                }
            };

    final private ResultCallback<DriveApi.DriveIdResult> uploadEditIdCallback = new ResultCallback<DriveApi.DriveIdResult>() {
        @Override
        public void onResult(DriveApi.DriveIdResult result) {
            DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, result.getDriveId());
            new EditContentsAsyncTask(mContext).execute(file);
        }
    };

    public class EditContentsAsyncTask extends ApiClientAsyncTask<DriveFile, Void, Boolean> {

        public EditContentsAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected Boolean doInBackgroundConnected(DriveFile... args) {
            DriveFile file = args[0];
            FileInputStream inputStream = null;
            OutputStream outputStream = null;
            byte[] memoData  = null;
            try {
                DriveApi.DriveContentsResult driveContentsResult = file.open(
                        getGoogleApiClient(), DriveFile.MODE_WRITE_ONLY, null).await();
                if (!driveContentsResult.getStatus().isSuccess()) {
                    return false;
                }
                DriveContents driveContents = driveContentsResult.getDriveContents();

                inputStream = new FileInputStream(mDatabaseFile);
                outputStream = driveContents.getOutputStream();
                memoData = new byte[inputStream.available()];

                while (inputStream.read(memoData) != -1) {
                    outputStream.write(memoData);
                }

                com.google.android.gms.common.api.Status status =
                        driveContents.commit(getGoogleApiClient(), null).await();
                return status.getStatus().isSuccess();

            } catch (IOException e) {
                Log.e(TAG, "IOException while reading from the stream", e);
                return false;
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                if(mUpload == UPLOAD) {
                    mCloudTextView.setText(UPLOAD_FAIL_MESSAGE);
                } else {
                    mCloudTextView.setText(SYNCHRONIZATION_FAIL_MESSAGE);
                }
                return;
            }
            if(mUpload == UPLOAD) {
                mCloudTextView.setText(UPLOAD_SUCCESS_MESSAGE);
            } else {
                mCloudTextView.setText(SYNCHRONIZATION_SUCCESS_MESSAGE);
            }
        }
    }

    final private ResultCallback<DriveApi.DriveContentsResult> uploadNewDriveContentsCallback
            = new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        if(mUpload == UPLOAD) {
                            mCloudTextView.setText(UPLOAD_FAIL_MESSAGE);
                        } else {
                            mCloudTextView.setText(SYNCHRONIZATION_FAIL_MESSAGE);
                        }
                        return;
                    }

                    final DriveContents driveContents = result.getDriveContents();
                    // Perform I/O off the UI thread.
                    new Thread() {
                        @Override
                        public void run() {
                            // write content to DriveContents
                            FileInputStream inputStream = null;
                            OutputStream outputStream = null;
                            try {
                                inputStream = new FileInputStream(mDatabaseFile);
                                outputStream = driveContents.getOutputStream();

                                byte[] memoData = new byte[inputStream.available()];
                                while (inputStream.read(memoData) != -1) {
                                    outputStream.write(memoData);
                                }
                            } catch (IOException e) {
                                Log.e(TAG, e.getMessage());
                            } finally {
                                try {
                                    if (inputStream != null) {
                                        inputStream.close();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    if (outputStream != null) {
                                        outputStream.close();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            String mimeType = MimeTypeMap.getSingleton()
                                    .getExtensionFromMimeType("db");
                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle(CREATE_FILE_NAME)
                                    .setMimeType(mimeType)
                                    .setStarred(true).build();

                            // create a file on root folder
                            Drive.DriveApi.getRootFolder(mGoogleApiClient)
                                    .createFile(mGoogleApiClient, changeSet, driveContents)
                                    .setResultCallback(uploadFileCallback);
                        }
                    }.start();
                }
            };

    final private ResultCallback<DriveFolder.DriveFileResult> uploadFileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        if(mUpload == UPLOAD) {
                            mCloudTextView.setText(UPLOAD_FAIL_MESSAGE);
                        } else {
                            mCloudTextView.setText(SYNCHRONIZATION_FAIL_MESSAGE);
                        }
                        return;
                    }
                    if(mUpload == UPLOAD) {
                        mCloudTextView.setText(UPLOAD_SUCCESS_MESSAGE);
                    } else {
                        mCloudTextView.setText(SYNCHRONIZATION_SUCCESS_MESSAGE);
                    }
                }
            };

    public Metadata getMetaData(MetadataBuffer metadataBuffer){
        Metadata metadata = null;
        for(int i = 0; i< metadataBuffer.getCount(); i++){
            metadata = metadataBuffer.get(i);
            if(metadata.getTitle().equals(CREATE_FILE_NAME)){
                break;
            }
        }
        return metadata;
    }

}
