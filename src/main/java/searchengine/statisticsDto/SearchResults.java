package searchengine.statisticsDto;

import lombok.Data;


import java.util.ArrayList;
import java.util.List;


@Data
public class SearchResults {
    private boolean result = true;
    private int count = 0;
    private List<StatisticsSearch> data = new ArrayList<>();

    public SearchResults(boolean result) {
        this.result = result;
    }

    public SearchResults(boolean result, int count, List<StatisticsSearch> data) {
        this.result = result;
        this.count = count;
        this.data = data;
    }
}
