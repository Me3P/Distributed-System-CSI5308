package com.company;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.Viewer;

import java.util.ArrayList;

public class Yo_Yo_Algorithm {
    private Graph graph;
    private Viewer viewer;
    private SpriteManager sman;
    private int adj[][];
    private boolean isConnected[][];
    private int numberOfIncomingEdges[], minValue[];
    private int n, m; // number of Nodes
    private double currentPosition[][] ;
    private static double width = 6000 , height = 4000;
    private int sec = 2;
    Yo_Yo_Algorithm(Graph graph, Viewer viewer, SpriteManager sman, int adj[][]){
        this.graph = graph;
        this.viewer = viewer;
        this.sman = sman;
        this.adj = adj;
        this.n = adj.length;
        this.minValue = new int[n];
        this.numberOfIncomingEdges = new int[n];
        this.isConnected = new boolean[n][n];
        this.currentPosition = new double[n][3];
        this.m = 0;
        for (int i = 0; i < n; i++) {
            this.minValue[i] = i;
            this.numberOfIncomingEdges[i] = 0;
            for (int j = 0; j < n; j++) {
                if (this.adj[i][j] == 0){
                    this.isConnected[i][j] = true;
                    m++;
                }
                else {
                    this.isConnected[i][j] = false;
                }
            }
            for (int j = 0; j < 3; j++) {
                currentPosition[i][j] = 0;
            }
        }
        for (int i = 0; i < n; i++) {
            graph.getNode(i).addAttribute("ui.label", i + ", "+ i);
            graph.getNode(i).addAttribute("ui.class", "internal");
        }
    }
    public void run() throws InterruptedException {
        while(m != 0){
            int lvl[] = findLvl(this.FindSources(0));
            this.render(lvl);
            this.YoDown(this.FindSources(0), lvl);
            this.YoUp(this.FindSources(1));
        }
    }
    private int[] findLvl(ArrayList<Integer> arr) {
        int lvl[] = new int[n];

        for (int i = 0; i < arr.size(); i++) {
            lvl[arr.get(i)] = 1;
        }
        while(!arr.isEmpty()){
            int top = arr.get(0);
            arr.remove(0);
            for (int i = 0; i < n; i++) {
                if (isConnected[top][i] && numberOfIncomingEdges[i] != 0 && --numberOfIncomingEdges[i] == 0) {
                    arr.add(i);
                    lvl[i] = lvl[top] + 1;
                }
            }
        }
        return lvl;
    }
    public void render(int lvl[]) throws InterruptedException {
        ArrayList<Integer> arr = this.FindSources(0);
        double nextPosition[][] = new double[n][3];
        int mxLvl = 0;
        for (int i = 0; i < n; i++) {
            mxLvl = Math.max(mxLvl, lvl[i]);
        }
        int curLvl = 0;
        int curIndex = 0;
        int size = 0;
        while(!arr.isEmpty()) {
            int top = arr.get(0);
            arr.remove(0);

            if (lvl[top] > curLvl){
                curLvl = lvl[top];
                curIndex = 1;
                size = arr.size()+1;
            }
            else curIndex++;

            nextPosition[top][0] = width/mxLvl*(lvl[top])  ;
            nextPosition[top][1] = height/size * (curIndex + 0.15*Math.random());
            nextPosition[top][2] = 0.0;

            for (int i = 0; i < n; i++) {
                if (isConnected[top][i] && numberOfIncomingEdges[i] != 0 && --numberOfIncomingEdges[i] == 0) {
                    arr.add(i);
                }
            }
        }

        double diff[][] = new double[n][3];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < 3; j++) {
                diff[i][j] = (nextPosition[i][j] - currentPosition[i][j]) / (sec*1000);
            }
        }

        for (int i = 0; i < sec*1000; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < 3; k++) {
                    currentPosition[j][k] += diff[j][k];
                    graph.getNode(j).setAttribute("xyz", currentPosition[j][0], currentPosition[j][1], currentPosition[j][2]);

                }
            }
            Thread.sleep(1);
        }
    }

    private ArrayList<Integer> FindSources(int mode){ // mode 0 incoming edges for Yo down, mode 1 outgoing edges for Yo up
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (this.isConnected[i][j]){
                    if (mode == 0)
                        this.numberOfIncomingEdges[j]++;
                    else
                        this.numberOfIncomingEdges[i]++;
                }
            }
        }
        ArrayList<Integer> arr = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) {
            if (this.numberOfIncomingEdges[i] == 0){
                arr.add(i);
            }
        }
        return arr;
    }
    private void YoDown(ArrayList<Integer> queue, int lvl[]){
        ArrayList<Edge> edges = new ArrayList<>();
        while(!queue.isEmpty()){
            Integer top = queue.get(0);
            queue.remove(0);
            for (int i = 0; i < n; i++) {
                if (this.isConnected[top][i]){
                    this.adj[top][i] = this.minValue[top];
                    this.minValue[i] = Math.min(this.minValue[i], this.minValue[top]);
                    graph.getNode(i).addAttribute("ui.label", i + ", "+ this.minValue[i]); // update the UI
                    this.send(top, i, "#03D4E3");
                    edges.add(graph.getEdge(top+" "+i));
                    if (this.numberOfIncomingEdges[i] != 0 && --this.numberOfIncomingEdges[i] == 0){
                        queue.add(i);
                    }
                }
            }
        }
    }
    private void YoUp(ArrayList<Integer> queue){

        while(!queue.isEmpty()){
            ArrayList<Integer> yesList = new ArrayList<>();
            Integer top = queue.get(0);
            queue.remove(0);
            for (int i = 0; i < n; i++) {
                if (this.isConnected[i][top] && this.numberOfIncomingEdges[i] != 0){
                    if (this.adj[i][top] > this.minValue[top]){ // no edge and should be flipped
                        this.minValue[i] = this.minValue[top];
                        graph.getNode(i).addAttribute("ui.label", i + ", "+ this.minValue[i]); // update the UI
                        this.flip(i, top);
                    }
                    else{ // yes edge
                        yesList.add(i);
                    }
                    if (this.numberOfIncomingEdges[i] != 0 && --this.numberOfIncomingEdges[i] == 0){
                        queue.add(i);
                    }
                }
            }
            boolean isSink = true;
            for (int i = 0; i < n; i++) {
                if (isConnected[top][i]) isSink = false;
            }
            if (!(isSink) && yesList.size() > 0)  {

                int source = yesList.get(0);
                int dest = top;
                graph.getEdge(source+" "+dest).setAttribute("ui.class", "yes_link");
                graph.getNode(source+"").setAttribute("ui.class", "yes");
                graph.getNode(dest+"").setAttribute("ui.class", "yes");
                sleep();
                graph.getNode(source+"").removeAttribute("ui.class");
                graph.getNode(dest+"").removeAttribute("ui.class");
                graph.getEdge(source+" " +dest).removeAttribute("ui.class");
                yesList.remove(0);
            }
            for (Integer i :
                    yesList) {
                this.remove(i, top);
            }
        }
    }
    private void remove(int source, int dest){
        m--;
        this.isConnected[source][dest] = false;

        graph.getEdge(source+" "+dest).setAttribute("ui.class", "yes_link");

        graph.getNode(source+"").setAttribute("ui.class", "yes");
        graph.getNode(dest+"").setAttribute("ui.class", "yes");

        sleep();

        graph.getNode(source+"").removeAttribute("ui.class");
        graph.getNode(dest+"").removeAttribute("ui.class");

        graph.removeEdge(source+" "+dest);
    }
    private void flip(int source, int dest){
        this.isConnected[source][dest] = false;
        this.isConnected[dest][source] = true;
        this.adj[dest][source] = this.adj[source][dest];
        graph.removeEdge(source+" "+dest);
        graph.addEdge(dest+" "+source, dest+"", source+"", true);
        graph.getEdge(dest+" "+source).setAttribute("ui.class", "no_link");
        graph.getEdge(dest+" "+source).setAttribute("ui.label", ""+this.adj[source][dest]);

        graph.getNode(source+"").setAttribute("ui.class", "no");
        graph.getNode(dest+"").setAttribute("ui.class", "no");

        sleep();

        graph.getNode(source+"").removeAttribute("ui.class");
        graph.getNode(dest+"").removeAttribute("ui.class");

        graph.getEdge(dest+" "+source).removeAttribute("ui.class");
    }
    private void send(int source, int dest, String color){
//        Sprite s = sman.addSprite(source+" "+dest);
//////        s.setPosition(2, 1, 0);
////        s.attachToEdge(source+" "+dest);
////        for (int i = 0; i < sec*1000; i++) {
////            double p = i/(sec*1000);
////            double x = p*this.currentPosition[dest][0] - (1 - p)*this.currentPosition[source][0];
////            double y = p*this.currentPosition[dest][1] - (1 - p)*this.currentPosition[source][1];
////            double z = 0;
////            s.setAttribute("xyz", x, y ,z);
////            try {
////                Thread.sleep(1);
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
////        }
//        s.setAttribute("shape", "flow");

        graph.getEdge(source+" "+dest).setAttribute("ui.class", "send");
        graph.getNode(source+"").setAttribute("ui.class", "send");
        graph.getNode(dest+"").setAttribute("ui.class", "send");

        sleep();

        graph.getNode(source+"").removeAttribute("ui.class");
        graph.getNode(dest+"").removeAttribute("ui.class");
        graph.getEdge(source+" "+dest).removeAttribute("ui.class");

//        s.setAttribute("shape", "flow");

        graph.getEdge(source+" "+dest).setAttribute("ui.label", ""+this.adj[source][dest]);
        graph.getEdge(source+" "+dest).setAttribute("text-size", "30px");
    }
    protected void sleep() {
        try { Thread.sleep(1000); } catch (Exception e) {}
    }




}
