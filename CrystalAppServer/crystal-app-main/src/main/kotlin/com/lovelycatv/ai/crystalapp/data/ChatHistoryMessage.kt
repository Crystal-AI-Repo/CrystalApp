package com.lovelycatv.ai.crystalapp.data

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

    fun getMessageChainByBranchPath(path: BranchPath): MutableList<T> {
        var currentHeader = this.getMessageEntity()
        val chain = mutableListOf(currentHeader)

        /**
         * Only one element
         */
        if (this.children.isEmpty()) {
            return chain
        }

        var counter = 0

        while (currentHeader.children.isNotEmpty() && counter < path.branchIndexes.size) {
            val next = currentHeader.children[path.branchIndexes[counter++]].getMessageEntity()
            chain.add(next)
            currentHeader = next
        }

        return chain
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
}