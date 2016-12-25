package com.lance.popupkit;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * PopupWindow的装饰封装,简化繁琐操作
 * @author ganchengkai
 *
 */
public class MainActivity extends ActionBarActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	}
	
	// 不需要锚点,不需要建立冗余变量
	public void popupView(View view){
		View menuView = LayoutInflater.from(this).inflate(R.layout.view_test, null);
		
		new PopupKit.Builder(menuView)
			.setOutsideTouchable(true)
			.setOnClickListener(new int[]{R.id.btn_0, R.id.btn_1}, this)
			.show();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_0:
			System.out.println("btn0");
			break;
		case R.id.btn_1:
			System.out.println("btn1");
			break;
		}
	}

}
