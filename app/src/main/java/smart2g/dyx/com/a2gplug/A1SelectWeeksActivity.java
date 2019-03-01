package smart2g.dyx.com.a2gplug;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.math.BigInteger;

public class A1SelectWeeksActivity extends Activity {

    private RelativeLayout mEveryDayLayout;
    private ImageView mEveryDaySelect;
    private ListView mWeekListView;
    private int[] mWeeks;
    private String[] mWeeksName;

    private WeekAdapter mWeekAdapter;

    private boolean isEveryDay = false;

    private Button confirm_weeks_bt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a1_select_weeks_layout);

        if (mWeeks == null) {
            mWeeks = new int[7];
        }

//		if (RmtApplaction.mControlDevice != null && RmtApplaction.mControlDevice.getDeviceType() == DeviceType.M1) {
        mWeeksName = getResources().getStringArray(R.array.week_array);
//		} else {
//			mWeeksName = getResources().getStringArray(R.array.week_array);
//		}

        findView();

        setListener();

        mWeekAdapter = new WeekAdapter();
        mWeekListView.setAdapter(mWeekAdapter);

        initView();
    }

    private void findView() {
        mEveryDayLayout = (RelativeLayout) findViewById(R.id.every_day_layout);
        mEveryDaySelect = (ImageView) findViewById(R.id.every_day_select);
        mWeekListView = (ListView) findViewById(R.id.week_lsit);
        confirm_weeks_bt = (Button) findViewById(R.id.confirm_weeks_bt);
    }

    private void initView() {
        if (checkAllSelect()) {
            isEveryDay = true;
            mEveryDaySelect.setVisibility(View.VISIBLE);
        } else {
            isEveryDay = false;
            mEveryDaySelect.setVisibility(View.GONE);
        }
    }

    private void setListener() {
        mEveryDayLayout.setOnClickListener(new OnSingleClickListener() {

            @Override
            public void doOnClick(View v) {
                if (isEveryDay) {
                    isEveryDay = false;
                    for (int i = 0; i < mWeeks.length; i++) {
                        mWeeks[i] = 0;
                    }
                    mWeekAdapter.notifyDataSetChanged();
                    mEveryDaySelect.setVisibility(View.GONE);
                } else {
                    isEveryDay = true;
                    for (int i = 0; i < mWeeks.length; i++) {
                        mWeeks[i] = 1;
                    }
                    mWeekAdapter.notifyDataSetChanged();
                    mEveryDaySelect.setVisibility(View.VISIBLE);
                }
            }
        });

        mWeekListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mWeeks[position] == 1) {
                    mWeeks[position] = 0;
                } else {
                    mWeeks[position] = 1;
                }
                mWeekAdapter.notifyDataSetChanged();

                initView();
            }
        });

        confirm_weeks_bt.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void doOnClick(View v) {
                String binary = "";
                for (int i = mWeeks.length - 1; i >= 0; i--) {
                    binary = mWeeks[i] + binary;
                }

                System.out.println("binary:" + array(binary));

                Intent intent = new Intent();
                intent.putExtra("TimerWeeksInt", binaryToDecimal(array(binary)));
                intent.putExtra("TimerWeeks", mWeeks);
                setResult(RESULT_OK, intent);

                finish();

            }
        });
    }

    public static String array(String s){
        int length=s.length();
        char[] array=s.toCharArray();
        for(int i=0;i<length/2;i++){
            array[i]=s.charAt(length-1-i);
            array[length-1-i]=s.charAt(i);
        }
        return new String(array);
    }

    public static int binaryToDecimal(String binarySource) {
        BigInteger bi = new BigInteger(binarySource, 2);    //转换为BigInteger类型
        return Integer.parseInt(bi.toString());        //转换成十进制
    }

    private boolean checkAllSelect() {
        for (int i = 0; i < mWeeks.length; i++) {
            if (mWeeks[i] == 0)
                return false;
        }

        return true;
    }

    class WeekAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mWeeksName.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.weeks_item_layout, null);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.week);
                viewHolder.selected = (ImageView) convertView.findViewById(R.id.select);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.textView.setText(mWeeksName[position]);
            if (mWeeks[position] == 1) {
                viewHolder.selected.setVisibility(View.VISIBLE);
            } else {
                viewHolder.selected.setVisibility(View.GONE);
            }

            return convertView;
        }

        class ViewHolder {
            TextView textView;
            ImageView selected;
        }
    }

}
