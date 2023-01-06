import java.io.*;
import java.util.ArrayList;
import java.util.Date;

import static java.lang.Thread.sleep;


public class Main {
    private static long SLEEP_INTERVAL = 100;


    public static void main(String args[]) throws IOException, InterruptedException {

       // Author author = new Author("caios2978", "SeyongPark", "https://github.com/sey2/Hango-java/issues/1#issue-1370906209");
       // Message message = new Message("MessageID", "오늘은 뭐해?", new Date(2023,01,07), author);

        //ArrayList<Message> list = new ArrayList<>();

        //for(int i=0; i<1000000000; i++) list.add(message);

       // System.out.println("list all add");

        //Thread.sleep(5000);


        //System.out.println(Main.getObjectSize(Message.class) + " Byte");

        size();



    }

    public static long getObjectSize(Class<?> aClass){
        long result = 0;

        //if the class does not have a no-argument constructor, then
        //inform the user and return 0.
        try {
            aClass.getConstructor(new Class[]{});
        }
        catch (NoSuchMethodException ex) {
            System.out.println(aClass + " does not have a no-argument constructor.");
            return result;
        }

        //this array will simply hold a bunch of references, such that
        //the objects cannot be garbage-collected
        Object[] objects = new Object[100];

        //build a bunch of identical objects
        try {
            Object throwAway = aClass.getDeclaredConstructor().newInstance();

            long startMemoryUse = getMemoryUse();
            for (int idx=0; idx < objects.length ; ++idx) {
                objects[idx] = aClass.getDeclaredConstructor().newInstance();
            }
            long endMemoryUse = getMemoryUse();

            float approximateSize = (endMemoryUse - startMemoryUse)/100f;
            result = Math.round(approximateSize);
        }
        catch (Exception ex) {
            System.out.println("Cannot create object using " + aClass);
        }
        return result;
    }

    private static long getMemoryUse(){
        putOutTheGarbage();
        long totalMemory = Runtime.getRuntime().totalMemory();
        putOutTheGarbage();
        long freeMemory = Runtime.getRuntime().freeMemory();
        return (totalMemory - freeMemory);
    }

    private static void putOutTheGarbage() {
        collectGarbage();
        collectGarbage();
    }

    private static void collectGarbage() {
        try {
            System.gc();
            Thread.currentThread().sleep(SLEEP_INTERVAL);
            System.runFinalization();
            Thread.currentThread().sleep(SLEEP_INTERVAL);
        }
        catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    public static void size() {
        System.gc();
        Runtime r = Runtime.getRuntime();
        System.out.println("전체 힙 메모리 " + r.totalMemory());
        long firstMemorySize = r.freeMemory();
        System.out.println("가용한 힙 메모리 " + firstMemorySize);

        //Message[] o = new Message[10000];
        Author author = new Author("caios2978", "SeyongPark", "https://github.com/sey2/Hango-java/issues/1#issue-1370906209");
        Message message = new Message("MessageID", "오늘은 뭐해?", new Date(2023,01,07), author);
        ArrayList<Message> list = new ArrayList<>();

        for(int i = 0 ; i < 150000000; i++){
            list.add(message);
        }
        long lastMemorySize = r.freeMemory();
        System.out.println("객체 생성 후 가용한 힙 메모리 " + lastMemorySize);
        System.out.println("Object 객체 하나의 메모리 크기는 " + (firstMemorySize - lastMemorySize)/10000);
    }

}
