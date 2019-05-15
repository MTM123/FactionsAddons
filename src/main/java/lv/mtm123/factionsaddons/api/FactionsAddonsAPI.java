package lv.mtm123.factionsaddons.api;

/**
 * Provides API for FactionsAddons
 */
public interface FactionsAddonsAPI {

    /**
     * Returns SpawnerManager
     *
     * @return SpawnerManager if present, null otherwise. Use FactionsAddonsAPI#isModuleEnabled() to check.
     */
    SpawnerManager getSpawnerManager();

    /**
     * Returns PlayerSettingsManager
     *
     * @return PlayerSettingsManager
     */
    PlayerSettingsManager getPlayerSettingsManager();

    /**
     * Returns if specified module is enabled
     *
     * @param module Module to check
     * @return Returns true if module is enabled
     */
    boolean isModuleEnabled(Module module);

}
