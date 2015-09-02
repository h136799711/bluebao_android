package com.itboye.bluebao.adapter;

import java.io.IOException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.itboye.bluebao.R;
import com.itboye.bluebao.bean.Frag_tab_target_Aim;
import com.itboye.bluebao.util.UtilStream;

/**
 * frag_tab_target中的listview的adapter
 * 
 * @author Administrator
 *
 */
public class AdapterForAim extends BaseAdapter {

	private Context context;
	private ArrayList<Frag_tab_target_Aim> aims;// aimsForAdaptre
	private ArrayList<Frag_tab_target_Aim> allAims;// 包含各个日期的aims

	// =================================================
	@Override
	public int getCount() {
		return aims.size();
	}

	@Override
	public Object getItem(int position) {
		return aims.get(position);
	}
	
/*	*//**
	 * 去除第position
	 * @param position
	 *//*
  public void removeItem(int position){
        aims.remove(position);
        notifyDataSetChanged();
    }*/

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	
		LinearLayout view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_fragment_tab_target_item, null);
		TextView tv_time = (TextView) view.findViewById(R.id.frag_tab_target_item_time);
		TextView tv_goal = (TextView) view.findViewById(R.id.frag_tab_target_item_goal);
		
		//==8.12==========
		ImageButton ibtn_edit = (ImageButton) view.findViewById(R.id.frag_tab_target_ibtn_edit);
		ImageButton ibtn_delete = (ImageButton) view.findViewById(R.id.frag_tab_target_ibtn_delete);
		
		ibtn_delete.setOnClickListener(new MyClickListenerInItem(position));
		ibtn_edit.setOnClickListener(new MyClickListenerInItem(position));
		//==8.12==========
		
		Frag_tab_target_Aim aim = aims.get(position);
		tv_time.setText(aim.getTime_hour()+" 小时 " + aim.getTime_minute()+" 分钟");
		tv_goal.setText(aim.getGoal()+"  大卡");

		return view;
	}

	// =================================================

	/**
	 * 构造函数
	 */
	public AdapterForAim(Context context, ArrayList<Frag_tab_target_Aim> aims,ArrayList<Frag_tab_target_Aim> allAims) {
		this.context = context;
		this.aims = aims;
		this.allAims = allAims;
	}

	// =================================================

	/**
	 * Item中ImageButton的onClick事件
	 */
	private class MyClickListenerInItem implements View.OnClickListener {
		
		private static final String SP_FILE_NAME = "frag_tab_target_aims";
		private static final String SP_FILE_NAME_KEY = "aims";
		protected static final String TAG = "-----MyClickListenerInItem";
		private SharedPreferences spItem;
		private SharedPreferences.Editor editorItem;
		
		//不管是修改还是删除，传过来的position都是一个，所以修改的时候可以直接获取
		//这个position是aims中的position，即adapter中的；
		//而不是allAims中的position
		private int position; 
		

		//修改之后aim的新数据初始化，设置为类的成员变量
		//String afterDayOfWeek = "";
		int afterHour = 0;
		int afterMinute = 0;
		int afterGoal = 0;
		
		//构造函数
		public MyClickListenerInItem(int pos){
			this.position = pos;
			this.spItem = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE); // 只能被本应用程序读写;
			this.editorItem = spItem.edit();
		}
		
		//====================================
		@Override
		public void onClick(View v) {
			switch ( v.getId() ) {
			
			case R.id.frag_tab_target_ibtn_delete : //删除
				Toast.makeText(context, "要删除第" + position +"个item。", Toast.LENGTH_SHORT).show();
				Frag_tab_target_Aim aimToDelete = aims.get(position);
				allAims.remove(aimToDelete);
				
				Log.i("-----AdapterForAim", "after delete aims(position) allAims are :" + allAims.toString() );
				
				//把SP中相应的数据也擦除掉
				String afterDeleteTheItem = "";
				try {
					afterDeleteTheItem = UtilStream.SurveyList2String(allAims);
				} catch (IOException e) {
					e.printStackTrace();
				}
				editorItem.putString(SP_FILE_NAME_KEY, afterDeleteTheItem);
				editorItem.commit();
				
				//更新Adapter
				aims.remove(position);
				notifyDataSetChanged();
				
				break;
				
				//====================================
			case R.id.frag_tab_target_ibtn_edit : //修改
				
				//获取修改前aim数据
				final Frag_tab_target_Aim aimToEdit = aims.get(position);
				final String beforeDayOfWeek = aimToEdit.getDayOfWeek();// 目标设定日期
				final String beforeHour = aimToEdit.getTime_hour();
				final String beforeMinute = aimToEdit.getTime_minute();
				final String beforeGoal = aimToEdit.getGoal();
	
				Toast.makeText(context, "要修改第" + position +"个item。", Toast.LENGTH_SHORT).show();
				//弹出选择框供用户修改,如果没有选择，则值
				LinearLayout dialog_aim = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_dialog_aim, null);

				// picTime and picGoal
				NumberPicker picChangeTimeHour = (NumberPicker) dialog_aim.findViewById(R.id.dialog_aim_np_time_hour);
				NumberPicker picChangeTimeMinute = (NumberPicker) dialog_aim.findViewById(R.id.dialog_aim_np_time_minute);
				NumberPicker picChangeGoal = (NumberPicker) dialog_aim.findViewById(R.id.dialog_aim_np_goal);

				// 小时数
				picChangeTimeHour.setMinValue(0);
				picChangeTimeHour.setMaxValue(24);
				picChangeTimeHour.setValue( Integer.parseInt(beforeHour) ); // 默认显示修改前数据
				picChangeTimeHour.setOnValueChangedListener(new OnValueChangeListener() {

					@Override
					public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
							afterHour = newVal; //oldVal前一个选中的值，newValue当前选中的值
					}
				});

				// 分钟数
				picChangeTimeMinute.setMinValue(0);
				picChangeTimeMinute.setMaxValue(59);
				picChangeTimeMinute.setValue( Integer.parseInt(beforeMinute) ); // 默认显示修改前数据
				picChangeTimeMinute.setOnValueChangedListener(new OnValueChangeListener() {

					@Override
					public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
							afterMinute = newVal;
					}
				});

				// 卡路里数
				picChangeGoal.setMinValue(0);
				picChangeGoal.setMaxValue(1000);
				picChangeGoal.setValue( Integer.parseInt(beforeGoal) ); // 默认显示修改前数据
				picChangeGoal.setOnValueChangedListener(new OnValueChangeListener() {

					@Override
					public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
							afterGoal = newVal;
					}
				});

				// ==设定目标的弹出框==========---------------------------------------------------------------------------------------------

				// AlertDialog
				AlertDialog.Builder editBuilder = new AlertDialog.Builder(context);
				editBuilder.setTitle("修改目标").setView(dialog_aim)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						int finalHour ;
						int finlaMinute;
						int finalGoal;
						
						//先判断是否为0，若是0，
						//可能是原数据为0: 取原数据0
						//也可能是修改的时候没有选择：取原数据。
						//总之，是0就去原数据，否则就去新数据，即afterHour
						if ( afterHour==0 ){ 
							afterHour = Integer.parseInt( beforeHour );
						}
						if ( afterMinute==0 ){
							afterMinute = Integer.parseInt( beforeMinute );
						}
						if ( afterGoal==0 ){
							afterGoal = Integer.parseInt( beforeGoal );
						}
						
						//点击编辑之后，不管用户是否重新选择了数值，都新建一个aim，并对addAims和aims进行更新
						Log.i(TAG, "开始修改");
						Log.i(TAG, "新建aim并复制");
						
							//1 更新allAim，写进SP中
							Frag_tab_target_Aim aimAfterEdited = new Frag_tab_target_Aim();
							aimAfterEdited.setDayOfWeek(beforeDayOfWeek);//设定目标的日期不变
							aimAfterEdited.setGoal(afterGoal+"");
							aimAfterEdited.setTime_hour(afterHour+"");
							aimAfterEdited.setTime_minute(afterMinute+"");
							
							Log.i(TAG, "新aim中数据：" +aimAfterEdited.getDayOfWeek() +"  " + aimAfterEdited.getTime_hour() + "  " + aimAfterEdited.getTime_minute()
																					+"  " +aimAfterEdited.getGoal());
							
							Log.i(TAG, "修改allAims中数据");
							//更新allAims中的aim,不能根据position，因为position是aims中的，而不是allAims中的
							allAims.remove(aimToEdit);
							allAims.add(aimAfterEdited);
							
							Log.i(TAG, "修改SP中数据");
							String strtemp = "";
							try {
								strtemp = UtilStream.SurveyList2String(allAims);
							} catch (IOException e) {
								e.printStackTrace();
							}
							editorItem.putString(SP_FILE_NAME_KEY, strtemp);
							editorItem.commit();
							
							Log.i(TAG, "修改aims中数据");
							//2 更新adapter
							aims.set(position, aimAfterEdited);//更新aims中的aim
							//aims.remove(position);
							//aims.add(position, aimAfterEdited);
							notifyDataSetChanged();
							
						
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				})
				.create().show();
				
				break;
				
				
				
					
				
				
			}
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
