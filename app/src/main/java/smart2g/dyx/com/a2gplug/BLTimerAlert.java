package smart2g.dyx.com.a2gplug;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BLTimerAlert {

    public interface OnSecondAlertClick {
        void onClick(int hour, int min, int second);
    }

    public static Dialog showSecondAlert(final Context context, int hour, int min, int second, final OnSecondAlertClick alertDo) {
        final Dialog dlg = new Dialog(context, R.style.BLTheme_Dialog);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.bl_hour_time_alert_layout, null);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);

        final WheelView hourView = (WheelView) layout.findViewById(R.id.hour_view);
        final WheelView minView = (WheelView) layout.findViewById(R.id.min_view);
        final WheelView secondView = (WheelView) layout.findViewById(R.id.second_view);
        TextView confimButton = (TextView) layout.findViewById(R.id.btn_yes);
        TextView cacelButton = (TextView) layout.findViewById(R.id.btn_close);
        secondView.setVisibility(View.VISIBLE);
        hourView.setVisibleItems(5);
        minView.setVisibleItems(5);
        secondView.setVisibleItems(5);

        hourView.setAdapter(new NumericWheelAdapter(0, 23, "%02d"));
        minView.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        secondView.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        hourView.setLabel(context.getString(R.string.alert_hour));
        minView.setLabel(context.getString(R.string.alert_min));
        secondView.setLabel(context.getString(R.string.alert_second));
        hourView.setCyclic(true);
        minView.setCyclic(true);
        secondView.setCyclic(true);
        hourView.setCurrentItem(hour);
        minView.setCurrentItem(min);
        secondView.setCurrentItem(second);

        confimButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDo.onClick(hourView.getCurrentItem(), minView.getCurrentItem(), secondView.getCurrentItem());
                dlg.dismiss();
            }
        });

        cacelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });

        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        lp.gravity = Gravity.BOTTOM;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(layout);
        dlg.show();
        return dlg;
    }

}
