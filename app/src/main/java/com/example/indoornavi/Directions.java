package com.example.indoornavi;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Vector;
/*
class node {
    int id;
    int x;
    int y;
    String location_info;

    node(int id, int x, int y, String location_info) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.location_info = location_info;
    }
}

public class Directions {
    //경로생성, 진행방향 제시
    Context mContext;
    Dikstra dikstra;
    int id = 0;
    ArrayList<node> m = new ArrayList<>();

    TTS tts;

    public Directions(Context context) {
        mContext = context;
        tts = new TTS(mContext);
        createMap();
    }

    public void createMap() {

        m.add(new node(1, 1, 1, "교차로"));

        double weight = Math.sqrt(Math.pow((m.get(i).x - m.get(j).x), 2) + Math.pow((m.get(i).y - m.get(j).y), 2));
        dikstra.input(m.get(i).id, m.get(j).id, weight);
    }

    public boolean startDirections(int x, int y) {
        int current_x = x;
        int current_y = y;

        String destination = "2번 출구";
        String direction = getDirection(current_x, current_y, destination);

        System.out.println("경로 : " + direction);
        return false;
    }

    public String getDirection(int x, int y, String destination) {
        //경로 배열 보고 방향 제시
        //만약 여기서 제시한 방향대로 안가고 반대로 갔다(자이로 센서 이용해서 알아내야 할듯) > 경로 (이건 다른 쪽에서 하기)
        int current_x = x;
        int current_y = y;
        String d = destination;

        int start_node = 0;
        for (int i = 0; i < m.size(); i++) {
            if (current_x == m.get(i).x && current_y == m.get(i).y) {
                start_node = m.get(i).id;
                break;
            }
        }

        int end_node = 0;
        for (int i = 0; i < m.size(); i++) {
            if (m.get(i).location_info.equals(destination)) {
                end_node = m.get(i).id;
            }
        }

        Vector<Integer> route = dikstra.start(start_node, end_node);
        return route.toString();
    }
}

class Dikstra {
    private int n;                  //노드들의 수
    private double weights[][];    //노드들간의 가중치 저장할 변수

    public Dikstra(int n) {
        this.n = n;
        weights = new double[n][n];
    }

    public void input(int i, int j, double w) {
        weights[i][j] = w;
        weights[j][i] = w;
    }

    public Vector<Integer> start(int start, int end) {
        double distance[] = new double[n];     //최단 거리를 저장할 변수
        boolean[] visit = new boolean[n];     //해당 노드를 방문했는지 체크할 변수

        int prev[] = new int[n];
        int stack[] = new int[n];

        Vector<Integer> route = new Vector<>();

        for (int i = 0; i < n; i++) {
            //초기화
            distance[i] = Integer.MAX_VALUE;
            prev[i] = 0;
            visit[i] = false;
        }

        distance[start] = 0; //시작점의 거리는 0

        int k = 0;
        int min;
        for (int i = 0; i < n; i++) {
            min = Integer.MAX_VALUE;
            for (int j = 0; j < n; j++) {
                //정점의 수만큼 반복
                if (visit[j] == false && distance[j] < min) {
                    //확인하지 않고 거리가 짧은 정점을 찾음
                    k = j;
                    min = (int) distance[j];
                }
            }
            visit[k] = true; //해당정점확인체크

            if (min == Integer.MAX_VALUE)
                break;

            // I->J 보다 I->K->J의 거리가 더 작으면 갱신.
            for (int j = 0; j < n; j++) {
                if (distance[k] + weights[k][j] < distance[j]) {
                    distance[j] = distance[k] + weights[k][j]; //최단거리 저장
                    prev[j] = k; // j로 가기위해 k를 거쳐야함
                }
            }
        }

        //콘솔에서 최단 경로 출력;
        int tmp;
        int top = -1;
        tmp = end - 1;
        while (true) {
            stack[++top] = tmp + 1;
            if (tmp == start - 1) break;
            tmp = prev[tmp];
        }

        //역추적 결과 출력
        route.removeAllElements();
        for (int i = top; i > -1; i--) {
            System.out.printf("%d", stack[i]);
            route.add(stack[i]);
        }

        return route;
    }
}




*/

