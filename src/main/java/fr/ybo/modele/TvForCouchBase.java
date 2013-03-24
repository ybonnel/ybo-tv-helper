package fr.ybo.modele;

import com.google.common.collect.Lists;
import fr.ybo.xmltv.Tv;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TvForCouchBase implements Serializable {

    private List<ChannelForCouchBase> channel;


    private List<ProgrammeForCouchBase> programme;

    public List<ChannelForCouchBase> getChannel() {
        if (channel == null) {
            channel = new ArrayList<ChannelForCouchBase>();
        }
        return this.channel;
    }

    public List<ProgrammeForCouchBase> getProgramme() {
        if (programme == null) {
            programme = new ArrayList<ProgrammeForCouchBase>();
        }
        return programme;
    }

    public static TvForCouchBase fromTv(Tv tv) {
        TvForCouchBase tvForCouchBase = new TvForCouchBase();

        tvForCouchBase.channel = new ArrayList<ChannelForCouchBase>(Lists.transform(tv.getChannel(), new TransformChannels()));

        tvForCouchBase.programme = new ArrayList<ProgrammeForCouchBase>(Lists.transform(tv.getProgramme(), new TransformProgrammes()));
        return tvForCouchBase;
    }

}
