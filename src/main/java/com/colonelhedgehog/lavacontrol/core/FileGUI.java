package com.colonelhedgehog.lavacontrol.core;

import javax.swing.*;
import java.io.File;

/**
 * Created by ColonelHedgehog on 12/29/14.
 * You have freedom to modify given sources. Please credit me as original author.
 * Keep in mind that this is not for sale.
 */
public class FileGUI
{
    public JPanel FilePanel;
    public JFileChooser fc;

    public FileGUI()
    {
        fc = new JFileChooser();

        File file = new File(Main.mainGUI.jarPath.getText());

        if (file.exists())
        {
            fc.setCurrentDirectory(file.getParentFile());
        }

        int result = fc.showOpenDialog(FilePanel);


        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);

        if(result == JFileChooser.APPROVE_OPTION)
        {
            File chosen = fc.getSelectedFile();

            if (chosen != null)
            {
                String apath = chosen.getAbsolutePath();
                if(!chosen.getName().endsWith(".jar"))
                {
                    JOptionPane.showMessageDialog(Main.menuFrame, "ERROR: The file \"" + apath + "\" is not a Jar file.");
                    return;
                }
                Main.mainGUI.jarPath.setText(apath);
                Main.listJars(new File(new File(apath).getParent() + "/plugins"));
            }
        }
    }
}
