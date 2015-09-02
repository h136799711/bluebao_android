package com.itboye.bluebao.exwidget;

import com.itboye.bluebao.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

/**
 * 自定义组件：带清除功能的EditText
 * 
 * @author Administrator
 *
 */
public class ExClearEditText extends EditText implements OnFocusChangeListener, TextWatcher {

	private Drawable mClearDrawable; // 删除图标的引用

	private boolean hasFoucs; // 控件是否得到焦点

	// =================================================

	/**
	 * 三个构造方法
	 */
	public ExClearEditText(Context context) {
		this(context, null);
	}

	public ExClearEditText(Context context, AttributeSet attrs) {
		// 这里构造方法也很重要，不加这个很多属性不能再XML里面定义
		this(context, attrs, android.R.attr.editTextStyle);
	}

	public ExClearEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	// =================================================

	/**
	 * 初始化并添加监听事件
	 */
	private void init() {

		// textView.getCompoundDrawables(Drawable left, Drawable top, Drawable
		// right, Drawable bottom)
		mClearDrawable = getCompoundDrawables()[2]; // 获取EditText右侧的图标

		if (mClearDrawable == null) {
			mClearDrawable = getResources().getDrawable(R.drawable.exwidget_clearedittext_right_clear);
		}

		// 组件在容器X Y轴上的起点 宽高
		// 真正的宽度 高度
		mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());

		// 默认设置隐藏图标
		setClearIconVisible(false);

		// 设置焦点改变的监听
		setOnFocusChangeListener(this);

		// 设置输入框里面内容发生改变的监听
		addTextChangedListener(this);

	}

	/**
	 * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
	 * 
	 * @param visible
	 */
	protected void setClearIconVisible(boolean visible) {
		Drawable right = visible ? mClearDrawable : null;
		setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
	}

	/**
	 * implement OnFocusChangeListener 要实现的方法
	 * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
	 */
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		this.hasFoucs = hasFocus;
		if (hasFocus) {
			setClearIconVisible(getText().length() > 0); // 输入框中有文字时：true
		} else {
			setClearIconVisible(false);
		}
	}

	/**
	 * implement TextWatcher 要实现的方法 当输入框里面内容发生变化的时候回调的方法
	 */
	@Override
	public void onTextChanged(CharSequence s, int start, int count, int after) {
		if (hasFoucs) {
			setClearIconVisible(s.length() > 0);
		}
	}

	/**
	 * implement TextWatcher 要实现的方法
	 */
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	// =================================================

	/**
	 * 最主要的监听事件 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件 当我们按下的位置 在
	 * EditText的宽度 - 图标到控件右边的间距 - 图标的宽度 和 EditText的宽度 -
	 * 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
	 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_UP) {

			if (getCompoundDrawables()[2] != null) {

				boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
						&& (event.getX() < ((getWidth() - getPaddingRight())));

				if (touchable) {
					this.setText("");
				}
			}
		}
		return super.onTouchEvent(event);
	}

}