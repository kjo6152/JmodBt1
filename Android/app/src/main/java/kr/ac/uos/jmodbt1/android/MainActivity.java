package kr.ac.uos.jmodbt1.android;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements View.OnClickListener {
    private static final String tag = MainActivity.class.getName();

    BluetoothManager mBluetoothManager;
    ImageView status;
    ImageView refresh;
    TextView deviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothManager = ((ApplicationContext)getApplication()).getBluetoothManager();
        ((ImageButton) findViewById(R.id.main_led_button)).setOnClickListener(this);

        status = ((ImageView) findViewById(R.id.main_status));
        refresh = ((ImageView) findViewById(R.id.main_refresh));
        deviceName = ((TextView) findViewById(R.id.main_device_name));

        refresh.setOnClickListener(this);
        backPressCloseHandler = new BackPressCloseHandler(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        deviceName.setText(mBluetoothManager.getSelectedDevice().getName());
        mBluetoothManager.setCallback(callback);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mBluetoothManager.removetCallback();
    }

    private BackPressCloseHandler backPressCloseHandler;

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }


    BluetoothManager.BluetoothCallback callback = new BluetoothManager.BluetoothCallback(){
        @Override
        public void connect(BluetoothDevice device, boolean ret) {
            Log.i(tag, "connect");
            //if(bluetoothManager.isEanbled())status.setImageAlpha(0);
        }

        @Override
        public void disconnect(BluetoothDevice device) {
            Log.i(tag, "disconnect");
            //if(bluetoothManager.isEanbled())status.setImageAlpha(0);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_led_button:
                Log.i(tag, "button pressed");
                mBluetoothManager.sendMessage(1);
                break;
            case R.id.main_refresh:
                Log.i(tag, "refresh pressed");
                mBluetoothManager.connect(mBluetoothManager.getSelectedDevice());
                break;
        }
    }
}
