package es.carlosrolindez.bluetoothinterface;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.IBluetooth;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.Set;



public class BtListenerManager {
    private static final String TAG = "BtListenerManager";

    public enum BtEvent {CONNECTED, DISCONNECTED, BONDED, DISCOVERY_STARTED, DISCOVERY_FINISHED }

    private Context mContextBt = null;

    private boolean mBtReceiverRegistered = false;
    private boolean mBtIsBound = false;
    private IBluetooth iBt = null;

    private BtListener mBtListener = null;

    public interface BtListener {
        void addBtDevice(String name, BluetoothDevice device);
        void notifyBtEvent(BluetoothDevice device, BtEvent event);
    }


    public BtListenerManager(Context context,BtListener listener) {

        mContextBt = context;
        mBtListener = listener;
    }

    public void searchBtDevices() {


        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_NAME_CHANGED);
        IntentFilter filter3 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        IntentFilter filter4 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter5 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        IntentFilter filter6 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        mContextBt.registerReceiver(mBtReceiver, filter1);
        mContextBt.registerReceiver(mBtReceiver, filter2);
        mContextBt.registerReceiver(mBtReceiver, filter3);
        mContextBt.registerReceiver(mBtReceiver, filter4);
        mContextBt.registerReceiver(mBtReceiver, filter5);
        mContextBt.registerReceiver(mBtReceiver, filter6);

        mBtReceiverRegistered = true;

        Intent intent = new Intent(IBluetooth.class.getName());
        intent.setPackage("com.android.bluetooth");
        if (!mBtIsBound) {
            mContextBt.bindService(intent, mBtServiceConnection, Context.BIND_AUTO_CREATE);
        } else {
            sendNames();
        }

    }

    private final ServiceConnection mBtServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBtIsBound = true;
            iBt = IBluetooth.Stub.asInterface(service);
            sendNames();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBtIsBound = false;
        }
    };


    private void sendNames() {
        BluetoothAdapter mBTA = BluetoothAdapter.getDefaultAdapter();

        if (mBTA != null) {
            Set<BluetoothDevice> pairedDevices = mBTA.getBondedDevices();
            if (mBtListener != null) {
                if (pairedDevices.size() > 0) {

                    for (BluetoothDevice device : pairedDevices) {
                        String currentName = null; // = device.getName();
                        try {
                            currentName = iBt.getRemoteAlias(device);
                        } catch (RemoteException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if (currentName == null)
                            currentName = device.getName();
                        mBtListener.addBtDevice(currentName, device);
                    }

                }

            }
        }
    }

    private BroadcastReceiver mBtReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action) || BluetoothDevice.ACTION_NAME_CHANGED.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (mBtListener != null)
                    mBtListener.addBtDevice(device.getName(), device);

                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (mBtListener != null)
                    mBtListener.notifyBtEvent(null, BtEvent.DISCOVERY_FINISHED);

            } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (mBtListener != null)
                    mBtListener.notifyBtEvent(device,BtEvent.CONNECTED);

            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (mBtListener != null)
                    mBtListener.notifyBtEvent(device, BtEvent.DISCONNECTED);

            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    if (mBtListener != null) {
                        mBtListener.notifyBtEvent(device, BtEvent.BONDED);
                    }
                }
            }
        }

    };




    private void doUnbindServiceBt() {
        if (mBtIsBound) {
            try {
                mContextBt.unbindService(mBtServiceConnection);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }


    private void BtDone() {
        if (mBtReceiverRegistered) {
            mContextBt.unregisterReceiver(mBtReceiver);
            mBtReceiverRegistered = false;
            doUnbindServiceBt();
        }

    }

    public void closeService() {
        BtDone();
    }


}


