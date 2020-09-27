package nodomain.freeyourgadget.gadgetbridge.util.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.GBException;
import nodomain.freeyourgadget.gadgetbridge.model.PolarHrData_;
import sidev.lib.android.siframe.tool.SQLiteHandler;
import sidev.lib.val.SuppressLiteral;


public class PolarHrDataHandler extends SQLiteHandler<PolarHrData_> {

    public static final String tableName= "Polar_Hr_Data";

    public PolarHrDataHandler(@NotNull Context ctx) {
        this(ctx, null);
    }
    public PolarHrDataHandler(@NotNull Context ctx, @Nullable SQLiteOpenHelper helper) {
        super(ctx, new HrCollectionHandler());
        setTableName(tableName);
        try{
            //Agar db yg jadi tempat penyimpanan sama dg settingan app.
            setSqliteHelperDelegate(helper != null ? helper : GBApplication.acquireDB().getHelper());
        } catch (GBException e){
            throw new IllegalStateException("Can't acquire DB");
        }
    }

    @NotNull
    @Override
    protected Class<PolarHrData_> getModelClass() {
        return PolarHrData_.class;
    }

    @Override
    public PolarHrData_ createModel(@NotNull Map<String, ?> map) {
        int hr= (Integer) map.get("hr");
        List<Integer> rrs= (List) map.get("rrs");
        boolean contactStatus= (Boolean) map.get("contactStatus");
        boolean contactStatusSupported= (Boolean) map.get("contactStatusSupported");
        boolean rrAvailable= (Boolean) map.get("rrAvailable");
        long timestamp= (Long) map.get("timestamp");
        return new PolarHrData_(hr, rrs, contactStatus, contactStatusSupported, rrAvailable, timestamp);
    }
}


//@SuppressWarnings(SuppressLiteral.UNCHECKED_CAST)
class HrCollectionHandler implements SQLiteHandler.CollectionTypeHandler<PolarHrData_>{
    @NotNull
    @Override
    public <C extends List<? extends PolarHrData_>> C flattenQueryResult(
            @NotNull C dataList,
            @NotNull String[] collectionAttribNameList
    ) {
        long lastTimestamp= dataList.get(0).timestamp;
        List<Integer> lastRrs= new ArrayList<>();
        List<PolarHrData_> newList= new ArrayList<>();

        int limit= dataList.size();
        for(int i = 1; i< limit; i++){
            PolarHrData_ data= dataList.get(i);
            boolean isSameRow= data.timestamp == lastTimestamp;
            if(!isSameRow){
                newList.add(
                        new PolarHrData_(data.hr, lastRrs, data.contactStatus, data.contactStatusSupported,
                                data.rrAvailable, data.timestamp)
                );
                // Jika row berbeda, maka buat list rrs yg baru.
                lastRrs= new ArrayList<>();
            }
            lastRrs.add(data.rrs.get(0));

            // Jika ternyata sampai akhir adalah row yg sama.
            if(i == limit -1 && isSameRow){
                newList.add(
                        new PolarHrData_(data.hr, lastRrs, data.contactStatus, data.contactStatusSupported,
                                data.rrAvailable, data.timestamp)
                );
            }
            lastTimestamp= data.timestamp;
        }
        //@SuppressWarnings(SuppressLiteral.UNCHECKED_CAST)
        return (C) newList;
    }

    @Nullable
    @Override
    public Class<?> resolveColumnType(@NotNull Field field) {
        if(field.getName().equals("rrs"))
            return Integer.class;
        return null;
    }
}