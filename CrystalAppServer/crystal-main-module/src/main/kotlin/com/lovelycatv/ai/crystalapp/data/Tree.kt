package com.lovelycatv.ai.crystalapp.data

/**
 * @author lovelycat
 * @since 2025-04-19 04:20
 * @version 1.0
 */
data class Tree<T>(val data: T) : ChatHistoryMessage<Tree<T>> {
    private val _children: MutableList<Tree<T>> = mutableListOf()
    override val children: List<Tree<T>> = this._children

    /**
     * Get the sub-type instance of [ChatHistoryMessage]
     *
     * @return Real instance of [ChatHistoryMessage]
     */
    override fun getMessageEntity(): Tree<T> {
        return this
    }

    override fun addChildNodes(children: Collection<Tree<T>>) {
        this._children.addAll(children)
    }

    override fun addChildNode(child: Tree<T>) {
        this._children.add(child)
    }
}