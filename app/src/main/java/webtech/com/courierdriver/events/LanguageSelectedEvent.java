package webtech.com.courierdriver.events;

/**
 * Created by Abdulrahim on 10/1/2017.
 */

public class LanguageSelectedEvent {
    private int language;

    public LanguageSelectedEvent(int language) {
        this.language = language;
    }

    public int getLanguage() {
        return language;
    }
}
