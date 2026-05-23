package com.backstage.system.mapper.document;

import com.backstage.system.domain.document.OshDoc;
import com.backstage.system.domain.document.OshDocRef;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OshDocMapper {

    OshDoc selectDocById(@Param("id") Long id);

    int insertDoc(OshDoc doc);

    int updateDocContent(OshDoc doc);

    Long selectPrimaryDocIdBySectionId(@Param("sectionId") Long sectionId);

    String selectPrimaryDocContentBySectionId(@Param("sectionId") Long sectionId);

    int softDeleteSectionDocRefs(@Param("sectionId") Long sectionId, @Param("updateBy") String updateBy);

    int insertDocRef(OshDocRef ref);
}
