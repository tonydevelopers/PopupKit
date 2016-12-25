package com.lance.popupkit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

/**
 * 弹出底部菜单
 * 采用装饰模式,生成器模式
 * @author ganchengkai
 * 
 */
public class PopupKit implements OnClickListener, OnDismissListener {
	private boolean isOutsideTouchable = true;
	
	private final int CANCEL_BACKKEY = 0;// 按返回键取消
	private final int CANCEL_BACKVIEW = CANCEL_BACKKEY + 1;// 点击空白背景取消
	private final int CANCEL_MANUAL = CANCEL_BACKKEY + 2;// 手动取消
	
	private int cancelFlag = CANCEL_BACKKEY;
	
	private static final int SHADE_VIEW_ID = 100;// 用于检测是否需要dismiss
	private static final int TRANSLATE_DURATION = 200;
	private static final int ALPHA_DURATION = 300;

	private PopupWindow mPopup;
	
	private Activity mContext;
	private ViewGroup mDecordView;// 所有视图之上的容器视图
	private View mRootView;// 内容视图的容器
	private View mShadeView;// 弹出框上半透明阴影
	private View mPanel;

	private PopupListener mListener;

	public PopupKit(View contentView) {
		mPopup = new KitPopupWindow(contentView.getContext());
		mContext = (Activity) contentView.getContext();
		mDecordView = (ViewGroup) mContext.getWindow().getDecorView();
		mPanel = contentView;

		mRootView = createView(mPanel);

		mPopup.setWidth(LayoutParams.MATCH_PARENT);
		mPopup.setHeight(LayoutParams.MATCH_PARENT);
		mPopup.setFocusable(true);
		ColorDrawable clearColor = new ColorDrawable(Color.TRANSPARENT);
		mPopup.setBackgroundDrawable(clearColor);
		mPopup.setOnDismissListener(this);
		
		
		mPopup.setContentView(mRootView);
	}
	
	public View findViewById(int resId){
		return mPanel.findViewById(resId);
	}
	
	public static class Builder {
		private PopupKit popupKit;
		
		public Builder(View contentView) {
			popupKit = new PopupKit(contentView);
		}
		
		public Builder setOutsideTouchable(boolean touchable) {
			popupKit.setOutsideTouchable(touchable);
			return this;
		}
		
		public Builder setOnClickListener(View[] views, OnClickListener listener){
			popupKit.setOnClickListeners(views, listener);
			return this;
		}
		
		public Builder setOnClickListener(int[] resIds, OnClickListener listener){
			popupKit.setOnClickListeners(resIds, listener);
			return this;
		}
		
		public PopupKit show(){
			popupKit.show();
			
			return popupKit;
		}
		
	}
	/**
	 * 创建全屏视图
	 * 
	 * @param menuView
	 * @return
	 */
	private View createView(View menuView) {
		FrameLayout parent = new FrameLayout(mContext);
		parent.setLayoutParams(new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT));
		mShadeView = new View(mContext);
		mShadeView.setLayoutParams(new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT));
		mShadeView.setBackgroundColor(Color.argb(136, 0, 0, 0));
		mShadeView.setId(SHADE_VIEW_ID);
		mShadeView.setOnClickListener(this);

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.BOTTOM;
		mPanel.setLayoutParams(params);
		mPanel.setOnClickListener(null);// 防止点击空白组件后消失

		parent.addView(mShadeView);
		parent.addView(mPanel);
		return parent;
	}

	private Animation createTranslationInAnimation() {
		int type = TranslateAnimation.RELATIVE_TO_SELF;
		TranslateAnimation anim = new TranslateAnimation(type, 0, type, 0,
				type, 1, type, 0);
		anim.setInterpolator(new LinearInterpolator());
		anim.setDuration(TRANSLATE_DURATION);
		return anim;
	}

	private Animation createAlphaInAnimation() {
		AlphaAnimation anim = new AlphaAnimation(0, 1);
		anim.setDuration(ALPHA_DURATION);
		return anim;
	}

	private Animation createTranslationOutAnimation() {
		int type = TranslateAnimation.RELATIVE_TO_SELF;
		TranslateAnimation anim = new TranslateAnimation(type, 0, type, 0,
				type, 0, type, 1);
		anim.setInterpolator(new LinearInterpolator());
		anim.setDuration(TRANSLATE_DURATION);
		anim.setFillAfter(true);
		return anim;
	}

	private Animation createAlphaOutAnimation() {
		AlphaAnimation anim = new AlphaAnimation(1, 0);
		anim.setDuration(ALPHA_DURATION);
		anim.setFillAfter(true);
		return anim;
	}
	
	public boolean isOutsideTouchable() {
		return isOutsideTouchable;
	}

	public void setOutsideTouchable(boolean isOutsideTouchable) {
		this.isOutsideTouchable = isOutsideTouchable;
	}
	
	public void setOnClickListeners(View[] views, final OnClickListener listener){
		for(int i=0;i<views.length;i++){
			views[i].setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					cancelFlag = CANCEL_MANUAL;
					popDismiss();
					listener.onClick(v);
				}
			});
		}
	}
	
	public void setOnClickListeners(int[] resIds, final OnClickListener listener){
		for(int i=0;i<resIds.length;i++){
			findViewById(resIds[i]).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					cancelFlag = CANCEL_MANUAL;
					popDismiss();
					listener.onClick(v);
				}
			});
		}
	}

	public void show() {
		mPopup.showAtLocation(mDecordView, Gravity.CENTER
				| Gravity.CENTER_HORIZONTAL, 0, 0);
		
		mShadeView.startAnimation(createAlphaInAnimation());
		mPanel.startAnimation(createTranslationInAnimation());
	}

	/**
	 * 销毁自身视图
	 */
	public void popDismiss() {
		mPanel.startAnimation(createTranslationOutAnimation());
		mShadeView.startAnimation(createAlphaOutAnimation());

		mPanel.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				mPopup.dismiss();
			}
		}, ALPHA_DURATION);
		
	}

	public static interface PopupListener {

		// 消失监听
		public void onDismiss(PopupKit popup, boolean isCancel);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == SHADE_VIEW_ID && !isOutsideTouchable()) {
			return;
		}
		cancelFlag = CANCEL_BACKVIEW;
		popDismiss();
	}

	@Override
	public void onDismiss() {
		if (mListener != null){
			mListener.onDismiss(this, cancelFlag != CANCEL_MANUAL);
		}
	}
	
	class KitPopupWindow extends PopupWindow {
		
		public KitPopupWindow(Context context) {
			super(context);
		}

		@Override
		public void dismiss() {
			if(cancelFlag == CANCEL_BACKKEY){
				mPanel.startAnimation(createTranslationOutAnimation());
				mShadeView.startAnimation(createAlphaOutAnimation());

				mPanel.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						KitPopupWindow.super.dismiss();
					}
				}, ALPHA_DURATION);
			}else{
				super.dismiss();
			}
		}
		
	}

}
