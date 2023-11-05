package xju.fjj.webpan.service;

import xju.fjj.webpan.entity.dto.SessionShareDto;
import xju.fjj.webpan.entity.pojo.ShareInfo;
import xju.fjj.webpan.entity.query.ShareInfoQuery;
import xju.fjj.webpan.entity.vo.FileShareVo;
import xju.fjj.webpan.entity.vo.PagedResult;
import xju.fjj.webpan.entity.vo.ShareInfoVo;

import java.util.List;

public interface ShareInfoService {
    PagedResult<List<FileShareVo>> findListByPage(ShareInfoQuery query);

    void saveShareInfo(ShareInfo shareInfo);

    void deleteBatch(String userId, List<Integer> shareIds);

    FileShareVo getFileShareVoById(Integer shareId);

    SessionShareDto checkShareCode(Integer shareId, String code);

    ShareInfoVo getShareInfoVoFromId(Integer shareId);
}
