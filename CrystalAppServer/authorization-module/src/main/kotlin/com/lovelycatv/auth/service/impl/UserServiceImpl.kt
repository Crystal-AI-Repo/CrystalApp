package com.lovelycatv.auth.service.impl

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.lovelycatv.auth.entity.RoleEntity
import com.lovelycatv.auth.entity.UserEntity
import com.lovelycatv.auth.mapper.UserMapper
import com.lovelycatv.auth.service.RoleService
import com.lovelycatv.auth.service.UserRoleService
import com.lovelycatv.auth.service.UserService
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * @author lovelycat
 * @since 2025-04-09 19:44
 * @version 1.0
 */
@Service
class UserServiceImpl(
    private val userMapper: UserMapper,
    private val userRoleService: UserRoleService,
    private val roleService: RoleService
) : UserService, ServiceImpl<UserMapper, UserEntity?>(), UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails {
        if (username == null) throw UsernameNotFoundException("Username could not be null")

        val user = getOne(
            QueryWrapper<UserEntity>()
                .eq("username", username)
                .or()
                .eq("email", username)
        ) ?: throw UsernameNotFoundException("User $username not found")

        // Get user permissions
        val roles = userRoleService.getUserRoleIds(user.id)
        val roleWithPermissions = roleService.getRolesTree(roles.map { it.roleId }, true)

        roleWithPermissions.forEach {
            addAuthoritiesForUser(user, it)
        }

        return user
    }

    private fun addAuthoritiesForUser(user: UserEntity, roleEntity: RoleEntity) {
        user.addAuthority(SimpleGrantedAuthority("ROLE_${roleEntity.role}"))
        roleEntity.permissions.forEach {
            user.addAuthority(SimpleGrantedAuthority(it.permission))
        }
        roleEntity.children.forEach {
            addAuthoritiesForUser(user, it)
        }
    }

    override fun getMapper(): UserMapper {
        return this.userMapper
    }
}