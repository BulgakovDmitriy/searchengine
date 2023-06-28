package searchengine.parsers;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import org.jsoup.select.Elements;
import searchengine.statisticsDto.StatisticsPage;



@Slf4j
public class SiteParser extends RecursiveTask<List<StatisticsPage>> {
    private final String address;
    private final List<String> addressList;
    private final List<StatisticsPage> statisticsPageList;

    public SiteParser(String address, List<StatisticsPage> statisticsPageList, List<String> addressList) {
        this.address = address;
        this.statisticsPageList = statisticsPageList;
        this.addressList = addressList;
    }

    public Document getConnect(String url) {
        Document document = null;
        try {
            Thread.sleep(150);
            document = Jsoup.connect(url).userAgent(UserAgent.getUserAgent()).get();
//            .referrer("http://www.google.com").get(); - не работает с некоторыми сайтами !
        } catch (Exception e) {
            log.debug("Can't get connected to the site" + url);
        }
        return document;
    }

    @Override
    protected List<StatisticsPage> compute() {
        try {
            Thread.sleep(150);
            Document document = getConnect(address);
            String html = document.outerHtml();
            Connection.Response response = document.connection().response();
            int statusCode = response.statusCode();
            StatisticsPage statisticsPage = new StatisticsPage(address, html, statusCode);
            statisticsPageList.add(statisticsPage);
            Elements elements = document.select("a");
            List<SiteParser> taskList = new ArrayList<>();
            for (Element el : elements) {
                String link = el.absUrl("href");
                if (link.startsWith(el.baseUri()) && !link.equals(el.baseUri()) && !link.contains("#") && !isFile(link) && !addressList.contains(link))
                {
                    addressList.add(link);
                    SiteParser task = new SiteParser(link, statisticsPageList, addressList);
                    task.fork();
                    taskList.add(task);
                }
            }
            taskList.forEach(ForkJoinTask::join);
        } catch (Exception e) {
            log.debug("Parsing error - " + address);
            StatisticsPage statisticsPage = new StatisticsPage(address, "", 500);
            statisticsPageList.add(statisticsPage);
        }
        return statisticsPageList;
    }

    private static boolean isFile(String link) {
        link.toLowerCase();
        return link.contains(".jpg")
                || link.contains(".jpeg")
                || link.contains(".png")
                || link.contains(".gif")
                || link.contains(".webp")
                || link.contains(".pdf")
                || link.contains(".eps")
                || link.contains(".xlsx")
                || link.contains(".doc")
                || link.contains(".pptx")
                || link.contains(".docx")
                || link.contains("?_ga")
                || link.contains(".svg");
    }


}