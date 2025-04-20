package com.lovelycatv.ai.crystalapp.data

import com.lovelycatv.ai.crystalapp.common.utils.startWith

/**
 * @author lovelycat
 * @since 2025-04-19 03:16
 * @version 1.0
 */
interface ChatHistoryMessage<T: ChatHistoryMessage<T>> {
    val children: List<T>

    /**
     * Get the sub-type instance of [ChatHistoryMessage]
     *
     * @return Real instance of [ChatHistoryMessage]
     */
    fun getMessageEntity(): T

    fun addChildNode(child: T)

    fun addChildNodes(children: Collection<T>)

    fun isLeafNode() = this.children.isEmpty()

    fun getMessageByBranchPath(path: BranchPath): T {
        return this.getMessageChainByBranchPath(path, true).last()
    }

    fun getMessageChainByBranchPath(path: BranchPath, leafOnly: Boolean = false): MutableList<T> {
        var currentHeader = this.getMessageEntity()
        val chain = mutableListOf<T>()

        if (!leafOnly) {
            chain.add(currentHeader)
        }

        /**
         * Only one element
         */
        if (this.children.isEmpty()) {
            return chain
        }

        var counter = 0

        while (currentHeader.children.isNotEmpty() && counter < path.branchIndexes.size) {
            val next = currentHeader.children[path.branchIndexes[counter]]
            if (!leafOnly || counter == path.branchIndexes.size - 1) {
                chain.add(next)
            }
            currentHeader = next
            counter++
        }

        return chain
    }

    fun findAllAvailableLeaves(nodeFilter: NodeFilter<T>): List<List<T>> {
        val paths = this.findAllLeafPaths()

        val originalLeafNodeWithAvailableMessageChain = paths.map {
            // Find out all leaf paths
            this.getMessageChainByBranchPath(BranchPath(it))
        }.associateBy {
            // Associate the original leaf node with message chain it belongs to
            nodeFilter.getUniqueId(it.last())
        }.mapValues {
            // Filter out all available nodes in message chain
                (_, messageChain) -> messageChain.filter { nodeFilter.isAvailable(it) }
        }

        // Prerequisite: Any node has more than 1 available sub-tree must be available.

        // Currently, the Actual Leaf Node (mark as A) should be the last node in every chains.
        // If the Original Leaf Node is equals to A, then this chain will be recognized as valid. (mark all chains fit this condition as C1)
        // Otherwise, the validity of chain still needs to be verified. (mark all chains fit this condition as C2)

        val c1 = mutableListOf<List<T>>()
        val c2 = mutableListOf<List<T>>()

        originalLeafNodeWithAvailableMessageChain.forEach { (originalLeafNodeKey, filteredChain) ->
            val actualLeafNode = filteredChain.last()
            if (originalLeafNodeKey == nodeFilter.getUniqueId(actualLeafNode)) {
                c1.add(filteredChain)
            } else {
                c2.add(filteredChain)
            }
        }

        // * How to verify the validity?
        // - If the leaf node of chain in C2 is a child node of any chain in C1,
        //   then this chain is invalid.
        //   Otherwise this chain is still valid.
        // - In another case, if there are x and y (y != x) in C2 => y startWith x, then x is invalid.
        //   Eg: If the c2 is [[0, 1, 0, 0], [0, 1, 0, 0, 2, 1, 0]], [0, 1, 0, 0] ∈ [0, 1, 0, 0, 2, 1, 0],
        //       then [0, 1, 0, 0] is a invalid chain.

        val validChainsInC2 = c2.filter { chainToBeVerified ->
            val leafNode = chainToBeVerified.last()

            val condition1 = !c1.any {
                chainInC1 -> chainInC1.any {
                    nodeFilter.getUniqueId(it) == nodeFilter.getUniqueId(leafNode)
                }
            }

            val condition2 = c2.any { y -> y != chainToBeVerified && y.startWith(chainToBeVerified) }

            condition1 && !condition2
        }

        return c1 + validChainsInC2
    }

    fun findAllLeafPaths(): List<List<Int>> {
        return this.findAllLeafPaths(this)
    }

    private fun findAllLeafPaths(t: ChatHistoryMessage<T>): List<MutableList<Int>> {
        if (t.children.isEmpty()) {
            return emptyList()
        }

        val result = mutableListOf<MutableList<Int>>()

        t.children.forEachIndexed { i, it ->
            val res = findAllLeafPaths(it)
            if (res.isEmpty()) {
                result.add(mutableListOf(i))
            } else {
                // If the path of this child is not empty,
                // add the index of this child before all of the returned paths
                result.addAll(res.onEach { it.add(0, i) })
            }
        }

        return result
    }

    interface NodeFilter<T> {
        fun getUniqueId(t: T): Any

        fun isAvailable(t: T): Boolean
    }
}