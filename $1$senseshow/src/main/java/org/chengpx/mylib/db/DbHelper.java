package org.chengpx.mylib.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * create at 2018/4/27 12:47 by chengpx
 */
public class DbHelper extends OrmLiteSqliteOpenHelper {

    private final static String DB_NAME = "app.db";

    private static DbHelper sDbHelper;
    private static Context sContext;

    private String mTag = "org.chengpx.mylib.db.DbHelper";

    private DbHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
    }

    public static OrmLiteSqliteOpenHelper getInstance(Context context) {
        if (sDbHelper == null) {
            synchronized (DbHelper.class) {
                if (sDbHelper == null) {
                    sContext = context;
                    sDbHelper = new DbHelper(context, DB_NAME, null, 1);
                }
            }
        }
        return sDbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        Log.d(mTag, DB_NAME + " 开始创建");
        Properties props = new Properties();
        try {
            props.load(sContext.getAssets().open("tables.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Object key : props.keySet()) {
            String className = (String) props.get(key);
            try {
                if (TableUtils.createTable(connectionSource, Class.forName(className)) > 0) {
                    Log.d(mTag, className + " 映射表创建");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        Log.d(mTag, DB_NAME + " 完成创建");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        Log.d(mTag, DB_NAME + " 更新: " + oldVersion + " > " + newVersion);
    }

}
