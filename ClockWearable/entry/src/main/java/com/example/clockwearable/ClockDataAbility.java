package com.example.clockwearable;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.content.Intent;
import ohos.data.DatabaseHelper;
import ohos.data.dataability.DataAbilityUtils;
import ohos.data.rdb.*;
import ohos.data.resultset.ResultSet;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.net.Uri;
import ohos.utils.PacMap;

import java.io.FileDescriptor;

public class ClockDataAbility extends Ability {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "Demo");
    public static final String DB_NAME = "clockdataability.db";
    public static final String DB_TAB_NAME = "myclock";
    public static final String DB_COLUMN_CLOCK_ID = "clockid";
    public static final String DB_COLUMN_YEAR = "years";
    public static final String DB_COLUMN_MONTH = "months";
    public static final String DB_COLUMN_DAY = "days";
    public static final String DB_COLUMN_HOUR = "hours";
    public static final String DB_COLUMN_MINUTE = "minutes";
    public static final String DB_COLUMN_HAPPENED = "happened";
    private static final int DB_VERSION = 1;
    private StoreConfig config = StoreConfig.newDefaultConfig(DB_NAME);
    private RdbStore rdbStore;
    private RdbOpenCallback rdbOpenCallback = new RdbOpenCallback() {
        @Override
        public void onCreate(RdbStore rdbStore) {
            rdbStore.executeSql("create table if not exists "
                    + DB_TAB_NAME + " ("
                    + DB_COLUMN_CLOCK_ID + " integer primary key, "
                    + DB_COLUMN_YEAR + " integer not null, "
                    + DB_COLUMN_MONTH + " integer not null, "
                    + DB_COLUMN_DAY + " integer not null, "
                    + DB_COLUMN_HOUR + " integer not null, "
                    + DB_COLUMN_MINUTE + " integer not null, "
                    + DB_COLUMN_HAPPENED + " integer not null)");
        }

        @Override
        public void onUpgrade(RdbStore rdbStore, int oldVersion, int newVersion) {

        }
    };

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        HiLog.info(LABEL_LOG, "ClockDataAbility onStart");
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        rdbStore = databaseHelper.getRdbStore(config, DB_VERSION, rdbOpenCallback, null);
    }

    @Override
    public ResultSet query(Uri uri, String[] columns, DataAbilityPredicates predicates) {
        RdbPredicates rdbPredicates = DataAbilityUtils.createRdbPredicates(predicates, DB_TAB_NAME);
        ResultSet resultSet = rdbStore.query(rdbPredicates, columns);
        if (resultSet == null) {
            HiLog.info(LABEL_LOG, "resultSet is null");
        }
        return resultSet;
    }

    @Override
    public int insert(Uri uri, ValuesBucket value) {
        HiLog.info(LABEL_LOG, "ClockDataAbility insert");
        String path = uri.getLastPath();
        if (!DB_TAB_NAME.equals(path)) {
            HiLog.info(LABEL_LOG, "DataAbility insert path is not matched");
            return -1;
        }
        ValuesBucket values = new ValuesBucket();
        values.putInteger(DB_COLUMN_CLOCK_ID, value.getInteger(DB_COLUMN_CLOCK_ID));
        values.putInteger(DB_COLUMN_YEAR, value.getInteger(DB_COLUMN_YEAR));
        values.putInteger(DB_COLUMN_MONTH, value.getInteger(DB_COLUMN_MONTH));
        values.putInteger(DB_COLUMN_DAY, value.getInteger(DB_COLUMN_DAY));
        values.putInteger(DB_COLUMN_HOUR, value.getInteger(DB_COLUMN_HOUR));
        values.putInteger(DB_COLUMN_MINUTE, value.getInteger(DB_COLUMN_MINUTE));
        values.putInteger(DB_COLUMN_HAPPENED, value.getInteger(DB_COLUMN_HAPPENED));
        int index = (int) rdbStore.insert(DB_TAB_NAME, values);
        DataAbilityHelper.creator(this, uri).notifyChange(uri);
        return index;
    }

    @Override
    public int delete(Uri uri, DataAbilityPredicates predicates) {
        RdbPredicates rdbPredicates = DataAbilityUtils.createRdbPredicates(predicates, DB_TAB_NAME);
        int index = rdbStore.delete(rdbPredicates);
        DataAbilityHelper.creator(this, uri).notifyChange(uri);
        return index;
    }

    @Override
    public int update(Uri uri, ValuesBucket value, DataAbilityPredicates predicates) {
        RdbPredicates rdbPredicates = DataAbilityUtils.createRdbPredicates(predicates, DB_TAB_NAME);
        int index = rdbStore.update(value, rdbPredicates);
        DataAbilityHelper.creator(this, uri).notifyChange(uri);
        return index;
    }

    @Override
    public FileDescriptor openFile(Uri uri, String mode) {
        return null;
    }

    @Override
    public String[] getFileTypes(Uri uri, String mimeTypeFilter) {
        return new String[0];
    }

    @Override
    public PacMap call(String method, String arg, PacMap extras) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}