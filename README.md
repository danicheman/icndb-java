# icndb-java
Java API Wrapper for the Internet Chuck Norris Database http://www.icndb.com/api/

######Sample

```java
import com.werkncode.ChuckNorris;
import com.werkncode.API;

...

//grab a random joke
String randomJoke = ChuckNorris.getRandomJoke();
System.out.println(joke);

//total joke count for http://www.icndb.com/
int apiJokeCount = ChuckNorris.getJokeCount();
System.out.println("Joke count: " + apiJokeCount);

```