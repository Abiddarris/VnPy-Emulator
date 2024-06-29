package org.renpy.android;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.abiddarris.plugin.PluginArguments;
import com.abiddarris.plugin.game.RenPyGame;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.libsdl.app.SDLActivity;

public class PythonSDLActivity extends SDLActivity {

    /**
     * This exists so python code can access this activity.
     */
    public static PythonSDLActivity mActivity = null;

    /**
     * The layout that contains the SDL view. VideoPlayer uses this to add
     * its own view on on top of the SDL view.
     */
    public FrameLayout mFrameLayout;


    /**
     * A layout that contains mLayout. This is a 3x3 grid, with the layout
     * in the center. The idea is that if someone wants to show an ad, they
     * can stick it in one of the other cells..
     */
    public LinearLayout mVbox;
    
    private File gameScript;
    private RenPyGame game;

    protected String[] getLibraries() {
        return new String[] {
            "png16",
            "SDL2",
            "SDL2_image",
            "SDL2_ttf",
            "SDL2_gfx",
            "SDL2_mixer",
            "python2.7",
            "pymodules",
            "main",
        };
    }


    // GUI code. /////////////////////////////////////////////////////////////


    public void addView(View view, int index) {
        mVbox.addView(view, index, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, (float) 0.0));
    }

    public void removeView(View view) {
        mVbox.removeView(view);
    }

    @Override
    public void setContentView(View view) {
        mFrameLayout = new FrameLayout(this);
        mFrameLayout.addView(view);

        mVbox = new LinearLayout(this);
        mVbox.setOrientation(LinearLayout.VERTICAL);
        mVbox.addView(mFrameLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, (float) 1.0));

        game.setContentView(view);
        
        super.setContentView(mVbox);
    }

    private void setupMainWindowDisplayMode() {
        View decorView = setSystemUiVisibilityMode();
        decorView.setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                setSystemUiVisibilityMode(); // Needed to avoid exiting immersive_sticky when keyboard is displayed
            }
        });
    }

    private View setSystemUiVisibilityMode() {
        View decorView = getWindow().getDecorView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            int options;
            options =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            decorView.setSystemUiVisibility(options);

        }

        return decorView;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            setupMainWindowDisplayMode();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setupMainWindowDisplayMode();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        game = RenPyGame.getInstance(this);
        game.onCreate(savedInstanceState);
        
        super.onCreate(savedInstanceState);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        if(gameScript != null) {
            gameScript.delete();
        }
    }

    private void copyGameMainScript(String gamePath, String scriptName) {
    	try {
            gameScript = new File(gamePath, "main.py");
            gameScript.deleteOnExit();
            
            var src = new File(gamePath, scriptName);
            var inputStream = new BufferedInputStream(new FileInputStream(src));
            var outputStream = new BufferedOutputStream(new FileOutputStream(gameScript));
            var buf = new byte[8192];
            int len;
            while((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public native void nativeSetEnv(String variable, String value);

    public void preparePython() {
        Log.v("python", "Starting preparePython.");

        mActivity = this;

        PluginArguments arguments = new PluginArguments(getIntent());
        String path = arguments.getGamePath();
        String python = arguments.getRenpyPrivatePath();
        
        copyGameMainScript(path, arguments.getGameScript());
            
        nativeSetEnv("ANDROID_ARGUMENT", path);
        nativeSetEnv("ANDROID_PRIVATE", python);
        nativeSetEnv("ANDROID_PUBLIC",  path);
        nativeSetEnv("ANDROID_OLD_PUBLIC", path);
        
        // Figure out the APK path.
        String apkFilePath;
        ApplicationInfo appInfo;
        PackageManager packMgmr = getApplication().getPackageManager();

        try {
            appInfo = packMgmr.getApplicationInfo(getPackageName(), 0);
            apkFilePath = appInfo.sourceDir;
        } catch (NameNotFoundException e) {
            apkFilePath = "";
        }

        nativeSetEnv("ANDROID_APK", apkFilePath);
        nativeSetEnv("PYTHONOPTIMIZE", "2");
        nativeSetEnv("PYTHONHOME", python);
        nativeSetEnv("PYTHONPATH", python + ":" + python + "/lib");

        Log.v("python", "Finished preparePython.");

    };
    
    // Code to support public APIs. ////////////////////////////////////////////

    public void openUrl(String url) {
        Log.i("python", "Opening URL: " + url);

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void vibrate(double s) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            v.vibrate((int) (1000 * s));
        }
    }

    public int getDPI() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.densityDpi;
    }

    public PowerManager.WakeLock wakeLock = null;

    public void setWakeLock(boolean active) {
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "plugin735606:Screen On");
            wakeLock.setReferenceCounted(false);
        }

        if (active) {
            wakeLock.acquire(TimeUnit.HOURS.toMillis(1));
        } else {
            wakeLock.release();
        }
    }

}
