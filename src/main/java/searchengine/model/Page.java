package searchengine.model;

import lombok.Data;
import lombok.NoArgsConstructor;


import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "page", indexes = {@Index(name = "path_list", columnList = "path")})
@Data
@NoArgsConstructor
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "site_id", referencedColumnName = "id")
    private SitePage siteId;
    @Column(length = 1000, columnDefinition = "VARCHAR(515)", nullable = false)
    private String path;


    @Column (nullable = false)
    private int code;
    @Column(length = 16777215, columnDefinition = "mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci", nullable = false)
    private String content;


    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL)
    private List<IndexSearch> index = new ArrayList<>();

    public Page(SitePage siteId, String path, int code, String content) {
        this.siteId = siteId;
        this.path = path;
        this.code = code;
        this.content = content;
    }

}
