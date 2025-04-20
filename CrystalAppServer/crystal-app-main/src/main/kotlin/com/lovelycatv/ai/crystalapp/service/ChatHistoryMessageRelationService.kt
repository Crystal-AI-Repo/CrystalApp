package com.lovelycatv.ai.crystalapp.service

import com.baomidou.mybatisplus.extension.service.IService
import com.lovelycatv.ai.crystalapp.common.ServiceFuncResult
import com.lovelycatv.ai.crystalapp.data.BranchPath
import com.lovelycatv.ai.crystalapp.entity.ChatHistoryMessageEntity
import com.lovelycatv.ai.crystalapp.entity.ChatHistoryMessageRelationEntity

/**
 * @author lovelycat
 * @since 2025-04-14 20:38
 * @version 1.0
 */
interface ChatHistoryMessageRelationService : IService<ChatHistoryMessageRelationEntity?> {
    fun getChildrenNodes(parentId: Long): List<ChatHistoryMessageRelationEntity> {
        return this.batchGetChildrenNodes(listOf(parentId))[parentId] ?: emptyList()
    }

    fun batchGetChildrenNodes(parentIds: Collection<Long>): Map<Long, List<ChatHistoryMessageRelationEntity>>

    fun addChildNode(parentId: Long, childId: Long): ServiceFuncResult<*>

    /**
     * Get the parent node relation
     *
     * @param currentId Current nodeId
     * @return If the given [currentId] is root, the return value will be null
     */
    fun getParentNode(currentId: Long): ChatHistoryMessageRelationEntity?

    /**
     * Search for the root node upwards based on the given child node: [leafNodeId]
     *
     * @param leafNodeId From this node.
     * @param depth If the given depth is not null, the root node may not be found.
     * @return All nodes in the path, the last element is root.
     */
    fun searchUpwardsForRoot(leafNodeId: Long, depth: Long?): ServiceFuncResult<List<ChatHistoryMessageRelationEntity>>

    fun searchPathToNode(targetId: Long): ServiceFuncResult<BranchPath?>
}