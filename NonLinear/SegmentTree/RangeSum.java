package NonLinear.SegmentTree;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

public class RangeSum {
    static long[] arr;
    static long[] tree;

    public static long init(int start, int end, int node) {
        if(start == end) {
            tree[node] = arr[start];
            return tree[node];
        }
        int mid = (start + end) / 2;
        return tree[node] = init(start, mid, node * 2) + init(mid+1, end, node * 2 + 1);
    }

    public static long query(int start, int end, int left, int right, int node) {
        if(end < left || start > right) return 0;
        if(left <= start && end <= right) return tree[node];
        int mid = (start + end) / 2;
        return query(start, mid, left, right, node * 2) + query(mid+1, end, left, right, node * 2 + 1);
    }

    public static void update(int start, int end, int node, int index, long diff) {
        if(index < start || index > end) return;
        tree[node] += diff;
        if(start == end) return;

        int mid = (start + end) / 2;
        update(start, mid, node * 2, index, diff);
        update(mid + 1, end, node * 2 + 1, index, diff);
    }

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // 1) 배열 크기 입력
        System.out.print("배열의 크기(N)을 입력: ");
        int N = Integer.parseInt(br.readLine());

        arr = new long[N + 1];      // 1-based index 사용
        tree = new long[4 * N];     // 세그먼트 트리 배열

        // 2) 배열 원소 입력
        System.out.println(N + "개의 배열 원소를 입력해 주세요.");
        StringTokenizer st = new StringTokenizer(br.readLine());
        for(int i = 1; i <= N; i++) {
            arr[i] = Long.parseLong(st.nextToken());
        }

        // 3) 세그먼트 트리 초기화
        init(1, N, 1);

        // 4) 사용자에게 명령어 안내
        System.out.println("====================================");
        System.out.println("[사용 방법]");
        System.out.println(" 1) query left right");
        System.out.println("     - arr[left..right] 구간 합을 출력");
        System.out.println(" 2) update index newValue");
        System.out.println("     - arr[index]를 newValue로 갱신");
        System.out.println(" 3) q 또는 quit");
        System.out.println("     - 프로그램 종료");
        System.out.println("====================================");

        while(true) {
            System.out.print("명령어 입력 >> ");
            String line = br.readLine();
            if(line == null || line.trim().isEmpty()) {
                continue; // 공백 입력이 들어오면 무시
            }

            st = new StringTokenizer(line);
            String cmd = st.nextToken();

            // 종료 조건
            if(cmd.equals("q") || cmd.equals("quit")) {
                System.out.println("프로그램을 종료합니다.");
                break;
            }
            // 구간 합 질의
            else if(cmd.equals("query")) {
                if(st.countTokens() < 2) {
                    System.out.println("query 명령어 사용법: query left right");
                    continue;
                }
                int left = Integer.parseInt(st.nextToken());
                int right = Integer.parseInt(st.nextToken());

                // 범위 확인(1 <= left <= right <= N)
                if(left < 1 || right > N || left > right) {
                    System.out.println("잘못된 범위입니다.");
                    continue;
                }

                long result = query(1, N, left, right, 1);
                System.out.println(String.format("arr[%d..%d] 합: %d", left, right, result));
            }
            // 값 업데이트
            else if(cmd.equals("update")) {
                if(st.countTokens() < 2) {
                    System.out.println("update 명령어 사용법: update index newValue");
                    continue;
                }
                int index = Integer.parseInt(st.nextToken());
                long newValue = Long.parseLong(st.nextToken());

                // 범위 확인
                if(index < 1 || index > N) {
                    System.out.println("인덱스 범위가 유효하지 않습니다.");
                    continue;
                }

                long diff = newValue - arr[index];
                update(1, N, 1, index, diff);
                arr[index] = newValue; // 실제 배열도 갱신
                System.out.println(String.format("arr[%d]가 %d로 업데이트 되었습니다.", index, newValue));
            }
            // 그 외 명령어 처리
            else {
                System.out.println("알 수 없는 명령어입니다. (query / update / q / quit)");
            }

            System.out.println(Arrays.toString(arr));
        }
    }
}
