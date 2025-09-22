package com.example.eventplanner.pagination;

import java.util.EventListener;

public interface OnPaginateListener extends EventListener {
    void OnPaginate(int newPage);
}
