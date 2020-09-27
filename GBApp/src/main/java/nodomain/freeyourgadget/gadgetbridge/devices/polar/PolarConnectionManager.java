package nodomain.freeyourgadget.gadgetbridge.devices.polar;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import polar.com.sdk.api.PolarBleApi;
import polar.com.sdk.api.PolarBleApiCallback;
import polar.com.sdk.api.PolarBleApiDefaultImpl;
import polar.com.sdk.api.errors.PolarInvalidArgument;
import polar.com.sdk.api.model.PolarDeviceInfo;
import sidev.lib.android.siframe.tool.util.fun._LogFunKt;

/**
 * Kelas yg mengatur koneksi ke gelang Polar. Manager ini dapat menggunakan `GBDevice` yg sama
 * sehingga state sebelumnya tidak hilang.
 */
public class PolarConnectionManager {
    enum Connection{
        CONNECTED,
        DISCONNECTED,
        CONNECTING,
    }

    @NonNull
    private Context mContext;
    @NonNull
    private GBDevice mGBDevice;
    @NonNull
    private PolarBleApi mApi;
    @Nullable
    private PolarBleApiCallback mCallback;
    @NonNull
    private Connection mConnectionState = Connection.DISCONNECTED;

    public PolarConnectionManager(@NonNull Context c, @NonNull GBDevice device){
        // Validasi device
        if(device.getType() != DeviceType.POLAR)
            throw new IllegalArgumentException("Device bkn merupakan Polar");

        mGBDevice= device;
        setConnectionState(device.getState());

        mContext= c;

        mApi = PolarBleApiDefaultImpl.defaultImplementation(c,
                PolarBleApi.FEATURE_BATTERY_INFO |
                        PolarBleApi.FEATURE_DEVICE_INFO |
                        PolarBleApi.FEATURE_HR |
                        PolarBleApi.FEATURE_POLAR_SENSOR_STREAMING);
    }

    public void setCallback(@Nullable final PolarBleApiCallback c){
        mCallback = null;
        if(c != null){
            // callback `c` di-wrap agar connectionState dapat diupdate scr otomatis.
            mCallback = new PolarBleApiCallback() {
                /**
                 * Device is now connected
                 *
                 * @param polarDeviceInfo Polar device information
                 */
                @Override
                public void deviceConnected(@NonNull PolarDeviceInfo polarDeviceInfo) {
                    c.deviceConnected(polarDeviceInfo);
                    setConnectionState(Connection.CONNECTED);
                }

                /**
                 * Connecting to device
                 *
                 * @param polarDeviceInfo Polar device information
                 */
                @Override
                public void deviceConnecting(@NonNull PolarDeviceInfo polarDeviceInfo) {
                    c.deviceConnecting(polarDeviceInfo);
                    setConnectionState(Connection.CONNECTING);
                }

                /**
                 * Device is now disconnected, no further action is needed from the application
                 * if polar.com.sdk.api.PolarBleApi#disconnectFromPolarDevice is not called. Device will be automatically reconnected
                 *
                 * @param polarDeviceInfo Polar device information
                 */
                @Override
                public void deviceDisconnected(@NonNull PolarDeviceInfo polarDeviceInfo) {
                    c.deviceDisconnected(polarDeviceInfo);
                    setConnectionState(Connection.DISCONNECTED);
                }
            };
        }
        mApi.setApiCallback(mCallback);
    }

    @NonNull
    public PolarBleApi getApi(){
        return mApi;
    }

    @NonNull
    public Connection getConnectionState() {
        return mConnectionState;
    }

    private void setConnectionState(@NonNull Connection state){
        mConnectionState = state;
        switch (state){
            case DISCONNECTED: mGBDevice.setState(GBDevice.State.NOT_CONNECTED); break;
            case CONNECTED: mGBDevice.setState(GBDevice.State.CONNECTED); break;
            case CONNECTING: mGBDevice.setState(GBDevice.State.CONNECTING); break;
        }
    }
    private void setConnectionState(@NonNull GBDevice.State state){
        switch (state){
            case NOT_CONNECTED: mConnectionState = Connection.DISCONNECTED; break;
            case CONNECTED: mConnectionState = Connection.CONNECTED; break;
            case CONNECTING: mConnectionState = Connection.CONNECTING; break;
            default: {
                _LogFunKt.loge(this, "Status device polar: " +state +" diabaikan karena PolarConnectionManager hanya mendukung 3 status (lihat PolarConnectionManager.Connection)");
                mConnectionState = Connection.DISCONNECTED;
            }
        }
    }

    /**
     *
     * @param id dapat berupa device id atau BT address.
     */
    public void connectToDevice(@Nullable String id) throws PolarInvalidArgument {
        mApi.connectToDevice(id != null ? id : mGBDevice.getAddress());
    }

    public void disconnectFromDevice(@Nullable String id) throws PolarInvalidArgument{
        mApi.disconnectFromDevice(id != null ? id : mGBDevice.getAddress());
    }
}
