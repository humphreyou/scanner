package com.decoder.sdk;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.MainThread;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;


public final class WorkflowModel extends AndroidViewModel {
    @NotNull
    private final MutableLiveData workflowState;
    @NotNull
    private final MutableLiveData objectToSearch;
    @NotNull
    private final MutableLiveData searchedObject;
    @NotNull
    private final MutableLiveData detectedBarcode;
    private final HashSet objectIdsToSearch;
    private boolean isCameraLive;

    @NotNull
    public final MutableLiveData getWorkflowState() {
        return this.workflowState;
    }

    @NotNull
    public final MutableLiveData getObjectToSearch() {
        return this.objectToSearch;
    }

    @NotNull
    public final MutableLiveData getSearchedObject() {
        return this.searchedObject;
    }

    @NotNull
    public final MutableLiveData getDetectedBarcode() {
        return this.detectedBarcode;
    }

    public final boolean isCameraLive() {
        return this.isCameraLive;
    }

    private final Context getContext() {
        Application var10000 = this.getApplication();
        Intrinsics.checkNotNullExpressionValue(var10000, "getApplication<Application>()");
        Context var1 = var10000.getApplicationContext();
        Intrinsics.checkNotNullExpressionValue(var1, "getApplication<Application>().applicationContext");
        return var1;
    }

    @MainThread
    public final void setWorkflowState(@NotNull WorkflowModel.WorkflowState workflowState) {
        if (workflowState != WorkflowModel.WorkflowState.CONFIRMED &&
            workflowState != WorkflowModel.WorkflowState.SEARCHING &&
            workflowState != WorkflowModel.WorkflowState.SEARCHED) {
            //this.confirmedObject = (DetectedObjectInfo)null;
        }

        this.workflowState.setValue(workflowState);
    }

    public final void  markCameraLive() {
        isCameraLive = true;
        objectIdsToSearch.clear();
    }

    public final void  markCameraFrozen() {
        isCameraLive = false;
    }

    public WorkflowModel(@NotNull Application application) {
        super(application);
        this.workflowState = new MutableLiveData();
        this.objectToSearch = new MutableLiveData();
        this.searchedObject = new MutableLiveData();
        this.detectedBarcode = new MutableLiveData();
        this.objectIdsToSearch = new HashSet();
    }

    public static enum WorkflowState {
        NOT_STARTED,
        DETECTING,
        DETECTED,
        CONFIRMING,
        CONFIRMED,
        SEARCHING,
        SEARCHED;
    }
}
