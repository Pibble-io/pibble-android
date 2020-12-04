package com.star.pibbledev.services.global.customview.alertview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.star.pibbledev.R;

public class NotReadyYetDialog extends Dialog {

    public NotReadyYetDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.item_not_ready_yet_dialog);

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.btn_cancel);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
