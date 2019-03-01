package com.example.apple.geektech;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class SaveRecordDialog extends AppCompatDialogFragment {
    EditText name;
    ISaveRecordDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_save_record_dialog, null);

        builder.setView(view)
                .setTitle("Save")
                .setPositiveButton("save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String names = name.getText().toString();
                        if (!names.trim().equals("")){
                            listener.applyName(names);

                        } else {
                            name.setError("Name is required");
                        }
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        name = view.findViewById(R.id.nameDialogTV);

        return builder.create();
    }


    public interface ISaveRecordDialogListener {
        void applyName(String name);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (ISaveRecordDialogListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context + " must implement ISaveRecordDialogLister");
        }
    }
}