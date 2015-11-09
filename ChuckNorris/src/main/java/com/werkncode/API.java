package com.werkncode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by werkn on 15-11-08.
 * Java Wrapper for ICND (Internet Chuck Norris Database) API
 */
public class API {

    static final String LOG_TAG = "API";

    static final String API_FAILURE_MESSAGE = "Something has gone terribly, terribly wrong!";

    public static final String METHOD_TYPE_COUNT = "count";
    public static final String METHOD_TYPE_CAT = "categories";
    public static final String METHOD_TYPE_RANDOM = "random";

    //JSON
    static final String INCD_TYPE = "type";
    static final String INCD_SUCCESS = "success";
    static final String INCD_VALUE = "value";
    static final String INCD_JOKE = "joke";

    //API connection
    static final String CHUCK_API_BASE_URL = "http://api.icndb.com";
    static final String JOKE_ROOT = "jokes";
    static final String RANDOM_JOKE = "random";
    static final String JOKE_CATEGORIES = "categories";
    static final String JOKE_COUNT = "count";
    static final String LIMIT_TO = "?limitTo=";
    static final String EXCLUDE = "?exclude=";
    static final String FIRST_NAME = "?firstName=";
    static final String LAST_NAME = "&amp;lastName=";

    private static String arrayToString(String[] array) {
        StringBuilder sb = new StringBuilder();
        for (String n : array) {
            if (sb.length() > 0)
                sb.append(',');
        }

        return sb.toString();
    }

    //example request:  http://api.icndb.com/jokes/random?firstName=&amp;lastName=Doe
    public static String buildJokeRequest(String[] limit, String[] exclude, String firstName, String lastName) {

        String requestUrl = CHUCK_API_BASE_URL + "/" + JOKE_ROOT + "/" + RANDOM_JOKE + "/";

        //API can only handle either a `limitTo` OR an `exclude` not both
        if (limit != null && exclude == null)
            requestUrl += LIMIT_TO + "[" + arrayToString(limit) + "]";
        else if (exclude != null && limit == null)
            requestUrl += EXCLUDE + "[" + arrayToString(exclude) + "]";
        else if (exclude != null && limit != null)
            System.err.println("Error, limit parameter and exclusion parameter cannot both be set.");

        //handle only full names, toss the any other combo, ie:  first + no last, last + no first name set
        if (firstName != null && lastName != null)
            requestUrl += FIRST_NAME + firstName + LAST_NAME + lastName;

        return requestUrl;
    }

    public static String buildJokeByIdRequest(int id) {
        return CHUCK_API_BASE_URL + "/" + JOKE_ROOT + "/" + id;
    }

    public static String buildJokeCategoriesRequest() {
        return CHUCK_API_BASE_URL + "/" + JOKE_CATEGORIES;
    }

    public static String buildJokeCountRequest() {
        return CHUCK_API_BASE_URL + "/" + JOKE_ROOT + "/" + JOKE_COUNT;
    }

    //connect to http://api.icndb.com/jokes/random, grab a random joke
    public static String[] getChuckJoke(String requestUrl, String methodType) {
        String chuckJsonStr;

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(requestUrl);

            InputStream inputStream = null;

            try {
                // Create the request to TMDB
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                inputStream = urlConnection.getInputStream();
            } catch(Exception e) {
                System.err.println(LOG_TAG + ": Failed to connect to " + url.toString());
                System.err.println(e);
            }

            if (inputStream == null) {
                // Nothing to do.
                return null;
            }

            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            chuckJsonStr = buffer.toString();

            //pass our data to JsonDescriptor which will filter each piece of data out
            return getJokeDataFromJson(chuckJsonStr, methodType);
        } catch (IOException e) {
            System.err.println(LOG_TAG + ": IOException" + e);
        } catch (JSONException e) {
            System.err.println(LOG_TAG + ": JSONException" + e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    System.err.println(LOG_TAG + ": Error closing stream.");
                }
            }
        }

        return null;
    }

    private static String[] getJokeDataFromJson(String chuckJsonStr, String methodType) {

        try {

            JSONObject jokeJson = new JSONObject(chuckJsonStr);

            //check query status, `success`, everything OK
            if (jokeJson.getString(INCD_TYPE).contains(INCD_SUCCESS)) {

                //filter by method type
                if (methodType.contains(METHOD_TYPE_CAT)) {
                    JSONArray jokeCategoryArray = jokeJson.getJSONArray(INCD_VALUE);
                    String[] categories = new String[jokeCategoryArray.length()];

                    for (int i = 0; i < categories.length; i++)
                        categories[i] = jokeCategoryArray.getString(i);

                    return categories;

                //METHOD_TYPE_COUNT & .._RANDOM are duplicate at the moment, future release will change this
                } else if (methodType.contains(METHOD_TYPE_COUNT)) {
                    return new String[] { jokeJson.getString(INCD_VALUE) };
                } else if (methodType.contains(METHOD_TYPE_RANDOM)) {
                    JSONObject jsonJokeObject = jokeJson.getJSONObject(INCD_VALUE);

                    return new String[] { jsonJokeObject.getString(INCD_JOKE) };
                } else {
                    return new String[] { API_FAILURE_MESSAGE };
                }

            }

        } catch (JSONException e) {
            System.err.println(LOG_TAG + ": JSONException" + e);
        }

        return new String[] { API_FAILURE_MESSAGE };
    }

}
