package com.example.forms.security;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class AuthEventBus {
    private static AuthEventBus instance;
    private final MutableLiveData<Boolean> unauthorizedEvent = new MutableLiveData<>();

    public static AuthEventBus getInstance() {
        if (instance == null) instance = new AuthEventBus();
        return instance;
    }

    public LiveData<Boolean> getUnauthorizedEvent() {
        return unauthorizedEvent;
    }

    public void notifyUnauthorized() {
        unauthorizedEvent.postValue(true);
    }



}
