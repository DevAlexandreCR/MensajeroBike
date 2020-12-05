package mensajero.mensajerobike.Constantes;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

public class MensajeroBike extends Application implements Application.ActivityLifecycleCallbacks {

    private boolean activityEnPrimerPlano;
    private static boolean activityVisible;
    private Activity activeActivity;

    public static boolean isActivityVisible() {
        return activityVisible;
    }
    public static void activityResumed() {
        activityVisible = true;
    }
    public static void activityPaused() {
        activityVisible = false;
    }
    public  Activity getActiveActivity(){
        return activeActivity;
    }
    public boolean isActivityInForeground() {
        return activityEnPrimerPlano;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        activityVisible = true;
        activityEnPrimerPlano = true;

    }

    @Override
    public void onActivityPaused(Activity activity) {
        activityVisible = false;
        activityEnPrimerPlano = false;
        activeActivity = null;
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
