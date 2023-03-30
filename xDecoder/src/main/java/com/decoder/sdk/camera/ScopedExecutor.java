package com.decoder.sdk.camera;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;


public final class ScopedExecutor implements Executor {
    private final AtomicBoolean shutdown;
    private final Executor executor;

    public void execute(@NotNull final Runnable command) {
        if (!shutdown.get()) {
            executor.execute((Runnable)(new Runnable() {
                public final void run() {
                    if (!shutdown.get()) {
                        command.run();
                    }
                }
            }));
        }
    }

    public final void shutdown() {
        this.shutdown.set(true);
    }

    public ScopedExecutor(@NotNull Executor executor) {
        super();
        this.executor = executor;
        this.shutdown = new AtomicBoolean();
    }
}
