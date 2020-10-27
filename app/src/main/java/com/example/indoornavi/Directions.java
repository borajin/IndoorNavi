package com.example.indoornavi;

public class Directions {
    //경로생성, 진행방향 제시

    int[][] map;

    public Directions(int[][] map) {
        createMap(map);
    }

    public void createMap(int[][] map) {
        //지도 데이터베이스를 다익스트라 알고리즘 적용하기 좋은 형태로 변환

        this.map = map;
    }

    public void startDirections(int x, int y) {
        int current_x = x;
        int current_y = y;

        String subway_route[] = {"티켓", "개찰구", "앨리베이터"};
        int i = 0;

        while(true) {
            String destination = subway_route[i];
            String direction = getDirection(current_x, current_y, destination);

            //이때 자이로센서가 왼쪽, 오른쪽을 인식하면 원래 방향으로 돌아올 수 있도록 오른쪽, 왼쪽으로 복귀하라고 안내. (이건 어려울 거 같으니 일단 패스)
            if(direction.equals("straight")) {
                //"다음 점자블록까지 직진하세요"
            } else if(direction.equals("right")) {
                //"오른쪽으로 돌아 직진하세요"
            } else if(direction.equals("left")) {
                //"왼쪽으로 돌아 직진하세요"
            } else if(direction.equals("arrive")) {
                //"목적지에 도착했습니다."
                i++;
            }
        }
    }

    public String getDirection(int x, int y, String destination) {
        //경로 배열 보고 방향 제시
        //만약 여기서 제시한 방향대로 안가고 반대로 갔다(자이로 센서 이용해서 알아내야 할듯) > 경로 (이건 다른 쪽에서 하기)
        int current_x = x;
        int current_y = y;
        String d = destination;

        int[] route = createRoute(current_x, current_y, d);

        //route 로 진행방향 알아내기

        return "direction";
    }

    public int[] createRoute(int x, int y, String destination) {
        //현재 위치 입력받아 길찾기 알고리즘 돌려 경로 배열 반환
        int current_x = x;
        int current_y = y;
        String d = destination;

        int[] route = Dikstra(current_x, current_y, d);

        return route;
    }

    public int[] Dikstra(int x, int y, String destination) {
        int[] route = new int[0];

        return route;
    }
}
