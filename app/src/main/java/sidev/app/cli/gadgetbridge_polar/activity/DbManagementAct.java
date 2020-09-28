package sidev.app.cli.gadgetbridge_polar.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;

import org.jetbrains.annotations.NotNull;

import static sidev.lib.collection._ConvertFunKt.*;

import kotlin.Unit;
import nodomain.freeyourgadget.gadgetbridge.activities.DbManagementActivity;
import sidev.app.cli.gadgetbridge_polar.R;
import sidev.app.cli.gadgetbridge_polar.util.Util;
import sidev.lib.android.siframe.view.tool.dialog.DialogListView;
import static sidev.lib.android.siframe.tool.util.fun._ActFragFunKt.*;

public class DbManagementAct extends DbManagementActivity {
    Button exportBtn;
    ExportDialogListView dialog;

    @Override
    protected int getLayoutId(){return R.layout.act_db_management;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        exportBtn= findViewById(R.id.exportDBButton);

        exportBtn.setOnClickListener(v -> {
            if(dialog == null){
                dialog= new ExportDialogListView(this);
                dialog.setDataList(toList(ExportKind.values()));
                dialog.showtBtnAction(false);
                dialog.setTitle("Export data to...");
                dialog.setOnItemClickListener((_v, _int, data) -> {
                    exportData(data);
                    dialog.cancel();
                    return Unit.INSTANCE;
                });
            }
            dialog.show();
        });
    }

    void exportData(ExportKind kind){
        String msg= "<export-data>";
        switch (kind){
            case SQLite: {
                msg= "Data exported to SQLite form";
                super.exportDB();
                break;
            }
            case CSV: {
                msg= "Data exported to CSV form";
                Util.exportDbToCsv(this);
                break;
            }
            //TODO: export
        }
        toast(this, msg);
    }


}
enum ExportKind {
    SQLite,
    CSV
}
class ExportDialogListView extends DialogListView<ExportKind>{
    public ExportDialogListView(@NotNull Context c) {
        super(c);
    }
}