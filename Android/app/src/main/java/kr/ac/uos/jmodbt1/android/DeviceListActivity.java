package kr.ac.uos.jmodbt1.android;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class DeviceListActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    BluetoothManager mBluetoothManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        mBluetoothManager = ((ApplicationContext)getApplication()).getBluetoothManager();
        deviceList = mBluetoothManager.getDevcies();

        ListView view = (ListView)findViewById(R.id.list_device);
        view.setOnItemClickListener(this);

        DeviceListAdapter adapter = new DeviceListAdapter();
        view.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mBluetoothManager.setCallback(callback);

        if(mBluetoothManager.isEanbled()){
            Intent intent = new Intent(DeviceListActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        mBluetoothManager.removetCallback();
    }

    BluetoothManager.BluetoothCallback callback = new BluetoothManager.BluetoothCallback(){
        @Override
        public void connect(BluetoothDevice device, final boolean ret) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ret) {
                        Toast.makeText(DeviceListActivity.this, "연결 성공", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DeviceListActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(DeviceListActivity.this, "연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public void disconnect(BluetoothDevice device) {

        }
    };

    ArrayList<BluetoothDevice> deviceList = new ArrayList<>();

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mBluetoothManager.connect(deviceList.get(position));
    }

    class DeviceListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return deviceList.size();
        }

        @Override
        public Object getItem(int position) {
            return deviceList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                LayoutInflater mInflater = (LayoutInflater) DeviceListActivity.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.item_device_list,
                        parent, false);
            }


            TextView deviceNameTextView = (TextView)convertView.findViewById(R.id.list_device_name);
            deviceNameTextView.setText(deviceList.get(position).getName());

            return convertView;
        }
    }
}
