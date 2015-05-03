package com.colonelhedgehog.lavacontrol.core;

import javax.swing.*;
import java.io.*;

/**
 * Created by ColonelHedgehog on 12/30/14.
 * You have freedom to modify given sources. Please credit me as original author.
 * Keep in mind that this is not for sale.
 */
public class Settings
{
    public enum Field
    {
        MEM_BASH, MAX_CONSOLE_LINES, SSH_ENABLED, SSH_USERNAME, SSH_PASSWORD, SSH_HOST, LAST_PATH
    }

    public Settings()
    {

    }

    public void saveSettings()
    {
        if(Main.mainGUI == null)
        {
            return; // One of them fine.
        }

        File f = new File(System.getProperty("user.home") + "/Library/LavaControl/settings.ccq");
        f.delete();

        f.getParentFile().mkdirs();
        try
        {
            f.createNewFile();
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(Main.menuFrame, "FATAL: Unable to create settings file at \"" + f.getAbsolutePath() + "\"!");
            return;
        }

        System.out.println("[Lava Control] Created a new settings file in: \"" + f.getAbsolutePath() + "\"");


        BufferedWriter bw;
        try
        {
            bw = new BufferedWriter(new FileWriter(f));
            int bash = 0;

            try
            {
                bash = Integer.parseInt(Main.mainGUI.memoryBash.getText());
            }
            catch (NumberFormatException nfe)
            {

            }

            bw.write("$memory = " + bash);
            bw.write("\n$path = " + Main.mainGUI.jarPath.getText());
            bw.write("\n$maxconsolelines = " + Main.mainGUI.maxConsoleLines.getText());
            bw.write("\n$sshenabled = " + Main.mainGUI.sshEnabled.isSelected());
            bw.write("\n$sshusername = " + Main.mainGUI.sshUsername.getText());
            bw.write("\n$sshhost = " + Main.mainGUI.sshHost.getText());
            String p = "";

            for(char c : Main.mainGUI.sshPassword.getPassword())
            {
                p.concat(String.valueOf(c));
            }

            bw.write("\n$sshpassword = " + p);

            bw.flush();
            bw.close();
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(Main.menuFrame, "FATAL: Unable to save settings at \"" + f.toString() + "\"!");
            e.printStackTrace();
        }
    }

    public Object getField(Field fi)
    {
        int mem = 512;
        String path = "/";
        int maxConsoleLines = 100;
        String sshUsername = "Username";
        String sshHost = "Host";
        String sshPassword = "";
        boolean sshEnabled = false;

        File f = new File(System.getProperty("user.home") + "/Library/Lava Control/settings.ccq");
        if (f.exists())
        {
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(f));
                String line;
                while((line = br.readLine()) != null)
                {
                    if(line.startsWith("$memory = "))
                    {
                        try
                        {
                            mem = Integer.parseInt(line.replace("$memory = ", ""));
                        }
                        catch(NumberFormatException nfe)
                        {
                            System.out.println("[Lava Control] Strange error occurred. $memory was not an integer. Reverting to defaults.");
                        }
                    }
                    else if(line.startsWith("$path = "))
                    {
                        path = line.replace("$path = ", "");
                    }
                    else if (line.startsWith("$maxconsolelines = "))
                    {
                        maxConsoleLines = Integer.parseInt(line.replace("$maxconsolelines = ", ""));
                    }
                    else if (line.startsWith("$sshenabled = "))
                    {
                        sshEnabled = Boolean.parseBoolean(line.replace("$sshenabled = ", ""));
                    }
                    else if (line.startsWith("$sshusername = "))
                    {
                        sshUsername = line.replace("$sshusername = ", "");
                    }
                    else if (line.startsWith("$sshpassword = "))
                    {
                        sshPassword = line.replace("$sshpassword = ", "");
                    }
                    else if (line.startsWith("$sshhost = "))
                    {
                        sshHost = line.replace("$sshhost = ", "");
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        if(fi == Field.MEM_BASH)
        {
            return mem;
        }
        else if(fi == Field.LAST_PATH)
        {
            return path;
        }
        else if (fi == Field.MAX_CONSOLE_LINES)
        {
            return maxConsoleLines;
        }
        else if (fi == Field.SSH_ENABLED)
        {
            return sshEnabled;
        }
        else if (fi == Field.SSH_USERNAME)
        {
            return sshUsername;
        }
        else if (fi == Field.SSH_PASSWORD)
        {
            return sshPassword;
        }
        else if (fi == Field.SSH_HOST)
        {
            return sshHost;
        }
        else
        {
            return null;
        }
    }
}
