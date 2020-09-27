package nodomain.freeyourgadget.gadgetbridge.entities;

import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import de.greenrobot.dao.DaoException;

import de.greenrobot.dao.AbstractDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

/**
 * This class represents the summary of a user's activity event. I.e. a walk, hike, a bicycle tour, etc.
 */
public class BaseActivitySummary implements nodomain.freeyourgadget.gadgetbridge.model.ActivitySummary {

    private Long id;
    private String name;
    /** Not-null value. */
    private java.util.Date startTime;
    /** Not-null value. */
    private java.util.Date endTime;
    private int activityKind;
    private Integer baseLongitude;
    private Integer baseLatitude;
    private Integer baseAltitude;
    private String gpxTrack;
    private long deviceId;
    private long userId;
    private String summaryData;
    private byte[] rawSummaryData;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient BaseActivitySummaryDao myDao;

    private Device device;
    private Long device__resolvedKey;

    private User user;
    private Long user__resolvedKey;


    public BaseActivitySummary() {
    }

    public BaseActivitySummary(Long id) {
        this.id = id;
    }

    public BaseActivitySummary(Long id, String name, java.util.Date startTime, java.util.Date endTime, int activityKind, Integer baseLongitude, Integer baseLatitude, Integer baseAltitude, String gpxTrack, long deviceId, long userId, String summaryData, byte[] rawSummaryData) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.activityKind = activityKind;
        this.baseLongitude = baseLongitude;
        this.baseLatitude = baseLatitude;
        this.baseAltitude = baseAltitude;
        this.gpxTrack = gpxTrack;
        this.deviceId = deviceId;
        this.userId = userId;
        this.summaryData = summaryData;
        this.rawSummaryData = rawSummaryData;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getBaseActivitySummaryDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /** Not-null value. */
    @Override
    public java.util.Date getStartTime() {
        return startTime;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setStartTime(java.util.Date startTime) {
        this.startTime = startTime;
    }

    /** Not-null value. */
    @Override
    public java.util.Date getEndTime() {
        return endTime;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setEndTime(java.util.Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public int getActivityKind() {
        return activityKind;
    }

    public void setActivityKind(int activityKind) {
        this.activityKind = activityKind;
    }

    /**
     * Temporary, bip-specific
     */
    public Integer getBaseLongitude() {
        return baseLongitude;
    }

    /**
     * Temporary, bip-specific
     */
    public void setBaseLongitude(Integer baseLongitude) {
        this.baseLongitude = baseLongitude;
    }

    /**
     * Temporary, bip-specific
     */
    public Integer getBaseLatitude() {
        return baseLatitude;
    }

    /**
     * Temporary, bip-specific
     */
    public void setBaseLatitude(Integer baseLatitude) {
        this.baseLatitude = baseLatitude;
    }

    /**
     * Temporary, bip-specific
     */
    public Integer getBaseAltitude() {
        return baseAltitude;
    }

    /**
     * Temporary, bip-specific
     */
    public void setBaseAltitude(Integer baseAltitude) {
        this.baseAltitude = baseAltitude;
    }

    @Override
    public String getGpxTrack() {
        return gpxTrack;
    }

    public void setGpxTrack(String gpxTrack) {
        this.gpxTrack = gpxTrack;
    }

    @Override
    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getSummaryData() {
        return summaryData;
    }

    public void setSummaryData(String summaryData) {
        this.summaryData = summaryData;
    }

    public byte[] getRawSummaryData() {
        return rawSummaryData;
    }

    public void setRawSummaryData(byte[] rawSummaryData) {
        this.rawSummaryData = rawSummaryData;
    }

    /** To-one relationship, resolved on first access. */
    public Device getDevice() {
        long __key = this.deviceId;
        if (device__resolvedKey == null || !device__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DeviceDao targetDao = daoSession.getDeviceDao();
            Device deviceNew = targetDao.load(__key);
            synchronized (this) {
                device = deviceNew;
            	device__resolvedKey = __key;
            }
        }
        return device;
    }

    public void setDevice(Device device) {
        if (device == null) {
            throw new DaoException("To-one property 'deviceId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.device = device;
            deviceId = device.getId();
            device__resolvedKey = deviceId;
        }
    }

    /** To-one relationship, resolved on first access. */
    public User getUser() {
        long __key = this.userId;
        if (user__resolvedKey == null || !user__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            User userNew = targetDao.load(__key);
            synchronized (this) {
                user = userNew;
            	user__resolvedKey = __key;
            }
        }
        return user;
    }

    public void setUser(User user) {
        if (user == null) {
            throw new DaoException("To-one property 'userId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.user = user;
            userId = user.getId();
            user__resolvedKey = userId;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
