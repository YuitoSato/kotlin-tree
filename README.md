# kotlin-tree 🌳

Kotlin Declarative APIs for Multi-way Tree Data.

Easy to convert trees to other tree models, Path Enumeration Models and Adjacency Models, etc.

## Installation

This library is in preparation for release.

## Quick Start

```kt
val treeNode: TreeNode<Int> = nodeOf(
    1,
    mutableListOf(
        nodeOf(
            11,
            mutableList(
                leafOf(111),
                leafOf(112)
            )
        ),
        leafOf(12)
    )
)
// 1
// ├── 11
// │   ├── 111
// │   └── 112
// └── 12

treeNode.map { ele -> ele * 2 }
// 2
// ├── 22
// │   ├── 222
// │   └── 224
// └── 24

treeNode.filter { ele -> ele % 2 != 0 }
// 1
// └── 11
//     └── 111

treeNode.find { ele -> ele == 11 }
// 11
// └── 111

treeNode.forEach { ele -> println(ele) }
// => 1
// => 11
// => 111
// => 112
// => 12
```

## Examples

### Adjacency Models <-> Trees

```kt
// Adjacency Models -> Trees
val adjacencyList = AdjacencyList.of(
    getSelfNodeId = { it },
    list = listOf(
        null to 1,
        1 to 11,
        11 to 111,
        1 to 12,
        null to 2,
        2 to 21,
        3 to 31 // the parentNodeId is not found
    )
)
val (treeNodes, parentNodeNotFoundList) = adjacencyList.toTreeNode()
// treeNodes
// 1
// ├── 11
// │   ├── 111
// │   └── 112
// └── 12
// 2
// └── 21
// parentNodeNotFoundList
// (parentNodeId: 3, selfNodeId: 31)
```

```kt
// Trees -> Adjacency Models
val treeNode = nodeOf(
    1,
    mutableListOf(
        nodeOf(
            11,
            mutableList(
                leafOf(111),
                leafOf(112)
            )
        ),
        leafOf(12)
    )
)
val adjacencyList = AdjacencyList.fromTreeNode(
    treeNode
)
// (parentNodeId, selfNodeID)
// (null, 1)
// (1, 11)
// (11, 111)
// (1, 12)
```

### PathEnumeration Models <-> Trees

```kt
// PathEnumeration Models -> Trees
val pathEnumerationList = PathEnumerationList.of(
    listOf(1) to 1,
    listOf(1, 11) to 11,
    listOf(1, 11, 111) to 111,
    listOf(1, 12) to 12,
    listOf(2) to 2,
    listOf(2, 21) to 21,
    listOf(3, 31) to 31,
    // the path is not found
)
val (treeNodes, parentNodeNotFoundList) =
    pathEnumerationList.toTreeNode()
// treeNodes
// 1
// ├── 11
// │   ├── 111
// │   └── 112
// └── 12
// 2
// └── 21
// parentNodeNotFoundList
// (path: 3/31, value: 31)
```

```kt
// Trees -> PathEnumeration Models
val treeNode = nodeOf(
    1,
    mutableListOf(
        nodeOf(
            11,
            mutableList(
                leafOf(111),
                leafOf(112)
            )
        ),
        leafOf(12)
    )
)
val pathEnumerationList = PathEnumerationList.fromTreeNode(treeNode)
// (path, value)
// (1, 1)
// (1/11, 11)
// (1/11/111, 111)
// (1/12, 12)
```

