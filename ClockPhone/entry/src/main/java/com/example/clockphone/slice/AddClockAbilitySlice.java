package com.example.clockphone.slice;

import com.example.clockphone.ResourceTable;
import com.example.clockphone.net.Net;
import com.example.clockphone.net.NetIf;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import org.devio.hi.json.HiJson;

import java.util.HashMap;
import java.util.Map;

public class AddClockAbilitySlice extends AbilitySlice {
    private Net net = new Net();
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Integer years, months, days, hours, minutes;
    private Integer clockid;
    private Image image_ok, image_cancel;

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_add_clock);
        initLayout();
        setListener();
    }

    private void setListener() {
        image_ok.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                setClock();
            }
        });
        image_cancel.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                terminate();
            }
        });
    }

    private void initLayout() {
        datePicker = (DatePicker) findComponentById(ResourceTable.Id_date_pick);
        timePicker = (TimePicker) findComponentById(ResourceTable.Id_time_picker);
        timePicker.showSecond(false);
        timePicker.enableSecond(false);
        image_ok = (Image) findComponentById(ResourceTable.Id_image_ok);
        image_cancel = (Image) findComponentById(ResourceTable.Id_image_cancel);
    }

    private Integer getDateAndTimeThentoClockId() {
        years = datePicker.getYear();
        months = datePicker.getMonth();
        days = datePicker.getDayOfMonth();
        hours = timePicker.getHour();
        minutes = timePicker.getMinute();
        String year = null, month = null, day = null, hour = null, minute = null;
        year = "" + (years - 2000);
        if (months < 10) {
            month = "0" + months;
        } else {
            month = "" + minutes;
        }
        if (days < 10) {
            day = "0" + days;
        } else {
            day = "" + days;
        }
        if (hours < 10) {
            hour = "0" + hours;
        } else {
            hour = "" + hours;
        }
        if (minutes < 10) {
            minute = "0" + minutes;
        } else {
            minute = "" + minutes;
        }
        String idString = year + month + day + hour + minute;
        clockid = Integer.parseInt(idString);
        return clockid;
    }

    private void setClock() {
        Map<String, Integer> params = new HashMap<>();
        params.put("clockid", getDateAndTimeThentoClockId());
        params.put("years", years);
        params.put("months", months);
        params.put("days", days);
        params.put("hours", hours);
        params.put("minutes", minutes);
        net.setClock(params, new NetIf.NetListener() {
            @Override
            public void onSuccess(HiJson res) {

            }

            @Override
            public void onFail(String message) {
                System.out.println(message);
            }
        });
        terminate();
    }
}
