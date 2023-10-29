package com.example.clockwearable;

import com.example.clockwearable.bean.Clock;
import com.example.clockwearable.net.Net;
import com.example.clockwearable.net.NetIf;
import com.example.clockwearable.slice.ClockAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.rdb.ValuesBucket;
import ohos.data.resultset.ResultSet;
import ohos.rpc.IRemoteObject;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.net.Uri;
import org.devio.hi.json.HiJson;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClockServiceAbility extends Ability {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "Demo");
    private Net net = new Net();
    private static final String DB_COLUMN_CLOCK_ID = "clockid";
    private static final String DB_COLUMN_YEAR = "years";
    private static final String DB_COLUMN_MONTH = "months";
    private static final String DB_COLUMN_DAY = "days";
    private static final String DB_COLUMN_HOUR = "hours";
    private static final String DB_COLUMN_MINUTE = "minutes";
    private static final String DB_COLUMN_SEND = "sends";
    private static final String DB_COLUMN_HAPPENED = "happened";
    private DataAbilityHelper databaseHelper;
    private static final String BASE_URI = "dataability:///com.example.clockwearable.ClockDataAbility";
    private static final String DATA_PATH = "/myclock";
    private boolean isInAlarm = false;
    private int clockid;

    @Override
    public void onStart(Intent intent) {
        HiLog.error(LABEL_LOG, "ClockServiceAbility::onStart");
        super.onStart(intent);
        databaseHelper = DataAbilityHelper.creator(this);
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                getClock();//从后台获取闹钟
                doClock();//按时拉起闹钟
            }
        }, 10, TimeUnit.SECONDS);
    }

    //判断是否有闹钟需要拉起
    private void doClock() {
        if (isInAlarm) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        List<Clock> clockList = getLocalAllClocks();
        for (Clock clock : clockList) {
            if (clock.getHappened() == 0 && clock.getDays() == day && clock.getHours() == hour && clock.getMinutes() == minute) {
                clockid = clock.getClockid();
                startClockAlarmAbility(clockid);
            }
        }
    }

    //启动闹钟
    private void startClockAlarmAbility(int clockid) {
        isInAlarm = true;
        Intent intent = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withBundleName(getBundleName())
                .withAbilityName(ClockAbilitySlice.class.getName())
                .build();
        intent.setOperation(operation);
        intent.setParam("clockid", clockid);
        startAbility(intent);
    }

    //获取本地闹钟数据
    private List<Clock> getLocalAllClocks() {
        List<Clock> clockList = new LinkedList<>();
        String[] columns = new String[]{ClockDataAbility.DB_COLUMN_CLOCK_ID, ClockDataAbility.DB_COLUMN_YEAR,
                ClockDataAbility.DB_COLUMN_MONTH, ClockDataAbility.DB_COLUMN_DAY, ClockDataAbility.DB_COLUMN_HOUR,
                ClockDataAbility.DB_COLUMN_MINUTE, ClockDataAbility.DB_COLUMN_HAPPENED};
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        try {
            ResultSet resultSet = databaseHelper.query(Uri.parse(BASE_URI + DATA_PATH), columns, predicates);
            if (resultSet == null || resultSet.getRowCount() == 0) {
                return clockList;
            }
            resultSet.goToFirstRow();
            do {
                Clock clock = new Clock();
                clock.setClockid(resultSet.getInt(resultSet.getColumnIndexForName(ClockDataAbility.DB_COLUMN_CLOCK_ID)));
                clock.setYears(resultSet.getInt(resultSet.getColumnIndexForName(ClockDataAbility.DB_COLUMN_YEAR)));
                clock.setMonths(resultSet.getInt(resultSet.getColumnIndexForName(ClockDataAbility.DB_COLUMN_MONTH)));
                clock.setDays(resultSet.getInt(resultSet.getColumnIndexForName(ClockDataAbility.DB_COLUMN_DAY)));
                clock.setHours(resultSet.getInt(resultSet.getColumnIndexForName(ClockDataAbility.DB_COLUMN_HOUR)));//?
                clock.setMinutes(resultSet.getInt(resultSet.getColumnIndexForName(ClockDataAbility.DB_COLUMN_MINUTE)));
                clock.setHappened(resultSet.getInt(resultSet.getColumnIndexForName(ClockDataAbility.DB_COLUMN_HAPPENED)));
                clockList.add(clock);
            } while (resultSet.goToNextRow());
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            exception.printStackTrace();
        }
        return clockList;
    }

    //从后台服务器获取闹钟数据
    private void getClock() {
        net.getAllClock(new NetIf.NetListener() {
            @Override
            public void onSuccess(HiJson res) {
                checkClockAndInsert(res);//保存未保存到本地的闹钟到本地
            }

            @Override
            public void onFail(String message) {

            }
        });
    }

    //检查查询到的数据，判断其是否已被保存到本地或已响过
    private void checkClockAndInsert(HiJson res) {
        HiJson hiJson;
        int count = res.count();
        int i = 0;
        int happened, sends;
        while (i < count) {
            hiJson = res.get(i);
            happened = hiJson.value(DB_COLUMN_HAPPENED);
            sends = hiJson.value(DB_COLUMN_SEND);
            if (happened == 0 && sends == 0) {
                inserts(hiJson.value(DB_COLUMN_CLOCK_ID), hiJson.value(DB_COLUMN_YEAR), hiJson.value(DB_COLUMN_MONTH), hiJson.value(DB_COLUMN_DAY), hiJson.value(DB_COLUMN_HOUR), hiJson.value(DB_COLUMN_MINUTE), hiJson.value(DB_COLUMN_HAPPENED));
                Map<String, Integer> params = new HashMap<>();
                params.put("clockid", hiJson.value(DB_COLUMN_CLOCK_ID));
                net.setSends(params, new NetIf.NetListener() {//修改已保存的闹钟的sends值为1
                    @Override
                    public void onSuccess(HiJson res) {

                    }

                    @Override
                    public void onFail(String message) {

                    }
                });
            }
            i++;
        }
    }

    //插入数据到本地
    private void inserts(int clockid, int years, int months, int days, int hours, int minutes, int happened) {
        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putInteger(DB_COLUMN_CLOCK_ID, clockid);
        valuesBucket.putInteger(DB_COLUMN_YEAR, years);
        valuesBucket.putInteger(DB_COLUMN_MONTH, months);
        valuesBucket.putInteger(DB_COLUMN_DAY, days);
        valuesBucket.putInteger(DB_COLUMN_HOUR, hours);
        valuesBucket.putInteger(DB_COLUMN_MINUTE, minutes);
        valuesBucket.putInteger(DB_COLUMN_HAPPENED, happened);
        try {
            if (databaseHelper.insert(Uri.parse(BASE_URI + DATA_PATH), valuesBucket) != -1) {
                HiLog.debug(LABEL_LOG, "insert successful");
            } else {
                HiLog.debug(LABEL_LOG, "insert successful?????");
            }
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            exception.printStackTrace();
            HiLog.debug(LABEL_LOG, "insert: dataRemote exception|illegalStateException");
            HiLog.debug(LABEL_LOG, exception.toString());
        }
    }

    @Override
    public void onBackground() {
        super.onBackground();
        HiLog.info(LABEL_LOG, "ClockServiceAbility::onBackground");
    }

    @Override
    public void onStop() {
        super.onStop();
        HiLog.info(LABEL_LOG, "ClockServiceAbility::onStop");
    }

    @Override
    public void onCommand(Intent intent, boolean restart, int startId) {
    }

    @Override
    public IRemoteObject onConnect(Intent intent) {
        return null;
    }

    @Override
    public void onDisconnect(Intent intent) {
    }
}