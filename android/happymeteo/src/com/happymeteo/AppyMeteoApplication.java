package com.happymeteo;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(
    formKey = "", // This is required for backward compatibility but not used
    formUri = "https://happymeteo.appspot.com/crash_report"
)
public class AppyMeteoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }
}