package com.hybrid.probuk.selfiePro;

import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.hybrid.probuk.global.GlobalMembers;

public class SettingsActivity extends PreferenceActivity {
	/**
	 * Determines whether to always show the simplified settings UI, where
	 * settings are presented in a single list. When false, settings are shown
	 * as a master/detail two-pane view on tablets. When true, a single pane is
	 * shown on tablets.
	 */
	private static final boolean ALWAYS_SIMPLE_PREFS = false;

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		setupSimplePreferencesScreen();
	}

	/**
	 * Shows the simplified settings UI if the device configuration if the
	 * device configuration dictates that a simplified, single-pane UI should be
	 * shown.
	 */
	@SuppressWarnings("deprecation")
	private void setupSimplePreferencesScreen() {
		if (!isSimplePreferences(this)) {
			return;
		}
		//		PreferenceCategory fakeHeader = new PreferenceCategory(this);
		//		fakeHeader.setTitle(R.string.pref_header_general);
		//		getPreferenceScreen().addPreference(fakeHeader);
		addPreferencesFromResource(R.xml.pref_general);

		bindPreferenceSummaryToValue(findPreference("screen_flash_key"));
		bindPreferenceSummaryToValue(findPreference("screen_flash_time"));

	}

	/** {@inheritDoc} */
	@Override
	public boolean onIsMultiPane() {
		return isXLargeTablet(this) && !isSimplePreferences(this);
	}

	/**
	 * Helper method to determine if the device has an extra-large screen. For
	 * example, 10" tablets are extra-large.
	 */
	private static boolean isXLargeTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	/**
	 * Determines whether the simplified settings UI should be shown. This is
	 * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
	 * doesn't have newer APIs like {@link PreferenceFragment}, or the device
	 * doesn't have an extra-large screen. In these cases, a single-pane
	 * "simplified" settings UI should be shown.
	 */
	private static boolean isSimplePreferences(Context context) {
		return ALWAYS_SIMPLE_PREFS
				|| Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
				|| !isXLargeTablet(context);
	}

	/** {@inheritDoc} */
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<Header> target) {
		if (!isSimplePreferences(this)) {
			loadHeadersFromResource(R.xml.pref_headers, target);
		}
	}

	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			String stringValue = value.toString();

			if (preference instanceof ListPreference) {
				Log.d("SETTINGS","List Preference");
				ListPreference listPreference = (ListPreference) preference;
				if(listPreference.getKey().equalsIgnoreCase("screen_flash_key")){
					int index = listPreference.findIndexOfValue(stringValue);
					Log.d("SETTINGS","List Preference"+index);
					if(index==1){
						GlobalMembers.GLOBAL_FULL_FLASH=true;
					}
					else if(index==0){
						GlobalMembers.GLOBAL_FULL_FLASH=false;
					}
					preference.setSummary(index >= 0 ? listPreference.getEntries()[index]: null);
				}
				else if(listPreference.getKey().equalsIgnoreCase("screen_flash_time")){
					int index = listPreference.findIndexOfValue(stringValue);
					Log.d("SETTINGS","List Preference"+index);
					switch(index){
					case 0:{
						GlobalMembers.GLOBAL_WAIT_VALUE=2000;
						return true;
					}
					case 1:{
						GlobalMembers.GLOBAL_WAIT_VALUE=1500;
						return true;
					}
					case 2:{
						GlobalMembers.GLOBAL_WAIT_VALUE=1000;
						return true;
					}
					case 3:{
						GlobalMembers.GLOBAL_WAIT_VALUE=750;
						return true;
					}
					case 4:{
						GlobalMembers.GLOBAL_WAIT_VALUE=500;
						return true;
					}
					}
					preference.setSummary(stringValue);
				}
			} else {
				// For all other preferences, set the summary to the value's
				// simple string representation.
				preference.setSummary(stringValue);
			}
			return true;
		}
	};

	/**
	 * Binds a preference's summary to its value. More specifically, when the
	 * preference's value is changed, its summary (line of text below the
	 * preference title) is updated to reflect the value. The summary is also
	 * immediately updated upon calling this method. The exact display format is
	 * dependent on the type of preference.
	 *
	 * @see #sBindPreferenceSummaryToValueListener
	 */
	private static void bindPreferenceSummaryToValue(Preference preference) {
		// Set the listener to watch for value changes.
		preference
		.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		// Trigger the listener immediately with the preference's
		// current value.
		sBindPreferenceSummaryToValueListener.onPreferenceChange(
				preference,	PreferenceManager.getDefaultSharedPreferences(
						preference.getContext()).getString(preference.getKey(),
								"")
				);
	}
}
