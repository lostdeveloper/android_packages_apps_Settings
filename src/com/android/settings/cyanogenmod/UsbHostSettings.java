/*
 * Copyright (C) 2012 The CyanogenMod Project
 * Copyright (C) 2013 Timur Mehrvarz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.cyanogenmod;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.ListPreference;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

/**
 * USB Host Settings
 */
public class UsbHostSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "UsbHostSettings";

    private static final String HOST_MODE_FILE = "/sys/kernel/usbhost/usbhost_hostmode";

    private static final String FI_MODE_FILE = "/sys/kernel/usbhost/usbhost_fixed_install_mode";
    private static final String FI_MODE_PREF = "fixed_installation";   // from res/values/strings.xml
    private CheckBoxPreference mFiModePref;

    private static final String FASTCHARGE_IN_HOSTMODE_FILE = "/sys/kernel/usbhost/usbhost_fastcharge_in_host_mode";
    private static final String FASTCHARGE_IN_HOSTMODE_PREF = "fastcharge_in_host_mode";   // from res/values/strings.xml
    private CheckBoxPreference mFastChargeInHostModePref;
	private static final String USE_FASTCHARGE_IN_HOSTMODE_PROP = "persist.sys.use_fcharge_host";
    private static final String USE_FASTCHARGE_IN_HOSTMODE_DEFAULT = "0";

    private static final String HP_WIRED_ACCESSORY_PREF = "hotplug_wired_accessory";   // from res/values/strings.xml
    private CheckBoxPreference mHpWiredAccessoryPref;
    private static final String USE_HP_WIRED_ACCESSORY_PERSIST_PROP = "persist.sys.use_wired_accessory";
    private static final String USE_HP_WIRED_ACCESSORY_DEFAULT = "1";

    private static final String HP_ON_BOOT_FILE = "/sys/kernel/usbhost/usbhost_hotplug_on_boot";
    private static final String HP_ON_BOOT_PREF = "hotplug_on_boot";   // from res/values/strings.xml
    private CheckBoxPreference mHpOnBootPref;

    private static final String LANDSCAPE_UI_PREF = "landscape_mode";   // from res/values/strings.xml
    private CheckBoxPreference mLandscapeUIPref;
    private static final String USE_LANDSCAPE_UI_PERSIST_PROP = "persist.sys.use_landscape_mode";
    private static final String USE_LANDSCAPE_UI_DEFAULT = "0";

/*--------------------

    private static final String USE_FASTCHARGE_IN_HOSTMODE_PROP = "persist.sys.use_fastcharge_hostmode";
    private static final String USE_FASTCHARGE_IN_HOSTMODE_DEFAULT = "0";

	String useFastChargeInHostMode = SystemProperties.get(USE_FASTCHARGE_IN_HOSTMODE_PROP,
	                                                      USE_FASTCHARGE_IN_HOSTMODE_DEFAULT);

    if("1".equals(useFastChargeInHostMode)) {
		if (Utils.fileWriteOneLine(FASTCHARGE_IN_HOSTMODE_FILE, "0"))
			Utils.fileWriteOneLine(FASTCHARGE_IN_HOSTMODE_FILE, "1");
    } else if("0".equals(useFastChargeInHostMode)) {
		if (Utils.fileWriteOneLine(FASTCHARGE_IN_HOSTMODE_FILE, "1"))
			Utils.fileWriteOneLine(FASTCHARGE_IN_HOSTMODE_FILE, "0");
    }

"1".eq 
    if (Utils.fileWriteOneLine(FASTCHARGE_IN_HOSTMODE_FILE, mFastChargeInHostModePref.isChecked() ? "1" : "0")) {
        Log.i(TAG, "onPreferenceTreeClick value changed");
        SystemProperties.set(USE_FASTCHARGE_IN_HOSTMODE_PROP,
                mFastChargeInHostModePref.isChecked() ? "1" : "0");
        return true;
    }

*/


/*
    private AlertDialog alertDialog;
*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.usbhost_settings);

        PreferenceScreen prefSet = getPreferenceScreen();
        mFiModePref = (CheckBoxPreference) prefSet.findPreference(FI_MODE_PREF);
        mFastChargeInHostModePref = (CheckBoxPreference) prefSet.findPreference(FASTCHARGE_IN_HOSTMODE_PREF);
        mHpWiredAccessoryPref = (CheckBoxPreference) prefSet.findPreference(HP_WIRED_ACCESSORY_PREF);
        mHpOnBootPref = (CheckBoxPreference) prefSet.findPreference(HP_ON_BOOT_PREF);
        mLandscapeUIPref = (CheckBoxPreference) prefSet.findPreference(LANDSCAPE_UI_PREF);
        String temp;

		int curHostMode = 0;
        if((temp = Utils.fileReadOneLine(HOST_MODE_FILE)) != null) {
            if("1".equals(temp))
            	curHostMode=1;
        }

		if(curHostMode>0) {
			// FI checkbox disabled while device in USB host mode
	        mFiModePref.setEnabled(false);
	    } else if((temp = Utils.fileReadOneLine(FI_MODE_FILE)) != null) {
            mFiModePref.setChecked("1".equals(temp));
        }

		// hotplug-on-boot is default on in the kernel + it's checkbox is disabled 
        mHpOnBootPref.setEnabled(false);
        if((temp = Utils.fileReadOneLine(HP_ON_BOOT_FILE)) != null) {
            mHpOnBootPref.setChecked("1".equals(temp));
        }

		// TODO: landscape is off + it's checkbox is disabled 
        //mLandscapeUIPref.setEnabled(false);

        if (getPreferenceManager() != null) {

			// hotplug is default on + it's checkbox is disabled
            String useHpWiredAccessory = SystemProperties.get(USE_HP_WIRED_ACCESSORY_PERSIST_PROP,
                                                              USE_HP_WIRED_ACCESSORY_DEFAULT);
            useHpWiredAccessory = "1";
            mHpWiredAccessoryPref.setChecked("1".equals(useHpWiredAccessory));
	        mHpWiredAccessoryPref.setEnabled(false);

            String useLandscapeMode = SystemProperties.get(USE_LANDSCAPE_UI_PERSIST_PROP,
                                                           USE_LANDSCAPE_UI_DEFAULT);
            mLandscapeUIPref.setChecked("1".equals(useLandscapeMode));
            activateLandscapeModeBuildProp("1".equals(useLandscapeMode));

			String useFastChargeInHostMode = SystemProperties.get(USE_FASTCHARGE_IN_HOSTMODE_PROP,
			                                                      USE_FASTCHARGE_IN_HOSTMODE_DEFAULT);
			mFastChargeInHostModePref.setChecked("1".equals(useFastChargeInHostMode));

            /* Display the warning dialog */
