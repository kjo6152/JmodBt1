package kr.ac.uos.jmodbt1.android;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
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

    public static final UUID JBT_MOD_1_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    BluetoothAdapter mBluetoothAdatper = BluetoothAdapter.getDefaultAdapter();

    private Context mContext;

    public BluetoothManager(Context context) {
        this.mContext = context;
    }

    /**
     * 연결 가능한 디바이스 목록을 리턴한다.
     *
     * @return
     */
    public ArrayList<BluetoothDevice> getDevcies() {
        ArrayList<BluetoothDevice> list = new ArrayList<>();
        Set<BluetoothDevice> devices = mBluetoothAdatper.getBondedDevices();
        for (BluetoothDevice device : devices) {
            list.add(device);
        }
        return list;
    }

    /** 별도의 스레드에서 연결시도 */
    static Thread connectThread = null;

    /** 연결이 되었을 때 호출되는 콜백 인터페이스 */
    interface BluetoothCallback {
        void connect(boolean ret);
    }

    /**
     * 블루투스 디바이스와 연결한다.
     *
     * @param device
     * @return
     */
    public void connect(final BluetoothDevice device, final BluetoothCallback callback) {
        if (device == null) {
            callback.connect(false);
            return;
        }

        if (connectThread != null) {
            Toast.makeText(mContext, "연결중입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        connectThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mBluetoothSocket = device.createRfcommSocketToServiceRecord(JBT_MOD_1_UUID);
                    mBluetoothSocket.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.connect(false);
                    connectThread = null;
                    return;
                }

                callback.connect(true);
            }
        });

        connectThread.start();
    }

    /** 통신이 가능한 상태인지 확인한다. */
    public boolean isEanbled() {
        return (mBluetoothThread != null);
    }

    /** 블루투스 통신을 시작한다. */
    public void start() {
        if (mBluetoothThread != null) {
            tearDown();
        }
        mBluetoothThread = new BluetoothThread();
        mBluetoothThread.start();
    }

    /** 블루투스 통신을 종료한다. */
    public void tearDown() {
        if (mBluetoothThread != null) mBluetoothThread.tearDown();
        mBluetoothThread = null;
    }


    Timer timer = null;

    void cancelTimer() {
        if (timer != null) timer.cancel();
        timer = null;
    }

    void startRepeatTimer(TimerTask task) {
        cancelTimer();
        timer = new Timer();
        timer.schedule(task, 100, 100);
    }

    /**
     * 메시지 송신을 위한 함수
     * 통신의 신뢰성을 위해 응답이 올때까지 100ms마다 다시 보낸다.
     *
     * @param msg
     */
    public void sendMessage(final int msg) {
        if (isEanbled() == false) {
            Log.i(tag, "isEnabled false");
            start();
        }

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (mBluetoothThread != null) mBluetoothThread.sendMessage(msg);
            }
        };

        startRepeatTimer(task);
    }

    BluetoothSocket mBluetoothSocket = null;
    BluetoothThread mBluetoothThread = null;

    /**
     * 블루투스 기기와 통신하기 위한 스레드
     * 블루투스 기기와 연결 후 데이터 수신을 위해 대기한다.
     * 데이터가 수신되면 송신을 위한 타이머를 종료한다.
     */
    class BluetoothThread extends Thread {
        PrintWriter mPrintWriter = null;
        BufferedReader mBufferedReader = null;

        @Override
        public void run() {
            Log.i(tag, "start client");
            if (mBluetoothSocket != null) {
                try {
                    mPrintWriter = new PrintWriter(mBluetoothSocket.getOutputStream());
                    mBufferedReader = new BufferedReader(new InputStreamReader(mBluetoothSocket.getInputStream()));

                    while (!isInterrupted()) {
                        int msg = mBufferedReader.read();
                        Log.i(tag, "receive data : " + msg);
                        cancelTimer();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            tearDown();
        }

        /**
         * 블루투스 소켓을 닫고 통신을 종료한다.
         */
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

        /**
         * 연결된 블루투스 기기로 데이터를 보낸다.
         *
         * @param msg
         */
        synchronized public void sendMessage(final int msg) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if (mPrintWriter == null) return;
                    mPrintWriter.write(msg);
                    mPrintWriter.flush();
                    Log.i(tag, "sendMessage : " + msg);
                }
            });
        }

    }
}
