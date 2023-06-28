package searchengine.statisticsDto;


import lombok.Value;

@Value
public class StatisticsIndex {
    Long pageID;
    Long lemmaID;
    Float rank;
}
