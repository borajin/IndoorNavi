package com.example.indoornavi;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;


public class Directions {
    //경로생성, 진행방향 제시
    Context mContext;
    Astar astar;

    TTS tts;

    ArrayList<String> route;

    public Directions(Context context) {
        mContext = context;
        tts = new TTS(mContext);
        astar = new Astar(context);
    }

    public String getRoute(String currentLocation, String destination) {
        //경로 배열 보고 방향 제시
        //만약 여기서 제시한 방향대로 안가고 반대로 갔다(자이로 센서 이용해서 알아내야 할듯) > 경로 (이건 다른 쪽에서 하기)

        route = astar.search(currentLocation, destination);

        StringBuilder r = new StringBuilder();

        for(String n : route) {
            r.append(n).append("->");
        }

        return r.toString();
    }

    public String getDirection(int x, int y, String nextLocation) {
        int x1 = x;
        int y1 = y;
        int x2 = astar.findLocationInfo(nextLocation).getX();
        int y2 = astar.findLocationInfo(nextLocation).getY();

        //내가 변환한 cell 값이랑 tm_x tm_y 값이랑 반대방향? 이라서 tm_x tm_y로 바꿀 땐 음수가 왼쪽, 양수가 오른쪽이어야 함.
        if(getAngle(x1, y1, x2, y2) < 0) {
            Log.d("Astar", x + " " + y + " " + x2 + " " + y2);
            return "오른쪽";
        } else {
            Log.d("Astar", x + " " + y + " " + x2 + " " + y2);
            return "왼쪽";
        }
    }

    private double getAngle(int x1, int y1, int x2, int y2) {
        double rad = Math.atan2(y2 - y1, x2 - x1);
        return (rad*180)/Math.PI ;
    }
}

class Astar {
    private PriorityQueue<Node> openList;
    private ArrayList<Node> closedList;
    HashMap<Node, Integer> gMaps;
    HashMap<Node, Integer> fMaps;
    private int initialCapacity = 100;

    public ArrayList<Node> n = new ArrayList<>();

    Context mContext;

    private static final String LOG_TAG = "Astar";

    public Astar(Context context) {
        gMaps = new HashMap<Node, Integer>();
        fMaps = new HashMap<Node, Integer>();
        openList = new PriorityQueue<Node>(initialCapacity, new fCompare());
        closedList = new ArrayList<Node>();

        this.mContext = context;

        createMap();
    }

