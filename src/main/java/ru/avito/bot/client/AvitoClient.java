package ru.avito.bot.client;

import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.net.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import ru.avito.bot.model.AvitoItem;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

public class AvitoClient {
    private static AvitoClient instance;
    private AvitoClient() {}

    public static synchronized AvitoClient getInstance() {
        if (instance == null) {
            instance = new AvitoClient();
        }
        return instance;
    }
    public final static String AVITO_URL = "https://www.avito.ru";
    public final HttpClient client = HttpClient.newBuilder().build();

    public List<AvitoItem> getAvitoItems(String... searchArguments) {
        return parseHtmlToAvitoItems(find(searchArguments));
    }
    private String find(String... searchArguments) {
        var query = String.join("+", searchArguments);
        try {
            var uri = new URIBuilder(AVITO_URL + "/moskva")
                    .addParameter("q", query)
                    .addParameter("s", "104")
                    .build();
            var request = HttpRequest.newBuilder(uri)
                    .header("accept", "*/*")
                    .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpStatus.SC_TOO_MANY_REQUESTS) {
                //TODO: do it later
            }
            return response.body();
        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<AvitoItem> parseHtmlToAvitoItems(String html) {
        var rootElements = Jsoup.parse(html).select("[class*=iva-item-body]");
        return rootElements.stream()
                .map(this::mapToAvitoItem)
                .collect(Collectors.toList());
    }

    private AvitoItem mapToAvitoItem(Element element) {
        var name = element.select("[itemprop=name]").first().childNode(0).toString();
        var price = element.select("[itemprop=price]").first().attributes().get("content");
        var currency = element.select("[itemprop=priceCurrency]").first().attributes().get("content");
        var dateInfo = element.select("[data-marker=item-date]").first().childNode(0).toString();
        var href = AVITO_URL + element.getElementsByAttribute("href").attr("href");
        return AvitoItem.builder()
                .name(name)
                .price(price)
                .currency(currency)
                .dateInfo(dateInfo)
                .href(href)
                .build();
    }


}
