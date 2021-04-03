package io.restassured;

import com.codeborne.selenide.Condition;
import com.jayway.jsonpath.JsonPath;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.open;

public class CombiningUiAndRestTests {

    private static final String URL_KEY = "https://freesound.org/";
    private static final String API_PATH = "apiv2/search/text/";
    private static final String API_KEY = "rcBTauOX7I4qJbioXWtd04GWp1f9lLq9RFd2rXpJ";
    private static final String query = "cat";
    private static String filename;
    private static String username;
    private static String soundId;

    @BeforeEach
    public void beforeTest() throws UnirestException {
        String json = Unirest.get(URL_KEY + API_PATH)
                .queryString("token", API_KEY)
                .queryString("query", query)
                .asString()
                .getBody();

        filename = JsonPath.read(json, "$.results[0].name");
        username = JsonPath.read(json, "$.results[0].username");
        soundId = String.valueOf((Integer)JsonPath.read(json, "$.results[0].id"));
    }


    //проверка в UI видимости кнопки "play"
    @Test
    public void playButtonTest() {
        open(URL_KEY + String.format("people/%s/sounds/%s/", username, soundId));
        $("#single_sample_player .play")
                .should(Condition.visible);
    }

    //в результатах поиска присутствует заданный файл
    @Test
    public void filenameVerificationTest() {
        open(URL_KEY + "search/?q=" + query);
        $$(".sound_filename")
                .get(0)
                .should(text(filename));
    }
}
