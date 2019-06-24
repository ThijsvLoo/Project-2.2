package com.mygdx.mass.Data;

//import org.knowm.xchart.XYChart;
//import org.knowm.xchart.XYChartBuilder;
import com.mygdx.mass.Agents.Agent;
import org.knowm.xchart.*;
import org.knowm.xchart.style.*;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class Chart {

    // Create Chart
    final XYChart chart = new XYChartBuilder().width(600).height(400).title("Intruder vs Guard Wins").xAxisTitle("Number of Sims").yAxisTitle("Number of Wins").build();
    private ArrayList<Integer> intruderWins = new ArrayList<Integer>();
    private ArrayList<Integer> guardWins = new ArrayList<Integer>();
    private ArrayList<Integer> xAxis = new ArrayList<Integer>();
    private ArrayList<Integer> errorBar = new ArrayList<Integer>();
    private JPanel chartPanel = new XChartPanel<XYChart>(chart);

    public Chart() {

        xAxis.add(0);
        guardWins.add(0);
        intruderWins.add(0);
        errorBar.add(0);

// Customize Chart
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);

// Series
        try {
            chart.addSeries("Guard Wins", xAxis, guardWins);
            chart.addSeries("Intruder Wins", xAxis, intruderWins);
        } catch (Exception e){
            System.out.println(e.toString());
        }
// Schedule a job for the event-dispatching thread:
// creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                // Create and set up the window.
                JFrame frame = new JFrame("Intruder vs Guard Wins");
                frame.setLayout(new BorderLayout());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                // chart
                frame.add(chartPanel, BorderLayout.CENTER);


                // label
                JLabel label = new JLabel("Maastricht University DKE Group 3", SwingConstants.CENTER);
                frame.add(label, BorderLayout.SOUTH);

                // Display the window.
                frame.pack();
                frame.setVisible(true);
//                frame.update(frame.getGraphics());
            }
        });
    }
    public void addWin(Agent agent){
        xAxis.add(xAxis.size() + 1);
        errorBar.add(0);
        if (agent.getAgentType().equals(Agent.AgentType.INTRUDER)){
            intruderWins.add(intruderWins.size() + 1);
            if(guardWins.size() != 0) {
                guardWins.add(guardWins.get(guardWins.size() - 1));
            } else guardWins.add(0);
        }
        if (agent.getAgentType().equals(Agent.AgentType.GUARD)){
            if(intruderWins.size() != 0) {
                intruderWins.add(intruderWins.get(intruderWins.size() - 1));
            } else {
                intruderWins.add(0);
            }
            guardWins.add(guardWins.size() + 1);
        }

        chart.updateXYSeries("Guard Wins", xAxis, guardWins, errorBar);
        chart.updateXYSeries("Intruder Wins", xAxis, intruderWins, errorBar);
        chartPanel.updateUI();
    }
}
