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
    public ProcessBuilder pb;
    public Process p;
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

        while(!Thread.interrupted())
        {
            try
            {
                //input.close();
                Main.mainGUI.launchJar.setToolTipText("Another instance of your server is running. Be aware, clicking this may fail to bind the port.");
                //System.out.println("Thread begin.");
                try
                {
                    File path = new File(jarPath.getText());

                    String[] commands;
                    if(!System.getProperty("java.version").startsWith("1.8"))
                    {
                        commands = new String[]{"java", "-Xmx" + Main.mainGUI.memoryBash.getText() + "M", "-jar", new File(Main.mainGUI.jarPath.getText()).getName().replace(" ", "\\ "), "-o true"};
                    }
                    else
                    {
                        commands = new String[]{"java", "-XX:MaxPermSize=128M", "-Xmx" + Main.mainGUI.memoryBash.getText() + "M", "-jar", new File(Main.mainGUI.jarPath.getText()).getName().replace(" ", "\\ "), "-o true"};
                    }
                    String commandstr = "java " + (!System.getProperty("java.version").startsWith("1.8") ? "-XX:MaxPermSize=128M" : "") + " -Xmx" + Main.mainGUI.memoryBash.getText() + "M " + "-jar " + new File(Main.mainGUI.jarPath.getText()).getAbsolutePath().replace(" ", "\\ ") + " -o true";
                    //System.out.println("Commands: " + Arrays.toString(commands));
                    //System.out.println("Command: " + commandstr);
                    System.out.println("Launching " + Main.mainGUI.jarPath.getText() + " with " + Main.mainGUI.memoryBash.getText() + "MBs of memory availible.");

                    if(ssh)
                    {
                        String[] sshcommand = {"ssh", "-t", "-t", Main.mainGUI.sshUsername.getText() + "@" + Main.mainGUI.sshHost.getText()};
                        //System.out.println("SSH! SSH Command: " + Arrays.toString(sshcommand));

                        pb = new ProcessBuilder(sshcommand);
                        //System.out.println("ProcessBuilder SSH Command made.");
                        String stuff = "";

                        for (char c : Main.mainGUI.sshPassword.getPassword())
                        {
                            stuff = stuff.concat(String.valueOf(c));
                        }

                        p = pb.start();

                        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        br.read(Main.mainGUI.sshPassword.getPassword());
                        br.read(commandstr.toCharArray());
                        br.close();

                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
                        bw.write(stuff);
                        bw.write(commandstr);
                        bw.flush();
                        bw.close();
                        //System.out.println("Process started.");
                        //System.out.println("Printing password: " + stuff);
                        //System.out.println(stuff);
                        //System.out.println("Printing command: " + commandstr);
                        //System.out.println(commandstr);
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
                    }

                    //String[] commands = {"java", "-Xmx" + mem + "M", "-XX:MaxPermSize=128M", "-jar", path.getAbsolutePath(), "-o", "true"};
                    //System.out.println("[Lava Control] Launching " + file + ". Using " + mem + " megabytes of memory. ");
                }
                catch (IOException e)
                {
                    System.out.println("[Lava Control] ERROR: Couldn't launch " + file + "! Are any of the arguments incorrect?");
                    e.printStackTrace();
                }

                pb.redirectErrorStream(true);
                Process p = pb.start();
                String s;
                BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
                BufferedWriter stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
                Main.consoleGUI.createConsoleInput(stdin);
                Main.mainGUI.p = p;

                while ((s = stdout.readLine()) != null)
                {
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
                Main.consoleGUI.disableWindow();
                this.interrupt();
                Main.consoleGUI.consoleThread.interrupt();
                Main.mainGUI.launchJar.setToolTipText("");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
