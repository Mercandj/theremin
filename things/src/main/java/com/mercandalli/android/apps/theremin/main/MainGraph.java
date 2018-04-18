package com.mercandalli.android.apps.theremin.main;

import android.app.Application;

import com.mercandalli.android.sdk.soundsystem.AudioManager;
import com.mercandalli.android.sdk.soundsystem.SoundSystemModule;
import com.mercandalli.android.apps.theremin.main_thread.MainThreadModule;
import com.mercandalli.android.apps.theremin.main_thread.MainThreadPost;
import com.mercandalli.android.sdk.soundsystem.ThereminManager;

public class MainGraph {

    private static MainGraph instance;

    public static MainGraph get() {
        if (instance == null) {
            throw new IllegalStateException("Init first");
        }
        return instance;
    }

    public static MainGraph init(Application application) {
        if (instance == null) {
            instance = new MainGraph(application);
        }
        return instance;
    }

    private final MainThreadPost mainThreadPost;
    private final AudioManager audioManager;
    private final ThereminManager thereminManager;

    private MainGraph(Application application) {
        MainThreadModule mainThreadModule = new MainThreadModule();
        mainThreadPost = mainThreadModule.provideMainThreadPost();
        SoundSystemModule soundSystemModule = new SoundSystemModule(application);
        audioManager = soundSystemModule.provideAudioManager();
        thereminManager = soundSystemModule.provideThereminManager();
    }

    public MainThreadPost provideMainThreadPost() {
        return mainThreadPost;
    }

    public AudioManager provideAudioManager() {
        return audioManager;
    }

    public ThereminManager provideThereminManager() {
        return thereminManager;
    }
}
