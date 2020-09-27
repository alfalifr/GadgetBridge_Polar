package sidev.app.cli.gadgetbridge_polar.activity;

import android.os.Bundle;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import nodomain.freeyourgadget.gadgetbridge.activities.ControlCenterv2;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import sidev.app.cli.gadgetbridge_polar.R;
import sidev.app.cli.gadgetbridge_polar.fragment.ListAllDbFrag;
import sidev.lib.android.siframe.tool.util.fun._ActFragFunKt;

public class MainAct extends ControlCenterv2 {
///*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ViewGroup vg= findViewById(R.id.drawer_layout);
//        vg.removeAllViews();
//        vg.addView(getLayoutInflater().inflate(R.layout.activity_main, vg, false));

        TextView toAllDbTv= findViewById(R.id.toAllDbTv);
        toAllDbTv.setOnClickListener(v -> _ActFragFunKt.startSingleFragAct_config(this, ListAllDbFrag.class));
//        toAllDbTv.setOnClickListener(v -> _ActFragFunKt.startAct(this, SingleFragAct_.class));
//        toAllDbTv.setOnClickListener(v -> _ActFragFunKt.toast(this, "Test toast"));
    }
// */

    @Override
    protected int getLayoutId() { return R.layout.activity_main; }

    @NotNull
    @Override
    protected List<GBDevice> getDeviceList() {
        return super.getDeviceList(); //getDummyDevice(1000);
    }

    //TODO: Dummy
    public List<GBDevice> getDummyDevice(int count){
        List<GBDevice> list= new ArrayList();
        String address= "address";
        String name= "name";
        String alias= "alias";
        DeviceType[] type= DeviceType.values();
        int limit= Math.min(count, type.length);
        int i;
        for(i= 0; i < limit; i++){
            DeviceType typeItr= type[i];
            name= getString(typeItr.getName());
            list.add(new GBDevice(address +i, name, name +" _" +i, type[i]));
        }
        i++;
        list.add(new GBDevice(address +i, name +i, alias +i, DeviceType.fromKey(109)));
        return list;
    }

}