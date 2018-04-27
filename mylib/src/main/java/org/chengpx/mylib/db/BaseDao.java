package org.chengpx.mylib.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;

/**
 * create at 2018/4/27 14:08 by chengpx
 */
public abstract class BaseDao<T> {

    protected Dao mDao;

    public BaseDao(Context context) {
        Class<? extends BaseDao> aClass = getClass();// 得到运行时 class
        Type genericSuperclass = aClass.getGenericSuperclass();
        if (!(genericSuperclass instanceof ParameterizedType)) {
            return;
        }
        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        if (actualTypeArguments.length == 0) {
            return;
        }
        Type actualTypeArgument = actualTypeArguments[0];
        if (!(actualTypeArgument instanceof Class)) {
            return;
        }
        Class actualClassArgument = (Class) actualTypeArgument;
        OrmLiteSqliteOpenHelper ormLiteSqliteOpenHelper = DbHelper.getInstance(context);
        try {
            mDao = ormLiteSqliteOpenHelper.getDao(actualClassArgument);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void close() {
        mDao = null;
    }

}
