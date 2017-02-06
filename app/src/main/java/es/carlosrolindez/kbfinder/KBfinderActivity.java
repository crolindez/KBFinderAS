package es.carlosrolindez.kbfinder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;

import es.carlosrolindez.bluetoothinterface.BtA2dpConnectionManager;
import es.carlosrolindez.bluetoothinterface.BtA2dpDevice;
import es.carlosrolindez.bluetoothinterface.BtListenerManager;

public class KBfinderActivity extends Activity  implements BtListenerManager.BtListener{
	private static String TAG = "KBfinder";
    private static final String FILENAME = "settings.txt";

    private BluetoothAdapter mBluetoothAdapter = null;
    private BtListenerManager mBtListenerManager = null;
    private BtA2dpConnectionManager mBtA2DpConnectionManager = null;

	private final ArrayBTdevice deviceList = new ArrayBTdevice();
	private KBdeviceListAdapter deviceListAdapter = null;
	private ListView mListView = null;

    private MenuItem progressItem;

    ProgressDialog dialog;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

  //      requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
		    Toast.makeText(this, getString(R.string.bt_not_availabe), Toast.LENGTH_LONG).show();
		    finish();
		}	
    }
	
    @Override
    public void onStart() {
        super.onStart();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);
        }
        else
            startBtA2dpConnection();

    }
    
    @Override
    public void onResume() {
        super.onResume();
    }

    private void startBtA2dpConnection() {
        mBtListenerManager = new BtListenerManager(getApplicationContext());
        mBtListenerManager.setBtListener(this);

        mBtA2DpConnectionManager = new BtA2dpConnectionManager(getApplicationContext());

        mListView = (ListView)findViewById(R.id.list);
        deviceListAdapter = new KBdeviceListAdapter(this, deviceList , mListView, mBtA2DpConnectionManager);
        mListView.setAdapter(deviceListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                KBdevice device = (KBdevice) parent.getItemAtPosition(position);

                if (device.mBtDevice.getBondState() != BluetoothDevice.BOND_BONDED)
                    device.mBtDevice.createBond();
                else
                    mBtA2DpConnectionManager.connectBluetoothA2dp(device.mBtDevice);
            }
        });

        mBtListenerManager.searchBtDevices();

    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    startBtA2dpConnection();
                } else {
                    Toast.makeText(this, R.string.bt_not_enabled,Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;           
        }
    }


		
	@Override
	protected void onDestroy() {
        if (mBtListenerManager !=null)
            mBtListenerManager.closeService();
        if (mBtA2DpConnectionManager !=null)
            mBtA2DpConnectionManager.closeService();
		super.onDestroy();
	}
		


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        progressItem = menu.findItem(R.id.bt_scan);
        return super.onPrepareOptionsMenu(menu);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId())
		{
	        case R.id.bt_scan: 
	            // Launch the DeviceListActivity to see devices and do scan
	        	doDiscovery();
                progressItem.setActionView(new ProgressBar(this));
	            return true;

			case R.id.clear_memory:
				
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        switch (which){
				        case DialogInterface.BUTTON_POSITIVE:

				        	File dir = getFilesDir();
				        	File file = new File(dir,FILENAME);
				        	file.delete();
				            break;

				        case DialogInterface.BUTTON_NEGATIVE:
				            //No button clicked
				            break;
				        }
				    }
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.are_you_sure).setPositiveButton("Yes", dialogClickListener)
				    .setNegativeButton("No", dialogClickListener).show();
                return true;
		}

		return super.onOptionsItemSelected(item);
	}
	

	/**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {

        // Indicate scanning in the title
 //       setProgressBarIndeterminateVisibility(true);
        // Request discover from BluetoothAdapter
        mBluetoothAdapter.startDiscovery();
    }

    public void addBtDevice(String name, BluetoothDevice device) {
        KBdevice kbdevice = new KBdevice(name,device);

        if (kbdevice.deviceType == KBdevice.OTHER) return;

        for (BtA2dpDevice a2dpDevice : deviceList)
            if (kbdevice.deviceMAC.equals(a2dpDevice.deviceMAC)) return;

        deviceList.addSorted(kbdevice);
		deviceListAdapter.notifyDataSetChanged();
    }

    public void notifyBtEvent(BluetoothDevice device, BtListenerManager.BtEvent event) {
        switch (event) {
            case DISCOVERY_STARTED:
                break;
            case DISCOVERY_FINISHED:
//                setProgressBarIndeterminateVisibility(false);
                progressItem.setActionView(null);
                break;

            case CONNECTED:
                for (BtA2dpDevice listDevice : deviceList)
                {
                    if (device.getAddress().equals(listDevice.deviceMAC)) {
                        listDevice.connected = true;
                        break;
                    }
                }
                deviceListAdapter.notifyDataSetChanged();

 //               Toast.makeText(this, device.getName() + " Connected", Toast.LENGTH_SHORT).show();

 /*           	final AudioManager am = (AudioManager) this.getSystemService(AUDIO_SERVICE);
                if (KBdevice.getDeviceType(device.getAddress()) == KBdevice.IN_WALL_BT) {
	                new CountDownTimer(2000, 1000) {
	    				@Override
	    				public void onFinish() {
	    					am.setStreamVolume(AudioManager.STREAM_MUSIC, (int)(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC) *0.6),	0);
	    				}
	    				@Override
	    				public void onTick(long millisUntilFinished) {
	    				}
	    			}.start();
                } else if (KBdevice.getDeviceType(device.getAddress()) == KBdevice.ISELECT) {
	                new CountDownTimer(2000, 1000) {
	    				@Override
	    				public void onFinish() {
	    					am.setStreamVolume(AudioManager.STREAM_MUSIC, (int)(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * 0.9), 0);
	    				}
	    				@Override
	    				public void onTick(long millisUntilFinished) {
	    				}
	    			}.start();
                }
*/
                break;

            case DISCONNECTED:
                for (BtA2dpDevice listDevice : deviceList)
                {
                    if (device.getAddress().equals(listDevice.deviceMAC)) {
                        listDevice.connected = false;
                        break;
                    }
                }
                deviceListAdapter.notifyDataSetChanged();

 //               Toast.makeText(this, device.getName() + " Disconnected", Toast.LENGTH_SHORT).show();

                break;

            case BONDED:
                if ((       KBdevice.getDeviceType(device.getAddress()) == KBdevice.IN_WALL_BT)
                        || (KBdevice.getDeviceType(device.getAddress()) == KBdevice.ISELECT)
                        || (KBdevice.getDeviceType(device.getAddress()) == KBdevice.IN_WALL_WIFI)) {

                    mBtA2DpConnectionManager.connectBluetoothA2dp(device);

                } else if (KBdevice.getDeviceType(device.getAddress()) == KBdevice.SELECTBT) {
                    //disconnect current A2dp connection (if different to current device)
                    BluetoothDevice connectedDevice = null;
                    for (BtA2dpDevice listDevice : deviceList)
                    {
                        if (listDevice.connected) {
                            if (!device.getAddress().equals(listDevice.deviceMAC))
                                connectedDevice = listDevice.mBtDevice;
                            break;
                        }
                    }
                    if (connectedDevice!=null)
                        mBtA2DpConnectionManager.connectBluetoothA2dp(device);

                    Intent localIntent = new Intent (this, SelectBtActivity.class);
                    localIntent.putExtra(Constants.LAUNCH_MAC, device);
                    startActivity(localIntent);
                }

                break;
        }

    }

}
