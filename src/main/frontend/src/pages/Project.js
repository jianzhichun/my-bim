import React, {useCallback, useEffect, useReducer, useRef, useState} from "react";
import {
    Descriptions,
    Input,
    PageHeader,
    Row,
    Tag,
    Spin,
    Tooltip,
    Modal,
    Upload,
    Menu,
    Dropdown, Button, Image,
    message,
    Tabs
} from 'antd';
import {useHistory} from "react-router-dom";
import {useTranslation} from "react-i18next";
import axios from "../axios";
import {
    InboxOutlined, LinkOutlined,
    ProjectOutlined, QrcodeOutlined,
    ReloadOutlined,
    ShareAltOutlined,
    UploadOutlined
} from "@ant-design/icons";
import QRCode from 'qrcode.react';
import TagSelect from '../components/TagSelect';
import UserSelect from '../components/UserSelect';
import ProjectDelete from '../components/ProjectDelete';
import BimViewer from "../components/BimViewer";


const Project = ({id, project, refresh}) => {
    const shareLinkPrefix = `${window.location.protocol}//${window.location.host}/bim-viewer/${id}?shareToken=`,
        {t} = useTranslation(),
        history = useHistory(),
        [name, setName] = useState(),
        [nameBordered, setNameBordered] = useState(false),
        [tags, setTags] = useState(),
        [owners, setOwners] = useState(),
        [description, setDescription] = useState(),
        [descriptionBordered, setDescriptionBordered] = useState(false),
        [models, setModels] = useState(),
        [shareToken, setShareToken] = useState(),
        timer = useRef(0);
    useEffect(() => {
        setName(() => project.name);
        setTags(() => project.tags);
        setOwners(() => project.owners);
        setModels(() => project.models);
        setDescription(() => project.ext && project.ext.description);
        setShareToken(() => project.shareToken);
    }, [project]);
    const update = useCallback((project) => {
            clearTimeout(timer.current);
            timer.current = setTimeout(() => axios.put(`/bim-project/${id}`, project).catch(refresh), 100);
        }, [id, refresh]),
        setTagsAndUpdate = useCallback((tags) => {
            setTags(tags);
            update({tags});
        }, [setTags, update]),
        setOwnersAndUpdate = useCallback((owners) => {
            setOwners(owners);
            update({owners});
        }, [setOwners, update]);
    return (
        <div style={{height: '100%'}}>
            <BimViewer style={{width: '100%', height: '70%'}} models={models} screenShot={blobUrl => {
                Modal.confirm({
                    icon: false,
                    okText: t('project.model.screenshotOkText'),
                    onOk(...args) {
                        const xhr = new XMLHttpRequest();
                        xhr.open('GET', blobUrl);
                        xhr.responseType = 'blob';
                        const name = blobUrl.substring(blobUrl.lastIndexOf('/') + 1);
                        xhr.onreadystatechange = function () {
                            if (this.readyState === 4 && this.status === 200) {
                                const imageBlob = this.response;
                                const imageType = imageBlob.type;
                                let imageName;
                                if (imageType.includes('png')) {
                                    imageName = `${name}.png`;
                                } else if (imageType.includes('gif')) {
                                    imageName = `${name}.gif`;
                                } else {
                                    imageName = `${name}.jpg`;
                                }
                                const form = new FormData();
                                form.append('cover', imageBlob, imageName);
                                axios.post(`/bim-project/svf/${id}/setCover`, form, {headers: {"Content-Type": "multipart/form-data"}})
                                    .then(() => message.success(t('project.model.setCoverSuccess')));
                            }
                        };
                        xhr.send();
                    },
                    content: <Image width={'100%'} src={blobUrl}/>
                })
            }}/>
            <PageHeader
                ghost={false}
                onBack={() => history.go(-1)}
                title={<Input bordered={nameBordered}
                              onFocus={() => setNameBordered(true)}
                              onBlur={() => {
                                  setNameBordered(false);
                                  update({name});
                              }}
                              value={name} onChange={e => setName(e.target.value)}
                              onPressEnter={(event) => event.target.blur()} style={{minWidth: '100px'}}/>}
                subTitle={<TagSelect mode={'tags'} onChange={setTagsAndUpdate}
                                     style={{minWidth: '200px', marginTop: '5px'}}
                                     value={tags} placeholder={t('project.edit.tag.placeholder')}/>}
                extra={[
                    <Button key={1} onClick={() => {
                        const generateShareToken = () => axios.post(`/bim-project/${id}/generateShareToken`).then((shareToken) => {
                            setShareToken(shareToken);
                            modal.update({content: content(`${shareLinkPrefix}${shareToken}`)});
                        }), content = (shareToken) => {
                            const shareLink = `${shareLinkPrefix}${shareToken}`;
                            return <Tabs tabBarExtraContent={{
                                right: <Tooltip title={t('project.share.refresh')}>
                                    <ReloadOutlined style={{cursor: 'pointer'}} onClick={() => generateShareToken()}/>
                                </Tooltip>
                            }} defaultActiveKey="2">
                                <Tabs.TabPane
                                    tab={<span>
                                     <LinkOutlined/>
                                        {t('project.share.link')}
                                    </span>}
                                    key="1"
                                >
                                    <a href={shareLink} target="_blank" rel="noopener noreferrer">{shareLink}</a>
                                </Tabs.TabPane>
                                <Tabs.TabPane
                                    tab={<span>
                                     <QrcodeOutlined/>
                                        {t('project.share.qrcode')}
                                    </span>}
                                    key="2"
                                >
                                    <div style={{width: '100%', textAlign: 'center'}}>
                                        <QRCode value={shareLink} size={200} fgColor="#000000"/>
                                    </div>
                                </Tabs.TabPane>
                            </Tabs>
                        };
                        if (!shareToken) {
                            generateShareToken();
                        }
                        const modal = Modal.success({icon: false, content: content(shareToken)});
                    }} icon={<ShareAltOutlined/>}>{t('project.share.value')}</Button>,
                    <ProjectDelete key={2} id={id} onSuccess={() => history.push('/bim')} icon={false}/>
                ]}
            >
                <Descriptions size="small" column={3}>
                    <Descriptions.Item label={t('project.owner')}>
                        <UserSelect mode={'multiple'} onChange={setOwnersAndUpdate}
                                    style={{minWidth: '200px', marginTop: '-5px'}}
                                    value={owners} placeholder={t('project.edit.owner.placeholder')}/>
                    </Descriptions.Item>
                    <Descriptions.Item label={t('project.createdOn')}>{project.createdOn}</Descriptions.Item>
                    <Descriptions.Item label={t('project.updatedOn')}>{project.updatedOn}</Descriptions.Item>
                    <Descriptions.Item label={t('project.description')}>
                        <Input.TextArea bordered={descriptionBordered} style={{marginTop: '-3px'}}
                                        onFocus={() => setDescriptionBordered(true)}
                                        onBlur={() => {
                                            setDescriptionBordered(false);
                                            update({ext: {...project.ext, description}});
                                        }}
                                        value={description} onChange={e => setDescription(e.target.value)}
                                        onPressEnter={(event) => event.target.blur()}/>
                    </Descriptions.Item>
                    <Descriptions.Item
                        label={<Tooltip title={t('project.model.upload')}>
                            {t('project.model.value')}&nbsp;
                            <UploadOutlined style={{cursor: 'pointer'}} onClick={() => {
                                Modal.info({
                                    okText: t('project.model.close'),
                                    content: <Upload.Dragger
                                        onRemove={false}
                                        data={file => file.args}
                                        onChange={({file,}) => {
                                            if (file.status === "done" && file.name.lastIndexOf('svf') > -1) {
                                                refresh();
                                            }
                                        }}
                                        showUploadList={{showRemoveIcon: false}}
                                        directory
                                        action={file => axios.get(`/bim-project/svf/${id}/${file.webkitRelativePath}?contentType=${file.type}`).then(({url, args}) => {
                                            file.args = args;
                                            return url;
                                        })}
                                    >
                                        <p className="ant-upload-drag-icon">
                                            <InboxOutlined/>
                                        </p>
                                        <p className="ant-upload-text">{t('project.model.uploadText')}</p>
                                        <p className="ant-upload-hint">{t('project.model.uploadHint')}</p>
                                    </Upload.Dragger>
                                })
                            }}/>
                        </Tooltip>}
                    >
                        {models && models.map(m => <Dropdown key={m.name} overlay={<Menu>
                            <Menu.Item key="1" onClick={
                                () => axios.post(`/bim-project/svf/${id}/${m.name}/setMainModel`).then(() => refresh())
                            }>{t('project.model.setMainModel')}</Menu.Item>
                        </Menu>} trigger={['contextMenu']}>
                            <Tag closable
                                 onClose={() => axios.delete(`/bim-project/svf/${id}/${m.name}`).then(() => refresh())}
                                 icon={<ProjectOutlined/>}
                            >{m.name}</Tag>
                        </Dropdown>)}
                    </Descriptions.Item>
                </Descriptions>
            </PageHeader>
        </div>
    );
};

export default ({match}) => {
    const id = match.params.id,
        [project, setProject] = useState(),
        [refreshFlag, refresh] = useReducer(v => !v, true);

    useEffect(() => {
        const timer = setTimeout(() => axios.get(`/bim-project/${id}`).then(setProject), 100);
        return () => clearTimeout(timer);
    }, [id, refreshFlag]);
    return (!project ?
        <Row align={"middle"} justify={"center"} style={{height: '60%', width: '90%'}}><Spin size={"large"}/></Row> :
        <Project id={id} project={project} refresh={refresh}/>);
}