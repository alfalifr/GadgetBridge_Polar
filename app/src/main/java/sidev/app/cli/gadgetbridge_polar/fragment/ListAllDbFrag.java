package sidev.app.cli.gadgetbridge_polar.fragment;

import android.view.View;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.GBException;
import sidev.lib.android.siframe.adapter.DialogListAdp;
import sidev.lib.android.siframe.db.Attribute;
import sidev.lib.android.siframe.lifecycle.fragment.RvFrag;
import sidev.lib.android.siframe.tool.util._DbUtil;

public class ListAllDbFrag extends RvFrag<DialogListAdp<String>> {

    @NotNull
    @Override
    public DialogListAdp<String> initRvAdp() {
        return new DialogListAdp<>(requireContext());
    }

    @Override
    public void _initView(@NotNull View view) {
        ArrayList<String> strList= new ArrayList<>();
        strList.add("===================== all db name ====================");

        _DbUtil.SQLite.Operation op= null;
        try {
            op = _DbUtil.SQLite.INSTANCE.beginOp(GBApplication.acquireDB().getHelper());
        } catch (GBException e) {
            e.printStackTrace();
        }
        assert op != null;

        String[] tableNames= op.listAllTableName();
        Collections.addAll(strList, tableNames);

        strList.add("===================== all db attrib ====================");
        for(String name : tableNames){
            strList.add(" == table " +name +" == ");
            String sql= op.getTableSqlDeclaration(name);
            strList.add(" -- sql: " +sql);
            List<Attribute> attrs= op.listTableAttribute(name);
            for(Attribute attr: attrs){
                strList.add(" -- " +name +" [" +attr.getIndex() +"] " +attr.getName() +" " +attr.getType() +" isPrimary: " +attr.isPrimary());
            }
        }
        rvAdp.setDataList(strList);
    }
}
