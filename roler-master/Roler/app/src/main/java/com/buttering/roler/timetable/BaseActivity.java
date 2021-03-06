package com.buttering.roler.timetable;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.buttering.roler.R;
import com.buttering.roler.VO.Schedule;
import com.buttering.roler.depth.DepthBaseActivity;
import com.buttering.roler.dialog.ScheduleDialog;
import com.buttering.roler.net.basepresenter.BasePresenter;
import com.buttering.roler.util.MyApplication;
import com.vocketlist.android.roboguice.log.Ln;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class BaseActivity extends DepthBaseActivity implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener, ITimeView {


	@BindView(R.id.activity_base_fab) FloatingActionButton floatingActionButton;

	private static final int TYPE_DAY_VIEW = 1;
	private static final int TYPE_THREE_DAY_VIEW = 2;
	private static final int TYPE_WEEK_VIEW = 3;

	private SweetAlertDialog materialDialog;
	private List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
	private int mWeekViewType;
	private ITimePresenter presenter;
	private WeekView mWeekView;
	private String nowDate;
	private String addNowDate;
	private int startTimeOfDay;
	private int startMinOfDay;
	private int endTimeOfDay;
	private int endMinOfDay;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);

		ButterKnife.bind(this);

		presenter = new TimePresenter(this);

		setToolbar();
		setToday();

		// Get a reference for the week view in the layout.
		mWeekView = (WeekView) findViewById(R.id.weekView);

		// Set an action when any event is clicked.
		mWeekView.setOnEventClickListener(this);

		// The week view has infinite scrolling horizontally. We have to provide the events of a
		// month every time the month changes on the week view.
		mWeekView.setMonthChangeListener(this);

		// Set long press listener for events.
		mWeekView.setEventLongPressListener(this);

		setupDateTimeInterpreter(false);

		//load Schedule
		presenter.getSchduleList(nowDate);

		mWeekView.setOverlappingEventGap(5);

		// 사용자가 원하는 날짜에 일정을 추가할수 있도록 한다
		mWeekView.setScrollListener(new WeekView.ScrollListener() {
			@Override
			public void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay) {

				DateFormat dateType = new SimpleDateFormat(BaseActivity.this.getString(R.string.today_data_format));
				dateType.setTimeZone(TimeZone.getDefault());
				Ln.v(TimeZone.getDefault());
				addNowDate = dateType.format(newFirstVisibleDay.getTime());
			}
		});


	}

	@OnClick(R.id.activity_base_fab)
	public void addScheduleOnClick(){
		createAddScheduleDialog();
	}

	private void createAddScheduleDialog() {
		final View innerView = getLayoutInflater().inflate(R.layout.dialog_time_custom, null);
		AppCompatTextView startTime = (AppCompatTextView) innerView.findViewById(R.id.dialog_startTime_edt);
		AppCompatTextView endTime = (AppCompatTextView) innerView.findViewById(R.id.dialog_endTime_edt);
		AlertDialog.Builder alert = new AlertDialog.Builder(BaseActivity.this);
		alert.setTitle(getString(R.string.add_schedule_title));
		alert.setView(innerView);

		final Calendar c = Calendar.getInstance();
		int hourofDay = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		//set start time
		startTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						startTimeOfDay = hourOfDay;
						startMinOfDay = minute;
						startTime.setText(getString(R.string.set_time_title,hourOfDay,minute));
					}
				};

				ScheduleDialog dialog = new ScheduleDialog(BaseActivity.this, timeSetListener, hourofDay, minute, true);
				dialog.setTitle(getString(R.string.add_start_time_title));
				dialog.show();

			}
		});

		endTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						endTimeOfDay = hourOfDay;
						endMinOfDay = minute;
						endTime.setText(getString(R.string.set_time_title,hourOfDay,minute));
					}
				};

				ScheduleDialog dialog = new ScheduleDialog(BaseActivity.this, timeSetListener, hourofDay, minute, true);
				dialog.setTitle(getString(R.string.add_end_time_title));
				dialog.show();

			}
		});

		alert.setPositiveButton(getString(R.string.positive_title), (dialog, whichButton) -> {
			AppCompatEditText content = (AppCompatEditText) innerView.findViewById(R.id.dialog_time_content);
			String contents = content.getText().toString();
			if (!contents.isEmpty()) {
				//Todo send to SERVER

				Calendar startCalendar = Calendar.getInstance();

				int nowMonth = startCalendar.get(Calendar.MONTH);
				int nowYear = startCalendar.get(Calendar.YEAR);
				int nowDay = startCalendar.get(Calendar.DAY_OF_MONTH);

				Calendar calendar = Calendar.getInstance();
				DateFormat sdf = new SimpleDateFormat(getString(R.string.add_schedule_date_format));
				calendar.set(nowYear, nowMonth, nowDay, startTimeOfDay, startMinOfDay);
				String startDate = sdf.format(calendar.getTime());

				Calendar calendar2 = Calendar.getInstance();
				calendar2.set(nowYear, nowMonth, nowDay, endTimeOfDay, endMinOfDay);
				String endDate = sdf.format(calendar2.getTime());


				presenter.addSchdule(getString(R.string.schedule_contents,startDate ,endDate ,contents)
						, startDate, endDate, addNowDate);

			} else {
				Toast.makeText(this, getString(R.string.empty_schedule_content), Toast.LENGTH_SHORT).show();
			}
		});


		alert.setNegativeButton(getString(R.string.negative_title),
				(dialog, whichButton) -> {
					// Todo Canceled. NOTHING
				});
		AlertDialog dialog = alert.create();
		dialog.show();

	}



	private void setToday() {
		Calendar cal = Calendar.getInstance();
		DateFormat dateType = new SimpleDateFormat(getString(R.string.today_data_format));
		nowDate = dateType.format(cal.getTime());
		addNowDate = dateType.format(cal.getTime());
	}


	private void setToolbar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.custom_toolBar);
		TextView textView = (TextView) findViewById(R.id.toolbar_title);
		ImageView imageView = (ImageView) findViewById(R.id.toolBar_image);
		imageView.setImageResource(R.drawable.ic_keyboard_arrow_left_black_24dp);
		textView.setTextColor(Color.BLACK);
		textView.setText(getString(R.string.activity_base_toolbar_title));
		setSupportActionBar(toolbar);
		imageView.setOnClickListener(v -> {
			finish();
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		setupDateTimeInterpreter(id == R.id.action_week_view);
		switch (id) {
			case R.id.action_today: {
				mWeekView.goToToday();
			}
			return true;
			case R.id.action_day_view:
				if (mWeekViewType != TYPE_DAY_VIEW) {
					item.setChecked(!item.isChecked());
					mWeekViewType = TYPE_DAY_VIEW;
					mWeekView.setNumberOfVisibleDays(1);

					// Lets change some dimensions to best fit the view.
					mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
					mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
					mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
				}
				return true;
			case R.id.action_three_day_view:
				if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
					item.setChecked(!item.isChecked());
					mWeekViewType = TYPE_THREE_DAY_VIEW;
					mWeekView.setNumberOfVisibleDays(3);

					// Lets change some dimensions to best fit the view.
					mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
					mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
					mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
				}
				return true;
			case R.id.action_week_view:
				if (mWeekViewType != TYPE_WEEK_VIEW) {
					item.setChecked(!item.isChecked());
					mWeekViewType = TYPE_WEEK_VIEW;
					mWeekView.setNumberOfVisibleDays(7);

					// Lets change some dimensions to best fit the view.
					mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
					mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
					mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
				}
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Set up a nowDate time interpreter which will show short nowDate values when in week view and long
	 * nowDate values otherwise.
	 *
	 * @param shortDate True if the nowDate values should be short.
	 */
	private void setupDateTimeInterpreter(final boolean shortDate) {
		mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
			@Override
			public String interpretDate(Calendar date) {
				SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
				String weekday = weekdayNameFormat.format(date.getTime());
				SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

				// All android api level do not have a standard way of getting the first letter of
				// the week day name. Hence we get the first char programmatically.
				// Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
				if (shortDate)
					weekday = String.valueOf(weekday.charAt(0));
				return "              " + weekday.toUpperCase() + " " + format.format(date.getTime()) + "              ";
			}

			@Override
			public String interpretTime(int hour) {
				return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
			}
		});
	}

	protected String getEventTitle(Calendar time) {
		return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
	}

	@Override
	public void onEventClick(WeekViewEvent event, RectF eventRect) {
		Toast.makeText(this, "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
		createScheduleDeleteDialog(event);

	}

	private void createScheduleDeleteDialog(WeekViewEvent event) {

		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setMessage("일정을 삭제 하시겠습니까?").setCancelable(false)
				.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						presenter.deleteSchdule((int) event.getId());
					}
				})
				.setNegativeButton("취소", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				});

		AlertDialog alertDialog = alert.create();
		alertDialog.show();

	}

	@Override
	public void onEmptyViewLongPress(Calendar time) {
		Toast.makeText(this, "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show();
	}

	public WeekView getWeekView() {
		return mWeekView;
	}

	@Override
	public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {

		// Return only the events that matches newYear and newMonth.
		List<WeekViewEvent> matchedEvents = new ArrayList<WeekViewEvent>();
		for (WeekViewEvent event : events) {
			if (eventMatches(event, newYear, newMonth)) {
				matchedEvents.add(event);
			}
		}

		return matchedEvents;
	}

	private boolean eventMatches(WeekViewEvent event, int year, int month) {
		return (event.getStartTime().get(Calendar.YEAR) == year && event.getStartTime().get(Calendar.MONTH) == month) || (event.getEndTime().get(Calendar.YEAR) == year && event.getEndTime().get(Calendar.MONTH) == month);
	}

	@Override
	public void updateSchedule() {
		presenter.getSchduleList(nowDate);
	}

	@Override
	public void setScheduleList(List<Schedule> schedules) {
		this.events.clear();
//		for (Schedule event : schedules) {
//			this.events.add(event.toWeekViewEvent(event, nowDate));
//		}

		for(int i = 0; i<schedules.size(); i++) {
			Schedule event = new Schedule();
			this.events.add(event.toWeekViewEvent(schedules.get(i), schedules.get(i).getDate()));
		}

		getWeekView().notifyDatasetChanged();
	}

	@Override
	public void showLoadingBar() {
		materialDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
		materialDialog.getProgressHelper().setBarColor(this.getResources().getColor(R.color.dialog_color));
		materialDialog.setTitleText(getString(R.string.loading_dialog_title));
		materialDialog.setCancelable(false);
		materialDialog.show();
	}

	@Override
	public void hideLoadingBar() {
		if (materialDialog != null)
			materialDialog.dismiss();
	}

	@Override
	public void updateWeekView() {
		mWeekView.notifyDatasetChanged();
	}

	public static Calendar toCalendar(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

}