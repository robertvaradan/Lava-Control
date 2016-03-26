package com.colonelhedgehog.lavacontrol.core;

import com.colonelhedgehog.lavacontrol.core.components.RoundedCornerBorder;
import com.colonelhedgehog.lavacontrol.core.components.SmoothJProgressBar;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Lava Control
 * Created by ColonelHedgehog on 3/23/16.
 */
public class BuildGUI
{
    private JPanel mainPanel;
    private JTextArea consoleText;
    private JScrollPane scrollPane;
    private JButton buildJar;

    public JButton getStopBuildProcessButton()
    {
        return stopBuildProcessButton;
    }

    public JButton getPreferencesButton()
    {
        return preferencesButton;
    }

    public SmoothJProgressBar getCommandProgress()
    {
        return commandProgress;
    }

    private JButton stopBuildProcessButton;
    private JButton preferencesButton;
    private SmoothJProgressBar commandProgress;
    private JButton updateBuildToolsButton;

    private Runnable runnable;
    private Thread buildThread;
    private Thread downloadThread;
    private Runnable downloadRunnable;
    private ProcessBuilder processBuilder;
    private Process process;
    private Thread timerThread;

    private MessageConsole messageConsole;

    private static final class Lock
    {
    }

    private final Object lock = new Lock();

    public BuildGUI()
    {
        scrollPane.setBorder(new RoundedCornerBorder(6));

        buildJar.addActionListener(e -> {
            if (!isFileClosed())
            {
                JOptionPane.showMessageDialog(Main.buildFrame, "ERROR: The file you're trying to overwrite is in use.\nPlease close it before building a Spigot Jar.");
                return;
            }

            buildJar();
        });

        stopBuildProcessButton.addActionListener(e -> {

            writeTo("\n\n" + Main.Prefix + "!! Terminated build processes.\n");

            stopBuildProcesses();
        });

        updateBuildToolsButton.addActionListener(e -> generateBuildTools());

        DefaultCaret caret = (DefaultCaret) consoleText.getCaret();

        if (Main.getSettings().getStickyScrollBar())
        {
            caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        }
        else
        {
            caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        }

        consoleText.setCaret(caret);

        preferencesButton.addActionListener(e -> Main.createPrefsGUI(Main.buildFrame));
    }

    private Timer downloadTimer;

    public void runDownloadTimer(final long maxSize)
    {
        Runnable runnable = new Runnable()
        {
            File buildToolsParent = new File(System.getProperty("user.home") + "/Library/LavaControl/BuildBench");
            File buildToolsPath = new File(buildToolsParent + "/BuildTools.jar");

            @Override
            public void run()
            {
                downloadTimer = new Timer(1, e -> {
                    long size = buildToolsPath.length();

                    double dec = ((double) size / maxSize);

                    commandProgress.setValue((int) (dec * 10000));
                    //System.out.println("PERCENT BUILT: " + size + "/" + maxSize + " -> " + (dec * 100) + "%");

                    updateBuildToolsButton.setText("Downloading Jar: " + Math.round(dec * 100) + "%");
                });

                downloadTimer.start();
            }
        };

        timerThread = new Thread(runnable);
        timerThread.start();
    }

