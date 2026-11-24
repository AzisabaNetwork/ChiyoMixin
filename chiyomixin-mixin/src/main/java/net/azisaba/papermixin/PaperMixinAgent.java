package net.azisaba.papermixin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.nio.file.Paths;

public class PaperMixinAgent {
    static {
        System.setProperty("org.slf4j.simpleLogger.logFile", "System.out");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(PaperMixinAgent.class);

    // ChiyoMixin - set -Dchiyomixin.mixin.disable=true to start the server without Mixin
    public static final String DISABLE_PROPERTY = "chiyomixin.mixin.disable";
    private static boolean started = false;

    /**
     * Entry point when started with {@code -javaagent:<jar>}.
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        start(agentArgs);
    }

    /**
     * Entry point when started with {@code java -jar <chiyomixin jar>} (Launcher-Agent-Class).
     */
    public static void agentmain(String agentArgs, Instrumentation inst) {
        start(agentArgs);
    }

    private static synchronized void start(String agentArgs) {
        if (started) {
            // the jar is both the launcher and a -javaagent; only initialize once
            return;
        }
        started = true;
        if (Boolean.getBoolean(DISABLE_PROPERTY)) {
            LOGGER.info("Mixin is disabled by -D{}=true", DISABLE_PROPERTY);
            return;
        }
        if (agentArgs != null && !agentArgs.isEmpty()) {
            MixinLoader.applicationClass = agentArgs;
        }
        MixinLoader.load(EnvironmentType.AGENT, LOGGER, Paths.get(".papermixin"));
        initEnv();
    }

    public static void initEnv() {
        try {
            // Prevent loading of classes until we are ready
            Class.forName("net.azisaba.papermixin.PaperMixinEnvironment")
                    .getConstructor(EnvironmentType.class, Logger.class)
                    .newInstance(EnvironmentType.AGENT, LOGGER);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to initialize Mixin", e);
        }
    }
}
