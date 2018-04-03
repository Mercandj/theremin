package com.mercandalli.android.apps.theremin.main;

import android.app.Application;

import com.mercandalli.android.apps.theremin.audio.AudioManager;
import com.mercandalli.android.apps.theremin.audio.AudioModule;
import com.mercandalli.android.apps.theremin.main_thread.MainThreadModule;
import com.mercandalli.android.apps.theremin.main_thread.MainThreadPost;

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

    private MainGraph(Application application) {
        MainThreadModule mainThreadModule = new MainThreadModule();
        mainThreadPost = mainThreadModule.provideMainThreadPost();
        audioManager = new AudioModule(application).provideAudioManager();
    }

    public MainThreadPost provideMainThreadPost() {
        return mainThreadPost;
    }

    public AudioManager provideAudioManager() {
        return audioManager;
    }
}
