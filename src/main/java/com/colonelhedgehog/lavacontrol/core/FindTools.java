package com.colonelhedgehog.lavacontrol.core;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by ColonelHedgehog on 8/4/15.
 */
public class FindTools
{

    public static ArrayList<Integer> searchTextIndexes(JTextArea textArea, String toFind, boolean matchCase)
    {
        ArrayList<Integer> list = new ArrayList<>();

        if(toFind == null || toFind.length() == 0)
        {
            return list;
        }

        ////System.out.println("DEBUG: Begin search.");
        int toFindLength = toFind.length();
        ////System.out.println("DEBUG: toFind = " + toFind);

        int index = 0;
        String log = textArea.getText();
        ////System.out.println("DEBUG: Log size = " + log.length());

        if(!matchCase)
        {
            log = log.toLowerCase();
            toFind = toFind.toLowerCase();
        }

        int newIndex = 0;
        while(newIndex != -1)
        {
            newIndex = log.indexOf(toFind, index);
            ////System.out.println("startIndex (newIndex) is now " + newIndex);

            index = newIndex + toFindLength;
            ////System.out.println("endIndex (index) is now " + index);

            if(newIndex == -1)
            {
                //System.out.println("newIndex is -1");
                continue;
            }

            list.add(newIndex);
        }
        return list;
    }

}
