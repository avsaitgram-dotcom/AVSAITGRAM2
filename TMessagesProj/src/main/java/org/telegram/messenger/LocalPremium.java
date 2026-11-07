package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;

/** Локальный премиум AVSAITGRAM: меняет только поведение клиента, серверный статус Telegram не трогает. */
public final class LocalPremium {

    private static final String PREFS = "avsait_prefs";
    private static final String KEY = "localPremiumEnabled";
    private static Boolean cached;

    private LocalPremium() {
    }

    private static SharedPreferences prefs() {
        return ApplicationLoader.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static boolean isEnabled() {
        if (cached == null) {
            if (ApplicationLoader.applicationContext == null) {
                return false;
            }
            cached = prefs().getBoolean(KEY, true);
        }
        return cached;
    }

    public static void setEnabled(boolean enabled) {
        cached = enabled;
        prefs().edit().putBoolean(KEY, enabled).apply();
    }
}
