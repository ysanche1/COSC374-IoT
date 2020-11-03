package gateway_simulation;

public class processing {
    static void processLong()
    {
        try{
            Thread.sleep(500);
        }catch(Exception e){}
    }

    static void processMed()
    {
        try{
            Thread.sleep(150);
    }catch(Exception e){}
    }

    static void processFast()
    {
        try{
            Thread.sleep(70);
        }catch(Exception e){}
    }
}
