package net.anfet.simple.support.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Oleg on 08.08.2016.
 */
public final class SupportAuthorization {

	private static SupportAuthorization instance;

	private final SharedPreferences preferences;
	private LoginMap map;

	private SupportAuthorization(Context context) {
		preferences = context.getSharedPreferences(SupportAuthorization.class.getSimpleName(), Context.MODE_PRIVATE);
		try {
			String value = preferences.getString(SupportAuthorization.class.getSimpleName(), null);
			map = SupportGson.get().fromJson(value, LoginMap.class);
		} catch (Exception ex) {
			Log.e(getClass().getSimpleName(), "Cannot read map", ex);
		}

		if (map == null) {
			map = new LoginMap();
		}
	}

	public static SupportAuthorization getInstance(Context context) {
		return (instance == null ? (instance = new SupportAuthorization(context)) : instance);
	}

	public void save() {
		String value = SupportGson.get().toJson(map);
		preferences.edit().putString(SupportAuthorization.class.getSimpleName(), value).commit();
	}

	public LoginMap getMap() {
		return map;
	}

	public String getPassword(String login) {
		if (login.contains(login)) {

			LoginPassword loginPassword = map.getLogins().get(login);
			if (loginPassword != null) {
				if (loginPassword.expireAt == null || loginPassword.expireAt.before(new Date())) {
					Log.i(getClass().getSimpleName(), String.format(Locale.US, "Login '%s' expired", login));
					map.getLogins().remove(login);
					save();
					return null;
				}

				return loginPassword.password;
			}
		}

		return null;
	}

	public String getLastLogin() {
		return map.lastLogin;
	}

	public void setLastLogin(String lastLogin) {
		map.lastLogin = lastLogin;
	}

	public void freshen(String login) {
		LoginPassword loginPassword = map.getLogins().get(login);
		loginPassword.freshen();
		save();
	}

	public void save(String login, String password) {
		map.getLogins().put(login, new LoginPassword(login, password));
		save();
	}

	public class LoginMap {
		String lastLogin;
		private Map<String, LoginPassword> logins;

		LoginMap() {
			logins = new TreeMap<>();
		}

		public Map<String, LoginPassword> getLogins() {
			return logins == null ? (logins = new TreeMap<>()) : logins;
		}
	}

	public class LoginPassword {
		String login;
		String password;
		Timestamp expireAt;

		public LoginPassword(String login, String password) {
			this.login = login;
			this.password = password;
			freshen();
		}

		public void freshen() {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, 7);
			this.expireAt = new Timestamp(calendar.getTimeInMillis());
		}

		@Override
		public String toString() {
			return login;
		}

		public String getLogin() {
			return login;
		}

		public String getPassword() {
			return password;
		}
	}
}
