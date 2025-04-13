package com.lovelycatv.auth.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.fasterxml.jackson.annotation.JsonIgnore
import com.lovelycatv.ai.crystalapp.common.data.DataBaseEntity
import com.lovelycatv.ai.crystalapp.common.data.Desensitization
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * @author lovelycat
 * @since 2025-04-09 19:41
 * @version 1.0
 */
@TableName("users")
data class UserEntity(
    @TableId(type = IdType.AUTO)
    var id: Long,
    @TableField("username")
    private var username: String,
    @TableField("password")
    private var password: String,
    @TableField("nickname")
    var nickname: String,
    @TableField("email")
    var email: String,
    @TableField("avatar")
    var avatar: String,
    @TableField("registered_time")
    var registeredTime: Long,
    @TableField("modified_time")
    var modifiedTime: Long,
    @TableField("activated")
    var activated: Boolean
) : UserDetails, Desensitization, DataBaseEntity {

    fun toPublicVO() = PublicVO(id, username, nickname, avatar, activated)

    data class PublicVO(
        var id: Long,
        var username: String,
        var nickname: String,
        var avatar: String,
        var activated: Boolean
    )

    @TableField(exist = false)
    private val authorities: MutableCollection<GrantedAuthority> = mutableListOf()

    fun addAuthority(authority: GrantedAuthority) {
        this.authorities.add(authority)
    }

    override fun getAuthorities(): MutableCollection<GrantedAuthority> {
        return this.authorities
    }

    fun setUsername(value: String) {
        this.username = value
    }

    override fun getUsername(): String {
        return this.username
    }

    fun setPassword(value: String) {
        this.password = value
    }

    override fun getPassword(): String {
        return this.password
    }

    @JsonIgnore
    override fun isAccountNonExpired(): Boolean {
        return super.isAccountNonExpired()
    }

    @JsonIgnore
    override fun isAccountNonLocked(): Boolean {
        return super.isAccountNonLocked()
    }

    @JsonIgnore
    override fun isCredentialsNonExpired(): Boolean {
        return super.isCredentialsNonExpired()
    }

    @JsonIgnore
    override fun isEnabled(): Boolean {
        return super.isEnabled()
    }

    override fun desensitize() {
        this.password = ""
        this.email = ""
    }

}