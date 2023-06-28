package searchengine.statisticsDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsSearch implements Comparable<StatisticsSearch> {

    private String site;

    private String siteName;

    private String uri;

    private String title;

    private String snippet;

    private Float relevance;

    @Override
    public int compareTo(StatisticsSearch o) {
        if (relevance > o.getRelevance()) {
            return -1;
        }
        return 1;
    }

}
