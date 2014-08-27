package com.schautup.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.schautup.utils.Utils;

/**
 * Extension of {@link android.widget.TextView} only with animation.
 *
 * @author Xinyue Zhao
 */
public final class AnimImageTextView extends TextView {
	public AnimImageTextView(Context context) {
		super(context);
		init(context);
	}

	public AnimImageTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AnimImageTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	/**
	 * Init.
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 */
	private void init(Context cxt) {
	}

	/**
	 * Click listener for {@link com.schautup.views.AnimImageTextView} which enhance an animation when clicking.
	 *
	 * @author Xinyue Zhao
	 */
	public static abstract class OnAnimTextViewClickedListener implements OnClickListener {
		/**
		 * Impl. Event what user clicks.
		 */
		public abstract void onClick();

		@Override
		public final void onClick(final View v) {
			v.setEnabled(false);
			final float initAplha = ViewHelper.getAlpha(v);
			ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(v, Utils.ALPHA, initAplha, 0.1f, 0.2f, 0.3f, 0.4f,
					0.5f, initAplha).setDuration(400);
			objectAnimator.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);
					onClick();
					v.setEnabled(true);
				}
			});
			objectAnimator.start();
		}
	}
}
