# kotlin-tree 🌳

Kotlin Declarative APIs for Multi-way Tree Data.

Easy to convert trees to other tree models, Path Enumeration Models and Adjacency Models, etc.

## Installation

### Gradle

```kts
dependencies {
    implementation("io.github.yuitosato:kotlin-tree:3.0.0")
}
```

### Maven

```pom
<dependency>
    <groupId>io.github.yuitosato</groupId>
    <artifactId>kotlin-tree</artifactId>
    <version>3.0.1</version>
</dependency>
```

## Definition

The base class TreeNode is defined as follows.

TreeNode has the value property, which is the content of the node, and the children property, which is the node's child
nodes.

```kt
interface TreeNode<T> {
    val value: T
    val children: List<TreeNode<T>>
}
```

## Quick Start

You can create a tree node instance and operate the contents of nodes easily and simply.

```kt
val treeNode: TreeNode<Int> = nodeOf(
    1,
    listOf(
        nodeOf(
            11,
            listOf(
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
```

## Examples

### DSL Style 
```kt
val treeNode: TreeNode<Int> = nodeOf(1) {
    addNode(11) {
        addLeaf(111)
        addLeaf(112)
    }
    addLeaf(12)
}
// 1
// ├── 11
// │   ├── 111
// │   └── 112
// └── 12
```

### Tree Operations

kotlin-tree provides APIs that operates the contents of nodes like Kotlin Collection APIs such as map, filter, etc.
Also, kotlin-tree provides APIs that operates the nodes themselves. (It means lambda blocks receive TreeNode instances,
not the contents of nodes T.)
These methods are named xxxNode, mapNode, filterNode, etc.

```kt
val treeNode: TreeNode<Int> = nodeOf(
    1,
    listOf(
        nodeOf(
            11,
            listOf(
                leafOf(111),
                leafOf(112)
            )
        ),
        leafOf(12)
    )
)
println(treeNode.toFormattedString)
// 1
// ├── 11
// │   ├── 111
// │   └── 112
// └── 12

treeNode.map { ele -> ele * 2 }
treeNode.mapNode { node -> node.value * 2 }
// 2
// ├── 22
// │   ├── 222
// │   └── 224
// └── 24

treeNode.filter { ele -> ele % 2 != 0 }
treeNode.filterNode { node -> node.value % 2 != 0 }
// 1
// └── 11
//     └── 111

treeNode.find { ele -> ele > 10 }
treeNode.findNode { node -> node.value > 10 }
// [
//   11
//   ├── 111,
//   └── 112,
//   111,
//   112,
//   12
// ]

treeNode.forEach { ele -> println(ele) }
treeNode.forEachNode { node -> println(node.value) }
// => 1
// => 11
// => 111
// => 112
// => 12

treeNode.fold(0) { (acc, ele) -> acc + ele }
treeNode.foldNode(0) { (acc, node) -> node.value + ele }
// (1 + 11 + 111 + 112 + 12)

treeNode.withIndices()
// ([], 1)
// ├── ([0], 11)
// │   ├── ([0, 0], 111)
// │   └── ([0, 1], 112)
// └── ([1], 12)

treeNode.getOrElse(listOf(0, 1))
// => leafOf(112)
```

### Adjacency Models -> Trees Conversions

```kt
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

### Trees -> Adjacency Models Conversions

```kt
val treeNode = nodeOf(
    1,
    listOf(
        nodeOf(
            11,
            listOf(
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

### PathEnumeration Models -> Trees Conversions

```kt
val pathEnumerationList = PathEnumerationList.of(
    listOf(1) to 1,
    listOf(1, 11) to 11,
    listOf(1, 11, 111) to 111,
    listOf(1, 12) to 12,
    listOf(2) to 2,
    listOf(2, 21) to 21,
    listOf(3, 31) to 31, // the path is not found
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

### Trees -> PathEnumeration Models Conversions

```kt
val treeNode = nodeOf(
    1,
    listOf(
        nodeOf(
            11,
            listOf(
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

# 📝 License

Copyright © 2023 YuitoSato.

This project is licensed under Apache 2.0.
