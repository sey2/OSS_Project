package com.stfalcon.chatkit.dialogs;

import android.view.View;

public interface SetOnClickItemListener {

    public void onDeleteClick(DialogsListAdapter.BaseDialogViewHolder holder, View view, int itemId, int getAdapterPosition);
}
