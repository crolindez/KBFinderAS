package es.carlosrolindez.kbfinder;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;




public class BtFragment extends Fragment {
	private String fragmentName;
	private Context mContext;
	
	private TextView songName;

	public BtFragment() {
		super();
	}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        mContext = getActivity();
        fragmentName =  "BT";
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.bt_fragment, container, false);    
      	((TextView) rootView.findViewById(R.id.fragment_text)).setText(fragmentName);
      	songName = (TextView)rootView.findViewById(R.id.song_name);
		
		ImageView button_previous_BT = (ImageView)rootView.findViewById(R.id.previous_BT);
		ImageView button_play_pause_BT = (ImageView)rootView.findViewById(R.id.play_pause_BT);
		ImageView button_next_BT = (ImageView)rootView.findViewById(R.id.next_BT);
		songName.setSelected(true);
		
		
		button_previous_BT.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				AudioManager am = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);

				long eventtime = SystemClock.uptimeMillis() - 1;
				KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS, 0);
				am.dispatchMediaKeyEvent(downEvent);

				eventtime++;
				KeyEvent upEvent = new KeyEvent(eventtime,eventtime,KeyEvent.ACTION_UP,KeyEvent.KEYCODE_MEDIA_PREVIOUS, 0);         
				am.dispatchMediaKeyEvent(upEvent);

			}
		});

		button_play_pause_BT.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				AudioManager am = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);

				long eventtime = SystemClock.uptimeMillis() - 1;
				KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
				am.dispatchMediaKeyEvent(downEvent);

				eventtime++;
				KeyEvent upEvent = new KeyEvent(eventtime,eventtime,KeyEvent.ACTION_UP,KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);         
				am.dispatchMediaKeyEvent(upEvent);

			}
		});

		button_next_BT.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				AudioManager am = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);

				long eventtime = SystemClock.uptimeMillis() - 1;
				KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT, 0);
				am.dispatchMediaKeyEvent(downEvent);

				eventtime++;
				KeyEvent upEvent = new KeyEvent(eventtime,eventtime,KeyEvent.ACTION_UP,KeyEvent.KEYCODE_MEDIA_NEXT, 0);         
				am.dispatchMediaKeyEvent(upEvent);

			}
		});
      	
      	
      	return rootView;	
        
        
    }
    
    public void setSongName(String name) {
        songName.setText(name);    	
    }    
    
    public void setBlinking(boolean blink) {
    	if (blink) {
		    Animation anim = new AlphaAnimation(0.0f, 1.0f);
		    anim.setDuration(500); //You can manage the time of the blink with this parameter
		    anim.setStartOffset(20);
		    anim.setRepeatMode(Animation.REVERSE);
		    anim.setRepeatCount(Animation.INFINITE);
		    songName.startAnimation(anim);
    	} else {
    		songName.clearAnimation();		
    	}
    }
    

}
