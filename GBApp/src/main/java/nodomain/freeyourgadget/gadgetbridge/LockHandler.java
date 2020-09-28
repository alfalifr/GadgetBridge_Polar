/*  Copyright (C) 2016-2020 Andreas Shimokawa, Carsten Pfeiffer, Taavi Eomäe

    This file is part of Gadgetbridge.

    Gadgetbridge is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Gadgetbridge is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package nodomain.freeyourgadget.gadgetbridge;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import com.androidcommunications.polar.api.ble.model.gatt.client.BlePMDClient;

import nodomain.freeyourgadget.gadgetbridge.database.DBHandler;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoMaster;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.util.db.PolarEcgDataHandler;
import nodomain.freeyourgadget.gadgetbridge.util.db.PolarHrDataHandler;
import polar.com.sdk.api.model.PolarEcgData;
import sidev.lib.android.siframe.tool.util.fun._LogFunKt;
import sidev.lib.annotation.Modified;

/**
 * Provides low-level access to the database.
 */
public class LockHandler implements DBHandler {

    private DaoMaster daoMaster = null;
    private DaoSession session = null;
    private SQLiteOpenHelper helper = null;
    private Context context;

    @Modified(arg = "param added: c")
    public LockHandler(@NonNull Context c) {
        context= c;
    }

    @Modified(arg = "body")
    public void init(DaoMaster daoMaster, DaoMaster.OpenHelper helper) {
        if (isValid()) {
            throw new IllegalStateException("DB must be closed before initializing it again");
        }
        if (daoMaster == null) {
            throw new IllegalArgumentException("daoMaster must not be null");
        }
        if (helper == null) {
            throw new IllegalArgumentException("helper must not be null");
        }
        this.daoMaster = daoMaster;
        this.helper = helper;

        session = daoMaster.newSession();
        if (session == null) {
            throw new RuntimeException("Unable to create database session");
        }

        //added, create table saat init.
        new PolarEcgDataHandler(context).createTable();
        new PolarHrDataHandler(context).createTable();
//        _LogFunKt.loge(this, "PolarEcgData.class.getDeclaredFields().length= " +PolarEcgData.class.getDeclaredFields().length);
    }

    @Override
    public DaoMaster getDaoMaster() {
        return daoMaster;
    }

    private boolean isValid() {
        return daoMaster != null;
    }

    private void ensureValid() {
        if (!isValid()) {
            throw new IllegalStateException("LockHandler is not in a valid state");
        }
    }

    @Override
    public void close() {
        ensureValid();
        GBApplication.releaseDB();
    }

    @Override
    public synchronized void openDb() {
        if (session != null) {
            throw new IllegalStateException("session must be null");
        }
        // this will create completely new db instances and in turn update this handler through #init()
        GBApplication.app().setupDatabase();
    }

    @Override
    public synchronized void closeDb() {
        if (session == null) {
            throw new IllegalStateException("session must not be null");
        }
        session.clear();
        session.getDatabase().close();
        session = null;
        helper = null;
        daoMaster = null;
    }

    @Override
    public SQLiteOpenHelper getHelper() {
        ensureValid();
        return helper;
    }

    @Override
    public DaoSession getDaoSession() {
        ensureValid();
        return session;
    }

    @Override
    public SQLiteDatabase getDatabase() {
        ensureValid();
        return daoMaster.getDatabase();
    }
}
