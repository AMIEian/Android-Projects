package sysnotion.tagid.tagsmart.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

public class FileManager {

    private static final String TAG = FileManager.class.getSimpleName();

    private static final int MINIMUM_SPACE_MB_SIZE = 100;

    private static final long SIZE_KB = 1024L;

    private static final long SIZE_MB = SIZE_KB * SIZE_KB;

    private File mDir;

    private File mFile;

    private PrintWriter mWriter;

    private Context mContext;

    private CopyOnWriteArrayList<String> mTagList;

    private Thread mWriteThread;

    private boolean mDoLoop;

    private boolean forceStop;

    private long mAvailableSpace;

    private StatFs mStatFs;

    public FileManager(Context ctx) {
        mContext = ctx;
    }

    public String getCSVFilePath()
    {
        String storage = Environment.getExternalStorageDirectory().getPath();
        String path = storage + Constants.mLogDir+Constants.mLogFileName  + Constants.mExtentionName;
        return  path;
    }

    public boolean openFile() {
        closeFile();
        mStatFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        if (!isWriteAvailable()) {
            Toast.makeText(mContext, "File Write : Your storage is running out of space.\nNeed " +
                    "over than 100MB free space.", Toast.LENGTH_SHORT).show();
            return false;
        }
        makeDir();

        mFile = new File(mDir, Constants.mLogFileName  + Constants.mExtentionName);
        //clear the content of file
        if (mFile.exists()) {
            RandomAccessFile raf = null;
            try {
                raf = new RandomAccessFile(mFile, "rw");
                raf.setLength(0);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (readyToWrite()) {
            clearTagList();
            mTagList = new CopyOnWriteArrayList<>();
            mDoLoop = true;
            forceStop = false;
            mWriter.println();
            mWriteThread = new Thread() {
                @Override
                public void run() {
                    while (!forceStop && (mDoLoop || (mTagList != null && mTagList.size() != 0))) {
                        if (mTagList.size() > 0) {
                            for (String tag : mTagList) {
                                try {
                                    mWriter.println(tag + ",");
                                    mTagList.remove(tag);
                                } catch (java.lang.Exception e) {
                                    Log.e(TAG, "Exception");
                                    forceStop = true;
                                    break;
                                }
                            }
                        }
                        if (!isWriteAvailable()) {
                            forceStop = true;
                            Log.e(TAG, "File Write : Your storage is running out of space.\nNeed " +
                                    "over than 100MB free space.");
                            break;
                        }
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            Log.e(TAG, "InterruptedException");
                        }
                    }
                }
            };
            mWriteThread.setName("FileWriteThread");
            mWriteThread.start();
            return true;
        }
        return false;
    }

    private void clearTagList() {
        if (mTagList != null) {
            mTagList.clear();
            mTagList = null;
        }
    }

    public void writeToFile(String msg) {
        if (mFile != null && mWriter != null) {
            mTagList.add(msg);
        }
    }

    public void closeFile() {
        mDoLoop = false;
        if (mWriter != null) {
            mWriter.flush();
            mWriter.close();
            mWriter = null;
        }
        if (mFile != null) {
            FileScanning scanning = new FileScanning(mContext, mFile);
            mFile = null;
        }
        clearTagList();
    }

    private void makeDir() {
        String storage = mContext.getExternalFilesDir(null).getAbsolutePath();
        mDir = new File(storage + Constants.mLogDir);

        if (!mDir.exists())
        {
            if( mDir.mkdirs())
            {
                Log.e(TAG, "path = " + storage + Constants.mLogDir);
            }
            else
            {
                Log.e(TAG, "Directory not created");
            }
        }

    }

    private boolean readyToWrite() {
        try {
            mWriter = new PrintWriter(new BufferedWriter(new FileWriter(mFile, true), 1024), true);
        } catch (Exception e) {
            Log.e(TAG, "Exception = " + e.toString());
            return false;
        }
        return true;
    }

    private boolean isWriteAvailable() {
        if (getAvailableSpaceInMB() > MINIMUM_SPACE_MB_SIZE)
            return true;
        return false;
    }

    private long getAvailableSpaceInMB(){
        mAvailableSpace = mStatFs.getAvailableBlocksLong() * mStatFs.getBlockSizeLong();
        return mAvailableSpace / SIZE_MB;
    }
}


