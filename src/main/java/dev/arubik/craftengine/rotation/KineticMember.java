package dev.arubik.craftengine.rotation;

/**
 * Shared interface for any machine that participates in a kinetic (RPM/SU) network.
 * Implemented by DataMachineBlockEntity (and any future kinetic machine).
 */
public interface KineticMember {
    long rpmNetworkId();
    void setRpmNetworkId(long id);
    void joinNetwork(long networkId);
    void leaveNetwork();
    /** Report SU contribution: negative = generates capacity, positive = consumes. */
    void reportSuToNetwork(float su);
    /** Called by the network when overstress state changes. */
    void onNetworkOverstressChanged(boolean overStressed);
    float getRpm();
}
