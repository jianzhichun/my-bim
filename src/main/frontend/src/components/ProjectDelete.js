import {Button, message, Modal} from "antd";
import axios from "../axios";
import {DeleteOutlined} from "@ant-design/icons";
import React from "react";
import {useTranslation} from "react-i18next";

export default ({id, onSuccess, icon = true}) => {
    const {t} = useTranslation(),
        onClick = (e) => {
            e.stopPropagation();
            Modal.confirm({
                content: t('project.delete.confirm'),
                onOk: () => axios.delete(`/bim-project/${id}`).then(() => {
                    message.success(t('project.delete.success'));
                    onSuccess();
                })
            })
        };
    return (icon ? <DeleteOutlined onClick={onClick}/> :
        <Button onClick={onClick} danger icon={<DeleteOutlined/>}>{t('project.delete.value')}</Button>);
}