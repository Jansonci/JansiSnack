package tech.wetech.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cjbi
 */
@Configuration
public class Admin3Properties {

  private Map<String, Event> events = new HashMap<>();

  public Map<String, Event> getEvents() {
    return events;
  }

  public void setEvents(Map<String, Event> events) {
    this.events = events;
  }

  public static class Event {
    private String text;
    private String logTemplate;

    public String getText() {
      return text;
    }

    public void setText(String text) {
      this.text = text;
    }

    public String getLogTemplate() {
      return logTemplate;
    }

    public void setLogTemplate(String logTemplate) {
      this.logTemplate = logTemplate;
    }
  }

}
