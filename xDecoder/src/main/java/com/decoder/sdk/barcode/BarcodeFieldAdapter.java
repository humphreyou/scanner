package com.decoder.sdk.barcode;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import com.decoder.sdk.R;

public final class BarcodeFieldAdapter extends Adapter {
    private final List<BarcodeField> barcodeFieldList;
    @NotNull
    public BarcodeFieldAdapter.BarcodeFieldViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return this.create(parent);
    }

    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        BarcodeFieldViewHolder barcodeHolder = (BarcodeFieldViewHolder)holder;
        barcodeHolder.bindBarcodeField(barcodeFieldList.get(position));
    }

    public int getItemCount() {
        return barcodeFieldList.size();
    }

    public BarcodeFieldAdapter(@NotNull List barcodeFieldList) {
        super();
        this.barcodeFieldList = barcodeFieldList;
    }

    public static final class BarcodeFieldViewHolder extends ViewHolder {
        private final TextView labelView;
        private final TextView valueView;

        public  void bindBarcodeField(@NotNull BarcodeField barcodeField) {
            labelView.setText(barcodeField.getLabel());
            valueView.setText(barcodeField.getValue());
        }

        public BarcodeFieldViewHolder(View view) {
            super(view);
            labelView = view.findViewById(R.id.barcode_field_label);
            valueView = view.findViewById(R.id.barcode_field_value);
        }
    }

    public BarcodeFieldViewHolder create(@NotNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.barcode_field, parent, false);
        return new BarcodeFieldViewHolder(view);
    }
}
