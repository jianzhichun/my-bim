package com.zjjqtech.bimplatform.controller;

import com.zjjqtech.bimplatform.controller.utils.Result;
import com.zjjqtech.bimplatform.model.BimProject;
import com.zjjqtech.bimplatform.model.BimProjectAbbr;
import com.zjjqtech.bimplatform.model.FileUploadArgs;
import com.zjjqtech.bimplatform.model.User;
import com.zjjqtech.bimplatform.security.SecurityConfig;
import com.zjjqtech.bimplatform.service.BimProjectService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

/**
 * @author zao
 * @date 2020/09/21
 */
@RestController
@RequestMapping("/api/bim-project")
public class BimProjectController {

    private final BimProjectService bimProjectService;

    public BimProjectController(BimProjectService bimProjectService) {
        this.bimProjectService = bimProjectService;
    }

    @PreAuthorize("hasRole('ADMIN') || @bimProjectService.checkIsOwnerOfBimProject(#id)")
    @PutMapping("/{id}")
    public BimProject update(@PathVariable String id, @RequestBody BimProject bimProject) {
        bimProject.setId(id);
        return bimProjectService.save(bimProject);
    }

    @PreAuthorize("hasRole('ADMIN') || @bimProjectService.checkIsOwnerOfBimProject(#id)")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        bimProjectService.delete(id);
    }

    @PutMapping
    public BimProject create(@RequestBody BimProject bimProject, @AuthenticationPrincipal @ApiIgnore User self) {
        List<User> owners = bimProject.getOwners();
        if (owners == null) {
            bimProject.setOwners(Collections.singletonList(self));
        } else if (owners.stream().map(User::getId).noneMatch(self.getId()::equals)) {
            owners.add(self);
        }
        return bimProjectService.save(bimProject);
    }

    @GetMapping("/{name}")
    public BimProject find(@PathVariable String name) {
        return bimProjectService.find(name);
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
    public Page<BimProjectAbbr> find(String nameLike, @RequestParam(value = "tags") List<String> tags, @ApiIgnore Pageable pageable) {
        return bimProjectService.find(nameLike, tags, pageable);
    }

    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query", value = "Results page you want to retrieve (0..N)"),
                    @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query", value = "Number of records per page."),
                    @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                            value = "Sorting criteria in the format: property(,asc|desc). Default sort order is ascending. Multiple sort criteria are supported.")
            }
    )
    @GetMapping("/self")
    public Page<BimProjectAbbr> findMine(String nameLike, @RequestParam(value = "tags") List<String> tags, @ApiIgnore Pageable pageable) {
        return SecurityConfig.getUser().map(User::getId).map(id -> bimProjectService.find(id, nameLike, tags, pageable)).orElse(Page.empty());
    }

    @PreAuthorize("hasRole('ADMIN') || @bimProjectService.checkIsOwnerOfBimProject(#id)")
    @GetMapping("/{type}/{id}/{modelName}/**")
    public FileUploadArgs getModelFileUploadUrl(@PathVariable String type, @PathVariable String id, @PathVariable String modelName, HttpServletRequest request, String contentType) {
        final String path = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString(),
                bestMatchingPattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();
        return bimProjectService.getModelFileUploadArgs("/bim-project", type, id, modelName, new AntPathMatcher().extractPathWithinPattern(bestMatchingPattern, path), contentType);
    }

    @PreAuthorize("hasRole('ADMIN') || @bimProjectService.checkIsOwnerOfBimProject(#id)")
    @PostMapping("/{type}/{id}/{modelName}/setMainModel")
    public void setMainModel(@PathVariable String type, @PathVariable String id, @PathVariable String modelName) {
        bimProjectService.setMainModel(type, id, modelName);
    }

    @PreAuthorize("hasRole('ADMIN') || @bimProjectService.checkIsOwnerOfBimProject(#id)")
    @PostMapping("/{type}/{id}/{oldName}/rename")
    public void renameModel(@PathVariable String type, @PathVariable String id, @PathVariable String oldName, String newName) {
        bimProjectService.renameModel(type, id, oldName, newName);
    }

    @PreAuthorize("hasRole('ADMIN') || @bimProjectService.checkIsOwnerOfBimProject(#id)")
    @DeleteMapping("/{type}/{id}/{modelName}")
    public void deleteModel(@PathVariable String type, @PathVariable String id, @PathVariable String modelName) {
        bimProjectService.deleteModel(type, id, modelName);
    }

    @SneakyThrows
    @PreAuthorize("hasRole('ADMIN') || @bimProjectService.checkIsOwnerOfBimProject(#id)")
    @PostMapping("/{type}/{id}/setCover")
    public void setCover(@PathVariable String type, @PathVariable String id, @RequestParam("cover") MultipartFile file) {
        bimProjectService.setCover("/bim-project/cover", type, id, file.getOriginalFilename(), file.getInputStream(), file.getSize(), file.getContentType());
    }

    @PreAuthorize("hasRole('ADMIN') || @bimProjectService.checkIsOwnerOfBimProject(#id)")
    @PostMapping("/{id}/generateShareToken")
    public Result<String> generateShareToken(@PathVariable String id) {
        return Result.of(bimProjectService.generateShareToken(id));
    }

}
