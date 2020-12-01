package gateway_simulation;

// While not an extension of DeviceCloud, represents cloud-based gateway services

public class GatewayCloud{
    String[] deviceFunctions = new String[]{"sendEvent(name: set_target_temp value: increase)","sendEvent(name: set_target_temp value: decrease)",
            "sendEvent(name: set_target_temp value: custom)", "sendEvent(name: lock, value: unlocked, isStateChange: true, displayed: true)"};
    String[] threatAssessment = new String[]{"OK","OK","OK","WARNING"};
    public GatewayCloud(){

    }

    public String[] evaluate(String m) {
        System.out.println("***** SECURITY EVALUATION IN PROGRESS *****\n");
        String[] evaluation = new String[2];
        evaluation[0] = m;
            for(int i = 0; i < deviceFunctions.length; i++) {
                if (m.equals(deviceFunctions[i]))
                    if (threatAssessment[i].equals("WARNING")) {
                        evaluation[1] = "WARNING";
                        return evaluation;
                    }
            }
            evaluation[1] = "SUSPICIOUS";
            return evaluation;
        }

}