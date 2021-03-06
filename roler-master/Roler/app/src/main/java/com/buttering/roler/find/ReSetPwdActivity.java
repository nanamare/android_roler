package com.buttering.roler.find;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.buttering.roler.R;
import com.buttering.roler.depth.DepthBaseActivity;
import com.buttering.roler.login.LogInActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReSetPwdActivity extends DepthBaseActivity implements IReSetUpPwdView {

	@BindView(R.id.activity_find_pwd_input_edt)
	EditText input_edt;
	@BindView(R.id.activity_find_pwd_re_input_edt)
	EditText reInput_edt;
	@BindView(R.id.activity_find_pwd_change_btn)
	Button change_btn;

	private ReSetPwdPresenter presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_pwd);
		ButterKnife.bind(this);
		presenter = new ReSetPwdPresenter(this);
		setToolbar();
		setUpPwd();
	}

	private void setUpPwd() {
		change_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = getIntent();
				String userEmail = intent.getStringExtra("userEmail");

				String input = input_edt.getText().toString();
				String reInput = reInput_edt.getText().toString();
				if (isValid(input, reInput)) {
					//presenter 자리
					presenter.changePwd(input, userEmail);
				} else {
					Toast.makeText(ReSetPwdActivity.this, "잘못된 값", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private boolean isValid(String inputPwd, String reInputPwd) {
		if (!inputPwd.equals(reInputPwd)) {
			Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (!isPwdValid(inputPwd)) {
			Toast.makeText(getApplicationContext(), getString(R.string.join_invalid_pwd),
					Toast.LENGTH_SHORT).show();
			return false;
		}

		if (inputPwd == null || inputPwd.isEmpty()) {
			Toast.makeText(getApplicationContext(), getString(R.string.join_empty_pwd),
					Toast.LENGTH_SHORT).show();
			return false;
		}

		if (inputPwd.length() < 8) {
			Toast.makeText(getApplicationContext(), getString(R.string.join_too_short_pwd),
					Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	private boolean isPwdValid(String pwd) {
		boolean isPwd = false;

		String expression = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,}$";

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(pwd);
		if (matcher.matches()) {
			isPwd = true;
		}
		return isPwd;
	}

	private void setToolbar() {

		Toolbar toolbar = (Toolbar) findViewById(R.id.custom_toolBar);
		TextView textView = (TextView) toolbar.findViewById(R.id.toolbar_title);
		ImageView imageView = (ImageView) findViewById(R.id.toolBar_image);
		imageView.setImageResource(R.drawable.ic_keyboard_arrow_left_black_24dp);
		imageView.setOnClickListener(view -> {
			finish();
		});
		textView.setTextColor(Color.BLACK);
		textView.setText("Find Password");
		setSupportActionBar(toolbar);

	}

	@Override
	public void moveToLoginActivity() {
		Toast.makeText(this, "비밀번호 변경완료 " + "\n" + "다시 로그인 해주세요", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(this, LogInActivity.class);
		startActivity(intent);
		finish();
	}
}
