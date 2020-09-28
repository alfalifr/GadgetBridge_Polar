package sidev.app.cli.gadgetbridge_polar.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.AboutActivity;
import nodomain.freeyourgadget.gadgetbridge.activities.AppBlacklistActivity;
import nodomain.freeyourgadget.gadgetbridge.activities.ControlCenterv2;
import nodomain.freeyourgadget.gadgetbridge.activities.DbManagementActivity;
import nodomain.freeyourgadget.gadgetbridge.activities.DebugActivity;
import nodomain.freeyourgadget.gadgetbridge.activities.SettingsActivity;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;
import nodomain.freeyourgadget.gadgetbridge.util.GB;
import sidev.app.cli.gadgetbridge_polar.R;
import sidev.app.cli.gadgetbridge_polar.fragment.ListAllDbFrag;
import sidev.lib.android.siframe.tool.util.fun._ActFragFunKt;

public class MainAct extends ControlCenterv2 {
///*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ViewGroup vg= findViewById(R.id.drawer_layout);
//        vg.removeAllViews();
//        vg.addView(getLayoutInflater().inflate(R.layout.activity_main, vg, false));

        TextView toAllDbTv= findViewById(R.id.toAllDbTv);
        toAllDbTv.setOnClickListener(v -> _ActFragFunKt.startSingleFragAct_config(this, ListAllDbFrag.class));
//        toAllDbTv.setOnClickListener(v -> _ActFragFunKt.startAct(this, SingleFragAct_.class));
//        toAllDbTv.setOnClickListener(v -> _ActFragFunKt.toast(this, "Test toast"));
    }
// */

    @Override
    protected int getLayoutId() { return R.layout.activity_main; }

    @NotNull
    @Override
    protected List<GBDevice> getDeviceList() {
        return super.getDeviceList(); //getDummyDevice(1000);
    }

    //TODO: Dummy
    public List<GBDevice> getDummyDevice(int count){
        List<GBDevice> list= new ArrayList();
        String address= "address";
        String name= "name";
        String alias= "alias";
        DeviceType[] type= DeviceType.values();
        int limit= Math.min(count, type.length);
        int i;
        for(i= 0; i < limit; i++){
            DeviceType typeItr= type[i];
            name= getString(typeItr.getName());
            list.add(new GBDevice(address +i, name, name +" _" +i, type[i]));
        }
        i++;
        list.add(new GBDevice(address +i, name +i, alias +i, DeviceType.fromKey(109)));
        return list;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        DrawerLayout drawer = findViewById(nodomain.freeyourgadget.gadgetbridge.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        //Modified switch -> if-else karena perubahan dari app ke library
        int itemId = item.getItemId();
        if (itemId == nodomain.freeyourgadget.gadgetbridge.R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivityForResult(settingsIntent, MENU_REFRESH_CODE);
            return true;
        } else if (itemId == nodomain.freeyourgadget.gadgetbridge.R.id.action_debug) {
            Intent debugIntent = new Intent(this, DebugActivity.class);
            startActivity(debugIntent);
            return true;
        } else if (itemId == nodomain.freeyourgadget.gadgetbridge.R.id.action_db_management) {
            // Ubah ke activity kustom
            Intent dbIntent = new Intent(this, DbManagementAct.class);
            startActivity(dbIntent);
            return true;
        } else if (itemId == nodomain.freeyourgadget.gadgetbridge.R.id.action_blacklist) {
            Intent blIntent = new Intent(this, AppBlacklistActivity.class);
            startActivity(blIntent);
            return true;
        } else if (itemId == nodomain.freeyourgadget.gadgetbridge.R.id.device_action_discover) {
            launchDiscoveryActivity();
            return true;
        } else if (itemId == nodomain.freeyourgadget.gadgetbridge.R.id.action_quit) {
            GBApplication.quit();
            return true;
        } else if (itemId == nodomain.freeyourgadget.gadgetbridge.R.id.donation_link) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://liberapay.com/Gadgetbridge")); //TODO: centralize if ever used somewhere else
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            return true;
        } else if (itemId == nodomain.freeyourgadget.gadgetbridge.R.id.external_changelog) {
/*
            ChangeLog cl = createChangeLog();
            try {
                cl.getLogDialog().show();
            } catch (Exception ignored) {
                GB.toast(getBaseContext(), "Error showing Changelog", Toast.LENGTH_LONG, GB.ERROR);
            }
            return true;
 */
            return super.onNavigationItemSelected(item);
        } else if (itemId == nodomain.freeyourgadget.gadgetbridge.R.id.about) {
            Intent aboutIntent = new Intent(this, AboutActivity.class);
            startActivity(aboutIntent);
            return true;
        }

        return true;
    }

}