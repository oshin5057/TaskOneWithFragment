package com.example.android.taskone.listener;

public interface ItemListener {
    void onDelete(int position, int cursorId);
    void onEdit(int position, int cursorId);
}
