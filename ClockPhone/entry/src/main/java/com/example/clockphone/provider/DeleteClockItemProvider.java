package com.example.clockphone.provider;

import com.example.clockphone.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.*;
import com.example.clockphone.bean.Clock;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DeleteClockItemProvider extends BaseItemProvider {
    private AbilitySlice slice;
    private List<Clock> dataList = new ArrayList<>();
    private SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
    private HiLogLabel logLabel = new HiLogLabel(HiLog.LOG_APP, 0x010, "MY_TAG");

    public DeleteClockItemProvider(AbilitySlice abilitySlice) {
        this.slice = abilitySlice;
    }

    @Override
    public int getCount() {
        HiLog.info(logLabel,"1    count: "+dataList.size());
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

    public List<Clock> getDataList() {
        return dataList;
    }

    public void setDataList(List<Clock> dataList) {
        this.dataList = dataList;
    }

    @Override
    public Component getComponent(int i, Component component, ComponentContainer componentContainer) {
        final Component component1;
        ViewHolder viewHolder;
        Clock clock = dataList.get(i);
        if (component == null) {
            component1 = LayoutScatter.getInstance(slice).parse(ResourceTable.Layout_item_delete_clock, null, false);
            viewHolder = new ViewHolder();
            viewHolder.textDate = (Text) component1.findComponentById(ResourceTable.Id_item_text_date);
            viewHolder.textTime = (Text) component1.findComponentById(ResourceTable.Id_item_text_time);
            viewHolder.checkboxDelete = (Checkbox) component1.findComponentById(ResourceTable.Id_item_checkbox);
            viewHolder.checkboxDelete.setCheckedStateChangedListener(new AbsButton.CheckedStateChangedListener() {
                @Override
                public void onCheckedChanged(AbsButton absButton, boolean isCheck) {
                    if (isCheck) {
                        clock.setSends(2);
                    }
                }
            });
            component1.setTag(viewHolder);
        } else {
            component1 = component;
            viewHolder = (ViewHolder) component.getTag();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, clock.getYears());
        calendar.set(Calendar.MONTH, clock.getMonths() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, clock.getDays());
        calendar.set(Calendar.HOUR_OF_DAY, clock.getHours());
        calendar.set(Calendar.MINUTE, clock.getMinutes());
        viewHolder.textTime.setText(simpleDateFormat1.format(calendar.getTime()));
        viewHolder.textDate.setText(simpleDateFormat2.format(calendar.getTime()));
        HiLog.info(logLabel,"2");
        calendar.clear();
        if (clock.getSends() == 2) {
            viewHolder.checkboxDelete.setChecked(true);
        }
        return component1;
    }

    class ViewHolder {
        Text textDate;
        Text textTime;
        Checkbox checkboxDelete;
    }
}
