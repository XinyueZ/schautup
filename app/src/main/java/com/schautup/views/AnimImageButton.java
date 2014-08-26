package com.schautup.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

/**
 * Extension of {@link android.widget.ImageButton} only with animation.
 *
 * @author Xinyue Zhao
 */
public final class AnimImageButton extends ImageButton {
	public AnimImageButton(Context context) {
		super(context);
		init(context);
	}

	public AnimImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AnimImageButton(Context context, AttributeSet attrs, int defStyle) {
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
	 * Click listener for {@link AnimImageButton} which enhance an animation when clicking.
	 *
	 * @author  Xinyue Zhao
	 */
	public static class OnAnimImageButtonClickedListener implements OnClickListener {
		@Override
		public void onClick(final View v) {
			v.setEnabled(false);
			final float initX = ViewHelper.getScaleX(v);
			final float initY = ViewHelper.getScaleY(v);
			ViewPropertyAnimator animator = ViewPropertyAnimator.animate(v);
			animator.scaleX(0.5f).scaleY(0.5f).setDuration(100).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);
					ViewPropertyAnimator animator = ViewPropertyAnimator.animate(v);
					animator.scaleX(initX).scaleY(initY).setDuration(100).setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							super.onAnimationEnd(animation);
							v.setEnabled(true);
						}
					}).start();
				}
			}).start();
		}
	}
}
