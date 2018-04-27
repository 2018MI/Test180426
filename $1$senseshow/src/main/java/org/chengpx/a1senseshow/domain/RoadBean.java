package org.chengpx.a1senseshow.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * create at 2018/4/27 8:23 by chengpx
 */
@DatabaseTable(tableName = "road")
public class RoadBean {

    @DatabaseField(generatedId = true)
    private Integer RoadId;
    @DatabaseField(columnName = "Status")
    private Integer Status;
    @DatabaseField(columnName = "insertDate")
    private Date insertDate;
    @DatabaseField(columnName = "desc")
    private String desc;

    public Integer getRoadId() {
        return RoadId;
    }

    public void setRoadId(Integer roadId) {
        RoadId = roadId;
    }

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
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
        return "RoadBean{" +
                "RoadId=" + RoadId +
                ", Status=" + Status +
                ", insertDate=" + insertDate +
                ", desc='" + desc + '\'' +
                '}';
    }

}
