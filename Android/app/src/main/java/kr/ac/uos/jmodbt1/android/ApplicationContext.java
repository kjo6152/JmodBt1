package kr.ac.uos.jmodbt1.android;

import android.app.Application;
import android.util.Log;

/**
 * Created by jiwon on 2015-12-13.
 */
public class ApplicationContext extends Application {
    private static final String tag = ApplicationContext.class.getName();

    BluetoothManager mBluetoothManager;

    @Override
    public void onCreate() {
        super.onCreate();

        mBluetoothManager = new BluetoothManager(this);

        Log.i(tag,"onCreate");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        Log.i(tag,"onTerminate");
    }

    public BluetoothManager getBluetoothManager(){
        return mBluetoothManager;
    }
}
