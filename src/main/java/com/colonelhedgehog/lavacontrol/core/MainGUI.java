package com.colonelhedgehog.lavacontrol.core;

import com.colonelhedgehog.lavacontrol.core.components.JCheckBoxList;
import com.sun.management.OperatingSystemMXBean;

import javax.swing.*;
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
    public JFrame frame;
    public JTextField jarPath;
    public JButton chooseJar;
    public JPanel MainPanel;
    public JTextField memoryBash;
    public JButton launchJar;
    //public JList cList;
    public JScrollPane pluginList;
    public JTextField maxConsoleLines;
    public JCheckBox sshEnabled;
    public JTextField sshUsername;
    public JTextField sshHost;
    public JPasswordField sshPassword;
    public JLabel passwordLabel;

    public Process p;
    public ProcessBuilder pb;
    public ProcessThread thread;
    public JCheckBoxList checkList;

    public MainGUI()
    {
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

        disableSSH();

        sshEnabled.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                sshUsername.setEnabled(sshEnabled.isSelected());
                sshHost.setEnabled(sshEnabled.isSelected());
                sshPassword.setEnabled(sshEnabled.isSelected());
                passwordLabel.setEnabled(sshEnabled.isSelected());
                chooseJar.setEnabled(!sshEnabled.isSelected());
                pluginList.setEnabled(!sshEnabled.isSelected());

                if(checkList != null)
                {
                    checkList.setEnabled(!sshEnabled.isSelected());
                }
            }
        });

        jarPath.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(!sshEnabled.isSelected())
                {
                    File chosen = new File(jarPath.getText());
                    ImageIcon scaled = new ImageIcon(Main.getIcon().getImage().getScaledInstance(96, 96, java.awt.Image.SCALE_SMOOTH));

                    if (chosen.exists())
                    {
                        String apath = chosen.getAbsolutePath();
                        if (!chosen.getName().endsWith(".jar"))
                        {
                            JOptionPane.showMessageDialog(Main.menuFrame, "ERROR: The file \"" + apath + "\" is not a Jar file.", "Couldn't Select Jar", JOptionPane.INFORMATION_MESSAGE, scaled);
                            return;
                        }
                        Main.mainGUI.jarPath.setText(apath);
                        Main.listJars(new File(new File(apath).getParent() + "/plugins"));
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(Main.menuFrame, "ERROR: The file \"" + jarPath.getText() + "\" does not exist.", "Couldn't Select Jar", JOptionPane.INFORMATION_MESSAGE, scaled);
                    }
                }
            }
        });
    }

    private void disableSSH()
    {
        sshEnabled.setEnabled(false);
        sshEnabled.setSelected(false);
        sshEnabled.setToolTipText("SSH connections are currently unsupported.");
    }

    public void launch()
    {
        Main.createConsoleGUI();

        ImageIcon scaled = new ImageIcon(Main.getIcon().getImage().getScaledInstance(96, 96, java.awt.Image.SCALE_SMOOTH));
        final File jar = new File(jarPath.getText());
        if(!jar.exists() && !sshEnabled.isSelected())
        {
            JOptionPane.showMessageDialog(frame, "ERROR: The file \"" + jarPath.getText() + "\" doesn't exist.", "Couldn't Select Jar", JOptionPane.INFORMATION_MESSAGE, scaled);
            return;
        }

        if(!jar.getName().endsWith(".jar"))
        {
            JOptionPane.showMessageDialog(frame, "ERROR: The file \"" + jarPath.getText() + "\" isn't a Jar file.", "Couldn't Select Jar", JOptionPane.INFORMATION_MESSAGE, scaled);
            return;
        }

        int mem;

        try
        {
            mem = Integer.parseInt(memoryBash.getText());
        }
        catch (NumberFormatException nfe)
        {
            JOptionPane.showMessageDialog(frame, "ERROR: The memory argument \"" + memoryBash.getText() + "\" must be an integer.", "Invalid Memory Specified", JOptionPane.INFORMATION_MESSAGE, scaled);
            return;
        }

        long maxMemory = ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getFreePhysicalMemorySize();

        if(mem < 8)
        {
            JOptionPane.showMessageDialog(frame, "ERROR: Too little memory specified! You must put at least 8 megabytes. (512 recommended!)", "Couldn't Select Jar", JOptionPane.INFORMATION_MESSAGE, scaled);
            return;
        }
        if(maxMemory < 512)
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
        if(maxMemory < mem)
        {
            int selected = JOptionPane.showConfirmDialog(frame, "WARNING: You have specified more memory than your system has available. Do you wish to proceed?", "Couldn't Select Jar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, scaled);
            if(selected == JOptionPane.YES_OPTION)
            {
                System.out.println("[Lava Control] *Gulp* Here goes... running with " + (maxMemory - mem) + " more megabytes memory than I have available.");
            }
            else
            {
                return;
            }
        }

        //Runtime rt = Runtime.getRuntime();
        if(!sshEnabled.isSelected())
        {
            createShellFile(jar.getParentFile());
        }

        thread = new ProcessThread(jar.getAbsolutePath(), jarPath, mem, sshEnabled.isSelected());

        thread.start();
    }

    private void createShellFile(File fi)
    {
        File path = new File(fi.getPath());

        if(!path.exists())
        {
            path.mkdirs();
        }

        File f = new File(path + "/lavacontrol_launch.sh");
        if(f.exists())
        {
            f.delete();
        }

        try
        {
            f.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write("cd \"$( dirname \"$0\" )\"\n");
            bw.write("java " + (!System.getProperty("java.version").startsWith("1.8") ? "-XX:MaxPermSize=128M" : "") + " -Xmx" + memoryBash.getText() + "M -jar " + new File(jarPath.getText()).getName() + " -o true");
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
}
