package net.anfet.simple.support.library.preferences;

import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import java.util.Set;

public class PreferenceValueBinder {


	public static void setPreferenceSummary(final Preference preference) {
		if (preference instanceof ListPreference) {
			final ListPreference listPreference = (ListPreference) preference;
			final int index = listPreference.findIndexOfValue(listPreference.getValue());
			preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
		} else if (preference instanceof EditTextPreference) {
			preference.setSummary(((EditTextPreference) preference).getText());
		} else {
			try {
				Object val = PreferenceManager.getDefaultSharedPreferences(preference.getContext())
									 .getString(preference.getKey(), "");
				preference.setSummary(val.toString());
			} catch (Exception ignored) {

			}
		}
	}

	public static void bindAll(PreferenceFragment settings) {
		if (settings.getActivity() == null)
			return;

		Set<String> keys = PreferenceManager.getDefaultSharedPreferences(settings.getActivity()).getAll().keySet();
		for (String key : keys) {
			Preference pref = settings.findPreference(key);
			if (pref != null)
				setPreferenceSummary(pref);
		}
	}

}
