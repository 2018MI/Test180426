package org.chengpx.mylib.common;

import org.chengpx.mylib.AppException;

/**
 * create at 2018/4/23 17:28 by chengpx
 */
public class DataUtils {

    public static String obj2str(Object obj) {
        String str = null;
        if (obj instanceof Integer) {
            Integer i = (Integer) obj;
            str = i + "";
        } else if (obj instanceof Double) {
            Double d = (Double) obj;
            str = d + "";
        } else {
            str = (String) obj;
        }
        return str;
    }

    public static float obj2float(Object obj) throws AppException {
        if (obj instanceof String) {
            String str = (String) obj;
            if ("".equals(obj)) {
                throw new AppException("obj == \"\"");
            }
            return Float.parseFloat(str);
        } else if (obj instanceof Double) {
            double d = (double) obj;
            return (float) d;
        } else if (obj instanceof Integer) {
            return (int) obj;
        } else {
            return (float) obj;
        }
    }

    public static int obj2int(Object obj) throws AppException {
        if (obj instanceof String) {
            String str = (String) obj;
            if ("".equals(str)) {
                throw new AppException("obj == \"\"");
            }
            return Integer.parseInt(str);
        } else if (obj instanceof Double) {
            double d = (double) obj;
            return (int) d;
        } else if (obj instanceof Float) {
            float f = (float) obj;
            return (int) f;
        } else {
            return (int) obj;
        }
    }

}
