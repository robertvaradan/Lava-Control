package com.colonelhedgehog.lavacontrol.core;

import com.colonelhedgehog.lavacontrol.core.components.JCheckBoxList;
import com.sun.management.OperatingSystemMXBean;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 * Created by ColonelHedgehog on 12/29/14.
 * You have freedom to modify given sources. Please credit me as original author.
 * Keep in mind that this is not for sale.
 */
public class MainGUI
{
    private JFrame frame;
    private JTextField jarPath;
    private JButton chooseJar;
    private JPanel MainPanel;
    private JButton launchJar;
    //private JList cList;
    private JScrollPane pluginList;
    private JButton optionsButton;

    private Process process;
    private JCheckBoxList checkList;

    public MainGUI()
    {
        jarPath.setText(Main.getSettings().getLastPath());
        launchJar.setActionCommand("Launch");
        launchJar.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (e.getActionCommand().equals("Launch"))
                {
                    launch();
                }
            }
        });

        chooseJar.setActionCommand("Click");
        chooseJar.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (e.getActionCommand().equals("Click"))
                {
                    Main.createFileGUI();
                }
            }
        });

        final Font defaultFont = jarPath.getFont();
        final boolean[] wasDifferent = new boolean[1];

        jarPath.getDocument().addDocumentListener(new DocumentListener()
        {
            public void changedUpdate(DocumentEvent e)
            {
                doCheck();
            }

            public void removeUpdate(DocumentEvent e)
            {
                doCheck();
            }

            public void insertUpdate(DocumentEvent e)
            {
                doCheck();
            }

            private void doCheck()
            {
                if (!jarPath.getText().equals(Main.settings.getLastPath()))
                {
                    Font font = new Font(defaultFont.getName(), Font.ITALIC, defaultFont.getSize());
                    jarPath.setFont(font);

                    jarPath.setForeground(new Color(127, 127, 127));
                    wasDifferent[0] = true;
                }
                else
                {
                    if (wasDifferent[0])
                    {
                        wasDifferent[0] = false;
                        jarPath.setFont(defaultFont);
                        jarPath.setForeground(new Color(0, 0, 0));
                    }
                }
            }
        });

        jarPath.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (!Main.getSettings().getSSHEnabled())
                {
                    File chosen = new File(jarPath.getText());
                    ImageIcon scaled = new ImageIcon(Main.getIcon().getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH));

                    if (chosen.exists())
                    {
                        String apath = chosen.getAbsolutePath();
                        if (!chosen.getName().endsWith(".jar"))
                        {
                            JOptionPane.showMessageDialog(Main.menuFrame, "ERROR: The file \"" + apath + "\" is not a Jar file.", "Couldn't Select Jar", JOptionPane.INFORMATION_MESSAGE, scaled);
                            return;
                        }

                        jarPath.setText(apath);
                        Main.listJars(new File(new File(apath).getParent() + "/plugins"));

                        Main.getSettings().setLastPath(apath);

                        doGreenHiglight();

                        wasDifferent[0] = false;
                        jarPath.setFont(defaultFont);
                        jarPath.setForeground(new Color(0, 0, 0));
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(Main.menuFrame, "ERROR: The file \"" + jarPath.getText() + "\" does not exist.", "Couldn't Select Jar", JOptionPane.INFORMATION_MESSAGE, scaled);
                    }
                }
            }
        });

        optionsButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Main.createPrefsGUI();
            }
        });
    }

    private Timer timer;

    private void doGreenHiglight()
    {
        if (timer != null && timer.isRunning())
        {
            timer.stop();
        }

        timer = new Timer(3, new ActionListener()
        {
            private int x = 0;

            public void actionPerformed(ActionEvent ae)
            {
                jarPath.setBackground(new Color(140 + x, 255, 140 + x));

                if (140 + x >= 255)
                {
                    timer.stop();
                    return;
                }

                x++;
            }
        });

        timer.start();
    }


    public void launch()
    {
        Main.createConsoleGUI();

        ImageIcon scaled = new ImageIcon(Main.getIcon().getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH));
        final File jar = new File(jarPath.getText());
        if (!jar.exists() && !Main.getSettings().getSSHEnabled())
        {
            JOptionPane.showMessageDialog(frame, "ERROR: The file \"" + jarPath.getText() + "\" doesn't exist.", "Couldn't Select Jar", JOptionPane.INFORMATION_MESSAGE, scaled);
            return;
        }

        if (!jar.getName().endsWith(".jar"))
        {
            JOptionPane.showMessageDialog(frame, "ERROR: The file \"" + jarPath.getText() + "\" isn't a Jar file.", "Couldn't Select Jar", JOptionPane.INFORMATION_MESSAGE, scaled);
            return;
        }

        int mem = Main.getSettings().getMemBash();


        long maxMemory = ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getFreePhysicalMemorySize();

        if (mem < 8)
        {
            JOptionPane.showMessageDialog(frame, "ERROR: Too little memory specified! You must put at least 8 megabytes. (512 recommended!)", "Couldn't Select Jar", JOptionPane.INFORMATION_MESSAGE, scaled);
            return;
        }
        if (maxMemory < 512)
        {
            int selected = JOptionPane.showConfirmDialog(frame, "WARNING: You have specified more memory than your system has available. Do you wish to proceed?", "Couldn't Select Jar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, scaled);
            if (selected == JOptionPane.YES_OPTION)
            {
                System.out.println("[Lava Control] *Gulp* Here goes... running with " + (512 - mem) + " less megabytes of memory than recommended.");
            }
            else
            {
                return;
            }
        }
        if (maxMemory < mem)
        {
            int selected = JOptionPane.showConfirmDialog(frame, "WARNING: You have specified more memory than your system has available. Do you wish to proceed?", "Couldn't Select Jar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, scaled);
            if (selected == JOptionPane.YES_OPTION)
            {
                System.out.println("[Lava Control] *Gulp* Here goes... running with " + (maxMemory - mem) + " more megabytes memory than I have available.");
            }
            else
            {
                return;
            }
        }

        //Runtime rt = Runtime.getRuntime();
        if (!Main.getSettings().getSSHEnabled())
        {
            createShellFile(jar.getParentFile());
        }

        ProcessThread thread = new ProcessThread(jar.getAbsolutePath(), jarPath, mem, Main.getSettings().getSSHEnabled());

        thread.start();
    }

    private void createShellFile(File fi)
    {
        File path = new File(fi.getPath());

        if (!path.exists())
        {
            path.mkdirs();
        }

        File f = new File(path + "/lavacontrol_launch.sh");
        if (f.exists())
        {
            f.delete();
        }

        try
        {
            f.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write("cd \"$( dirname \"$0\" )\"\n");
            bw.write("java " + (!System.getProperty("java.version").startsWith("1.8") ? "-XX:MaxPermSize=128M" : "") + " -Xmx" + Main.getSettings().getMemBash() + "M -jar " + new File(jarPath.getText()).getName() + " -o true");
            bw.flush();
            bw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        try
        {
            Runtime.getRuntime().exec("chmod +x " + f.getAbsolutePath().replace(" ", "\""));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public DefaultListModel<JCheckBox> model;

    public ActionListener onCheck;

    public void createUIComponents()
    {
    }

    public JTextField getJarPath()
    {
        return jarPath;
    }


    public JButton getChooseJar()
    {
        return this.chooseJar;
    }

    public JScrollPane getPluginList()
    {
        return this.pluginList;
    }

    public JCheckBoxList getCheckList()
    {
        return this.checkList;
    }

    public void setChecklist(JCheckBoxList checkList)
    {
        this.checkList = checkList;
    }

    public JPanel getMainPanel()
    {
        return this.MainPanel;
    }

    public JButton getLaunchJar()
    {
        return this.launchJar;
    }

    public Process getProcess()
    {
        return this.process;
    }

    public void setProcess(Process process)
    {
        this.process = process;
    }
}
