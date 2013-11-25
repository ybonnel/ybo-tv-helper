package fr.ybo.modele;

import com.google.common.collect.Lists;
import fr.ybo.xmltv.Tv;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TvForNoSql implements Serializable {

    private List<ChannelForNoSql> channel;


    private List<ProgrammeForNoSql> programme;

    public List<ChannelForNoSql> getChannel() {
        if (channel == null) {
            channel = new ArrayList<ChannelForNoSql>();
        }
        return this.channel;
    }

    public List<ProgrammeForNoSql> getProgramme() {
        if (programme == null) {
            programme = new ArrayList<ProgrammeForNoSql>();
        }
        return programme;
    }

    public static TvForNoSql fromTv(Tv tv) {
        TvForNoSql tvForNoSql = new TvForNoSql();

        tvForNoSql.channel = new ArrayList<ChannelForNoSql>(Lists.transform(tv.getChannel(), new TransformChannels()));

        tvForNoSql.programme = new ArrayList<ProgrammeForNoSql>(Lists.transform(tv.getProgramme(), new TransformProgrammes()));
        return tvForNoSql;
    }

}
