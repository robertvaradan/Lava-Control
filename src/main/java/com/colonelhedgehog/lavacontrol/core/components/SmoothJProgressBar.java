package com.colonelhedgehog.lavacontrol.core.components;

import javax.swing.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by ColonelHedgehog on 8/2/15.
 */
public class SmoothJProgressBar extends JProgressBar
{
    public ScheduledFuture<?> currentFuture;

    @Override
    public void setValue(final int destValue)
    {
        //System.out.println("Going to value: " + destValue);
        if(currentFuture != null)
        {
            currentFuture.cancel(true);
        }

        final int[] currentValue = {super.getValue()};

        if (destValue == 0 || destValue < currentValue[0] || currentValue[0] == destValue)
        {
            super.setValue(destValue);
        }

        final int distance = destValue - currentValue[0];

        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        final Runnable runnable = new Runnable()
        {
            private int iteration = 0;
            private int easeOut = 0;
            public void run()
            {
                iteration += distance/50;

                if (currentValue[0] < destValue)
                {
                    double newValue = iteration;

                    if(currentValue[0] / destValue >= 0.85)
                    {
                        easeOut++;
                        int easeValue = (int) (Math.pow(easeOut * 2, 2));
                        newValue -= easeValue;
                        //System.out.println("NOW: Easing out -" + easeValue);
                    }

                    int finalValue = currentValue[0] += newValue;

                    //int easeOut = 0;

                    superSetValue(finalValue > destValue ? destValue : finalValue);
                    //System.out.println("Debug: Iterating: " + finalValue + "/" + destValue);
                }
                else
                {
                    if(destValue == 10000)
                    {
                        superSetValue(0);
                    }

                    currentFuture.cancel(true);
                    //System.out.println("Debug: Cancelled.");
                }
            }
        };


        currentFuture = executor.scheduleAtFixedRate(runnable, 0, 10, TimeUnit.MILLISECONDS);
    }

    private void superSetValue(int n)
    {
        super.setValue(n);
    }
}
