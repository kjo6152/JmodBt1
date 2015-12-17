package kr.ac.uos.jmodbt1.android;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
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
    ImageView text_on;
    ImageView text_off;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothManager = ((ApplicationContext)getApplication()).getBluetoothManager();
        ((ImageButton) findViewById(R.id.main_led_button)).setOnClickListener(this);

        status = ((ImageView) findViewById(R.id.main_status));
        refresh = ((ImageView) findViewById(R.id.main_refresh));
        deviceName = ((TextView) findViewById(R.id.main_device_name));

        text_on = ((ImageView) findViewById(R.id.main_button_text_on));
        text_off = ((ImageView) findViewById(R.id.main_button_text_off));

        refresh.setOnClickListener(this);
        backPressCloseHandler = new BackPressCloseHandler(this);

        //폰트 적용
        mTypeface = Typeface.createFromAsset(getAssets(), "helvetica_neue_thin_italic.otf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        setGlobalFont(root);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initView();

        mBluetoothManager.setCallback(callback);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mBluetoothManager.removetCallback();
    }

    void initView(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deviceName.setText(mBluetoothManager.getSelectedDevice().getName());
                if (mBluetoothManager.isEanbled()) {
                    status.setImageResource(R.mipmap.main_button_layer3_on);
                    text_on.setVisibility(View.VISIBLE);
                    text_off.setVisibility(View.INVISIBLE);
                } else {
                    status.setImageResource(R.mipmap.main_button_layer3_off);

                    text_on.setVisibility(View.INVISIBLE);
                    text_off.setVisibility(View.VISIBLE);
                }
            }
        });
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
            initView();
        }

        @Override
        public void disconnect(BluetoothDevice device) {
            Log.i(tag, "disconnect");
            initView();
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
                Toast.makeText(this, "재연결을 시도합니다.", Toast.LENGTH_SHORT).show();
                mBluetoothManager.connect(mBluetoothManager.getSelectedDevice());
                break;
        }
    }

    /** 폰트 적용을 위한 코드 */
    private Typeface mTypeface;
    void setGlobalFont(ViewGroup root) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if (child instanceof TextView)
                ((TextView)child).setTypeface(mTypeface);
            else if (child instanceof ViewGroup)
                setGlobalFont((ViewGroup)child);
        }
    }
}
