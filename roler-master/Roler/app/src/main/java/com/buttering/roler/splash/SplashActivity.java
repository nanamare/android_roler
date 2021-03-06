package com.buttering.roler.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Toast;

import com.buttering.roler.R;
import com.buttering.roler.login.LogInActivity;
import com.buttering.roler.plan.PlanActivity;
import com.buttering.roler.util.NetUtil;
import com.buttering.roler.util.SharePrefUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nanamare on 2016-07-30.
 */
public class SplashActivity extends AppCompatActivity {

	@BindView(R.id.title) protected AppCompatTextView splash_title;
	@BindView(R.id.subTitle) protected AppCompatTextView splash_subTitle;

	private static final String TAG = SplashActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		ButterKnife.bind(this);

		splash_title.setVisibility(View.INVISIBLE);
		splash_subTitle.setVisibility(View.INVISIBLE);

		/**
		 * check the network state
		 */
		if (NetUtil.isNetworkAvailable(this)) {

			new Handler().postDelayed(() -> splash_title.setVisibility(View.VISIBLE), 1000);

			new Handler().postDelayed(() -> splash_subTitle.setVisibility(View.VISIBLE), 1500);


			Intent intent = new Intent(this, LogInActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			boolean isLoggedIn = SharePrefUtil.getBooleanSharedPreference("isLoggedIn");
			if (isLoggedIn) {

				new Handler().postDelayed(() -> {
					Intent loggedInIntent = new Intent(this, PlanActivity.class);
					loggedInIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(loggedInIntent);
					finish();
				}, 1500);


			} else {

				new Handler().postDelayed(() -> {
					startActivity(intent);
					finish();
				}, 1500);
			}

		} else {
			Toast.makeText(getApplicationContext(), getString(R.string.confirm_internet_connection), Toast.LENGTH_LONG).show();
			super.onPause();
		}


	}


}
