package com.rnknown.a500pxgallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruslan on 15.07.2016.
 */
public class PhotoFetchr {

    private static final String TAG = "PhotoFetchr";

    private static final String API_KEY = "JpCyh4K53B1Hz26bN5p2BNpSPVj3yX3pIe7AjEFP";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with "
                        + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem> fetchItems(Integer pageValue) {

        List<GalleryItem> items = new ArrayList<>();

        try {
            String url = Uri.parse("https://api.500px.com/v1/photos")
                    .buildUpon()
                    .appendQueryParameter("feature", "fresh")
                    .appendQueryParameter("page", Integer.toString(pageValue))
                    .appendQueryParameter("consumer_key", API_KEY)
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
        } catch (JSONException joe) {
            Log.e(TAG, "Failed to fetch items: " + joe);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items: " + ioe);
        }
        return items;
    }

    public void parseItems(List<GalleryItem> items, JSONObject jsonBody)
            throws IOException, JSONException {

        JSONArray photoJsonArray = jsonBody.getJSONArray("photos");

        for (int i = 0; i < photoJsonArray.length(); i++) {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);

            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getString("id"));
            item.setCaption(photoJsonObject.getString("name"));
            item.setUrl(photoJsonObject.getString("image_url"));
            items.add(item);
        }
    }
}
