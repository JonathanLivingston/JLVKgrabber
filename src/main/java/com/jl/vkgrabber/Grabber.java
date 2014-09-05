package com.jl.vkgrabber;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.jl.vkgrabber.util.Configuration;

/**
 * Created by JonathanLivingston on 18.05.2014 16:34.
 */
public class Grabber {

    public static void main(String args[]) throws Exception {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("https").setHost("api.vk.com").setPath("/method/audio.get").setParameter("oid",
                Configuration.getProperty(Configuration.USER_ID)).setParameter("need_user", "0").setParameter("count",
                "2000").setParameter("offset", "0").setParameter("access_token",
                Configuration.getProperty(Configuration.TOKEN));
        URI uri = builder.build();

        HttpGet getRequest = new HttpGet(uri);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpResponse response = httpClient.execute(getRequest);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (InputStream stream = entity.getContent()) {
                    try {
                        parseAndDownload(IOUtils.toString(stream));
                    } catch (IOException | ParseException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
    }

    private static void parseAndDownload(String response) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonResponse = (JSONObject) parser.parse(response);
        JSONArray mp3list = (JSONArray) jsonResponse.get("response");
        for (Object jsonObject : mp3list) {
            try {
                JSONObject mp3 = (JSONObject) jsonObject;
                String path = (Configuration.getProperty(Configuration.OUTPUT_FOLDER) + File.separator + mp3.get("artist")
                        + " - " + mp3.get("title")).replaceAll("\"", "").replaceAll("?", "").replaceAll("/", "").replaceAll("\\", "");
                File file = new File(path + ".mp3");
                if (!file.exists()) {
                    FileUtils.copyURLToFile(new URL((String) mp3.get("url")), file);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
