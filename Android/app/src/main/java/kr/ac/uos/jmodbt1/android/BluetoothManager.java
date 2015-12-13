package kr.ac.uos.jmodbt1.android;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by jiwon on 2015-10-21.
 */
public class BluetoothManager {
    private static final String tag = BluetoothManager.class.getName();

    public static final UUID POMEAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    BluetoothAdapter mBluetoothAdatper = BluetoothAdapter.getDefaultAdapter();

    private Context mContext;
    public BluetoothManager(Context context){
        this.mContext = context;
    }

    useCallback callback = null;
    public void setCallback(useCallback callback) {
        this.callback = callback;
    }

    public void removeCallback() {
        callback = null;
    }

    public ArrayList<BluetoothDevice> getDevcies() {
        ArrayList<BluetoothDevice> list = new ArrayList<>();
        Set<BluetoothDevice> devices = mBluetoothAdatper.getBondedDevices();
        for (BluetoothDevice device : devices) {
            list.add(device);
        }
        return list;
    }

    public boolean connect(BluetoothDevice device) {
        if (device == null) return false;

        try {
            mBluetoothSocket = device.createRfcommSocketToServiceRecord(POMEAL_UUID);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public BluetoothSocket getBluetoothSocket(){
        return mBluetoothSocket;
    }

    public boolean isEanbled() {
        return (mBluetoothThread != null);
    }

    public void start() {
        if (mBluetoothThread != null) {
            tearDown();
        }
        mBluetoothThread = new BluetoothThread();
        mBluetoothThread.start();
    }

    public void tearDown() {
        if (mBluetoothThread != null) mBluetoothThread.tearDown();
        mBluetoothThread = null;
    }


    Timer timer = null;
    void cancelTimer(){
        if(timer!=null)timer.cancel();
        timer = null;
    }

    void startRepeatTimer(TimerTask task){
        cancelTimer();
        timer = new Timer();
        timer.schedule(task,100,100);
    }

    public void sendMessage(final int msg) {
        if(isEanbled()==false){
            Log.i(tag, "isEnabled false");
            start();
        }

        TimerTask task = new TimerTask(){
            @Override
            public void run() {
                if(mBluetoothThread!=null)mBluetoothThread.sendMessage(msg);
            }
        };

        startRepeatTimer(task);
    }

    BluetoothThread mBluetoothThread = null;
    BluetoothSocket mBluetoothSocket = null;
    PrintWriter mPrintWriter = null;
    BufferedReader mBufferedReader = null;

    class BluetoothThread extends Thread {
        @Override
        public void run() {
            Log.i(tag, "start client");
            if (getBluetoothSocket() != null) {
                try {
                    mBluetoothSocket.connect();
                    mPrintWriter = new PrintWriter(mBluetoothSocket.getOutputStream());
                    mBufferedReader = new BufferedReader(new InputStreamReader(mBluetoothSocket.getInputStream()));

                    while (!isInterrupted()) {
                        int msg = mBufferedReader.read();
                        Log.i(tag, "receive data : " + msg);
                        cancelTimer();
                        if (callback != null) callback.receive(msg);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            tearDown();
        }

        public void tearDown() {
            Log.i(tag, "tearDown client");
            if (mBluetoothSocket != null) {
                try {
                    mBluetoothSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mBufferedReader = null;
                mPrintWriter = null;
                mBluetoothSocket = null;
                interrupt();
            }
            mBluetoothThread = null;
        }

        Handler handler = new Handler(Looper.getMainLooper());
        synchronized public void sendMessage(final int msg) {
            Log.i(tag, "sendMessage1 : " + msg);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if (mPrintWriter == null) return;
                    mPrintWriter.write(msg);
                    mPrintWriter.flush();
                    Log.i(tag, "sendMessage2 : " + msg);
                }
            });
        }

    }

    interface useCallback {
        void receive(int msg);
    }
}
