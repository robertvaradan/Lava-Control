package com.colonelhedgehog.lavacontrol.core.system.mac;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent;
import com.apple.eawt.Application;
import com.apple.eawt.FullScreenUtilities;
import com.colonelhedgehog.lavacontrol.core.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by ColonelHedgehog on 8/1/15.
 */
public class MacSetup
{

    public static void setCanFullscreenWindow(JFrame frame, boolean can)
    {
        FullScreenUtilities.setWindowCanFullScreen(frame, can);
    }

    public static void createMacSettings()
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
        application.setDockIconImage(Main.getIcon().getImage());
        application.setAboutHandler(new AboutHandler()
        {
            @Override
            public void handleAbout(AppEvent.AboutEvent aboutEvent)
            {
                JOptionPane.showMessageDialog(Main.menuFrame, "Lava Control is a multi-platform application for making plugin development\n" +
                        "less painful. This application was designed by ColonelHedgehog. You can view\n" +
                        "his website at https://colonelhedgehog.com", "About Lava Control", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
}
