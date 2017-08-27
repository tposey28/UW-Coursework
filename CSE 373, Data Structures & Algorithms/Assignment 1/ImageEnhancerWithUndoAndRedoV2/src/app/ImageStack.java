//package app;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
//ImageStack
//Taylor Posey
//CSE 373 A, Autumn 2014
//This object manages BufferedImages using a LIFO stack.

public class ImageStack{
	private ArrayList<BufferedImage> stack;
	private boolean popWasLast;
	private int size;
	
	public ImageStack() {
		stack = new ArrayList<BufferedImage>();
		popWasLast = false;
		size = 0;
	}
	
	public void push(BufferedImage newImage) {
		stack.add(newImage);
		popWasLast = false;
		size++;
	}
	
	public BufferedImage pop() {
		emptyCheck();
		popWasLast = true;
		size--;
		return stack.remove(size);
	}

	public BufferedImage peek() {
		emptyCheck();
		popWasLast = false;
		return stack.get(size - 1);
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
	public void clear() {
		stack.removeAll(stack);
		popWasLast = false;
		size = 0;
	}
	
	public boolean popWasLast() {
		return popWasLast;
	}
	
	public int getSize() {
		return size;
	}
	
	private void emptyCheck() {
		if (isEmpty()) {
			throw new IllegalStateException();
		}
	}
}
