package fr.ybo;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import fr.ybo.modele.ChannelForNoSql;
import fr.ybo.modele.ProgrammeForNoSql;
import fr.ybo.modele.TvForNoSql;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, URISyntaxException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {


        logger.info("ybo-tv-helper begin");

        TvForNoSql tvForNoSql = TvForNoSql.fetchFromTelerama();

        logger.info("Channels {}", tvForNoSql.getChannel().size());
        logger.info("Programmes {}", tvForNoSql.getProgramme().size());

        insertIntoDatabase(tvForNoSql);
    }

    private static void insertIntoDatabase(TvForNoSql tv) throws URISyntaxException, IOException, ExecutionException, InterruptedException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        DB db = new MongoClient().getDB("ybotv");
        Jongo jongo = new Jongo(db);

        try {

            logger.info("Create jeton");


            // Insertion des chaines
            logger.info("Insert channels");
            MongoCollection collectionChannels = jongo.getCollection("channels");
            Set<String> channelIds = new HashSet<String>();
            for (ChannelForNoSql channel : tv.getChannel()) {
                ChannelForNoSql channelInDb = collectionChannels.findOne("{id : '" + channel.getId() +"'}").as(ChannelForNoSql.class);
                if (channelInDb == null) {
                    collectionChannels.save(channel);
                } else {
                    channel.setKey(channelInDb.getKey());
                    collectionChannels.update(new ObjectId(channelInDb.getKey())).merge(channel);
                }
                channelIds.add(channel.getId());
            }

            for (ChannelForNoSql channelInDb : collectionChannels.find().as(ChannelForNoSql.class)) {
                if (!channelIds.contains(channelInDb.getId())) {
                    collectionChannels.remove(new ObjectId(channelInDb.getKey()));
                }
            }

            db.getCollection("new_programmes").drop();

            MongoCollection collectionProgrammes = jongo.getCollection("new_programmes");
            collectionProgrammes.ensureIndex("{channel:1}");

            logger.info("Insert programs");
            for (ProgrammeForNoSql programme : tv.getProgramme()) {
                collectionProgrammes.insert(programme);
            }

            db.getCollection("new_programmes").rename("programmes", true);

        } finally {
            db.getMongo().close();
        }
    }
}
