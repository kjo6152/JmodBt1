package kr.ac.uos.jmodbt1.android;

import android.app.Activity;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by jiwon on 2015-12-17.
 */
public class BackPressCloseHandler {
    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            BluetoothManager mBluetoothManager = ((ApplicationContext)activity.getApplication()).getBluetoothManager();
            mBluetoothManager.tearDown();
            activity.finish();
            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity,
                "\'뒤로\'버튼을 한번 더 누르시면\r\n연결을 종료합니다.", Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if( v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }
}
