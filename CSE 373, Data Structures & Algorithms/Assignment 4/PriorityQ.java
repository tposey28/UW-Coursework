// PriorityQ.java
// A4 Extra Credit by Taylor Posey, UWNetID: tposey28
// 
// CSE 373, University of Washington, Autumn 2014.
//
// Custom Priority Queue, implements a minimum heap and uses generic types.
// Stores the given object type in a sorted queue, where first out is the
// the smallest element of the queue. Type must be Comparable.

import java.util.List;

public class PriorityQ<T extends Comparable<T>> {
	private Object[] minHeap; // Array implementation of minimum heap
	private int size;  // Number of elements in queue
	private int comparisonsCount; // Number of comparisons made since reset
	
	// Constructs queue with a size of zero
	public PriorityQ() {
		minHeap = new Object[512];
		size = 0;
	}
	
	// Constructs queue using a given list of elements
	// Given elements are immediately sorted
	public PriorityQ(List<T> elements) {
		minHeap = new Object[256];
		while (minHeap.length < elements.size()) {
			minHeap = new Object[minHeap.length * 2];
		}
		for (int i = 0; i < elements.size(); i++) {
			minHeap[i + 1] = elements.get(i);
		}
		size = elements.size();
		buildHeap();
	}
	
	// Sorts entire queue. Assumes queue is currently sorted randomly.
	public void buildHeap() {
		for (int i = 1; i <= size / 2; i++) {
			T value = get(i);
			int hole = perculateDown(i, value);
			minHeap[hole] = value;
		}
		printComparisons();
		resetComparisonsCount();
	}
	
	// Inserts given element. Queue is then sorted.
	public void insert(T value) {
		if (size == minHeap.length - 1) {
			resize();
		}
		size++;
		int index = perculateUp(size, value);
		minHeap[index] = value;
	}
	
	// Removes and returns minimum element. Queue is then sorted.
	public T deleteMin() {
		if (isEmpty()) {
			throw new IllegalArgumentException();
		}
		T min = get(1);
		int hole = perculateDown(1, get(size));
		minHeap[hole] = minHeap[size];
		size--;
		return min;
	}
	
	// Returns boolean representing if queue is empty.
	public boolean isEmpty() {
		return size == 0;
	}
	
	// Returns int representing number of elements in queue.
	public int size() {
		return size;
	}
	
	// Returns int representing number of comparisons made since reset.
	public int comparisonsCount() {
		return comparisonsCount;
	}
	
	// Resets comparison count to 0.
	public void resetComparisonsCount() {
		comparisonsCount = 0;
	}
	
	// Prints size of queue to console.
	public void printSize() {
		System.out.println("Current priority queue size: " + size);
	}
	
	// Prints comparison count to console.
	public void printComparisons() {
		System.out.println("Comparisons count: " + comparisonsCount);
	}
	
	// Accepts index representing a temporary hole, and an element.
	// Continues to move element up tree, and other elements down, until 
	// heap is sorted again. Returns the new index for element.
	private int perculateUp(int hole, T value) {
		while (hole > 1 && value.compareTo(get(hole/2)) < 0) {
			minHeap[hole] = minHeap[hole/2];
			hole /= 2;
			comparisonsCount++;
		}
		return hole;
	}
	
	// Accepts index representing a temporary hole, and an element.
	// Continues to move element down tree, and other elements up, until 
	// heap is sorted again. Returns the new index for element.
	private int perculateDown(int hole, T value) {
		while (2*hole <= size) {
			int left = 2*hole;
			int right = left + 1;
			int min;
			if (right > size || get(left).compareTo(get(right)) < 0) {
				min = left;
			} else {
				min = right;
			}
			comparisonsCount++;
			if (get(min).compareTo(value) < 0) {
				minHeap[hole] = minHeap[min];
				hole = min;
				comparisonsCount++;
			} else {
				break;
			}
		}
		return hole;
	}
	
	// For receiving elements from object array
	@SuppressWarnings("unchecked")
	private T get(int i) {
		T value = (T) minHeap[i];
		return value;
	}
	
	// Resizes array when space is needed
	private void resize() {
		Object[] temp = minHeap;
		minHeap = new Object[minHeap.length * 2];
		for (int i = 0; i < temp.length; i++) {
			minHeap[i] = temp[i];
		}
	}
}
