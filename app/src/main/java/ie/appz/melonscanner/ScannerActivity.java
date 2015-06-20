package ie.appz.melonscanner;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.axio.melonplatformkit.DeviceHandle;
import com.axio.melonplatformkit.DeviceManager;
import com.axio.melonplatformkit.IDeviceManagerListener;

public class ScannerActivity extends AppCompatActivity implements IDeviceManagerListener {
    private DeviceHandleAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);


        ListView lvDevices = (ListView) findViewById(R.id.lvDevices);
        mAdapter = new DeviceHandleAdapter();
        lvDevices.setAdapter(mAdapter);

        lvDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DeviceHandle deviceHandle = DeviceManager.getManager().getAvailableDevices().get
                        (position);

                switch (deviceHandle.getState()) {
                    case CONNECTED:
                        deviceHandle.disconnect();
                        mAdapter.notifyDataSetChanged();
                        break;
                    case DISCONNECTED:
                        deviceHandle.connect();
                        mAdapter.notifyDataSetChanged();
                        break;
                    case CONNECTING:
                    default:
                        //do nothing
                }
            }
        });

        lvDevices.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long
                    id) {

                return true;
            }
        });

        new AlertDialog.Builder(this).setMessage("When the Melon is " +
                "connected it's light will change from flashing to solid.").setPositiveButton
                ("OKAY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scanner, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.mDeviceHandleArray
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DeviceManager.getManager().addListener(this);

        DeviceManager.getManager().startScan();
    }


    @Override
    protected void onPause() {
        super.onPause();
        DeviceManager.getManager().removeListener(this);
        DeviceManager.getManager().stopScan();

    }

    @Override
    public void onDeviceScanStopped() {
        Toast.makeText(ScannerActivity.this, "Device Scan Stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeviceScanStarted() {
        Toast.makeText(ScannerActivity.this, "Device Scan Started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeviceFound(DeviceHandle deviceHandle) {
        Toast.makeText(ScannerActivity.this, "Device Found", Toast.LENGTH_SHORT).show();


        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDeviceReady(DeviceHandle deviceHandle) {
        Toast.makeText(ScannerActivity.this, "Device Ready", Toast.LENGTH_SHORT).show();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDeviceDisconnected(DeviceHandle deviceHandle) {
        Toast.makeText(ScannerActivity.this, "Device Disconnected", Toast.LENGTH_SHORT).show();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDeviceConnected(DeviceHandle deviceHandle) {
        Toast.makeText(ScannerActivity.this, "Device Connected", Toast.LENGTH_SHORT).show();
        mAdapter.notifyDataSetChanged();
    }

    class DeviceHandleAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return DeviceManager.getManager().getAvailableDevices().size();
        }

        @Override
        public DeviceHandle getItem(int position) {
            return DeviceManager.getManager().getAvailableDevices().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DeviceHandle deviceHandle = getItem(position);
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_2,
                        parent, false);
                viewHolder = new ViewHolder();
                viewHolder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
                viewHolder.text2 = (TextView) convertView.findViewById(android.R.id.text2);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.text1.setText("Name: " + deviceHandle.getName());
            viewHolder.text2.setText("State: " + deviceHandle.getState().name());


            return convertView;
        }

        class ViewHolder {
            TextView text1;
            TextView text2;
        }
    }
}

