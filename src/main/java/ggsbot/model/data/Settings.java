package ggsbot.model.data;

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

    @Column(name="classes")
    private String pointClasses;

    @Column(name="file_formats")
    private String fileFormats;

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

    public String getPointClasses() {
        return pointClasses;
    }

    public void setPointClasses(String pointClasses) {
        this.pointClasses = pointClasses;
    }

    public void setFileFormats(String fileFormats) {
        this.fileFormats = fileFormats;
    }

    public String getFileFormats() {
        return fileFormats;
    }
}
