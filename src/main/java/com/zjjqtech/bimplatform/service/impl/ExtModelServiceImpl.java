package com.zjjqtech.bimplatform.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.zjjqtech.bimplatform.model.ExtModel;
import com.zjjqtech.bimplatform.repository.ExtModelRepository;
import com.zjjqtech.bimplatform.service.ExtModelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * @author zao
 * @date 2020/09/24
 */
@Service
@Transactional(rollbackOn = Exception.class)
public class ExtModelServiceImpl implements ExtModelService {

    private final ExtModelRepository extModelRepository;

    public ExtModelServiceImpl(ExtModelRepository extModelRepository) {
        this.extModelRepository = extModelRepository;
    }

    @Override
    public Page<ExtModel> find(String nameLike, Pageable pageable) {
        return this.extModelRepository.findByNameLike(nameLike, pageable);
    }

    @Override
    public ExtModel find(String name) {
        return this.extModelRepository.findFirstByName(name);
    }

    @Override
    public void delete(String name) {
        this.extModelRepository.deleteByName(name);
    }

    @Override
    public ExtModel save(String name, JsonNode ext) {
        ExtModel extModel = this.extModelRepository.findFirstByName(name);
        if (null == extModel) {
            extModel = new ExtModel();
            extModel.setName(name);
        }
        extModel.setExt(ext);
        return this.extModelRepository.save(extModel);
    }
}
