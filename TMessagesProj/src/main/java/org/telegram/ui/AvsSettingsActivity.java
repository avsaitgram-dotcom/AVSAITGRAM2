package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocalPremium;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

/** Экран «Настройки AVSAITGRAM». Новые пункты добавляй по образцу localPremiumRow. */
public class AvsSettingsActivity extends BaseFragment {

    private RecyclerListView listView;
    private ListAdapter adapter;

    private int rowCount;
    private int headerRow;
    private int localPremiumRow;
    private int infoRow;

    @Override
    public boolean onFragmentCreate() {
        updateRows();
        return super.onFragmentCreate();
    }

    private void updateRows() {
        rowCount = 0;
        headerRow = rowCount++;
        localPremiumRow = rowCount++;
        infoRow = rowCount++;
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle("Настройки AVSAITGRAM");
        actionBar.setAllowOverlayTitle(true);
        if (AndroidUtilities.isTablet()) {
            actionBar.setOccupyStatusBar(false);
        }
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        FrameLayout frameLayout = new FrameLayout(context);
        fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setAdapter(adapter = new ListAdapter());
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView.setOnItemClickListener((view, position) -> {
            if (position == localPremiumRow) {
                boolean enabled = !LocalPremium.isEnabled();
                LocalPremium.setEnabled(enabled);
                ((TextCheckCell) view).setChecked(enabled);
                getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
            }
        });
        return fragmentView;
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 1;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == headerRow) return 0;
            if (position == infoRow) return 2;
            return 1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            Context context = parent.getContext();
            if (viewType == 0) {
                view = new HeaderCell(context);
            } else if (viewType == 1) {
                view = new TextCheckCell(context);
            } else {
                view = new TextInfoPrivacyCell(context);
            }
            if (viewType != 2) {
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    ((HeaderCell) holder.itemView).setText("AVSAITGRAM");
                    break;
                case 1:
                    TextCheckCell cell = (TextCheckCell) holder.itemView;
                    if (position == localPremiumRow) {
                        cell.setTextAndCheck("Local Premium", LocalPremium.isEnabled(), false);
                    }
                    break;
                case 2:
                    ((TextInfoPrivacyCell) holder.itemView).setText("Local Premium работает только в этом приложении и не меняет статус Premium на серверах Telegram.");
                    break;
            }
        }
    }
}
