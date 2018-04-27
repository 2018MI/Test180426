package org.chengpx.a1senseshow.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * create at 2018/4/27 14:35 by chengpx
 */
@DatabaseTable(tableName = "sense")
public class SenseBean {

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(columnName = "SenseName")
    private String SenseName;
    @DatabaseField(columnName = "val")
    private Integer val;
    @DatabaseField(columnName = "startRange")
    private Integer startRange;
    @DatabaseField(columnName = "endRange")
    private Integer endRange;
    @DatabaseField(columnName = "insertDate")
    private Date insertDate;
    @DatabaseField(columnName = "desc")
    private String desc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSenseName() {
        return SenseName;
    }

    public void setSenseName(String senseName) {
        this.SenseName = senseName;
    }

    public Integer getVal() {
        return val;
    }

    public void setVal(Integer val) {
        this.val = val;
    }

    public Integer getStartRange() {
        return startRange;
    }

    public void setStartRange(Integer startRange) {
        this.startRange = startRange;
    }

    public Integer getEndRange() {
        return endRange;
    }

    public void setEndRange(Integer endRange) {
        this.endRange = endRange;
    }

    public Date getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Date insertDate) {
        this.insertDate = insertDate;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "SenseBean{" +
                "id=" + id +
                ", SenseName='" + SenseName + '\'' +
                ", val=" + val +
                ", startRange=" + startRange +
                ", endRange=" + endRange +
                ", insertDate=" + insertDate +
                ", desc='" + desc + '\'' +
                '}';
    }

}
