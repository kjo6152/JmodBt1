package kr.ac.uos.jmodbt1.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;


public class MainActivity extends Activity implements View.OnClickListener {
    private static final String tag = MainActivity.class.getName();

    BluetoothManager bluetoothManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((ImageButton) findViewById(R.id.led_button)).setOnClickListener(this);

        bluetoothManager = new BluetoothManager(this);
        bluetoothManager.getCheckDevcie();
        bluetoothManager.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.led_button:
                Log.i(tag, "button pressed");
                bluetoothManager.sendMessage('0'+1);
                break;
        }
    }
}
