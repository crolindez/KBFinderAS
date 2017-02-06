package es.carlosrolindez.bluetoothinterface;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.IBluetoothA2dp;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;



public class BtA2dpConnectionManager {
    private static final String TAG = "BtA2dpConnectionManager";
    private static final String DEVICE_FILTER = "es.carlosrolindez.BtA2dpConnectionManager.Filter";

    private Context mContextBTa2dp = null;

    private boolean mBta2dpReceiverRegistered = false;
    private boolean mBtA2dpIsBound = false;
    private IBluetoothA2dp iBtA2dp = null;

    private BluetoothDevice connectingDevice = null;

    public BtA2dpConnectionManager(Context context) {
        mContextBTa2dp = context;
    }


    public void connectBluetoothA2dp(BluetoothDevice device) {
        connectingDevice = device;

        if (!mBta2dpReceiverRegistered) {
            IntentFilter filter1 = new IntentFilter(DEVICE_FILTER);
            mContextBTa2dp.registerReceiver(mBtA2dpReceiver, filter1);
            mBta2dpReceiverRegistered = true;
        }

        Intent intent = new Intent(IBluetoothA2dp.class.getName());
        intent.setPackage("com.android.bluetooth");
        mContextBTa2dp.bindService(intent, mBtA2dpServiceConnection, Context.BIND_AUTO_CREATE);
    }


    private final ServiceConnection mBtA2dpServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mBtA2dpIsBound = true;
            iBtA2dp = IBluetoothA2dp.Stub.asInterface(service);
            sendA2dpConnection();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBtA2dpIsBound = false;

        }

    };

    private void sendA2dpConnection() {
        Intent intent = new Intent();
        intent.setAction(DEVICE_FILTER);
        mContextBTa2dp.sendBroadcast(intent);
    }


    private void doUnbindServiceBtA2dp() {
        if (mBtA2dpIsBound) {
            try {
                mContextBTa2dp.unbindService(mBtA2dpServiceConnection);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }


    private void btA2dpDone() {
        if (mBta2dpReceiverRegistered) {
            mContextBTa2dp.unregisterReceiver(mBtA2dpReceiver);
            mBta2dpReceiverRegistered = false;
            doUnbindServiceBtA2dp();
        }

    }

    public void closeService() {
        btA2dpDone();
    }


    private final BroadcastReceiver mBtA2dpReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            new connectA2dpTask().execute(/*conectingMAC*/);
        }

    };


    private class connectA2dpTask extends AsyncTask<String, Void, Boolean> {


        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            btA2dpDone();
        }

        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... arg0) {

            if (connectingDevice == null) {
                return false;
            }

            try {
                if (iBtA2dp != null) {
                    if (iBtA2dp.getConnectionState(connectingDevice) == 0) {
                        iBtA2dp.connect(connectingDevice);
                    } else {
                        iBtA2dp.disconnect(connectingDevice);
                        connectingDevice = null;
                    }
                }

            } catch (Exception e) {
            }
            return true;
        }

    }



}

