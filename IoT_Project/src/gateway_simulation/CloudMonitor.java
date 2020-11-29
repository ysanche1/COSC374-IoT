package gateway_simulation;

public class CloudMonitor implements Runnable{
    CloudBackup cloudBackup;
    Thread t;
    public CloudMonitor(CloudBackup cb){
       cloudBackup = cb;

    }
    public void monitor(){
        t = new Thread(this);
        t.start();
    }

    private void checkCloudStatus() {
        if(Main.gateway.loggedIn) {
            if (Main.tcCloud.cloudOnline) {
                Main.tc.thermostat.activeCloud = Main.tcCloud;
                Main.tc.setNotification("Cloud Backup: Inactive");
            } else {
                cloudBackup.targetTemp = Main.tc.thermostat.activeCloud.targetTemp;
                Main.tc.thermostat.activeCloud = cloudBackup;
                Main.tc.setNotification("Cloud Backup: Active");
            }
        }
    }

    @Override
    public void run() {
        while(true){
            try {
                checkCloudStatus();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
