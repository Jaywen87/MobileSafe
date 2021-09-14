package com.wenjie.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * 应用程序信息的业务bean
 */
public class AppInfo {
    private Drawable icon;
    private String name;
    private String pacakagename;
    private boolean inRom;
    private boolean userApp;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPacakagename() {
        return pacakagename;
    }

    public void setPacakagename(String pacakagename) {
        this.pacakagename = pacakagename;
    }

    public boolean isInRom() {
        return inRom;
    }

    public void setInRom(boolean inRom) {
        this.inRom = inRom;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }
}
