package mx.com.cubozoft.movies;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

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

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private ArrayAdapter<String> mAdapterPeliculas;
    private GridView gv;
    private String more = "MAS!!";
    private int currentP;
    private String currentT;

    public MainActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        currentP = 1;
        callFetchMovieTask();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        mAdapterPeliculas = new ArrayAdapter<String>(getContext(),R.layout.item_gredview,R.id.item_gv, new ArrayList<String>());

        gv =(GridView)rootView.findViewById(R.id.gridView);
        gv.setAdapter(mAdapterPeliculas);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mAdapterPeliculas.getItem(position).equals(more))
                {
                    //o sea es el ultimo
                    callFetchMovieTask();
                }
                else
                {
                    Toast.makeText(getContext(), mAdapterPeliculas.getItem(position).toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    public void callFetchMovieTask()
    {

        SharedPreferences shpref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String criterio = shpref.getString(getString(R.string.pref_list_key),getString(R.string.pref_list_defval));

        new FetchMovieInfoTask().execute(criterio,Integer.toString(currentP));
    }


    public class FetchMovieInfoTask extends AsyncTask<String,Void,Lista> {
        private final String LOG_TAG = FetchMovieInfoTask.class.getSimpleName();

        @Override
        protected void onPostExecute(Lista ls) {
            if(ls.getLista() == null){return;}
            if(ls.getNoPagina() == 1)
            {
                mAdapterPeliculas.clear();
            }
            else
            {
                mAdapterPeliculas.remove(more);
            }

            mAdapterPeliculas.addAll(ls.listaFinal());
            currentP ++;
//            mAdapterPeliculas.notifyDataSetChanged();
        }

        @Override
        protected Lista doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            String[] peliculas = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String MOVIE_BASE_URL =
                        "https://api.themoviedb.org/3/discover/movie?";
                final String KEYPARAM = "api_key";
                final String SORT_PARAM = "sort_by";
                final String KEYVALUE = "cf83e327e32d1fdf81596961d098f640";
                final String SORT_VAL = params[0];
                final String PAGECAPTION = "page";
                String PAGEVAL = "";

                try{
                    PAGEVAL = params[1];
                }
                catch(Exception ex)
                {
                    PAGEVAL = "1";
                }


                Uri builtUri = Uri.parse(MOVIE_BASE_URL);

                Uri.Builder builder = builtUri.buildUpon()
                        .appendQueryParameter(KEYPARAM, KEYVALUE)
                        .appendQueryParameter(SORT_PARAM, SORT_VAL)
                        .appendQueryParameter(PAGECAPTION,PAGEVAL);

                builtUri = builder.build();

                URL url = new URL(builtUri.toString());




                // Create the request to OpenWeatherMap, and open the connection
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
                moviesJsonStr = buffer.toString();
                peliculas = getDataFromJson(moviesJsonStr);

                return new Lista(Integer.parseInt(PAGEVAL), new ArrayList<String>(Arrays.asList(peliculas)));

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.

                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
        }


        private String[] getDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "results";
            final String OWM_TITLE = "original_title";
         

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray resultArray = movieJson.getJSONArray(OWM_LIST);

            String[] resultStrs = new String[resultArray.length()];
            for(int i = 0; i < resultArray.length(); i++) {

                // Get the JSON object representing the day
                JSONObject peliculaobj = resultArray.getJSONObject(i);


                resultStrs[i] = peliculaobj.getString(OWM_TITLE);
            }


            return resultStrs;

        }
    }
}
