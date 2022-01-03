package ggsbot.model.data;

import javax.persistence.*;

@Entity
@Table(name = "POINT")
public class Point {

    @Id
    @Column(name = "id")
    private long id;

    @Column(name="lat")
    private double lat;

    @Column(name="lon")
    private double lon;

    @Column(name="point_name")
    private String name;

    @Column(name = "point_index")
    private String index;

    @Column(name="point_class")
    private String pointClass;

    @Column(name="mark")
    private String mark;

    @Column(name="center_type")
    private String centerType;

    @Column(name="sign_type")
    private String signType;

    @Column(name = "subject")
    private String subject;

    @Column(name = "municipal")
    private String municipal;

    @Column(name="zone")
    private Integer zone;

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getZone() {
        return zone;
    }

    public void setZone(Integer zone) {
        this.zone = zone;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getPointClass() {
        return pointClass;
    }

    public void setPointClass(String pointClass) {
        this.pointClass = pointClass;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getCenterType() {
        return centerType;
    }

    public void setCenterType(String centerType) {
        this.centerType = centerType;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMunicipal() {
        return municipal;
    }

    public void setMunicipal(String municipal) {
        this.municipal = municipal;
    }
}
