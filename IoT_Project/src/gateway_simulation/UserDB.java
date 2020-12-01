package gateway_simulation;

public class UserDB {
    String[] usernames = new String[]{"adam", "ben", "yaneli"};
    String[] passwords = new String[]{"pass1", "aaaa", "pass3"};


    public String checkUserDB(String m) {
        System.out.println("    ******* CHECKING USER DATABASE *******\n");
        for (int i = 0; i < usernames.length; i++) {
            if (m.equals(usernames[i]))
                return passwords[i];
        }
        return "ERROR";
    }
}