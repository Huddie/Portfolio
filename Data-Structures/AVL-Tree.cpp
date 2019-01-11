#include <iostream>





template <T> class BinNode {
	private:
		T data;
		BinNode<T> next;
	public:
		BinNode<T>();
		BinNode<T> getNext();
		T getData();
};

template <T> BinNode::BinNode<T>(T data, BinNode<T> next) {
	this->data = data;
	this->next = next;:x
}
template <T> BinNode::BinNode<T>() {
	data = null;
	next = null;
}

template <T> T BinNode::getData() {
	return this->data;
}

template <T> BinNode<T> BinNode::getNext() { 
	return this->next;
}

template <T> class AVLTree: public BinTree {
	private:
		BinNode<T> head;
	public:
		AVTree();
		print_inorder();

};





int main() {


}
