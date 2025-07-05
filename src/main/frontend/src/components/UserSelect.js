import {Select, Spin, Tag} from "antd";
import axios from "../axios";
import React, {useCallback, useRef, useState} from "react";
import {UserOutlined} from "@ant-design/icons";

export default ({onChange, placeholder, style, value = []}) => {
    const [associationTags, setAssociationTags] = useState([]),
        [associationTagsFetching, setAssociationTagsFetching] = useState(false),
        user2tag = u => {
            return {
                label: u.username,
                value: u.id
            };
        },
        tag2user = t => {
            return {
                id: t.value,
                username: t.label
            };
        },
        timer = useRef(0);
    const search = useCallback(value => {
        clearTimeout(timer.current);
        timer.current = setTimeout(() => {
            setAssociationTagsFetching(true);
            axios.get('/user', {
                params: {
                    nameLike: `%${value}%`,
                    size: 10,
                    page: 0
                }
            }).then(page => {
                setAssociationTags(() => page.content.map(user2tag));
                setAssociationTagsFetching(() => false);
            }).catch(() => setAssociationTagsFetching(() => false));
        }, 100);
    }, []);
    return (<Select showSearch labelInValue mode={'multiple'} bordered={false}
                    showArrow={false}
                    filterOption={false}
                    tagRender={props => {
                        const {label, closable, onClose} = props;
                        return (
                            <Tag icon={<UserOutlined/>} closable={closable} onClose={onClose} style={{marginRight: 3}}>
                                {label}
                            </Tag>
                        );
                    }}
                    value={value.map(user2tag)}
                    onChange={(value) => onChange(value.map(tag2user))} placeholder={placeholder} style={style}
                    notFoundContent={associationTagsFetching ? <Spin size="small"/> : null}
                    onSearch={search}
                    options={associationTags}
                    menuItemSelectedIcon={<UserOutlined/>}
    >
    </Select>);
}