package com.schautup.bus;

/**
 * Event fired after an external App has been installed or removed.
 *
 * @author Xinyue Zhao
 */
public final class ExternalAppChangedEvent {
	/**
	 * Package-Name of the installed external App.
	 */
	private final String mPackageName;

	/**
	 * Constructor of {@link ExternalAppChangedEvent}
	 * @param packageName The package-name of the application that has been changed.
	 */
	public ExternalAppChangedEvent(String packageName) {
		mPackageName = packageName;
	}

	/**
	 * Package-Name of the installed external App.
	 */
	public String getPackageName() {
		return mPackageName;
	}
}