    private void createMap() {
        DBAdapter db = new DBAdapter(mContext);

        String sql = String.format("select IDX, CELL_X, CELL_Y, LOCATION_INFO, NEIGHBORS from TR_FPDB_MAP where LOCATION_INFO is not null");
        Cursor cursor = db.search(sql);

        int i = 0;

        if(cursor != null) {
            while (cursor.moveToNext()) {
                int idx = cursor.getInt(cursor.getColumnIndex("IDX"));
                int x = cursor.getInt(cursor.getColumnIndex("CELL_X"));
                int y = cursor.getInt(cursor.getColumnIndex("CELL_Y"));
                String locationInfo = cursor.getString(cursor.getColumnIndex("LOCATION_INFO"));

                String[] neighborsIdx = cursor.getString(cursor.getColumnIndex("NEIGHBORS")).split("/");

                n.add(new Node(idx, x, y, locationInfo.replace(" ", ""), neighborsIdx));
            }
            cursor.close();
        }

        try {
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(int j = 0; j<n.size(); j++) {
            for(String neighborIdx : n.get(j).getNeighborsIdx()) {
                int nIdx = Integer.parseInt(neighborIdx);

                for(int k=0; k<n.size(); k++) {
                    if(n.get(k).getIdx() == nIdx) {
                        n.get(j).addNeighbor(n.get(k));
                    }
                }
            }
        }
    }

    public ArrayList<String> search(String currentLocation, String destination) {


        Node start = this.findLocationInfo(currentLocation);
        Node end = this.findLocationInfo(destination);

        if(start == null || end == null) {
            return null;
        }

        openList.clear();
        closedList.clear();

        gMaps.put(start, 0);
        openList.add(start);

        while (!openList.isEmpty()) {
            Node current = openList.element();

            if (current.equals(end)) {
                Log.d(LOG_TAG, "SUCCESS ASTAR");
                return printPath(current);
            }
            closedList.add(openList.poll());
            ArrayList<Node> neighbors = current.getNeighbors();

            for (Node neighbor : neighbors) {
                int gScore = gMaps.get(current) + h(neighbor, current); //실제 거리
                int fScore = gScore + h(neighbor, end); //시작노드 ~ 목표노드까지 추정 거리

                if (closedList.contains(neighbor)) {
                    if (gMaps.get(neighbor) == null) {
                        gMaps.put(neighbor, gScore);
                    }
                    if (fMaps.get(neighbor) == null) {
                        fMaps.put(neighbor, fScore);
                    }

                    if (fScore >= fMaps.get(neighbor)) {
                        continue;
                    }
                }
                if (!openList.contains(neighbor) || fScore < fMaps.get(neighbor)) {
                    neighbor.setParent(current);
                    gMaps.put(neighbor, gScore);
                    fMaps.put(neighbor, fScore);
                    if (!openList.contains(neighbor)) {
                        openList.add(neighbor);

                    }
                }
            }
        }

        Log.d(LOG_TAG, "NO PATH");
        return null;
    }

    //어떤 거리 알고리즘 쓸지에 따라 정확성, 속도 등 달라짐
    private int h(Node node, Node goal) {
        int x = node.getX() - goal.getX();
        int y = node.getY() - goal.getY();
        return x * x + y * y;
    }

    private ArrayList<String> printPath(Node node) {
        ArrayList<String> route = new ArrayList<>();

        route.add((String) node.getLocationInfo());

        while (node.getParent() != null) {
            node = node.getParent();
            route.add((String) node.getLocationInfo());
        }

        Collections.reverse(route);

        return route;
    }

    public Node findLocationInfo(String locationInfo) {
        for(int i=0; i<n.size(); i++) {
            if(n.get(i).getLocationInfo().equals(locationInfo.replace(" ", ""))) {
                Log.d("Astar", n.get(i).getLocationInfo() + " " + i);
                return n.get(i);
            }
        }

        Log.d("Astar", "find location info 실패");
        return null;
    }

    class fCompare implements Comparator<Node> {

        public int compare(Node o1, Node o2) {
            if (fMaps.get(o1) < fMaps.get(o2)) {
                return -1;
            } else if (fMaps.get(o1) > fMaps.get(o2)) {
                return 1;
            } else {
                return 1;
            }
        }
    }
}

class Node {
    private Node parent;
    private ArrayList<Node> neighbors;

    private int idx;
    private int x;
    private int y;
    private String locationInfo;
    private String[] neighborsIdx;

    public Node(int idx, int x, int y, String locationInfo, String[] neighborsIdx) {
        neighbors = new ArrayList<Node>();

        this.idx = idx;
        this.x = x;
        this.y = y;
        this.locationInfo = locationInfo;
        this.neighborsIdx = neighborsIdx;
    }

    public ArrayList<Node> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(ArrayList<Node> neighbors) {
        this.neighbors = neighbors;
    }

    public void addNeighbor(Node node) {
        this.neighbors.add(node);
    }

    public void addNeighbors(Node... node) {
        this.neighbors.addAll(Arrays.asList(node));
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public int getIdx() {
        return idx;
    }

    public String[] getNeighborsIdx() {
        return neighborsIdx;
    }

    public String getLocationInfo() {
        return locationInfo;
    }

    public boolean equals(Node n) {
        return this.x == n.x && this.y == n.y;
    }
}


