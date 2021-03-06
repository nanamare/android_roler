package com.buttering.roler.plan;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buttering.roler.R;
import com.buttering.roler.VO.Role;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * Created by nanamare on 16. 7. 31..
 */
public class PlanActivityAdapter extends BaseAdapter {

	private Context context;
	private List<Role> roles;

	public PlanActivityAdapter(Context context, List<Role> roles) {
		this.context = context;
		this.roles = roles;
	}

	public PlanActivityAdapter(Context context, List<Role> roles, int progress) {
		this.context = context;
		this.roles = roles;
	}


	@Override
	public int getCount() {
		return roles.size();
	}

	@Override
	public Object getItem(int position) {
		return roles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder viewHolder;

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.activity_plan_item, null);

			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tv_rolePrimaryPlan.setText(String.valueOf(roles.get(position).getRolePrimary()));
		viewHolder.tv_roleContentPlan.setText(roles.get(position).getRoleContent());
		viewHolder.tv_roleNamePlan.setText(roles.get(position).getRoleName());
		viewHolder.waveLoadingView.setProgressValue(roles.get(position).getProgress());
		viewHolder.waveLoadingView.setCenterTitle(roles.get(position).getProgress() + "%");
		viewHolder.waveLoadingView.setBorderColor(context.getResources().getColor(bgColor[roles.get(position).getRolePrimary()]));
		viewHolder.waveLoadingView.setCenterTitleColor(context.getResources().getColor(R.color.wallet_holo_blue_light));
		viewHolder.waveLoadingView.setWaveColor(context.getResources().getColor(bgColor[roles.get(position).getRolePrimary()]));
		viewHolder.top_ll.setBackground(ContextCompat.getDrawable(context, R.drawable.plan_item_shape_round));
		viewHolder.rl_roleItemTop.setBackground(ContextCompat.getDrawable(context, R.drawable.plan_item_top_round));
		((GradientDrawable) viewHolder.rl_roleItemTop.getBackground()).setColor(context.getResources().getColor(bgColor[roles.get(position).getRolePrimary()]));

		return convertView;
	}


	public void setRoleList(List<Role> roles) {
		this.roles.clear();
		this.roles = roles;
	}

	final int[] bgColor = {R.color.holo_green_dark, R.color.role_color_1, R.color.role_color_2, R.color.role_color_3,
			R.color.role_color_4, R.color.role_color_5};


	public static class ViewHolder {
		@BindView(R.id.ll_roleItemTop)
		LinearLayout rl_roleItemTop;
		@BindView(R.id.tv_rolePrimaryPlan)
		TextView tv_rolePrimaryPlan;
		@BindView(R.id.tv_roleNamePlan)
		TextView tv_roleNamePlan;
		@BindView(R.id.tv_roleContentPlan)
		TextView tv_roleContentPlan;
		@BindView(R.id.waveLoadingView)
		WaveLoadingView waveLoadingView;
		@BindView(R.id.activity_plan_item_top_ll)
		LinearLayout top_ll;

		public ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}

	}


}
