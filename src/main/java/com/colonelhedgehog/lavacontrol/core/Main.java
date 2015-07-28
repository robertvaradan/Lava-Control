package com.colonelhedgehog.lavacontrol.core;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent;
import com.apple.eawt.Application;
import com.colonelhedgehog.lavacontrol.core.Components.JCheckBoxList;
import com.colonelhedgehog.lavacontrol.core.Components.RoundedCornerBorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Main
{

    private static ImageIcon icon;
    public static void main(String[] args)
    {
        System.out.println("[Lava Control] Loading...");

        try
        {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Lava Control");
        }
        catch (Exception e)
        {
            // Whatever
        }


        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {
            System.out.println("[Lava Control] Wasn't able to set look-and-feel. Your system may not be fully supported. ");
            //e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                settings.saveSettings();
                if (mainGUI.p != null && consoleGUI.consoleThread.isAlive())
                {
                    mainGUI.p.destroy();
                }
                else if(consoleGUI.consoleThread.isAlive())
                {
                    consoleGUI.consoleThread.interrupt();
                }
            }
        });

        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                createMainGUI();
            }
        });
        icon = new ImageIcon(Main.class.getResource("/media/logo.png"));

        if(System.getProperty("os.name").toLowerCase().contains("mac"))
        {
            Application application = Application.getApplication();
            PopupMenu popupmenu = new PopupMenu("Lava Control");
            MenuItem menuItem = new MenuItem("Start Server");
            menuItem.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    Main.mainGUI.launch();
                }
            });
            popupmenu.add(menuItem);
            application.setDockMenu(popupmenu);
            application.setDockIconImage(icon.getImage());
            application.setAboutHandler(new AboutHandler()
            {
                @Override
                public void handleAbout(AppEvent.AboutEvent aboutEvent)
                {
                    JOptionPane.showMessageDialog(menuFrame, "Lava Control is a multi-platform application for making plugin development\n" +
                                                             "less painful. This application was designed by ColonelHedgehog. You can view\n" +
                                                             "his website at https://colonelhedgehog.com", "About Lava Control", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }

    }

    public static JFrame menuFrame;
    public static JFrame consoleFrame;

    public static MainGUI mainGUI = null;
    public static FileGUI fileGUI = null;
    public static ConsoleGUI consoleGUI = null;
    public static Settings settings;

    public static void createConsoleGUI()
    {
        consoleFrame = new JFrame("Lava Control | Console");
        System.out.println("[Lava Control] Creating Lava Control's Console GUI...");

        consoleGUI = new ConsoleGUI();

        consoleFrame.setContentPane(consoleGUI.ConsolePanel);
        consoleFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        consoleFrame.setBounds(0, 0, 800, 600);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        consoleFrame.setLocation(dim.width / 2 - consoleFrame.getSize().width / 2, dim.height / 2 - consoleFrame.getSize().height / 2);
        consoleFrame.setVisible(true);

        MessageConsole mc = new MessageConsole(consoleGUI.consoleText);
        mc.redirectOut();
        mc.redirectErr(Color.RED, null);
        int ml = 100;

        try
        {
            ml = Integer.parseInt(mainGUI.maxConsoleLines.getText());
        }
        catch (NumberFormatException nfe)
        {
            // Whatever :(
        }

        mc.setMessageLines(ml);
    }

    public static ImageIcon getIcon()
    {
        return icon;
    }
    private static void createMainGUI()
    {
        Thread uithread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {


                settings = new Settings();

                menuFrame = new JFrame("Lava Control | Main");
                menuFrame.setIconImage(icon.getImage());

                if (mainGUI == null)
                {
                    mainGUI = new MainGUI();
                }

                menuFrame.setContentPane(mainGUI.MainPanel);
                menuFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                menuFrame.setBounds(0, 0, 800, 600);
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                menuFrame.setLocation(dim.width / 2 - menuFrame.getSize().width / 2 - 10, dim.height / 2 - menuFrame.getSize().height / 2 - 10);
                menuFrame.setVisible(true);
                System.out.println("[Lava Control] Created Lava Control's GUI.");

                mainGUI.jarPath.setText(settings.getField(Settings.Field.LAST_PATH).toString());
                mainGUI.memoryBash.setText(settings.getField(Settings.Field.MEM_BASH).toString());
                mainGUI.maxConsoleLines.setText(settings.getField(Settings.Field.MAX_CONSOLE_LINES).toString());
                mainGUI.sshEnabled.setSelected(Boolean.parseBoolean(settings.getField(Settings.Field.SSH_ENABLED).toString()));
                mainGUI.sshHost.setText(settings.getField(Settings.Field.SSH_HOST).toString());
                mainGUI.sshUsername.setText(settings.getField(Settings.Field.SSH_USERNAME).toString());
                mainGUI.sshPassword.setText(settings.getField(Settings.Field.SSH_PASSWORD).toString());

                boolean se = mainGUI.sshEnabled.isSelected();

                mainGUI.sshUsername.setEnabled(se);
                mainGUI.sshPassword.setEnabled(se);
                mainGUI.sshHost.setEnabled(se);
                mainGUI.passwordLabel.setEnabled(se);
                mainGUI.chooseJar.setEnabled(!se);
                mainGUI.pluginList.setEnabled(!se);

                if (mainGUI.checkList != null)
                {
                    mainGUI.checkList.setEnabled(!se);
                    mainGUI.checkList.setBorder(new RoundedCornerBorder(6));
                }

                mainGUI.createUIComponents();
                listJars(new File(new File(settings.getField(Settings.Field.LAST_PATH).toString()).getParent() + "/plugins"));
                mainGUI.pluginList.scrollRectToVisible(new Rectangle(mainGUI.pluginList.getSize()));
                mainGUI.pluginList.setMinimumSize(mainGUI.pluginList.getSize());
                mainGUI.pluginList.setPreferredSize(mainGUI.pluginList.getSize());
                mainGUI.pluginList.setMaximumSize(mainGUI.pluginList.getSize());
                mainGUI.pluginList.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);

                mainGUI.jarPath.setBorder(new RoundedCornerBorder(6));
                mainGUI.pluginList.setBorder(new RoundedCornerBorder(6));

                mainGUI.memoryBash.setBorder(new RoundedCornerBorder(6));
                mainGUI.sshUsername.setBorder(new RoundedCornerBorder(6));
                mainGUI.sshHost.setBorder(new RoundedCornerBorder(6));
                mainGUI.sshPassword.setBorder(new RoundedCornerBorder(6));
                mainGUI.maxConsoleLines.setBorder(new RoundedCornerBorder(6));
            }
        });
        uithread.start();
    }

    public static void listJars(File f)
    {
        if (mainGUI.sshEnabled.isSelected())
        {
            return;
        }

        JCheckBox firstBox = null;
        DefaultListModel<JCheckBox> model = new DefaultListModel<>();
        if (mainGUI.checkList != null)
        {
            //System.out.println("Already exists lol: " + mainGUI.checkList.getName());
            mainGUI.pluginList.remove(mainGUI.checkList);
        }
        //mainGUI.pluginList.repaint();

        File[] files = new File(f.getPath()).listFiles();
        if (files != null)
        {
            for (File file : files)
            {
                if (file.getName().endsWith(".jar") || file.getName().endsWith("._jar"))
                {
                    JCheckBox cb = new JCheckBox(file.getName());

                    if (firstBox == null)
                    {
                        firstBox = cb;
                    }

                    cb.setSelected(file.getName().endsWith(".jar"));
                    cb.setVisible(true);
                    cb.setText(file.getName());
                    model.addElement(cb);
                    mainGUI.pluginList.validate();
                    mainGUI.pluginList.repaint();
                }
            }
        }

        JCheckBoxList jCheckBoxList = new JCheckBoxList(model, mainGUI.jarPath.getText());
        jCheckBoxList.setName("pluginCheckboxList");
        jCheckBoxList.setSize(mainGUI.pluginList.getSize());
        mainGUI.pluginList.add(jCheckBoxList);
        mainGUI.checkList = jCheckBoxList;
        mainGUI.pluginList.setViewportView(mainGUI.checkList);
        //jCheckBoxList.setVisible(true);
    }

    public static void createFileGUI()
    {
        System.out.println("[Lava Control] Creating Lava Control's File Chooser GUI...");
        fileGUI = new FileGUI();
    }
}
