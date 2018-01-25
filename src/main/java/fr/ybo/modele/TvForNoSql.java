package fr.ybo.modele;

import net.sf.json.JSONObject;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TvForNoSql implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(TvForNoSql.class);

    private List<ChannelForNoSql> channel;


    private List<ProgrammeForNoSql> programme;

    public List<ChannelForNoSql> getChannel() {
        if (channel == null) {
            channel = new ArrayList<>();
        }
        return this.channel;
    }

    public List<ProgrammeForNoSql> getProgramme() {
        if (programme == null) {
            programme = new ArrayList<>();
        }
        return programme;
    }

    private static Set<Integer> channelToIgnore = new HashSet<>(Arrays.asList(
            2320,
            1923,
            1922,
            1921,
            1928,
            1927,
            1926,
            1925,
            1924,
            1932,
            1931,
            1929,
            1936,
            1935,
            1934,
            1933,
            1941,
            1940,
            1939,
            1938,
            1937,
            2014,
            2006,
            1944,
            1943,
            1942,
            604
    ));

    private static AtomicInteger numero = new AtomicInteger(1);

    private static Map<String, Integer> numeros = Stream.of(
            192,
            4,
            80,
            34,
            47,
            118,
            111,
            445,
            119,
            195,
            446,
            444,
            234,
            78,
            481,
            226,
            458,
            482,
            160,
            1404,
            1401,
            1403,
            1402,
            1400,
            1399,
            112,
            2111,
            191,
            205,
            145,
            115,
            225,
            33,
            35,
            1563,
            657,
            30,
            1290,
            1304,
            1335,
            730,
            733,
            732,
            734,
            185,
            1562,
            10,
            282,
            284,
            283,
            401,
            285,
            287,
            833,
            1190,
            1960,
            5,
            121,
            87,
            54,
            2,
            479,
            49,
            128,
            403,
            58,
            299,
            483,
            321,
            924,
            229,
            32,
            2040,
            344,
            197,
            293,
            300,
            79,
            36,
            888,
            473,
            1746,
            12,
            2037,
            38,
            1776,
            659,
            7,
            212,
            243,
            719,
            88,
            451,
            63,
            147,
            662,
            402,
            929,
            787,
            563,
            6,
            1585,
            325,
            605,
            453,
            343,
            241,
            753,
            265,
            262,
            125,
            907,
            835,
            64,
            1146,
            1336,
            1337,
            1338,
            1339,
            1340,
            1341,
            1342,
            15,
            237,
            1166,
            415,
            463,
            184,
            987,
            57,
            110,
            529,
            1073,
            140,
            124,
            53,
            51,
            19,
            525,
            781,
            61,
            416,
            218,
            560,
            475,
            406,
            683,
            308,
            421,
            535,
            701,
            1045,
            539,
            491,
            273,
            524,
            116,
            18,
            13,
            219,
            172,
            3,
            208,
            169,
            1948,
            156,
            154,
            155,
            340,
            449,
            1179,
            1461
    ).map(Object::toString)
            .collect(Collectors.toMap(
                    Function.identity(),
                    channel -> numero.getAndIncrement()
            ));

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

    public static String calculateRFC2104HMAC(String data, String key)
            throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        return toHexString(mac.doFinal(data.getBytes()));
    }


    public static String signature(String params) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        return calculateRFC2104HMAC(params, "Eufea9cuweuHeif");
    }


    @SuppressWarnings("unchecked")
    public static TvForNoSql fetchFromTelerama() throws IOException, ExecutionException, InterruptedException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {

        try (AsyncHttpClient client = new DefaultAsyncHttpClient()) {

            Response response = client.prepareGet("https://api.telerama.fr/v1/application/initialisation")
                    .addQueryParam("appareil", "android_tablette")
                    .addQueryParam("api_cle", "apitel-5304b49c90511")
                    .addQueryParam("api_signature", signature("/v1/application/initialisationappareilandroid_tablette"))
                    .addHeader("accept-encoding", "gzip")
                    .execute().get();

            if (response.getStatusCode() != 200) {
                throw new RuntimeException("Unable to get initialisation data, status=" + response.getStatusCode() + ", response=" + response.getResponseBody());
            }

            JSONObject jsonResponse = JSONObject.fromObject(response.getResponseBody()).getJSONObject("donnees");
            Stream<Object> streamGenres = jsonResponse.getJSONArray("genres")
                    .stream();
            Map<Integer, String> genres =
                    streamGenres.map(JSONObject::fromObject)
                    .collect(
                            Collectors.toMap(
                                    json -> json.getInt("id"),
                                    json -> json.getString("libelle")
                            )
                    );



            Stream<Object> stream = jsonResponse.getJSONArray("chaines")
                    .stream();
            TvForNoSql tv = new TvForNoSql();

            tv.channel = stream.map(JSONObject::fromObject)
                    .filter(jsonChannel -> !channelToIgnore.contains(jsonChannel.getInt("id")))
                    .map((JSONObject json) -> new ChannelForNoSql(json, numeros))
                    .collect(Collectors.toList());

            Set<String> channelsToRemove = new HashSet<>();


            tv.programme = tv.channel.stream()
                    .peek(channel -> logger.info("Getting programme of {}", channel.getDisplayName()))
                    .map(channel -> {
                        List<ProgrammeForNoSql> programmes = getProgrammeForChannel(client, channel, genres);
                        if (programmes.isEmpty()) {
                            logger.warn("Channel {} - {} has no programmes", channel.getId(), channel.getDisplayName());
                            channelsToRemove.add(channel.getId());
                        }
                        return programmes;
                    })
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            tv.channel.removeIf(channel -> channelsToRemove.contains(channel.getId()));


            return tv;
        }
    }

    public static List<ProgrammeForNoSql> getProgrammeForChannel(AsyncHttpClient client, ChannelForNoSql channel, Map<Integer, String> genres)  {
        List<ProgrammeForNoSql> result = new ArrayList<>();

        try {
            LocalDate date = LocalDate.now().minusDays(1);
            int responseStatus;

            do {

                String url = "http://api.telerama.fr/v1/programmes/telechargement?dates=" + date.toString() + "&id_chaines=" + channel.getId() + "&nb_par_page=300&page=1";

                Response response = getResponse(client, channel, date, url);

                responseStatus = response.getStatusCode();

                date = date.plusDays(1);
                if (responseStatus == 200) {
                    Stream<Object> donnees = JSONObject.fromObject(response.getResponseBody())
                            .getJSONArray("donnees")
                            .stream();

                    result.addAll(donnees.map(JSONObject::fromObject)
                            .map(json -> new ProgrammeForNoSql(json, genres)).collect(Collectors.toList()));

                } else if (responseStatus != 404 && responseStatus != 503) {

                    throw new RuntimeException("Unable to get programmes data, status=" + response.getStatusCode() + ", response=" + response.getResponseBody());
                }


            } while (responseStatus == 200);

        } catch (InterruptedException | ExecutionException | NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        return result;


    }

    private static Response getResponse(AsyncHttpClient client, ChannelForNoSql channel, LocalDate date, String url) throws InterruptedException, ExecutionException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        int count = 0;
        Response response;
        do {
            if (count > 0) {
                Thread.sleep(count * 60000);
                System.out.println("Retry of (" + count + ")" + url);
            }
            response = client
                    .prepareGet(url)
                    .addQueryParam("appareil", "android_tablette")
                    .addQueryParam("api_cle", "apitel-5304b49c90511")
                    .addQueryParam("api_signature", signature("/v1/programmes/telechargementappareilandroid_tablettedates" + date.toString() + "id_chaines" + channel.getId() + "nb_par_page300page1"))
                    .addHeader("accept-encoding", "gzip")
                    .execute().get();
            
            count++;

        } while (response.getStatusCode() != 200 && response.getStatusCode() != 404 && response.getStatusCode() != 503 && count < 10);
        
        return response;
    }

}
