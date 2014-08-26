package com.schautup.exceptions;

/**
 * Exception fired when same data will be added into DB.
 * <p/>
 * <p/>
 * For example:
 * <p/>
 * {@link com.schautup.data.ScheduleItem} [MUTE, 13, 30] has been inserted.
 * <p/>
 * When user wanna do insert with same data([MUTE, 13, 30]), {@link AddSameDataException} will be caught.
 *
 * @author Xinyue Zhao
 */
public final class AddSameDataException extends Exception {
}
