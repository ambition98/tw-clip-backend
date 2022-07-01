package com.isedol_clip_backend.web.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
public class ReqCategoryDto {
    @NotBlank(message = "Required Parameter. String categoryName")
    private String categoryName;
}
