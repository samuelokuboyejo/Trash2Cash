package com.trash2cash.users.utils;

import com.trash2cash.users.model.User;

public class RecyclerUtils {

    public static String getRecyclerDisplayName(User recycler) {
        if (recycler == null) {
            return "A recycler";
        }
        if (recycler.getBusinessName() != null && !recycler.getBusinessName().isBlank()) {
            return recycler.getBusinessName();
        }
        if (recycler.getFirstName() != null && !recycler.getFirstName().isBlank()) {
            return recycler.getFirstName();
        }
        return "A recycler";
    }
}
