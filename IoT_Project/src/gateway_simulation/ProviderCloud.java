package gateway_simulation;

// Represents vendor cloud associated with thermostat.

public class ProviderCloud extends DeviceCloud {
    public void soundOff(){
        System.out.println("  Request handled by provider cloud\n");
    }
}