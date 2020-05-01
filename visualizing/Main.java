package com.company;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.Viewer;

import java.util.Scanner;

public class Main {
    /*
    Assumptions nodes values are from 0 to n-1 and edges are given to us in format of "u w" means there is an edge from
    node u to node w
     */
    public static void main(String[] args) throws InterruptedException {
        System.setProperty("org.graphstream.ui.renderer",
                "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        int [][] adj = ReadData();
        Graph graph = CreateGraph(adj);
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");

        graph.addAttribute("ui.stylesheet",
                "url('C:/Users/saman/Documents/PhD/2nd Semester/Distributed computing/Java Project/stylesheet.css')");
        graph.setStrict(true);

        SpriteManager sman = new SpriteManager(graph);
        Viewer viewer = graph.display(false);
        Yo_Yo_Algorithm algorithm = new Yo_Yo_Algorithm(graph, viewer, sman, adj);
        algorithm.run();
    }
    private static int [][] ReadData(){
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int m = sc.nextInt();
        int [][] adj_grpah = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                adj_grpah[i][j] = -1; // means that they are not connected
            }
        }
        for (int i = 0; i < m; i++) {
            int u = sc.nextInt();
            int w = sc.nextInt();
            if (u > w){
                int temp = u;
                u = w;
                w =  temp;
            }
            adj_grpah[u][w] = 0;
        }
        return adj_grpah;
    }
    private static Graph CreateGraph(int grh[][]){
        Graph graph;
        graph = new SingleGraph("Yo_Yo");
        for (int i = 0; i < grh.length; i++) {
            graph.addNode(i+"");
        }
        for (int i = 0; i < grh.length; i++) {
            for (int j = 0; j < grh[i].length; j++) {
                if (grh[i][j] != -1)
                    graph.addEdge(i + " " + j, i + "", j + "",true);
            }
        }
        return graph;
    }

}
