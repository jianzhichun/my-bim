package com.zjjqtech.bimplatform.controller;

import com.zjjqtech.bimplatform.model.Message;
import com.zjjqtech.bimplatform.service.MessageService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author zao
 * @date 2020/09/25
 */
@RestController
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @ApiImplicitParams(
        {
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query", value = "Results page you want to retrieve (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query", value = "Number of records per page."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                              value = "Sorting criteria in the format: property(,asc|desc). Default sort order is ascending. Multiple sort criteria are supported.")
        }
    )
    @GetMapping("/{locale}")
    public Page<Message> find(@PathVariable String locale, String nameLike, Pageable pageable) {
        return this.messageService.find(locale, nameLike, pageable);
    }

    @GetMapping("/{locale}/{key}")
    public Message find(@PathVariable String locale, @PathVariable String key) {
        return this.messageService.find(locale, key);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{locale}/{key}")
    public Message save(@PathVariable String locale, @PathVariable String key, String content) {
        return this.messageService.save(locale, key, content);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{locale}/{key}")
    public void delete(@PathVariable String locale, @PathVariable String key) {
        this.messageService.delete(locale, key);
    }

}
