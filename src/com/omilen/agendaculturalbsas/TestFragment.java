package com.omilen.agendaculturalbsas;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.omilen.agendaculturalbsas.loaders.RESTLoader;
import com.omilen.agendaculturalbsas.loaders.RESTLoader.RESTResponse;
import com.omilen.agendaculturalbsas.model.Event;

public class TestFragment extends SherlockListFragment implements LoaderCallbacks<RESTLoader.RESTResponse>, RefreshListener {

	private String category;
	private int fragmentPosition = 0;
	private List<Event> events;
	private EventAdapter eventAdapter;

	// This is our REST action.
	private Uri searchUri = Uri.parse("http://agendacultural.buenosaires.gob.ar/static/rssgen.php");

	private static final int LOADER_SEARCH = 0x2;
	private static final String ARGS_URI = "com.omilen.agendaculturalbsas.loader.ARGS_URI";
	private static final String ARGS_PARAMS = "com.omilen.agendaculturalbsas.loader.ARGS_PARAMS";

	public static TestFragment newInstance(String category, int fragmentPosition) {
		TestFragment fragment = new TestFragment(category.toLowerCase(), fragmentPosition);
		// Supply category and position input as an argument.
		Bundle args = new Bundle();
		args.putString("category", category.toLowerCase());
		args.putInt("fragmentPosition", fragmentPosition);
		fragment.setArguments(args);
		return fragment;
	}

	/**
	 * When creating, retrieve this instance's number from its arguments.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			category = getArguments().getString("category").toLowerCase();
			fragmentPosition = getArguments().getInt("fragmentPosition");
		}
	}

	public TestFragment() {
		super();
	}

	public TestFragment(String category, int fragmentPosition) {
		super();
		this.category = category.toLowerCase();
		this.fragmentPosition = fragmentPosition;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) {
			// We have different layouts, and in one of them this
			// fragment's containing frame doesn't exist. The fragment
			// may still be created from its saved state, but there is
			// no reason to try to create its view hierarchy because it
			// won't be displayed. Note this is not needed -- we could
			// just run the code below, where we would create and return
			// the view hierarchy; it would just never be used.
			return null;
		}
		// These are the loader arguments. They are stored in a Bundle because
		// LoaderManager will maintain the state of our Loaders for us and
		// reload the Loader if necessary. This is the whole reason why
		// we have even bothered to implement RESTLoader.
		Bundle args = new Bundle();
		args.putParcelable(ARGS_URI, searchUri);
		Bundle params = new Bundle();
		params.putString("categoria", category.toLowerCase());
		args.putParcelable(ARGS_PARAMS, params);

		// Initialize the Loader.
		getSherlockActivity().getSupportLoaderManager().initLoader(LOADER_SEARCH + fragmentPosition, args, this);

		return (LinearLayout) inflater.inflate(R.layout.fragment_layout, container, false);
	}

	@Override
	public Loader<RESTResponse> onCreateLoader(int id, Bundle args) {
		getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);
		if (args != null && args.containsKey(ARGS_URI) && args.containsKey(ARGS_PARAMS)) {
			Uri action = args.getParcelable(ARGS_URI);
			Bundle params = args.getParcelable(ARGS_PARAMS);

			return new RESTLoader(getSherlockActivity(), RESTLoader.HTTPVerb.GET, action, params);
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<RESTResponse> loader, RESTResponse data) {
		int code = data.getCode();
		String json = data.getData();

		// Check to see if we got an HTTP 200 code and have some data.
		if (code == 200 && !json.equals("")) {

			// For really complicated JSON decoding I usually do my heavy lifting
			// Gson and proper model classes, but for now let's keep it simple
			// and use a utility method that relies on some of the built in
			// JSON utilities on Android.
			events = getEventsFromRss(json);

			// this.eventAdapter = new EventAdapter(getSherlockActivity(), R.layout.custom_row, events);
			this.eventAdapter = new EventAdapter(getSherlockActivity(), R.layout.custom_row, events);
			setListAdapter(this.eventAdapter);

			// Load our list adapter with our Tweets.
			// setListAdapter(new ArrayAdapter<String>(getSherlockActivity(), android.R.layout.simple_list_item_1, events));

		} else {
			Toast.makeText(getSherlockActivity(), "Failed to load Rss data. Check your internet settings.", Toast.LENGTH_SHORT).show();
		}
		getSherlockActivity().setSupportProgressBarIndeterminateVisibility(false);
	}

	@Override
	public void onLoaderReset(Loader<RESTResponse> arg0) {
		getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);
	}

	@Override
	public void onRefresh() {
		Bundle args = new Bundle();
		args.putParcelable(ARGS_URI, searchUri);
		Bundle params = new Bundle();
		params.putString("categoria", category.toLowerCase());
		args.putParcelable(ARGS_PARAMS, params);
		// TODO NPE getSherlockActivity
		// getSherlockActivity().getSupportLoaderManager().restartLoader(LOADER_SEARCH + fragmentPosition, args, this);
	}

	protected List<Event> getEventsFromRss(String rss) {
		ArrayList<Event> results = new ArrayList<Event>();
		RSSHandler rh = new RSSHandler(results);
		rh.createFeed(rss);
		return results;
	}

	private class EventAdapter extends ArrayAdapter<Event> {

		private List<Event> events;

		public EventAdapter(Context context, int textViewResourceId, List<Event> events) {
			super(context, textViewResourceId, events);
			this.events = events;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSherlockActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.custom_row, null);
			}
			Event event = events.get(position);
			if (event != null) {
				TextView title = (TextView) v.findViewById(R.id.title);
				if (title != null) {
					title.setText(event.getTitle());
				}
				TextView content = (TextView) v.findViewById(R.id.content);
				if (content != null) {
					content.setText(event.getContent());
				}
			}
			return v;
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Toast.makeText(getSherlockActivity(), "Got click: " + position, Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(this.events.get(position).getLink()));
		startActivity(intent);
	}

}
