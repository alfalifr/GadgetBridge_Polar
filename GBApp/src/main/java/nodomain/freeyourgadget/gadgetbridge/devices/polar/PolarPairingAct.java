package nodomain.freeyourgadget.gadgetbridge.devices.polar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import kotlin.Unit;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.GBException;
import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.activities.AbstractGBActivity;
import nodomain.freeyourgadget.gadgetbridge.activities.ControlCenterv2;
import nodomain.freeyourgadget.gadgetbridge.database.DBHelper;
import nodomain.freeyourgadget.gadgetbridge.devices.DeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.util.AndroidUtils;
import nodomain.freeyourgadget.gadgetbridge.util.BondingInterface;
import nodomain.freeyourgadget.gadgetbridge.util.BondingUtil;
import nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper;
import nodomain.freeyourgadget.gadgetbridge.util.GB;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;
import polar.com.sdk.api.PolarBleApiCallback;
import polar.com.sdk.api.errors.PolarInvalidArgument;
import polar.com.sdk.api.model.PolarDeviceInfo;
import sidev.lib.android.siframe.tool.util._ThreadUtil;
import sidev.lib.android.siframe.tool.util.fun._ActFragFunKt;
import sidev.lib.android.siframe.tool.util.fun._LogFunKt;
//import sidev.lib.android.siframe.tool.util.fun._ActFragFunKt;

public class PolarPairingAct extends AbstractGBActivity implements BondingInterface {

    private TextView pairingTv;
    private boolean isPairing= false;
    private GBDeviceCandidate deviceCandidate;

    private final BroadcastReceiver pairingReceiver = BondingUtil.getPairingReceiver(this);
    private final BroadcastReceiver bondingReceiver = BondingUtil.getBondingReceiver(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polar_pairing);

        this.deviceCandidate = getIntent().getParcelableExtra(DeviceCoordinator.EXTRA_DEVICE_CANDIDATE);

        pairingTv = findViewById(R.id.tv_pairing_msg);
        _ActFragFunKt.toast(this, "PolarPairingAct onCreate()");

        startPairing();
    }

    @Override
    protected void onDestroy() {
        unregisterBroadcastReceivers();
        if(isPairing)
            stopPairing();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        unregisterBroadcastReceivers();
        if(isPairing)
            stopPairing();
        super.onStop();
    }

    /**
     * Called when pairing is complete
     *
     * @param success
     */
    @Override
    public void onBondingComplete(boolean success) {
        if (!isPairing)
            return;
        else
            isPairing = false;

        if (success) {
            String macAddress = deviceCandidate.getMacAddress();
            BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(macAddress);

//            _LogFunKt.loge(this, "onBondingComplete() success= true device= " +device +" device.getBondState()= " +(device != null ? device.getBondState() : null));

            if (device != null && device.getBondState() == BluetoothDevice.BOND_NONE) {
                Prefs prefs = GBApplication.getPrefs();
                //Tambahkan mac address yg berhasil di-pair ke sharedPrefs.
                prefs.getPreferences().edit().putString(PolarConst.PREFS_MAC_ADDRESS_KEY, macAddress).apply();
            }
            broadcastPolarDeviceRegistered();
//                registerDeviceToDb();
            GBApplication.toMainActivity(this, true);
        } else{
            // Cuma toast aja karena pairing opsional dan semua fitur pada Polar diakses scr langsung tanpa pairing.
            GB.toast(this, "Pairing dg " +deviceCandidate +" tidak berhasil.", Toast.LENGTH_LONG, 0);
        }
        finish();
    }

    /**
     * Should return the device that is currently being paired
     **/
    @Override
    public BluetoothDevice getCurrentTarget() {
        return deviceCandidate.getDevice();
    }

    /**
     * This forces bonding activities to encapsulate the removal
     * of all broadcast receivers on demand
     **/
    @Override
    public void unregisterBroadcastReceivers() {
        AndroidUtils.safeUnregisterBroadcastReceiver(LocalBroadcastManager.getInstance(this), pairingReceiver);
        AndroidUtils.safeUnregisterBroadcastReceiver(this, bondingReceiver);
    }

    /**
     * This forces bonding activities to handle the addition
     * of all broadcast receivers in the same place
     **/
    @Override
    public void removeBroadcastReceivers() {
        LocalBroadcastManager.getInstance(this).registerReceiver(pairingReceiver, new IntentFilter(GBDevice.ACTION_DEVICE_CHANGED));
        registerReceiver(bondingReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
    }

    /**
     * Just returns the Context
     */
    @Override
    public Context getContext() {
        return this;
    }


    private void startPairing() {
/*
        isPairing = true;
        pairingTv.setText(getString(nodomain.freeyourgadget.gadgetbridge.R.string.pairing, deviceCandidate));

        if (!BondingUtil.shouldUseBonding()) {
            BondingUtil.attemptToFirstConnect(getCurrentTarget());
            return;
        }
        BondingUtil.tryBondThenComplete(this, deviceCandidate);
 */
/*
        isPairing= true;
        pairingTv.setText(getString(nodomain.freeyourgadget.gadgetbridge.R.string.pairing, deviceCandidate));

        try {
            PolarConnectionManager manager=
                    new PolarConnectionManager(this, new PolarCoordinator().createDevice(deviceCandidate));

            manager.setCallback(new PolarBleApiCallback() {
                @Override
                public void deviceConnected(@NonNull PolarDeviceInfo polarDeviceInfo) {
                    isPairing= false;
                    onBondingComplete(true);
                }

                @Override
                public void deviceConnecting(@NonNull PolarDeviceInfo polarDeviceInfo) {
                    isPairing = true;
                    pairingTv.setText(getString(nodomain.freeyourgadget.gadgetbridge.R.string.pairing, deviceCandidate));
                }

                @Override
                public void deviceDisconnected(@NonNull PolarDeviceInfo polarDeviceInfo) {
                    isPairing= false;
                    onBondingComplete(false);
                }
            });
            manager.connectToDevice(deviceCandidate.getMacAddress());
        } catch (PolarInvalidArgument polarInvalidArgument) {
            polarInvalidArgument.printStackTrace();
        }
// */
        isPairing= true;
        onBondingComplete(true);
    }

    protected void broadcastPolarDeviceRegistered(){
        registerDeviceToDb();
        Intent i= new Intent();
//        i.setAction(GBDevice.ACTION_DEVICE_CHANGED);
        i.setAction(DeviceManager.ACTION_REFRESH_DEVICELIST);
        i.putExtra(GBDevice.EXTRA_DEVICE, new PolarCoordinator().createDevice(deviceCandidate));
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    protected void registerDeviceToDb() {
        try {
            DBHelper.getDevice(
                    new PolarCoordinator().createDevice(deviceCandidate),
                    GBApplication.acquireDB().getDaoSession()
            );
        } catch (GBException e){
            _LogFunKt.loge(this, "registerDeviceToDb() error", e);
        }
    }

    private void stopPairing() {
        isPairing = false;
        BondingUtil.stopBluetoothBonding(deviceCandidate.getDevice());
    }
}
