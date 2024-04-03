package me.redstoner2019.main;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;

public class LoggerDump {
    public static File errorFile = new File("log/error_dump.log");
    public static File consoleFile = new File("log/console_dump.log");
    public static void initialize() throws Exception{
        if(!new File("log/").exists()) new File("log/").mkdirs();

        if(errorFile.exists()) errorFile.delete();
        if(consoleFile.exists()) consoleFile.delete();

        errorFile.createNewFile();
        consoleFile.createNewFile();

        try{
            PrintStream consoleOut = new PrintStream(System.out);
            PrintStream errorOut = new PrintStream(System.err);

            FileOutputStream logOutStream = new FileOutputStream(errorFile);
            PrintStream logPs = new PrintStream(logOutStream);

            FileOutputStream errorOutStream = new FileOutputStream(consoleFile);
            PrintStream errorPs = new PrintStream(errorOutStream);

            System.setErr(new DoubleWriteStream(errorPs,errorOut,errorOutStream,"[ERROR]",true));

            System.setOut(new DoubleWriteStream(logPs,consoleOut,logOutStream,"[INFO] ",true));
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error init");
            System.exit(0);
        }
        System.out.println("Init complete");
    }
}

class DoubleWriteStream extends PrintStream {
    private PrintStream p1;
    private PrintStream p2;
    private String prefix;
    private boolean timePrefix;
    public DoubleWriteStream(PrintStream p1, PrintStream p2, OutputStream out, String prefix,boolean timePrefix){
        super(out);
        this.p1 = p1;
        this.p2 = p2;
        this.prefix = prefix;
        this.timePrefix = timePrefix;
    }
    public DoubleWriteStream(OutputStream out) {
        super(out);
    }

    public DoubleWriteStream(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public DoubleWriteStream(OutputStream out, boolean autoFlush, String encoding) throws UnsupportedEncodingException {
        super(out, autoFlush, encoding);
    }

    public DoubleWriteStream(OutputStream out, boolean autoFlush, Charset charset) {
        super(out, autoFlush, charset);
    }

    public DoubleWriteStream(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    public DoubleWriteStream(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(fileName, csn);
    }

    public DoubleWriteStream(String fileName, Charset charset) throws IOException {
        super(fileName, charset);
    }

    public DoubleWriteStream(File file) throws FileNotFoundException {
        super(file);
    }

    public DoubleWriteStream(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(file, csn);
    }

    public DoubleWriteStream(File file, Charset charset) throws IOException {
        super(file, charset);
    }
    public static String lastMessage = "";

    @Override
    public void println(int x) {
        println(x + "");
    }

    @Override
    public void println(char x) {
        println(x + "");
    }

    @Override
    public void println(long x) {
        println(x + "");
    }

    @Override
    public void println(float x) {
        println(x + "");
    }

    @Override
    public void println(char[] x) {
        println(Arrays.toString(x) + "");
    }

    @Override
    public void println(double x) {
        println(x + "");
    }

    @Override
    public void println(Object x) {
        println(x + "");
    }

    @Override
    public void println(String x) {
        if(timePrefix){
            p1.println(new Date().toGMTString() + " " + prefix + " " + x);
            p2.println(new Date().toGMTString() + " " + prefix + " " + x);
            //if(!lastMessage.equals(x))
        } else {
            p1.println(x);
            p2.println(x);
        }
        lastMessage = x;
    }

    @Override
    public void println(boolean x) {
        println(x + "");
    }
}

