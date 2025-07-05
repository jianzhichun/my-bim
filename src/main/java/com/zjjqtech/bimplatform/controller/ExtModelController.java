package com.zjjqtech.bimplatform.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.zjjqtech.bimplatform.model.ExtModel;
import com.zjjqtech.bimplatform.service.ExtModelService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author zao
 * @date 2020/09/24
 */
@RestController
@RequestMapping("/api/ext-model")
public class ExtModelController {

    private final ExtModelService extModelService;

    public ExtModelController(ExtModelService extModelService) {
        this.extModelService = extModelService;
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
    public Page<ExtModel> find(String nameLike, Pageable pageable) {
        return this.extModelService.find(nameLike, pageable);
    }

    @GetMapping("/{name}")
    public ExtModel find(@PathVariable String name) {
        return this.extModelService.find(name);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{name}")
    public void delete(@PathVariable String name) {
        this.extModelService.delete(name);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{name}")
    public ExtModel save(@PathVariable String name, @RequestBody JsonNode ext) {
        return this.extModelService.save(name, ext);
    }
}
