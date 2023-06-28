package searchengine.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



@Entity
@Data
@Table(name = "lemma", indexes = {@Index(name = "lemma_list", columnList = "lemma")})
@NoArgsConstructor
public class Lemma implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", referencedColumnName = "id", nullable = false)
    private SitePage sitePageId;

    @Column(name = "lemma", nullable = false)
    private String lemma;

    @Column(name = "frequency", nullable = false)
    private int frequency;

    @OneToMany(mappedBy = "lemma", cascade = CascadeType.ALL)
    private List<IndexSearch> index = new ArrayList<>();


    public Lemma(String lemma, int frequency, SitePage sitePageId) {
        this.lemma = lemma;
        this.frequency = frequency;
        this.sitePageId = sitePageId;
    }

    @Override
    public String toString() {
        return "Lemma{" +
                "id=" + id +
                ", sitePageId=" + sitePageId +
                ", lemma='" + lemma + '\'' +
                ", frequency=" + frequency +
                '}';
    }
}
