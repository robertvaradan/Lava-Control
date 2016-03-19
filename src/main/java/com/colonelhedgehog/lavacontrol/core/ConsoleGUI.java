package com.colonelhedgehog.lavacontrol.core;

import com.colonelhedgehog.lavacontrol.core.components.RoundedCornerBorder;
import com.colonelhedgehog.lavacontrol.core.components.SmoothJProgressBar;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ColonelHedgehog on 12/30/14.
 * You have freedom to modify given sources. Please credit me as original author.
 * Keep in mind that this is not for sale.
 */
public class ConsoleGUI
{

    private JPanel ConsolePanel;
    private JTextField consoleInput;
    private JTextArea consoleText;
    private JScrollPane scrollPane;
    private JButton exportLogButton;
    private JButton killButton;
    private JButton stopButton;
    private JButton sendCommand;
    private SmoothJProgressBar commandProgress;
    private JButton searchButton;
    private JCheckBox keepScrollbarAtBottomCheckBox;
    private InputStream in;
    private BufferedWriter writer;
    private Thread consoleThread;
    private int index = -1;
    private List<String> messageHistory = new ArrayList<>();
    private boolean disabled = false;
    private boolean nextWillBeError = false;

    public ConsoleGUI()
    {

        final Settings settings = Main.getSettings();
        settings.reloadSettings();

        //in = new JTextFieldInputStream(consoleInput);
        //char c;
        /*try
        {
            while ((c = (char) in.read()) != -1)
            {
                System.out.printf(String.valueOf(c));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }*/
        consoleInput.setText("");

        consoleText.setLineWrap(true);

        killButton.addActionListener(killListener);
        stopButton.addActionListener(stopListener);
        searchButton.addActionListener(searchListener);
        exportLogButton.addActionListener(exportLogListener);
        consoleInput.setBorder(new RoundedCornerBorder(6));
        scrollPane.setBorder(new RoundedCornerBorder(6));
        DefaultCaret caret = (DefaultCaret) consoleText.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        consoleText.setCaret(caret);
        commandProgress.setValue(0);
        keepScrollbarAtBottomCheckBox.setSelected(settings.getStickyScrollBar());
        keepScrollbarAtBottomCheckBox.addActionListener(stickyListener);


        final int mask = Main.isMac() ? InputEvent.META_DOWN_MASK : InputEvent.CTRL_DOWN_MASK;
        KeyStroke killKey = KeyStroke.getKeyStroke(KeyEvent.VK_K, mask);
        killButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(killKey, "killKey");
        killButton.getActionMap().put("killKey", buttonAction);

        KeyStroke stopKey = KeyStroke.getKeyStroke(KeyEvent.VK_S, mask);
        stopButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stopKey, "stopKey");
        stopButton.getActionMap().put("stopKey", buttonAction);

        KeyStroke findKey = KeyStroke.getKeyStroke(KeyEvent.VK_F, mask);
        searchButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(findKey, "findKey");
        searchButton.getActionMap().put("findKey", buttonAction);

        KeyStroke exportKey = KeyStroke.getKeyStroke(KeyEvent.VK_E, mask);
        exportLogButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(exportKey, "exportKey");
        exportLogButton.getActionMap().put("exportKey", buttonAction);

        if (Main.getSettings().getStickyScrollBar())
        {
            caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        }
        else
        {
            caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        }

