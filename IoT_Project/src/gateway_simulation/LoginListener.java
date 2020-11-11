package gateway_simulation;

public class LoginListener extends Thread{
Message message;

public LoginListener(Message message) {
    this.message = message;
}
    public void run() {
        System.out.println("GATEAWAY THREAD RUNNING");
        try {
            while (message.mNum != 5)
                join();
            message.displayContents();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
