package com.decoder.sdk.barcode;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.decoder.sdk.WorkflowModel;
import com.decoder.sdk.WorkflowModel.WorkflowState;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.decoder.sdk.R;

public final class BarcodeResultFragment extends BottomSheetDialogFragment {
    private static final String TAG = "BarcodeResultFragment";
    private static final String ARG_BARCODE_FIELD_LIST = "arg_barcode_field_list";

    @NotNull
    public View onCreateView(@NotNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {

        ArrayList<BarcodeField> barcodeFieldList;

        View view = layoutInflater.inflate(R.layout.barcode_bottom_sheet, viewGroup);

        Bundle arguments = this.getArguments();
        if (arguments != null && arguments.containsKey(ARG_BARCODE_FIELD_LIST)) {
            barcodeFieldList = arguments.getParcelableArrayList(ARG_BARCODE_FIELD_LIST);
            if (barcodeFieldList == null) {
                barcodeFieldList = new ArrayList();
            }
        } else {
            Log.e(TAG, "No barcode field list passed in!");
            barcodeFieldList = new ArrayList();
        }

        RecyclerView recycler_view = (RecyclerView)view.findViewById(R.id.barcode_field_recycler_view);
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager((Context)this.getActivity()));
        recycler_view.setAdapter(new BarcodeFieldAdapter(barcodeFieldList));
        return view;
    }

    public void onDismiss(@NotNull DialogInterface dialogInterface) {
        if (getActivity() != null) {
            ((WorkflowModel)ViewModelProviders.of(getActivity()).
                    get(WorkflowModel.class)).
                    setWorkflowState(WorkflowState.DETECTING);
        }
        super.onDismiss(dialogInterface);
    }

    public void onDestroyView() {
        super.onDestroyView();
    }

    public static void show(@NotNull FragmentManager fragmentManager, @NotNull ArrayList barcodeFieldArrayList) {

        BarcodeResultFragment barcodeResultFragment = new BarcodeResultFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ARG_BARCODE_FIELD_LIST, barcodeFieldArrayList);
        barcodeResultFragment.setArguments(bundle);
        barcodeResultFragment.show(fragmentManager, TAG);
    }

    public static void dismiss(@NotNull FragmentManager fragmentManager) {
        if (fragmentManager.findFragmentByTag(TAG) != null) {
            ((BarcodeResultFragment)fragmentManager.findFragmentByTag(TAG)).dismiss();
        }

    }
}
