package ggsbot.model.data;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;

@Entity
@Table(name = "CLIENT")
public class Client {

    @Id
    @Column(name="id")
    private long id;

    @Column(name="state")
    private int state;

    @Column(name="count")
    private int count;

    @OneToOne(cascade = CascadeType.ALL)
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}