package lv.mtm123.factionsaddons.api;

/**
 * Represents player settings
 */
public interface PlayerSettings {

    /**
     * Checks if TNT is visible for player
     * @return True if TNT is visible for player
     */
    boolean isTNTVisible();

    /**
     * Sets visibility of TNT
     * @param visible Whether to make TNT visible
     */
    void setTNTVisible(boolean visible);

    /**
     * Checks if player has enderpearl cooldown
     * @return True if player has enderpearl cooldown
     */
    boolean hasEnderpearlCooldown();

    /**
     * Returns time left till cooldown ends
     * @return Timeleft in miliseconds
     */
    long getEnderpearlCooldownLeft();

    /**
     * Sets cooldown until certain time
     * @param cooldown Timestamp of when cooldown ends. Values lower than current timestamp will remove cooldown
     */
    void setEnderpearlCooldownUntil(long cooldown);

}
