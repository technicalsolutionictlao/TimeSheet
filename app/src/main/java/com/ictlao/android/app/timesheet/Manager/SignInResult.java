package com.ictlao.android.app.timesheet.Manager;

import com.google.firebase.auth.FirebaseUser;

public interface SignInResult {
    void onSuccess(FirebaseUser user);
    void onFail(String error);
}
