package org.chengpx.a1senseshow.dao;

import android.content.Context;

import org.chengpx.a1senseshow.domain.SenseBean;
import org.chengpx.mylib.db.BaseDao;

import java.sql.SQLException;
import java.util.List;

/**
 * create at 2018/4/27 14:49 by chengpx
 */
public class SenseDao extends BaseDao<SenseBean> {

    private static SenseDao sSenseDao;

    private SenseDao(Context context) {
        super(context);
    }

    public static SenseDao getInstance(Context context) {
        if (sSenseDao == null) {
            synchronized (SenseDao.class) {
                if (sSenseDao == null) {
                    sSenseDao = new SenseDao(context);
                }
            }
        }
        return sSenseDao;
    }

    public List<SenseBean> select(String senseName) {
        try {
            return mDao.queryForEq("SenseName", senseName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
