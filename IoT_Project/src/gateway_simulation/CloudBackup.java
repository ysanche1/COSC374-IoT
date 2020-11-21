package gateway_simulation;

public class CloudBackup extends DeviceCloud {
    @Override
        public void soundOff(){
            System.out.println("Request handled by cloud backup");
        }
}
