package com.werkncode;

public class ChuckNorris {

    public static String getRandomJoke() { return getRandomJoke(null, null, null, null); }
    public static String getRandomJoke(String[] limit, String[] exclude) { return getRandomJoke(limit, exclude, null, null); }
    public static String getRandomJoke(String firstName, String lastName) { return getRandomJoke(null, null, firstName, lastName); }
    public static String getRandomJoke(String[] limit, String[] exclude, String firstName, String lastName ) {
        return API.getChuckJoke(API.buildJokeRequest(limit, exclude, firstName, lastName), API.METHOD_TYPE_RANDOM)[0];
    }

    public static String getJokeById(int id) {
        return API.getChuckJoke(API.buildJokeByIdRequest(id), API.METHOD_TYPE_RANDOM)[0];
    }

    public static String[] getJokeCategories() {
        return API.getChuckJoke(API.buildJokeCategoriesRequest(), API.METHOD_TYPE_CAT);
    }

    public static int getJokeCount() {
        return Integer.parseInt(API.getChuckJoke(API.buildJokeCountRequest(), API.METHOD_TYPE_COUNT)[0]);
    }

}
