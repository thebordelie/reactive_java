package ru.itmo.reactivejava.shared.config;

public final class DelayConfig {
    private static volatile boolean enabled = true;

    private DelayConfig() {}

    public static void setEnabled(boolean enabled) {
        DelayConfig.enabled = enabled;
    }

    public static boolean isEnabled() {
        return enabled;
    }
}
