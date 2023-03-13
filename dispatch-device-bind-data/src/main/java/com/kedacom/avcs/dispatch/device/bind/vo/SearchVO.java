package com.kedacom.avcs.dispatch.device.bind.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liuyue
 * @date 2022/4/14 16:18
 */
@Data
public class SearchVO {

    @ApiModelProperty(value="页码",name="pageNo",required = true)
    private Integer pageNo = 0;

    @ApiModelProperty(value="页数",name="pageSize",required = true)
    private Integer pageSize = 20;

}