    private void generateBuildTools()
    {
        writeTo(Main.Prefix + "Downloading BuildTools. Process initiated...\n");

        updateBuildToolsButton.setEnabled(false);
        buildJar.setEnabled(false);

        downloadRunnable = () -> {
            try
            {
                writeTo(Main.Prefix + "Setting up folders to download BuildTools into.\n");

                File buildToolsParent = new File(System.getProperty("user.home") + "/Library/LavaControl/BuildBench");
                boolean mkdirs = buildToolsParent.mkdirs();

                if (mkdirs)
                {
                    writeTo(Main.Prefix + "Created folder: " + buildToolsParent + "\n");
                }

                File buildToolsPath = new File(buildToolsParent + "/BuildTools.jar");

                boolean delete = buildToolsPath.delete();

                if (delete)
                {
                    writeTo(Main.Prefix + "Removed old BuildTools.jar.\n");
                }

                URL webURL = new URL("https://hub.spigotmc.org/jenkins/job/" +
                        "BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar");

                writeTo(Main.Prefix + "Now establishing connection to " + webURL + "\n");

                HttpURLConnection connection =
                        (HttpURLConnection) webURL.openConnection();

                // Specify what portion of file to download.
                connection.setRequestProperty("Range",
                        "bytes=" + 0 + "-");

                // Connect to server.
                connection.connect();

                writeTo(Main.Prefix + "Successfully connected to " + webURL.getHost() + "...\n");

                final long maxSize = connection.getContentLengthLong();

                runDownloadTimer(maxSize);

                final long startTime = System.nanoTime();
                writeTo(Main.Prefix + "Beginning download of BuildTools.jar...\n");

                // Check for valid content length.
                ReadableByteChannel rbc = Channels.newChannel(webURL.openStream());
                FileOutputStream fos = new FileOutputStream(buildToolsPath);

                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                downloadTimer.stop();

                final long endTime = System.nanoTime();
                writeTo(Main.Prefix + "Completed download (" + ((endTime - startTime) / 1000000000) + "s). Finishing up...\n");

                timerThread.interrupt();
                downloadThread.interrupt();

                timerThread = null;
                downloadTimer = null;
                updateBuildToolsButton.setText("Update BuildTools");

                commandProgress.setValue(0);

                if (buildThread != null)
                {
                    synchronized (lock)
                    {
                        lock.notifyAll();
                    }
                }
                else
                {
                    updateBuildToolsButton.setEnabled(true);
                    updateBuildToolsButton.setText("Update BuildTools");
                    buildJar.setEnabled(true);
                    writeTo("\n============================================================");
                    writeTo("\n\n" + Main.Prefix + "Finished downloading BuildTools to folder: " + buildToolsParent.getAbsolutePath() + "\n");
                }
            }

            catch (IOException e)
            {
                writeTo("\n============================================================");
                writeTo("\n\n" + Main.Prefix + "Failed to download BuildTools. Stopping...");
                JOptionPane.showMessageDialog(Main.buildFrame, "ERROR: Something went wrong while downloading. Check your settings! " +
                        "\n\"" + e.getLocalizedMessage() + "\"" +
                        "\n" + e.getMessage());
                stopBuildProcesses();
                reset();
            }
        };

        downloadThread = new Thread(downloadRunnable);

        downloadThread.start();
    }

