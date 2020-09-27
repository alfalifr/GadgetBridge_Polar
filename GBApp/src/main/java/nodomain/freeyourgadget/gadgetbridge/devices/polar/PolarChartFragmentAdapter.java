package nodomain.freeyourgadget.gadgetbridge.devices.polar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import nodomain.freeyourgadget.gadgetbridge.activities.charts.PolarEcgChartFrag;
import nodomain.freeyourgadget.gadgetbridge.activities.charts.PolarHrChartFrag;
import nodomain.freeyourgadget.gadgetbridge.adapter.ChartFragmentAdapter;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;

//TODO: polar later
public class PolarChartFragmentAdapter extends ChartFragmentAdapter {
    @NonNull
    private GBDevice mDevice;
    /**
     *
     * @param fm
     * @param device harus memiliki tipe == `DeviceType.POLAR`
     */
    public PolarChartFragmentAdapter(FragmentManager fm, @NonNull GBDevice device) {
        super(fm);
        if(device.getType() != DeviceType.POLAR)
            throw new IllegalArgumentException("GBDevice yg di-pass bkn merupakan tipe Polar.");
        mDevice= device;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment frag;
        switch (position){
            case 0: frag= new PolarHrChartFrag(); break;
            case 1: frag= new PolarEcgChartFrag(); break;
            default: throw new IndexOutOfBoundsException("Posisi adapter PolarChartFragmentAdapter hanya sampai index 1");
        }
        Bundle arg= new Bundle();
        arg.putString("id", mDevice.getAddress());
        arg.putParcelable("device", mDevice);
        frag.setArguments(arg);
        return frag;
    }

    @Override
    public String getPageTitle(int position) {
        switch (position){
            case 0: return "Polar Heart Rate";
            case 1: return "Polar Electrocardiac Graph";
        }
        throw new IndexOutOfBoundsException("Posisi adapter PolarChartFragmentAdapter hanya sampai index 1");
    }
}
