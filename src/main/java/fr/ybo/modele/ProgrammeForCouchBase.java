package fr.ybo.modele;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.ybo.xmltv.Programme;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgrammeForCouchBase implements Serializable {

    private String start;
    private String stop;
    private String channel;
    private String date;
    private String icon;
    private String title;
    private String subTitle;
    private String desc;
    private List<String> categories;
    private Map<String, String> ratings;
    private String episodeNum;
    private String starRating;
    private List<String> directors;
    private List<String> actors;
    private List<String> writers;
    private List<String> presenters;

    @JsonProperty("id")
    public String getId() {
        return start + channel;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String description) {
        this.desc = description;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public Map<String, String> getRatings() {
        return ratings;
    }

    public void setRatings(Map<String, String> ratings) {
        this.ratings = ratings;
    }

    public String getEpisodeNum() {
        return episodeNum;
    }

    public void setEpisodeNum(String episodeNum) {
        this.episodeNum = episodeNum;
    }

    public String getStarRating() {
        return starRating;
    }

    public void setStarRating(String starRating) {
        this.starRating = starRating;
    }

    public List<String> getDirectors() {
        if (directors == null) {
            directors = new ArrayList<String>();
        }
        return directors;
    }

    public List<String> getActors() {
        if (actors == null) {
            actors = new ArrayList<String>();
        }
        return actors;
    }

    public List<String> getWriters() {
        if (writers == null) {
            writers = new ArrayList<String>();
        }
        return writers;
    }

    public List<String> getPresenters() {
        if (presenters == null) {
            presenters = new ArrayList<String>();
        }
        return presenters;
    }

    public static ProgrammeForCouchBase fromProgramme(Programme programme) {
        ProgrammeForCouchBase programmeForMemCache = new ProgrammeForCouchBase();
        programmeForMemCache.setCategories(new ArrayList<String>(programme.getCategories()));
        programmeForMemCache.setChannel(programme.getChannel());
        programmeForMemCache.setDate(programme.getDate());
        programmeForMemCache.setDesc(programme.getOneDesc());
        programmeForMemCache.setEpisodeNum(programme.getOneEpisodeNum());
        programmeForMemCache.setIcon(programme.getOneIcon());
        programmeForMemCache.setRatings(new HashMap<String, String>(programme.getRatings()));
        programmeForMemCache.setStarRating(programme.getOneStarRating());
        programmeForMemCache.setStart(programme.getStart());
        programmeForMemCache.setStop(programme.getStop());
        programmeForMemCache.setSubTitle(programme.getOneSubTitle());
        programmeForMemCache.setTitle(programme.getOneTitle());
        if (programme.getCredits() != null) {
            programmeForMemCache.getDirectors().addAll(programme.getCredits().getDirectors());
            programmeForMemCache.getActors().addAll(programme.getCredits().getActors());
            programmeForMemCache.getWriters().addAll(programme.getCredits().getWriters());
            programmeForMemCache.getPresenters().addAll(programme.getCredits().getPresenters());
        }
        return programmeForMemCache;
    }
}
