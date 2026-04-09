package ru.obabok.mixin;

import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.service.*;
import org.spongepowered.asm.util.ReEntranceLock;
import ru.obabok.Launcher;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MyMixinService implements IMixinService {
    private final ReEntranceLock lock = new ReEntranceLock(1);

    @Override public String getName() { return "SimpleLoaderService"; }
    @Override public boolean isValid() { return true; }
    @Override public void prepare() {}

    @Override
    public MixinEnvironment.Phase getInitialPhase() {
        return null;
    }

    @Override
    public void offer(IMixinInternal iMixinInternal) {

    }

    @Override public void init() {}
    @Override public void beginPhase() {}

    @Override
    public void checkEnv(Object o) {

    }

    @Override public String getSideName() { return "CLIENT"; }

    @Override
    public MixinEnvironment.CompatibilityLevel getMinCompatibilityLevel() {
        return null;
    }

    @Override
    public MixinEnvironment.CompatibilityLevel getMaxCompatibilityLevel() {
        return null;
    }

    @Override
    public ILogger getLogger(String s) {
        return Launcher.LOGGER;
    }

    @Override public ReEntranceLock getReEntranceLock() { return lock; }
    @Override public Collection<String> getPlatformAgents() { return Collections.emptyList(); }

    @Override
    public IContainerHandle getPrimaryContainer() {
        return primaryContainer;
    }

    @Override
    public Collection<IContainerHandle> getMixinContainers() {
        return List.of();
    }

    @Override public ITransformerProvider getTransformerProvider() { return null; }

    @Override
    public IClassTracker getClassTracker() {
        return null;
    }

    @Override
    public IMixinAuditTrail getAuditTrail() {
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
    }

    @Override
    public IClassProvider getClassProvider() {
        return ClassProvider.getInstance(); //wtf
        //return null;
    }
    @Override
    public IClassBytecodeProvider getBytecodeProvider(){
        return BytecodeProvider.getInstance();
    }

    private final IContainerHandle primaryContainer = new IContainerHandle() {
        @Override
        public Collection<IContainerHandle> getNestedContainers() {
            return Collections.emptyList(); // Ключевой момент: возвращаем пустой список вместо null
        }

        @Override
        public String getAttribute(String name) { return null; }
    };
}

