package com.example.clockphone.slice;

import com.example.clockphone.ResourceTable;
import com.example.clockphone.net.Net;
import com.example.clockphone.net.NetIf;
import com.example.clockphone.provider.DeleteClockItemProvider;
import com.example.clockphone.bean.Clock;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.CommonDialog;
import ohos.agp.window.dialog.IDialog;
import org.devio.hi.json.HiJson;

import java.util.ArrayList;
import java.util.List;

public class DeleteClockAbilitySlice extends AbilitySlice {
    private ListContainer listContainer;
    private Checkbox checkboxSelectAll;
    private DeleteClockItemProvider listViewItemProvider;
    private Net net = new Net();

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_delete_clock);
        initListContainer();
        initView();
    }

    private void initListContainer() {
        listContainer = (ListContainer) this.findComponentById(ResourceTable.Id_list_clock_view);
        listViewItemProvider = new DeleteClockItemProvider(this);
        net.getAllClock(new NetIf.NetListener() {
            @Override
            public void onSuccess(HiJson res) {
                bindData(res);
            }

            @Override
            public void onFail(String message) {

            }
        });
    }

    private void initView() {
        Image imageCancel = (Image) findComponentById(ResourceTable.Id_image_cancel);
        imageCancel.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                terminate();
            }
        });

        checkboxSelectAll = (Checkbox) findComponentById(ResourceTable.Id_checkbox_selectall);
        checkboxSelectAll.setCheckedStateChangedListener((absButton, isCheck) -> {
            List<Clock> clockList = listViewItemProvider.getDataList();
            for (Clock clock : clockList) {
                clock.setSends(2);
            }
            listViewItemProvider.notifyDataChanged();
        });

        Button btnDelete = (Button) findComponentById(ResourceTable.Id_button_delete);
        btnDelete.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                List<Clock> clockList = listViewItemProvider.getDataList();
                int selectClock = 0;
                for (Clock clock : clockList) {
                    if (clock.getSends() == 2) {
                        selectClock++;
                    }
                }
                if (selectClock > 0) {
                    CommonDialog alertDialog = new CommonDialog(DeleteClockAbilitySlice.this);
                    TextField textFieldName = new TextField(DeleteClockAbilitySlice.this);
                    textFieldName.setText(String.format("是否删%d个闹钟?", selectClock));
                    textFieldName.setTextSize(20, Text.TextSizeType.FP);
                    DirectionalLayout.LayoutConfig layoutConfig = new DirectionalLayout.LayoutConfig();
                    layoutConfig.alignment = LayoutAlignment.CENTER;
                    textFieldName.setLayoutConfig(layoutConfig);
                    alertDialog.setContentCustomComponent(textFieldName);
                    alertDialog.setButton(IDialog.BUTTON1, "取消", new IDialog.ClickedListener() {
                        @Override
                        public void onClick(IDialog iDialog, int i) {
                            iDialog.destroy();
                        }
                    });
                    alertDialog.setButton(IDialog.BUTTON3, "删除", new IDialog.ClickedListener() {
                        @Override
                        public void onClick(IDialog iDialog, int i) {
                            List<Clock> clockDeleteList = listViewItemProvider.getDataList();
                            int count = 0;
                            for (Clock clock : clockDeleteList) {
                                if (clock.getSends() == 2) {
                                    count++;
                                }
                            }
                            int[] clockDeleteInts = new int[count];
                            List<Clock> clockDeleteLists = new ArrayList<>();
                            int j = 0;
                            for (Clock clock : clockDeleteList) {
                                if (clock.getSends() == 2) {
                                    clockDeleteLists.add(clock);
                                    clockDeleteInts[j] = clock.getClockid();
                                    j++;
                                }
                            }
                            net.deleteClock(clockDeleteInts, new NetIf.NetListener() {
                                @Override
                                public void onSuccess(HiJson res) {

                                }

                                @Override
                                public void onFail(String message) {

                                }
                            });
                            iDialog.destroy();
                            net.getAllClock(new NetIf.NetListener() {
                                @Override
                                public void onSuccess(HiJson res) {
                                    bindData(res);
                                }

                                @Override
                                public void onFail(String message) {

                                }
                            });
                        }
                    });
                    alertDialog.show();
                }
            }
        });
    }

    private void bindData(HiJson res) {
        HiJson hiJson;
        int count = res.count();
        List<Clock> clockLists = new ArrayList<>();
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
            clockLists.add(clock);
            i++;
        }
        listViewItemProvider.setDataList(clockLists);
        listContainer.setItemProvider(listViewItemProvider);
        listViewItemProvider.notifyDataChanged();
        listContainer.setItemProvider(listViewItemProvider);
        checkboxSelectAll.setChecked(false);
    }
}
