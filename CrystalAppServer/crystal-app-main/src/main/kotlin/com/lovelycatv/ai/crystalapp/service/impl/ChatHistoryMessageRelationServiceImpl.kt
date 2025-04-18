package com.lovelycatv.ai.crystalapp.service.impl

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.lovelycatv.ai.crystalapp.common.ServiceFuncResult
import com.lovelycatv.ai.crystalapp.common.utils.getOneByColumn
import com.lovelycatv.ai.crystalapp.entity.ChatHistoryMessageEntity
import com.lovelycatv.ai.crystalapp.entity.ChatHistoryMessageRelationEntity
import com.lovelycatv.ai.crystalapp.mapper.ChatHistoryMessageRelationMapper
import com.lovelycatv.ai.crystalapp.service.ChatHistoryMessageRelationService
import org.springframework.stereotype.Service

/**
 * @author lovelycat
 * @since 2025-04-14 20:38
 * @version 1.0
 */
@Service
class ChatHistoryMessageRelationServiceImpl : ChatHistoryMessageRelationService, ServiceImpl<ChatHistoryMessageRelationMapper, ChatHistoryMessageRelationEntity?>() {
    override fun getChildrenNodes(parentId: Long): List<ChatHistoryMessageRelationEntity> {
        return list(
            QueryWrapper<ChatHistoryMessageRelationEntity>().eq("current_id", parentId)
        ).filterNotNull().sortedBy { it.orderNo }
    }

    override fun addChildNode(parentId: Long, childId: Long): ServiceFuncResult<*> {
        val children = this.getChildrenNodes(parentId)
        return if (save(ChatHistoryMessageRelationEntity(parentId, childId, children.size))) {
            ServiceFuncResult.success("Success")
        } else {
            ServiceFuncResult.failed("Could not add relation between Parent: [$parentId] and Child: [$childId]")
        }
    }

    /**
     * Get the parent node relation
     *
     * @param currentId Current nodeId
     * @return If the given [currentId] is root, the return value will be null
     */
    override fun getParentNode(currentId: Long): ChatHistoryMessageRelationEntity? {
        return this.getOneByColumn("next_id" to currentId)
    }

    /**
     * Search for the root node upwards based on the given child node: [leafNodeId]
     *
     * @param leafNodeId From this node
     * @param includeLeaf If true, the first element of result will be the given node
     * @return All nodes in the path, the last element is root
     */
    override fun searchUpwardsForRoot(
        leafNodeId: Long,
        includeLeaf: Boolean
    ): ServiceFuncResult<List<ChatHistoryMessageRelationEntity>> {
        var currentNode: ChatHistoryMessageRelationEntity? = this.getParentNode(leafNodeId) ?: return ServiceFuncResult.failedWithData("Leaf $leafNodeId not found", emptyList())

        val result = mutableListOf<ChatHistoryMessageRelationEntity>()

        if (includeLeaf) {
            result.add(currentNode!!)
        }

        while (currentNode != null) {
            currentNode = this.getParentNode(currentNode.currentId)
            if (currentNode != null) {
                result.add(currentNode)
            }
        }

        return ServiceFuncResult.success("", result)
    }
}