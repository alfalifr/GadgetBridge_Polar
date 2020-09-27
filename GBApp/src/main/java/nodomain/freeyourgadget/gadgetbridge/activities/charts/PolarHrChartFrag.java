package nodomain.freeyourgadget.gadgetbridge.activities.charts;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.polar.polarsdkecghrdemo.HRFrag;
import com.polar.polarsdkecghrdemo.TimePlotter;

import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

import nodomain.freeyourgadget.gadgetbridge.devices.polar.PolarConnectionManager;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.PolarHrData_;
import nodomain.freeyourgadget.gadgetbridge.util.db.PolarHrDataHandler;
import polar.com.sdk.api.PolarBleApi;
import polar.com.sdk.api.PolarBleApiCallback;
import polar.com.sdk.api.PolarBleApiDefaultImpl;
import polar.com.sdk.api.errors.PolarInvalidArgument;
import polar.com.sdk.api.model.PolarDeviceInfo;
import polar.com.sdk.api.model.PolarHrData;
import sidev.lib.android.siframe.tool.util.fun._ActFragFunKt;
import sidev.lib.android.siframe.tool.util.fun._LogFunKt;
import sidev.lib.annotation.Modified;

@Modified(arg = "added")
public class PolarHrChartFrag extends HRFrag {

    private GBDevice mDevice;
    private PolarConnectionManager mConnectionManager;
    private PolarHrDataHandler dbHandler;

    //Sebagian besar copy-an dari kode aslinya
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        skipOnCreate= true;
        super.onCreate(savedInstanceState);
        classContext= requireContext();
//        setContentView(R.layout.activity_hr);
        assert getArguments() != null;

        mDevice= getArguments().getParcelable("device");
        assert mDevice != null;
        mConnectionManager= new PolarConnectionManager(requireContext(), mDevice);

        dbHandler= new PolarHrDataHandler(requireContext());

        DEVICE_ID = mDevice.getAddress(); //getArguments().getString("id"); //getIntent().getStringExtra("id");
        textViewHR = findViewById(com.polar.polarsdkecghrdemo.R.id.info2);
        textViewFW = findViewById(com.polar.polarsdkecghrdemo.R.id.fw2);

        plot = findViewById(com.polar.polarsdkecghrdemo.R.id.plot2);

/*
        api = PolarBleApiDefaultImpl.defaultImplementation(requireContext(),
                PolarBleApi.FEATURE_BATTERY_INFO |
                        PolarBleApi.FEATURE_DEVICE_INFO |
                        PolarBleApi.FEATURE_HR);
 */
//        api.setApiCallback();

        api = mConnectionManager.getApi();
        mConnectionManager.setCallback(new PolarBleApiCallback() {
            @Override
            public void blePowerStateChanged(boolean b) {
                Log.d(TAG, "BluetoothStateChanged " + b);
            }

            @Override
            public void deviceConnected(PolarDeviceInfo s) {
                Log.d(TAG, "Device connected " + s.deviceId);
                Toast.makeText(classContext, com.polar.polarsdkecghrdemo.R.string.connected,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void deviceConnecting(PolarDeviceInfo polarDeviceInfo) {

            }

            @Override
            public void deviceDisconnected(PolarDeviceInfo s) {
                Log.d(TAG, "Device disconnected " + s);

            }

            @Override
            public void ecgFeatureReady(String s) {
                Log.d(TAG, "ECG Feature ready " + s);
            }

            @Override
            public void accelerometerFeatureReady(String s) {
                Log.d(TAG, "ACC Feature ready " + s);
            }

            @Override
            public void ppgFeatureReady(String s) {
                Log.d(TAG, "PPG Feature ready " + s);
            }

            @Override
            public void ppiFeatureReady(String s) {
                Log.d(TAG, "PPI Feature ready " + s);
            }

            @Override
            public void biozFeatureReady(String s) {

            }

            @Override
            public void hrFeatureReady(String s) {
                Log.d(TAG, "HR Feature ready " + s);
            }

            @Override
            public void disInformationReceived(String s, UUID u, String s1) {
                if( u.equals(UUID.fromString("00002a28-0000-1000-8000-00805f9b34fb"))) {
                    String msg = "Firmware: " + s1.trim();
                    Log.d(TAG, "Firmware: " + s + " " + s1.trim());
                    textViewFW.append(msg + "\n");
                }
            }

            @Override
            public void batteryLevelReceived(String s, int i) {
                String msg = "ID: " + s + "\nBattery level: " + i;
                Log.d(TAG, "Battery level " + s + " " + i);
//                Toast.makeText(classContext, msg, Toast.LENGTH_LONG).show();
                textViewFW.append(msg + "\n");
            }

            @Override
            public void hrNotificationReceived(@NonNull String s,
                                               @NonNull PolarHrData polarHrData) {
                Log.d(TAG, "HR " + polarHrData.hr);
                List<Integer> rrsMs = polarHrData.rrsMs;
                String msg = String.valueOf(polarHrData.hr) + "\n";
                for (int i : rrsMs) {
                    msg += i + ",";
                }
                if (msg.endsWith(",")) {
                    msg = msg.substring(0, msg.length() - 1);
                }
                textViewHR.setText(msg);
                plotter.addValues(polarHrData);
                dbHandler.insert(new PolarHrData_(polarHrData));
            }

            @Override
            public void polarFtpFeatureReady(String s) {
                Log.d(TAG, "Polar FTP ready " + s);
            }
        });

        try {
//            api.connectToDevice(DEVICE_ID);
            // Agar gak 2x connect stelah connect di MainAct.
            if(!mDevice.isConnected() && !mDevice.isConnecting())
                mConnectionManager.connectToDevice(DEVICE_ID);
        } catch (PolarInvalidArgument a){
            a.printStackTrace();
            _ActFragFunKt.toast(requireContext(), "Can't connect to device with identifier: " +DEVICE_ID +". \nPlease restart di Activity.");
            _LogFunKt.loge(this, "Can't connect to device with identifier: " +DEVICE_ID, a);
//            Toast.makeText(requireContext(), "Error= " +a.getMessage(), Toast.LENGTH_SHORT).show();
        }

        plotter = new TimePlotter(requireContext(), "HR/RR");
        plotter.setListener(this);

        plot.addSeries(plotter.getHrSeries(), plotter.getHrFormatter());
        plot.addSeries(plotter.getRrSeries(), plotter.getRrFormatter());
        plot.setRangeBoundaries(50, 100,
                BoundaryMode.AUTO);
        plot.setDomainBoundaries(0, 360000,
                BoundaryMode.AUTO);
        // Left labels will increment by 10
        plot.setRangeStep(StepMode.INCREMENT_BY_VAL, 10);
        plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 60000);
        // Make left labels be an integer (no decimal places)
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).
                setFormat(new DecimalFormat("#"));
        // These don't seem to have an effect
        plot.setLinesPerRangeLabel(2);
    }
}
