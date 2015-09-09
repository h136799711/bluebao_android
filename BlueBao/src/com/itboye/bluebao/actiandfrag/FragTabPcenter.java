package com.itboye.bluebao.actiandfrag;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itboye.bluebao.R;
import com.itboye.bluebao.bean.BestScoreBean;
import com.itboye.bluebao.bean.PInfo;
import com.itboye.bluebao.bean.TotalScoreBean;
import com.itboye.bluebao.exwidget.CircleImageView;
import com.itboye.bluebao.util.Util;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * fragment home for tab pcenter
 * @author Administrator   
 */
public class FragTabPcenter extends Fragment {

	protected static final String TAG = "-----FragTabPcenter";
	private SharedPreferences sp;
	private Gson gson = new Gson();

	//private ImageView iv_userimg;
	private CircleImageView iv_userimg;
	private TextView tv_usernickname;
	private TextView tv_userSentence;
	private TextView tv_userHeight;
	private TextView tv_userWeight;
	private TextView tv_userBMI;
	private TextView tv_totalMiles;
	private TextView tv_totalTime;
	private TextView tv_totalCars;
	private TextView tv_bestMiles;
	private TextView tv_bestTime;
	private TextView tv_bestCars;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout_fragment_pcenter = inflater.inflate(R.layout.layout_fragment_tab_pcenter, container, false);

		iv_userimg = (CircleImageView) layout_fragment_pcenter.findViewById(R.id.frag_tab_pcenter_iv_userimg);
		tv_usernickname = (TextView) layout_fragment_pcenter.findViewById(R.id.frag_tab_pcenter_et_username);
		tv_userSentence = (TextView) layout_fragment_pcenter.findViewById(R.id.frag_tab_pcenter_et_userSentence);
		tv_userHeight = (TextView) layout_fragment_pcenter.findViewById(R.id.frag_tab_pcenter_et_userHeight);
		tv_userWeight = (TextView) layout_fragment_pcenter.findViewById(R.id.frag_tab_pcenter_et_userWeight);
		tv_userBMI = (TextView) layout_fragment_pcenter.findViewById(R.id.frag_tab_pcenter_et_userBMI);
		tv_totalMiles = (TextView) layout_fragment_pcenter.findViewById(R.id.frag_tab_pcenter_tv_userTotalLength);
		tv_totalTime = (TextView) layout_fragment_pcenter.findViewById(R.id.frag_tab_pcenter_tv_userTotalTime);
		tv_totalCars = (TextView) layout_fragment_pcenter.findViewById(R.id.frag_tab_pcenter_tv_userTotalCalorie);
		tv_bestMiles = (TextView) layout_fragment_pcenter.findViewById(R.id.frag_tab_pcenter_tv_userLongestLength);
		tv_bestTime = (TextView) layout_fragment_pcenter.findViewById(R.id.frag_tab_pcenter_tv_userLongestTime);
		tv_bestCars = (TextView) layout_fragment_pcenter.findViewById(R.id.frag_tab_pcenter_tv_userMostCalorie);

		return layout_fragment_pcenter;
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

		// 如果在SP中有用户信息，则显示
		sp = getActivity().getSharedPreferences(Util.SP_FN_PINFO, Context.MODE_PRIVATE);
		String strObj = sp.getString(Util.SP_KEY_PINFO, "");
		if (!strObj.isEmpty()) {
			PInfo pInfo = gson.fromJson(strObj, PInfo.class);
			tv_usernickname.setText(pInfo.getNickname());
			tv_userSentence.setText(pInfo.getSignature());
			tv_userHeight.setText(pInfo.getHeight() + "");
			tv_userWeight.setText(pInfo.getWeight() + "");
			//tv_userBMI.setText(pInfo.getBMI() + "");
			tv_userBMI.setText("正常");
		}else{
			tv_usernickname.setText("爱运动 享自由");
			tv_userSentence.setText("个性签名");
			tv_userHeight.setText("165");
			tv_userWeight.setText( "65");
			tv_userBMI.setText("正常");
		}

