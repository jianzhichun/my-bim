package com.zjjqtech.bimplatform.service.impl;

import com.zjjqtech.bimplatform.infrastructure.exception.BizException;
import com.zjjqtech.bimplatform.model.BimProject;
import com.zjjqtech.bimplatform.model.BimProjectAbbr;
import com.zjjqtech.bimplatform.model.Tag;
import com.zjjqtech.bimplatform.model.User;
import com.zjjqtech.bimplatform.repository.BimProjectRepository;
import com.zjjqtech.bimplatform.repository.TagRepository;
import com.zjjqtech.bimplatform.repository.UserRepository;
import com.zjjqtech.bimplatform.service.BimProjectService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.zjjqtech.bimplatform.security.SecurityConfig.getUser;

/**
 * @author zao
 * @date 2020/09/21
 */
public abstract class AbstractBimProjectServiceImpl implements BimProjectService {

    protected final BimProjectRepository bimProjectRepository;
    protected final TagRepository tagRepository;
    protected final UserRepository userRepository;


    public AbstractBimProjectServiceImpl(BimProjectRepository bimProjectRepository, TagRepository tagRepository, UserRepository userRepository) {
        this.bimProjectRepository = bimProjectRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BimProject find(String name) {
        return bimProjectRepository.findFirstByName(name);
    }

    @Override
    public BimProject save(BimProject bimProject) {
        if (!CollectionUtils.isEmpty(bimProject.getOwners())) {
            for (User owner : bimProject.getOwners()) {
                if (null == owner.getId() || !userRepository.existsById(owner.getId())) {
                    throw new BizException("validate.error.user.non-existed");
                }
            }
        }
        if (!CollectionUtils.isEmpty(bimProject.getTags())) {
            List<Tag> tags = new ArrayList<>(bimProject.getTags().size());
            for (Tag tag : bimProject.getTags()) {
                if (null == tag.getId()) {
                    if (!StringUtils.isEmpty(tag.getName())) {
                        Tag tag0 = tagRepository.findFirstByName(tag.getName());
                        if (null == tag0) {
                            tag0 = tagRepository.save(tag);
                        }
                        tags.add(tag0);
                    }
                } else {
                    throw new BizException("validate.error.bim-project.non-existed");
                }
            }
            bimProject.setTags(tags);
        }
        if (null != bimProject.getId()) {
            Optional<BimProject> old = bimProjectRepository.findById(bimProject.getId());
            if (old.isPresent()) {
                bimProject = old.get().merge(bimProject);
            }
        }
        return bimProjectRepository.save(bimProject);
    }

    @Override
    public void delete(String id) {
        Optional<BimProject> bimProjectHolder = bimProjectRepository.findById(id);
        if (bimProjectHolder.isPresent()) {
            BimProject bimProject = bimProjectHolder.get();
            if (CollectionUtils.isEmpty(bimProject.getModels())) {
                bimProjectRepository.deleteById(id);
            } else {
                throw new BizException("validate.error.bim-model.delete-exist");
            }
        }
    }

    @Override
    public Page<BimProjectAbbr> find(String nameLike, List<String> tags, Pageable pageable) {
        if (CollectionUtils.isEmpty(tags)) {
            return bimProjectRepository.findByNameLike(nameLike, pageable);
        } else {
            return bimProjectRepository.findByNameLikeAndTags_NameIn(nameLike, tags, pageable);
        }
    }

    @Override
    public Page<BimProjectAbbr> find(String userId, String nameLike, List<String> tags, Pageable pageable) {
        if (CollectionUtils.isEmpty(tags)) {
            return bimProjectRepository.findByNameLikeAndOwners_Id(nameLike, userId, pageable);
        } else {
            return bimProjectRepository.findByNameLikeAndTags_NameInAndOwners_Id(nameLike, tags, userId, pageable);
        }
    }


    @Override
    public boolean checkIsOwnerOfBimProject(String id) {
        return getUser().map(user -> this.bimProjectRepository.existsByIdAndOwners_id(id, user.getId())).orElse(false);
    }

    @Override
    public boolean canAccess(String id) {
        return this.bimProjectRepository.existsByIdAndPublicShareIsTrue(id) || checkIsOwnerOfBimProject(id);
    }

    @Override
    public boolean canAccess(String id, String shareToken) {
        return this.bimProjectRepository.existsByIdAndShareToken(id, shareToken) || checkIsOwnerOfBimProject(id);
    }

    @Override
    public String generateShareToken(String id) {
        Optional<BimProject> bimProjectHolder = this.bimProjectRepository.findById(id);
        if (bimProjectHolder.isPresent()) {
            BimProject bimProject = bimProjectHolder.get();
            String shareToken = RandomStringUtils.randomAlphabetic(6);
            bimProject.setShareToken(shareToken);
            this.bimProjectRepository.save(bimProject);
            return shareToken;
        } else {
            throw new BizException("validate.error.bim-project.non-existed");
        }
    }

}