/*
            alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle(R.string.performance_settings_warning_title);
            alertDialog.setMessage(getResources().getString(R.string.performance_settings_warning));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                    getResources().getString(com.android.internal.R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    PerformanceSettings.this.finish();
                }
            });
            alertDialog.show();
*/
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        if(preference == mFiModePref) {
            Log.i(TAG, "onPreferenceTreeClick mFiModePref checked="+mFiModePref.isChecked());

		    String temp;
			int curHostMode = 0;
		    if((temp = Utils.fileReadOneLine(HOST_MODE_FILE)) != null) {
		        if("1".equals(temp))
		        	curHostMode=1;
		    }

			if(curHostMode>0) {
				// not allowed to change value when in host mode
			    mFiModePref.setEnabled(false);
			    mFiModePref.setChecked(!mFiModePref.isChecked()); // undo
			} else
            if (Utils.fileWriteOneLine(FI_MODE_FILE, mFiModePref.isChecked() ? "1" : "0")) {
                Log.i(TAG, "onPreferenceTreeClick value changed");
                return true;
            }
            Log.i(TAG, "onPreferenceTreeClick failed");

        } else if(preference == mFastChargeInHostModePref) {
            Log.i(TAG, "onPreferenceTreeClick mFastChargeInHostModePref checked="+mFastChargeInHostModePref.isChecked());
            if (Utils.fileWriteOneLine(FASTCHARGE_IN_HOSTMODE_FILE, mFastChargeInHostModePref.isChecked() ? "1" : "0")) {
                Log.i(TAG, "onPreferenceTreeClick value changed");
	            SystemProperties.set(USE_FASTCHARGE_IN_HOSTMODE_PROP,
	                    mFastChargeInHostModePref.isChecked() ? "1" : "0");
                return true;
            }
		    mFastChargeInHostModePref.setChecked(!mFastChargeInHostModePref.isChecked()); // undo
            Log.i(TAG, "onPreferenceTreeClick failed");

        } else if(preference == mHpWiredAccessoryPref) {
            SystemProperties.set(USE_HP_WIRED_ACCESSORY_PERSIST_PROP,
                    mHpWiredAccessoryPref.isChecked() ? "1" : "0");
            return true;
                    
        } else if(preference == mHpOnBootPref) {
            Log.i(TAG, "onPreferenceTreeClick mHpOnBootPref checked="+mHpOnBootPref.isChecked());
            if (Utils.fileWriteOneLine(HP_ON_BOOT_FILE, mHpOnBootPref.isChecked() ? "1" : "0")) {
                Log.i(TAG, "onPreferenceTreeClick value changed");
                return true;
            }
            Log.i(TAG, "onPreferenceTreeClick failed");

        } else if(preference == mLandscapeUIPref) {
            SystemProperties.set(USE_LANDSCAPE_UI_PERSIST_PROP,
                    mLandscapeUIPref.isChecked() ? "1" : "0");
            activateLandscapeModeBuildProp(mLandscapeUIPref.isChecked());
            return true;
                    
        }
        // If we didn't handle it, let preferences handle it.
        Log.i(TAG, "onPreferenceTreeClick not handled");
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
/*
        if (preference == mUseDitheringPref) {
            String newVal = (String) newValue;
            int index = mUseDitheringPref.findIndexOfValue(newVal);
            SystemProperties.set(USE_DITHERING_PERSIST_PROP, newVal);
            mUseDitheringPref.setSummary(mUseDitheringPref.getEntries()[index]);
        }
*/

/*
        String fname = "";

        if (newValue != null) {
            if (preference == mFiModePref) {
                Log.i(TAG, "onPreferenceChange mFiModePref "+(String)newValue);
                fname = FI_MODE_FILE;
            } else {
                Log.i(TAG, "onPreferenceChange other");
            }

            if (Utils.fileWriteOneLine(fname, (String) newValue)) {
                Log.i(TAG, "onPreferenceChange value changed");
                return true;
            }
        }
*/
        Log.i(TAG, "onPreferenceChange not implemented");
        return false;
    }

    private boolean activateLandscapeModeBuildProp(boolean state) {
/*
        FileInputStream fis = openFileInput("/system/build.prop");
        String content = "";
        byte[] input = new byte[fis.available()];
        while (fis.read(input) != -1) {}
        content += new String(input);
        
        // TODO: patch "ro.sf.lcd_density=xxx"

        DataOutputStream outstream= new DataOutputStream(new FileOutputStream(file,false));
        String body = content;
        outstream.write(body.getBytes());
        outstream.close();
*/
        return true;
    }
}