		int uId = Util.uId;
		if (uId != 0) {
			getDataAndShow(uId);
		} else {
			tv_totalCars.setText("0卡");
			tv_totalTime.setText("0时");
			tv_totalMiles.setText("0公里");

			tv_bestCars.setText("0卡");
			tv_bestTime.setText("0时");
			tv_bestMiles.setText("0公里");
		}
		super.onResume();
	}

	private void getDataAndShow(final int uId) {
		getTotalDataAndShow(uId);
		getBestDataAndShow(uId);
	}

	private void getTotalDataAndShow(final int uId) {

		String token = Util.getAccessToken(getActivity());
		if (token.isEmpty()) {
			try {
				Thread.sleep(2000);
				token = Util.getAccessToken(getActivity());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (!token.isEmpty()) {

			HttpUtils httpUtils = new HttpUtils();

			// （1）获取总成绩
			RequestParams params = new RequestParams();
			params.addBodyParameter("uid", uId + "");

			String urlGetTotalData = Util.urlGetTotalData + token;
			//Log.i(TAG, urlGetTotalData);

			httpUtils.send(HttpMethod.POST, urlGetTotalData, params, new RequestCallBack<String>() {
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					//Log.i(TAG, "获取总成绩失败：" + arg1);
					tv_totalMiles.setText("0公里");
					tv_totalTime.setText("0时");
					tv_totalCars.setText("0卡");
					//Toast.makeText(getActivity(), "获取总成绩失败" + arg1, Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					//Log.i(TAG, "获取总成绩成功：" + arg0.result);
					TotalScoreBean tsb = gson.fromJson(arg0.result, TotalScoreBean.class);
					//Log.i(TAG, tsb.getData().getSum_max_distance() + tsb.getData().getSum_max_time()
					//		+ tsb.getData().getSum_max_calorie());

					if (tsb.getData().getSum_max_distance() != null) {
						//Log.i(TAG, "true");
						tv_totalMiles.setText(tsb.getData().getSum_max_distance() + "公里");
					} else {
						//Log.i(TAG, "false");
						tv_totalMiles.setText("0公里");
					}

					if (tsb.getData().getSum_max_time() != null) {
						float time = Float.parseFloat(tsb.getData().getSum_max_time()) / (float) 3600.0;
						if( time<1f){
							tv_totalTime.setText("0"+Util.df.format(time) + "时");
						}else{
							tv_totalTime.setText(Util.df.format(time) + "时");
						}
						
					} else {
						tv_totalTime.setText("0时");
					}

					if (tsb.getData().getSum_max_calorie() != null) {
						tv_totalCars.setText(tsb.getData().getSum_max_calorie() + "卡");
					} else {
						tv_totalCars.setText("0卡");
					}
				}
			});
		} else {
			Toast.makeText(getActivity(), "获取token失败", Toast.LENGTH_SHORT).show();
		}
	}

	private void getBestDataAndShow(final int uId) {
		// 执行获取token和最好成绩

		// 1 获取token
		String token = Util.getAccessToken(getActivity());
		if (token.isEmpty()) {
			try {
				Thread.sleep(2000);
				token = Util.getAccessToken(getActivity());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// 2 获取数据
		if (!token.isEmpty()) {

			HttpUtils httpUtils = new HttpUtils();

			// （2）获取最好成绩
			RequestParams params2 = new RequestParams();
			params2.addBodyParameter("uid", uId + "");

			String urlGetBestData = Util.urlGetBestData + token;
			//Log.i(TAG, urlGetBestData);

			httpUtils.send(HttpMethod.POST, urlGetBestData, params2, new RequestCallBack<String>() {
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					Log.i(TAG, "获取最好成绩失败：" + arg1);
					tv_bestMiles.setText("0公里");
					tv_bestTime.setText("0时");
					tv_bestCars.setText("0卡");
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					Log.i(TAG, "获取最好成绩成功：" + arg0.result);
					String temp1 = Util.deletebracket(arg0.result);
					//Log.i(TAG, "temp is:" + temp1.trim());
					BestScoreBean bsb = gson.fromJson(temp1, BestScoreBean.class);
					//Log.i(TAG, bsb.getData().getBest_distance() + "  " + bsb.getData().getBest_cost_time() + "  "
					//		+ bsb.getData().getBest_calorie());

					if (bsb.getData().getBest_distance() != null) {
						tv_bestMiles.setText(bsb.getData().getBest_distance() + "公里");
					} else {
						tv_bestMiles.setText("0公里");
					}

					if (bsb.getData().getBest_cost_time() != null) {
						float time = Float.parseFloat(bsb.getData().getBest_cost_time()) / (float) 3600.0;
						if( time<1f){
							tv_bestTime.setText("0"+Util.df.format(time) + "时");
						}else{
							tv_bestTime.setText(Util.df.format(time) + "时");
						}
					} else {
						tv_bestTime.setText("0时");
					}

					if (bsb.getData().getBest_calorie() != null) {
						tv_bestCars.setText(bsb.getData().getBest_calorie() + "卡");
					} else {
						tv_bestCars.setText("0卡");
					}
				}

			});

		} else {
			//Log.i(TAG, "token is empty");
			Toast.makeText(getActivity(), "获取token失败", Toast.LENGTH_SHORT).show();
		}
	}
}