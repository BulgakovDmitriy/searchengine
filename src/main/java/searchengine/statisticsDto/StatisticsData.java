package searchengine.statisticsDto;

import lombok.Value;

import java.util.List;

@Value
public class StatisticsData {
    private TotalStatistics total;
    private List<DetailedStatisticsItem> detailed;
}
