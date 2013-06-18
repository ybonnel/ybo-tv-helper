package fr.ybo;


import com.couchbase.client.CouchbaseClient;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import fr.ybo.modele.ChannelForCouchBase;
import fr.ybo.modele.ProgrammeForCouchBase;
import fr.ybo.modele.TvForCouchBase;
import fr.ybo.xmltv.Channel;
import fr.ybo.xmltv.Programme;
import fr.ybo.xmltv.Tv;
import net.spy.memcached.PersistTo;
import net.spy.memcached.internal.CheckedOperationTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Lists.newArrayList;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private final static Set<String> idsFromKaezer = new HashSet<String>() {{
        add("BEI1.kazer.org");
        add("BEI2.kazer.org");
        add("NOL1.kazer.org");
    }};

    private final static Map<String, String> mapIds = new HashMap<String, String>() {{
        put("C20", "13R1");
        put("C4133", "6TER");
        put("C27", "MOT1");
        put("C23", "AB11");
        put("C24", "AB31");
        put("C25", "AB41");
        put("C26", "ACT1");
        put("C29", "ANI1");
        put("C7", "ART1");
        put("BEI1", "BEI1");
        put("BEI2", "BEI2");
        put("C15", "BFM1");
        put("C38", "BOO1");
        put("C56", "CIN20");
        put("C58", "CIN21");
        put("C60", "CIN18");
        put("C61", "CIN22");
        put("C62", "CIN23");
        put("C65", "CIN28");
        put("C42", "CAN1");
        put("C4", "CAN2");
        put("C43", "CAN3");
        put("C44", "CAN4");
        put("C45", "CAN10");
        put("C47", "CAN5");
        put("C50", "CAR1");
        put("C4136", "CHE1");
        put("C53", "CIN1");
        put("C54", "CIN2");
        put("C68", "CO11");
        put("C8", "DIR1");
        put("C17", "EUR2");
        put("C71", "DIS1");
        put("C73", "DIS3");
        put("C75", "DIS5");
        put("C80", "EEN1");
        put("C89", "EUR3");
        put("C83", "EQU1");
        put("C84", "ESC1");
        put("C85", "ESP1");
        put("C2", "FRA2");
        put("C3", "FRA3");
        put("C13", "FRA4");
        put("C5", "FRA5");
        put("C119", "FRA1");
        put("C121", "GAM1");
        put("C18", "GUL1");
        put("C4131", "HD1");
        put("C16", "ITL1");
        put("C126", "JIM1");
        put("C4132", "LEQ1");
        put("C130", "LAD1");
        put("C131", "LAU1");
        put("C14", "LAC1");
        put("C6", "M61");
        put("C139", "M6M2");
        put("C141", "MAC1");
        put("C142", "MAN1");
        put("C144", "MCM1");
        put("C148", "MEZ1");
        put("C149", "MOT2");
        put("C150", "MTV1");
        put("C168", "NAT1");
        put("C171", "NIC1");
        put("NOL1", "NOL1");
        put("C12", "NRJ1");
        put("C11", "NT11");
        put("C4134", "NU23");
        put("C177", "OMT1");
        put("C186", "PAR1");
        put("C187", "PIN1");
        put("C188", "PLA1");
        put("C190", "PLA5");
        put("C191", "PLA2");
        put("C192", "PLA3");
        put("C4135", "RMC2");
        put("C199", "RTL2");
        put("C201", "SCI1");
        put("C202", "SEA1");
        put("C204", "SPO1");
        put("C206", "TCM1");
        put("C220", "TLT1");
        put("C227", "TVA1");
        put("C1", "TF11");
        put("C228", "TF61");
        put("C229", "TIJ1");
        put("C10", "TMC1");
        put("C235", "TSR1");
        put("C236", "TSR2");
        put("C243", "TVB1");
        put("C237", "TV51");
        put("C246", "USH1");
        put("C247", "VIV1");
        put("C299", "TOU1");
        put("C9", "W91");
        put("C90", "EUR4"); // Eurosport 2
        put("C122", "HIS1"); // histoire
        put("C294", "IDF1"); // IDF1
        put("C203", "SCL1"); // Série club
        put("C146", "MCM2"); // MCM Pop
        put("C147", "MCM3"); // MCM Top
    }};

    public static void main(String[] args) throws IOException, JAXBException, InterruptedException, ExecutionException, URISyntaxException {

        String xmlFile = args[0];


        logger.info("ybo-tv-helper begin");


        Tv tv = getTvFromXml(xmlFile);

        Map<String, Integer> compteurs = new HashMap<String, Integer>();

        logger.info("begin mapping ids");

        for (Programme programme : tv.getProgramme()) {
            programme.setChannel(Splitter.on('.').split(programme.getChannel()).iterator().next());
            if (mapIds.containsKey(programme.getChannel())) {
                programme.setChannel(mapIds.get(programme.getChannel()));
            }
            if (!compteurs.containsKey(programme.getChannel())) {
                compteurs.put(programme.getChannel(), 0);
            }
            compteurs.put(programme.getChannel(), compteurs.get(programme.getChannel()) + 1);
        }

        for (Channel channel : tv.getChannel()) {
            channel.setId(Splitter.on('.').split(channel.getId()).iterator().next());
            if (mapIds.containsKey(channel.getId())) {
                channel.setId(mapIds.get(channel.getId()));
            } else {
                logger.error("Chaine non connue : channel {} {}", channel.getId(), channel.getOneDisplayName());
            }
            if (!compteurs.containsKey(channel.getId())) {
                logger.error("Chaine sans programme channel {} {}", channel.getId(), channel.getOneDisplayName());
            }
        }

        logger.info("end mapping ids");

        logger.info("begin transform tv");

        TvForCouchBase tvForCouchBase = TvForCouchBase.fromTv(tv);

        logger.info("end transform tv");

        insertIntoChouchbase(tvForCouchBase);
    }

    private static void insertIntoChouchbase(TvForCouchBase tv) throws URISyntaxException, IOException, ExecutionException, InterruptedException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        CouchbaseClient client = new CouchbaseClient(newArrayList(new URI("http://127.0.0.1:8091/pools")), "default", "");

        try {


            // Insertion des chaines
            logger.info("Insert channels");
            client.set("channels", (int) TimeUnit.DAYS.toSeconds(3), mapper.writeValueAsString(tv.getChannel()), PersistTo.ZERO).get();

            logger.info("Insert programs");
            for (ProgrammeForCouchBase programme : tv.getProgramme()) {
                int retry = 0;
                while (retry < 5) {
                    try {
                        client.set("prg_" + programme.getId(), (int) TimeUnit.DAYS.toSeconds(3), mapper.writeValueAsString(programme), PersistTo.ZERO).get();
                        retry = Integer.MAX_VALUE;
                    } catch (Exception exception) {
                        retry++;
                        logger.warn("Exception détectée, essai({}) : {}", retry, exception.getMessage());
                    }
                }
            }
        } finally {
            client.shutdown(30, TimeUnit.SECONDS);
        }
    }

    private static Tv getTvFromXml(String xmlFile) throws JAXBException, IOException {
        JAXBContext jc = JAXBContext.newInstance("fr.ybo.xmltv");
        Unmarshaller um = jc.createUnmarshaller();

        logger.info("Beging getZip");

        Tv tv = (Tv) um.unmarshal(new File(xmlFile));
        logger.info("End getZip");

        logger.info("Begin get from kaezer");

        Tv tvFromKaezer = (Tv) um.unmarshal(GetZip.getFile());



        for (Channel channel : tvFromKaezer.getChannel()) {
            if (idsFromKaezer.contains(channel.getId())) {
                tv.getChannel().add(channel);
            }
        }

        for (Programme programme : tvFromKaezer.getProgramme()) {
            if (idsFromKaezer.contains(programme.getChannel())) {
                tv.getProgramme().add(programme);
            }
        }

        logger.info("End get from kaezer");

        return tv;
    }
}
