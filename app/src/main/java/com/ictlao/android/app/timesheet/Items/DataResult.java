package com.ictlao.android.app.timesheet.Items;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public interface DataResult {
    void onSuccess(DataSnapshot snapshot);
    void onError(DatabaseError error);
}
