UML

Group
---
+ String description = null // overwrite this to give a more descriptive string to display in results
+ static void main(String[] args) // initiate an instance of Runner and pass it an instance of this group
+ void before() // no-op by default, override to “enable”
+ void beforeEach()
+ void afterEach()
+ void after()
+ null | Group[] visit(ResultsTree results) // used by Runner evaluate tests in a Group and for traversing nested Groups

Runner
---
- Group[] groups
+ Runner(…Group group)
+ ResultsTree run() // traverse each Group in this.groups recursively using group.visit() and saving results to a tree to return
+ void renderResults(ResultsTree results, OutputStream outputTarget) // render results to given stream

ResultsTree
---
- ResultsNode* leftRoot // a reference to the first Node of the tree, name accepts the possibility of multiple roots as a linked list
+ void preorder(Fn visit) // preorder traversal, executing given visit function on every node
// TODO: define methods for inserting new Nodes as children of and or siblings to a given Node

ResultsNode
---
// implements a Node as a member of a Tree (by having 0..1 parent and 0..many children, and as a doubly linked list by having 0..1 previous Node and 0..1 next Node, as well as a head Node and tail Node
- ResultsNode* parent
- ResultsNode* headChild
- ResultsNode* tailChild
- ResultsNode* nextSibling
- ResultsNode* previousSibling
+ ResultsNode* getParent()
+ void setParent(ResultsNode* value)
+ ResultsNode* getHeadChild()
+ void setHeadChild(ResultsNode* value)
+ ResultsNode* getTailChild()
+ void setTailChild(ResultsNode* value)
+ ResultsNode* getNextSibling()
+ void setNextSibling(ResultsNode* value)
+ ResultsNode* getPreviousSibling()
+ void setPreviousSibling(ResultsNode* value)
// TODO: define constructor and define fields required to store data related to a test’s success or failure, including error information (e.g stack trace)
// also define method for representing state as a (possibly multi-line) string
