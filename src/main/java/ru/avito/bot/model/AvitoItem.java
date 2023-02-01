package ru.avito.bot.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "href")
public class AvitoItem {
    private String name;
    private String price;
    private String currency;
    private String dateInfo;
    private String href;

    @Override
    public String toString() {
        return  "Наименование: " + name + "\n" +
                "цена : " + price + " " + currency + "\n" +
                "Дата публикации: " + dateInfo + "\n" +
                "Ссылка на товар: " + href + "\n";
    }
}
