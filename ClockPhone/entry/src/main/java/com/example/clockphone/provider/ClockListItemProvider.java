package com.example.clockphone.provider;

import com.example.clockphone.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.*;
import com.example.clockphone.bean.Clock;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class ClockListItemProvider extends BaseItemProvider {
    private AbilitySlice slice;
    private List<Clock> dataList = new LinkedList<>();
    private SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");

    public ClockListItemProvider(AbilitySlice abilitySlice) {
        this.slice = abilitySlice;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setDataList(List<Clock> dataList) {
        this.dataList = dataList;
    }

    @Override
    public Component getComponent(int position, Component component, ComponentContainer componentContainer) {
        Component component1;
        ViewHolder viewHolder;
        if (component == null) {
            component1 = LayoutScatter.getInstance(slice).parse(ResourceTable.Layout_item_clock, null, false);
            viewHolder = new ViewHolder();
            viewHolder.textDate = (Text) component1.findComponentById(ResourceTable.Id_item_text_date);
            viewHolder.textTime = (Text) component1.findComponentById(ResourceTable.Id_item_text_time);
            viewHolder.component_clock_state = component1.findComponentById(ResourceTable.Id_item_component_state);
            component1.setTag(viewHolder);
        } else {
            component1 = component;
            viewHolder = (ViewHolder) component.getTag();
        }
        Clock clock = dataList.get(position);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, clock.getYears());
        calendar.set(Calendar.MONTH, clock.getMonths() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, clock.getDays());
        calendar.set(Calendar.HOUR_OF_DAY, clock.getHours());
        calendar.set(Calendar.MINUTE, clock.getMinutes());
        viewHolder.textTime.setText(simpleDateFormat1.format(calendar.getTime()));
        viewHolder.textDate.setText(simpleDateFormat2.format(calendar.getTime()));
        calendar.clear();
        if (clock.getHappened() == 0) {
            viewHolder.component_clock_state.setBackground(null);
        }
        return component1;
    }

    class ViewHolder {
        Text textDate;
        Text textTime;
        Component component_clock_state;
    }
}
