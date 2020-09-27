package sidev.app.cli.gadgetbridge_polar.util;

import android.content.Context;

import androidx.annotation.NonNull;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.internal.DaoConfig;
import kotlin.Pair;
import kotlin.Triple;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.GBException;
import nodomain.freeyourgadget.gadgetbridge.database.DBHandler;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoMaster;
import nodomain.freeyourgadget.gadgetbridge.util.db.PolarEcgDataHandler;
import nodomain.freeyourgadget.gadgetbridge.util.db.PolarHrDataHandler;
import sidev.lib.android.siframe.db.DbCursor;
import sidev.lib.android.siframe.tool.util._DbUtil;
import sidev.lib.android.siframe.tool.util.fun._ActFragFunKt;

import static sidev.lib.android.siframe.tool.util.fun._ActFragFunKt.toast;

public class Util {
    private Util(){}
//    public Util getInstance(){ return new Util(); }

    /**
     * Mengambil semua record pada DB yg merupakan tabel data dari modul GadgetBridge.
     */
    @NonNull
    public static List<Pair<String /*tableName*/, List<Triple<Integer, String, Object>>> /*rowList*/>
    getGadgetBridgeAllDbRow(@NonNull Context c){
        List<Pair<String, List<Triple<Integer, String, Object>>>> resList= new ArrayList<>();

        try(DBHandler handler= GBApplication.acquireDB()){
            //Begin operation
            _DbUtil.SQLite.Operation op= _DbUtil.SQLite.INSTANCE.beginOp(handler.getHelper());
            //For each dao class in GB module.

            for(Class cls: getGagdetBrigdeDaoClassList()){
                String tableName= (String) cls.getField("TABLENAME").get(null);
                assert tableName != null;
                List<Triple<Integer, String, Object>> rows= op.getAllRowFromTable(tableName);
                resList.add(new Pair(tableName, rows));
            }

        } catch (Exception e) {
            e.printStackTrace();
            _ActFragFunKt.toast(c, "Can't get all rows for GadgetBridge data");
        }
        return resList;
    }

    /**
     * Mengambil semua record pada DB yg merupakan tabel data dari modul GadgetBridge + data kustom dari modul Polar.
     */
    @NonNull
    public static List<Pair<String /*tableName*/, List<Triple<Integer, String, Object>>> /*rowList*/>
    getAllDbRow(@NonNull Context c){
        List<Pair<String, List<Triple<Integer, String, Object>>>> resList= getGadgetBridgeAllDbRow(c);

        try(DBHandler handler= GBApplication.acquireDB()){
            _DbUtil.SQLite.Operation op= _DbUtil.SQLite.INSTANCE.beginOp(handler.getHelper());
//        PolarHrDataHandler polarHrDataHandler= new PolarHrDataHandler(c);
//        PolarEcgDataHandler polarEcgDataHandler= new PolarEcgDataHandler(c);

            String hrTableName= PolarHrDataHandler.tableName;
            List<Triple<Integer, String, Object>> hrRows= op.getAllRowFromTable(hrTableName);
            resList.add(new Pair(hrTableName, hrRows));

            String ecgTableName= PolarEcgDataHandler.tableName;
            List<Triple<Integer, String, Object>> ecgRows= op.getAllRowFromTable(ecgTableName);
            resList.add(new Pair(ecgTableName, ecgRows));
        } catch (Exception e){
            e.printStackTrace();
            _ActFragFunKt.toast(c, "Can't get all rows for Polar data");
        }

        return resList;
    }


    /**
     * Mengambil semua DAO, tidak termasuk data yg dari Polar karena beda format.
     */
    @NonNull
    public static List<Class<? extends AbstractDao<?, ?>>> getGagdetBrigdeDaoClassList() throws GBException, NoSuchFieldException, IllegalAccessException {
        DBHandler handler= GBApplication.acquireDB();

        DaoMaster master= handler.getDaoMaster();
        Field f= master.getClass().getField("daoConfigMap"); //.get(master);
        f.setAccessible(true);

//            @SuppressWarnings({SuppressLiteral.UNCHECKED_CAST})
        Map<Class<? extends AbstractDao<?, ?>>, DaoConfig> daoMap =
                (Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>) f.get(master);
        assert daoMap != null;

        return new ArrayList<>(daoMap.keySet());
    }

    public static void exportCsvData(@NonNull Context c){
        try(DBHandler handler= GBApplication.acquireDB()){
            _DbUtil.SQLite.Operation op= _DbUtil.SQLite.INSTANCE.beginOp(handler.getHelper());
//        PolarHrDataHandler polarHrDataHandler= new PolarHrDataHandler(c);
//        PolarEcgDataHandler polarEcgDataHandler= new PolarEcgDataHandler(c);

            List<String> tableNameList= new ArrayList<>();
            for(Class cls: getGagdetBrigdeDaoClassList()){
                String tableName= (String) cls.getField("TABLENAME").get(null);
                assert tableName != null;
                tableNameList.add(tableName);
            }
            tableNameList.add(PolarHrDataHandler.tableName);
            tableNameList.add(PolarEcgDataHandler.tableName);

            for(String tableName : tableNameList){
                DbCursor cursor= op.query("SELECT * FROM " +tableName);

                if(cursor.moveToFirst()){
                    FileWriter out= new FileWriter(FileUtil.getCsvFileDest(tableName +".csv"));
                    String[] headers= cursor.getColumnNames();
                    CSVPrinter printer= new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(headers));

                    int i= 0;
                    while (cursor.moveToPosition(i++)){
                        Object[] values= cursor.getValuesAtCurrentRow();
                        printer.printRecord(values);
                    }
                }
            }
            _ActFragFunKt.toast(c, "DB exported to CSVs successfully");
        } catch (Exception e){
            e.printStackTrace();
            _ActFragFunKt.toast(c, "Can't get all rows for Polar data. \nerror= " +e.getMessage());
        }
    }
}