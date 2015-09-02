package com.itboye.bluebao.actiandfrag;

import android.app.Activity;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.itboye.bluebao.R;
import com.itboye.bluebao.util.Util;

/**
 * 设置activity
 * @author Administrator
 *
 */
public class ActiConfig extends Activity implements OnClickListener{
	
	private RelativeLayout rl_vibrate ;
	private RelativeLayout rl_sound;
	private ImageView iv_vibrate ;
	private ImageView iv_sound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_acti_config);
		
		 rl_vibrate = (RelativeLayout) findViewById(R.id.acti_config_rl_vibrate);
		 rl_sound = (RelativeLayout) findViewById(R.id.acti_config_rl_sound);
		 iv_vibrate = (ImageView) findViewById(R.id.acti_config_iv_vibrate_right);
		 iv_sound = (ImageView) findViewById(R.id.acti_config_iv_sound_right);
		 
		 rl_vibrate.setOnClickListener(this);
		 rl_sound.setOnClickListener(this);
		 
		 iv_sound.setVisibility(View.VISIBLE);
		
	}

	@Override
	protected void onPause() {
		Log.i("ACTI_CONFIG", "vibrate is:" + Util.vibrate + "  sound is:" + Util.sound);
		super.onPause();
	}
	
	@Override
	public void onClick(View v) {
		switch ( v.getId() ){
		case R.id.acti_config_rl_vibrate:
			iv_vibrate.setVisibility(View.VISIBLE);
			iv_sound.setVisibility(View.INVISIBLE);
			Util.vibrate = true;
			Util.sound = false;
			break;
			
		case R.id.acti_config_rl_sound:
			iv_sound.setVisibility(View.VISIBLE);
			iv_vibrate.setVisibility(View.INVISIBLE);
			Util.sound = true;
			Util.vibrate = false;
			break;
		}
	}
}