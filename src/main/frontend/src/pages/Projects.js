import React, {useCallback, useEffect, useState} from "react";
import {useHistory} from "react-router-dom";
import {useTranslation} from "react-i18next";
import {Input, Card, List, Row, Divider, Spin} from 'antd';
import axios from "../axios";
import {PlusCircleOutlined} from '@ant-design/icons';
import ProjectDelete from '../components/ProjectDelete';
import TagSelect from '../components/TagSelect';
import TagsView from '../components/TagsView';

const {Search} = Input;
export default ({mine}) => {
    const {t} = useTranslation(),
        history = useHistory(),
        size = 7,
        [newProjectLoading, setNewProjectLoading] = useState(false),
        [nameLike, setNameLike] = useState(''),
        [tags, setTags] = useState([]),
        [total, setTotal] = useState(0),
        [totalPages, setTotalPages] = useState(0),
        [page, setPage] = useState(0),
        [items, setItems] = useState([]),
        [isSearching, setIsSearching] = useState(false),
        search = useCallback((nameLike, size, page, tags) => {
            setIsSearching(() => true);
            axios.get(`/bim-project${mine ? '/self' : ''}`, {
                params: {
                    nameLike: `%${nameLike}%`,
                    size,
                    page,
                    tags: tags.join(',')
                }
            }).then(page => {
                setItems(() => [{}, ...page.content]);
                setPage(() => page.number);
                setTotal(() => page.totalElements);
                setTotalPages(() => page.totalPages);
                setIsSearching(() => false);
            }).catch(() => setIsSearching(() => false));
        }, [mine]);
    useEffect(() => {
        const timer = setTimeout(() => search(nameLike, size, page, tags), 100);
        return () => clearTimeout(timer);
    }, [search, nameLike, page, tags]);
    return <div>
        <Row justify="center" gutter={16}>
            <Search style={{maxWidth: '60%'}}
                    placeholder={t('project.search.nameLike.placeholder')}
                    enterButton
                    size={"large"}
                    onSearch={setNameLike}
                    loading={isSearching}
                    prefix={<TagSelect mode={'multiple'} onChange={setTags} value={tags}
                                       style={{minWidth: '150px', maxWidth: '300px'}}
                                       placeholder={t('project.search.tag.placeholder')}/>}
            />
        </Row>
        <Divider/>
        <Row justify="center" gutter={16}>
            <List
                style={{width: '75%'}}
                grid={{gutter: 16, column: 4}}
                pagination={{
                    total: total + totalPages,
                    current: page + 1,
                    onChange: (p) => setPage(p - 1),
                    pageSize: size + 1
                }}
                dataSource={items}
                renderItem={item => (
                    <List.Item>
                        <Card style={{width: 220, height: 225, textAlign: "center", cursor: 'pointer'}}
                              hoverable
                              extra={item.id && <ProjectDelete id={item.id} onSuccess={() => {
                                  if (items.length === 2) {
                                      setPage(page - 1);
                                  } else {
                                      search(nameLike, size, page, tags);
                                  }
                              }}/>}
                              size="small"
                              onClick={() => {
                                  if (item.id) {
                                      history.push(`/bim/project/${item.id}`);
                                  } else {
                                      setNewProjectLoading(true);
                                      axios.put('/bim-project/', {
                                          name: t('project.newName'),
                                          ext: {description: t('project.newName')}
                                      }).then(project => {
                                          setNewProjectLoading(false);
                                          history.push(`/bim/project/${project.id}`);
                                      }).catch(() => {
                                          setNewProjectLoading(false);
                                      });
                                  }
                              }}
                              title={item.id && <div style={{textAlign: 'left'}}>{item.name}</div>}
                              cover={item.id && <div style={{maxWidth: '220px', height: '145px'}}>
                                  <img src={(item.ext && item.ext.coverUrl) || `${process.env.PUBLIC_URL}/logo.png`}
                                       style={{maxWidth: '100%', maxHeight: '100%'}}
                                       alt={`${process.env.PUBLIC_URL}/logo.png`}/>
                              </div>}
                        >
                            {!item.id ?
                                (newProjectLoading ?
                                        <Spin size={"large"} style={{paddingTop: '70px'}}/> :
                                        <PlusCircleOutlined
                                            style={{fontSize: '120px', color: 'grey', paddingTop: '30px'}}/>
                                ) : <Card.Meta description={<TagsView id={item.id} />} />
                            }
                        </Card>
                    </List.Item>
                )}
            />
        </Row>
    </div>;
};