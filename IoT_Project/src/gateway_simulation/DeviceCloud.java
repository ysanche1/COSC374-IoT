package gateway_simulation;

public abstract class DeviceCloud {
    Boolean cloudOnline = true;
    int targetTemp;
    int currentTemp;
    public abstract void soundOff();

    public void setTemperature(int targetTemp) throws InterruptedException {
        this.targetTemp = targetTemp;
        soundOff();
    }

    public void increase() {
        targetTemp++;
        soundOff();
    }
    public void decrease() {
        targetTemp--;
        soundOff();
    }
}
