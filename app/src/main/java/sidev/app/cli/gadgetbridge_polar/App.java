package sidev.app.cli.gadgetbridge_polar;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import sidev.app.cli.gadgetbridge_polar.activity.MainAct;
import sidev.app.cli.gadgetbridge_polar.activity.SingleFragAct_;
import sidev.lib.android.siframe.tool.util.fun._ActFragFunKt;

public class App extends GBApplication {
    static {
        _ActFragFunKt.setSingleFragAct(SingleFragAct_.class, true);
        setMainAct(MainAct.class);
    }
}
