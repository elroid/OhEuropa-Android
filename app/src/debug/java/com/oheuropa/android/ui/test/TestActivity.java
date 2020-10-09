package com.oheuropa.android.ui.test;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.oheuropa.android.ui.base.BaseActivity;


/**
 * Class: com.oheuropa.android.ui.test.TestActivity
 * Project: OhEuropa-Android
 * Created Date: 07/03/2018 16:48
 *
 * @author <a href="mailto:e@elroid.com">Elliot Long</a>
 *         Copyright (c) 2018 Elroid Ltd. All rights reserved.
 */
public class TestActivity extends BaseActivity
{
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		doSomething();
	}

	private void doSomething(){

	}

}
