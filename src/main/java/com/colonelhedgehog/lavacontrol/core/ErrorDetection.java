package com.colonelhedgehog.lavacontrol.core;

/**
 * Created by ColonelHedgehog on 8/3/15.
 */
public class ErrorDetection
{
    public static void determineErrorQueues(String str)
    {
        String prefix = "[Lava Control]: ";

        if(str.startsWith("java.lang"))
        {
            if(str.startsWith("org.bukkit.event.EventException"))
            {
                String section1Removed = str.replace(str.substring(0, str.indexOf("Caused by: ")), "");
                String middleSection = section1Removed.replace(section1Removed.substring(section1Removed.indexOf("\n", 0), section1Removed.length()), "");
                System.out.println(prefix + "Diagnosis: - Problem wtih");
            }
        }
        else if(str.startsWith("org.bukkit.event.EventException"))
        {

        }
    }

    public String getMessageFromPackage(String pack)
    {
        if(pack.equals("java.lang.NullPointerException"))
        {
            return "The plugin attempted to access a variable with a value that was null." +
                    "\nMost likely, you made a method call with one or more variables as" +
                    "\nparameters but did not check to see if they were null. This is most" +
                    "\neasily fixed by simply adding an if(var == null) statement BEFORE" +
                    "\naccessing it.";
        }
        else if(pack.equals("java.util.ConcurrentModificationException"))
        {
            return "A concurrent modification exception is commonly thrown when you attempt" +
                    "\nto modify a (Hash)Map's contents while still looping through it. The" +
                    "\nfix can be easily remedied in most cases by adding the items you want" +
                    "\nto modify to a local map (one that is created inside the method)" +
                    "\nand then modifying the initial map using your local map. It takes" +
                    "\nlonger, but it will usually fix the problem. Also read about" +
                    "\nConcurrentHashMaps: <url>http://java.sun.com/j2se/" + System.getProperty("java.version") + "/docs/api/java/util/Math.html</url>\n";
        }

        return "--NULL";
    }
}
