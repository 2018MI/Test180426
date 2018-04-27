package org.chengpx.a1senseshow.dao;

import android.content.Context;

import org.chengpx.a1senseshow.domain.RoadBean;
import org.chengpx.mylib.db.BaseDao;

/**
 * create at 2018/4/27 14:45 by chengpx
 */
public class RoadDao extends BaseDao<RoadBean> {

    private static RoadDao sRoadDao;

    private RoadDao(Context context) {
        super(context);
    }

    public static RoadDao getInstance(Context context) {
        if (sRoadDao == null) {
            synchronized (RoadDao.class) {
                if (sRoadDao == null) {
                    sRoadDao = new RoadDao(context);
                }
            }
        }
        return sRoadDao;
    }

}
