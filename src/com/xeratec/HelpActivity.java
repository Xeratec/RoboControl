package com.xeratec;

import java.util.Locale;

import com.xeratec.robocontrol.R;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class HelpActivity extends Activity implements ActionBar.TabListener {
	private static Activity currentActivity;

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		currentActivity = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.help, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			return PlaceholderFragment.newInstance(position + 1);
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			case 3:
				return getString(R.string.title_section4).toUpperCase(l);
			}
			return null;
		}
	}

	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
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
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = null;
			int number = getArguments().getInt("section_number");
			
			rootView = inflater.inflate(R.layout.fragment_help, container,false);
			//TextView helpText = (TextView) rootView.findViewById(R.id.textHelp);
			WebView help = (WebView) rootView.findViewById(R.id.webView1);
			help.setBackgroundColor(Color.TRANSPARENT);
			
			//helpText.setMovementMethod(LinkMovementMethod.getInstance());
			
			switch (number) {
			case 1:
				//helpText.setText(Html.fromHtml(getString(R.string.help_general), new ImageGetter() ,null));
				help.loadUrl("file:///android_res/raw/help_general.html");
				break;
			case 2:
				//helpText.setText(Html.fromHtml(getString(R.string.help_settings), new ImageGetter() ,null));
				help.loadUrl("file:///android_res/raw/help_settings.html");
				break;
			case 3:
				//helpText.setText(Html.fromHtml(getString(R.string.help_control), new ImageGetter() ,null));
				help.loadUrl("file:///android_res/raw/help_control.html");
				break;
			case 4:
				//helpText.setText(Html.fromHtml(getString(R.string.help_control), new ImageGetter() ,null));
				help.loadUrl("file:///android_res/raw/help_technic.html");
				break;
			default:
				//helpText.setText(Html.fromHtml(getString(R.string.help_general), new ImageGetter() ,null));
				help.loadUrl("file:///android_res/raw/help_general.html");
				break;
			}
			return rootView;
		}
		
		private class ImageGetter implements Html.ImageGetter {

		     public Drawable getDrawable(String source) {
		    	 
		        int id;

		        id = getResources().getIdentifier(source, "drawable", currentActivity.getPackageName());

		        if (id == 0) {
		            // the drawable resource wasn't found in our package, maybe it is a stock android drawable?
		            id = getResources().getIdentifier(source, "drawable", "android");
		        }

		        if (id == 0) {
		            return null;    
		        }
		        else {
		            Drawable d = getResources().getDrawable(id);
		            d.setBounds(0,0,d.getIntrinsicWidth(),d.getIntrinsicHeight());
		            return d;
		        }
		     }

		 }
	}
}
