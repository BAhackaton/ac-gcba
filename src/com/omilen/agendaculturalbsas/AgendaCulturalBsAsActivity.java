package com.omilen.agendaculturalbsas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitlePageIndicator.IndicatorStyle;

public class AgendaCulturalBsAsActivity extends SherlockFragmentActivity implements ViewPager.OnPageChangeListener {

	public static final String TAG = "Agenda Cultural";

	private ViewPager mPager;
	private TitlePageIndicator mIndicator;
	private FragmentStatePagerAdapter mAdapter;
	private MenuItem searchItem;
	private MenuItem refreshItem;
	private MenuItem favoritesItem;
	private MenuItem shareItem;
	private int pageSelected = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// This has to be called before setContentView and you must use the
		// class in com.actionbarsherlock.view and NOT android.view
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mAdapter = new MyPagerAdapter(getSupportFragmentManager());

		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);
		mIndicator.setFooterIndicatorStyle(IndicatorStyle.Triangle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		searchItem = menu.add(R.string.search).setIcon(R.drawable.ic_search).setActionView(R.layout.collapsible_edittext);
		searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT
				| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

		refreshItem = menu.add(R.string.refresh).setIcon(R.drawable.ic_refresh);
		refreshItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		favoritesItem = menu.add(R.string.favorites).setIcon(android.R.drawable.btn_star_big_off);
		favoritesItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		shareItem = menu.add(R.string.share).setIcon(R.drawable.ic_title_share);
		shareItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		ShareActionProvider actionProvider = new ShareActionProvider(this);
		shareItem.setActionProvider(actionProvider);
		// ShareActionProvider actionProvider = (ShareActionProvider) shareItem.getActionProvider();
		actionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
		actionProvider.setShareIntent(createShareIntent());

		return true;
	}

	/**
	 * Creates a sharing {@link Intent}.
	 * 
	 * @return The sharing intent.
	 */
	private Intent createShareIntent() {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, "Compartir Evento Agenda Cultural Buenos Aires");;
		return shareIntent;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// This uses the imported MenuItem from ActionBarSherlock
		Toast.makeText(this, "Got click: " + item.toString(), Toast.LENGTH_SHORT).show();
		if (item.equals(refreshItem)) {
			((RefreshListener) mAdapter.getItem(pageSelected)).onRefresh();
		}
		return true;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageSelected(int position) {
		pageSelected = position;
	}
}