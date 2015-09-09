package com.itboye.bluebao.actiandfrag;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.itboye.bluebao.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 购买器材activity
 * 
 * @author Administrator
 */
public class ActiBuyDevice extends Activity implements OnClickListener {

	ImageButton ibtn_tmall;
	ImageButton ibtn_jd;
	ImageButton ibtn_1hao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_acti_buydevice);
		ibtn_tmall = (ImageButton) findViewById(R.id.acti_bdevice_ibtn_tmall);
		ibtn_jd = (ImageButton) findViewById(R.id.acti_bdevice_ibtn_jd);
		ibtn_1hao = (ImageButton) findViewById(R.id.acti_bdevice_ibtn_1hao);
		ibtn_tmall.setOnClickListener(this);
		ibtn_jd.setOnClickListener(this);
		ibtn_1hao.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPause(this);
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.acti_bdevice_ibtn_tmall:
			Uri uri1 = Uri.parse("https://lanbao.tmall.com/?ali_trackid=17_f4b1ec7576ec5f043744ad9a09b8889c");
			Intent intent1 = new Intent(Intent.ACTION_VIEW, uri1);
			startActivity(intent1);
			break;

		case R.id.acti_bdevice_ibtn_jd:
			Uri uri2 = Uri.parse("http://mall.jd.com/index-24454.html?cpdad=1DLSUE");
			Intent intent2 = new Intent(Intent.ACTION_VIEW, uri2);
			startActivity(intent2);
			break;

		case R.id.acti_bdevice_ibtn_1hao:
			Uri uri3 = Uri
					.parse("http://shop.yhd.com/dpzx/m-156767.html?tp=51.蓝堡.121.0.2.KwqhkbN-10-EX1uu&glTrueReffer=http%3A%2F%2Fsearch.yhd.com%2Fc0-0%2Fk%2525E8%252593%25259D%2525E5%2525A0%2525A1%2F2%2F%3Ftp%3D1.1.12.0.15.KwqhbY3-10-EX1uu");
			Intent intent3 = new Intent(Intent.ACTION_VIEW, uri3);
			startActivity(intent3);
			break;
		}
	}
}