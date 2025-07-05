import {Select, Spin, Tag} from "antd";
import axios from "../axios";
import React, {useCallback, useState, useRef} from "react";
import {TagOutlined} from "@ant-design/icons";


export default ({onChange, mode, placeholder, style, value = []}) => {
    const [associationTags, setAssociationTags] = useState([]),
        [associationTagsFetching, setAssociationTagsFetching] = useState(false),
        timer = useRef(0);
    const search = useCallback(value => {
        clearTimeout(timer.current);
        timer.current = setTimeout(() => {
            setAssociationTagsFetching(true);
            axios.get('/tag/', {
                params: {
                    nameLike: `%${value}%`,
                    size: 10,
                    page: 0
                }
            }).then(page => {
                setAssociationTags(() => page.content);
                setAssociationTagsFetching(() => false);
            }).catch(() => setAssociationTagsFetching(() => false));
        }, 100);
    }, []);
    return (<Select mode={mode} bordered={false} value={value}
                    tagRender={props => {
                        const {label, closable, onClose} = props;
                        return (
                            <Tag icon={<TagOutlined/>} closable={closable} onClose={onClose} style={{marginRight: 3}}>
                                {label}
                            </Tag>
                        );
                    }}
                    onChange={onChange} placeholder={placeholder} style={style}
                    notFoundContent={associationTagsFetching ? <Spin size="small"/> : null}
                    onSearch={search}
                    options={associationTags.map(t => {
                        return {label: t.name, value: t.name};
                    })}
                    menuItemSelectedIcon={<TagOutlined/>}
    >
    </Select>);
}