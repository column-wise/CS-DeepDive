package Algorithm.Sorting.QuickSort;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

public class QuickSort {
	public static void quickSort(int[] arr, int start, int end) {
		if(start >= end) return;

		int pivotIndex = partition(arr, start, end);
		quickSort(arr, start, pivotIndex - 1);
		quickSort(arr, pivotIndex + 1, end);
	}

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

	private static void swap(int[] arr, int i, int j) {
		int temp = arr[i];
		arr[i] = arr[j];
		arr[j] = temp;
	}

	public static void main(String[] args) throws Exception {
		System.out.print("Size of Array: ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;
		int N = Integer.parseInt(br.readLine());

		int[] arr = new int[N];
		System.out.print("Enter Elements Separated By Space: ");
		st = new StringTokenizer(br.readLine());

		for(int i = 0; i < N; i++) {
			arr[i] = Integer.parseInt(st.nextToken());
		}

		System.out.println("Original Array: " + Arrays.toString(arr));
		quickSort(arr, 0, N - 1);
		System.out.println("Sorted Array: " + Arrays.toString(arr));
	}
}
