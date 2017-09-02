package fr.ybo.modele;

import net.sf.json.JSONObject;
import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

public class ProgrammeForNoSql implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ProgrammeForNoSql.class);

    public ProgrammeForNoSql() {
    }

    public ProgrammeForNoSql(JSONObject json, Map<Integer, String> genres) {
        start = json.getJSONObject("horaire").getString("debut")
                .replace("-", "")
                .replace(":", "")
                .replace(" ", "");
        stop = json.getJSONObject("horaire").getString("fin")
                .replace("-", "")
                .replace(":", "")
                .replace(" ", "");
        channel = Integer.toString(json.getInt("id_chaine"));

        if (json.get("annee_realisation") != null) {
            date = json.getString("annee_realisation");
        }


        icon = Optional.ofNullable(json.optJSONObject("vignettes"))
                .map(vignettes -> vignettes.getString("petite"))
                .orElse(null);

        title = json.getString("titre");
        subTitle = json.getString("soustitre");
        desc = json.getString("resume");

        ratings = new HashMap<>();
        ratings.put("CSA", "-" + json.getString("csa"));

        episodeNum = Optional.ofNullable(json.optJSONObject("serie"))
                .map(serie -> serie.getString("saison") + "-" + serie.getString("numero_episode")).orElse(null);


        if (json.has("intervenants")) {

            Stream<Object> stream = json.getJSONArray("intervenants").stream();

            stream.map(JSONObject::fromObject)
                    .forEach(intervenant -> {
                        switch (intervenant.getString("libelle")) {
                            case "Interprète":
                            case "Acteur":
                            case "Guest star":
                            case "Invité":
                            case "Autre Invité":
                            case "Chef d'orchestre":
                            case "Soliste":
                            case "Voix Off VO":
                            case "Voix Off VF":
                            case "Invité vedette":
                            case "Compagnie":
                            case "Danseur":
                                getActors().add(intervenant.getString("prenom") + " " + intervenant.getString("nom"));
                                break;
                            case "Décors":
                            case "Image":
                            case "Musique":
                            case "":
                                break;
                            case "Présentateur":
                            case "Présentateur vedette":
                            case "Autre présentateur":
                                getPresenters().add(intervenant.getString("prenom") + " " + intervenant.getString("nom"));
                                break;
                            case "Réalisateur":
                            case "Réalisateur/Metteur en Scène":
                            case "Metteur en scène":
                            case "Chorégraphe":
                                getDirectors().add(intervenant.getString("prenom") + " " + intervenant.getString("nom"));
                                break;
                            case "Auteur" :
                            case "Scénario":
                            case "Scénariste":
                            case "Créateur":
                            case "Dialogue":
                            case "Origine Scénario":
                            case "Scénario original":
                            case "Compositeur":
                                getWriters().add(intervenant.getString("prenom") + " " + intervenant.getString("nom"));
                                break;
                            default:
                                logger.warn("Type d'intervenant inconnu : {}", intervenant.toString());
                        }
                    });

        }
        this.critique = json.getString("critique");
        this.categories = new ArrayList<>();


        String genre = genres.get(json.optInt("id_genre"));
        if (genre != null) {
            this.categories.add(genre);
        }
        String genreSpecifique = json.optString("genre_specifique", "");
        if (genreSpecifique != null && genreSpecifique.length() > 0) {
            this.categories.add(genreSpecifique);
        }

    }

    @Id
    @ObjectId
    private String id;
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
    private String critique;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
