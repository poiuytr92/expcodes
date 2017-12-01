/*
	Author:     Exp
	Date:       2017-12-01
	Code:       POJ 2255
	Problem:    Tree Recovery
	URL:		http://poj.org/problem?id=2255
*/

/*
	题意分析：
	  给出一棵二叉树的前序遍历与中序遍历，求后序遍历.
	  其中二叉树由不重复的大写字母组成，每个节点一个字母.

	解题思路：
	 ① 前序遍历的第一个字母必是 根
	 ② 在中序遍历的字母串中找出 根字母，那么根字母左右两边的字符串就分别是它的左、右子树
	 ③ 由于树的最大深度只有26，因此可以利用递归结合①②复原二叉树
	 ④ 对复原的二叉树使用DFS做后序遍历即可

	 注：
	  对二叉树的 前序遍历、中序遍历、后序遍历 均可以使用DFS来做，
	  均是自上而下、自左至右遍历，区别在于打印节点的时机不同：
	  [前序遍历] 从父节点进入时打印当前节点
	  [中序遍历] 从左子树返回时打印当前节点
	  [后序遍历] 从右子树返回时打印当前节点
*/

#include <memory.h>
#include <iostream>
using namespace std;

const static int STR_LEN = 27;
const static char NULL_CHAR = '\0';

class Node {
	public:
		char name;
		Node* left;
		Node* right;

		Node(): name(NULL_CHAR), left(NULL), right(NULL) {}

		bool operator == (const Node& other) {
			return (this == &other || this->name == other.name);
		}

		bool operator != (const Node& other) {
			return !(operator == (other));
		}
};


Node* createTree(char* preOrder, char* inOrder);
void dfs(Node* node, char* _out_postOrder);

int main(void) {
	char preOrder[STR_LEN] = { NULL_CHAR };
	char inOrder[STR_LEN] = { NULL_CHAR };

	while(cin >> preOrder >> inOrder) {
		Node* root = createTree(preOrder, inOrder);
		char postOrder[STR_LEN] = { NULL_CHAR };
		dfs(root, postOrder);
		cout << postOrder << endl;

		memset(preOrder, STR_LEN, sizeof(char) * STR_LEN);
		memset(inOrder, STR_LEN, sizeof(char) * STR_LEN);
	}
	return 0;
}


Node* createTree(char* preOrder, char* inOrder) {
	const int LEN = strlen(preOrder);
	if(LEN <= 0) {
		return NULL;
	}

	Node* root = new Node();
	root->name = *preOrder;	// 前序遍历的第一个节点必定是树根

	char* leftPreOrder = new char[LEN + 1];		// 左子树前序列
	char* leftInOrder = new char[LEN + 1];		// 左子树中序列
	char* rigntPreOrder = new char[LEN + 1];	// 右子树前序列
	char* rigntInOrder = new char[LEN + 1];		// 右子树中序列
	strcpy(leftPreOrder, preOrder);
	strcpy(leftInOrder, inOrder);
	strcpy(rigntPreOrder, preOrder);
	strcpy(rigntInOrder, inOrder);

	// 根据根节点在中序序列的位置，调整左右子树的前序序列和中序序列范围
	for(int i = 0; *(inOrder + i) != NULL_CHAR; i++) {
		if(root->name == *(inOrder + i)) {
			*(leftInOrder + i) = NULL_CHAR;
			int leftLen = strlen(leftInOrder);
			*(leftPreOrder + leftLen + 1) = NULL_CHAR; 

			Node* leftTree = createTree((leftPreOrder + 1), leftInOrder);
			Node* rightTree = createTree((rigntPreOrder + leftLen + 1), (rigntInOrder + i + 1));
			root->left = leftTree;
			root->right = rightTree;
			break;
		}
	}

	delete[] leftPreOrder;
	delete[] leftInOrder;
	delete[] rigntPreOrder;
	delete[] rigntInOrder;
	return root;
}

void dfs(Node* node, char* _out_postOrder) {
	if(node == NULL) {
		return;
	}

	dfs(node->left, _out_postOrder);
	dfs(node->right, _out_postOrder);

	*(_out_postOrder + strlen(_out_postOrder)) = node->name;
}