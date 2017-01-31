package com.oscarsalguero.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 * Created by RacZo on 10/30/16.
 */
public class ForecastFragment extends Fragment {

    private static final String LOG_TAG = ForecastFragment.class.getSimpleName();

    private ListView mListView;

    private String mLocation = "";

    private List<String> weekForecast = new ArrayList<>();

    private ArrayAdapter<String> mForecastArrayAdapter;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public ForecastFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ForecastFragment newInstance(int sectionNumber) {
        ForecastFragment fragment = new ForecastFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Dummy data
        /*
        String[] fakeForecastData = new String[]
                {
                        "Saturday - Sunny - 88/63",
                        "Sunday - Cloudy - 72/42",
                        "Monday - Sunny - 88/63",
                        "Tuesday - Sunny - 81/63",
                        "Wednesday - Sunny - 83/63",
                        "Thursday - Sunny - 83/63",
                        "Friday - Showers - 74/66"
                };
        weekForecast.addAll(Arrays.asList(fakeForecastData));
        */

        mForecastArrayAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_text_view,
                weekForecast
        );

        mListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        mListView.setAdapter(mForecastArrayAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String forecast = mForecastArrayAdapter.getItem(i);
                //Toast.makeText(view.getContext(), forecast, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, forecast);
                getActivity().startActivity(intent);

            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_forecast_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                updateWeather();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateWeather() {
        /*
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            mLocation = PreferenceManager
                    .getDefaultSharedPreferences(getActivity())
                    .getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
            //new FetchWeatherTask().execute(mLocation);
        } else {
            Log.e(LOG_TAG, "No Internet Connection");
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        */
        FetchWeatherTask fetchWeatherTask = new FetchWeatherTask(getActivity());
        mLocation = PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        fetchWeatherTask.execute(mLocation);
    }

    /*
    private class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            int numDays = 7;
            String units = "metric";
            String format = "json";

            try {

                Uri.Builder uriBuilder = new Uri.Builder();
                uriBuilder.scheme("http")
                        .authority("api.openweathermap.org")
                        .appendPath("data")
                        .appendPath("2.5")
                        .appendPath("forecast")
                        .appendPath("daily")
                        .appendQueryParameter("q", params[0])
                        .appendQueryParameter("cnt", Integer.toString(numDays))
                        .appendQueryParameter("units", units)
                        .appendQueryParameter("mode", format)
                        .appendQueryParameter("appid", BuildConfig.OPEN_WEATHER_MAP_API_KEY);

                Log.v(LOG_TAG, "URL: " + uriBuilder.build().toString());

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String forecastJsonStr = null;

                try {

                    URL url = new URL(uriBuilder.toString());
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        return null;
                    }
                    forecastJsonStr = buffer.toString();

                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }

                return getWeatherDataFromJson(forecastJsonStr, numDays);

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] result) {

            if (result != null) {
                //Log.v(LOG_TAG, result.toString());
                mForecastArrayAdapter.clear();
                mForecastArrayAdapter.addAll(result);
            }

        }
    }
    */

    /**
     * The date/time conversion code is going to be moved outside the asynctask later,
     * so for convenience we're breaking it out into its own method now.
     */
    private String getReadableDateString(long time) {
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {

        String temperatureUnits = PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_temperature_units_key), getString(R.string.pref_temperature_units_metric));
        if (temperatureUnits.equalsIgnoreCase(getString(R.string.pref_temperature_units_imperial))) {
            high = (high * 1.8) + 32;
            low = (low * 1.8) + 32;
        } else if (!temperatureUnits.equalsIgnoreCase(getString(R.string.pref_temperature_units_metric))) {
            Log.d(LOG_TAG, "Temperature Unit not found: " + temperatureUnits);
        }

        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

}