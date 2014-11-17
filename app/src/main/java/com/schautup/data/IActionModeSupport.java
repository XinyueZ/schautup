package com.schautup.data;

/**
 * Models that need to be deleted when action-mode is on.
 */
public interface IActionModeSupport {
	/**
	 * Set whether item is checked to delete or not.
	 * @param check {@code true} if checked.
	 */
	void setCheck(boolean check);
	/**
	 * To know whether item is checked to delete or not.
	 * @return   {@code true} if checked.
	 */
	boolean isChecked();
}
