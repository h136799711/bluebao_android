package com.itboye.bluebao.exwidget;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.itboye.bluebao.R;
import com.itboye.bluebao.util.Util;



public class RoundProcessBar extends View {
	/**
	 * 画笔对象的引用
	 */
	private Paint paint;
	
	
	private int colorCircle;// 外圆颜色
	
	private int colorProcessBar; //进度条颜色
	private float widthProcessBar; //进度条宽度
	
	private int colorAlready;//"已完成" 颜色，字体大小
	private float textSizeAlready;
	
	private int colorPercent; // 进度百分比颜色，字体大小
	private float textSizePercent;
	
	private int colorLine; // 线条
	private float heightLine;
	
	private int colorAim; // "目标"颜色，字体大小
	private float textSizeAim;
	
	private int colorAimNumber; // 目标值颜色，字体大小
	private float textSizeAimNumber;
	
	private int max; //最大进度
	//private int progress; //当前进度
	private float progress; //当前进度
	private boolean textIsDisplayable; //是否显示中间的进度
	private int aimNumber; // 设定的目标值
	
	private int style; //实心或者空心
	public static final int STROKE = 0;
	public static final int FILL = 1;
	
	public RoundProcessBar(Context context) {
		this(context, null);
	}

	public RoundProcessBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public RoundProcessBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		paint = new Paint();
		
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,R.styleable.RoundProcessBar);
		
		//获取自定义属性和默认值
		colorCircle = mTypedArray.getColor(R.styleable.RoundProcessBar_colorCircle, Color.LTGRAY);
		
		colorProcessBar = mTypedArray.getColor(R.styleable.RoundProcessBar_colorProcessBar, Color.GREEN);
		widthProcessBar = mTypedArray.getDimension(R.styleable.RoundProcessBar_widthProcessBar, 10);
		
		colorAlready = mTypedArray.getColor(R.styleable.RoundProcessBar_colorAlready, R.color.colorAlready);
		textSizeAlready = mTypedArray.getDimension(R.styleable.RoundProcessBar_textSizeAlready, 35);
		
		colorPercent = mTypedArray.getColor(R.styleable.RoundProcessBar_colorPercent, R.color.colorPercent);
		textSizePercent = mTypedArray.getDimension(R.styleable.RoundProcessBar_textSizePercent, 60);
		
		colorLine = mTypedArray.getColor(R.styleable.RoundProcessBar_colorLine, R.color.colorLine);
		heightLine = mTypedArray.getDimension(R.styleable.RoundProcessBar_heightLine, 1);
		
		colorAim = mTypedArray.getColor(R.styleable.RoundProcessBar_colorAim, R.color.colorAim);
		textSizeAim = mTypedArray.getDimension(R.styleable.RoundProcessBar_textSizeAim, 20);
		
		colorAimNumber = mTypedArray.getColor(R.styleable.RoundProcessBar_colorAimNumber, R.color.colorAimNumber);
		textSizeAimNumber = mTypedArray.getDimension(R.styleable.RoundProcessBar_textSizeAimNumber, 40);
		
		max = mTypedArray.getInteger(R.styleable.RoundProcessBar_max, 100);
		textIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProcessBar_textIsDisplayable, true);
		style = mTypedArray.getInt(R.styleable.RoundProcessBar_style, 0);
		
		mTypedArray.recycle();
	}
	

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		 //画圆环
		int center = getWidth()/2; //获取圆心的x坐标。getWidth()获取控件的宽度，这里既是圆的直径
		int radius = (int) (center - widthProcessBar/2); //圆环的半径
		paint.setColor(colorCircle); //设置圆环的颜色
		paint.setStyle(Paint.Style.STROKE); //设置空心
		paint.setStrokeWidth(widthProcessBar); //设置圆环的宽度
		paint.setAntiAlias(true);  //消除锯齿 
		canvas.drawCircle(center, center, radius, paint); 
		
		Log.e("log", center + "");
		
		 //画出 “已完成” 三个字
		paint.setStrokeWidth(0); //设置画笔宽度
		paint.setColor(colorAlready);
		paint.setTextSize(textSizeAlready);// 字体大小
		paint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
		float textWidthAlready = paint.measureText( "已完成");   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
		canvas.drawText( "已完成", center - textWidthAlready / 2, (float)(0.8*center - textWidthAlready/2), paint); //画出进度百分比
		
		 //显示进度百分比，不断变化
		paint.setStrokeWidth(0); //设置画笔宽度
		paint.setColor(colorPercent);
		paint.setTextSize(textSizePercent);// 字体大小
		paint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
		int percent = (int)(((float)progress / (float)max) * 100);  //中间的进度百分比，先转换成float在进行除法运算，不然都为0
		float textWidth = paint.measureText(percent + "%");   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
		if(textIsDisplayable && percent != 0 && style == STROKE){
			canvas.drawText(percent + "%", center - textWidth / 2, center + textSizePercent/2, paint); //画出进度百分比
		}
		
		 //画出 百分比下边的横线
		paint.setStrokeWidth(0); //设置画笔宽度
		paint.setColor(colorLine);
		//canvas.drawLine((float)(1-0.5)*center + (float)(1.1*widthProcessBar), (float)(1+ 0.4)*center, (float)(1+0.5)*center - (float)(1.1*widthProcessBar), (float)(1+ 0.4)*center, paint);
		canvas.drawLine((float)(1-0.5)*radius -10 , (float)(1+ 0.4)*center, (float)(1+0.5)*radius + 15 , (float)(1+ 0.4)*center, paint);
		
		//显示 目标
		paint.setStrokeWidth(0); //设置画笔宽度
		paint.setColor(colorAim);
		paint.setTextSize(textSizeAim);// 字体大小
		paint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
		float textWidthAim = paint.measureText("目标");   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
		canvas.drawText("目标", (float)(1-0.5)*center + (float)(1.3*widthProcessBar), (float)(1+0.5)*center + textWidthAim/2, paint); //画出进度百分比
		
		//显示 目标数字
		paint.setStrokeWidth(0); //设置画笔宽度
		paint.setColor(colorAim);
		paint.setTextSize(textSizeAimNumber);// 字体大小
		paint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
		//float textWidthAimNumber = paint.measureText(aimNumber+"大卡");   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
		//canvas.drawText(aimNumber+"大卡", center , (float)(1+0.5)*center + textWidthAimNumber/3, paint); //画出进度百分比
		float textWidthAimNumber = paint.measureText(aimNumber+"");   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
		canvas.drawText(aimNumber+"", center , (float)(1+0.5)*center + textWidthAimNumber/3+6, paint); //画出进度百分比
		
		 //画processbar的进度
		//设置进度是实心还是空心
		paint.setStrokeWidth(widthProcessBar); //设置圆环的宽度
		paint.setColor(colorProcessBar);  //设置进度的颜色
		//用于定义的圆弧的形状和大小的界限，left top right bottom，得到的oval是一个矩形
		RectF oval = new RectF(center - radius, center - radius, center+ radius, center + radius);  
		
		switch (style) {
		case STROKE:{
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawArc(oval, 270, 360 * progress / max, false, paint);  //根据进度画圆弧
			break;
		}
		case FILL:{
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			if(progress !=0)
				canvas.drawArc(oval, 0, 360 * progress / max, true, paint);  //根据进度画圆弧
			break;
		}
		}
		
	}
	
	
	public synchronized int getMax() {
		return max;
	}

	/**
	 * 设置进度的最大值
	 * @param max
	 */
	public synchronized void setMax(int max) {
		if(max < 0){
			throw new IllegalArgumentException("max not less than 0");
		}
		this.max = max;
	}

	/**
	 * 获取进度.需要同步
	 * @return
	 */
	public synchronized float getProgress() {
		return progress;
	}

	/**
	 * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
	 * 刷新界面调用postInvalidate()能在非UI线程刷新
	 * @param progress
	 */
	public synchronized void setProgress(float progress) {
		if(progress < 0){
			throw new IllegalArgumentException("progress not less than 0");
		}
		if(progress > max){
			progress = max;
		}
		if(progress <= max){
			this.progress = progress;
			postInvalidate();
		}
		
	}
	
	public int getColorCircle() {
		return colorCircle;
	}

	public void setColorCircle(int colorCircle) {
		this.colorCircle = colorCircle;
	}


	public int getColorProcessBar() {
		return colorProcessBar;
	}

	public void setColorProcessBar(int colorProcessBar) {
		this.colorProcessBar = colorProcessBar;
	}



	public int getColorPercent() {
		return colorPercent;
	}

	public void setColorPercent(int colorPercent) {
		this.colorPercent = colorPercent;
	}

	public float getTextSizePercent() {
		return textSizePercent;
	}

	public void setTextSizePercent(float textSizePercent) {
		this.textSizePercent = textSizePercent;
	}

	public float getWidthProcessBar() {
		return widthProcessBar;
	}

	public void setWidthProcessBar(float widthProcessBar) {
		this.widthProcessBar = widthProcessBar;
	}

	public int getColorAlready() {
		return colorAlready;
	}

	public void setColorAlready(int colorAlready) {
		this.colorAlready = colorAlready;
	}

	public float getTextSizeAlready() {
		return textSizeAlready;
	}

	public void setTextSizeAlready(float textSizeAlready) {
		this.textSizeAlready = textSizeAlready;
	}

	public int getColorAim() {
		return colorAim;
	}

	public void setColorAim(int colorAim) {
		this.colorAim = colorAim;
	}

	public float getTextSizeAim() {
		return textSizeAim;
	}

	public void setTextSizeAim(float textSizeAim) {
		this.textSizeAim = textSizeAim;
	}

	public int getColorAimNumber() {
		return colorAimNumber;
	}

	public void setColorAimNumber(int colorAimNumber) {
		this.colorAimNumber = colorAimNumber;
	}

	public float getTextSizeAimNumber() {
		return textSizeAimNumber;
	}

	public void setTextSizeAimNumber(float textSizeAimNumber) {
		this.textSizeAimNumber = textSizeAimNumber;
	}

	public int getColorLine() {
		return colorLine;
	}

	public void setColorLine(int colorLine) {
		this.colorLine = colorLine;
	}

	public float getHeightLine() {
		return heightLine;
	}

	public void setHeightLine(float heightLine) {
		this.heightLine = heightLine;
	}

	public int getAimNumber() {
		return aimNumber;
	}

	public void setAimNumber(int aimNumber) {
		this.aimNumber = aimNumber;
	}


}

