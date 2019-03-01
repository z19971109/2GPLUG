package smart2g.dyx.com.a2gplug;

/**
 * Created by Administrator on 2018/8/21.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class TimerListView extends ListView {
    public TimerListView(Context context) {
        super(context);
    }

    public TimerListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimerListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
