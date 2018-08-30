package sumitcodes.com;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

    private int pageWidth;
    private float traverseDistance;
    private View city0;
    private View city1;
    private View city2;
    private View city3;
    private View light;
    private View birdMain;
    private View cloud;
    private View planeMain;
    private View balloonMain;
    private View sun;
    private View planeCard1;
    private View balloonCard1;
    private View birdCard1;
    private View moon;
    private View planeCard2;
    private View balloonCard2;
    private ViewPager viewPager;
    private static final float[] NEGATIVE = {
            -1.0f, 0, 0, 0, 255,
            0, -1.0f, 0, 0, 255,
            0, 0, -1.0f, 0, 255,
            0, 0, 0, 1.0f, 0
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        traverseDistance = getResources().getDimension(R.dimen.traverse_distance);
        city0 = findViewById(R.id.city_0);
        city1 = findViewById(R.id.city_1);
        city2 = findViewById(R.id.city_2);
        city3 = findViewById(R.id.city_3);
        birdMain = findViewById(R.id.bird_main);
        balloonMain = findViewById(R.id.balloon_main);
        planeMain = findViewById(R.id.plane_main);
        light = findViewById(R.id.light);
        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        viewPager = findViewById(R.id.view_pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);

        Display mDisplay = getWindowManager().getDefaultDisplay();
        pageWidth = mDisplay.getWidth();

        viewPager.setPageTransformer(true, new AnimatePageTransformer());
        (new Handler()).postDelayed(this::changePage, 5000);


        final View view = findViewById(R.id.city_wrapper);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final View view1 = findViewById(R.id.city_hidden);
                Bitmap bitmap1 = getImageBitmap(screenshot(view1));
                Bitmap bitmap2 = getImageBitmap(screenshot(view));

                ImageView cityCard1 = viewPager.findViewWithTag("0").findViewById(R.id.city_card_1);
                cityCard1.setImageBitmap(bitmap1);
                cityCard1.setColorFilter(getResources().getColor(R.color.brick));

                ImageView cityCard2 = viewPager.findViewWithTag("1").findViewById(R.id.city_card_2);
                cityCard2.setImageBitmap(bitmap1);
                cityCard2.setColorFilter(getResources().getColor(R.color.silver));
                cityCard2.setTranslationX(-view.getWidth() * 0.1f);

                ImageView cityLight = viewPager.findViewWithTag("1").findViewById(R.id.city_light);
                cityLight.setImageBitmap(bitmap2);
                cityLight.setColorFilter(getResources().getColor(R.color.light));
                cityLight.setTranslationX(-view.getWidth() * 0.1f);

                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void changePage() {
        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(viewPager.getContext(), new AccelerateDecelerateInterpolator());
            mScroller.set(viewPager, scroller);
            viewPager.setCurrentItem(1);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
    }

    private Bitmap screenshot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private Bitmap getImageBitmap(Bitmap bitmap) {
        Bitmap transparentBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int margin = getResources().getDimensionPixelOffset(R.dimen.margin);
        int statusBar = getResources().getDimensionPixelOffset(R.dimen.verticalSpace);

        Bitmap croppedBitmap = getCurveImage(Bitmap.createBitmap(bitmap, margin, margin, width - 2 * margin, (3 * height) / 5 - statusBar));
        Canvas canvas = new Canvas(transparentBitmap);
        canvas.drawBitmap(croppedBitmap, margin, margin, null);

        return transparentBitmap;
    }

    private Bitmap getCurveImage(Bitmap bitmap) {
        int w = bitmap.getWidth(), h = bitmap.getHeight();
        Bitmap rounder = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(rounder);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        int radius = getResources().getDimensionPixelOffset(R.dimen.radius);
        canvas.drawRoundRect(new RectF(0, 0, w, h), radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        Bitmap result = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas resultCanvas = new Canvas(result);
        resultCanvas.drawBitmap(bitmap, 0, 0, null);
        resultCanvas.drawBitmap(rounder, 0, 0, paint);
        return result;
    }

    class AnimatePageTransformer implements ViewPager.PageTransformer {

        public void transformPage(@NonNull View view, float position) {
            int screen = Integer.parseInt(String.valueOf(view.getTag()));
            float distance = position * pageWidth;
            float oppPosition = position - 1;
            float oppDistance = oppPosition * traverseDistance;

            if (sun == null) {
                sun = viewPager.findViewWithTag("0").findViewById(R.id.sun);
                moon = viewPager.findViewWithTag("1").findViewById(R.id.moon);
                cloud = viewPager.findViewWithTag("1").findViewById(R.id.cloud);
                birdCard1 = viewPager.findViewWithTag("0").findViewById(R.id.bird_card_1);
                planeCard1 = viewPager.findViewWithTag("0").findViewById(R.id.plane_card_1);
                planeCard2 = viewPager.findViewWithTag("1").findViewById(R.id.plane_card_2);
                balloonCard1 = viewPager.findViewWithTag("0").findViewById(R.id.balloon_card_1);
                balloonCard2 = viewPager.findViewWithTag("1").findViewById(R.id.balloon_card_2);
            }

            float balloonXFactor = 2;
            float balloonYFactor = 5;
            if (screen == 0 && position >= -1) {
                light.setTranslationX(-distance * 0.42f);
                sun.setTranslationX(-distance * 1.42f);
                birdMain.setTranslationX(-position * traverseDistance * 0.5f);
                birdCard1.setTranslationX((-position * (traverseDistance * 0.5f + pageWidth)));

                planeMain.setTranslationX(-position * traverseDistance);
                planeMain.setTranslationY(position * traverseDistance);
                planeCard1.setTranslationX(-position * (traverseDistance + pageWidth));
                planeCard1.setTranslationY(position * traverseDistance);

                balloonMain.setTranslationX(-position * traverseDistance * balloonXFactor);
                balloonMain.setTranslationY(position * traverseDistance * balloonYFactor);
                balloonCard1.setTranslationX(-position * (traverseDistance * balloonXFactor + pageWidth));
                balloonCard1.setTranslationY(position * traverseDistance * balloonYFactor);
            }

            if (screen == 1 && position >= -1) {
                city0.setTranslationX(distance * 0.1f);
                city1.setTranslationX(distance * 0.1f);
                city2.setTranslationX(distance * 0.2f);
                city3.setTranslationX(distance * 0.3f);

                cloud.setTranslationX(distance * 2.0f);
                moon.setTranslationX(-pageWidth * (position + oppPosition * 0.42f));

                planeCard2.setTranslationX(-(oppDistance + distance));
                planeCard2.setTranslationY(oppDistance);

                balloonCard2.setTranslationX(-(oppDistance * balloonXFactor + distance));
                balloonCard2.setTranslationY(oppDistance * balloonYFactor);
            }
        }
    }

}
