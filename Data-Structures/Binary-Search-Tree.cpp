// Example program
#include <iostream>
#include <iostream>



class BSTNode {

    private:
        int data;
        BSTNode* parent;
        BSTNode* left;
        BSTNode* right;

        void set_data(int);
        void set_parent(const BSTNode*);
        void set_left(const BSTNode*);
        void set_right(const BSTNode*);

    public:
        
        friend class BSTree;

        int get_data();
        BSTNode* get_left(void);
        BSTNode* get_right(void);

        BSTNode() {
            this->parent = nullptr; //(*this.parent)
            this->left = nullptr;
            this->right = nullptr;
        }
 };



class BSTree {
    
    // Mark: Private
    private:
        BSTNode* root;
        int size;
        int height; 

        BSTNode* construct_node(int);
        void _update_heightBy(int);
        void _update_sizeBy(int);
        void _insert(int);
        void _remove(int);
        void inorder(BSTNode*);
        void preorder(BSTNode*);
        void postorder(BSTNode*);
        void levelorder(BSTNode*);

        
    // Mark: Public
    public:

    // Constructors

        // Default
        BSTree() {
            root = nullptr;
            size = 0;
            height = 0;
        }

        BSTree(int root_val) {
            root = construct_node(root_val);
            _update_sizeBy(1);
            _update_heightBy(1);
        }

    // Getters
        int get_height(void);
        int get_size(void);

    // Actions
        void insert(int);
        void remove(int);

    // Informationals
        bool isEmpty(void);

    // Prints
        void print_inorder(void);
        void print_postorder(void);
        void print_levelorder(void);
        void print_preorder(void);
 };


void BSTNode::set_data(int val) {
    this->data = val;
}

BSTNode* BSTNode::get_right(void) {
    return this->right;
}

BSTNode* BSTNode::get_left(void) {
    return this->left;
}

int BSTNode::get_data(void) {
    return this->data;
}
/**
 *  Returns the current height of the tree
 */
int BSTree::get_height(void) {
    return this->height;
}

/**
 *  Returns the current size of the tree
 */
int BSTree::get_size(void) {
    return this->size;
}

bool BSTree::isEmpty(void) {
    return this->root == nullptr;
}
/** 
 * Creates a node with the given value and inserts into the tree
 * 
 * @param digit is the data for the new node
 */
void BSTree::insert(int val) {
    BSTNode* new_node = this->construct_node(val);
    BSTNode* parent;

    // Check if we are adding the root
    if(this->isEmpty()) {
        std::cout<<"ROOT SET";
        root = new_node;
    }
    else  {
        BSTNode* current = root;
        // Look for the new_nodes proper parent 
        while(current) {

            parent = current;
            current = new_node->get_data() > current->get_data() 
                ? current->get_right() 
                : current->get_left();
        }


        if(new_node->get_data() < parent->get_data()) {
            parent->set_left(new_node);
        } else {
            parent->set_right(new_node);
        }

        std::cout<<"\Parent: "<<parent->data;

        new_node->set_parent(parent);
    }
}

/** 
 * Remove the node with data matching that which was passed in 
 * 
 * @param digit is the data to match and remove
 */
void BSTree::remove(int  val) {

}

void BSTree::preorder(BSTNode* curr) {
    if(curr != nullptr) {
        std::cout<<" "<<curr->data<<" ";
        if(curr->left) preorder(curr->left);
        if(curr->right) preorder(curr->right);
    }
    else return;
}
void BSTree::inorder(BSTNode* curr) {
    if(curr != nullptr) {
        if(curr->left) inorder(curr->left);
        std::cout<<" "<<curr->data<<" ";
        if(curr->right) inorder(curr->right);
    }
    else return;
}
void BSTree::postorder(BSTNode* curr) {
    if(curr != nullptr) {
        if(curr->left) postorder(curr->left);
        if(curr->right) postorder(curr->right);
        std::cout<<" "<<curr->data<<" ";
    }
    else return;
}

void BSTree::levelorder(BSTNode* curr) {}

void BSTNode::set_parent(const BSTNode* node) {}
void BSTNode::set_right(const BSTNode* node) {}
void BSTNode::set_left(const BSTNode* node) {}

void BSTree::print_inorder(void) {
    inorder(root);
}

void BSTree::print_postorder(void){
    postorder(root);
}

void BSTree::print_levelorder(void){
    levelorder(root);
}

void BSTree::print_preorder(void){
    preorder(root);
}

BSTNode* BSTree::construct_node(int  val) {
    BSTNode* node = new BSTNode();
    node->set_data(val);
    return node;
}

int main(void) {
    BSTree b;
    int ch,tmp,tmp1;
    while(true) {
       std::cout<<std::endl<<std::endl;
       std::cout<<" Binary Search Tree Operations "<<std::endl;
       std::cout<<" ----------------------------- "<<std::endl;
       std::cout<<" 1. Insertion/Creation "<<std::endl;
       std::cout<<" 2. In-Order Traversal "<<std::endl;
       std::cout<<" 3. Pre-Order Traversal "<<std::endl;
       std::cout<<" 4. Post-Order Traversal "<<std::endl;
       std::cout<<" 5. Exit "<<std::endl;
       std::cout<<" Enter your choice : ";
       std::cin>>ch;
       switch(ch) {
           case 1 : std::cout<<" Enter Number to be inserted : ";
                    std::cin>>tmp;
                    b.insert(tmp);
                    break;
           case 2 : std::cout<<std::endl;
                    std::cout<<" In-Order Traversal "<<std::endl;
                    std::cout<<" -------------------"<<std::endl;
                    b.print_inorder();
                    break;
           case 3 : std::cout<<std::endl;
                    std::cout<<" Pre-Order Traversal "<<std::endl;
                    std::cout<<" -------------------"<<std::endl;
                    b.print_preorder();
                    break;
           case 4 : std::cout<<std::endl;
                    std::cout<<" Post-Order Traversal "<<std::endl;
                    std::cout<<" --------------------"<<std::endl;
                    b.print_postorder();
                    break;
           case 5 : return 0;

       }
    }
}