        consoleText.setCaret(caret);
    }

    final Action buttonAction = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            JButton source = (JButton) e.getSource();
            source.doClick();
        }
    };

    final ActionListener sendListener = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            consoleInput.postActionEvent();
        }
    };

    final ActionListener searchListener = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (Main.searchGUI == null)
            {
                Main.searchGUI = new SearchGUI();
                Main.searchDialog = new JDialog(Main.consoleFrame, "Search the console log...");
                Main.searchDialog.setContentPane(Main.searchGUI.getSearchPanel());
                Main.searchDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
                Main.searchGUI.getSearchField().setText(Main.searchGUI.getLastSearch());
            }

            Main.searchDialog.setBounds(0, 0, 450, 110);
            Main.searchDialog.setResizable(false);
            Main.searchDialog.setLocationRelativeTo(Main.consoleFrame);
            Main.searchDialog.setVisible(true);
        }
    };

    final ActionListener killListener = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            System.out.println("[Lava Control] >>> ! FORCE KILL: Destroying process, skipping save procedures. ! <<<");
            System.out.println("[Lava Control] If possible, stop the server using the \"stop\" command/button to prevent issues.");
            consoleThread.interrupt();

            if (Main.mainGUI.getProcess() != null)
            {
                Main.mainGUI.getProcess().destroy();
            }
        }
    };

    final ActionListener stopListener = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            stopButton.setText("Stopping...");
            consoleInput.setText("stop");
            consoleInput.postActionEvent();
            commandProgress.setIndeterminate(true);
        }
    };

    final ActionListener stickyListener = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            final Settings settings = Main.getSettings();
            settings.setStickyScrollBar(keepScrollbarAtBottomCheckBox.isSelected());
            settings.saveSettings();
            settings.reloadSettings();
        }
    };

    final ActionListener exportLogListener = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            ExportGUI exportGUI = new ExportGUI();
            exportGUI.show();
        }
    };

    public void createConsoleInput(final BufferedWriter writer)
    {
        this.writer = writer;
        this.consoleThread = new Thread()
        {
            @Override
            public void run()
            {
                consoleInput.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent action)
                    {
                        try
                        {
                            commandProgress.setValue(100 * 20);
                            String cmd = consoleInput.getText();
                            messageHistory.add(cmd);
                            commandProgress.setValue(100 * 40);

                            System.out.println("» /" + cmd);
                            commandProgress.setValue(100 * 60);
                            writer.write(cmd + "\n");
                            commandProgress.setValue(100 * 80);
                            consoleInput.setText("");
                            commandProgress.setValue(100 * 100);
                            writer.flush();
                            index++;
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });

                consoleInput.addKeyListener(new KeyAdapter()
                {
                    public void keyPressed(KeyEvent e)
                    {
                        if (index == -1)
                        {
                            return;
                        }

                        if (e.getKeyCode() == KeyEvent.VK_UP)
                        {
                            consoleInput.setText(messageHistory.get(index != 0 ? index-- : 0));
                        }
                        else if (e.getKeyCode() == KeyEvent.VK_DOWN)
                        {
                            consoleInput.setText(messageHistory.get(index != (messageHistory.size() - 1) ? index++ : 0));
                        }
                    }
                });
            }
        };

        sendCommand.addActionListener(sendListener);
        consoleThread.start();
        //DefaultCaret caret = (DefaultCaret) consoleText.getCaret();
        //caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);*/
    }

    public void disableWindow()
    {
        disabled = true;
        consoleInput.setText("Server stopped.");
        consoleInput.setEnabled(false);
        stopButton.setEnabled(false);
        stopButton.setText("Stopped");
        killButton.setEnabled(false);
        commandProgress.setIndeterminate(false);
        commandProgress.setValue(0);


        Main.mainGUI.getLaunchJar().setEnabled(true);
        Main.mainGUI.getLaunchJar().setToolTipText("");
        nextWillBeError = false;
        ServerStatic.serverInitialized = false;

        if (Main.settings.getCloseWindowOnStop())
        {
            Main.consoleFrame.setVisible(false);
        }
    }

    private double factor = 0;


    public void determineQueues(String str)
    {
        ErrorDetection.determineErrorQueues(str);

        String info = "\\[\\d+:\\d+:\\d+\\ INFO]: ";
        String error = "\\[\\d+:\\d+:\\d+\\ ERROR]: ";

        if (!ServerStatic.serverInitialized)
        {
            // Plugin stuff
            if (str.matches("\\[(\\w+)\\] Loading (\\w+) v/"))
            {
                ServerStatic.loadedPlugins++;
                return;
            }

            // Standard status stuff

            int mod = 100;
            int max = 100 * mod;

            if (str.matches("Loading libraries, please wait..."))
            {
                commandProgress.setValue(mod * 10);
                return;
            }

            if (str.matches(info + "Starting minecraft server version \\d+.\\d+.\\d+"))
            {
                commandProgress.setValue(mod * 25);
                return;
            }

            if (str.matches(info + "Default game type: (\\w+)"))
            {
                commandProgress.setValue(mod * 40);
                return;
            }

            if (str.matches(info + "Starting Minecraft server on \\*\\:\\d+"))
            {
                commandProgress.setValue(mod * 50);
                return;
            }

            if (commandProgress.getValue() < max)
            {
                if ((str.startsWith("[Multiverse\\-Core] Loading World & Settings - ") || str.matches(info + "\\-\\-\\-\\-\\-\\-\\-\\- World Settings For \\[([^)]+)\\] \\-\\-\\-\\-\\-\\-\\-\\-")))
                {
                    commandProgress.setValue((commandProgress.getValue()) + (mod * 2)); // lol, if you have 25 worlds...
                }
                else if (str.matches("\\[(\\w+)\\] Enabling (\\w+) v"))
                {
                    if (factor == 0)
                    {
                        int headroom = (max) - commandProgress.getValue();

                        if (headroom > 1)
                        {
                            factor = headroom / ServerStatic.loadedPlugins;
                        }
                        else
                        {
                            factor = -1;
                        }
                        return;
                    }

                    if (factor != -1)
                    {
                        commandProgress.setValue((int) ((commandProgress.getValue() + factor) * (max)));
                    }
                }
            }

            if (str.matches(info + "Done \\(\\d+.\\d+s\\)\\! For help, type \\\"help\" or \\\"\\?\""))
            {
                commandProgress.setIndeterminate(false);
                commandProgress.setValue(max);
                ServerStatic.serverInitialized = true;
            }
        }
    }

    public JPanel getConsolePanel()
    {
        return ConsolePanel;
    }

    public JTextField getConsoleInput()
    {
        return consoleInput;
    }

    public JTextArea getConsoleText()
    {
        return consoleText;
    }

    public Thread getConsoleThread()
    {
        return consoleThread;
    }

    public JButton getExportLogButton()
    {
        return exportLogButton;
    }

    public JScrollPane getScrollPane()
    {
        return scrollPane;
    }

    public boolean getNextWillBeError()
    {
        return nextWillBeError;
    }

    public void setNextWillBeError(boolean nextWillBeError)
    {
        this.nextWillBeError = nextWillBeError;
    }

    public boolean getDisabled()
    {
        return disabled;
    }

    public JButton getSearchButton()
    {
        return searchButton;
    }

    public JButton getStopButton()
    {
        return stopButton;
    }

    public JButton getKillButton()
    {
        return killButton;
    }
}