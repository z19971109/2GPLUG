package smart2g.dyx.com.a2gplug;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TimerListAdapter extends BaseAdapter {

    private Context context;

    private List<TimerInstance> lists;

    private int controlIndex;

    private String controlAction;

    private onClickMyButtonView onClickMyButtonView;

    private LayoutInflater mInflater;

    public TimerListAdapter(Context context, List<TimerInstance> lists) {
        this.context = context;
        this.lists = lists;
        mInflater = LayoutInflater.from(context);
    }

    public interface onClickMyButtonView {
        public void myTextViewClick(int id);
    }

    public void setOnClickMyTextView(onClickMyButtonView onClickMyButtonView) {
        this.onClickMyButtonView = onClickMyButtonView;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    class ChildViewHolder {
        TextView timer_flag;
        TextView on_time_state_view;
        TextView weeks;
        TextView on_time;
        TextView off_time;
        TextView action_textView;
        ImageView time_enable_button;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ChildViewHolder childViewHolder;
        if (convertView == null) {
            childViewHolder = new ChildViewHolder();
            convertView = mInflater.inflate(R.layout.sp_mini_delay_item2_layout, null);
            childViewHolder.timer_flag = (TextView) convertView.findViewById(R.id.timer_flag);
            childViewHolder.on_time_state_view = (TextView) convertView.findViewById(R.id.on_time_state_view);
            childViewHolder.weeks = (TextView) convertView.findViewById(R.id.weeks);
            childViewHolder.on_time = (TextView) convertView.findViewById(R.id.on_time);
            childViewHolder.off_time = (TextView) convertView.findViewById(R.id.off_time);
            childViewHolder.action_textView = convertView.findViewById(R.id.action_textView);
            childViewHolder.time_enable_button = (ImageView) convertView.findViewById(R.id.time_enable_button);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }


        final TimerInstance instance = lists.get(position);

//        System.out.println("RESPONSE_TIMER:"+instance.getStatus()+"|"+instance.getStatus().equals("ENABLE"));

        if (instance.getStatus().equals("ENABLE")) {
            childViewHolder.timer_flag.setBackgroundResource(R.drawable.timer_alerm);
            childViewHolder.on_time_state_view.setTextColor(context.getResources().getColor(R.color.sp_mini_time_blue));
            childViewHolder.time_enable_button.setBackgroundResource(R.drawable.switch_on);
        }

        if (instance.getStatus().equals("DISABLE")) {
            childViewHolder.timer_flag.setBackgroundResource(R.drawable.timer_alerm_gray);
            childViewHolder.on_time_state_view.setTextColor(context.getResources().getColor(R.color.sp_mini_bar_text_gray));
            childViewHolder.time_enable_button.setBackgroundResource(R.drawable.switch_off);
        }


        if (instance.getSwitchAction().equals("ON")) {
            childViewHolder.action_textView.setText("开");
        }
        if (instance.getSwitchAction().equals("OFF")) {
            childViewHolder.action_textView.setText("关");
        }

        childViewHolder.on_time.setText(instance.getSTime());
        childViewHolder.off_time.setText(instance.getETime());
        String result = Integer.toBinaryString(instance.getUserDaySet());

        String newResult = result;

        if (instance.getMode().equals("ONCE")) {
            childViewHolder.weeks.setText("执行一次");
        } else if (instance.getMode().equals("EVERYDAY")) {
            childViewHolder.weeks.setText("每天");
        } else if (instance.getMode().equals("CUSTOM")) {

            System.out.println("result:" + result.length());

            if (result.length() != 7) {

                for (int i = 0; i < 7 - result.length(); i++) {
                    newResult = "0" + newResult;

                    System.out.println("resultNew:" + newResult);

                }
            }


            int[] str = new int[newResult.length()];
            for (int i = 0; i < newResult.length(); i++) {
                str[i] = Integer.parseInt(newResult.substring(i, i + 1));
            }
            childViewHolder.weeks.setText(getweeks(str));
        }

        childViewHolder.time_enable_button.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                onClickMyButtonView.myTextViewClick(position);
            }
        });

        return convertView;
    }

    private String getweeks(int[] weeks) {

        String selectWeek = "";
        String[] weeksStrings = context.getResources().getStringArray(R.array.m1_week_array);
        for (int i = weeksStrings.length - 1; i >= 0; i--) {
            if (weeks[i] == 1) {
                selectWeek = selectWeek + "  " + weeksStrings[i];
//                System.out.println("select:"+selectWeek);
            }

        }

        if (selectWeek.equals("")) {
            return context.getString(R.string.run_one_time);
        } else {
            return selectWeek;
        }
    }

}
