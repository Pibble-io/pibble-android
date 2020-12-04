package com.star.pibbledev.services.global.customview.alertview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.star.pibbledev.R;

public class UpvotedDoneDialog extends LinearLayout {

    public UpvotedDoneDialog(Context context) {
        super(context);

        LayoutInflater minflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert minflater != null;
        final View view = minflater.inflate(R.layout.item_upvoted_done_dialog, this, true);
    }

}
