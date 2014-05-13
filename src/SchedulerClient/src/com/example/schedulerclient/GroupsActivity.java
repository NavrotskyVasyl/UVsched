package com.example.schedulerclient;

import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GroupsActivity extends ActionBarActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	private static long userId;
	private static String userPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groups);

		// Get the message from the intent
	    Intent intent = getIntent();
	    userPassword = intent.getStringExtra(LoginActivity.SAVED_USER_PASS);
	    userId = intent.getLongExtra(LoginActivity.SAVED_USER_ID, -1);
		
		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

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
		getMenuInflater().inflate(R.menu.groups, menu);
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
		if (id == R.id.action_create_group) {
			CreateNewGroup();
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
			// Show 4 total pages.
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.groups_section_info).toUpperCase(l);
			case 1:
				return getString(R.string.groups_section_all).toUpperCase(l);
			case 2:
				return getString(R.string.groups_section_favorite).toUpperCase(l);
			case 3:
				return getString(R.string.groups_section_rescent).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements ClientMsgsSender.ServerAnswerReceiver, View.OnClickListener {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";
		
		ClientMsgsSender sender;
		ProgressBar progress;
		LinearLayout list;

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
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			int secArg = getArguments().getInt(ARG_SECTION_NUMBER);
			
			if(secArg > 1) {
				View rootView = inflater.inflate(R.layout.fragment_groups,
						container, false);
				
				progress = (ProgressBar) rootView.findViewById(R.id.progressBarGroups);
				list = (LinearLayout) rootView.findViewById(R.id.groupsLinLayout);
				progress.setVisibility(View.VISIBLE);
				
				sender = ServerInfo.CreateStdMsgsSender(inflater.getContext());//new ClientMsgsSender("localhost", 8080);
				sender.SendMessage(CommandConstructor.getAllGroupsListCommand(userId, userPassword), this, null);
				
				return rootView;
			}
			
			View rootView = inflater.inflate(R.layout.fragment_groups_info,
					container, false);
			return rootView;
		}

		private void AddGroupView(String groupName, Boolean visibleToAll, int index, long groupId) {
			LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f);
			LayoutParams LLParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
			
			LinearLayout LL = new LinearLayout(getView().getContext());
			if(index % 2 == 0) {
				LL.setBackgroundColor(Color.parseColor("#E5D7ED"));
				LL.setTag(Integer.valueOf(Color.parseColor("#E5D7ED")));
			}
		    LL.setOrientation(LinearLayout.HORIZONTAL);
		    LL.setWeightSum(6f);
		    LL.setLayoutParams(LLParams);
		    
		    
		    ImageView icon = new ImageView(getView().getContext());
		    if(visibleToAll)
		    	icon.setImageResource(R.drawable.ic_visible);
		    else
		    	icon.setImageResource(R.drawable.ic_hiden);
		    FrameLayout.LayoutParams iconParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
		    icon.setLayoutParams(iconParams);
			
			TextView listText = new TextView(getView().getContext());
	    	listText.setText(groupName);
            listText.setTag(groupName);
            listText.setLayoutParams(param);
            listText.setTypeface(Typeface.SANS_SERIF);
            listText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
            
            LL.addView(icon);
            LL.addView(listText);
            LL.setFocusable(true);
            LL.setId((int) groupId);
            
            // TODO Fix touch highlighting
            LL.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if(hasFocus)
						v.setBackgroundColor(Color.BLUE);
					else
						v.setBackgroundColor(Color.WHITE); // ((Integer)v.getTag()).intValue()
				}
			});
            
            LL.setOnClickListener(this);
            
            list.addView(LL);
		}
		
		@Override
		public void ProcessServerAnswer(String msgString, String answerString,
				String serverAdress, int serverPort, Bundle params) {
			progress.setVisibility(View.GONE);
			list.removeAllViews();
			
			try {
				JSONObject object = (JSONObject) new JSONTokener(answerString).nextValue();
				JSONObject answ = object.getJSONObject("answer");
				JSONArray groups = answ.getJSONArray("list");
				
			    for(int i=0; i<groups.length(); ++i) {
			    	JSONObject grObj = groups.getJSONObject(i);
			    	String grName 		 = grObj.getString("name");
			    	Boolean grVisibility = grObj.getBoolean("visibleToAll");
			    	long id 			 = grObj.getLong("groupId");
			    	
			    	AddGroupView(grName, grVisibility, i, id);
			    }
			    
			    //getParentFragment().setContentView(list);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onClick(View v) {
			int groupIndex = v.getId();
			// TODO Intent here
		}
	}
		
	private void CreateNewGroup() {
		// intent groups activity
		Intent intent = new Intent(this, CreateGroupActivity.class);
		intent.putExtra(LoginActivity.SAVED_USER_ID,  	userId);
		intent.putExtra(LoginActivity.SAVED_USER_PASS, 	userPassword);
		startActivity(intent);
	}

}
