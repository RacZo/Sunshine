package com.oscarsalguero.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 * Created by RacZo on 10/30/16.
 */
public class ForecastFragment extends Fragment {

    private static final String LOG_TAG = ForecastFragment.class.getSimpleName();

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
                new ArrayList<String>()
        );

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        FetchWeatherTask fetchWeatherTask = new FetchWeatherTask(getActivity(), mForecastArrayAdapter);
        mLocation = PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        fetchWeatherTask.execute(mLocation);
    }

}