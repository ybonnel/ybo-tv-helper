package fr.ybo.modele;

import net.sf.json.JSONObject;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ChannelForNoSql implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ChannelForNoSql.class);

    @Id
    @ObjectId
    private String key;
    private String id;
    private String displayName;
    private String icon;
    private Integer numero;

    public String getKey() {
        return key;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIcon() {
        return icon;
    }

    public Integer getNumero() {
        return numero;
    }

    public ChannelForNoSql() {
    }


    private static String logoDir = "/Users/ybonnel/sources/ybo-tv/ybo-tv-server/src/main/resources/public/logos";

    public ChannelForNoSql(JSONObject json, Map<String, Integer> numeros) {
        this.id = Integer.toString(json.getInt("id"));
        this.displayName = json.getString("nom");
        this.icon = "_" + this.id + ".png";

//        try {
//
//            if (!new File(logoDir, icon).exists()) {
//                File tmpFile = new File("/tmp", icon);
//                try (AsyncHttpClient client = new DefaultAsyncHttpClient();
//                     FileOutputStream out = new FileOutputStream(tmpFile)) {
//                    byte[] image = client.prepareGet(json.getString("logo"))
//                            .execute().get().getResponseBodyAsBytes();
//                    out.write(image);
//                }
//                logger.info("Execute : convert " + tmpFile.getAbsolutePath() + " -resize 132x132 -crop 132x99+0+16 +repage " + new File(logoDir, icon).getAbsolutePath());
//                Process process = Runtime.getRuntime().exec("convert " + tmpFile.getAbsolutePath() + " -resize 132x132 -crop 132x99+0+16 +repage " + new File(logoDir, icon).getAbsolutePath());
//                int result = process.waitFor();
//                if (result != 0) {
//                    logger.warn("Code retour : {} (channel : {})", result, displayName);
//                }
//                try (BufferedReader sout = new BufferedReader(new InputStreamReader(process.getInputStream()));
//                     BufferedReader serr = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
//                    sout.lines().forEach(logger::info);
//                    serr.lines().forEach(logger::warn);
//                }
//            }
//        } catch (Exception exception) {
//            throw new RuntimeException(exception);
//        }

        numero = numeros.getOrDefault(id, 999);
    }


    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ChannelForNoSql{");
        sb.append("key='").append(key).append('\'');
        sb.append(", id='").append(id).append('\'');
        sb.append(", displayName='").append(displayName).append('\'');
        sb.append(", icon='").append(icon).append('\'');
        sb.append(", numero=").append(numero);
        sb.append('}');
        return sb.toString();
    }


}
