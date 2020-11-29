package gateway_simulation;

public class UserDB {
    String[] usernames = new String[]{"apollack", "bstobby", "ysanchez"};
    String[] passwords = new String[]{"password", "passphrase", "passcode"};


    public String checkUserDB(String m) {
        System.out.println("**************CHECKING USER DATABASE*************\n");
        for (int i = 0; i < usernames.length; i++) {
            if (m.equals(usernames[i]))
                return passwords[i];
        }
        return "ERROR";
    }
}