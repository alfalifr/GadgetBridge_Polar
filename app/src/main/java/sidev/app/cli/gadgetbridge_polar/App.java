package sidev.app.cli.gadgetbridge_polar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Calendar;

import kotlin.Unit;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import sidev.app.cli.gadgetbridge_polar.activity.MainAct;
import sidev.app.cli.gadgetbridge_polar.activity.SingleFragAct_;
import sidev.lib.android.siframe.tool.util._FileUtil;
import sidev.lib.android.siframe.tool.util._ThreadUtil;
import sidev.lib.android.siframe.tool.util.fun._ActFragFunKt;
import sidev.lib.jvm.tool.util.FileUtil;
import sidev.lib.jvm.tool.util.ThreadUtil;
import sidev.lib.jvm.tool.util.TimeUtil;

public class App extends GBApplication {
    static {
        _ActFragFunKt.setSingleFragAct(SingleFragAct_.class, true);
        setMainAct(MainAct.class);

        Thread.setDefaultUncaughtExceptionHandler( (t, e) -> {
            String timestamp= TimeUtil.INSTANCE.timestamp(Calendar.getInstance(), "dd-MM-yyyy");
            String logFileName= "Log_" +timestamp +".txt";
            File dir= _FileUtil.INSTANCE.getExternalDir(GBApplication.getContext(), "log");

            try {
                if(dir != null){
                    File logFile= new File(dir, logFileName);
                    String timestamp_= TimeUtil.INSTANCE.timestamp();
                    FileUtil.INSTANCE.saveln(logFile, timestamp_ +": ============= New Exception ==========");
                    FileUtil.INSTANCE.saveln(logFile, "--MSG: ====== " +e.getMessage() +" ======");
                    FileUtil.INSTANCE.saveln(logFile, "--CAUSE: ======" +e.getCause() +" ======");

                    PrintWriter pw= new PrintWriter(new FileWriter(logFile, true));
                    e.printStackTrace(pw);
                    pw.close();
                }
            } catch (IOException e2){
                e2.printStackTrace();
            }

//            e.printStackTrace();
            System.exit(1); // Jangan throw, nti infinite loop.
        });
    }
}
