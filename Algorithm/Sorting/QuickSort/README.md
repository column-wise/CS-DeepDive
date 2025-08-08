# 퀵 소트(Quick Sort)

퀵 소트는 평균 $O(nlogn)$, 최악의 경우 $O(n^2)$ 의 시간 복잡도를 갖는 정렬 알고리즘이다.

불안정 정렬에 속하며, 분할 정복(Divide and Conquer)으로 구현된다.

## 동작 방식

1. pivot 이라고 불리는 원소를 1개 선정
2. pivot 보다 작은 원소들은 pivot 왼쪽으로, pivot 보다 큰 원소들은 오른쪽으로 몰아놓음(`partition` 메서드)
3. pivot 기준 왼쪽 부분 배열, 오른쪽 부분 배열에 대해 부분 배열의 길이가 0 또는 1이 될 때까지 
   1,2,3 과정을 재귀적으로 수행

```java
public static void QuickSort(int[] arr, int start, int end) {
	int pivotIndex = partition(arr, start ,end);
	
	QuickSort(arr, start, pivotIndex - 1);
	QuickSort(arr, pivotIndex + 1, end);
}
```

## 구현
partition 메서드가 핵심인데, 내가 최초 구현한 partition 메서드는 다음과 같다.

```java
private static int partition(int[] arr, int start, int end) {
    int pivot = arr[end];
    int i = start, j = end -1;

    while(i <= j) {
        if(arr[i] > pivot && arr[j] < pivot) {
            swap(arr, i, j);
            i++;
            j--;
        } else if(arr[i] < pivot) {
            i++;
        } else {
            j--;
        }
    }

    swap(arr, i, end);
    return i;
}
```

ex. [2, 8, 7, 3, 6, 1, 4]

<img width="2636" height="7422" alt="Image" src="https://github.com/user-attachments/assets/dc53fa84-a0cb-4c24-a967-aa1b65512864" />

동작하긴 하지만, 중복된 원소가 많은 배열이 입력되면 비효율적으로 동작한다.  
극단적으로 모든 원소가 중복일 경우, 재귀의 깊이는 N-1로 깊어지고, partition 내 while 문은 swap 없이 j-- 만 N번 수행되므로  
시간 복잡도가 $O(n^2)$이 된다.

이미 정렬된/역정렬된 배열이 입력되어도 시간 복잡도가 $O(n^2)$이 된다...

찾아보니 이미 잘 알려진 파티션 알고리즘이 있었다.

### 호어(Hoare) 파티션 알고리즘

퀵 정렬 알고리즘을 개발한 토니 호어의 partition 알고리즘이다.

```java
public static void QuickSort(int[] arr, int start, int end) {
	if(start >= end) return;

	int pivotIndex = partition(arr, start, end);
	QuickSort(arr, start, pivotIndex);
	QuickSort(arr, pivotIndex + 1, end);
}

private static int partition(int[] arr, int start, int end) {
    int pivot = arr[start]; // Hoare는 보통 첫 원소를 pivot
    int left = start - 1;
    int right = end + 1;

    while (true) {
        do { left++; } while (arr[left] < pivot);
        do { right--; } while (arr[right] > pivot);

        if (left >= right) return right; // right가 경계

        swap(arr, left, right);
    }
}
```

장점:
- 중복 원소에 상대적으로 강함
- 평균적으로 적은 swap 횟수

단점:
- 반환한 index가 최종 배열에서의 pivot의 위치가 아닐 수 있음.
- 재귀 호출 시 경계 처리가 Lomuto에 비해 약간 복잡함

### 로무토(Lomuto) 파티션 알고리즘

```java
public static void QuickSort(int[] arr, int start, int end) {
	if(start >= end) return;

	int pivotIndex = partition(arr, start, end);
	QuickSort(arr, start, pivotIndex - 1);
	QuickSort(arr, pivotIndex + 1, end);
}

private static int partition(int[] a, int lo, int hi) {
    int pivot = a[hi];      // 피벗: 끝 원소
    int i = lo - 1;         // 작은 구간의 끝

    for (int j = lo; j < hi; j++) {
        if (a[j] <= pivot) {  // 안정적으로 pivot 이하를 왼쪽으로 모음
            i++;
            swap(a, i, j);
        }
    }
    swap(a, i + 1, hi);     // 피벗을 정확한 위치로
    return i + 1;           // 피벗의 최종 인덱스
}
```

장점:
- 구현이 간단하고 명확
- pivot의 정확한 위치를 반환(반환하는 인덱스가 최종 배열에서의 pivot의 위치임을 보장)
- 안정적인 분할

단점:
- Hoare 파티션보다 더 많은 swap 발생
- 중복 원소가 많을 때 Hoare 방식보다 비효율적
  [3, 3, 3, 3, 3] 입력 시 [3, 3, 3, 3] | [3] 처럼 불균등하게 분할됨