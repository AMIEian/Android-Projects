package sysnotion.tagid.tagsmart.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import java.io.File;

public class FileScanning implements MediaScannerConnection.MediaScannerConnectionClient {

    private MediaScannerConnection mMsc;

    private File mScanFile;

    public FileScanning(Context mContext, File targetFile) {
        mScanFile = targetFile;
        mMsc = new MediaScannerConnection(mContext, this);
        mMsc.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        mMsc.scanFile(mScanFile.getAbsolutePath(), null);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        mMsc.disconnect();
    }
}
