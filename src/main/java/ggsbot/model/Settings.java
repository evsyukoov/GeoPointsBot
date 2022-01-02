package ggsbot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SETTINGS")
public class Settings {

    @Id
    @Column(name="id")
    private long id;

    @Column(name="radius")
    private int radius;

    @Column(name="class")
    private int pointClass;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getPointClass() {
        return pointClass;
    }

    public void setPointClass(int pointClass) {
        this.pointClass = pointClass;
    }
}
