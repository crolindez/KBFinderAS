package es.carlosrolindez.bluetoothinterface;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.IBluetoothA2dp;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.LauncherActivityInfo;
import android.os.AsyncTask;
import android.os.IBinder;

import java.util.List;


public class BtA2dpConnectionManager {
    private static final String TAG = "BtA2dpConnectionManager";
    private static final String DEVICE_FILTER = "es.carlosrolindez.BtA2dpConnectionManager.Filter";

    public enum BtA2dpEvent {CONNECTED, DISCONNECTED}

    private Context mContextBtA2dp = null;

    private boolean mBtA2dpReceiverRegistered = false;
    private boolean mBtA2dpIsBound = false;
    private IBluetoothA2dp iBtA2dp = null;

    private BluetoothDevice connectingDevice = null;

    private BtA2dpListener mBtA2dpListener = null;

    public interface BtA2dpListener {
        void notifyBtA2dpEvent(BluetoothDevice device, BtA2dpEvent event);
    }

    public BtA2dpConnectionManager(Context context,BtA2dpListener listener) {
        mContextBtA2dp = context;
        mBtA2dpListener = listener;
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(mContextBtA2dp, mProfileListener, BluetoothProfile.A2DP);
    }

    public void connectBluetoothA2dp(BluetoothDevice device) {
        connectingDevice = device;

        if (!mBtA2dpReceiverRegistered) {
            IntentFilter filter1 = new IntentFilter(DEVICE_FILTER);
            mContextBtA2dp.registerReceiver(mBtA2dpReceiver, filter1);
            mBtA2dpReceiverRegistered = true;
        }

        Intent intent = new Intent(IBluetoothA2dp.class.getName());
        intent.setPackage("com.android.bluetooth");
  //      LauncherService.getInstance()
        mContextBtA2dp.bindService(intent, mBtA2dpServiceConnection, Context.BIND_AUTO_CREATE);

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
        mContextBtA2dp.sendBroadcast(intent);
    }


    private void doUnbindServiceBtA2dp() {
        if (mBtA2dpIsBound) {
            try {
                mContextBtA2dp.unbindService(mBtA2dpServiceConnection);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }


    private void btA2dpDone() {
        if (mBtA2dpReceiverRegistered) {
            mContextBtA2dp.unregisterReceiver(mBtA2dpReceiver);
            mBtA2dpReceiverRegistered = false;
            doUnbindServiceBtA2dp();
        }

    }

    public void closeService() {
        btA2dpDone();
    }

    private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {

        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            BluetoothA2dp a2dp = (BluetoothA2dp) proxy;
 /*           try {
                a2dp.getClass()
                        .getMethod("connect",BluetoothDevice.class)
                        .invoke(a2dp, connectingDevice);
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            if (profile == BluetoothProfile.A2DP) {
                BluetoothA2dp btA2dp = (BluetoothA2dp) proxy;
                List<BluetoothDevice> a2dpConnectedDevices = btA2dp.getConnectedDevices();
                if (a2dpConnectedDevices.size() != 0) {
                    if (mBtA2dpListener != null) {
                        for (BluetoothDevice device : a2dpConnectedDevices) {
                            mBtA2dpListener.notifyBtA2dpEvent(device, BtA2dpEvent.CONNECTED);
                        }
                    }
                }
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(BluetoothProfile.A2DP, btA2dp);
            }
        }

        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.A2DP) {
                mBtA2dpListener.notifyBtA2dpEvent(null, BtA2dpEvent.DISCONNECTED);
            }
        }
    };

    private final BroadcastReceiver mBtA2dpReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            new connectA2dpTask().execute();
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

