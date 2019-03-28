package ch.unil.eda.activmatch.ui;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import ch.unil.eda.activmatch.R;
import ch.unil.eda.activmatch.adapter.CellView;
import ch.unil.eda.activmatch.adapter.GenericAdapter;

public class AlertDialogUtils {

    public static <T> AlertDialog createDialog(Context context, String title, List<T> items,
                                               CellView<T> cellDefiner) {
        RecyclerView recyclerView = new RecyclerView(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = View.inflate(context, R.layout.dialog_title, null);
        TextView text = v.findViewById(R.id.dialog_text);
        text.setText(title);
        builder.setCustomTitle(v);
        builder.setView(recyclerView);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        GenericAdapter<T> adapter = new GenericAdapter<>(cellDefiner);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter.setItems(items);
        return dialog;
    }

    public static void alert(Context c, CharSequence title, final Runnable onDismiss) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        View v = View.inflate(c, R.layout.dialog_title, null);
        TextView text = v.findViewById(R.id.dialog_text);
        text.setText(title);
        builder.setCustomTitle(v);
        builder.setPositiveButton(c.getString(android.R.string.ok), (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog ad = builder.create();
        ad.setCanceledOnTouchOutside(false);
        ad.setOnDismissListener(dialogInterface -> {
            if (onDismiss != null) {
                onDismiss.run();
            }

        });
        ad.show();
    }

    public static void confirmation(Context c, CharSequence title, final Runnable onOk) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        View v = View.inflate(c, R.layout.dialog_title, null);
        TextView text = v.findViewById(R.id.dialog_text);
        text.setText(title);
        builder.setCustomTitle(v);
        builder.setNegativeButton(c.getString(android.R.string.cancel), (dialogInterface, i) -> dialogInterface.dismiss());
        builder.setPositiveButton(c.getString(android.R.string.ok), (dialogInterface, i) -> {
            onOk.run();
            dialogInterface.dismiss();
        });
        AlertDialog ad = builder.create();
        ad.setCanceledOnTouchOutside(false);
        ad.show();
    }

    public static AlertDialog createLoadingDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = View.inflate(context, R.layout.progress_dialog, null);
        builder.setView(v);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        return dialog;
    }
}
