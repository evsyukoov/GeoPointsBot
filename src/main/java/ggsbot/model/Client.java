package ggsbot.model;

import javax.persistence.*;

@Entity
@Table(name = "CLIENT")
public class Client {

    @Id
    @Column(name="id")
    private long id;

    @Column(name="state")
    private int state;

    @Column(name="usage")
    private int usage;

    @OneToOne
    @PrimaryKeyJoinColumn(name="id", referencedColumnName = "id")
    private Settings settings;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getUsage() {
        return usage;
    }

    public void setUsage(int usage) {
        this.usage = usage;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}
