package gateway_simulation;

// Represents vendor cloud associated with thermostat.

public class ThermostatCloud extends DeviceCloud {
    public void soundOff(){
        System.out.println("Request handled by thermostat cloud");
    }
}