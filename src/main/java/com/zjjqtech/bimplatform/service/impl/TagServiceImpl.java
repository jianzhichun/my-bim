package com.zjjqtech.bimplatform.service.impl;

import com.zjjqtech.bimplatform.model.Tag;
import com.zjjqtech.bimplatform.model.TagAbbr;
import com.zjjqtech.bimplatform.repository.TagRepository;
import com.zjjqtech.bimplatform.service.TagService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author zao
 * @date 2020/09/24
 */
@Service
@Transactional(rollbackOn = Exception.class)
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Page<TagAbbr> find(String nameLike, Pageable pageable) {
        return this.tagRepository.findByNameLike(nameLike, pageable);
    }

    @Override
    public List<TagAbbr> findByProjectIdLimit3(String projectId) {
        return this.tagRepository.findByProjectIdLimit3(projectId);
    }

    @Override
    public Tag find(String name) {
        return this.tagRepository.findFirstByName(name);
    }

    @Override
    public void delete(String name) {
        this.tagRepository.deleteByName(name);
    }
}
