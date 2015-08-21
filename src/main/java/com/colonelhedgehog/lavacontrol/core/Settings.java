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
    private int memBash;
    private int maxConsoleLines;
    private boolean sshEnabled;
    private String sshUsername;
    private String sshPassword;
    private String sshHost;
    private String lastPath;

    private boolean stickyScrollBar;
    private boolean closeWindowOnStop;
    private boolean askExportLog;

    public Settings()
    {

    }

    public void saveSettings()
    {
        if(Main.mainGUI == null)
        {
            return; // One of them fine.
        }

        File f = new File(System.getProperty("user.home") + "/Library/Lava Control/settings.ccq");
        f.delete();

        f.getParentFile().mkdirs();
        try
        {
            f.createNewFile();
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(Main.menuFrame, "FATAL: Unable to create settings file at \"" + f.getAbsolutePath() + "\"!", "Failed to Create Settings", JOptionPane.INFORMATION_MESSAGE, Main.getIcon());
            return;
        }

        System.out.println("[Lava Control] Created a new settings file in: \"" + f.getAbsolutePath() + "\"");


        BufferedWriter bw;
        try
        {
            bw = new BufferedWriter(new FileWriter(f));

            bw.write("$memory = " + memBash);
            bw.write("\n$path = " + lastPath);
            bw.write("\n$maxconsolelines = " + maxConsoleLines);
            bw.write("\n$sshenabled = " + sshEnabled);
            bw.write("\n$sshusername = " + sshUsername);
            bw.write("\n$sshhost = " + sshHost);
            bw.write("\n$stickyscrollbar = " + stickyScrollBar);
            bw.write("\n$closewindowonstop = " + closeWindowOnStop);
            bw.write("\n$askexportlog = " + askExportLog);
            bw.write("\n$sshpassword = " + Main.getSettings().getSSHPassword());

            bw.flush();
            bw.close();
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(Main.menuFrame, "FATAL: Unable to save settings at \"" + f.toString() + "\"!", "Failed to Save Settings", JOptionPane.INFORMATION_MESSAGE, Main.getIcon());
            e.printStackTrace();
        }
    }

    public void reloadSettings()
    {
        memBash = 512;
        maxConsoleLines = 100;
        sshUsername = "Username";
        sshHost = "Host";
        sshPassword= "";
        sshEnabled = false;
        lastPath = "/";
        stickyScrollBar = true;
        closeWindowOnStop = false;
        askExportLog = true;

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
                            memBash = Integer.parseInt(line.replace("$memory = ", ""));
                        }
                        catch(NumberFormatException nfe)
                        {
                            System.out.println("[Lava Control] Strange error occurred. $memory was not an integer. Reverting to defaults.");
                        }
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
                    else if (line.startsWith("$sshhost = "))
                    {
                        sshHost = line.replace("$sshhost = ", "");
                    }
                    else if(line.startsWith("$path = "))
                    {
                        lastPath = line.replace("$path = ", "");
                    }
                    else if(line.startsWith("$stickyscrollbar = "))
                    {
                        stickyScrollBar = Boolean.parseBoolean(line.replace("$stickyscrollbar = ", ""));
                    }
                    else if(line.startsWith("$closewindowonstop = "))
                    {
                        closeWindowOnStop = Boolean.parseBoolean(line.replace("$closewindowonstop = ", ""));
                    }
                    else if(line.startsWith("$askexportlog = "))
                    {
                        askExportLog = Boolean.parseBoolean(line.replace("$askexportlog = ", ""));
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public int getMemBash()
    {
        return memBash;
    }

    public int getMaxConsoleLines()
    {
        return maxConsoleLines;
    }

    public boolean getSSHEnabled()
    {
        return sshEnabled;
    }

    public String getSSHUsername()
    {
        return sshUsername;
    }

    public String getSSHPassword()
    {
        return sshPassword;
    }

    public String getSSHHost()
    {
        return sshHost;
    }

    public String getLastPath()
    {
        return lastPath;
    }

    public boolean getStickyScrollBar()
    {
        return stickyScrollBar;
    }

    public boolean getCloseWindowOnStop()
    {
        return closeWindowOnStop;
    }

    public boolean getAskExportLog()
    {
        return askExportLog;
    }

    public void setMemBash(int memBash)
    {
        this.memBash = memBash;
    }

    public void setMaxConsoleLines(int maxConsoleLines)
    {
        this.maxConsoleLines = maxConsoleLines;
    }

    public void setSSHEnabled(boolean sshEnabled)
    {
        this.sshEnabled = sshEnabled;
    }

    public void setSSHUsername(String username)
    {
        this.memBash = memBash;
    }

    public void setSSHPassword(String sshPassword)
    {
        this.sshPassword = sshPassword;
    }

    public void setSSHHost(String sshHost)
    {
        this.sshHost = sshHost;
    }

    public void setLastPath(String lastPath)
    {
        this.lastPath = lastPath;
    }

    public void setStickyScrollBar(boolean stickyScrollBar)
    {
        this.stickyScrollBar = stickyScrollBar;
    }

    public void setCloseWindowOnStop(boolean closeWindowOnStop)
    {
        this.closeWindowOnStop = closeWindowOnStop;
    }

    public void setAskExportLog(boolean askExportLog)
    {
        this.askExportLog = askExportLog;
    }

    public void deleteSettings()
    {
        File f = new File(System.getProperty("user.home") + "/Library/Lava Control/settings.ccq");

        if(f.exists())
        {
            f.delete();
        }

        reloadSettings();
    }
}
