package com.dzh.sys.mapper;

import com.dzh.sys.entity.RoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 角色菜单映射表 Mapper 接口
 * </p>
 *
 * @author DongZH
 * @since 2023-04-11
 */
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    List<Integer> getMenuIdListByRoleId(Integer roleId);
}
