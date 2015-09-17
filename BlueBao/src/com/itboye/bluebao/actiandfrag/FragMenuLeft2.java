package com.itboye.bluebao.actiandfrag;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.itboye.bluebao.R;
import com.itboye.bluebao.bean.PInfo;
import com.itboye.bluebao.ble2.ActiBleScan;
import com.itboye.bluebao.breceiver.ReceiverTool;
import com.itboye.bluebao.exwidget.CircleImageView;
import com.itboye.bluebao.util.Util;

/**
 * fragment left_menu for drawer
 * 
 * @author Administrator
 */
public class FragMenuLeft2 extends Fragment {

	protected static final String TAG = "-----FragMenuLeft";
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	private Gson gson = new Gson();

	private CircleImageView iv_userimg;
	private TextView tv_username;
	private TextView tv_signature;
	private TextView tv_gerenziliao;
	private TextView tv_shebeiguanli;
	private TextView tv_yundongshuju;
	private TextView tv_mubiaoguanli;
	private TextView tv_guanyulanbao;
	private TextView tv_goumaiqicai;
	private TextView tv_shezhi;
	private TextView tv_zhuxiao;
	private ToggleButton tb_switch;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.layout_fragment_menu_left2, container, false);
		iv_userimg = (CircleImageView) view.findViewById(R.id.fragment_menu_left_top_userimg);
		tv_username = (TextView) view.findViewById(R.id.frag_menu_left_tv_username);
		tv_signature = (TextView) view.findViewById(R.id.frag_menu_left_tv_gexingqianming);
		tv_gerenziliao = (TextView) view.findViewById(R.id.frag_menu_left_gerenziliao);
		tv_shebeiguanli = (TextView) view.findViewById(R.id.frag_menu_left_shebeiguanli);
		tv_yundongshuju = (TextView) view.findViewById(R.id.frag_menu_left_yundongshuju);
		tv_mubiaoguanli = (TextView) view.findViewById(R.id.frag_menu_left_mubiaoguanli);
		tv_guanyulanbao = (TextView) view.findViewById(R.id.frag_menu_left_guanyulanbao);
		tv_goumaiqicai = (TextView) view.findViewById(R.id.frag_menu_left_goumaiqicai);
		tv_shezhi = (TextView) view.findViewById(R.id.frag_menu_left_shezhi);
		tv_zhuxiao = (TextView) view.findViewById(R.id.frag_menu_left_zhuxiao);
		tb_switch = (ToggleButton) view.findViewById(R.id.frag_menu_left_tb_switch);

		tv_gerenziliao.setOnClickListener(new MyOnClickListener());
		tv_shebeiguanli.setOnClickListener(new MyOnClickListener());
		tv_yundongshuju.setOnClickListener(new MyOnClickListener());
		tv_mubiaoguanli.setOnClickListener(new MyOnClickListener());
		tv_guanyulanbao.setOnClickListener(new MyOnClickListener());
		tv_goumaiqicai.setOnClickListener(new MyOnClickListener());
		tv_shezhi.setOnClickListener(new MyOnClickListener());
		tv_zhuxiao.setOnClickListener(new MyOnClickListener());
		tb_switch.setOnCheckedChangeListener(new MyOnCheckedChangeListener());

		return view;
	}

	@Override
	public void onResume() {
		// 如果用户修改过头像，则显示更改之后的头像
		sp = getActivity().getSharedPreferences(Util.SP_FN_USERIMG, Context.MODE_PRIVATE);
		String strUserImgName = sp.getString(Util.SP_KEY_USERIMG_USE, "");
		String strUserImgPath = sp.getString(Util.SP_KEY_USERIMG_PATH, "");
		String strUserImgUrl = strUserImgPath + "/" + strUserImgName;
		Log.i(TAG, "strUserImgPath is : " + strUserImgPath);
		Log.i(TAG, "strUserImgName is : " + strUserImgName);
		Log.i(TAG, "strUserImgUrl is : " + strUserImgUrl);

		// 若存在，判断其中是否有需要的这张图片
		if (strUserImgUrl.length() != 1) {
			Bitmap photoBitmap = BitmapFactory.decodeFile(strUserImgUrl);
			if (photoBitmap != null) {
				iv_userimg.setImageBitmap(photoBitmap);
			} else {
				iv_userimg.setImageDrawable(getResources().getDrawable(R.drawable.fragment_menu_left_userimg_default));
			}
		} else {
			iv_userimg.setImageDrawable(getResources().getDrawable(R.drawable.fragment_menu_left_userimg_default));
		}

		// 用户名 个性签名
		sp = getActivity().getSharedPreferences(Util.SP_FN_PINFO, Context.MODE_PRIVATE);
		String strtemp = sp.getString(Util.SP_KEY_PINFO, "");
		if (!strtemp.isEmpty()) {
			PInfo pInfo = gson.fromJson(strtemp, PInfo.class);
			tv_username.setText(pInfo.getNickname());
			tv_signature.setText(pInfo.getSignature());
		}
		
		//switch
		sp = getActivity().getSharedPreferences(Util.SP_FN_ALARM, Context.MODE_PRIVATE);
		String alarmOnOrOff = sp.getString(Util.SP_KEY_ALARM, "");
		if ( !alarmOnOrOff.isEmpty() ){
			tb_switch.setChecked(true);
		}else{
			tb_switch.setChecked(false);
		}

		super.onResume();
	}

	private class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.frag_menu_left_gerenziliao:// 个人资料
				Intent intent = new Intent(getActivity(), ActiPersonalInfo.class);
				startActivity(intent);
				break;

			case R.id.frag_menu_left_shebeiguanli:// 设备管理
				Intent intent3 = new Intent(getActivity(), ActiBleScan.class);
				startActivity(intent3);
				break;

			case R.id.frag_menu_left_yundongshuju:// 运动数据
				Intent intent2 = new Intent(getActivity(), ActiAlldata.class);
				startActivity(intent2);
				break;

			case R.id.frag_menu_left_mubiaoguanli:// 目标管理
				Intent intent4 = new Intent(ReceiverTool.SHOW_MY_AIMS);
				getActivity().sendBroadcast(intent4);
				break;

			case R.id.frag_menu_left_guanyulanbao:// 关于蓝堡
				Intent intent5 = new Intent(getActivity(), ActiAboutLB.class);
				startActivity(intent5);
				break;

			case R.id.frag_menu_left_goumaiqicai:// 购买器材
				Intent intent6 = new Intent(getActivity(), ActiBuyDevice.class);
				startActivity(intent6);
				break;

			case R.id.frag_menu_left_shezhi:// 设置
				Intent intent7 = new Intent(getActivity(), ActiConfig.class);
				startActivity(intent7);
				break;

			case R.id.frag_menu_left_zhuxiao:// 注销
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("您确定要注销吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						android.os.Process.killProcess(android.os.Process.myPid());// 这里目前是退出功能
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create().show();

				break;
			}

		}

	}

	private class MyOnCheckedChangeListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			sp = getActivity().getSharedPreferences(Util.SP_FN_ALARM, Context.MODE_PRIVATE);
			editor = sp.edit();
			if ( isChecked ){
				editor.putString(Util.SP_KEY_ALARM, "开启");
				editor.commit();
				Util.setAlarms(getActivity());
			}else{
				editor.putString(Util.SP_KEY_ALARM, "");
				editor.commit();
				Util.cancelAlarms(getActivity());
			}
		}
	}
}