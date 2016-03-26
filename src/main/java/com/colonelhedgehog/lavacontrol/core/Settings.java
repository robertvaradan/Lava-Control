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
    // MAIN GUI
    private int memBash;
    private int maxConsoleLines;
    private boolean sshEnabled;
    private String sshUsername;
    private String sshPassword;
    private String sshHost;
    private String lastPath;

    // CONSOLE GUI
    private boolean stickyScrollBar;
    private boolean closeWindowOnStop;
    private boolean askExportLog;

    // BUILD GUI
    private boolean saveJarToPath;
    private boolean generateDocs;
    private boolean generateSources;
    private String buildVersion;

    public Settings()
    {

    }

    public void saveSettings()
    {
        File old = new File(System.getProperty("user.home") + "/Library/Lava Control/");

        if (old.exists())
        {
            old.renameTo(new File(old.getParentFile() + "/LavaControl/"));
        }

        if (Main.mainGUI == null)
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
            JOptionPane.showMessageDialog(Main.menuFrame, "FATAL: Unable to create settings file at \"" + f.getAbsolutePath() + "\"!", "Failed to Create Settings", JOptionPane.INFORMATION_MESSAGE, Main.getIcon());
            return;
        }

        if (Main.consoleFrame == null || !Main.consoleFrame.isVisible())
        {
            System.out.println(Main.Prefix + "Created a new settings file in: \"" + f.getAbsolutePath() + "\"");
        }


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
            bw.write("\n$sshpassword = " + sshPassword);
            bw.write("\n$savejarpath = " + saveJarToPath);
            bw.write("\n$generatedocs = " + generateDocs);
            bw.write("\n$generatesources = " + generateSources);
            bw.write("\n$buildversion = " + buildVersion);

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
        sshPassword = "";
        sshEnabled = false;
        lastPath = "/";

        stickyScrollBar = true;
        closeWindowOnStop = false;
        askExportLog = true;

        saveJarToPath = true;
        generateDocs = false;
        generateSources = false;
        buildVersion = "";

        File f = new File(System.getProperty("user.home") + "/Library/LavaControl/settings.ccq");
        if (f.exists())
        {
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(f));
                String line;
                while ((line = br.readLine()) != null)
                {
                    if (line.startsWith("$memory = "))
                    {
                        try
                        {
                            memBash = Integer.parseInt(line.replace("$memory = ", ""));
                        }
                        catch (NumberFormatException nfe)
                        {
                            System.out.println(Main.Prefix + "Strange error occurred. $memory was not an integer. Reverting to defaults.");
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
                    else if (line.startsWith("$path = "))
                    {
                        lastPath = line.replace("$path = ", "");
                    }
                    else if (line.startsWith("$stickyscrollbar = "))
                    {
                        stickyScrollBar = Boolean.parseBoolean(line.replace("$stickyscrollbar = ", ""));
                    }
                    else if (line.startsWith("$closewindowonstop = "))
                    {
                        closeWindowOnStop = Boolean.parseBoolean(line.replace("$closewindowonstop = ", ""));
                    }
                    else if (line.startsWith("$askexportlog = "))
                    {
                        askExportLog = Boolean.parseBoolean(line.replace("$askexportlog = ", ""));
                    }
                    else if (line.startsWith("$savejarpath = "))
                    {
                        saveJarToPath = Boolean.parseBoolean(line.replace("$savejarpath = ", ""));
                    }
                    else if (line.startsWith("$generatedocs = "))
                    {
                        generateDocs = Boolean.parseBoolean(line.replace("$generatedocs = ", ""));
                    }
                    else if (line.startsWith("$generatesources = "))
                    {
                        generateSources = Boolean.parseBoolean(line.replace("$generatesources = ", ""));
                    }
                    else if (line.startsWith("$buildversion = "))
                    {
                        buildVersion = line.replace("$buildversion = ", "");
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

    public boolean saveJarToPath()
    {
        return saveJarToPath;
    }

    public boolean generateDocs()
    {
        return generateDocs;
    }

    public boolean generateSources()
    {
        return generateSources;
    }

    public String getBuildVersion()
    {
        return buildVersion;
    }

    public void deleteSettings()
    {
        File f = new File(System.getProperty("user.home") + "/Library/LavaControl/settings.ccq");

        if (f.exists())
        {
            f.delete();
        }

        reloadSettings();
    }

    public void setGenerateDocs(boolean generateDocs)
    {
        this.generateDocs = generateDocs;
    }

    public void setGenerateSources(boolean generateSources)
    {
        this.generateSources = generateSources;
    }

    public void setBuildVersion(String buildVersion)
    {
        this.buildVersion = buildVersion;
    }

    public void setSaveJarToPath(boolean saveJarToPath)
    {
        this.saveJarToPath = saveJarToPath;
    }
}
