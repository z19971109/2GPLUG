package smart2g.dyx.com.a2gplug;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class DeviceListAdapter extends BaseAdapter{

    private List<DeviceInstance> list;

    private Context context;

    public DeviceListAdapter(List<DeviceInstance> list , Context context){
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.device_list_item,
                null);

        ImageView device_icon = convertView.findViewById(R.id.device_icon);
        TextView device_name = convertView.findViewById(R.id.device_name);
        TextView device_mac = convertView.findViewById(R.id.device_mac);
        TextView daoqitime = convertView.findViewById(R.id.daoqitime);

        DeviceInstance instance = list.get(position);

//        if (instance.getDeviceType().equals(Constants.PLUG)){
        device_icon.setImageResource(R.drawable.icon_spmini);
//        }
        device_name.setText(instance.getName());
        device_mac.setText(instance.getMac());

//        daoqitime.setText(instance.getExpireTime());


        return convertView;
    }
}
