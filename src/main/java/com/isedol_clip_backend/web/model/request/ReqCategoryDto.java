package com.isedol_clip_backend.web.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
public class ReqCategoryDto {
    @NotEmpty
    private String categoryName;
}
