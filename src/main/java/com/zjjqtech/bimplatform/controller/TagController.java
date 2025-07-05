package com.zjjqtech.bimplatform.controller;

import com.zjjqtech.bimplatform.model.Tag;
import com.zjjqtech.bimplatform.model.TagAbbr;
import com.zjjqtech.bimplatform.service.TagService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zao
 * @date 2020/09/24
 */
@RestController
@RequestMapping("/api/tag")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query", value = "Results page you want to retrieve (0..N)"),
                    @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query", value = "Number of records per page."),
                    @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                            value = "Sorting criteria in the format: property(,asc|desc). Default sort order is ascending. Multiple sort criteria are supported.")
            }
    )
    @GetMapping
    public Page<TagAbbr> find(String nameLike, Pageable pageable) {
        return this.tagService.find(nameLike, pageable);
    }

    @GetMapping("/{name}")
    public Tag find(@PathVariable String name) {
        return this.tagService.find(name);
    }

    @GetMapping("/{id}/findByProjectIdLimit3")
    public List<TagAbbr> findByProjectIdLimit3(@PathVariable String id) {
        return this.tagService.findByProjectIdLimit3(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{name}")
    public void delete(@PathVariable String name) {
        this.tagService.delete(name);
    }
}
