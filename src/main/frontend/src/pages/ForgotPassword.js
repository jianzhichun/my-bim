import {useTranslation} from "react-i18next";
import {Button, Form, Image, Input, message, Row, Tooltip} from "antd";
import axios from "../axios";
import {QuestionCircleOutlined, UserOutlined} from "@ant-design/icons";
import React from "react";
import {useHistory} from "react-router-dom";

export default () => {
    const {t} = useTranslation(),
        history = useHistory();
    return (
        <Row style={{height: '80vh', flexFlow: "column"}} justify="center" align="middle" gutter={16}>
            <Image src={`${process.env.PUBLIC_URL}/logo.png`} style={{marginBottom: '10px'}}/>
            <Form labelCol={{xs: {span: 24,}, sm: {span: 8,},}}
                  name="forgotPassword"
                  onFinish={(values) => axios.post(`/user/${values.username}/forgotPassword`)
                      .then(() => {
                          history.push('/login');
                          message.success(t('forgotPassword.success'));
                      })}
            >
                <Form.Item
                    name="username"
                    rules={[
                        {
                            required: true,
                            message: t('forgotPassword.username.required'),
                        },
                    ]}
                >
                    <Input prefix={<UserOutlined className="site-form-item-icon"/>}
                           placeholder={t('forgotPassword.username.value')}/>
                </Form.Item>
                <Form.Item wrapperCol={{xs: {span: 24, offset: 0,}, sm: {span: 16, offset: 8,},}}>

                    <Button type="primary" htmlType="submit">
                        <Tooltip title={t('forgotPassword.tooltip')}>
                            <QuestionCircleOutlined/>&nbsp;
                            {t('forgotPassword.apply')}
                        </Tooltip>
                    </Button>
                </Form.Item>
            </Form>
        </Row>
    );
};
