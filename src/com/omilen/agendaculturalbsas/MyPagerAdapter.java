package com.omilen.agendaculturalbsas;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.viewpagerindicator.TitleProvider;

/**
 * The <code>PagerAdapter</code> serves the fragments when paging. Smooth Horizontal View Slider Original tutorial from:
 * http://thepseudocoder.wordpress.com/2011/10/05/android -page-swiping-using-viewpager
 */

public class MyPagerAdapter extends FragmentStatePagerAdapter implements TitleProvider {

	private static final String[] TABS = new String[] { "Teatro", "Cine", "Danza", "Literatura", "Festivales" };

	public MyPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		return TestFragment.newInstance(TABS[position % TABS.length], position);
	}

	@Override
	public int getCount() {
		return TABS.length;
	}

	@Override
	public String getTitle(int position) {
		return TABS[position % TABS.length].toUpperCase();
	}
}
