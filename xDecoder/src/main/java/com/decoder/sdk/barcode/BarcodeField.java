package com.decoder.sdk.barcode;

import android.os.Parcel;
import android.os.Parcelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BarcodeField implements Parcelable {
    private final String label;
    private final String value;
    public static final android.os.Parcelable.Creator CREATOR = new BarcodeField.Creator();
    public final String getLabel() {
        return this.label;
    }
    public final String getValue() {
        return this.value;
    }

    public BarcodeField(@NotNull String label, @NotNull String value) {
        super();
        this.label = label;
        this.value = value;
    }

    @NotNull
    public final BarcodeField copy(@NotNull String label, @NotNull String value) {
        return new BarcodeField(label, value);
    }

    @NotNull
    public String toString() {
        return "BarcodeField(label=" + this.label + ", value=" + this.value + ")";
    }

    public int hashCode() {
        int var1 = (label != null ? label.hashCode() : 0) * 31;
        return var1 + (value != null ? value.hashCode() : 0);
    }

    public boolean equals(@Nullable Object var1) {
        if (this != var1) {
            if (var1 instanceof BarcodeField) {
                BarcodeField var2 = (BarcodeField)var1;
                if (this.label.equals(var2.label)&& this.value.equals(var2.value)) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(@NotNull Parcel parcel, int flags) {
        parcel.writeString(this.label);
        parcel.writeString(this.value);
    }

    public static class Creator implements android.os.Parcelable.Creator {
        @NotNull
        public final BarcodeField[] newArray(int size) {
            return new BarcodeField[size];
        }

        @NotNull
        public final BarcodeField createFromParcel(@NotNull Parcel in) {
            return new BarcodeField(in.readString(), in.readString());
        }
    }
}
