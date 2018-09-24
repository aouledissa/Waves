package com.github.andromedcodes.waves.observers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.andromedcodes.waves.callbacks.OnDataReady;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BooleanSupplier;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by mohamed on 22/09/2018.
 * email: devmed01@gmail.com
 */
public final class ViewObserver {

    private static volatile ViewObserver instance = null;
    private Builder builder;

    private ViewObserver() {
        this.builder = new Builder();
    }

    private static void assertInitialized() {
        if (instance == null)
            instance = new ViewObserver();
    }

    public static Builder on(final View view) {
        assertInitialized();
        return instance.builder.view(view);
    }

    private static void animateAlpha(final View view) {
        ValueAnimator animation = ValueAnimator.ofFloat(0f, 1f);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setDuration(700);
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setAlpha(((float) valueAnimator.getAnimatedValue()));
            }
        });
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.setForeground(null);
            }
        });
        animation.start();
    }

    public static final class Builder {
        private View view;
        private boolean stopAllAtOnce;
        private OnDataReady stateListener;
        private View leaderView;

        public Builder stateListener(OnDataReady stateListener) {
            this.stateListener = stateListener;
            return this;
        }

        public Builder view(View view) {
            this.view = view;
            return this;
        }

        public Builder stopAllAtOnce(boolean stopAllAtOnce) {
            this.stopAllAtOnce = stopAllAtOnce;
            return this;
        }

        public Builder leader(View leaderView) {
            this.leaderView = leaderView;
            this.stopAllAtOnce = false;
            return this;
        }

        public Disposable execute() {
            final View view = instance.builder.view;
            final boolean stopAllAtOnce = instance.builder.stopAllAtOnce;
            final OnDataReady stateListener = instance.builder.stateListener;

            if (view == null)
                throw new RuntimeException("No view was assigned to Waves");

            Observable<View> observable = Observable.just(view)
                    .subscribeOn(Schedulers.single())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnDispose(new Action() {
                        @Override
                        public void run() {
                            animateAlpha(view);
                        }
                    });
            if (view instanceof TextView) {
                if (!((TextView) view).getText().toString().equals(""))
                    throw new RuntimeException("TextView is not empty");
                observable = observable
                        .repeatUntil(new BooleanSupplier() {
                            @Override
                            public boolean getAsBoolean() {
                                return ((TextView) view).getText().length() > 0;
                            }
                        });
            } else if (view instanceof ImageView) {
                if (((ImageView) view).getDrawable() != null)
                    throw new RuntimeException("ImageView is not empty");
                else
                    observable = observable
                            .repeatUntil(new BooleanSupplier() {
                                @Override
                                public boolean getAsBoolean() {
                                    return ((ImageView) view).getDrawable() != null;
                                }
                            });
            }

            return observable.doOnComplete(new Action() {
                @Override
                public void run() {
                    if (leaderView == view || stopAllAtOnce) {
                        stateListener.notifyDataReady();
                        return;
                    }
                    animateAlpha(view);
                }
            }).subscribe();
        }
    }
}
