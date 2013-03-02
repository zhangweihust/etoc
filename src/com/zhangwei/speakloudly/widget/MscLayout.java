package com.zhangwei.speakloudly.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class MscLayout extends ViewGroup {
	public static final String TAG_DEV = "test";
	
	private OnSoftInputListener onSoftInputListener;

	public MscLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public MscLayout(Context context, AttributeSet aSet) { 
		super(context, aSet); 
	}

	public MscLayout(Context context, AttributeSet set, int style) {
		super(context, set, style);
	}
	
	
	@Override
	protected void onMeasure(int width, int height) {
		super.onMeasure(width, height);
		int count = getChildCount();
		for(int i = 0; i<count; i++){
			final View childView = getChildAt(i);
		    final int childHeight = childView.getMeasuredHeight();
			getChildAt(i).measure(width, childHeight);
		}
	}

/*	@Override
	public void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		int count = getChildCount();
		for(int i = 0; i<count; i++){
			getChildAt(i).layout(0, 0, right - left, bottom - top);
		}
	}*/
	@Override
	public void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
	    int childTop=0;
	    // 获取所有的子View的个数
	    final int childCount = getChildCount();
	    int totalHeightOfChild = 0;
	    int gapHeight=0;
	    int gapDelta=0;
	    
	    for (int i = 0; i < childCount; i++) {
		       final View childView = getChildAt(i);
		       final int childHeight = childView.getMeasuredHeight();
		       totalHeightOfChild +=childHeight;
	    }
	    
	    gapHeight = (bottom-top)- totalHeightOfChild;
	    
	    if(gapHeight<0){
	    	gapHeight=0;
	    }
	    
	    if(childCount>1){
	    	gapDelta=gapHeight/(childCount-1);
	    }else{
	    	gapDelta=0;
	    }
	    
	    for (int i = 0; i < childCount; i++) {
	       final View childView = getChildAt(i);

	       final int childWidth = childView.getMeasuredWidth();
	       final int childHeight = childView.getMeasuredHeight();

	       childView.layout(0, childTop, right, childTop + childHeight);
	       // 下一个VIew的左边左边+一个
	       childTop += childHeight + gapDelta;
	   }
	}

	@Override
	protected void onSizeChanged(int width, int heigth, int oldWidth, int oldHeight) {
		if(onSoftInputListener!=null){
			if (heigth < oldHeight) { 
				onSoftInputListener.onShow();
			}else{
				onSoftInputListener.onHide();
			}
		}
	}
	
	public void setOnSoftInputListener(OnSoftInputListener onSoftInputListener) {
		this.onSoftInputListener = onSoftInputListener;
	}
	
	public interface OnSoftInputListener{
		
		void onShow();
		
		void onHide();
		
	}
}