    private void buildJar()
    {
        updateBuildToolsButton.setEnabled(false);
        buildJar.setEnabled(false);
        String oldTip = buildJar.getToolTipText();

        buildJar.setToolTipText("You can't build another Spigot Jar while this one is running.");

        messageConsole = new MessageConsole(consoleText);
        messageConsole.redirectOut();
        messageConsole.redirectErr(Color.RED, System.err);
        messageConsole.setMessageLines(Main.getSettings().getMaxConsoleLines());

        runnable = () -> {
            while (!Thread.interrupted())
            {
                try
                {
                    File buildToolsParent = new File(System.getProperty("user.home") + "/Library/LavaControl/BuildBench");
                    buildToolsParent.mkdirs();

                    File buildToolsPath = new File(buildToolsParent + "/BuildTools.jar");

                    if (!buildToolsPath.exists())
                    {
                        writeTo(Main.Prefix + "!! WARNING: No BuildTools Jar could be found. Downloading a new one.\n");
                        generateBuildTools();

                        synchronized (lock)
                        {
                            lock.wait();
                        }

                        writeTo(Main.Prefix + "All done downloading BuildTools Jar. Continuing with Spigot building.\n");
                    }

                    List<String> commands = new ArrayList<>();

                    commands.add("java");
                    commands.add("-jar");
                    commands.add(buildToolsPath.toString());

                    Settings settings = Main.getSettings();

                    String version = settings.getBuildVersion();

                    if (settings.generateDocs())
                    {
                        commands.add("--generate-docs");
                        //commands.add("true");
                    }

                    if (settings.generateSources())
                    {
                        commands.add("--generate-sources");
                        //commands.add("true");
                    }

                    if (!version.equals("") && !version.equals(" "))
                    {
                        commands.add("--rev");
                        commands.add(version);
                    }

                    processBuilder = new ProcessBuilder(commands);
                    writeTo(Main.Prefix + "Running BuildTools command: " + flatten(commands) + "\n");
                    processBuilder.directory(buildToolsParent);

                    process = processBuilder.start();

                    String s;
                    BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    while ((s = stdout.readLine()) != null)
                    {
                        determineQueues(s);

                        System.out.println(s);
                    }

                    process.waitFor();

                    process.getInputStream().close();
                    process.getErrorStream().close();

                    if (process != null)
                    {
                        process.destroy();
                    }

                    File found = findSpigotJar(buildToolsParent);

                    if (found == null)
                    {
                        writeTo("\n============================================================");
                        writeTo("\n\n" + Main.Prefix + "Failed to build Spigot Jar. Please check the console log to find out what went wrong.\n\n");

                        reset();
                        return;
                    }

                    File saveTo;

                    saveTo = found;

                    if (Main.getSettings().saveJarToPath())
                    {
                        File jarPath = new File(Main.mainGUI.getJarPath().getText());

                        if (!jarPath.getName().endsWith(".jar"))
                        {
                            writeTo("\n============================================================-");
                            writeTo("\n\n" + Main.Prefix + "ERROR: Could not save Spigot Jar to file named \"" +
                                    jarPath.getName() + "\" - Must be a .jar file.\n\n");
                        }
                        else
                        {
                            saveTo = jarPath;
                            Files.move(found.toPath(), jarPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                    }

                    writeTo("\n============================================================");
                    writeTo("\n\n" + Main.Prefix + "Finished building Jar(s). Please go to this directory to find your " +
                            "Spigot Jar:\n" + saveTo + "\n\n");
                }
                catch (Exception e)
                {
                    JOptionPane.showMessageDialog(Main.buildFrame, "ERROR: Something went wrong while building. Check your settings! " +
                            "\n\"" + e.getLocalizedMessage() + "\"");
                    e.printStackTrace();
                }

                stopBuildProcesses();
            }
        };

        buildThread = new Thread(runnable);

        buildThread.start();
    }

    public void stopBuildProcesses()
    {
        writeTo("\n\n============================================================\n\n");

        if (downloadThread != null)
        {
            downloadThread.interrupt();
            downloadThread = null;
        }

        if (timerThread != null)
        {
            timerThread.interrupt();
            timerThread = null;
        }

        if (buildThread != null)
        {
            buildThread.interrupt();
            buildThread = null;
        }

        if (process != null && process.isAlive())
        {
            process.destroy();
            process = null;
        }

        reset();
    }

    private void reset()
    {
        updateBuildToolsButton.setEnabled(true);

        if (messageConsole != null)
        {
            messageConsole.directBackOut();
            messageConsole.directBackErr();
        }

        commandProgress.setValue(0);
        updateBuildToolsButton.setText("Update BuildTools");
        updateBuildToolsButton.setEnabled(true);
        buildJar.setEnabled(true);
        buildJar.setToolTipText("");
    }

    private String flatten(List<String> commands)
    {
        String string = "";

        for (String in : commands)
        {
            string += " " + in;
        }

        return string.substring(1);
    }

    private File findSpigotJar(File directory)
    {
        File spigotFolder = new File(directory + "/Spigot/Spigot-Server/target/");

        if (!spigotFolder.exists())
        {
            return null;
        }

        for (File potentialJar : spigotFolder.listFiles())
        {
            if (!potentialJar.getName().endsWith(".jar"))
            {
                continue;
            }

            if (potentialJar.getName().startsWith("original"))
            {
                continue;
            }

            return potentialJar;
        }

        return null;
    }

    private void determineQueues(String line)
    {

    }

    public Thread getBuildThread()
    {
        return buildThread;
    }

    public JButton getBuildJar()
    {
        return buildJar;
    }

    public JScrollPane getScrollPane()
    {
        return scrollPane;
    }

    public JTextArea getConsoleText()
    {
        return consoleText;
    }

    public JPanel getMainPanel()
    {
        return mainPanel;
    }

    private boolean isFileClosed()
    {
        boolean closed;

        FileChannel channel = null;

        File file = new File(Main.mainGUI.getJarPath().getText());

        if (!file.exists())
        {
            return true;
        }

        try
        {
            channel = new RandomAccessFile(file, "rw").getChannel();
            closed = true;
        }
        catch (Exception ex)
        {
            closed = false;
        }
        finally
        {
            if (channel != null)
            {
                try
                {
                    channel.close();
                }
                catch (IOException ignored)
                {
                }
            }
        }

        return closed;
    }

    private void writeTo(String write)
    {
        consoleText.append(write);
    }
}
