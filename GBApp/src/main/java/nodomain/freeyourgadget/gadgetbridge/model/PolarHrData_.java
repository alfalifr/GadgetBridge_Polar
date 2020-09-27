package nodomain.freeyourgadget.gadgetbridge.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import polar.com.sdk.api.model.PolarHrData;
import sidev.lib.android.siframe.model.intfc.ModelId;

/**
 * Versi yg modifikasi agar menyimpan data timestamp juga.
 * Kelas model ini juga meng-override field lainnya agar dapat dideteksi
 * oleh `SQLiteHandler` dan disimpan di DB.
 */
public class PolarHrData_ extends PolarHrData {
//    public static final String TABLENAME = "DEVICE_ATTRIBUTES";

    /**
     * Heart rate in BPM (beats per minute).
     */
    public int hr;

    /**
     * R is the peak of the QRS complex in the ECG wave and RR is the interval between successive Rs.
     * In 1/1024 format.
     */
    public List<Integer> rrs;

    /**
     * RRs in milliseconds.
     */
    public List<Integer> rrsMs;

    /**
     * Equals true if the sensor has contact (with a measurable surface e.g. skin).
     */
    public boolean contactStatus;

    /**
     * Equals true if the sensor supports contact status
     */
    public boolean contactStatusSupported;

    /**
     * Equals true if RR data is available.
     */
    public boolean rrAvailable;

    /**
     * Last sample timestamp in nanoseconds.
     */
    @ModelId
    public long timestamp;

    public PolarHrData_(int hr, List<Integer> rrs, boolean contactStatus, boolean contactStatusSupported, boolean rrAvailable, long timestamp) {
        super(hr, rrs, contactStatus, contactStatusSupported, rrAvailable);

        this.hr = hr;
        this.rrs = rrs;
        this.contactStatus = contactStatus;
        this.contactStatusSupported = contactStatusSupported;
        this.rrAvailable = rrAvailable;
        rrsMs = new ArrayList<>();
        for( int rrRaw : rrs ){
            rrsMs.add((int)(Math.round(((float) rrRaw /1024.0)*1000.0)));
        }
        this.timestamp= timestamp;
    }

    public PolarHrData_(int hr, List<Integer> rrs, boolean contactStatus, boolean contactStatusSupported, boolean rrAvailable) {
        this(hr, rrs, contactStatus, contactStatusSupported, rrAvailable, new Date().getTime());
    }

    public PolarHrData_(PolarHrData baseFormData) {
        this(baseFormData.hr, baseFormData.rrs, baseFormData.contactStatus, baseFormData.contactStatusSupported,
                baseFormData.rrAvailable, new Date().getTime());
    }
}
