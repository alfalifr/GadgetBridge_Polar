package nodomain.freeyourgadget.gadgetbridge.devices;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import nodomain.freeyourgadget.gadgetbridge.activities.AbstractFragmentPagerAdapter;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import sidev.lib.annotation.Modified;

@Modified(arg = "added")
public interface ExtendedDeviceCoordinator extends DeviceCoordinator {
    @Nullable
    AbstractFragmentPagerAdapter getChartFragmentAdapter(@NonNull FragmentManager fm, @NonNull GBDevice device);
}
