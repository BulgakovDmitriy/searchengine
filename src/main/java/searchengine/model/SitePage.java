package searchengine.model;


import lombok.Data;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



@Entity
@Data
@Table(name = "site")
public class SitePage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "status_time", nullable = false)
    private Date statusTime;

    @Column(name = "last_error")
    private String lastError;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "siteId", cascade = CascadeType.ALL)
    protected List<Page> pageList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sitePageId", cascade = CascadeType.ALL)
    protected List<Lemma> lemmaEntityList = new ArrayList<>();

    @Override
    public String toString() {
        return "SitePage{" +
                "id=" + id +
                ", status=" + status +
                ", statusTime=" + statusTime +
                ", lastError='" + lastError + '\'' +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
