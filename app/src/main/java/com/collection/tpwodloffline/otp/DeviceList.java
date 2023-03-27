package com.collection.tpwodloffline.otp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.collection.tpwodloffline.R;

import java.util.Set;

public class DeviceList extends Activity {
    protected static final String TAG = "TAG";
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle mSavedInstanceState) {
        super.onCreate(mSavedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.device_list);

        setResult(Activity.RESULT_CANCELED);
        ArrayAdapter<String> mPairedDevicesArrayAdapter =
                new ArrayAdapter<>(DeviceList.this, R.layout.device_name);

        ListView mPairedListView = (ListView) findViewById(R.id.paired_devices);
        mPairedListView.setAdapter(mPairedDevicesArrayAdapter);
        mPairedListView.setOnItemClickListener(mDeviceClickListener);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();

        if (mPairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice mDevice : mPairedDevices) {
                mPairedDevicesArrayAdapter.add(mDevice.getName() + "\n" + mDevice.getAddress());
            }
        } else {
            String mNoDevices = "None Paired";
            mPairedDevicesArrayAdapter.add(mNoDevices);
        }
    }

    private final AdapterView.OnItemClickListener mDeviceClickListener =
            new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> mAdapterView,
                                        View mView, int mPosition, long mLong) {
                    try {
                        mBluetoothAdapter.cancelDiscovery();
                        String mDeviceInfo = ((TextView) mView).getText().toString();
                        String mDeviceAddress = mDeviceInfo.substring(mDeviceInfo.length() - 17);

                        Bundle mBundle = new Bundle();
                        mBundle.putString("DeviceAddress", mDeviceAddress);
                        Intent mBackIntent = new Intent();
                        mBackIntent.putExtras(mBundle);
                        setResult(Activity.RESULT_OK, mBackIntent);
                        finish();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }
}