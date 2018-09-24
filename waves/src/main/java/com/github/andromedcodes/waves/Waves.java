package com.github.andromedcodes.waves;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.annotation.ColorRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.github.andromedcodes.waves.callbacks.OnDataReady;
import com.github.andromedcodes.waves.observers.ViewObserver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by mohamed on 20/09/2018.
 * email: devmed01@gmail.com
 */
public class Waves implements OnDataReady {

    private static volatile Waves waves = null;

    private RequestBuilder requestBuilder;

    private Waves() {
        this.requestBuilder = new RequestBuilder();
    }

    private static void assertInitialized() {
        if (waves == null) {
            waves = new Waves();
        }
    }

    private static RequestBuilder getRequestBuilder() {
        assertInitialized();
        return waves.requestBuilder;
    }

    public static void apply(Activity context, @ColorRes int color, long duration, boolean stopAllAtOnce) {
        try {
            ViewGroup root = ((ViewGroup) ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0));
            for (int i = 0; i < root.getChildCount(); i++) {
                getRequestBuilder().on(root.getChildAt(i));
            }
            getRequestBuilder()
                    .waveColor(context.getResources().getColor(color))
                    .duration(duration)
                    .stopAllAtOnce(stopAllAtOnce)
                    .start();
        } catch (Exception e) {
            throw new RuntimeException("Waves was unable to retrieve root view from " + context);
        }
    }

    public static RequestBuilder on(View view) {
        assertInitialized();
        return waves.requestBuilder.on(view);
    }

    private static <T extends Activity> void instantiateWaves(T target, String suffix) {
        Class<?> targetClass = target.getClass();
        String className = targetClass.getName();
        try {
            Class<?> bindingClass = targetClass
                    .getClassLoader()
                    .loadClass(className + suffix);
            Constructor<?> classConstructor = bindingClass.getConstructor(targetClass);
            try {
                classConstructor.newInstance(target);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to invoke " + classConstructor, e);
            } catch (InstantiationException e) {
                throw new RuntimeException("Unable to invoke " + classConstructor, e);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                }
                if (cause instanceof Error) {
                    throw (Error) cause;
                }
                throw new RuntimeException("Unable to create instance.", cause);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Attempt to use Auto bind without shimmerizer-compiler annotation processor plugin");
            //throw new RuntimeException("Unable to find Class for " + className + suffix, e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find constructor for " + className + suffix, e);
        }
    }

    /*public static <T extends Activity> void standalone(T activity) {
        instantiateWaves(activity, BindingSuffix.GENERATED_CLASS_SUFFIX);
    }*/

    @Override
    public void notifyDataReady() {
        for (Disposable disposable : waves.requestBuilder.observerBag)
            if (!disposable.isDisposed()) disposable.dispose();
    }

    public static final class RequestBuilder {
        private static final float TRANSPARENT = 0f;
        private static final int OPAQUE = 1;
        private static final float LEFT = 0f;
        private static final float CENTER = .5f;
        private static final float RIGHT = 1f;
        private static final int START_DELAY = 300;

        private List<View> views = new ArrayList<>();
        private long duration = 1000;
        private boolean stopAtOnce;
        private View leaderView;

        private List<Disposable> observerBag = new ArrayList<>();
        private int[] shaderColors = {
                0xFFD3D3D3,
                0xFFE3E3E3,
                0xFFD3D3D3};

        public RequestBuilder waveColor(@ColorRes int color) {
            shaderColors[1] = views.get(0).getResources().getColor(color);
            return this;
        }

        public RequestBuilder backgroundColor(@ColorRes int color) {
            shaderColors[0] = views.get(0).getResources().getColor(color);
            shaderColors[2] = views.get(0).getResources().getColor(color);
            return this;
        }

        public RequestBuilder waveColorSet(int[] colors) {
            shaderColors = colors;
            return this;
        }

        public RequestBuilder duration(long duration) {
            this.duration = duration;
            return this;
        }

        public <T extends View> RequestBuilder on(T view) {
            if (view != null)
                views.add(view);
            return this;
        }

        public RequestBuilder stopAllAtOnce(boolean stopAtOnce) {
            this.stopAtOnce = stopAtOnce;
            return this;
        }

        public <T extends View> RequestBuilder leader(T view) {
            this.leaderView = view;
            return this;
        }

        private ValueAnimator getAnimation() {
            ValueAnimator animator = ValueAnimator.ofFloat(-1f, 1f);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setStartDelay(START_DELAY);
            animator.setDuration(duration);
            return animator;
        }

        private Shader updateShader(float w, float f) {
            float left = w * f;
            return new LinearGradient(left, 0f, left + w, 0f,
                    shaderColors,
                    new float[]{LEFT,
                            CENTER,
                            RIGHT},
                    Shader.TileMode.CLAMP);
        }

        private void updateGradient(final View view) {
            final PaintDrawable ppaintDrawable = new PaintDrawable();
            ValueAnimator animator = getAnimation();
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    final float value = (float) valueAnimator.getAnimatedValue();
                    ppaintDrawable.setShaderFactory(new ShapeDrawable.ShaderFactory() {
                        @Override
                        public Shader resize(int i, int i1) {
                            return updateShader(i, value);
                        }
                    });
                    ppaintDrawable.setShape(new RectShape());
                    if (view.getAlpha() == 0)
                        view.setAlpha(OPAQUE);
                }
            });

            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    view.setAlpha(TRANSPARENT);
                    view.setForeground(ppaintDrawable);
                }
            });
            animator.start();
        }

        public void start() {
            if (this.views.size() == 0)
                throw new RuntimeException("No view was passed to Waves");

            for (View view :
                    this.views) {
                updateGradient(view);
                observerBag.add(ViewObserver
                        .on(view)
                        .leader(leaderView)
                        .stateListener(waves)
                        .stopAllAtOnce(stopAtOnce)
                        .execute());
            }
        }

    }
}
