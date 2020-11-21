package gateway_simulation;

public class GatewayCloud{
    String[] deviceFunctions = new String[]{"INCREASE","DECREASE","CUSTOM", "UNLOCK DOOR"};
    String[] threatAssessment = new String[]{"OK","OK","OK","WARNING"};
    public GatewayCloud(){

    }

    public String[] evaluate(String m) {
        System.out.println("\n**************CLOUD EVALUATION IN PROGRESS*************");
        String[] evaluation = new String[2];
        evaluation[0] = m;
            for(int i = 0; i <= deviceFunctions.length; i++) {
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