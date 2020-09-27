package com.polar.polarsdkecghrdemo;

import android.content.Context;
import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYPlot;

import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;

//import java.util.List;
import java.util.UUID;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import polar.com.sdk.api.PolarBleApi;
import polar.com.sdk.api.PolarBleApiCallback;
import polar.com.sdk.api.PolarBleApiDefaultImpl;
import polar.com.sdk.api.model.PolarDeviceInfo;
import polar.com.sdk.api.model.PolarEcgData;
import polar.com.sdk.api.model.PolarHrData;
import polar.com.sdk.api.model.PolarSensorSetting;
import polar.com.sdk.api.errors.PolarInvalidArgument;
import sidev.lib.android.siframe.lifecycle.fragment.Frag;
import sidev.lib.android.siframe.tool.util.fun._ActFragFunKt;
import sidev.lib.android.siframe.tool.util.fun._LogFunKt;
import sidev.lib.annotation.Modified;


@Modified(arg = "inherits AppCompatActivity -> Fragment, name ECGActivity -> ECGFrag")
public class ECGFrag extends Frag /*AppCompatActivity*/ implements PlotterListener {

    @Modified(arg = "private -> protected")
    protected XYPlot plot;

    @Modified(arg = "private -> protected")
    protected Plotter plotter;

    @Modified(arg = "private -> protected")
    protected TextView textViewHR, textViewFW;

    @Modified(arg = "private -> protected")
    protected String TAG = "Polar_ECGActivity";

    public PolarBleApi api;

    @Modified(arg = "private -> protected")
    protected Disposable ecgDisposable = null;

    @Modified(arg = "private -> protected")
    protected Context classContext;

    @Modified(arg = "private -> protected")
    protected String DEVICE_ID;

    @Override
    public void _initView(@NotNull View view) { /*do nothing*/ }

    @Override
    public int getLayoutId() {
        return R.layout.activity_ecg;
    }

    @Modified(arg = "added")
    protected boolean skipOnCreate= false;

    @Modified(arg = "body")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!skipOnCreate){
//        setContentView(R.layout.activity_ecg);
            classContext= requireContext();
            DEVICE_ID = getArguments().getString("id"); //getIntent().getStringExtra("id");
            textViewHR = findViewById(R.id.info);
            textViewFW = findViewById(R.id.fw);

            plot = findViewById(R.id.plot);

            api = PolarBleApiDefaultImpl.defaultImplementation(requireContext(),
                    PolarBleApi.FEATURE_POLAR_SENSOR_STREAMING |
                            PolarBleApi.FEATURE_BATTERY_INFO |
                            PolarBleApi.FEATURE_DEVICE_INFO |
                            PolarBleApi.FEATURE_HR);
            api.setApiCallback(new PolarBleApiCallback() {
                @Override
                public void blePowerStateChanged(boolean b) {
                    Log.d(TAG, "BluetoothStateChanged " + b);
                }

                @Override
                public void deviceConnected(PolarDeviceInfo s) {
                    Log.d(TAG, "Device connected " + s.deviceId);
                    Toast.makeText(classContext, R.string.connected,
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
                    streamECG();
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
                public void hrNotificationReceived(String s,
                                                   PolarHrData polarHrData) {
                    Log.d(TAG, "HR " + polarHrData.hr);
                    textViewHR.setText(String.valueOf(polarHrData.hr));
                }

                @Override
                public void polarFtpFeatureReady(String s) {
                    Log.d(TAG, "Polar FTP ready " + s);
                }
            });
            try {
                api.connectToDevice(DEVICE_ID);
            } catch (PolarInvalidArgument a){
                a.printStackTrace();
                _ActFragFunKt.toast(requireContext(), "Can't connect to device with identifier: " +DEVICE_ID +". \nPlease restart di Activity.");
                _LogFunKt.loge(this, "Can't connect to device with identifier: " +DEVICE_ID, a);
            }

            plotter = new Plotter(requireContext(), "ECG");
            plotter.setListener(this);

            plot.addSeries(plotter.getSeries(), plotter.getFormatter());
            plot.setRangeBoundaries(-3.3, 3.3, BoundaryMode.FIXED);
            plot.setRangeStep(StepMode.INCREMENT_BY_FIT, 0.55);
            plot.setDomainBoundaries(0, 500, BoundaryMode.GROW);
            plot.setLinesPerRangeLabel(2);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        api.shutDown();
    }

    public void streamECG() {
        if (ecgDisposable == null) {
            ecgDisposable =
                    api.requestEcgSettings(DEVICE_ID).toFlowable().flatMap(new Function<PolarSensorSetting, Publisher<PolarEcgData>>() {
                        @Override
                        public Publisher<PolarEcgData> apply(PolarSensorSetting sensorSetting) throws Exception {
                            return api.startEcgStreaming(DEVICE_ID,
                                    sensorSetting.maxSettings());
                        }
                    }).observeOn(AndroidSchedulers.mainThread()).subscribe(
                            new Consumer<PolarEcgData>() {
                                @Override
                                public void accept(PolarEcgData polarEcgData) throws Exception {
                                    Log.d(TAG, "ecg update");
                                    for (Integer data : polarEcgData.samples) {
                                        plotter.sendSingleSample((float) ((float) data / 1000.0));
                                    }
                                }
                            },
                            new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Log.e(TAG,
                                            "" + throwable.getLocalizedMessage());
                                    ecgDisposable = null;
                                }
                            },
                            new Action() {
                                @Override
                                public void run() throws Exception {
                                    Log.d(TAG, "complete");
                                }
                            }
                    );
        } else {
            // NOTE stops streaming if it is "running"
            ecgDisposable.dispose();
            ecgDisposable = null;
        }
    }

    @Override
    public void update() {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                plot.redraw();
            }
        });
    }
}
