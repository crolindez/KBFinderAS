package es.carlosrolindez.kbfinder;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;




public class FmFragment extends Fragment {
    private static final String TAG = "FmFragment";
    
    public static final String EXTRA_STEREO = "FmFragment.Stereo";
    public static final String EXTRA_DEVICE = "FmFragment.Device";
    
	private String fragmentName;
	private Context mContext;
	
	private TextView frequencyText;
	private TextView RDSText;
	private ImageView mono;
	private boolean mStereo;
	private ImageView favorite;
	
	private boolean scanning;
	
	private static KBdeviceSettings mDevice;
	
	private SppBridge spp;
	
	public interface SppBridge {
		void sppMessage(String message);
	}
	

/*	public FmFragment(Context context) {
		mContext = context;
		fragmentName =  "FM";
		spp = (SppBridge) context;
		mStereo = true;
		mBtDevice = null;
		scanning = false;
	}*/
	
	public FmFragment() {
        super();
    }


	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		mStereo = getArguments().getBoolean(EXTRA_STEREO);
		mDevice = getArguments().getParcelable(EXTRA_DEVICE);
		mContext = getActivity();
		fragmentName =  "FM";
		spp = (SppBridge) mContext;

		scanning = false;
		
		super.onCreate(savedInstanceState);

	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fm_fragment, container, false);

        ((TextView) rootView.findViewById(R.id.fragment_text)).setText(fragmentName);
        Typeface myTypeface = Typeface.createFromAsset(mContext.getAssets(), "seven_segments.ttf");
        frequencyText = (TextView)rootView.findViewById(R.id.frequency);
        frequencyText.setTypeface(myTypeface);
        RDSText = (TextView)rootView.findViewById(R.id.RDS);
        
		mono = (ImageView)rootView.findViewById(R.id.mono_FM);
		favorite = (ImageView)rootView.findViewById(R.id.favorite_FM);
		
		setStereo(mStereo);

		ImageView button_scan_down_FM = (ImageView)rootView.findViewById(R.id.scan_down_FM);
		ImageView button_mem_FM = (ImageView)rootView.findViewById(R.id.mem_FM);
		ImageView button_scan_up_FM = (ImageView)rootView.findViewById(R.id.scan_up_FM);
		
		favorite.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if (mDevice.getFreqInArray(frequencyText.getText().toString())!=null) {
					mDevice.removeFreqFromArray(frequencyText.getText().toString());
					favorite.setBackgroundResource(R.drawable.nofavorite);
				} else {
					mDevice.addFreq2Array(frequencyText.getText().toString(),RDSText.getText().toString());
					favorite.setBackgroundResource(R.drawable.favorite);		
				}
			}
		});

		mono.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				setStereo(!mStereo);
				if (mStereo)
					spp.sppMessage("MON OFF\r");
				else
					spp.sppMessage("MON ON\r");
			}
		});
		

		frequencyText.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				showFmDialog();
			}
		});
		
		button_scan_down_FM.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				spp.sppMessage("SCN DOWN\r");
				setFrequency("___._");
				favorite.setVisibility(View.INVISIBLE);	
				scanning = true;
			}
		});

		button_mem_FM.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if (mDevice.fmPack.size()>0) 
					showMemDialog();
			}
		});

		button_scan_up_FM.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				spp.sppMessage("SCN UP\r");
				setFrequency("___._");
				favorite.setVisibility(View.INVISIBLE);	
				scanning = true;
			}
		});
           
        
        
        return rootView;	        
    }
    
    public void setFrequency(String frequency) {
        frequencyText.setText(frequency);    	
		favorite.setVisibility(View.VISIBLE);
		scanning = false;
		
		FmSet set = mDevice.getFreqInArray(frequency);
		if (set != null) {
			favorite.setBackgroundResource(R.drawable.favorite);
	    	RDSText.setText(set.getRDS());  			
		} else {
			favorite.setBackgroundResource(R.drawable.nofavorite);	
	    	RDSText.setText("");  
		}
    }
    
    public void setRDS(String RDS) {
    	if (!scanning) {
    		RDSText.setText(RDS);    	
			FmSet set = mDevice.getFreqInArray(frequencyText.getText().toString());
			if (set != null) {
				set.setRDS(RDS);
			}
		} 
    }  
    
    public void setStereo(boolean stereo) {   
    	mStereo = stereo;
		if (mStereo) 
			mono.setBackgroundResource(R.drawable.stereo);
		else
			mono.setBackgroundResource(R.drawable.mono);
    }
    
	private void showFmDialog() {
    	final int minFreq = 87;
    	final int maxFreq = 108;
    	final String dialMegaherz[] = new String[maxFreq-minFreq+1];

    	for (int frInt=maxFreq, index=0; frInt>=minFreq; frInt--,index++){
    		dialMegaherz[index] = Integer.toString(frInt);
    	}
 
    	final String dialKiloherz[] = new String[10];
    	for (int frInt=9, index=0; frInt>=0; frInt--,index++){
    		dialKiloherz[index] = Integer.toString(frInt);
    	}

        String freq[] = frequencyText.getText().toString().trim().split("\\.");
        final String newFreq[] = {freq[0], freq[1]};
        
    	final CountDownTimer countDownFm = new CountDownTimer(800,800){
    		public void onTick(long millisUntilFinished) {
		    }
		     	
		    public void onFinish() {
		    	spp.sppMessage("TUN "+newFreq[0]+"."+newFreq[1]+"\r");
				setFrequency(newFreq[0]+"."+newFreq[1]);
		    }
    	};
    	  	
    	
    	final Dialog fmDialog = new Dialog(mContext, R.style.CustomDialog);
    	fmDialog.setTitle(getResources().getString(R.string.fm_dial));
    	fmDialog.setContentView(R.layout.fm_dial);

    	
        final FmPicker megaherzPicker = (FmPicker) fmDialog.findViewById(R.id.megaherzs);
        final FmPicker kiloherzPicker = (FmPicker) fmDialog.findViewById(R.id.kiloherzs);
        
        megaherzPicker.setMaxValue(maxFreq-minFreq);
        megaherzPicker.setMinValue(0);
        megaherzPicker.setWrapSelectorWheel(false); 
        megaherzPicker.setDisplayedValues(dialMegaherz);
        megaherzPicker.setValue(maxFreq-Integer.parseInt(freq[0]));
        megaherzPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i2) {
            	newFreq[0] = Integer.toString(maxFreq-i2);
            	if (newFreq[0].equals("108")) {
            		newFreq[1] = "0";
            	} else if ( (newFreq[0].equals("87")) && (Integer.parseInt(newFreq[1])<5) ) {
            		newFreq[1] = "5";
            	} else newFreq[1] = Integer.toString(9-kiloherzPicker.getValue());      		
            	countDownFm.cancel();
            	countDownFm.start();
            }
        });
        kiloherzPicker.setMaxValue(9);
        kiloherzPicker.setMinValue(0);
        kiloherzPicker.setWrapSelectorWheel(true);
        kiloherzPicker.setDisplayedValues(dialKiloherz);
        kiloherzPicker.setValue(9-Integer.parseInt(freq[1]));
        kiloherzPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i2) {
            	newFreq[1] = Integer.toString(9-i2);
            	if (newFreq[0].equals("108")) {
            		newFreq[1] = "0";
            	} else if ( (newFreq[0].equals("87")) && (Integer.parseInt(newFreq[1])<5) ) {
            		newFreq[1] = "5";
            	}            		
            	
            	countDownFm.cancel();
            	countDownFm.start();
            }
        });

        fmDialog.show();

    	
    }

    private void showMemDialog() {

		int index = 0;
    	final String memories[] = new String[mDevice.fmPack.size()+1]; // one additional memory for empty line
   	
    	for (int memInt=mDevice.fmPack.size(); memInt>0; memInt--,index++) {
    		FmSet fmSet = mDevice.fmPack.get(memInt-1);
    		memories[index] = fmSet.getFm();
    		if ( fmSet.getRDS()!=null) 
    			memories[index] =memories[index] + " " + fmSet.getRDS();
    	}
    	memories[mDevice.fmPack.size()]=" ";
    	
    	final String pack[] = {" "," "};
    	index=mDevice.fmPack.size() - mDevice.getIndexInArray(frequencyText.getText().toString()) - 1;
        if (index<mDevice.fmPack.size()) {        	
     		pack[0]=mDevice.fmPack.get(index).getFm();
     		pack[1]=mDevice.fmPack.get(index).getRDS();
        }
        index++;
        
    	final CountDownTimer countDownFm = new CountDownTimer(800,800){
    		public void onTick(long millisUntilFinished) {
		    }
		     	
		    public void onFinish() {
		    	spp.sppMessage("TUN "+pack[0]+"\r");
				setFrequency(pack[0]);
				if (pack[1]!=null)
					setRDS(pack[1]);
				else
					setRDS("");	
		    }
    	};
    	  	
    	
    	final Dialog memDialog = new Dialog(mContext, R.style.CustomDialog);
    	memDialog.setTitle(getResources().getString(R.string.mem_dial));
    	memDialog.setContentView(R.layout.mem_dial);
 	
        final FmPicker memPicker = (FmPicker) memDialog.findViewById(R.id.memories);
        
        memPicker.setMaxValue(mDevice.fmPack.size()+1);       	
        memPicker.setMinValue(1);
        memPicker.setWrapSelectorWheel(false); 
        memPicker.setDisplayedValues(memories);
 		memPicker.setValue(index);
        memPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i2) {
            	int index = mDevice.fmPack.size()-i2;
            	if (index<0)
                	countDownFm.cancel();
            	else {
	            	pack[0] = mDevice.fmPack.get(index).getFm();
	            	pack[1] = mDevice.fmPack.get(index).getRDS();
	            	countDownFm.cancel();
	            	countDownFm.start();
            	}
            }
        });
        memPicker.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
			}
		});

        memDialog.show();

    	
    }

}
