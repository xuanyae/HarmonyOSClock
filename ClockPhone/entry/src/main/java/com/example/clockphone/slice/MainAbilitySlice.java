package com.example.clockphone.slice;

import com.example.clockphone.ResourceTable;
import com.example.clockphone.bean.Clock;
import com.example.clockphone.net.Net;
import com.example.clockphone.net.NetIf;
import com.example.clockphone.provider.ClockListItemProvider;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.agp.components.ListContainer;
import org.devio.hi.json.HiJson;

import java.util.ArrayList;
import java.util.List;

public class MainAbilitySlice extends AbilitySlice {
    private ListContainer listClockContainer;
    private ClockListItemProvider clockListItemProvider;
    List<Clock> clockList;
    private Net net = new Net();
    private Image imageAddClock;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        initListContainer();
        imageAddClock = (Image) this.findComponentById(ResourceTable.Id_image_add_clock);
        imageAddClock.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                present(new AddClockAbilitySlice(), new Intent());
            }
        });
    }

    private void initListContainer() {
        listClockContainer = (ListContainer) this.findComponentById(ResourceTable.Id_list_clock_view);
        clockListItemProvider = new ClockListItemProvider(this);
        net.getAllClock(new NetIf.NetListener() {
            @Override
            public void onSuccess(HiJson res) {
                bindData(res);
            }

            @Override
            public void onFail(String message) {

            }
        });
        listClockContainer.setItemLongClickedListener(new ListContainer.ItemLongClickedListener() {
            @Override
            public boolean onItemLongClicked(ListContainer listContainer, Component component, int i, long l) {
                present(new DeleteClockAbilitySlice(), new Intent());
                return true;
            }
        });
    }

    private void bindData(HiJson res) {
        HiJson hiJson;
        int count = res.count();
        this.clockList = new ArrayList<>();
        int i = 0;
        while (i < count) {
            hiJson = res.get(i);
            Clock clock = new Clock();
            clock.setClockid(hiJson.value("clockid"));
            clock.setYears(hiJson.value("years"));
            clock.setMonths(hiJson.value("months"));
            clock.setDays(hiJson.value("days"));
            clock.setHours(hiJson.value("hours"));
            clock.setMinutes(hiJson.value("minutes"));
            clock.setSends(hiJson.value("sends"));
            clock.setHappened(hiJson.value("happened"));
            this.clockList.add(clock);
            i++;
        }
        clockListItemProvider.setDataList(this.clockList);
        listClockContainer.setItemProvider(clockListItemProvider);
        listClockContainer.setReboundEffect(true);//回弹效果
    }

    private void refreshClocks() {
        net.getAllClock(new NetIf.NetListener() {
            @Override
            public void onSuccess(HiJson res) {
                bindData(res);
            }

            @Override
            public void onFail(String message) {
                System.out.println(message);
            }
        });
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
        refreshClocks();
    }
}
