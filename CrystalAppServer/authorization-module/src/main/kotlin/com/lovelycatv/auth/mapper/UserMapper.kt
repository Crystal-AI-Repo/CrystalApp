package com.lovelycatv.auth.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.lovelycatv.auth.entity.PermissionEntity
import com.lovelycatv.auth.entity.UserEntity
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

/**
 * @author lovelycat
 * @since 2025-04-09 19:42
 * @version 1.0
 */
@Mapper
interface UserMapper : BaseMapper<UserEntity?> {
    @Select("" +
        "SELECT * FROM user_permissions AS x WHERE x.id IN " +
        "(SELECT y.permission_id FROM role_permission_relations AS y WHERE y.role_id IN " +
        "(SELECT z.role_id FROM user_role_relations AS z WHERE z.user_id = #{userId}))" +
        "")
    fun getUserPermissions(userId: Long): List<PermissionEntity>
}