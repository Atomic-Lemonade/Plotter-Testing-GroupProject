package com.maths;
import java.util.Vector;



public class TNode {

	String label=null;
	TNode parent=null;
	Vector children=null;
	
	boolean marked=false;
	Object value=null;
	

	public boolean isMarked() {
		return marked;
	}

	public void setMarked(boolean marked) {
		this.marked = marked;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public TNode getParent() {
		return parent;
	}

	public void setParent(TNode parent) {
		this.parent = parent;
	}

	
	
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public TNode(){
		
		children=new Vector();
	}
	
	public TNode(Object value){
		
		children=new Vector();
		this.value=value;
	}
	
	public TNode(TNode parent){
		
		this.parent=parent;
		children=new Vector();
	}
	
	public TNode(TNode parent,Object value){
		
		this.parent=parent;
		children=new Vector();
		this.value=value;
	}
	
	public void appendChild(TNode child){
		
		child.setParent(this);
		children.add(child);
	}

	public int getChildCount() {
		
		
		return children.size();
	}

	public TNode getChildAt(int i) {
		
		return (TNode) children.elementAt(i);
	}
	
	public void removeChildAt(int i) {
		
		children.removeElementAt(i);
	}
	
	public void removeChild(TNode tNode) {
		
		children.remove(tNode);
	}
	
	public void clearTnode(){
		
		children=new Vector();
		
	}
	
	public String toString(){
		
		if(value==null)
			return null;
		
		return value.toString();
	}

	public void print() {

		System.out.println(toString());
		
		for (int i = 0; i <   getChildCount(); i++) {

			TNode child = getChildAt(i);
			child.print();
			

		}
		

	}

	public void printBranches() {
		
		if(getChildCount()==0)
		{
			Vector nodes=new Vector();
			
			nodes.add(this);
			TNode currentNode=this;
			TNode parent=null;
			
			while((parent=currentNode.getParent())!=null){
				
				nodes.add(parent);
				currentNode=parent;
			}
			System.out.println();
			for (int i = nodes.size()-1; i >=0; i--) {

				TNode member=(TNode) nodes.elementAt(i);
				System.out.print(member.toString());
				if(i>0)
					System.out.print("->");
			}
			System.out.println();
		}
			
			
			
		for (int i = 0; i <   getChildCount(); i++) {

			TNode child = getChildAt(i);
			child.printBranches();
			

		}
		
	}
	
	
	public void printTree() {
		
		System.out.print(toString());
		
		if(getChildCount()==0)
			return;
		
		System.out.print("(");
		for (int i = 0; i <   getChildCount(); i++) {

			if(i>0)
				System.out.print(",");
			
			TNode child = getChildAt(i);
			child.printTree();
			

		}
		System.out.print(")");
	}

	public void setLabel(char ch) {
		setLabel(""+ch);
		
	}
	
}
