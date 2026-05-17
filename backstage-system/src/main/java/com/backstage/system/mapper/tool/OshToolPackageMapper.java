package com.backstage.system.mapper.tool;

import com.backstage.system.domain.tool.OshToolPackage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OshToolPackageMapper {

    List<OshToolPackage> selectPackagesByToolId(@Param("toolId") Long toolId);

    List<OshToolPackage> selectPackagesByToolIds(@Param("toolIds") List<Long> toolIds);

    int insertToolPackage(OshToolPackage toolPackage);

    int updateToolPackage(OshToolPackage toolPackage);

    int softDeletePackagesByToolId(@Param("toolId") Long toolId, @Param("operator") String operator);
}
