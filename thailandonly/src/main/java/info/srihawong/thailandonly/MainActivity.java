package info.srihawong.thailandonly;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.google.android.gms.ads.*;


import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;



public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    public static Tracker gaTracker;

    public static InterstitialAd interstitialAds;
    public AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        /*******************************************************/
        //Create the interstitial Ads.
        interstitialAds = new InterstitialAd(this);
        interstitialAds.setAdUnitId(Config.adsInterstitialUnitIDFullPage);
        interstitialAds.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                //super.onAdLoaded();
                Log.d("tui","interstitial Ads Loaded");
            }

            @Override
            public void onAdClosed() {
                Log.d("tui","interstitial Ads Close");
                interstitialAds.loadAd(adRequest);
                //super.onAdClosed();
            }
        });

        // Create ad request.
        adRequest = new AdRequest.Builder()
                .addKeyword(Config.adsInterstitialKeyword)
                .build();

        // Begin loading your interstitial.
        interstitialAds.loadAd(adRequest);
        /*******************************************************/

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<String>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        getResources().getStringArray(R.array.section)),
                this);
        gaTracker = GoogleAnalytics.getInstance(this).getTracker(getResources().getString(R.string.ga_trackingId));
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getSupportActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getSupportActionBar().getSelectedNavigationIndex());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_about) {
            Intent aboutIntent = new Intent(getApplicationContext(),AboutUs.class);
            startActivity(aboutIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // When the given dropdown item is selected, show its contents in the
        // container view.
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
        return true;
    }

    @Override
    public void onBackPressed() {
        if(interstitialAds.isLoaded()){
            interstitialAds.show();
        }
        super.onBackPressed();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements OnRefreshListener{
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        private PullToRefreshLayout mPullToRefreshLayout;
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        PlusAPI plusAPI;
        ArrayList<ListItem> listItems;
        ListAdapter itemListAdapter;
        ListView listView;
        AdView adView;
        Boolean loadMore = false;
        String hashTag = "";
        ProgressBar progressBar;
        private int page = 1;
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            plusAPI = new PlusAPI();
            listView = (ListView) rootView.findViewById(R.id.listView);
            progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
            adView = (AdView) rootView.findViewById(R.id.adView);
            listItems = new ArrayList<ListItem>();
            hashTag = getResources().getStringArray(R.array.section)[getArguments().getInt(ARG_SECTION_NUMBER)-1].replace(" ","").toLowerCase();

            /*********** Ads request top ******************/
            AdRequest adRequest = new AdRequest.Builder()
                    .addKeyword(Config.adsInterstitialKeyword)
                    .build();

            adView.loadAd(adRequest);
            /**********************************************/

            return rootView;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            ViewGroup viewGroup = (ViewGroup) view;
            mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());
            ActionBarPullToRefresh.from(getActivity())
                    .insertLayoutInto(viewGroup)
                    .theseChildrenArePullable( R.id.listView, android.R.id.empty)
                    .listener(this)
                    .setup(mPullToRefreshLayout);
            getHashTag(false);
            gaTracker.send(MapBuilder.createEvent("ActionBar","Change",hashTag,null).build());
            itemListAdapter = new ListAdapter(getActivity().getBaseContext(),listItems);
            listView.setAdapter(itemListAdapter);

            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }
                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    final int lastItem = firstVisibleItem + visibleItemCount;
                    if(lastItem == totalItemCount && !loadMore) {
                        //load more data
                        getHashTag(true);
                        gaTracker.send(MapBuilder.createEvent("listView","nextPage",hashTag,null).build());
                    }
                }
            });
        }

        @Override
        public void onRefreshStarted(View view) {
            getHashTag(false);
            gaTracker.send(MapBuilder.createEvent("listView","refresh",hashTag,null).build());
        }

        public void getHashTag(Boolean nextPage){
            AQuery aq = new AQuery(getActivity());
            String url;
            loadMore = true;
            long cacheTime = PlusAPI.CACHE_TIME;
            if(nextPage) {
                url = plusAPI.getUrlNextPage(hashTag);
                page++;
            }else{
                url = plusAPI.getUrl(hashTag);
                page=1;
            }
            if(mPullToRefreshLayout.isRefreshing()){
                cacheTime = -1;
            }
            if(page==Config.adsInterstitialPageShow){
                if(interstitialAds.isLoaded()){
                    interstitialAds.show();
                }
            }
            aq.ajax(url, JSONObject.class,cacheTime,new AjaxCallback<JSONObject>(){
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.d("tui",url);
                    Log.d("tui","Http Code:"+String.valueOf(status.getCode()));

                    if(json!=null){
                        try {
                            plusAPI.setObject(json);
                            //Clear item list when refresh
                            if(mPullToRefreshLayout.isRefreshing()){
                                listItems.clear();
                            }
                            for(int i=0,j=plusAPI.items.length();i<j;i++) {
                                if(plusAPI.isImage(i)) {
                                    ListItem listItem = new ListItem(plusAPI.getTitle(i), plusAPI.getItemImage(i).get(0), plusAPI.getUseImage(i));
                                    listItems.add(listItem);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("tui",e.getMessage());
                        }
                        itemListAdapter.notifyDataSetChanged();
                    }else{
                        //Toast.makeText(getActivity(), "Connection Error:" + String.valueOf(status.getCode()), Toast.LENGTH_LONG);
                        Log.d("tui", status.getError().toString());
                    }
                    if(mPullToRefreshLayout.isRefreshing()){
                        mPullToRefreshLayout.setRefreshComplete();
                    }
                    loadMore = false;
                }
            });

        }

        @Override
        public void onStart() {
            super.onStart();
            EasyTracker.getInstance(getActivity().getApplicationContext()).activityStart(getActivity());
        }

        @Override
        public void onStop() {
            super.onStop();
            EasyTracker.getInstance(getActivity().getApplicationContext()).activityStop(getActivity());
        }
    }

}
