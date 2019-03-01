package smart2g.dyx.com.a2gplug;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PLUGAlert {

    public static final int YES = 0;

    public static final int NO = 1;

    public interface OnAlertSelectId {
        void onClick(int whichButton);
    }

    public static Dialog showAlert(Context context, String title, String messageId, String confimButtonText,
                                   String cancleButtonText, final OnAlertSelectId alertDo) {
        final Dialog dlg = new Dialog(context, R.style.BLTheme_Dialog);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.bl_alert_layout, null);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);

        TextView titleView = (TextView) layout.findViewById(R.id.dialog_title);
        TextView messTextView = (TextView) layout.findViewById(R.id.dialog_msg);
        messTextView.setGravity(Gravity.CENTER|Gravity.TOP);
        Button confimButton = (Button) layout.findViewById(R.id.dialog_yes);
        Button cacelButton = (Button) layout.findViewById(R.id.dialog_no);

        if(confimButtonText != null){
            confimButton.setText(confimButtonText);
        }

        if (cancleButtonText != null) {
            cacelButton.setText(cancleButtonText);
        }

        if(!TextUtils.isEmpty(title)){
            titleView.setText(title);
        }

        if (messageId != null) {
            messTextView.setText(messageId);
        }

        confimButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDo.onClick(YES);
                dlg.dismiss();
            }
        });

        cacelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDo.onClick(NO);
                dlg.dismiss();
            }
        });

        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        lp.gravity = Gravity.CENTER;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setCancelable(false);
        dlg.setContentView(layout);
        dlg.show();
        return dlg;
    }
}
