package com.joedephillipo.naturenews;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewsFragment extends Fragment {

    //ArrayList used to hold article object
    final ArrayList<Article> articles = new ArrayList<Article>();

    //Declares the array adapter used
    ArrayAdapter<String> mNewsAdapter;

    //Empty constructor
    public NewsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    //Creates the view used
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Instructions for the user when opening the app
        String[] newsArray = {
                "Enter a topic into the search box and press the button!"
        };

        //Makes the instructions list compatible
        List<String> newsList = new ArrayList<String>(
                Arrays.asList(newsArray)
        );

        //Creates the adapter
        mNewsAdapter =
                new ArrayAdapter<String>(getActivity(),
                        R.layout.list_item_news,
                        R.id.list_item_news_textview,
                        newsList
                );

        //Creates the view used
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Sets the adapter
        ListView listView = (ListView) rootView.findViewById(
                R.id.listview_news);
        listView.setAdapter(mNewsAdapter);

        //Creates an intent to open the articles url when any list item is clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article article = articles.get(position);
                String url = article.getArticleUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        return rootView;
    }

    //The data will be displayed as soon as the app starts
    @Override
    public void onStart(){
        super.onStart();
        getNewsData newsData = new getNewsData();
        newsData.execute();
    }

    public class getNewsData extends AsyncTask<Void, Void, String[]> {

        //Method to get and parse the JSON data
        private String[] getNewsDataFromJson(String newsJsonStr) throws JSONException {
            final String NEWS_RESPONSE = "response";
            final String NEWS_RESULTS = "results";
            final String NEWS_TITLE = "webTitle";
            final String NEWS_URL = "webUrl";

            //Creates a main JSON object and array
            JSONObject newsJsonInfo = new JSONObject(newsJsonStr);

            JSONObject newsResponse = newsJsonInfo.getJSONObject(NEWS_RESPONSE);
            JSONArray newsArray = newsResponse.getJSONArray(NEWS_RESULTS);


            //Creates a String array list for results the same length as the JSON array
            String[] resultStrs = new String[newsArray.length()];

            //Loops through the main JSON array
            for(int i = 0; i < newsArray.length(); i++){

                //Gets the articles title and url in their own strings
                JSONObject newsJsonUrl = newsArray.getJSONObject(i);
                String newsUrl = newsJsonUrl.getString(NEWS_URL);
                String newsTitle = newsJsonUrl.getString(NEWS_TITLE);

                //Stores the information
                articles.add(new Article(newsTitle, newsUrl));

                //Finally we have the list ready string ready to be put into the list.
                resultStrs[i] = newsTitle;
            }

            //Returns the whole array list of strings.*/
            return resultStrs;
        }

        @Override
        protected String[] doInBackground(Void... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String newsJsonStr = null;

            try {

                //Set the url as a URL
                URL url = new URL("http://content.guardianapis.com/search?section=environment&q=news&api-key=efe334c4-13c6-42d3-aa7b-0239f0e66740");

                //Try to get a connection to the created url
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
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
                newsJsonStr = buffer.toString();
            } catch (IOException e) {
                return null;

                //Clean up
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                    }
                }
            }

            try {
                return getNewsDataFromJson(newsJsonStr);
            } catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result){

            //Data goes into the adapter!!
            if (result != null){
                mNewsAdapter.clear();
                for (String bookInfoStr : result){
                    mNewsAdapter.add(bookInfoStr);
                }
            }
        }
    }
}