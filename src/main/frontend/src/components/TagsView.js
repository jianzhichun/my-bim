import React, {useEffect, useState} from "react";
import axios from "../axios";
import {Tag} from "antd";

export default ({id, style}) => {
    const [tags, setTags] = useState([]);
    useEffect(() => {
        axios.get(`/tag/${id}/findByProjectIdLimit3`).then(setTags)
    }, [id])
    return <div style={style || {textAlign: 'end', paddingBottom: '5px'}}>{
        tags.map(t => <Tag
            style={{maxWidth: '55px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis'}}
            key={t.id}>{t.name}</Tag>)
    }</div>
}