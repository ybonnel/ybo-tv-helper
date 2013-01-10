package fr.ybo;


import fr.ybo.xmltv.Channel;
import fr.ybo.xmltv.Programme;
import fr.ybo.xmltv.Tv;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException, JAXBException {

        String repertoireSortie = args[0];

        JAXBContext jc = JAXBContext.newInstance("fr.ybo.xmltv");
        Unmarshaller um = jc.createUnmarshaller();

        Tv tv = (Tv) um.unmarshal(GetZip.getFile());

        for (Channel channel : tv.getChannel()) {
            channel.transformId();
        }

        File fileRepSortie = new File(repertoireSortie);

        for (File oneFile : fileRepSortie.listFiles()) {
            oneFile.delete();
        }

        File fileChannels = new File(repertoireSortie, "channels.xml");

        Marshaller marshaller = jc.createMarshaller();
        Tv tvChannels = new Tv();
        tvChannels.getChannel().addAll(tv.getChannel());
        marshaller.marshal(tvChannels, fileChannels);

        Map<String, List<Programme>> mapProgrammes = new HashMap<String, List<Programme>>();
        for (Channel channel : tv.getChannel()) {
            mapProgrammes.put(channel.getId(), new ArrayList<Programme>());
        }

        Set<String> programmeIds = new HashSet<String>();

        for (Programme programme : tv.getProgramme()) {
            programme.transformChannelId();
            if (!programmeIds.contains(programme.getStart() + programme.getChannel())) {
                mapProgrammes.get(programme.getChannel()).add(programme);
                programmeIds.add(programme.getStart() + programme.getChannel());
            }
        }

        for (Map.Entry<String, List<Programme>> entry : mapProgrammes.entrySet()) {
            Tv tvForProgrammes = new Tv();
            tvForProgrammes.getProgramme().addAll(entry.getValue());
            File fileProgramme = new File(repertoireSortie, "programmes_" + entry.getKey() + ".xml");
            marshaller.marshal(tvForProgrammes, fileProgramme);
        }
    }
}
