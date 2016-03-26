package com.colonelhedgehog.lavacontrol.core;


import javax.swing.*;
import java.io.*;

/**
 * Created by ColonelHedgehog on 1/8/15.
 * You have freedom to modify given sources. Please credit me as original author.
 * Keep in mind that this is not for sale.
 */
public class ProcessThread extends Thread
{
    private ProcessBuilder pb;
    private Process p;
    private JTextField jarPath;
    private String file;
    private int mem;
    private boolean ssh;

    public ProcessThread(String file, JTextField jarPath, int mem, boolean ssh)
    {
        this.file = file;
        this.jarPath = jarPath;
        this.mem = mem;
        this.ssh = ssh;
    }

    @Override
    public void run()
    {

        BufferedReader br = null;
        BufferedWriter bw = null;
        while (!Thread.interrupted())
        {
            //input.close();
            //Main.mainGUI.launchJar.setToolTipText("Another instance of your server is running. Be aware, clicking this may fail to bind the port.");
            //System.out.println("Thread begin.");
            try
            {
                File path = new File(jarPath.getText());

                String[] commands;
                if (Float.parseFloat(System.getProperty("java.class.version")) >= 52.0)
                {
                    commands = new String[]{"java", "-Xmx" + Main.getSettings().getMemBash() + "M", "-jar", new File(Main.mainGUI.getJarPath().getText()).getName().replace(" ", "\\ "), "-o true"};
                }
                else
                {
                    commands = new String[]{"java", "-XX:MaxPermSize=128M", "-Xmx" + Main.prefsGUI.getMemoryBash() + "M", "-jar", new File(Main.mainGUI.getJarPath().getText()).getName().replace(" ", "\\ "), "-o true"};
                }
                String commandstr = "java -XX:MaxPermSize=128M -Xmx" + Main.getSettings().getMemBash() + "M " + "-jar " + new File(Main.mainGUI.getJarPath().getText()).getAbsolutePath().replace(" ", "\\ ") + " -o true";
                //System.out.println("Commands: " + Arrays.toString(commands));
                //System.out.println("Command: " + commandstr);
                System.out.println("Launching " + Main.mainGUI.getJarPath().getText() + " with " + Main.getSettings().getMemBash() + "MBs of memory.");

                if (ssh)
                {

                    String host = Main.getSettings().getSSHHost();
                    String username = Main.getSettings().getSSHUsername();
                    String password = Main.getSettings().getSSHPassword();
                    String passwordMask = "";

                    for (int i = 0; i < password.length(); i++)
                    {
                        passwordMask += "*";
                    }

                    System.out.println(Main.Prefix + "Initiating SSH connection.");
                    System.out.println("- Host IP: " + host);
                    System.out.println("- Username: " + username);

                    System.out.println("- Password: " + passwordMask);

                    /*JSch jsch = new JSch();
                    Session session = jsch.getSession(username, host);
                    JSch.setConfig("StrictHostKeyChecking", "no");
                    session.setPassword(password);
                    session.connect(1000);

                    channel = session.openChannel("shell");
                    channel.connect(3000);

                    br = new BufferedReader(new InputStreamReader(channel.getInputStream()));
                    bw = new BufferedWriter(new OutputStreamWriter(channel.getOutputStream()));

                    Main.consoleGUI.createConsoleInput(bw);
                    channel.connect();
                    bw.write(commandstr + "\r\n");
                    bw.flush();

                    String s;
                    while ((s = br.readLine()) != null)
                    {
                        System.out.println(s);
                    }

                    channel.disconnect();
                    System.out.println(Main.Prefix + "SSH session ended.");
                    channel.getInputStream().close();
                    channel.getOutputStream().close();*/
                }
                else
                {
                    pb = new ProcessBuilder(commands);
                    //System.out.println("Process Builder (no SSH!) created.");
                    // pb.command("cd \"$( dirname \"$0\" )\"\n");
                    //pb.command((!System.getProperty("java.version").startsWith("1.8") ? "-XX:MaxPermSize=128M" : "") + " -Xmx" + Main.mainGUI.memoryBash.getText() + "M -jar " + Main.mainGUI.jarPath.getText().replace(" ", "\\ ") + " -o true");
                    p = pb.start(); //pb.command(new String[]{"sh", jar.getParent() + "/LavaControl/.temp/lavacontrol_launch.sh"}).start();
                    //System.out.println("Process started.");

                    pb.directory(path.getParentFile());
                    //System.out.println("PB directory registered created.");


                    //String[] commands = {"java", "-Xmx" + mem + "M", "-XX:MaxPermSize=128M", "-jar", path.getAbsolutePath(), "-o", "true"};
                    //System.out.println(Main.Prefix + "Launching " + file + ". Using " + mem + " megabytes of memory. ");


                    //pb.redirectErrorStream(true);
                    Process p = pb.start();
                    String s;
                    BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    BufferedWriter stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
                    Main.consoleGUI.createConsoleInput(stdin);
                    Main.mainGUI.setProcess(p);

                    while ((s = stdout.readLine()) != null)
                    {
                        if(Main.consoleGUI.getNextWillBeError())
                        {
                            ErrorDetection.determineErrorQueues(s);
                            Main.consoleGUI.setNextWillBeError(false);
                        }
                        Main.consoleGUI.determineQueues(s);
                        System.out.println(s);
                    }

                    try
                    {
                        p.waitFor();

                        p.getInputStream().close();
                        p.getOutputStream().close();
                        p.getErrorStream().close();
                    }
                    catch (InterruptedException | IOException ioe)
                    {
                        ioe.printStackTrace();
                    }

                    p.destroy();

                    Thread.currentThread().interrupt();
                    Main.consoleGUI.getConsoleThread().interrupt();
                }
            }
            catch (IOException e)
            {
                System.out.println(Main.Prefix + "ERROR: Couldn't launch " + file + "! Are any of the arguments incorrect?");
                e.printStackTrace();
            }
        }

        Main.consoleGUI.determineQueues("[LavaControl] --Stopped");
        Main.consoleGUI.disableWindow();
        Main.mainGUI.getLaunchJar().setToolTipText("");
    }
}
