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
import polar.com.sdk.api.model.PolarEcgData;
import sidev.lib.android.siframe.tool.SQLiteHandler;


public class PolarEcgDataHandler extends SQLiteHandler<PolarEcgData> {

    public static final String tableName= "Polar_Ecg_Data";

    public PolarEcgDataHandler(@NotNull Context ctx) {
        this(ctx, null);
    }
    public PolarEcgDataHandler(@NotNull Context ctx, @Nullable SQLiteOpenHelper helper) {
        super(ctx, new EcgCollectionHandler());
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
    protected Class<PolarEcgData> getModelClass() {
        return PolarEcgData.class;
    }

    @Override
    public PolarEcgData createModel(@NotNull Map<String, ?> map) {
        List<Integer> samples= (List) map.get("rrs");
        long timeStamp= (Long) map.get("timeStamp");
        return new PolarEcgData(samples, timeStamp);
    }
}


//@SuppressWarnings(SuppressLiteral.UNCHECKED_CAST)
class EcgCollectionHandler implements SQLiteHandler.CollectionTypeHandler<PolarEcgData>{
    @NotNull
    @Override
    public <C extends List<? extends PolarEcgData>> C flattenQueryResult(
            @NotNull C dataList,
            @NotNull String[] collectionAttribNameList
    ) {
        long lastTimestamp= dataList.get(0).timeStamp;
        List<Integer> lastSamples= new ArrayList<>();
        List<PolarEcgData> newList= new ArrayList<>();

        int limit= dataList.size();
        for(int i = 1; i< limit; i++){
            PolarEcgData data= dataList.get(i);
            boolean isSameRow= data.timeStamp == lastTimestamp;
            if(!isSameRow){
                newList.add(
                        new PolarEcgData(lastSamples, data.timeStamp)
                );
                // Jika row berbeda, maka buat list rrs yg baru.
                lastSamples= new ArrayList<>();
            }
            lastSamples.add(data.samples.get(0));

            // Jika ternyata sampai akhir adalah row yg sama.
            if(i == limit -1 && isSameRow){
                newList.add(
                        new PolarEcgData(lastSamples, data.timeStamp)
                );
            }
            lastTimestamp= data.timeStamp;
        }
        //@SuppressWarnings(SuppressLiteral.UNCHECKED_CAST)
        return (C) newList;
    }

    @Nullable
    @Override
    public Class<?> resolveColumnType(@NotNull Field field) {
        if(field.getName().equals("samples"))
            return Integer.class;
        return null;
    }
}