package com.zjjqtech.bimplatform.controller;

import com.zjjqtech.bimplatform.service.BimProjectService;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zao
 * @date 2020/09/21
 */
@Controller
public class StaticController implements ErrorController {

    private final BimProjectService bimProjectService;

    public StaticController(BimProjectService bimProjectService) {
        this.bimProjectService = bimProjectService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PreAuthorize("hasRole('ADMIN') || @bimProjectService.canAccess(#bimProjectId)")
    @GetMapping("/bim-project/{type}/{bimProjectId}/{modelName}/**")
    public ResponseEntity<InputStreamResource> getBimProjectFileResource(@PathVariable String type, @PathVariable String bimProjectId, @PathVariable String modelName, HttpServletRequest request) {
        final String path = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString(),
                bestMatchingPattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();
        return bimProjectService.getFileResource(type, bimProjectId, modelName, new AntPathMatcher().extractPathWithinPattern(bestMatchingPattern, path));
    }

    @PreAuthorize("hasRole('ADMIN') || @bimProjectService.canAccess(#bimProjectId, #shareToken)")
    @GetMapping("/bim-project/share/{shareToken:.{6}}/{type}/{bimProjectId}/{modelName}/**")
    public ResponseEntity<InputStreamResource> getBimProjectFileResource(@PathVariable String shareToken, @PathVariable String type, @PathVariable String bimProjectId, @PathVariable String modelName, HttpServletRequest request) {
        final String path = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString(),
                bestMatchingPattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();
        return bimProjectService.getFileResource(type, bimProjectId, modelName, new AntPathMatcher().extractPathWithinPattern(bestMatchingPattern, path));
    }

    @GetMapping("/bim-project/cover/{type}/{bimProjectId}/{fileName:.*}")
    public ResponseEntity<InputStreamResource> getBimProjectFileResource(@PathVariable String type, @PathVariable String bimProjectId, @PathVariable String fileName) {
        return bimProjectService.getFileResource(type, bimProjectId, null, fileName);
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping("/error")
    public String error(HttpServletRequest req, HttpServletResponse resp) {
        if (HttpStatus.NOT_FOUND.value() == (Integer)req.getAttribute("javax.servlet.error.status_code")){
            resp.setStatus(HttpStatus.OK.value());
        }
        return "index";
    }

}
